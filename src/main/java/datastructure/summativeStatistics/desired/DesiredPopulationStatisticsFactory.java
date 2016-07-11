package datastructure.summativeStatistics.desired;

import config.Config;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import datastructure.summativeStatistics.structure.SelfCorrectingOneDimensionDataDistribution;
import datastructure.summativeStatistics.structure.SelfCorrectingTwoDimensionDataDistribution;
import datastructure.summativeStatistics.structure.TwoDimensionDataDistribution;
import utils.time.YearDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.InputFileReader;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
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
    public static PopulationStatistics initialisePopulationStatistics(Config config) throws IOException {

        DesiredPopulationStatisticsFactory.log.info("Creating PopulationStatistics instance");

        Map<YearDate, SelfCorrectingOneDimensionDataDistribution> maleDeath = readInSC1DDataFiles(config.getVarMaleDeathPaths());
        Map<YearDate, SelfCorrectingOneDimensionDataDistribution> femaleDeath = readInSC1DDataFiles(config.getVarFemaleDeathPaths());
        Map<YearDate, TwoDimensionDataDistribution> partnering = readIn2DDataFiles(config.getVarPartneringPaths());
        Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> orderedBirth = readInSC2DDataFiles(config.getVarOrderedBirthPaths());
        Map<YearDate, TwoDimensionDataDistribution> multipleBirth = readIn2DDataFiles(config.getVarMultipleBirthPaths());
        Map<YearDate, OneDimensionDataDistribution> separation = readIn1DDataFiles(config.getVarSeparationPaths());

        return new PopulationStatistics(config, maleDeath, femaleDeath, partnering, orderedBirth, multipleBirth, separation);
    }

    private static Map<YearDate,SelfCorrectingOneDimensionDataDistribution> readInSC1DDataFiles(DirectoryStream<Path> paths) {

        Map<YearDate, SelfCorrectingOneDimensionDataDistribution> data = new HashMap<YearDate, SelfCorrectingOneDimensionDataDistribution>();

        for (Path path : paths) {
            // read in each file
            SelfCorrectingOneDimensionDataDistribution tempData = InputFileReader.readInSC1DDataFile(path);
            data.put(tempData.getYear(), tempData);
        }
        return data;

    }

    private static Map<YearDate, TwoDimensionDataDistribution> readIn2DDataFiles(DirectoryStream<Path> paths) {

        Map<YearDate, TwoDimensionDataDistribution> data = new HashMap<YearDate, TwoDimensionDataDistribution>();

        for (Path path : paths) {
            // read in each file
            TwoDimensionDataDistribution tempData = InputFileReader.readIn2DDataFile(path);
            data.put(tempData.getYear(), tempData);
        }
        return data;
    }

    private static Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> readInSC2DDataFiles(DirectoryStream<Path> paths) {

        Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> data = new HashMap<YearDate, SelfCorrectingTwoDimensionDataDistribution>();

        for (Path path : paths) {
            // read in each file
            SelfCorrectingTwoDimensionDataDistribution tempData = InputFileReader.readInSC2DDataFile(path);
            data.put(tempData.getYear(), tempData);
        }
        return data;
    }

    private static Map<YearDate, OneDimensionDataDistribution> readIn1DDataFiles(DirectoryStream<Path> paths) {

        Map<YearDate, OneDimensionDataDistribution> data = new HashMap<YearDate, OneDimensionDataDistribution>();

        for (Path path : paths) {
            // read in each file
            OneDimensionDataDistribution tempData = InputFileReader.readIn1DDataFile(path);
            data.put(tempData.getYear(), tempData);
        }
        return data;
    }
}
