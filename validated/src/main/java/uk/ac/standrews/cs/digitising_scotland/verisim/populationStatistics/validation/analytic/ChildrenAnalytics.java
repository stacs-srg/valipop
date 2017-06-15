/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
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
package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.analytic;

import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.util.ArrayManipulation;

import java.io.PrintStream;
import java.util.*;

/**
 * An analytic class to analyse the distribution of child_ids.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Tom Dalton
 */

public class ChildrenAnalytics {

    private static final int MAX_CHILDREN = 100;
    private static final int ONE_HUNDRED = 100;

    private final int[] children_per_marriage = new int[MAX_CHILDREN]; // tracks family size
    private final IPopulation population;
    private static PrintStream out;

    private final Map<Integer, Double> fertilityRateByYear = new TreeMap<>();

    /**
     * Creates an analytic instance to analyse child_ids in a population.
     *
     * @param population the population to analyse
     * @throws Exception if the analysis cannot be completed
     */
    public ChildrenAnalytics(final IPopulation population, PrintStream resultsOutput) {

        this.population = population;
        out = resultsOutput;
        analyseChildren();
        calculateTFRByYear();
    }

    /**
     * Prints out all analyses.
     */
    public void printAllAnalytics() {

        final int sum = ArrayManipulation.sum(children_per_marriage);

        out.println("Children per marriage sizes:");
        for (int i = 0; i < children_per_marriage.length; i++) {
            if (children_per_marriage[i] != 0) {
                out.println("\t" + children_per_marriage[i] + " Marriages with " + i + " child_ids" + " = " + String.format("%.1f", children_per_marriage[i] / (double) sum * ONE_HUNDRED) + '%');
            }
        }

        out.println("Fertility rates by year:");
        for(Integer year : fertilityRateByYear.keySet()) {
            if(!fertilityRateByYear.get(year).equals(0.0)) {
                out.println("\t" + year + " Fertility rate = " + fertilityRateByYear.get(year));
            }
        }


    }

    /**
     * Analyses child_ids of marriages for the population.
     *
     * @throws Exception if the analysis cannot be completed
     */
    public void analyseChildren() {

        for (final IPerson person : population.getPeople()) {

            if(Character.toLowerCase(person.getSex()) == 'f') {
                final List<IPartnership> partnerships = person.getPartnerships();
                if (partnerships != null) {

                    for (final IPartnership partnership : partnerships) {

                        final List<IPerson> child_ids = partnership.getChildren();

                        if (child_ids != null) {
                            children_per_marriage[child_ids.size()]++;
                        }
                    }
                }
            }
        }
    }

    public void calculateTFRByYear() {

        Map<Integer, Integer> livingFemalesOfSBAgeInEachYear = new HashMap<>();
        Map<Integer, Integer> childrenBornInEachYear = new HashMap<>();

        final int MIN_CB_AGE = 15;
        final int MAX_CB_AGE = 50;

        for (final IPerson person : population.getPeople()) {

            if(Character.toLowerCase(person.getSex()) == 'f') {
                final List<IPartnership> partnerships = person.getPartnerships();
                if (partnerships != null) {

                    for (final IPartnership partnership : partnerships) {

                        final List<IPerson> child_ids = partnership.getChildren();

                        if (child_ids != null) {

                            for(final IPerson child : child_ids) {

                                int yob = child.getBirthDate().getYear();

                                try {
                                    childrenBornInEachYear.put(yob, childrenBornInEachYear.get(yob) + 1);
                                } catch(NullPointerException e) {
                                    childrenBornInEachYear.put(yob, 1);
                                }
                            }

                        }

                    }

                }

                int femalesYOB = person.getBirthDate().getYear();
                for(int y = femalesYOB + MIN_CB_AGE; y < femalesYOB + MAX_CB_AGE; y++) {

                    try {
                        livingFemalesOfSBAgeInEachYear.put(y, livingFemalesOfSBAgeInEachYear.get(y) + 1);
                    } catch(NullPointerException e) {
                        livingFemalesOfSBAgeInEachYear.put(y, 1);
                    }

                }

            }
        }

        Integer earliestYear = getSmallestValueInSets(livingFemalesOfSBAgeInEachYear.keySet(), childrenBornInEachYear.keySet());
        Integer latestYear = getLargestValueInSets(livingFemalesOfSBAgeInEachYear.keySet(), childrenBornInEachYear.keySet());

        if(earliestYear == null || latestYear == null) {
            return;
        }

        for(int y = earliestYear; y < latestYear; y ++) {
            int births;
            int femalesOfCBAge;

            try {
                births = childrenBornInEachYear.get(y);
            } catch (NullPointerException e) {
                births = 0;
            }

            try {
                femalesOfCBAge = livingFemalesOfSBAgeInEachYear.get(y);
            } catch (NullPointerException e) {
                femalesOfCBAge = 0;
            }

            double asfrForYear;
            if(femalesOfCBAge == 0) {
                asfrForYear = 0;
            } else {
                asfrForYear = births / (double) femalesOfCBAge;
            }

            fertilityRateByYear.put(y, asfrForYear);
        }

    }

    public static Integer getSmallestValueInSets(Set<Integer> a, Set<Integer> b) {

        ArrayList<Integer> sets = new ArrayList<>(a);
        sets.addAll(b);

        Collections.sort(sets);

        if(sets.size() == 0) {
            return null;
        } else {
            return sets.get(0);
        }

    }

    public static Integer getLargestValueInSets(Set<Integer> a, Set<Integer> b) {

        ArrayList<Integer> sets = new ArrayList<>(a);
        sets.addAll(b);

        Collections.sort(sets);

        if(sets.size() == 0) {
            return null;
        } else {
            return sets.get(sets.size() - 1);
        }

    }
}
