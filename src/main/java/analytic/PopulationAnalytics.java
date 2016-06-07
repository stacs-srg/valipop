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
package analytic;

import model.IPartnership;
import model.IPerson;
import model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;
import utils.time.Date;

import java.util.List;

/**
 * An analytic class to analyse the entire population.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PopulationAnalytics {

    private final IPopulation population;
    private static final int ONE_HUNDRED = 100;

    /**
     * Creates an analytic instance to analyse the entire population.
     *
     * @param population the population to analyse
     */
    public PopulationAnalytics(final IPopulation population) {

        this.population = population;
    }

    /**
     * Prints out all analyses.
     *
     * @throws Exception if the population size cannot be accessed
     */
    public void printAllAnalytics() throws Exception {

        final int size = population.getNumberOfPeople();
        final int number_males = countMales();
        final int number_females = countFemales();

        System.out.println("Population size = " + size);
        System.out.println("Number of males = " + number_males + " = " + String.format("%.1f", number_males / (double) size * ONE_HUNDRED) + '%');
        System.out.println("Number of females = " + number_females + " = " + String.format("%.1f", number_females / (double) size * ONE_HUNDRED) + '%');

//        printAllBirthDates();
//        printAllDeathDates();
//        printAllDates();
    }

    private int countMales()  {

        int count = 0;
        for (final IPerson person : population.getPeople()) {
            if (person.getSex() == IPerson.MALE) {
                count++;
            }
        }
        return count;
    }

    private int countFemales()  {

        int count = 0;
        for (final IPerson person : population.getPeople()) {
            if (person.getSex() == IPerson.FEMALE) {
                count++;
            }
        }
        return count;
    }

    /**
     * Prints the dates of birth of all people.
     */
    public void printAllBirthDates()  {

        for (final IPerson person : population.getPeople()) {
            printBirthDate(person);
            System.out.println();
        }
    }

    private static void printBirthDate(final IPerson person) {

        System.out.print(person.getBirthDate().toString());
    }

    /**
     * Prints the dates of death of all people.
     */
    public void printAllDeathDates() {

        for (final IPerson person : population.getPeople()) {
            printDeathDate(person);
            System.out.println();
        }
    }

    private static void printDeathDate(final IPerson person) {

        final Date death_date = person.getDeathDate();
        if (death_date != null) {
            System.out.print(death_date.toString());
        }
    }

    /**
     * Prints the dates of birth of child_ids in a partnership.
     *
     * @param partnership the partnership
     */
    public void printChildren(final IPartnership partnership) {

        if (partnership.getChildren() != null) {
            for (final IPerson child : partnership.getChildren()) {

                System.out.println("\t\tChild born: " + child.getBirthDate().toString());
            }
        }
    }

    /**
     * Prints the details of partnerships and child_ids for a given person.
     * @param person the person
     */
    @SuppressWarnings("FeatureEnvy")
    public void printPartnerships(final IPerson person)  {

        final List<IPartnership> partnership_ids = person.getPartnerships();
        if (partnership_ids != null) {
            for (final IPartnership partnership : partnership_ids) {


                final IPerson partner = partnership.getPartnerOf(person);
                System.out.println("\tPartner born: " + partner.getBirthDate().toString());

                final Date marriage_date = partnership.getPartnershipDate();
                if (marriage_date != null) {
                    System.out.println("\tMarriage on " + marriage_date.toString());
                }

                printChildren(partnership);
            }
        }
    }

    /**
     * Prints all significant dates for the population.
     */
    public void printAllDates()  {

        for (final IPerson person : population.getPeople()) {

            System.out.print(person.getSex() + " Born: ");
            printBirthDate(person);
            System.out.print(", Died: ");
            printDeathDate(person);
            System.out.println();
            printPartnerships(person);
        }
    }
}
