/*
 * valipop - <https://github.com/stacs-srg/valipop>
 * Copyright © 2026 Systems Research Group, University of St Andrews (graham.kirby@st-andrews.ac.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop.statistics.analysis.populationAnalytics;


import uk.ac.standrews.cs.valipop.simulationEntities.IPersonCollection;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.SexOption;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.stream.Stream;

/**
 * An analytic class to analyse the entire population.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PopulationAnalytics {

    public static final NumberFormat PERCENTAGE_FORMAT = NumberFormat.getPercentInstance();

    static {
        PERCENTAGE_FORMAT.setMinimumIntegerDigits(2);
        PERCENTAGE_FORMAT.setMinimumFractionDigits(3);
        PERCENTAGE_FORMAT.setMaximumFractionDigits(3);
    }

    private final IPersonCollection population;
    private final PrintStream out;

    /**
     * Creates an analytic instance to analyse the entire population.
     *
     * @param population the population to analyse
     */
    public PopulationAnalytics(final IPersonCollection population, final PrintStream resultsOutput) {

        this.population = population;
        out = resultsOutput;
    }

    /**
     * Prints out all analyses.
     */
    public void printAllAnalytics() {

        final int size = population.getNumberOfPeople();

        out.println("Population: " + size);
        out.println();

        out.println("    Female, " + PERCENTAGE_FORMAT.format(count(SexOption.FEMALE) / (double) size));
        out.println("    Male,   " + PERCENTAGE_FORMAT.format(count(SexOption.MALE) / (double) size));
        out.println();
    }

    private int count(final SexOption sex) {

        return (int) Stream.generate(population.getPeople().iterator()::next).
            limit(population.getNumberOfPeople()).
            filter(person -> person.getSex() == sex).
            count();
    }
}
