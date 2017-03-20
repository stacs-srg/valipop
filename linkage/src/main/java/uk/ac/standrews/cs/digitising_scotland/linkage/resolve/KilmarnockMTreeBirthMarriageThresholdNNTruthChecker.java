package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.digitising_scotland.linkage.resolve.distances.GFNGLNBFNBMNPOMDOMDistanceOverMarriage;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.DataDistance;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.MTree;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

/**
 * Attempt to perform linking using MTree matching
 * File is derived from KilmarnockLinker.
 * Created by al on 17/2/1017
 */
public class KilmarnockMTreeBirthMarriageThresholdNNTruthChecker extends KilmarnockMTreeMatcherGroundTruthChecker {

    // Trees

    private MTree<Marriage> marriageMtree;

    private KilmarnockMTreeBirthMarriageThresholdNNTruthChecker(String births_source_path, String deaths_source_path, String marriages_source_path) throws RecordFormatException, RepositoryException, StoreException, JSONException, BucketException, IOException {
        super(births_source_path, deaths_source_path, marriages_source_path);
    }

    private void compute() throws RepositoryException, BucketException, IOException {
        System.out.println("Creating Marriage MTree");
        long time = System.currentTimeMillis();
        createMarriageMTreeOverGFNGLNBFNBMNPOMDOM();
        long elapsed = (System.currentTimeMillis() - time) / 1000;
        System.out.println("Created Marriage MTree in " + elapsed + "s");

        System.out.println("Forming families from Marriage-Birth links");
        formFamilies();
        showFamilies();

        System.out.println("Finished");
    }

    private void showFamilies() throws BucketException {
        System.out.println("Number of families formed:" + new HashSet<Family>(families.values()).size());
        listFamilies();
    }

    private void createMarriageMTreeOverGFNGLNBFNBMNPOMDOM() throws RepositoryException, BucketException, IOException {

        System.out.println("Creating M Tree of marriages by GFNGLNBFNBMNPOMDOMDistanceOverBirth...");

        marriageMtree = new MTree<Marriage>(new GFNGLNBFNBMNPOMDOMDistanceOverMarriage());

        IInputStream<Marriage> stream = marriages.getInputStream();

        for (Marriage marriage : stream) {

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

            if (result.distance < 8.0F) {
                addBirthToMap(families, result.value.getId(), b); // used the marriage id as a unique identifier.
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

        if (args.length < 3) {
            ErrorHandling.error("Usage: run with births_source_path deaths_source_path marriages_source_path");
        }

        System.out.println("Running KilmarnockMTreeBirthMarriageThresholdNNTruthChecker");
        String births_source_path = args[0];
        String deaths_source_path = args[1];
        String marriages_source_path = args[2];

        KilmarnockMTreeBirthMarriageThresholdNNTruthChecker matcher = new KilmarnockMTreeBirthMarriageThresholdNNTruthChecker(births_source_path, deaths_source_path, marriages_source_path);
        matcher.compute();
    }
}
