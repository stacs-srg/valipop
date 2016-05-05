package model.implementation.populationStatistics;

import model.enums.EventType;
import model.implementation.config.Config;
import model.interfaces.dataStores.informationPassing.tableTypes.Table;
import model.time.TimeInstant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.InputFileReader;

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
    public static PopulationStatistics initialisePopulationStatistics(Config config) {

        DesiredPopulationStatisticsFactory.log.info("Creating PopulationStatistics");

        Map<TimeInstant, OneDimensionDataDistribution> death = readIn1DDataFiles(config.getVarDeathPaths());
        Map<TimeInstant, TwoDimensionDataDistribution> partnering = readIn2DDataFiles(config.getVarPartneringPaths());
        Map<TimeInstant, TwoDimensionDataDistribution> orderedBirth = readIn2DDataFiles(config.getVarOrderedBirthPaths());
        Map<TimeInstant, TwoDimensionDataDistribution> multipleBirth = readIn2DDataFiles(config.getVarMultipleBirthPaths());
        Map<TimeInstant, OneDimensionDataDistribution> separation = readIn1DDataFiles(config.getVarSeparationPaths());

        return new PopulationStatistics(config, death, partnering, orderedBirth, multipleBirth, separation);
    }

    private static Map<TimeInstant, TwoDimensionDataDistribution> readIn2DDataFiles(DirectoryStream<Path> paths) {

        Map<TimeInstant, TwoDimensionDataDistribution> data = new HashMap<TimeInstant, TwoDimensionDataDistribution>();

        for(Path path : paths) {
            // read in each file
            TwoDimensionDataDistribution tempData = InputFileReader.readIn2DDataFile(path);

            data.put(tempData.getYear(), tempData);

        }
        return data;
    }

    private static Map<TimeInstant, OneDimensionDataDistribution> readIn1DDataFiles(DirectoryStream<Path> paths) {

        Map<TimeInstant, OneDimensionDataDistribution> data = new HashMap<TimeInstant, OneDimensionDataDistribution>();

        for(Path path : paths) {
            // read in each file
            OneDimensionDataDistribution tempData = InputFileReader.readIn1DDataFile(path);

            data.put(tempData.getYear(), tempData);

        }
        return data;
    }

    /**
     * Inserts the given NumberTable into the data store for the specified variable for the given year.
     *
     * @param year     the given year
     * @param variable the specified variable
     * @param table    the NumberTable
     */
    void setData(int year, EventType variable, Table table) {

    }

}
