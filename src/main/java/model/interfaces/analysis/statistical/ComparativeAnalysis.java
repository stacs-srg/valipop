package model.interfaces.analysis.statistical;

import model.enums.EventType;
import model.interfaces.dataStores.informationPassing.tableTypes.OneWayTable;

/**
 * The ComparativeAnalysis interface provides statistical tests to verify the simulated population against a given
 * population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface ComparativeAnalysis {



    /**
     * Runs Kaplan-Meier analysis, see the provided {@link KaplanMeierAnalysis} class.
     *
     * @param variable       the variable
     * @param year           the year
     * @param expectedEvents the expected events
     * @param observedEvents the observed events
     * @return the km analysis
     */
    KaplanMeierAnalysis runKaplanMeier(EventType variable, int year, OneWayTable<Integer> expectedEvents, OneWayTable<Integer> observedEvents);


    /**
     * If all comparisons pass then return true, else return fail.
     *
     * @return have all comparative analyses passed
     */
    boolean passed();


    void runAnalysis();

}
