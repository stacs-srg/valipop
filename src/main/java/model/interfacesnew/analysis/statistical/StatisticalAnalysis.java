package model.interfacesnew.analysis.statistical;

import model.enums.VariableType;
import model.interfacesnew.dataStores.values.NumberTable;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface StatisticalAnalysis {

    KMAnalysis runKM(VariableType variable, int year, NumberTable expectedEvents, NumberTable observedEvents);

    NumberTable deriveExpectedEventsTable();

    NumberTable deriveObservedEventsTable();

}
