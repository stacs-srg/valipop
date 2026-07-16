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
import java.util.*;
import java.util.stream.Stream;

import static uk.ac.standrews.cs.valipop.statistics.analysis.populationAnalytics.PopulationAnalytics.PERCENTAGE_FORMAT;

/**
 * An analytic class to analyse the distribution of children.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Tom Dalton
 */

class ChildrenAnalytics {

    static final int MINIMUM_CHILD_BEARING_AGE = 15;
    static final int MAXIMUM_CHILD_BEARING_AGE = 50;

    private final IPersonCollection population;
    private final PrintStream out;

    ChildrenAnalytics(final IPersonCollection population, final PrintStream resultsOutput) {

        this.population = population;
        out = resultsOutput;
    }

    void printAllAnalytics() {

        out.println("Family size distribution:");
        out.println();

        printFamilySizes();

        out.println();
        out.println("Fertility rate distribution:");
        out.println();

        printFertilityRates();

        out.println();
    }

    private void printFamilySizes() {

        final Map<Integer, Integer> familySizeCounts = new TreeMap<>();

        for (final IPerson person : population.getPeople())
            if (person.getSex() == SexOption.FEMALE)

                for (final IPartnership partnership : person.getPartnerships()) {

                    final List<IPerson> child_ids = partnership.getChildren();
                    familySizeCounts.put(child_ids.size(), familySizeCounts.getOrDefault(child_ids.size(), 0) + 1);
                }

        final int sum = familySizeCounts.values().stream().reduce(Integer::sum).orElseThrow();

        for (final Map.Entry<Integer, Integer> entry : familySizeCounts.entrySet())
            out.println("    " + entry.getKey() + ", " + PERCENTAGE_FORMAT.format(entry.getValue() / (double) sum));
    }

    private void printFertilityRates() {

        final Map<Integer, Integer> femalesOfChildBearingAgeByYear = new HashMap<>();
        final Map<Integer, Integer> birthsByYear = new HashMap<>();
        final Map<Integer, Double> fertilityRateByYear = new TreeMap<>();

        for (final IPerson person : population.getPeople()) {

            if (person.getSex() == SexOption.FEMALE) {

                for (final IPartnership partnership : person.getPartnerships())
                    for (final IPerson child : partnership.getChildren()) {

                        final int year = child.getBirthDate().getYear();
                        birthsByYear.put(year, birthsByYear.getOrDefault(year, 0) + 1);
                    }

                final int motherYearOfBirth = person.getBirthDate().getYear();

                for (int year = motherYearOfBirth + MINIMUM_CHILD_BEARING_AGE; year < motherYearOfBirth + MAXIMUM_CHILD_BEARING_AGE; year++)
                    femalesOfChildBearingAgeByYear.put(year, femalesOfChildBearingAgeByYear.getOrDefault(year, 0) + 1);
            }
        }

        final int earliestYear = combineKeys(femalesOfChildBearingAgeByYear, birthsByYear).
            min(Integer::compareTo).
            orElseThrow();

        final int latestYear = combineKeys(femalesOfChildBearingAgeByYear, birthsByYear).
            max(Integer::compareTo).
            orElseThrow();

        for (int year = earliestYear; year <= latestYear; year++) {

            final int births = birthsByYear.getOrDefault(year, 0);
            final int femalesOfChildBearingAge = femalesOfChildBearingAgeByYear.getOrDefault(year, 0);

            final double fertilityRate = femalesOfChildBearingAge == 0 ? 0 : births / (double) femalesOfChildBearingAge;

            fertilityRateByYear.put(year, fertilityRate);
        }

        for (final Map.Entry<Integer, Double> entry : fertilityRateByYear.entrySet())
            out.println("    " + entry.getKey() + ", " + PERCENTAGE_FORMAT.format(entry.getValue()));
    }

    private static Stream<Integer> combineKeys(final Map<Integer, Integer> map1, final Map<Integer, Integer> map2) {
        return Stream.concat(map1.keySet().stream(), map2.keySet().stream());
    }
}
