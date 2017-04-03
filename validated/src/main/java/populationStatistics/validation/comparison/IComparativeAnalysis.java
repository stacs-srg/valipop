package populationStatistics.validation.comparison;

import config.Config;
import dateModel.Date;
import dateModel.exceptions.UnsupportedDateConversion;
import dateModel.timeSteps.CompoundTimeUnit;
import events.EventType;
import events.UnsupportedEventType;
import populationStatistics.dataDistributionTables.OneDimensionDataDistribution;
import populationStatistics.validation.exceptions.StatisticalManipulationCalculationError;
import populationStatistics.validation.kaplanMeier.IKaplanMeierAnalysis;
import populationStatistics.validation.summaryData.SummaryRow;
import simulationEntities.population.IPopulation;


import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

/**
 * The IComparativeAnalysis interface provides statistical tests to verify the simulated population against a given
 * population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface IComparativeAnalysis {


    /**
     * Runs Kaplan-Meier analysis, see the provided {@link IKaplanMeierAnalysis} class.
     *
     * @param expectedEvents the expected events
     * @param observedEvents the observed events
     * @return the km analysis
     */
    static IKaplanMeierAnalysis runKaplanMeier(EventType event, CompoundTimeUnit timePeriod, OneDimensionDataDistribution expectedEvents, OneDimensionDataDistribution observedEvents) throws StatisticalManipulationCalculationError {
        return null;
    }

    Map<Date, Map<EventType, IKaplanMeierAnalysis>> getResults();

    SummaryRow outputResults(PrintStream resultOutput, SummaryRow summary) throws UnsupportedDateConversion;

    void runAnalysis(IPopulation generatedPopulation, Config config) throws UnsupportedDateConversion, StatisticalManipulationCalculationError, IOException, UnsupportedEventType;

}
