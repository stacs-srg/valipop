package utils;

import model.implementation.populationStatistics.IntegerRange;
import model.implementation.populationStatistics.InvalidRangeException;
import model.implementation.populationStatistics.OneDimensionDataDistribution;
import model.implementation.populationStatistics.TwoDimensionDataDistribution;
import model.time.TimeInstant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class InputFileReader {

    public static Logger log = LogManager.getLogger(InputFileReader.class);

    private static final String TAB = "\t";
    private static final String COMMENT_INDICATOR = "#";

    public static Collection<String> getAllLines(Path path) {

        Collection<String> lines = new ArrayList<String>();
        String line;

        // Reads in all lines to a collection of Strings
        try (BufferedReader reader = Files.newBufferedReader(path)) {

            while((line = reader.readLine()) != null) {

                if (line.startsWith(COMMENT_INDICATOR)) {
                    continue;
                } else {
                    lines.add(line);
                }

            }
        } catch (IOException e) {
            log.fatal("Unable to read in the lines of the file: " + path.toString());
            System.exit(2);
        }

        return lines;
    }

    public static TwoDimensionDataDistribution readIn2DDataFile(Path path) {

        ArrayList<String> lines = new ArrayList<String>(getAllLines(path));

        TimeInstant year = null;
        String sourcePopulation = null;
        String sourceOrganisation = null;

        ArrayList<IntegerRange> columnLabels = new ArrayList<IntegerRange>();
        Map<IntegerRange, Map<IntegerRange, Double>> data = new HashMap<IntegerRange, Map<IntegerRange, Double>>();


        for(int i = 0; i < lines.size(); i++) {

            String s = lines.get(i);
            String[] split = s.split(TAB);

            switch (split[0].toLowerCase()) {
                case "year":
                    try {
                        year = new TimeInstant(split[1]);
                    } catch (NumberFormatException e) {
                        log.fatal("Non integer value given for year in file: " + path.toString());
                        System.exit(3);
                    }
                    break;
                case "population":
                    sourcePopulation = split[1];
                    break;
                case "source":
                    sourceOrganisation = split[1];
                    break;
                case "labels":
                    s = split[1];
                    split = s.split(TAB);

                    for(String l : split) {
                        try {
                            columnLabels.add(new IntegerRange(l));
                        } catch (NumberFormatException e) {
                            log.fatal("A LABEL is of the incorrect form in the file: " + path.toString());
                            System.exit(3);
                        } catch (InvalidRangeException e1) {
                            log.fatal("A LABEL specifies an invalid range in the file: " + path.toString());
                            System.exit(3);
                        }
                    }

                    break;
                case "data":
                    for( ; i < lines.size(); i++) {
                        s = lines.get(i);
                        split = s.split(TAB);

                        if(split.length != columnLabels.size() + 1) {
                            log.fatal("One or more data rows do not have the correct number of values in the file: " + path.toString());
                            System.exit(3);
                        }

                        IntegerRange rowLabel = null;
                        try {
                            rowLabel = new IntegerRange(split[0]);
                        } catch (NumberFormatException e) {
                            log.fatal("The first column is of an incorrect form on line " + (i+1) + "in the file: " + path.toString());
                            System.exit(3);
                        } catch (InvalidRangeException e1) {
                            log.fatal("The first column specifies an invalid range on line " + (i+1) + "in the file: " + path.toString());
                            System.exit(3);
                        }

                        Map<IntegerRange, Double> rowMap = new HashMap<IntegerRange, Double>();

                        for(int j = 1; j < split.length; j++) {
                            try {
                                rowMap.put(columnLabels.get(j - 1), Double.parseDouble(split[j]));
                            } catch (NumberFormatException e) {
                                log.fatal("The value in column " + j + " should be a Double on line " + (i+1) + "in the file: " + path.toString());
                                System.exit(3);
                            }
                        }

                        data.put(rowLabel, rowMap);

                    }
                    break;
            }


        }

        return new TwoDimensionDataDistribution(year, sourcePopulation, sourceOrganisation, data);
    }


    public static OneDimensionDataDistribution readIn1DDataFile(Path path) {

        ArrayList<String> lines = new ArrayList<String>(getAllLines(path));

        TimeInstant year = null;
        String sourcePopulation = null;
        String sourceOrganisation = null;

        Map<IntegerRange, Double> data = new HashMap<IntegerRange, Double>();


        for(int i = 0; i < lines.size(); i++) {

            String s = lines.get(i);
            String[] split = s.split(TAB);

            switch (split[0].toLowerCase()) {
                case "year":
                    try {
                        year = new TimeInstant(split[1]);
                    } catch (NumberFormatException e) {
                        log.fatal("Non integer value given for year in file: " + path.toString());
                        System.exit(3);
                    }
                    break;
                case "population":
                    sourcePopulation = split[1];
                    break;
                case "source":
                    sourceOrganisation = split[1];
                    break;
                case "data":
                    for( ; i < lines.size(); i++) {
                        s = lines.get(i);
                        split = s.split(TAB);

                        IntegerRange rowLabel = null;
                        try {
                            rowLabel = new IntegerRange(split[0]);
                        } catch (NumberFormatException e) {
                            log.fatal("The label is of an incorrect form on line " + (i+1) + "in the file: " + path.toString());
                            System.exit(3);
                        } catch (InvalidRangeException e1) {
                            log.fatal("The label specifies an invalid range on line " + (i+1) + "in the file: " + path.toString());
                            System.exit(3);
                        }

                        data.put(rowLabel, Double.parseDouble(split[1]));

                    }
                    break;
            }


        }

        return new OneDimensionDataDistribution(year, sourcePopulation, sourceOrganisation, data);
    }

}
