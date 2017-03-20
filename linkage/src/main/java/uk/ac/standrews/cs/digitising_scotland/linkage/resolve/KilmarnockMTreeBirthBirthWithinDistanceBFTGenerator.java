package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.linkage.resolve.distances.GFNGLNBFNBMNPOMDOMDistanceOverBirth;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.DataDistance;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.MTree;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Attempt to perform linking using MTree matching
 * File is derived from KilmarnockLinker.
 * Created by al on 17/2/1017
 */
public class KilmarnockMTreeBirthBirthWithinDistanceBFTGenerator extends KilmarnockMTreeMatcherGroundTruthChecker {

    private  MTree<BirthFamilyGT> birthMTree;

    // Maps

    public KilmarnockMTreeBirthBirthWithinDistanceBFTGenerator(String births_source_path, String deaths_source_path, String marriages_source_path) throws RecordFormatException, RepositoryException, StoreException, JSONException, BucketException, IOException {
        super(births_source_path, deaths_source_path, marriages_source_path );
    }

    private void compute() throws Exception {

        timedRun("Creating Birth MTree", new Callable<Void>(){
            public Void call() throws RepositoryException, BucketException, IOException {
                createBirthMTreeOverGFNGLNBFNBMNPOMDOM();
                return null;
            }
        });

        timedRun("Dumping bft.json", new Callable<Void>(){
            public Void call() throws RepositoryException, BucketException, IOException {
                dumpBFT();
                return null;
            }
        });

        System.out.println("Finished");
    }

    private void dumpBFT() throws FileNotFoundException, UnsupportedEncodingException {

        IInputStream<BirthFamilyGT> stream;
        try {
            stream = births.getInputStream();
        } catch (BucketException e) {
            System.out.println("Exception whilst getting births");
            return;
        }

        boolean first = true;

        PrintWriter writer = new PrintWriter("bft.json", "UTF-8");
        writer.print("{");

        for (BirthFamilyGT b : stream) {
            if (!first) {
                writer.println(",");
            }
            else {
                first = false;
            }
            // Calculate the neighbours of b, including b which is found in the rangeSearch
            List<DataDistance<BirthFamilyGT>> bsNeighbours = birthMTree.rangeSearch(b, 3);  // pronounced b's neighbours.

            writer.print("\"" + b.getId() + "\" : [");
            for (int i = 0; i < bsNeighbours.size(); i++) {
                writer.print("[" + bsNeighbours.get(i).value.getId() + ", " + Math.round(bsNeighbours.get(i).distance)+ "]");
                if (i != bsNeighbours.size() - 1) {
                    writer.print(",");
                }
            }
            writer.print(']');
        }
        writer.println("}");
        writer.close();
    }

    private void createBirthMTreeOverGFNGLNBFNBMNPOMDOM() throws RepositoryException, BucketException, IOException {

        System.out.println("Creating M Tree of births by GFNGLNBFNBMNPOMDOMDistanceOverBirth...");

        birthMTree = new MTree<>(new GFNGLNBFNBMNPOMDOMDistanceOverBirth());

        IInputStream<BirthFamilyGT> stream = births.getInputStream();

        for (BirthFamilyGT birth : stream) {

            birthMTree.add( birth );
        }
    }

    //***********************************************************************************

    public static void main(String[] args) throws Exception {

        if (args.length >= 3) {

            String births_source_path = args[0];
            String deaths_source_path = args[1];
            String marriages_source_path = args[2];
            System.out.println("Running KilmarnockMTreeBirthBirthWithinDistanceBFTGenerator");

            KilmarnockMTreeBirthBirthWithinDistanceBFTGenerator matcher = new KilmarnockMTreeBirthBirthWithinDistanceBFTGenerator(births_source_path, deaths_source_path, marriages_source_path);
            matcher.compute();
        } else {
            usage();
        }
    }

    private static void usage() {

        System.err.println("Usage: run with births_source_path deaths_source_path marriages_source_path");
    }
}
