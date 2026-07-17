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

import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.IPersonCollection;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.Period;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

import static uk.ac.standrews.cs.valipop.statistics.analysis.populationAnalytics.PopulationAnalytics.PERCENTAGE_FORMAT;

/**
 * An analytic class to analyse the distribution of deaths.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
class DeathAnalytics {

    private final IPersonCollection population;
    private final PrintStream out;

    DeathAnalytics(final IPersonCollection population, final PrintStream resultsOutput) {

        this.population = population;
        out = resultsOutput;
    }

    void printAllAnalytics() {

        out.println("Death age distribution:");
        out.println();

        printDeathAgeDistribution();
    }

    private void printDeathAgeDistribution() {

        final Map<Integer, Integer> agesAtDeath = new TreeMap<>();

        for (final IPerson person : population.getPeople()) {

            final LocalDate deathDate = person.getDeathDate();

            if (deathDate != null) {

                final int ageAtDeath = Period.between(person.getBirthDate(), deathDate).getYears();
                agesAtDeath.put(ageAtDeath, agesAtDeath.getOrDefault(ageAtDeath, 0) + 1);
            }
        }

        if (!agesAtDeath.isEmpty()) {

            final int sum = agesAtDeath.values().stream().reduce(Integer::sum).orElseThrow();

            for (final Map.Entry<Integer, Integer> entry : agesAtDeath.entrySet())
                out.println("    " + String.format("%3s", entry.getKey()) + ", " + PERCENTAGE_FORMAT.format(entry.getValue() / (double) sum));
        }
    }
}
