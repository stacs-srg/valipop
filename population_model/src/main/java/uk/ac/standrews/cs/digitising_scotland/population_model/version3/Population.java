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
package uk.ac.standrews.cs.digitising_scotland.population_model.version3;


import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal.ITemporalPopulationInfo;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal.TemporalIntegerDistribution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by tsd4 on 10/11/2015.
 */
public class Population implements ITemporalPopulationInfo {

    public static void main(String[] args) {
        Population pop = new Population();
        System.out.println(pop.generateSeedPopulation(10000).size());
    }

    private List<Person> people = new ArrayList<Person>();

    private static final int EPOCH_YEAR = 1600;
    private static final int DAYS_PER_YEAR = 365;

    private int startYear = 1855;
    private int endYear = 1900;

    private int timeStep = 365;

    private int seedSize = 10000;

    private Random random = new Random();


    public ArrayList<Person> generateSeedPopulation(int seedSize) {

        TemporalIntegerDistribution seedAgeForMalesDistribution = new TemporalIntegerDistribution(this, "seed_age_for_males_distribution_data_filename", random, false);
        TemporalIntegerDistribution seedAgeForFemalesDistribution = new TemporalIntegerDistribution(this, "seed_age_for_females_distribution_data_filename", random, false);



        return new ArrayList<>();


    }


    public int getEpochYear() {
        return EPOCH_YEAR;
    }

    public int getDaysPerYear() {
        return DAYS_PER_YEAR;
    }

    @Override
    public void setMaximumNumberOfChildrenInFamily(int maximum) {
        // Here to allow the use of temporal distributions for the time being
    }
}
