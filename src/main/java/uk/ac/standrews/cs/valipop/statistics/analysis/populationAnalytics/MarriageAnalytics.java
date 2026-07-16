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

import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.IPersonCollection;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.SexOption;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

import static uk.ac.standrews.cs.valipop.statistics.analysis.populationAnalytics.PopulationAnalytics.PERCENTAGE_FORMAT;

/**
 * An analytic class to analyse the distribution of marriages.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
class MarriageAnalytics {

    private final IPersonCollection population;

    private final PrintStream out;

    MarriageAnalytics(final IPersonCollection population, final PrintStream resultsOutput) {

        this.population = population;
        out = resultsOutput;
    }

    void printAllAnalytics() {

        out.println();
        out.println("Male marriage count distribution:");
        out.println();

        printMarriageCountDistribution();
    }

    private void printMarriageCountDistribution() {

        final Map<Integer, Integer> marriageCounts = new TreeMap<>();

        for (final IPerson person : population.getPeople()) {

            if (person.getSex() == SexOption.MALE) { // only look at Males to avoid counting marriages twice.

                final int marriageCount = person.getPartnerships().size();
                marriageCounts.put(marriageCount, marriageCounts.getOrDefault(marriageCount, 0) + 1);
            }
        }

        final int sum = marriageCounts.values().stream().reduce(Integer::sum).orElseThrow();

        for (final Map.Entry<Integer, Integer> entry : marriageCounts.entrySet())
            out.println("    " + entry.getKey() + ", " + PERCENTAGE_FORMAT.format(entry.getValue() / (double) sum));
    }
}
