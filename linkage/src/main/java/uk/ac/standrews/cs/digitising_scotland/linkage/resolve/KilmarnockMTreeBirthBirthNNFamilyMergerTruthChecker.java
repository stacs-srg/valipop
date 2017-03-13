package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.resolve.distances.GFNGLNBFNBMNPOMDOMDistanceOverMarriage;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.MTree;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;

import java.io.IOException;

/**
 * Created by al on 10/03/2017.
 */
public class KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker extends KilmarnockMTreeBirthBirthThresholdNNGroundTruthChecker {
    public KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker(String births_source_path, String deaths_source_path, String marriages_source_path) throws RecordFormatException, RepositoryException, StoreException, JSONException, BucketException, IOException {
        super(births_source_path, deaths_source_path, marriages_source_path);
    }

    private void compute() throws RepositoryException, BucketException, IOException {
        System.out.println("Creating Birth MTree");
        long time = System.currentTimeMillis();
        createBirthMTreeOverGFNGLNBFNBMNPOMDOM();
        long elapsed =  ( System.currentTimeMillis() - time ) / 1000 ;
        System.out.println("Created Marriage MTree in " + elapsed + "s");

        System.out.println("Forming families from Birth-Birth links");
        formFamilies();
        mergeFamilies();
        listFamilies();

        elapsed =  ( System.currentTimeMillis() - time ) / 1000 ;
        System.out.println("Finished in " + elapsed + "s");
    }

    private void mergeFamilies() {
        MTree<Family> familyMTree = new MTree( new GFNGLNBFNBMNPOMDOMDistanceOverMarriage() );
        for( Family f : families.values() ) {

        }


    }

    //***********************************************************************************

    public static void main(String[] args) throws Exception {

        if( args.length < 3 ) {
            ErrorHandling.error( "Usage: run with births_source_path deaths_source_path marriages_source_path");
        }

        System.out.println( "Running KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" );
        String births_source_path = args[0];
        String deaths_source_path = args[1];
        String marriages_source_path = args[2];

        KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker matcher = new KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker(births_source_path, deaths_source_path, marriages_source_path);
        matcher.compute();
    }
}
