package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.linkage.resolve.distances.GFNGLNBFNBMNPOMDOMDistanceOverFamily;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.DataDistance;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.MTree;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by al on 10/03/2017.
 */
public class KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker extends KilmarnockMTreeBirthBirthThresholdNNGroundTruthChecker {

    private static final int NUMBER_OF_NEARBY_FAMILIES_TO_CONSIDER_FOR_MERGING = 15;

    private final int max_family_size;
    private final float family_merge_distance_threshold;

    private KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker(String births_source_path, String deaths_source_path, String marriages_source_path, float match_family_distance_threshold, int max_family_size, float family_merge_distance_threshold) throws RecordFormatException, RepositoryException, StoreException, JSONException, BucketException, IOException {

        super(births_source_path, deaths_source_path, marriages_source_path, match_family_distance_threshold);

        this.max_family_size = max_family_size;
        this.family_merge_distance_threshold = family_merge_distance_threshold;
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

        // add families to family distance MTree
        for (Family f : families.values()) {
            familyMTree.add(f);
        }

        // Merge the families and put merged families into family_id_tofamilies
        for (Family f : families.values()) {

            for (DataDistance<Family> dd : familyMTree.nearestN(f, NUMBER_OF_NEARBY_FAMILIES_TO_CONSIDER_FOR_MERGING)) {

                if (dd.distance < family_merge_distance_threshold && f.getSiblings().size() < max_family_size) {

                    Family other_family = dd.value;
                    System.out.println("Merged family:" + f.getFathersForename() + " " + f.getFathersSurname() + " " + f.getMothersForename() + " " + f.getMothersMaidenSurname());
                    System.out.println("         with:" + other_family.getFathersForename() + " " + other_family.getFathersSurname() + " " + other_family.getMothersForename() + " " + other_family.getMothersMaidenSurname());

                    for (BirthFamilyGT child : other_family.siblings) { // merge the families.
                        f.siblings.add(child);
                    }
                } else {
                    family_id_to_families.put((long) f.id, f); // put the merged (or otherwise family into the new map
                    break;
                }
            }
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

        if (args.length >= 6) {

            System.out.println("Running KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker");
            String births_source_path = args[0];
            String deaths_source_path = args[1];
            String marriages_source_path = args[2];
            String family_distance_threshold_string = args[3];
            String max_family_size_string = args[4];
            String family_merge_distance_threshold_string = args[5];


            KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker matcher = new KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker(births_source_path, deaths_source_path, marriages_source_path, new Float(family_distance_threshold_string), Integer.parseInt(max_family_size_string), Float.parseFloat(family_merge_distance_threshold_string));
            matcher.compute();

        } else {
            usage();
        }
    }

    private static void usage() {

        System.err.println("Usage: run with births_source_path deaths_source_path marriages_source_path family_distance_threshold max_family_size family_merge_distance_threshold");
    }
}
