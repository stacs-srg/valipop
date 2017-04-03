package populationStatistics.validation.analytic;

import simulationEntities.population.IPopulation;

import java.io.PrintStream;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AnalyticsRunner {

    public static void runAnalytics(IPopulation population, PrintStream resultsOutput) {
        new PopulationAnalytics(population, resultsOutput).printAllAnalytics();
        new ChildrenAnalytics(population, resultsOutput).printAllAnalytics();
        new DeathAnalytics(population, resultsOutput).printAllAnalytics();
        new MarriageAnalytics(population, resultsOutput).printAllAnalytics();
    }

}
