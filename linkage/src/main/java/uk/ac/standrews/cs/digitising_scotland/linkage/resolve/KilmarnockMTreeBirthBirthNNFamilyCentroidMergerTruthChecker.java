package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.experiments.Experiment;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.linkage.resolve.distances.GFNGLNBFNBMNPOMDOMDistanceOverFamily;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.DataDistance;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.MTree;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by al on 10/03/2017.
 */
public class KilmarnockMTreeBirthBirthNNFamilyCentroidMergerTruthChecker extends KilmarnockMTreeBirthBirthThresholdNNGroundTruthChecker {

    private static final String[] ARG_NAMES = {"births_source_path", "deaths_source_path", "marriages_source_path", "family_distance_threshold", "max_family_size", "family_merge_distance_threshold"};

    private int max_family_size;
    private float family_merge_distance_threshold;

    private KilmarnockMTreeBirthBirthNNFamilyCentroidMergerTruthChecker(float match_family_distance_threshold, int max_family_size, float family_merge_distance_threshold) throws RecordFormatException, RepositoryException, StoreException, JSONException, BucketException, IOException {

        super(match_family_distance_threshold);

        this.max_family_size = max_family_size;
        this.family_merge_distance_threshold = family_merge_distance_threshold;
    }

    private KilmarnockMTreeBirthBirthNNFamilyCentroidMergerTruthChecker() throws RecordFormatException, RepositoryException, StoreException, JSONException, BucketException, IOException {

    }

    private void compute() throws Exception {

        timedRun("Creating Birth MTree", () -> {
            createBirthMTreeOverGFNGLNBFNBMNPOMDOM();
            return null;
        });

        timedRun("Forming families from Birth-Birth links", () -> {
            formFamilies();
            mergeFamilies();
            listFamilies();
            return null;
        });

        timedRun("Calculating linkage stats", () -> {
            calculateLinkageStats();
            return null;
        });
    }

    private void mergeFamilies() {

        Map<Long, Family> family_id_to_families = new HashMap<>(); // Maps from family id to family.

        MTree<Family> familyMTree = new MTree<>(new GFNGLNBFNBMNPOMDOMDistanceOverFamily());
        for (Family f : families.values()) {
            familyMTree.add(f);
        }

        for (Family f : families.values()) {

            int pool_size = 1; // the size of the pool in which we are examining families to merge.

            // These 2 lines initialise the search and are repeated in the if at the end of the while
            // Might be able to make this cleaner but at least I think I understand it! - al
            List<DataDistance<Family>> dds = familyMTree.nearestN(f, pool_size);
            int index = 0; // index in dds - the next family to look at in the search

            // This code sweeps out (in pools of 5) in circles looking for families to merge - we stop when the families we are finding are outside the threshold

            while (dds.get(index).distance < family_merge_distance_threshold && f.getSiblings().size() < max_family_size) {

                Family other = dds.get(index).value; // next family to compare with the current.

                System.out.println("Merged family:" + f.getFathersForename() + " " + f.getFathersSurname() + " " + f.getMothersForename() + " " + f.getMothersMaidenSurname());
                System.out.println("         with:" + other.getFathersForename() + " " + other.getFathersSurname() + " " + other.getMothersForename() + " " + other.getMothersMaidenSurname());

                for (BirthFamilyGT child : other.siblings) { // merge the families.
                    f.siblings.add(child);
                }

                index++;
                if( index == dds.size() ) { // we are at the end of the list - get the next circle of Families.
                    pool_size += 5;
                    dds = familyMTree.nearestN(f, pool_size); // get the next circle of families (including the ones we have already seen).
                    dds = dds.subList( pool_size - 1, dds.size() ); // chop off the families we have already looked at.
                    index = 0;
                }
            }
            family_id_to_families.put((long) f.id, f); // put the merged (or otherwise) family into the new map - slightly inefficient but easier to code
        }

        // finally create a new families hash map
        families = new HashMap<>(); // Maps from person id to family.

        // and insert all the people from family_id_tofamilies into families
        for (Family f : family_id_to_families.values()) {
            for (BirthFamilyGT child : f.getSiblings()) {
                families.put(child.getId(), f);
            }
        }
    }

    //***********************************************************************************

    public static void main(String[] args) throws Exception {

        Experiment experiment = new Experiment(ARG_NAMES, args, MethodHandles.lookup().lookupClass());

        if (args.length >= ARG_NAMES.length) {

            String births_source_path = args[0];
            String deaths_source_path = args[1];
            String marriages_source_path = args[2];
            String family_distance_threshold_string = args[3];
            String max_family_size_string = args[4];
            String family_merge_distance_threshold_string = args[5];

            KilmarnockMTreeBirthBirthNNFamilyCentroidMergerTruthChecker matcher = new KilmarnockMTreeBirthBirthNNFamilyCentroidMergerTruthChecker(Float.parseFloat(family_distance_threshold_string), Integer.parseInt(max_family_size_string), Float.parseFloat(family_merge_distance_threshold_string));

            experiment.printDescription();

            matcher.ingestRecords(births_source_path, deaths_source_path, marriages_source_path);
            matcher.compute();

        } else {
            experiment.usage();
        }
    }
}
