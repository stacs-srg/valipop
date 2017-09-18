/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.populationAnalytics;

import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.IPopulationExtended;
import uk.ac.standrews.cs.digitising_scotland.util.ArrayManipulation;

import java.io.PrintStream;


/**
 * An analytic class to analyse the distribution of deaths.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
public class DeathAnalytics {

    private static final int MAX_AGE_AT_DEATH = 110;
    private static final int ONE_HUNDRED = 100;

    private final int[] age_at_death = new int[MAX_AGE_AT_DEATH]; // tracks age of death over population
    private final IPopulationExtended population;

    private static PrintStream out;

    /**
     * Creates an analytic instance to analyse deaths in a population.
     *
     * @param population the population to analyse
     */
    public DeathAnalytics(final IPopulationExtended population, PrintStream resultsOutput) {

        this.population = population;
        out = resultsOutput;
        analyseDeaths();

    }

    /**
     * Prints out all analyses.
     */
    public void printAllAnalytics() {

        final int sum = ArrayManipulation.sum(age_at_death);

        out.println("Death distribution:");
        for (int i = 1; i < age_at_death.length; i++) {
            out.println("\tDeaths at age: " + i + " = " + age_at_death[i] + " = " + String.format("%.3f", age_at_death[i] / (double) sum * ONE_HUNDRED) + '%');
        }
    }

    /**
     * Analyses deaths in the population.
     */
    public void analyseDeaths() {

        for (final IPersonExtended person : population.getPeople_ex()) {

            final Date death_date = person.getDeathDate_ex();

            if (death_date != null) {

                final Date birth_date = person.getBirthDate_ex();
                final int age_at_death_in_years = DateUtils.differenceInYears(birth_date, death_date).getCount();
                if (age_at_death_in_years >= 0 && age_at_death_in_years < age_at_death.length) {
                    age_at_death[age_at_death_in_years]++;
                }
            }
        }
    }
}
