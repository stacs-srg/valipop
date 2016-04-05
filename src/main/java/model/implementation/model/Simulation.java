package model.implementation.model;

import model.implementation.analysis.statistics.ComparativeAnalysis;
import model.implementation.analysis.GeneratedPopulationComposition;
import model.implementation.analysis.GeneratedPopulationCompositionFactory;
import model.implementation.config.Config;
import model.implementation.occurrencesInformation.DesiredPopulationComposition;
import model.interfaces.populationModel.Population;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Simulation {

    private final static String PATH_TO_CONFIG_FILE = "config/config.txt";

    DesiredPopulationComposition desired;
    private final static Config config = new Config(PATH_TO_CONFIG_FILE);


    public static void main(String[] args) {

        Simulation sim = new Simulation();

        // get desired population info
        sim.desired = setUpSimData();

        // run model
        Population population = sim.runModel();

        // get comparable statistics for generate population
        GeneratedPopulationComposition generated = GeneratedPopulationCompositionFactory.createGeneratedPopulationComposition(population);

        // compare desired and generated population
        ComparativeAnalysis compare = new ComparativeAnalysis(sim.desired, generated);
        compare.runAnalysis();

        // Check for statistical significant similarity between desired and generated population
        if(compare.passed()) {
            System.out.println("Generated population similarity to desired population is statistically significant");
        }


    }

    private static DesiredPopulationComposition setUpSimData() {

        // if config to use saved data
            // load in saved data

        // else
            // look in config to find location of data to be used
            // for each input data type defined
                // for each year in the simulation
                    // create an EventRateTable and place in the DesiredPopulationComposition
                // end for
            // end for

            // for each data type
                // smooth value changes in gaps between years for which data is given
            // end for

        // end else

        // calculate desired birth rate to achieve seed population at Time 0, to do this:
        // for each population growth rates before Time 0 working backwards to Time start
            // apply the compound negative of the growth rate since the previous growth rate to the seed population desired size
            // GROWTH_RATES = intended growth rates for times before Time 0
        // end for

        // store the calculated start population as PRESENT_POPULATION_COUNT

        return null;

    }


    private Population runModel() {

        // INFO: at this point all the desired population statistics have been made available

        // initialise population structures

        // start time progression
        // for each time step from T Start to T End

            // if year end
                handleYearEvents();
            // fi

            // if quarter end
                handleQuarterlyEvents();
            // fi

        // end for

        return null;
    }

    private void handleQuarterlyEvents() {

        // clear out dead people for the quarter

        handleBirths();

    }

    private void handleYearEvents() {
        handleDeaths();
    }

    private void handleDeaths() {

        // handle deaths for the next year
        // for each age
            // DATA - get rate of death by age and gender
            // execute deaths
        // end for

    }

    private void handleBirths() {

        // create set of MOTHERS_NEEDING_FATHERS
        // create set of MOTHERS_WITH_FATHERS
        // create set of NEW_FATHERS

        // make children/decide on mothers
        // for each age of mothers of childbearing age
            // for each number of children already birthed to mothers
                // DATA - get rate of births by mothers age and birth order
                // DATA - get rate of multiple births in a maternity (by order)
                // select mothers to give birth (and which will bear twins, etc.)

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
            // make new children - add to population
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
