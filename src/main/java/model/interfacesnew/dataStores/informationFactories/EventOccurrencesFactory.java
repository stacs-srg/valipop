package model.interfacesnew.dataStores.informationFactories;


import model.occurrencesInformation.EventOccurrences;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface EventOccurrencesFactory extends SetEventOccurrences {

    /**
     * Creates a EventOccurrences object.
     *
     * @return the event occurrences
     */
    EventOccurrences createEventOccurances();

}
