package model.interfaces.dataStores.informationPassing.tableTypes;

import model.enums.EventType;

/**
 * The Table inteferface provides high level information about the tables in the model.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface Table {

    EventType getEvent();

}
