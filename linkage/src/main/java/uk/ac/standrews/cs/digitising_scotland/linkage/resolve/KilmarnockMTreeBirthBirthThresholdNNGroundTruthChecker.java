package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.linkage.resolve.distances.GFNGLNBFNBMNPOMDOMDistanceOverBirth;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.DataDistance;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.MTree;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;

import java.io.IOException;

/**
 * Attempt to perform linking using MTree matching
 * File is derived from KilmarnockLinker.
 * Created by al on 17/2/1017
 */
public class KilmarnockMTreeBirthBirthThresholdNNGroundTruthChecker extends KilmarnockMTreeMatcherGroundTruthChecker {

    private  MTree<BirthFamilyGT> birthMTree;

    public KilmarnockMTreeBirthBirthThresholdNNGroundTruthChecker(String births_source_path, String deaths_source_path, String marriages_source_path) throws RecordFormatException, RepositoryException, StoreException, JSONException, BucketException, IOException {
        super(births_source_path, deaths_source_path, marriages_source_path );
    }

    private void compute() throws RepositoryException, BucketException, IOException {
        System.out.println("Creating Birth MTree");
        long time = System.currentTimeMillis();
        createBirthMTreeOverGFNGLNBFNBMNPOMDOM();
        long elapsed =  ( System.currentTimeMillis() - time ) / 1000 ;
        System.out.println("Created Birth MTree in " + elapsed + "s");

        System.out.println("Forming families from Birth-Birth links");
        formFamilies();
        listFamilies();

        System.out.println("Finished");
    }


    protected void createBirthMTreeOverGFNGLNBFNBMNPOMDOM() throws RepositoryException, BucketException, IOException {

        System.out.println("Creating M Tree of births by GFNGLNBFNBMNPOMDOMDistanceOverBirth...");

        birthMTree = new MTree<BirthFamilyGT>( new GFNGLNBFNBMNPOMDOMDistanceOverBirth() );

        IInputStream<BirthFamilyGT> stream = births.getInputStream();

        for (BirthFamilyGT birth : stream) {

            birthMTree.add( birth );
        }

    }


    /**
     * Try and form families from Birth M Tree data_array
     */
    protected void formFamilies() {

        IInputStream<BirthFamilyGT> stream;
        try {
            stream = births.getInputStream();
        } catch (BucketException e) {
            System.out.println("Exception whilst getting births");
            return;
        }

        for (BirthFamilyGT to_match : stream) {

            DataDistance<BirthFamilyGT> matched = birthMTree.nearestNeighbour( to_match );

            if (matched.distance < 8.0F && matched.value != to_match ) {
                add_births_to_map(to_match, matched);
            }
        }
    }

    /**
     * Adds a birth record to a family map.
     * @param searched the record that was used to search for a match
     * @param found_dd the data distance that was matched in the search
     */
    private void add_births_to_map(BirthFamilyGT searched, DataDistance<BirthFamilyGT> found_dd ) {

        BirthFamilyGT found = found_dd.value;

        long searched_key = searched.getId();
        long found_key = found.getId();

        if( ! families.containsKey( searched_key ) && ! families.containsKey( found_key ) ) { // not seen either birth before
            // Create a new Family and add to map under both keys.
            Family new_family = new Family( searched );
            new_family.siblings.add( found );
            families.put( searched_key, new_family );
            families.put( found_key, new_family );
            return;
        }
        // Don't bother with whether these are the same family or not, or if the added values are already in the set
        // Set implementation should dela with this.
        if( families.containsKey( searched_key )  && ! families.containsKey( found_key )) { // already seen the searched birth => been found already
            Family f = families.get( searched_key );
            f.siblings.add( found );
        }
        if( families.containsKey( found_key )  && ! families.containsKey( searched_key ) ) { // already seen the found birth => been searcher for earlier
            Family f = families.get( found_key );
            f.siblings.add( searched );
        }
    }

    //***********************************************************************************

    public static void main(String[] args) throws Exception {
        if( args.length < 3 ) {
            ErrorHandling.error( "Usage: run with births_source_path deaths_source_path marriages_source_path");
        }

        System.out.println( "Running KilmarnockMTreeBirthBirthThresholdNNGroundTruthChecker" );
        String births_source_path = args[0];
        String deaths_source_path = args[1];
        String marriages_source_path = args[2];

        KilmarnockMTreeBirthBirthThresholdNNGroundTruthChecker matcher = new KilmarnockMTreeBirthBirthThresholdNNGroundTruthChecker(births_source_path, deaths_source_path, marriages_source_path);
        matcher.compute();
    }
}
