package model.implementation.model;

import model.implementation.analysis.PopulationComposition;
import model.implementation.analysis.statistics.ComparativeAnalysis;
import model.implementation.analysis.GeneratedPopulationCompositionFactory;
import model.implementation.config.Config;
import model.implementation.populationStatistics.DesiredPopulationStatisticsFactory;
import model.implementation.populationStatistics.PopulationStatistics;
import model.interfaces.populationModel.Population;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Simulation {

    public static Logger log = LogManager.getLogger(Simulation.class);

    private final static Path PATH_TO_CONFIG_FILE = Paths.get("/Users/tsd4/OneDrive/cs/PhD/population_model/src/main/resources/config/config.txt");

    PopulationStatistics desired;
    private final static Config config = new Config(PATH_TO_CONFIG_FILE);


    public static void main(String[] args) {

        Logger log = LogManager.getLogger("main");

        log.info("Program begins");

        Simulation sim = new Simulation();

        // run model
        Population population = sim.makeSimulatedPopulation();

        // perform comparisons
        ComparativeAnalysis comparisonOfDesiredAndGenerated = sim.analyseGeneratedPopulation(population);

        // Check for statistical significant similarity between desired and generated population
        if(comparisonOfDesiredAndGenerated.passed()) {
            System.out.println("Generated population similarity to desired population is statistically significant");
        }


    }

    public Simulation() {
        // get desired population info
        desired = setUpSimData();
    }

    public ComparativeAnalysis analyseGeneratedPopulation(Population generatedPopulation) {
        // get comparable statistics for generate population
        PopulationComposition generatedPopulationComposition = GeneratedPopulationCompositionFactory.createGeneratedPopulationComposition(generatedPopulation);

        // compare desired and generated population
        ComparativeAnalysis comparisonOfDesiredAndGenerated = new ComparativeAnalysis(desired, generatedPopulationComposition);

        return comparisonOfDesiredAndGenerated;

    }

    private static PopulationStatistics setUpSimData() {

        PopulationStatistics desiredStatistics = DesiredPopulationStatisticsFactory.initialisePopulationStatistics(config);

        // interpolate
        // for each data type
        //      smooth value changes in gaps between years for which data is given
        // end for

        // calculate desired birth rate to achieve seed population at Time 0, to do this:
        // for each population growth rates before Time 0 working backwards to Time start
            // apply the compound negative of the growth rate since the previous growth rate to the seed population desired size
            // GROWTH_RATES = intended growth rates for times before Time 0
        // end for

        // store the calculated start population as PRESENT_POPULATION_COUNT

        return null;

    }


    private Population makeSimulatedPopulation() {

        // INFO: at this point all the desired population statistics have been made available

        // initialise population structures

        // start time progression
        // for each time step from T Start to T End

            // at every min timestep
            // clear out dead people

            // if deaths timestep
                handleDeaths();

            // if births timestep
                handleBirths();

        // end for

        return null;
    }

    private void handleDeaths() {

        // handle deaths for the next year
        // for each age
            // DATA - get rate of death by age and gender
            // get count of people of given age
            // use data to calculate who to kill off

            // for each to be killed
                // execute death at a time in the next year
            // end for

        // end for

    }

    private void handleBirths() {

        // create set of MOTHERS_NEEDING_FATHERS
        // create set of MOTHERS_WITH_FATHERS
        // create set of NEW_FATHERS

        // make children/decide on mothers
        // for each age of mothers of childbearing age (AGE OF MOTHER)
            // for each number of children already birthed to mothers (BIRTH ORDER)
                // DATA 1 - get rate of births by mothers age and birth order
                // DATA 2 - get rate of multiple births in a maternity (by order)

                // select mothers to give birth (and which will bear twins, etc.)
                // get count of mothers of this age and birth order
                // use DATA 1 to see how many many children need to be born
                // use DATA 2 to decide how many mothers needed to birth children
                // select the correct number of mothers
                // make and assign the specified number of children - assign to correct place in population

                // if birth order 0
                    // add mothers to MOTHERS_NEEDING_FATHERS
                // else
                    // DATA - get rate of separation by number of children had
                    // select mothers to separate with fathers and add to MOTHERS_NEEDING_FATHERS
                    // add the rest to MOTHERS_WITH_FATHERS

            // end for

            // decide on new fathers
            // NUMBER_OF_FATHERS_NEEDED = MOTHERS_NEEEDING_FATHERS.size()
            // DATA - get age difference of parents at childs birth distribution (this is a subset/row of an ages in combination table)
            // Turn distribution into solid values based on the number of fathers required
            // select fathers and add to NEW_FATHERS

            // pair up MOTHERS_NEEDING_FATHERS with NEW_FATHERS

            // find appropriate birth date for child

            // update new children info to give fathers
            // keep count of children born this quarter as BIRTH_COUNT

            // MAGIC CHILDREN BIT
            // if before end of magic children period
                // calculate quarterly birth target, using:
                // current GROWTH_RATES and PRESENT_POPULATION to calculate yearly population growth and divide by 4
                // then any shortfall in the quarterly BIRTH_COUNT should be made up by:
                // adding Magic Children to the population
            // fi

        // end for

    }


}
