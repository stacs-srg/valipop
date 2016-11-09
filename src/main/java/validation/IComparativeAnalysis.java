package validation;

import config.Config;
import datastructure.summativeStatistics.generated.EventType;
import datastructure.summativeStatistics.generated.UnsupportedEventType;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import model.simulationEntities.IPopulation;
import validation.utils.StatisticalManipulationCalculationError;
import utils.time.CompoundTimeUnit;
import utils.time.Date;
import utils.time.UnsupportedDateConversion;

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
