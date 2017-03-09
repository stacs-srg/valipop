package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.KillieBirth;
import uk.ac.standrews.cs.digitising_scotland.linkage.resolve.distances.GFNGLNBFNBMNPOMDOMDistanceOverBirth;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.DataDistance;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.MTree;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Attempt to perform linking using MTree matching
 * File is derived from KilmarnockLinker.
 * Created by al on 17/2/1017
 */
public class KilmarnockMTreeBirthBirthWithinDistanceGroundTruthChecker extends KilmarnockMTreeMatcherGroundTruthChecker {

    private  MTree<KillieBirth> birthMTree;

    // Maps

    private HashMap<Long, Family> families = new HashMap<>(); // Maps from person id to family.

    public KilmarnockMTreeBirthBirthWithinDistanceGroundTruthChecker(String births_source_path, String deaths_source_path, String marriages_source_path) throws RecordFormatException, RepositoryException, StoreException, JSONException, BucketException, IOException {
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

            // Calculate the neighbours of b, including b.
            List<DataDistance<KillieBirth>> bsNeighbours = birthMTree.rangeSearch(b, 10);  // pronounced b's neighbours.
            bsNeighbours.add(new DataDistance<KillieBirth>(b, 0.0F));

            // fids is the set of families of neighbours that are different from bsFamily
            Set<Family> fids = new TreeSet<Family>();

            Family bsFamily = families.get(b.getId()); // maybe null - is this right????

            for (DataDistance<KillieBirth> dd_to_b : bsNeighbours) {
                KillieBirth n = dd_to_b.value;
                Family nsFamily = families.get(n.getId());
                if (nsFamily != null && nsFamily != bsFamily) {
                    fids.add(nsFamily);
                }
            }

            Family thisFamily;

            if (fids.size() == 1) { // just bsFamily in the set?
                // there are no "competing" family ids for this group of people
                // their id can remain the same

                thisFamily = fids.iterator().next();

            } else {
                // there are (zero or) multiple "competing" family ids for this group of people
                // let's merge them
                thisFamily = new Family();

                //  by now all neighbours are in the same family
                for (DataDistance<KillieBirth> dd : bsNeighbours) {
                    KillieBirth person = dd.value;
                    families.put(person.getId(), thisFamily);
                }

                // if a person was previously in a different family, we merge them into "thisFamilyID"
                for (Family f : fids) {
                    for (KillieBirth person : f.siblings) {

                        if (families.containsKey(person.getId())) {
                            families.remove(person.getId()); // nuke old family
                            families.put(person.getId(), thisFamily); // and replace with the new one.
                        }
                    }
                }
            }
        }
    }


    //***********************************************************************************

    public static void main(String[] args) throws Exception {

        System.out.println( "Running KilmarnockMTreeBirthBirthThresholdNNGroundTruthChecker" );
        String births_source_path = "/Digitising Scotland/KilmarnockBDM/births.csv";
        String deaths_source_path = "/Digitising Scotland/KilmarnockBDM/deaths.csv";
        String marriages_source_path = "/Digitising Scotland/KilmarnockBDM/marriages.csv";

        KilmarnockMTreeBirthBirthWithinDistanceGroundTruthChecker matcher = new KilmarnockMTreeBirthBirthWithinDistanceGroundTruthChecker(births_source_path, deaths_source_path, marriages_source_path);
        matcher.compute();
    }
}
