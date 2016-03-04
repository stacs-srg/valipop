package model.interfaces.dataStores.future;


import model.interfaces.dataStores.informationFactories.SetEventOccurrences;

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
