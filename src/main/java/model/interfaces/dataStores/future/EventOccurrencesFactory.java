package model.interfaces.dataStores.future;


import model.enums.EventType;
import model.interfaces.dataStores.informationPassing.tableTypes.Table;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface EventOccurrencesFactory {

    /**
     * Creates a EventOccurrences object.
     *
     * @return the event occurrences
     */
    EventOccurrences createEventOccurances();

    /**
     * Inserts the given NumberTable into the data store for the specified variable for the given year.
     *
     * @param year     the given year
     * @param variable the specified variable
     * @param table    the NumberTable
     */
    void setData(int year, EventType variable, Table table);

}
