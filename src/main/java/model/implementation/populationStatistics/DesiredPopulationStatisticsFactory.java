package model.implementation.populationStatistics;

import model.enums.EventType;
import model.implementation.config.Config;
import model.interfaces.dataStores.informationPassing.tableTypes.Table;
import model.time.DateClock;
import model.time.YearDate;
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

        Map<YearDate, OneDimensionDataDistribution> maleDeath = readIn1DDataFiles(config.getVarMaleDeathPaths());
        Map<YearDate, OneDimensionDataDistribution> femaleDeath = readIn1DDataFiles(config.getVarFemaleDeathPaths());
        Map<YearDate, TwoDimensionDataDistribution> partnering = readIn2DDataFiles(config.getVarPartneringPaths());
        Map<YearDate, TwoDimensionDataDistribution> orderedBirth = readIn2DDataFiles(config.getVarOrderedBirthPaths());
        Map<YearDate, TwoDimensionDataDistribution> multipleBirth = readIn2DDataFiles(config.getVarMultipleBirthPaths());
        Map<YearDate, OneDimensionDataDistribution> separation = readIn1DDataFiles(config.getVarSeparationPaths());

        return new PopulationStatistics(config, maleDeath, femaleDeath, partnering, orderedBirth, multipleBirth, separation);
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

    private static Map<YearDate, OneDimensionDataDistribution> readIn1DDataFiles(DirectoryStream<Path> paths) {

        Map<YearDate, OneDimensionDataDistribution> data = new HashMap<YearDate, OneDimensionDataDistribution>();

        for (Path path : paths) {
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
