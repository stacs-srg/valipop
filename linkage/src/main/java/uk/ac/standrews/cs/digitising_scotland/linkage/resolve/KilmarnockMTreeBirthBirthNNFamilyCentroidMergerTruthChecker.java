package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.linkage.resolve.distances.GFNGLNBFNBMNPOMDOMDistanceOverMarriage;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.DataDistance;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.MTree;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by al on 10/03/2017.
 */
public class KilmarnockMTreeBirthBirthNNFamilyCentroidMergerTruthChecker extends KilmarnockMTreeBirthBirthThresholdNNGroundTruthChecker {

    public KilmarnockMTreeBirthBirthNNFamilyCentroidMergerTruthChecker(String births_source_path, String deaths_source_path, String marriages_source_path,float match_family_distance_threshold) throws RecordFormatException, RepositoryException, StoreException, JSONException, BucketException, IOException {
        super(births_source_path, deaths_source_path, marriages_source_path,match_family_distance_threshold);
    }

    private void compute() throws RepositoryException, BucketException, IOException {
        System.out.println("Creating Birth MTree");
        long time = System.currentTimeMillis();
        createBirthMTreeOverGFNGLNBFNBMNPOMDOM();
        long elapsed =  ( System.currentTimeMillis() - time ) / 1000 ;
        System.out.println("Created Birth MTree in " + elapsed + "s");

        System.out.println("Forming families from Birth-Birth links");
        formFamilies();
        mergeFamilies();
        listFamilies();

        elapsed =  ( System.currentTimeMillis() - time ) / 1000 ;
        System.out.println("Finished in " + elapsed + "s");
    }

    private void mergeFamilies() {
        HashMap<Long, Family> family_id_tofamilies = new HashMap<>(); // Maps from family id to family.

        MTree<Family> familyMTree = new MTree(new GFNGLNBFNBMNPOMDOMDistanceOverMarriage());
        for (Family f : families.values()) {
            familyMTree.add(f);
        }

        for (Family f : families.values()) {

            Family new_family = f;


            int n = 1;
            List<DataDistance<Family>> dds = familyMTree.nearestN(f, n); /// AL IS HERE ***********

            while (dds.get(n).distance < 5 && f.getSiblings().size() < 15) {


                for (DataDistance<Family> dd : dds) {
                    if (dd.distance < 5 && f.getSiblings().size() < 15) {
                        Family other = dd.value;
                        System.out.println("Merged family:" + f.getFathersForename() + " " + f.getFathersSurname() + " " + f.getMothersForename() + " " + f.getMothersMaidenSurname());
                        System.out.println("         with:" + other.getFathersForename() + " " + other.getFathersSurname() + " " + other.getMothersForename() + " " + other.getMothersMaidenSurname());

                        for (BirthFamilyGT child : other.siblings) { // merge the families.
                            f.siblings.add(child);
                        }
                    } else {
                        family_id_tofamilies.put((long) f.id, f); // put the merged (or otherwise family into the new map
                    }
                }
                dds = familyMTree.nearestN(f, ++n);
            }
        }

        // finally create a new families hash map
        families = new HashMap<>(); // Maps from person id to family.
        // and insert all the people from family_id_tofamilies into families
        for( Family f : family_id_tofamilies.values() ) {
            for(BirthFamilyGT child : f.getSiblings() ) {
                families.put( child.getId(), f );
            }
        }
    }


    /**
     * Adds a birth record to a family map.
     * @param searched the record that was used to search for a match
     * @param found_dd the data distance that was matched in the search
     */
    @Override
    protected void add_births_to_map(BirthFamilyGT searched, DataDistance<BirthFamilyGT> found_dd ) {

//        BirthFamilyGT found = found_dd.value;
//
//        long searched_key = searched.getId();
//        long found_key = found.getId();
//
//        if( ! families.containsKey( searched_key ) && ! families.containsKey( found_key ) ) { // not seen either birth before
//            // Create a new Family and add to map under both keys.
//            Family new_family = new CentroidFamily( searched );
//            new_family.siblings.add( found );
//            families.put( searched_key, new_family );
//            families.put( found_key, new_family );
//            return;
//        }
//        // Don't bother with whether these are the same family or not, or if the added values are already in the set
//        // Set implementation should dela with this.
//        if( families.containsKey( searched_key )  && ! families.containsKey( found_key )) { // already seen the searched birth => been found already
//            Family f = families.get( searched_key );
//            f.siblings.add( found );
//        }
//        if( families.containsKey( found_key )  && ! families.containsKey( searched_key ) ) { // already seen the found birth => been searcher for earlier
//            Family f = families.get( found_key );
//            f.siblings.add( searched );
//        }
    }



    //***********************************************************************************

    public static void main(String[] args) throws Exception {

        if( args.length < 4 ) {
            ErrorHandling.error( "Usage: run with births_source_path deaths_source_path marriages_source_path family_distance_threshold");
        }

        System.out.println( "Running KilmarnockMTreeBirthBirthNNFamilyCentroidMergerTruthChecker" );
        String births_source_path = args[0];
        String deaths_source_path = args[1];
        String marriages_source_path = args[2];
        String family_distance_threshold_string = args[3];

        KilmarnockMTreeBirthBirthNNFamilyCentroidMergerTruthChecker matcher = new KilmarnockMTreeBirthBirthNNFamilyCentroidMergerTruthChecker(births_source_path, deaths_source_path, marriages_source_path,new Float(family_distance_threshold_string));
        matcher.compute();
    }
}
