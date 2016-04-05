package model.implementation.analysis.statistics;

import model.enums.EventType;
import model.interfaces.analysis.statistical.KaplanMeierAnalysis;
import model.interfaces.dataStores.informationAccess.StatisticalTables;
import model.interfaces.dataStores.informationPassing.tableTypes.OneWayTable;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ComparativeAnalysis implements model.interfaces.analysis.statistical.ComparativeAnalysis {

    StatisticalTables desired;
    StatisticalTables generated;

    public ComparativeAnalysis(StatisticalTables desired, StatisticalTables generated) {
        this.desired = desired;
        this.generated = generated;
    }

    @Override
    public KaplanMeierAnalysis runKaplanMeier(EventType variable, int year, OneWayTable<Integer> expectedEvents, OneWayTable<Integer> observedEvents) {
        return null;
    }

    @Override
    public boolean passed() {
        return false;
    }

    @Override
    public void runAnalysis() {

    }
}
