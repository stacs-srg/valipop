/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop.utils.fileUtils;

import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.basic_model.distributions.general.InconsistentWeightException;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.AgeDependantEnumeratedDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.OneDimensionDataDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.ProportionalDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.ValiPopEnumeratedDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting.MotherChildAdapter;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting.SelfCorrectingProportionalDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting.SelfCorrectingTwoDimensionDataDistribution;
import uk.ac.standrews.cs.valipop.utils.Logger;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.IntegerRange;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.InvalidRangeException;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.*;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting.SelfCorrectingOneDimensionDataDistribution;


import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class InputFileReader {

    private static final String TAB = "\t";
    private static final String COMMENT_INDICATOR = "#";
    public static Logger log = new Logger(InputFileReader.class);

    public static Collection<String> getAllLines(Path path) throws IOException {

        Collection<String> lines = new ArrayList<>();
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

    public static Map<YearDate, Double> readInSingleInputFile(Path path, Config config) throws IOException, InvalidInputFileException {

        Map<YearDate, Double> data = new HashMap<>();

        ArrayList<String> lines = new ArrayList<>(getAllLines(path));

        String sourcePopulation = null;
        String sourceOrganisation = null;

        for (int i = 0; i < lines.size(); i++) {

            String s = lines.get(i);
            String[] split = s.split(TAB);

            switch (split[0].toLowerCase()) {
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

                        YearDate year = null;
                        try {
                            year = new MonthDate("01/01/" + split[0]).getYearDate();
                        } catch (NumberFormatException e) {
                            throw new InvalidInputFileException("The year is of an incorrect form on line " + (i + 1) + " in the file: " + path.toString(), e);
                        }

                        data.put(year, Double.parseDouble(split[1]));

                    }
                    break;
            }
        }

        return data;

    }

    public static SelfCorrectingTwoDimensionDataDistribution readInSC2DDataFile(Path path, Config config, RandomGenerator randomGenerator) throws IOException, InvalidInputFileException {

        ArrayList<String> lines = new ArrayList<>(getAllLines(path));

        YearDate year = null;
        String sourcePopulation = null;
        String sourceOrganisation = null;

        ArrayList<IntegerRange> columnLabels = new ArrayList<>();
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
                    columnLabels = readInLabels(split, path);
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

                        Map<IntegerRange, Double> rowMap = new HashMap<>();

                        for (int j = 1; j < split.length; j++) {
                            try {
                                rowMap.put(columnLabels.get(j - 1), Double.parseDouble(split[j]));
                            } catch (NumberFormatException e) {
                                throw new InvalidInputFileException("The value in column " + j + " should be a Double on line " + (i + 1) + "in the file: " + path.toString(), e);
                            }
                        }

                        data.put(rowLabel,
                                new SelfCorrectingOneDimensionDataDistribution(
                                        year, sourcePopulation, sourceOrganisation, rowMap, config.getBinominalSampling(),
                                        randomGenerator
                                )
                        );

                    }
                    break;
            }


        }

        return new SelfCorrectingTwoDimensionDataDistribution(year, sourcePopulation, sourceOrganisation, data);
    }

    public static ValiPopEnumeratedDistribution readInNameDataFile(Path path, RandomGenerator randomGenerator) throws IOException, InvalidInputFileException, InconsistentWeightException {

        ArrayList<String> lines = new ArrayList<>(getAllLines(path));

        YearDate year = null;
        String sourcePopulation = null;
        String sourceOrganisation = null;

        ArrayList<String> columnLabels = new ArrayList<>();
        Map<String, BigDecimal> data = new HashMap<>();


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
                    columnLabels = readInStringLabels(split);
                    break;
                case "data":
                    i++; // go to next line for data rows
                    for (; i < lines.size(); i++) {
                        s = lines.get(i);
                        split = s.split(TAB);

                        if (split.length != columnLabels.size()) {
                            throw new InvalidInputFileException("One or more data rows do not have the correct number of values in the file: " + path.toString());
                        }

                        String rowLabel = split[0];

                        data.put(rowLabel, new BigDecimal(split[1]));

                    }
                    break;
            }


        }

        return new ValiPopEnumeratedDistribution(year, sourcePopulation, sourceOrganisation, data, randomGenerator);
    }

    public static AgeDependantEnumeratedDistribution readInDeathCauseDataFile(Path path, RandomGenerator randomGenerator) throws IOException, InvalidInputFileException, InconsistentWeightException {

        ArrayList<String> lines = new ArrayList<>(getAllLines(path));

        YearDate year = null;
        String sourcePopulation = null;
        String sourceOrganisation = null;

        ArrayList<String> columnLabels = new ArrayList<>();
        Map<IntegerRange, LabelledValueSet<String, BigDecimal>> data = new HashMap<>();


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
                    columnLabels = readInStringLabels(split);
                    break;
                case "data":
                    data = readIn2DDataTable(i, lines, path, columnLabels, StringToBigDecimalSet.class);
                    break;
            }


        }

        return new AgeDependantEnumeratedDistribution(year, sourcePopulation, sourceOrganisation, data, randomGenerator);
    }

    public static OneDimensionDataDistribution readIn1DDataFile(Path path) throws IOException, InvalidInputFileException {

        ArrayList<String> lines = new ArrayList<>(getAllLines(path));

        YearDate year = null;
        String sourcePopulation = null;
        String sourceOrganisation = null;

        Map<IntegerRange, Double> data = new HashMap<>();


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

    public static SelfCorrectingOneDimensionDataDistribution readInSC1DDataFile(Path path, Config config, RandomGenerator randomGenerator) throws IOException, InvalidInputFileException {
        OneDimensionDataDistribution d = readIn1DDataFile(path);
        return new SelfCorrectingOneDimensionDataDistribution(d.getYear(), d.getSourcePopulation(),
                d.getSourceOrganisation(), d.cloneData(), config.getBinominalSampling(), randomGenerator);
    }

    public static SelfCorrectingProportionalDistribution readInAgeAndProportionalStatsInput(Path path) throws IOException, InvalidInputFileException {
        ArrayList<String> lines = new ArrayList<>(getAllLines(path));

        YearDate year = null;
        String sourcePopulation = null;
        String sourceOrganisation = null;

        ArrayList<IntegerRange> columnLabels = new ArrayList<>();
        Map<IntegerRange, LabelledValueSet<IntegerRange, Double>> data = new HashMap<>();


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
                    columnLabels = readInLabels(split, path);
                    break;
                case "data":
                    data = readIn2DDataTable(i, lines, path, columnLabels, IntegerRangeToDoubleSet.class);
                    break;
                default:
                    break;
            }

        }
        return new SelfCorrectingProportionalDistribution(year, sourcePopulation, sourceOrganisation, data);
    }

    public static ProportionalDistribution readInAndAdaptAgeAndProportionalStatsInput(Path path) throws IOException, InvalidInputFileException {

        ArrayList<String> lines = new ArrayList<>(getAllLines(path));

        YearDate year = null;
        String sourcePopulation = null;
        String sourceOrganisation = null;

        ArrayList<IntegerRange> columnLabels = new ArrayList<>();
        Map<IntegerRange, LabelledValueSet<IntegerRange, Double>> data = new HashMap<>();


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
                    columnLabels = readInLabels(split, path);
                    break;
                case "data":
                    data = readIn2DDataTable(i, lines, path, columnLabels, IntegerRangeToDoubleSet.class);
                    break;
                default:
                    break;
            }

        }
        return new MotherChildAdapter(year, sourcePopulation, sourceOrganisation, data);
    }

