package uk.ac.standrews.cs.valipop.utils;

import org.apache.commons.math3.random.JDKRandomGenerator;
import uk.ac.standrews.cs.nds.util.FileUtil;
import uk.ac.standrews.cs.valipop.statistics.distributions.InconsistentWeightException;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.AgeDependantEnumeratedDistribution;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRange;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.LabelledValueSet;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DistributionGenerator {


    public static void main(String[] args) throws IOException, InvalidInputFileException, InconsistentWeightException {

        for(String s: args)
            System.out.println(s);

        int forYear;
        try {
            forYear = Integer.valueOf(args[0]);
        } catch (NumberFormatException e) {

            throw new RuntimeException(args[0], e);
        }
        String sourcePopulation = args[1];
        String sourceOrganisation = args[2];

        Path outToDir = Paths.get(args[3]);

        String filterOn = args[4];
        String filterValue = args[5];
        String groupY = args[6];
        String groupX = args[7];

        LinkedList<String> lines = new LinkedList<>();
        String labels = "";

        String[] files = {"src/main/resources/valipop/inputs/icem-scot-1861/icem-scot-1861-counties-A-D.csv",
                "src/main/resources/valipop/inputs/icem-scot-1861/icem-scot-1861-counties-R-W.csv",
                "src/main/resources/valipop/inputs/icem-scot-1861/icem-scot-1861-counties-E-K.csv",
                "src/main/resources/valipop/inputs/icem-scot-1861/icem-scot-1861-counties-L-P.csv"};

//        String[] files = {"src/main/resources/valipop/inputs/icem-scot-1861/test.csv"};

        for(String file : files) {
            ArrayList<String> fileLines = new ArrayList<>(InputFileReader.getAllLines(Paths.get(file)));

            if (lines.isEmpty()) {
                labels = fileLines.get(0);
                lines.addAll(fileLines.subList(1, fileLines.size() - 1));
            } else if (labels.equals(fileLines.get(0))) {
                lines.addAll(fileLines.subList(1, fileLines.size() - 1));
            } else {
                throw new RuntimeException("File header labels incompatible");
            }
        }

        DataRowSet dataset = new DataRowSet(labels, lines, filterOn, filterValue);

        if(dataset.hasLabel(filterOn) && dataset.hasLabel(groupY) && dataset.hasLabel(groupX)) {

//            Map<String, DataRowSet> tables = dataset.splitOn(filterOn);

//            for(String splitOn : tables.keySet()) {

//                DataRowSet table = tables.get(splitOn);

                TreeMap<IntegerRange, LabelledValueSet<String, Double>> dist = dataset.to2DTableOfProportions(groupX, groupY, new JDKRandomGenerator());

                PrintStream ps = FileUtil.createPrintStreamToFile(Paths.get(outToDir.toString(), filterValue + ".txt").toString());

                boolean first = true;

                ps.println("YEAR\t" + forYear);
                ps.println("POPULATION\t" + sourcePopulation);
                ps.println("SOURCE\t" + sourceOrganisation);
                ps.println("VAR\tOCCUPATION");
                ps.println("FORM\tPROPORTION");
                ps.println("SEX\t" + filterValue);

                for(IntegerRange iR: dist.keySet()) {

                    LabelledValueSet<String, Double> row = dist.get(iR);

                    if(first) {
                        ps.print("LABELS\t");
                        for(String s : row.getLabels())
                            ps.print(s + "\t");
                        ps.println();
                        ps.println("DATA");
                        first = false;
                    }
                    ps.print(iR + " \t");

                    for(String s : row.getLabels())
                        ps.print(row.getValue(s) + "\t");

                    ps.println();
                }

                ps.close();


            }


//        } else {
//            throw new InvalidInputFileException("group or/and filter variables do not appear in file labels");
//        }




    }


}
