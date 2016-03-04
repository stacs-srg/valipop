package model.interfaces.analysis.statistical;

import model.enums.EventType;
import model.interfaces.dataStores.informationPassing.tableTypes.OneWayTable;

/**
 * The StatisticalAnalysis interface provides statistical tests to verify the simulated population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface StatisticalAnalysis {

    /**
     * Runs Kaplan-Meier analysis.
     *
     * @param variable       the variable
     * @param year           the year
     * @param expectedEvents the expected events
     * @param observedEvents the observed events
     * @return the km analysis
     */
    KaplanMeierAnalysis runKaplanMeier(EventType variable, int year, OneWayTable<Integer> expectedEvents, OneWayTable<Integer> observedEvents);


}
