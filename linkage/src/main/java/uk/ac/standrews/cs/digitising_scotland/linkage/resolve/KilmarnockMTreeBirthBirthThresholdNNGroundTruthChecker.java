package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.KillieBirth;
import uk.ac.standrews.cs.digitising_scotland.linkage.resolve.distances.GFNGLNBFNBMNPOMDOMDistanceOverBirth;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.DataDistance;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.MTree;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;

import java.io.IOException;
import java.util.*;

/**
 * Attempt to perform linking using MTree matching
 * File is derived from KilmarnockLinker.
 * Created by al on 17/2/1017
 */
public class KilmarnockMTreeBirthBirthThresholdNNGroundTruthChecker extends KilmarnockMTreeMatcherGroundTruthChecker {

    private  MTree<KillieBirth> birthMTree;

    // Maps

    private HashMap< String, Family > family_ground_truth_map = new HashMap<>(); // Maps from FAMILY in birth record to a family unit using ground truth
    private HashMap< String, Family > inferred_family_map = new HashMap<>(); // Maps from FAMILY in birth record to a family unit using M tree derived data.
    private HashMap< String, Family > unmatched_map = new HashMap<>(); // Unmatched families

    public KilmarnockMTreeBirthBirthThresholdNNGroundTruthChecker(String births_source_path, String deaths_source_path, String marriages_source_path) throws RecordFormatException, RepositoryException, StoreException, JSONException, BucketException, IOException {
        super(births_source_path, deaths_source_path, marriages_source_path );
    }

    private void compute() throws RepositoryException, BucketException, IOException {
        System.out.println("Creating Birth MTree");
        long time = System.currentTimeMillis();
        createBirthMTreeOverGFNGLNBFNBMNPOMDOM();
        long elapsed =  ( System.currentTimeMillis() - time ) / 1000 ;
        System.out.println("Created Marriage MTree in " + elapsed + "s");

        System.out.println("Forming families from Birth-Birth links");
        formFamilies();
        listFamilies();

        System.out.println("Finished");
    }


    protected void createBirthMTreeOverGFNGLNBFNBMNPOMDOM() throws RepositoryException, BucketException, IOException {

        System.out.println("Creating M Tree of births by GFNGLNBFNBMNPOMDOMDistanceOverBirth...");

        birthMTree = new MTree<KillieBirth>( new GFNGLNBFNBMNPOMDOMDistanceOverBirth() );

        IInputStream<KillieBirth> stream = births.getInputStream();

        for (KillieBirth birth : stream) {

            birthMTree.add( birth );
        }

    }


    /**
     * Try and form families from Birth M Tree data_array
     */
    protected void formFamilies() {

        IInputStream<KillieBirth> stream;
        try {
            stream = births.getInputStream();
        } catch (BucketException e) {
            System.out.println("Exception whilst getting births");
            return;
        }

        for (KillieBirth to_match : stream) {

            DataDistance<KillieBirth> matched = birthMTree.nearestNeighbour( to_match );

            if (matched.distance < 8.0F && matched.value != to_match ) {
                add_births_to_map(inferred_family_map, to_match, matched);
            } else {
                unmatched_map.put(String.valueOf(to_match.getId()), new Family(to_match));
            }
        }
    }

    protected void listInterfamilyDistances( Collection<Family> families ) {

        ArrayList<Family> printed_already = new ArrayList();

        for( Family f : families ) {
            if( ! printed_already.contains( f ) ) {
                HashMap<KillieBirth, List<DataDistance<KillieBirth>>> family_distances = f.distances;
                for( KillieBirth b : family_distances.keySet() ) {
                    System.out.print( f.id + "\t" + b.getString( KillieBirth.FAMILY  ) + "\t" + b.getId() + "\t" + b.getString( KillieBirth.FORENAME) + "\t" + b.getString( KillieBirth.SURNAME) + "\t" );
                    List<DataDistance<KillieBirth>> distances = family_distances.get( b );
                    for( DataDistance<KillieBirth> dd : distances ) {
                        System.out.print( dd.distance + "\t" + dd.value.getString( KillieBirth.FORENAME) + "\t" + dd.value.getString( KillieBirth.SURNAME) + "\t" );
                    }
                    System.out.println();
                }
                printed_already.add(f);
            }
        }
    }

    /**
     * Adds a birth record to a family map.
     * @param map the map to which the record should be added
     * @param searched the record that was used to search for a match
     * @param found_dd the data distance that was matched in the search
     */
    private void add_births_to_map(HashMap<String, Family> map, KillieBirth searched, DataDistance<KillieBirth> found_dd ) {

        KillieBirth found = found_dd.value;

        String searched_key = String.valueOf( searched.getId() );
        String found_key = String.valueOf( found.getId() );

        if( ! map.containsKey( searched_key ) && ! map.containsKey( found_key ) ) { // not seen either birth before
            // Create a new Family and add to map under both keys.
            Family new_family = new Family( searched );
            new_family.siblings.add( found );
            new_family.addDistance( searched, found_dd );
            map.put( searched_key, new_family );
            map.put( found_key, new_family );
            return;
        }
        // Don't bother with whether these are the same family or not, or if the added values are already in the set
        // Set implementation should dela with this.
        if( map.containsKey( searched_key )  && ! map.containsKey( found_key )) { // already seen the searched birth => been found already
            Family f = map.get( searched_key );
            f.siblings.add( found );
            f.addDistance( searched, found_dd );
        }
        if( map.containsKey( found_key )  && ! map.containsKey( searched_key ) ) { // already seen the found birth => been searcher for earlier
            Family f = map.get( found_key );
            f.siblings.add( searched );
            f.addDistance( searched, found_dd );
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
