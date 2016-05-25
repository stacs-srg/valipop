package validation;

import datastructure.summativeStatistics.generated.EventType;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import datastructure.summativeStatistics.generated.StatisticalTables;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ComparativeAnalysis implements IComparativeAnalysis {

    StatisticalTables desired;
    StatisticalTables generated;

    public ComparativeAnalysis(StatisticalTables desired, StatisticalTables generated) {
        this.desired = desired;
        this.generated = generated;
    }

    @Override
    public IKaplanMeierAnalysis runKaplanMeier(EventType variable, int year, OneDimensionDataDistribution expectedEvents, OneDimensionDataDistribution observedEvents) {
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
