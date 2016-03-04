package model.interfacesnew.analysis.statistical;

import model.enums.VariableType;
import model.interfacesnew.dataStores.informationFlow.result.returnTable.NumberTable;

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
    KaplanMeierAnalysis runKaplanMeier(VariableType variable, int year, NumberTable expectedEvents, NumberTable observedEvents);


}
