package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import uk.ac.standrews.cs.digitising_scotland.linkage.experiments.Experiment;
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
import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * Attempt to perform linking using MTree matching
 * File is derived from KilmarnockLinker.
 * Created by al on 17/2/1017
 */
public class KilmarnockMTreeBirthBirthWithinDistanceBFTGenerator extends KilmarnockMTreeMatcherGroundTruthChecker {

    public static final String[] ARG_NAMES = {"births_source_path", "deaths_source_path", "marriages_source_path"};
    private  MTree<BirthFamilyGT> birthMTree;

    public KilmarnockMTreeBirthBirthWithinDistanceBFTGenerator() throws StoreException, IOException, RepositoryException {

    }

    private void compute() throws Exception {

        timedRun("Creating Birth MTree", () -> {
            createBirthMTreeOverGFNGLNBFNBMNPOMDOM();
            return null;
        });

        timedRun("Dumping bft.json", () -> {
            dumpBFT();
            return null;
        });
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

        Experiment experiment = new Experiment(ARG_NAMES, args, MethodHandles.lookup().lookupClass());

        KilmarnockMTreeBirthBirthWithinDistanceBFTGenerator matcher = new KilmarnockMTreeBirthBirthWithinDistanceBFTGenerator();

        if (args.length >= ARG_NAMES.length) {

            String births_source_path = args[0];
            String deaths_source_path = args[1];
            String marriages_source_path = args[2];

            experiment.printDescription();

            matcher.ingestRecords(births_source_path, deaths_source_path, marriages_source_path);
            matcher.compute();

        } else {
            experiment.usage();
        }
    }
}
