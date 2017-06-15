package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.analytic;

import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.IPopulation;

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