//    public static ProportionalDistribution readInAgeAndProportionalStatsInput(Path path) throws IOException, InvalidInputFileException {
//
//        ArrayList<String> lines = new ArrayList<>(getAllLines(path));
//
//        YearDate year = null;
//        String sourcePopulation = null;
//        String sourceOrganisation = null;
//
//        ArrayList<IntegerRange> columnLabels = new ArrayList<>();
//        Map<IntegerRange, LabelledValueSet<IntegerRange, Double>> data = new HashMap<>();
//
//
//        for (int i = 0; i < lines.size(); i++) {
//
//            String s = lines.get(i);
//            String[] split = s.split(TAB, 2);
//
//            switch (split[0].toLowerCase()) {
//                case "year":
//                    try {
//                        year = new YearDate(Integer.parseInt(split[1]));
//                    } catch (NumberFormatException e) {
//                        throw new InvalidInputFileException("Non integer value given for year in file: " + path.toString(), e);
//                    }
//                    break;
//                case "population":
//                    sourcePopulation = split[1];
//                    break;
//                case "source":
//                    sourceOrganisation = split[1];
//                    break;
//                case "labels":
//                    columnLabels = readInLabels(split, path);
//                    break;
//                case "data":
//                    data = readIn2DDataTable(i, lines, path, columnLabels);
//                    break;
//                default:
//                    break;
//            }
//
//        }
//        return new MotherChildAdapter(year, sourcePopulation, sourceOrganisation, data);
//    }

    private static ArrayList<IntegerRange> readInLabels(String[] split, Path path) throws InvalidInputFileException {

        ArrayList<IntegerRange> columnLabels = new ArrayList<>();
        String s = split[1];
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

        return columnLabels;
    }

    private static ArrayList<String> readInStringLabels(String[] split) {

        ArrayList<String> columnLabels = new ArrayList<>();
        String s = split[1];
        split = s.split(TAB);

        for (String l : split) {
            columnLabels.add(l);
        }

        return columnLabels;
    }

    private static <L, V extends Number> Map<IntegerRange, LabelledValueSet<L, V>> readIn2DDataTable(
                                            int i, ArrayList<String> lines, Path path, ArrayList<L> columnLabels,
                                                Class<? extends AbstractLabelToAbstractValueSet<L, V>> setType)
                                                                                    throws  InvalidInputFileException{



        Map<IntegerRange, LabelledValueSet<L, V>> data = new HashMap<>();

        i++; // go to next line for data rows
        for (; i < lines.size(); i++) {
            String s = lines.get(i);
            String[] split = s.split(TAB);

            if (split.length != columnLabels.size() + 1) {
                throw new InvalidInputFileException("One or more data rows do not have the correct number of values in the file: " + path.toString());
            }

            IntegerRange rowLabel;
            try {
                rowLabel = new IntegerRange(split[0]);
            } catch (NumberFormatException e) {
                throw new InvalidInputFileException("The first column is of an incorrect form on line " + (i + 1) + " in the file: " + path.toString(), e);
            } catch (InvalidRangeException e) {
                throw new InvalidInputFileException("The first column specifies an invalid range on line " + (i + 1) + " in the file: " + path.toString(), e);
            }

            Map<L, V> rowMap = new HashMap<>();

            Class<V> clazz = null;
            try {
                clazz = setType.newInstance().getValueClass();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            for (int j = 1; j < split.length; j++) {
                try {
                    try {
                        rowMap.put(columnLabels.get(j - 1), clazz.getConstructor(String.class).newInstance(split[j]));
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                } catch (NumberFormatException e) {
                    throw new InvalidInputFileException("The value in column " + j + " should be a Double on line " + (i + 1) + "in the file: " + path.toString(), e);
                }
            }

            try {
                data.put(rowLabel, setType.newInstance().init(rowMap));
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                System.out.println("Something has gone wrong with the fancy generics/reflection bit");
                throw new Error();
            }

        }
        return data;
    }

}
