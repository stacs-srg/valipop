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
package oldModel;


import oldModel.statistics.distributions.UniformIntegerDistribution;
import oldModel.statistics.distributions.ITemporalPopulationInfo;
import oldModel.statistics.distributions.TemporalIntegerDistribution;
import oldModel.statistics.lifetable.LifeTableCatalogue;
import oldModel.utils.DateManipulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by tsd4 on 10/11/2015.
 */
public class Population implements ITemporalPopulationInfo {

    private static final int EPOCH_YEAR = 1600;
    private static final int DAYS_PER_YEAR = 365;
    private static final int SEX_RATIO = 100;
    private static final int START_YEAR = 1601;
    private static final int END_YEAR = 1702;
    /**
     * The Life tables.
     */
    LifeTableCatalogue lifeTables;
    private List<Person> people = new ArrayList<Person>();
    private List<Person> deadPeople = new ArrayList<Person>();
    private int startYearInDays = DateManipulation.dateToDays(START_YEAR, 0, 0);
    private int endYearInDays = DateManipulation.dateToDays(END_YEAR, 0, 0);

    private int currentDay = startYearInDays;

    private int timeStep = 73;


    private Random random = new Random();

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        Population pop = new Population();
        pop.initLifeTables();
        pop.generateSeedPopulation(100000);
        pop.runSimulation();
        System.out.println(pop.people.size());

//        LifeTableCatalogueShadow ltcs = new LifeTableCatalogueShadow(pop.lifeTables, START_YEAR, END_YEAR);
//
//        double smallest = Double.MAX_VALUE;
//        double largest = Double.MIN_VALUE;
//        double sum = 0;
//        int count = 0;
//
//        for (Double d : ltcs.analyse(pop.people)) {
//            if (d < smallest)
//                smallest = d;
//            if (d > largest)
//                largest = d;
//
//            System.out.println((START_YEAR + count) + "\t" + d);
//
//            count++;
//            sum += d;
//
//        }
//
////            System.out.println("\nSmallest\tMean\tLargest");
//        System.out.println(smallest + "\t" + sum / (double) count + "\t" + largest);

    }

    /**
     * Gets days in year.
     *
     * @return the days in year
     */
    public static int getDaysInYear() {
        return DAYS_PER_YEAR;
    }

    /**
     * Gets epoch year.
     *
     * @return the epoch year
     */
    public static int getEpochYear() {
        return EPOCH_YEAR;
    }

    /**
     * Gets days per year.
     *
     * @return the days per year
     */
    public static int getDaysPerYear() {
        return DAYS_PER_YEAR;
    }

    /**
     * Run simulation.
     */
    public void runSimulation() {

        UniformIntegerDistribution dayInPeriod = new UniformIntegerDistribution(0, timeStep - 1, random);

        int currentYear = DateManipulation.daysToYear(currentDay - 10);
//        System.out.print(DateManipulation.daysToYear(currentDay));
//        System.out.println("\t" + people.size());

        while (currentDay < endYearInDays) {

            if(currentYear != DateManipulation.daysToYear(currentDay)) {
                System.out.print(DateManipulation.daysToYear(currentDay));
                System.out.println("\t" + people.size());
                currentYear++;
            }



            for (int i = 0; i < people.size(); i++) {
                Person p = people.get(i);
                if (lifeTables.toDieByNQX(p, currentDay, random, timeStep)) {

//                    int year = DateManipulation.daysToYear(currentDay);
//                    int daysLeftInYear = DateManipulation.differenceInDays(currentDay, DateManipulation.dateToDays(year + 1,0,0) - 1);
//
//                    int daysToDeath = dayInPeriod.getSample();
//
//                    if(daysLeftInYear < daysToDeath) {
//                        daysToDeath = daysLeftInYear - 1;
//                    }
//
//                    if(p.getAge(currentDay) != p.getAge(currentDay + daysToDeath)) {
//                        int deathAgeInDays = p.getAge(currentDay + daysToDeath) * DAYS_PER_YEAR - 1;
//                        daysToDeath = deathAgeInDays - currentDay;
//                    }

//                    System.out.println("Y: " + year + "   DL: " + daysLeftInYear + "   DTD: " + daysToDeath);

                    // MAke sure data point being put in current year


                    p.die(currentDay); // + daysToDeath);

                    deadPeople.add(p);
                    people.remove(p);
//                    people.add(new Person(currentDay, true));
                }
            }


            currentDay += timeStep;
        }

        people.addAll(deadPeople);

    }

    /**
     * Generate seed population.
     *
     * @param seedSize the seed size
     */
    public void generateSeedPopulation(int seedSize) {

        TemporalIntegerDistribution seedAgeForMalesDistribution = new TemporalIntegerDistribution(this, "seed_age_for_males_distribution_data_filename", random, false);
//        TemporalIntegerDistribution seedAgeForFemalesDistribution = new TemporalIntegerDistribution(this, "seed_age_for_females_distribution_data_filename", random, false);

        ArrayList<Person> seedPop = new ArrayList<Person>();

        for (int i = 0; i < seedSize; i++) {
//            if (random.nextBoolean()) {
            int age = seedAgeForMalesDistribution.getSample();
            age = 0;
            seedPop.add(new Person(currentDay - age, true));
//            } else {
//                int age = seedAgeForFemalesDistribution.getSample();
//                seedPop.add(new Person(currentDay - age, false));
//            }
        }

        people = seedPop;

    }

    /**
     * Init life tables.
     */
    public void initLifeTables() {
        lifeTables = new LifeTableCatalogue(EPOCH_YEAR, END_YEAR, "lifetable_catalogue");
    }

    @Override
    public void setMaximumNumberOfChildrenInFamily(int maximum) {
        // Here to allow the use of temporal distributions for the time being
    }
}
