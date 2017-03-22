package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import uk.ac.standrews.cs.digitising_scotland.linkage.experiments.Experiment;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.digitising_scotland.linkage.resolve.distances.GFNGLNBFNBMNPOMDOMDistanceOverMarriage;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.DataDistance;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.MTree;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Map;

/**
 * Attempt to perform linking using MTree matching
 * File is derived from KilmarnockLinker.
 * Created by al on 17/2/1017
 */
public class KilmarnockMTreeBirthMarriageThresholdNNTruthChecker extends KilmarnockExperiment {

    private static final String[] ARG_NAMES = {"births_source_path", "deaths_source_path", "marriages_source_path"};
    public static final float DISTANCE_THRESHOLD = 8.0F;

    private MTree<Marriage> marriageMtree;

    private KilmarnockMTreeBirthMarriageThresholdNNTruthChecker() throws StoreException, RepositoryException, IOException {

        super();
    }

    private void compute() throws Exception {

        timedRun("Creating Marriage MTree", () -> {
            createMarriageMTreeOverGFNGLNBFNBMNPOMDOM();
            return null;
        });

        timedRun("Forming families from Marriage-Birth links", () -> {
            formFamilies();
            showFamilies();
            return null;
        });
    }

    private void showFamilies() throws BucketException {

        System.out.println("Number of families formed:" + new HashSet<>(person_to_family_map.values()).size());
        printFamilies();
    }

    private void createMarriageMTreeOverGFNGLNBFNBMNPOMDOM() throws RepositoryException, BucketException, IOException {

        marriageMtree = new MTree<>(new GFNGLNBFNBMNPOMDOMDistanceOverMarriage());

        for (Marriage marriage : marriages.getInputStream()) {
            marriageMtree.add(marriage);
        }
    }

    /**
     * Try and form families from Marriage M Tree data_array
     */
    private void formFamilies() {

        IInputStream<BirthFamilyGT> stream;
        try {
            stream = births.getInputStream();
        } catch (BucketException e) {
            System.out.println("Exception whilst getting births");
            return;
        }

        for (BirthFamilyGT b : stream) {

            Marriage marriage_query = new Marriage();
            marriage_query.put(Marriage.GROOM_FORENAME, b.getFathersForename());
            marriage_query.put(Marriage.GROOM_SURNAME, b.getFathersSurname());
            marriage_query.put(Marriage.BRIDE_FORENAME, b.getMothersForename());
            marriage_query.put(Marriage.BRIDE_SURNAME, b.getMothersMaidenSurname());
            marriage_query.put(Marriage.PLACE_OF_MARRIAGE, b.getPlaceOfMarriage());

            marriage_query.put(Marriage.MARRIAGE_DAY, b.getString(BirthFamilyGT.PARENTS_DAY_OF_MARRIAGE));
            marriage_query.put(Marriage.MARRIAGE_MONTH, b.getString(BirthFamilyGT.PARENTS_MONTH_OF_MARRIAGE));
            marriage_query.put(Marriage.MARRIAGE_YEAR, b.getString(BirthFamilyGT.PARENTS_YEAR_OF_MARRIAGE));

            DataDistance<Marriage> result = marriageMtree.nearestNeighbour(marriage_query);

            if (result.distance < DISTANCE_THRESHOLD) {
                addBirthToMap(person_to_family_map, result.value.getId(), b); // used the marriage id as a unique identifier.
            }
        }
    }

    /**
     * Adds a birth record to a family map.
     *
     * @param map          the map to which the record should be added
     * @param birth_record the record to add to the map
     */
    private void addBirthToMap(Map<Long, Family> map, Long key, BirthFamilyGT birth_record) {

        if (map.containsKey(key)) { // have already seen a member of this family - so just add the birth to the family map
            // could check here to ensure parents are the same etc.
            Family f = map.get(key);
            f.siblings.add(birth_record);
        } else { // a new family we have not seen before
            Family new_family = new Family(birth_record);
            map.put(key, new_family);
        }
    }

    //***********************************************************************************

    public static void main(String[] args) throws Exception {

        Experiment experiment = new Experiment(ARG_NAMES, args, MethodHandles.lookup().lookupClass());

        if (args.length >= ARG_NAMES.length) {

            String births_source_path = args[0];
            String deaths_source_path = args[1];
            String marriages_source_path = args[2];

            KilmarnockMTreeBirthMarriageThresholdNNTruthChecker matcher = new KilmarnockMTreeBirthMarriageThresholdNNTruthChecker();

            experiment.printDescription();

            matcher.ingestRecords(births_source_path, deaths_source_path, marriages_source_path);
            matcher.compute();

        } else {
            experiment.usage();
        }
    }
}
