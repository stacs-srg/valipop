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
package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.inputted;

import uk.ac.standrews.cs.digitising_scotland.verisim.config.Config;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting.ProportionalDistributionAdapter;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting.SelfCorrectingProportionalDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.DataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting.SelfCorrectingOneDimensionDataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting.SelfCorrectingTwoDimensionDataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.fileUtils.InputFileReader;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.fileUtils.InvalidInputFileException;

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

    private static Logger log = LogManager.getLogger(DesiredPopulationStatisticsFactory.class);

    /**
     * Creates a PopulationStatistics object.
     *
     * @return the quantified event occurrences
     */
    public static PopulationStatistics initialisePopulationStatistics(Config config) throws IOException, InvalidInputFileException {

        DesiredPopulationStatisticsFactory.log.info("Creating PopulationStatistics instance");

        Map<YearDate, SelfCorrectingOneDimensionDataDistribution> maleDeath = readInSC1DDataFiles(config.getVarMaleDeathPaths(), config);
        Map<YearDate, SelfCorrectingOneDimensionDataDistribution> femaleDeath = readInSC1DDataFiles(config.getVarFemaleDeathPaths(), config);
        Map<YearDate, SelfCorrectingProportionalDistribution> partnering = readInAgeAndProportionalStatsInputFiles(config.getVarPartneringPaths(), config);
        Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> orderedBirth = readInSC2DDataFiles(config.getVarOrderedBirthPaths(), config);
        Map<YearDate, ProportionalDistributionAdapter> multipleBirth = readInAndAdaptAgeAndProportionalStatsInputFiles(config.getVarMultipleBirthPaths(), config);
        Map<YearDate, SelfCorrectingOneDimensionDataDistribution> separation = readInSC1DDataFiles(config.getVarSeparationPaths(), config);

        return new PopulationStatistics(config, maleDeath, femaleDeath, partnering, orderedBirth, multipleBirth, separation);
    }

    private static Map<YearDate,SelfCorrectingOneDimensionDataDistribution> readInSC1DDataFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        Map<YearDate, SelfCorrectingOneDimensionDataDistribution> data = new HashMap<>();

        for (Path path : paths) {
            // read in each file
            SelfCorrectingOneDimensionDataDistribution tempData = InputFileReader.readInSC1DDataFile(path, config);
            data.put(tempData.getYear(), tempData);
        }
        return insertDistributionsToMeetInputWidth(config, data);

    }

    private static Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> readInSC2DDataFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> data = new HashMap<>();

        for (Path path : paths) {
            // read in each file
            SelfCorrectingTwoDimensionDataDistribution tempData = InputFileReader.readInSC2DDataFile(path, config);
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

    private static Map<YearDate, ProportionalDistributionAdapter> readInAndAdaptAgeAndProportionalStatsInputFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        Map<YearDate, ProportionalDistributionAdapter> data = new HashMap<>();

        for (Path path : paths) {
            // read in each file
            ProportionalDistributionAdapter tempData = InputFileReader.readInAndAdaptAgeAndProportionalStatsInput(path);
            data.put(tempData.getYear(), tempData);
        }
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private static <T extends DataDistribution> Map<YearDate, T>  insertDistributionsToMeetInputWidth(Config config, Map<YearDate, T> inputs) {

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
