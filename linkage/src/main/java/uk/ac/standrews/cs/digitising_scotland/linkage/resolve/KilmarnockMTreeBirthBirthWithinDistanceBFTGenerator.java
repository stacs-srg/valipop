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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Attempt to perform linking using MTree matching
 * File is derived from KilmarnockLinker.
 * Created by al on 17/2/1017
 */
public class KilmarnockMTreeBirthBirthWithinDistanceBFTGenerator extends KilmarnockMTreeMatcherGroundTruthChecker {

    private  MTree<KillieBirth> birthMTree;

    // Maps

    public KilmarnockMTreeBirthBirthWithinDistanceBFTGenerator(String births_source_path, String deaths_source_path, String marriages_source_path) throws RecordFormatException, RepositoryException, StoreException, JSONException, BucketException, IOException {
        super(births_source_path, deaths_source_path, marriages_source_path );
    }

    private void compute() throws RepositoryException, BucketException, IOException {
        System.out.println("Creating Birth MTree");
        long time = System.currentTimeMillis();
        createBirthMTreeOverGFNGLNBFNBMNPOMDOM();
        long elapsed =  ( System.currentTimeMillis() - time ) / 1000 ;
        System.out.println("Created Marriage MTree in " + elapsed + "s");

        System.out.println("Forming families from Birth-Birth links");

        dumpBFT();

        System.out.println("Finished");
    }

    private void dumpBFT() {
        IInputStream<KillieBirth> stream;
        try {
            stream = births.getInputStream();
        } catch (BucketException e) {
            System.out.println("Exception whilst getting births");
            return;
        }

        Boolean first = true;

//        { a : [(b,2), (c,3)],
//          b : [(a,2), (d,4)]
//        }
        System.out.print("{");

        for (KillieBirth b : stream) {
            if (!first) {
                System.out.println(",");
            }
            else {
                first = false;
            }
            // Calculate the neighbours of b, including b which is found in the rangeSearch
            List<DataDistance<KillieBirth>> bsNeighbours = birthMTree.rangeSearch(b, 10);  // pronounced b's neighbours.

            System.out.print(b.getId() + ": [");
            for (int i = 0; i < bsNeighbours.size(); i++) {
                System.out.print("(" + bsNeighbours.get(i).value.getId() + ", " + bsNeighbours.get(i).distance + ")");
                if (i != bsNeighbours.size() - 1) {
                    System.out.print(",");
                }
            }
            System.out.print(']');
        }
        System.out.println("}");
    }


    private void createBirthMTreeOverGFNGLNBFNBMNPOMDOM() throws RepositoryException, BucketException, IOException {

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
    private void formFamilies() {

        IInputStream<KillieBirth> stream;
        try {
            stream = births.getInputStream();
        } catch (BucketException e) {
            System.out.println("Exception whilst getting births");
            return;
        }

        for (KillieBirth b : stream) {

            // Calculate the neighbours of b, including b which is found in the rangeSearch
            List<DataDistance<KillieBirth>> bsNeighbours = birthMTree.rangeSearch(b, 10);  // pronounced b's neighbours.

            // bs_neighbours_families is the set of families of neighbours that are different from bsFamily
            Set<Family> bs_neighbours_families = new TreeSet<Family>();

            Family bsFamily = families.get(b.getId()); // maybe null - is this right????

            // Add all of the families from bsNeighbours to bs_neighbours_families
            for (DataDistance<KillieBirth> dd_to_bs_neighbour : bsNeighbours) {
                KillieBirth bsNeighbour = dd_to_bs_neighbour.value;
                Family bs_neighbours_family = families.get(bsNeighbour.getId());
                if (bs_neighbours_family != null && bs_neighbours_family != bsFamily) {
                    bs_neighbours_families.add(bs_neighbours_family);
                }
            }

            Family thisFamily;

            if (bs_neighbours_families.size() == 1) { // just bsFamily in the set?
                // there are no "competing" family ids for this group of people
                // their id can remain the same

                thisFamily = bs_neighbours_families.iterator().next();

            } else {
                // there are (zero or) multiple "competing" family ids for this group of people
                // let's merge them
                thisFamily = new Family(b);
            }

            //  make all of bsNeighbours be in thisFamily
            for (DataDistance<KillieBirth> dd : bsNeighbours) {
                KillieBirth person = dd.value;
                families.put(person.getId(), thisFamily);
            }

            // if a person was previously in a different family, we merge them into thisFamily
            for (Family bs_neighbours_familiy : bs_neighbours_families) {
                for (KillieBirth sibling : bs_neighbours_familiy.siblings) {

                    if (families.containsKey(sibling.getId())) {
                        families.put(sibling.getId(), thisFamily); //  replace person's family with the new one.
                    }
                }
            }

        }
    }

    //***********************************************************************************

    public static void main(String[] args) throws Exception {

        if( args.length < 3 ) {
            ErrorHandling.error( "Usage: run with births_source_path deaths_source_path marriages_source_path");
        }

        System.out.println( "Running KilmarnockMTreeBirthBirthWithinDistanceGroundTruthChecker" );
        String births_source_path = args[0];
        String deaths_source_path = args[1];
        String marriages_source_path = args[2];

        KilmarnockMTreeBirthBirthWithinDistanceBFTGenerator matcher = new KilmarnockMTreeBirthBirthWithinDistanceBFTGenerator(births_source_path, deaths_source_path, marriages_source_path);
        matcher.compute();
    }
}
