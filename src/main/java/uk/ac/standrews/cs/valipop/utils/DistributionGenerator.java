package uk.ac.standrews.cs.valipop.utils;

import org.apache.commons.math3.random.JDKRandomGenerator;
import uk.ac.standrews.cs.valipop.statistics.distributions.InconsistentWeightException;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.AgeDependantEnumeratedDistribution;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DistributionGenerator {


    public static void main(String[] args) throws IOException, InvalidInputFileException, InconsistentWeightException {

        int forYear = Integer.valueOf(args[0]);
        String sourcePopulation = args[1];
        String sourceOrganisation = args[2];

        Path outToDir = Paths.get(args[3]);

        String filterOn = args[4];
        String groupY = args[5];
        String groupX = args[6];

        ArrayList<String> lines = new ArrayList<>();
        String labels = "";

        for(int x = 7; x < args.length; x++) {
            ArrayList<String> fileLines = new ArrayList<>(InputFileReader.getAllLines(Paths.get(args[x])));

            if (lines.isEmpty()) {
                labels = fileLines.get(0);
                lines.addAll(fileLines.subList(1, fileLines.size() - 1));
            } else if (labels.equals(fileLines.get(0))) {
                lines.addAll(fileLines.subList(1, fileLines.size() - 1));
            } else {
                System.out.println("File header labels incompatible");
            }
        }

        DataRowSet dataset = new DataRowSet(labels, lines);

        if(dataset.hasLabel(filterOn) && dataset.hasLabel(groupY) && dataset.hasLabel(groupX)) {

            Collection<DataRowSet> tables = dataset.splitOn(filterOn);

            for(DataRowSet table : tables) {
                AgeDependantEnumeratedDistribution aDEDist = new AgeDependantEnumeratedDistribution(Year.of(forYear), sourcePopulation, sourceOrganisation, table.to2DTableOfProportions(groupX, groupY), new JDKRandomGenerator());
            //    aDEDist.outputToFile(outToDir);
            }


        } else {
            throw new InvalidInputFileException("group or/and filter variables do not appear in file labels");
        }




    }


}
