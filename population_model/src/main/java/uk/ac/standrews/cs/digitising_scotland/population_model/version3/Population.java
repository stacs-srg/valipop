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
