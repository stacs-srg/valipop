package validation;

import config.Config;
import datastructure.summativeStatistics.generated.EventType;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import model.IPopulation;
import model.simulationLogic.StatisticalManipulationCalculationError;
import utils.time.Date;
import utils.time.UnsupportedDateConversion;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
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
    IKaplanMeierAnalysis runKaplanMeier(EventType event, OneDimensionDataDistribution expectedEvents, OneDimensionDataDistribution observedEvents) throws StatisticalManipulationCalculationError;


    /**
     * If all comparisons pass then return true, else return fail.
     *
     * @return have all comparative analyses passed
     */
    boolean passed();


    Map<Date, Map<EventType, IKaplanMeierAnalysis>> getResults();

    void outputResults(PrintStream resultOutput) throws UnsupportedDateConversion;


    void runAnalysis(IPopulation generatedPopulation, Config config) throws UnsupportedDateConversion, StatisticalManipulationCalculationError, IOException;

}
