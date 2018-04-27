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
package uk.ac.standrews.cs.valipop.statistics.populationStatistics;

import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.basic_model.distributions.general.InconsistentWeightException;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.AgeDependantEnumeratedDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.InputMetaData;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.ProportionalDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.ValiPopEnumeratedDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting.SelfCorrectingProportionalDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting.SelfCorrectingTwoDimensionDataDistribution;
import uk.ac.standrews.cs.valipop.utils.Logger;
import uk.ac.standrews.cs.valipop.utils.fileUtils.InputFileReader;
import uk.ac.standrews.cs.valipop.utils.fileUtils.InvalidInputFileException;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting.SelfCorrectingOneDimensionDataDistribution;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.IntegerRange;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This factory class handles the correct construction of a PopulationStatistics object.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public abstract class DesiredPopulationStatisticsFactory {

    private static Logger log = new Logger(DesiredPopulationStatisticsFactory.class);

    private static final int DETERMINISTIC_SEED = 1111111111;
    private static RandomGenerator randomGenerator;

    /**
     * Creates a PopulationStatistics object.
     *
     * @return the quantified event occurrences
     */
    public static PopulationStatistics initialisePopulationStatistics(Config config) throws IOException, InvalidInputFileException, InconsistentWeightException {

        DesiredPopulationStatisticsFactory.log.info("Creating PopulationStatistics instance");

        randomGenerator = new JDKRandomGenerator();

        if(config.deterministic()) {
            randomGenerator.setSeed(DETERMINISTIC_SEED);
        }

        Map<YearDate, SelfCorrectingOneDimensionDataDistribution> maleDeath = readInSC1DDataFiles(config.getVarMaleLifetablePaths(), config);
        Map<YearDate, AgeDependantEnumeratedDistribution> maleDeathCauses = readInDeathCauseDataFiles(config.getVarMaleDeathCausesPaths(), config);
        Map<YearDate, SelfCorrectingOneDimensionDataDistribution> femaleDeath = readInSC1DDataFiles(config.getVarFemaleLifetablePaths(), config);
        Map<YearDate, AgeDependantEnumeratedDistribution> femaleDeathCauses = readInDeathCauseDataFiles(config.getVarFemaleDeathCausesPaths(), config);
        Map<YearDate, SelfCorrectingProportionalDistribution> partnering = readInAgeAndProportionalStatsInputFiles(config.getVarPartneringPaths(), config);
        Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> orderedBirth = readInSC2DDataFiles(config.getVarOrderedBirthPaths(), config);
        Map<YearDate, ProportionalDistribution> multipleBirth = readInAndAdaptAgeAndProportionalStatsInputFiles(config.getVarMultipleBirthPaths(), config);
        Map<YearDate, SelfCorrectingOneDimensionDataDistribution> illegitimateBirth = readInSC1DDataFiles(config.getVarIllegitimateBirthPaths(), config);
        Map<YearDate, SelfCorrectingOneDimensionDataDistribution> marriage = readInSC1DDataFiles(config.getVarMarriagePaths(), config);
        Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> separation = readInSC2DDataFiles(config.getVarSeparationPaths(), config);
        Map<YearDate, Double> sexRatioBirth = readInSingleInputDataFile(config.getVarBirthRatioPath(), config);
        Map<YearDate, ValiPopEnumeratedDistribution> maleForename = readInNamesDataFiles(config.getVarMaleForenamePath(), config);
        Map<YearDate, ValiPopEnumeratedDistribution> femaleForename = readInNamesDataFiles(config.getVarFemaleForenamePath(), config);
        Map<YearDate, ValiPopEnumeratedDistribution> surname = readInNamesDataFiles(config.getVarSurnamePath(), config);

        return new PopulationStatistics(maleDeath, maleDeathCauses, femaleDeath, femaleDeathCauses, partnering, orderedBirth, multipleBirth, illegitimateBirth,
                marriage, separation, sexRatioBirth, maleForename, femaleForename, surname, config.getMinBirthSpacing(),
                config.getMinGestationPeriodDays(), randomGenerator);

    }

    private static Map<YearDate, Double> readInSingleInputDataFile(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        int c = 0;

        Map<YearDate, Double> data = new HashMap<>();

        for(Path p : paths) {
            if(c == 1) {
                throw new Error("Too many sex ratio files - there should only be one - remove any additional files from the ratio_birth directory");
            }

            data = InputFileReader.readInSingleInputFile(p, config);

            c++;
        }

        if(data.isEmpty()) {
            data.put(new YearDate(1600), 0.5);
        }

        return data;
    }

    private static Map<YearDate,SelfCorrectingOneDimensionDataDistribution> readInSC1DDataFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        Map<YearDate, SelfCorrectingOneDimensionDataDistribution> data = new HashMap<>();

        for (Path path : paths) {
            // read in each file
            SelfCorrectingOneDimensionDataDistribution tempData = InputFileReader.readInSC1DDataFile(path, config, randomGenerator);
            data.put(tempData.getYear(), tempData);
        }
        return insertDistributionsToMeetInputWidth(config, data);

    }

    private static Map<YearDate,ValiPopEnumeratedDistribution> readInNamesDataFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException, InconsistentWeightException {

        Map<YearDate, ValiPopEnumeratedDistribution> data = new HashMap<>();

        for (Path path : paths) {
            // read in each file
            ValiPopEnumeratedDistribution tempData = InputFileReader.readInNameDataFile(path, randomGenerator);
            data.put(tempData.getYear(), tempData);
        }
        return insertDistributionsToMeetInputWidth(config, data);

    }

    private static Map<YearDate, AgeDependantEnumeratedDistribution> readInDeathCauseDataFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException, InconsistentWeightException {

        Map<YearDate, AgeDependantEnumeratedDistribution> data = new HashMap<>();

        for (Path path : paths) {
            // read in each file
            AgeDependantEnumeratedDistribution tempData = InputFileReader.readInDeathCauseDataFile(path, randomGenerator);
            data.put(tempData.getYear(), tempData);
        }
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private static Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> readInSC2DDataFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> data = new HashMap<>();

        for (Path path : paths) {
            // read in each file
            SelfCorrectingTwoDimensionDataDistribution tempData = InputFileReader.readInSC2DDataFile(path, config, randomGenerator);
            data.put(tempData.getYear(), tempData);
        }
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private static Map<YearDate, SelfCorrectingProportionalDistribution> readInAgeAndProportionalStatsInputFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        Map<YearDate, SelfCorrectingProportionalDistribution> data = new HashMap<>();

        for (Path path : paths) {
            // read in each file
            SelfCorrectingProportionalDistribution tempData = InputFileReader.readInAgeAndProportionalStatsInput(path);
            data.put(tempData.getYear(), tempData);
        }
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private static Map<YearDate, ProportionalDistribution> readInAndAdaptAgeAndProportionalStatsInputFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        Map<YearDate, ProportionalDistribution> data = new HashMap<>();

        for (Path path : paths) {
            // read in each file
            ProportionalDistribution tempData = InputFileReader.readInAndAdaptAgeAndProportionalStatsInput(path);
            data.put(tempData.getYear(), tempData);
        }
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private static <T extends InputMetaData> Map<YearDate, T>  insertDistributionsToMeetInputWidth(Config config, Map<YearDate, T> inputs) {

        CompoundTimeUnit inputWidth = config.getInputWidth();

        YearDate prevInputDate = config.getTS().getYearDate();

        int c = 1;
        Date curDate;

        YearDate[] years = inputs.keySet().toArray(new YearDate[inputs.keySet().size()]);
        Arrays.sort(years);

        if(years.length == 0) {
            return inputs;
        }

        while(DateUtils.dateBeforeOrEqual(curDate = prevInputDate.advanceTime(new CompoundTimeUnit(inputWidth.getCount() * c, inputWidth.getUnit())), years[0])) {
            inputs.put(curDate.getYearDate(), inputs.get(years[0]));
            c++;
        }

        prevInputDate = years[0];

        for(YearDate curInputDate : years) {


            while(DateUtils.dateBeforeOrEqual(curDate = prevInputDate.advanceTime(new CompoundTimeUnit(inputWidth.getCount() * c, inputWidth.getUnit())), curInputDate)) {
                YearDate duplicateFrom = getNearestDate(curDate, prevInputDate, curInputDate);
                inputs.put(curDate.getYearDate(), inputs.get(duplicateFrom));
                c++;
            }

            c = 1;
            prevInputDate = curInputDate;
        }

        while(DateUtils.dateBeforeOrEqual(curDate = prevInputDate.advanceTime(new CompoundTimeUnit(inputWidth.getCount() * c, inputWidth.getUnit())), config.getTE())) {
            inputs.put(curDate.getYearDate(), inputs.get(prevInputDate));
            c++;
        }


        return inputs;

    }

    private static YearDate getNearestDate(Date referenceDate, YearDate option1, YearDate option2) {

        int refTo1 = DateUtils.differenceInDays(referenceDate, option1);
        int refTo2 = DateUtils.differenceInDays(referenceDate, option2);

        if(refTo1 < refTo2) {
            return option1;
        } else {
            return option2;
        }

    }
}
