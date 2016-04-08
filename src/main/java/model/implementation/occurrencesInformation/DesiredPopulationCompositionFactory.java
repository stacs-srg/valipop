package model.implementation.occurrencesInformation;

import model.enums.EventType;
import model.implementation.config.Config;
import model.implementation.occurrencesInformation.DesiredPopulationComposition;
import model.interfaces.dataStores.informationPassing.tableTypes.Table;
import utils.InputFileReader;

import java.io.File;

/**
 * This factory class handles the correct construction of a DesiredPopulationComposition object.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public abstract class DesiredPopulationCompositionFactory {


    /**
     * Creates a DesiredPopulationComposition object.
     *
     * @return the quantified event occurrences
     */
    public static DesiredPopulationComposition createQuantifiedEventOccurrences(Config config) {

        // if config to use saved data
            // load in saved data

        // else
            // look in config to find location of data to be used

            // for each input data type defined

                for(File f : config.getVarBirthFiles()) {
                    String[] lines = InputFileReader.getAllLines(f.toString());

                }

                // for each year in the simulation
                    // create an EventRateTable and place in the DesiredPopulationComposition
                // end for
            // end for

            // for each data type
                // smooth value changes in gaps between years for which data is given
            // end for

        // end else

        return null;
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
