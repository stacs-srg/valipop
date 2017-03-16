package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.linkage.resolve.distances.GFNGLNBFNBMNPOMDOMDistanceOverFamily;
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
public class KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker extends KilmarnockMTreeBirthBirthThresholdNNGroundTruthChecker {

    protected int max_family_size;
    protected float family_merge_distance_threshold;


    public KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker(String births_source_path, String deaths_source_path, String marriages_source_path, float match_family_distance_threshold, int max_family_size, float family_merge_distance_threshold ) throws RecordFormatException, RepositoryException, StoreException, JSONException, BucketException, IOException {
        super(births_source_path, deaths_source_path, marriages_source_path,match_family_distance_threshold);
        this.max_family_size = max_family_size;
        this.family_merge_distance_threshold = family_merge_distance_threshold;
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
        MTree<Family> familyMTree = new MTree( new GFNGLNBFNBMNPOMDOMDistanceOverFamily() );

        // add families to family distance MTree
        for( Family f : families.values() ) {
            familyMTree.add(f);
        }

        // Merge the families and put merged families into family_id_tofamilies
        for( Family f : families.values() ) {

            Family new_family = f;

            List<DataDistance<Family>> dds = familyMTree.nearestN(f, 15);
            for( DataDistance<Family> dd : dds ) {
                if ( dd.distance < 5 && f.getSiblings().size() < 15 ) {
                    Family other = dd.value;
                    System.out.println( "Merged family:" + f.getFathersForename() + " " + f.getFathersSurname() + " " + f.getMothersForename() + " " + f.getMothersMaidenSurname());
                    System.out.println( "         with:" + other.getFathersForename() + " " + other.getFathersSurname() + " " + other.getMothersForename() + " " + other.getMothersMaidenSurname());

                    for(BirthFamilyGT child : other.siblings ) { // merge the families.
                        f.siblings.add( child );
                    }
                } else {
                    family_id_tofamilies.put( (long) f.id, f ); // put the merged (or otherwise family into the new map
                    break;
                }
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

    //***********************************************************************************

    public static void main(String[] args) throws Exception {

        if( args.length < 6 ) {
            ErrorHandling.error( "Usage: run with births_source_path deaths_source_path marriages_source_path family_distance_threshold max_family_size family_merge_distance_threshold");
        }

        System.out.println( "Running KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" );
        String births_source_path = args[0];
        String deaths_source_path = args[1];
        String marriages_source_path = args[2];
        String family_distance_threshold_string = args[3];
        String max_family_size_string = args[4];
        String family_merge_distance_threshold_string = args[5];


        KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker matcher = new KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker(births_source_path, deaths_source_path, marriages_source_path, new Float(family_distance_threshold_string), new Integer(max_family_size_string),new Float(family_merge_distance_threshold_string)  );
        matcher.compute();
    }
}
