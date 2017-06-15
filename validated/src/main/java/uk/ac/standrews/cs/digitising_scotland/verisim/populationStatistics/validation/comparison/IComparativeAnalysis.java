package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.comparison;

import uk.ac.standrews.cs.digitising_scotland.verisim.config.Config;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.exceptions.UnsupportedDateConversion;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.EventType;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.UnsupportedEventType;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.OneDimensionDataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.exceptions.StatisticalManipulationCalculationError;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.kaplanMeier.IKaplanMeierAnalysis;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.summaryData.SummaryRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.IPopulation;


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
