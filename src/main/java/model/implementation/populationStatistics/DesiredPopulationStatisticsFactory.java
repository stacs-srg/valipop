package model.implementation.populationStatistics;

import model.enums.EventType;
import model.implementation.config.Config;
import model.interfaces.dataStores.informationPassing.tableTypes.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.InputFileReader;

import java.nio.file.Path;

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
    public static PopulationStatistics intialisePopulationStatistics(Config config) {

        DesiredPopulationStatisticsFactory.log.info("Creating PopulationStatistics");

        // if config to use saved data
            // load in saved data

        // else
            readInDataFiles(config);

        return null;
    }

    private static void readInDataFiles(Config config) {

        for(Path path : config.getVarBirthFiles()) {

            // read in each file

            InputFileReader.getAllLines(path);

            // for each year in the simulation
                // create an EventRateTable and place in the PopulationStatistics
            // end for


        }

        // repeat for each data type



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
