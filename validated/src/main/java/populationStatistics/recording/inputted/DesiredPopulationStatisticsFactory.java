package populationStatistics.recording.inputted;

import config.Config;
import dateModel.Date;
import dateModel.DateUtils;
import dateModel.dateImplementations.YearDate;
import dateModel.timeSteps.CompoundTimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import populationStatistics.dataDistributionTables.selfCorrecting.ProportionalDistributionAdapter;
import populationStatistics.dataDistributionTables.selfCorrecting.SelfCorrectingProportionalDistribution;
import populationStatistics.recording.PopulationStatistics;
import populationStatistics.dataDistributionTables.DataDistribution;
import populationStatistics.dataDistributionTables.OneDimensionDataDistribution;
import populationStatistics.dataDistributionTables.TwoDimensionDataDistribution;
import populationStatistics.dataDistributionTables.selfCorrecting.SelfCorrectingOneDimensionDataDistribution;
import populationStatistics.dataDistributionTables.selfCorrecting.SelfCorrectingTwoDimensionDataDistribution;
import utils.fileUtils.InputFileReader;
import utils.fileUtils.InvalidInputFileException;

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
        Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> partnering = readInSC2DDataFiles(config.getVarPartneringPaths(), config);
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

    private static Map<YearDate, TwoDimensionDataDistribution> readIn2DDataFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        Map<YearDate, TwoDimensionDataDistribution> data = new HashMap<>();

        for (Path path : paths) {
            // read in each file
            TwoDimensionDataDistribution tempData = InputFileReader.readIn2DDataFile(path);
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

    private static Map<YearDate, OneDimensionDataDistribution> readIn1DDataFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        Map<YearDate, OneDimensionDataDistribution> data = new HashMap<>();

        for (Path path : paths) {
            // read in each file
            OneDimensionDataDistribution tempData = InputFileReader.readIn1DDataFile(path);
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
