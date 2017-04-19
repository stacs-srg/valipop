package utils.fileUtils;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import populationStatistics.dataDistributionTables.OneDimensionDataDistribution;
import populationStatistics.dataDistributionTables.TwoDimensionDataDistribution;
import populationStatistics.dataDistributionTables.selfCorrecting.SelfCorrectingOneDimensionDataDistribution;
import populationStatistics.dataDistributionTables.selfCorrecting.SelfCorrectingTwoDimensionDataDistribution;
import dateModel.dateImplementations.YearDate;
import utils.specialTypes.integerRange.IntegerRange;
import utils.specialTypes.integerRange.InvalidRangeException;


import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class InputFileReader {

    private static final String TAB = "\t";
    private static final String COMMENT_INDICATOR = "#";
    public static Logger log = LogManager.getLogger(InputFileReader.class);

    public static Collection<String> getAllLines(Path path) throws IOException {

        Collection<String> lines = new ArrayList<String>();
        String line;

        // Reads in all lines to a collection of Strings
        try (BufferedReader reader = Files.newBufferedReader(path)) {

            while ((line = reader.readLine()) != null) {

                if (line.startsWith(COMMENT_INDICATOR) || line.length() == 0) {
                    continue;
                } else {
                    lines.add(line);
                }

            }
        } catch (IOException e) {
            throw new IOException("Unable to read in the lines of the file: " + path.toString(), e);

        }

        return lines;
    }

    public static TwoDimensionDataDistribution readIn2DDataFile(Path path) throws IOException, InvalidInputFileException {

        ArrayList<String> lines = new ArrayList<String>(getAllLines(path));

        YearDate year = null;
        String sourcePopulation = null;
        String sourceOrganisation = null;

        ArrayList<IntegerRange> columnLabels = new ArrayList<IntegerRange>();
        Map<IntegerRange, OneDimensionDataDistribution> data = new HashMap<IntegerRange, OneDimensionDataDistribution>();


        for (int i = 0; i < lines.size(); i++) {

            String s = lines.get(i);
            String[] split = s.split(TAB, 2);

            switch (split[0].toLowerCase()) {
                case "year":
                    try {
                        year = new YearDate(Integer.parseInt(split[1]));
                    } catch (NumberFormatException e) {
                        throw new InvalidInputFileException("Non integer value given for year in file: " + path.toString(), e);
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

                    for (String l : split) {
                        try {
                            columnLabels.add(new IntegerRange(l));

                        } catch (NumberFormatException e) {
                            throw new InvalidInputFileException("A LABEL is of the incorrect form in the file: " + path.toString(), e);
                        } catch (InvalidRangeException e) {
                            throw new InvalidInputFileException("A LABEL specifies an invalid range in the file: " + path.toString(), e);
                        }
                    }

                    break;
                case "data":
                    i++; // go to next line for data rows
                    for (; i < lines.size(); i++) {
                        s = lines.get(i);
                        split = s.split(TAB);

                        if (split.length != columnLabels.size() + 1) {
                            throw new InvalidInputFileException("One or more data rows do not have the correct number of values in the file: " + path.toString());
                        }

                        IntegerRange rowLabel = null;
                        try {
                            rowLabel = new IntegerRange(split[0]);
                        } catch (NumberFormatException e) {
                            throw new InvalidInputFileException("The first column is of an incorrect form on line " + (i + 1) + " in the file: " + path.toString(), e);
                        } catch (InvalidRangeException e) {
                            throw new InvalidInputFileException("The first column specifies an invalid range on line " + (i + 1) + " in the file: " + path.toString(), e);
                        }

                        Map<IntegerRange, Double> rowMap = new HashMap<IntegerRange, Double>();

                        for (int j = 1; j < split.length; j++) {
                            try {
                                rowMap.put(columnLabels.get(j - 1), Double.parseDouble(split[j]));
                            } catch (NumberFormatException e) {
                                throw new InvalidInputFileException("The value in column " + j + " should be a Double on line " + (i + 1) + "in the file: " + path.toString(), e);
                            }
                        }

                        data.put(rowLabel, new OneDimensionDataDistribution(year, sourcePopulation, sourceOrganisation, rowMap));

                    }
                    break;
            }


        }

        return new TwoDimensionDataDistribution(year, sourcePopulation, sourceOrganisation, data);
    }

    public static SelfCorrectingTwoDimensionDataDistribution readInSC2DDataFile(Path path) throws IOException, InvalidInputFileException {

        ArrayList<String> lines = new ArrayList<String>(getAllLines(path));

        YearDate year = null;
        String sourcePopulation = null;
        String sourceOrganisation = null;

        ArrayList<IntegerRange> columnLabels = new ArrayList<IntegerRange>();
        Map<IntegerRange, SelfCorrectingOneDimensionDataDistribution> data = new HashMap<>();


        for (int i = 0; i < lines.size(); i++) {

            String s = lines.get(i);
            String[] split = s.split(TAB, 2);

            switch (split[0].toLowerCase()) {
                case "year":
                    try {
                        year = new YearDate(Integer.parseInt(split[1]));
                    } catch (NumberFormatException e) {
                        throw new InvalidInputFileException("Non integer value given for year in file: " + path.toString(), e);
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

                    for (String l : split) {
                        try {
                            columnLabels.add(new IntegerRange(l));
                        } catch (NumberFormatException e) {
                            throw new InvalidInputFileException("A LABEL is of the incorrect form in the file: " + path.toString(), e);
                        } catch (InvalidRangeException e) {
                            throw new InvalidInputFileException("A LABEL specifies an invalid range in the file: " + path.toString(), e);
                        }
                    }

                    break;
                case "data":
                    i++; // go to next line for data rows
                    for (; i < lines.size(); i++) {
                        s = lines.get(i);
                        split = s.split(TAB);

                        if (split.length != columnLabels.size() + 1) {
                            throw new InvalidInputFileException("One or more data rows do not have the correct number of values in the file: " + path.toString());
                        }

                        IntegerRange rowLabel = null;
                        try {
                            rowLabel = new IntegerRange(split[0]);
                        } catch (NumberFormatException e) {
                            throw new InvalidInputFileException("The first column is of an incorrect form on line " + (i + 1) + " in the file: " + path.toString(), e);
                        } catch (InvalidRangeException e) {
                            throw new InvalidInputFileException("The first column specifies an invalid range on line " + (i + 1) + " in the file: " + path.toString(), e);
                        }

                        Map<IntegerRange, Double> rowMap = new HashMap<IntegerRange, Double>();

                        for (int j = 1; j < split.length; j++) {
                            try {
                                rowMap.put(columnLabels.get(j - 1), Double.parseDouble(split[j]));
                            } catch (NumberFormatException e) {
                                throw new InvalidInputFileException("The value in column " + j + " should be a Double on line " + (i + 1) + "in the file: " + path.toString(), e);
                            }
                        }

                        data.put(rowLabel, new SelfCorrectingOneDimensionDataDistribution(year, sourcePopulation, sourceOrganisation, rowMap));

                    }
                    break;
            }


        }

        return new SelfCorrectingTwoDimensionDataDistribution(year, sourcePopulation, sourceOrganisation, data);
    }

//    public static SelfCorrectingTwoDimensionDataDistribution readInSC2DDataFile(Path path) {
//        SelfCorrectingTwoDimensionDataDistribution d = readIn2DDataFile(path);
//        return new SelfCorrectingTwoDimensionDataDistribution(d.getYear(), d.getSourcePopulation(), d.getSourceOrganisation(), d /*removed clone method call */);
//    }

    public static OneDimensionDataDistribution readIn1DDataFile(Path path) throws IOException, InvalidInputFileException {

        ArrayList<String> lines = new ArrayList<String>(getAllLines(path));

        YearDate year = null;
        String sourcePopulation = null;
        String sourceOrganisation = null;

        Map<IntegerRange, Double> data = new HashMap<IntegerRange, Double>();


        for (int i = 0; i < lines.size(); i++) {

            String s = lines.get(i);
            String[] split = s.split(TAB);

            switch (split[0].toLowerCase()) {
                case "year":
                    try {
                        year = new YearDate(Integer.parseInt(split[1]));
                    } catch (NumberFormatException e) {
                        throw new InvalidInputFileException("Non integer value given for year in file: " + path.toString(), e);
                    }
                    break;
                case "population":
                    sourcePopulation = split[1];
                    break;
                case "source":
                    sourceOrganisation = split[1];
                    break;
                case "data":
                    i++; // go to next line for data rows
                    for (; i < lines.size(); i++) {
                        s = lines.get(i);
                        split = s.split(TAB);

                        IntegerRange rowLabel = null;
                        try {
                            rowLabel = new IntegerRange(split[0]);
                        } catch (NumberFormatException e) {
                            throw new InvalidInputFileException("The label is of an incorrect form on line " + (i + 1) + " in the file: " + path.toString(), e);
                        } catch (InvalidRangeException e) {
                            throw new InvalidInputFileException("The label specifies an invalid range on line " + (i + 1) + "in the file: " + path.toString(), e);
                        }

                        data.put(rowLabel, Double.parseDouble(split[1]));

                    }
                    break;
            }


        }

        return new OneDimensionDataDistribution(year, sourcePopulation, sourceOrganisation, data);
    }

    public static SelfCorrectingOneDimensionDataDistribution readInSC1DDataFile(Path path) throws IOException, InvalidInputFileException {
        OneDimensionDataDistribution d = readIn1DDataFile(path);
        return new SelfCorrectingOneDimensionDataDistribution(d.getYear(), d.getSourcePopulation(), d.getSourceOrganisation(), d.cloneData());
    }
}
