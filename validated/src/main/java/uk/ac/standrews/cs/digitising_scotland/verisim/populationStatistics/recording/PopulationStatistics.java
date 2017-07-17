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
package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording;

import uk.ac.standrews.cs.digitising_scotland.verisim.config.Config;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.EventType;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.UnsupportedEventType;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.DeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting.ProportionalDistributionAdapter;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting.SelfCorrectingProportionalDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.*;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.comparison.EventRateTables;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.OneDimensionDataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting.SelfCorrectingOneDimensionDataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting.SelfCorrectingTwoDimensionDataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.kaplanMeier.utils.FailureTimeRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.InvalidRangeException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.CollectionUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.*;


/**
 * The PopulationStatistics holds data about the rate at which specified events occur to specified subsets of
 * members of the summative population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PopulationStatistics implements PopulationComposition, EventRateTables {

    private MonthDate startDate;
    private Date endDate;

    private Map<YearDate, SelfCorrectingOneDimensionDataDistribution> maleDeath;
    private Map<YearDate, SelfCorrectingOneDimensionDataDistribution> femaleDeath;
    private Map<YearDate, SelfCorrectingProportionalDistribution> partnering;
    private Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> orderedBirth;
    private Map<YearDate, ProportionalDistributionAdapter> multipleBirth;
    private Map<YearDate, SelfCorrectingOneDimensionDataDistribution> separation;

    // Population Constants
    private int maxGestationPeriodDays = 280;
    private int minGestationPeriodDays = 147;
    private int minBirthSpacingDays = 730;
    private double maxProportionBirthsDueToInfidelity = 0.1;
    private double maleProportionOfBirths = 0.5; // i.e. if 0.52 then in every 100 births, 52 will be male and 48 female

    public PopulationStatistics(Config config,
                                Map<YearDate, SelfCorrectingOneDimensionDataDistribution> maleDeath,
                                Map<YearDate, SelfCorrectingOneDimensionDataDistribution> femaleDeath,
                                Map<YearDate, SelfCorrectingProportionalDistribution> partnering,
                                Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> orderedBirth,
                                Map<YearDate, ProportionalDistributionAdapter> multipleBirth,
                                Map<YearDate, SelfCorrectingOneDimensionDataDistribution> separation) {

        this.maleDeath = maleDeath;
        this.femaleDeath = femaleDeath;
        this.partnering = partnering;
        this.orderedBirth = orderedBirth;
        this.multipleBirth = multipleBirth;
        this.separation = separation;

        this.startDate = config.getTS();
        this.endDate = config.getTE();

    }

    /*
    -------------------- DateBounds interface methods --------------------
     */

    @Override
    public MonthDate getStartDate() {
        return startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public void setStartDate(AdvancableDate start) {
        startDate = start.getMonthDate();
    }

    @Override
    public void setEndDate(Date end) {
        endDate = end;
    }

    /*
    -------------------- EventRateTables interface methods --------------------
     */

    public DeterminedCount getDeterminedCount(StatsKey key) {

        if(key instanceof DeathStatsKey) {
            DeathStatsKey k = (DeathStatsKey) key;
            return getDeathRates(k.getDate(), k.getSex()).determineCount(k);
        }

        if(key instanceof BirthStatsKey) {
            BirthStatsKey k = (BirthStatsKey) key;
            return getOrderedBirthRates(k.getDate()).determineCount(k);
        }

        if(key instanceof MultipleBirthStatsKey) {
            MultipleBirthStatsKey k = (MultipleBirthStatsKey) key;
            return getMultipleBirthRates(k.getDate()).determineCount(k);
        }

        if(key instanceof SeparationStatsKey) {
            SeparationStatsKey k = (SeparationStatsKey) key;
            return getSeparationByChildCountRates(k.getDate()).determineCount(k);
        }

        if(key instanceof PartneringStatsKey) {
            PartneringStatsKey k = (PartneringStatsKey) key;
            return getPartneringRates(k.getDate()).determineCount(k);
        }

        throw new Error("Key based access not implemented for key class: " + key.getClass().toGenericString());
    }

    public void returnAchievedCount(DeterminedCount achievedCount) {

        if(achievedCount.getKey() instanceof DeathStatsKey) {
            DeathStatsKey k = (DeathStatsKey) achievedCount.getKey();
            getDeathRates(k.getDate(), k.getSex()).returnAchievedCount(achievedCount);
            return;
        }

        if(achievedCount.getKey() instanceof BirthStatsKey) {
            BirthStatsKey k = (BirthStatsKey) achievedCount.getKey();
            getOrderedBirthRates(k.getDate()).returnAchievedCount(achievedCount);
            return;
        }

        if(achievedCount.getKey() instanceof MultipleBirthStatsKey) {
            MultipleBirthStatsKey k = (MultipleBirthStatsKey) achievedCount.getKey();
            getMultipleBirthRates(k.getDate()).returnAchievedCount(achievedCount);
            return;
        }

        if(achievedCount.getKey() instanceof SeparationStatsKey) {
            SeparationStatsKey k = (SeparationStatsKey) achievedCount.getKey();
            getSeparationByChildCountRates(k.getDate()).returnAchievedCount(achievedCount);
            return;
        }

        if(achievedCount.getKey() instanceof PartneringStatsKey) {
            PartneringStatsKey k = (PartneringStatsKey) achievedCount.getKey();
            getPartneringRates(k.getDate()).returnAchievedCount(achievedCount);
            return;
        }

        throw new Error("Key based access not implemented for key class: "
                + achievedCount.getKey().getClass().toGenericString());

    }

    @Override
    public SelfCorrectingOneDimensionDataDistribution getDeathRates(Date year, char gender) {
        if (Character.toLowerCase(gender) == 'm') {
            return maleDeath.get(getNearestYearInMap(year.getYearDate(), maleDeath));
        } else {
            return femaleDeath.get(getNearestYearInMap(year.getYearDate(), femaleDeath));
        }
    }

    @Override
    public SelfCorrectingProportionalDistribution getPartneringRates(Date year) {
        return partnering.get(getNearestYearInMap(year.getYearDate(), partnering));
    }

    @Override
    public SelfCorrectingTwoDimensionDataDistribution getOrderedBirthRates(Date year) {
        return orderedBirth.get(getNearestYearInMap(year.getYearDate(), orderedBirth));
    }

    @Override
    public ProportionalDistributionAdapter getMultipleBirthRates(Date year) {
        return multipleBirth.get(getNearestYearInMap(year.getYearDate(), multipleBirth));
    }

    @Override
    public SelfCorrectingOneDimensionDataDistribution getSeparationByChildCountRates(Date year) {
        return separation.get(getNearestYearInMap(year.getYearDate(), separation));
    }

    /*
    -------------------- StatisticalTables interface methods --------------------
     */

    @Override
    public OneDimensionDataDistribution getCohortSurvivorTable(AdvancableDate cohortYear, EventType event) {
        return null;
    }

    @Override
    public OneDimensionDataDistribution getCohortSurvivorTable(AdvancableDate cohortYear, EventType event, Double scalingFactor, int timeLimit, IPopulation generatedPopulation) throws UnsupportedEventType {

        Map<IntegerRange, Double> survival = new HashMap<>();

        double survivors = scalingFactor;
        survival.put(new IntegerRange(0), survivors);

        int age = 0;
        for (AdvancableDate d = cohortYear; DateUtils.dateBeforeOrEqual(d, cohortYear.advanceTime(timeLimit, TimeUnit.YEAR)); d = d.advanceTime(1, TimeUnit.YEAR)) {

            double nMx = 0;

            switch (event) {
                case FIRST_BIRTH:
                    nMx = calculateOrderedBirthRate(cohortYear, d, age, 0, generatedPopulation, survivors);
                    break;
                case SECOND_BIRTH:
                    nMx = calculateOrderedBirthRate(cohortYear, d, age, 1, generatedPopulation, survivors);
                    break;
                case THIRD_BIRTH:
                    nMx = calculateOrderedBirthRate(cohortYear, d, age, 2, generatedPopulation, survivors);
                    break;
                case FOURTH_BIRTH:
                    nMx = calculateOrderedBirthRate(cohortYear, d, age, 3, generatedPopulation, survivors);
                    break;
                case FIFTH_BIRTH:
                    nMx = calculateOrderedBirthRate(cohortYear, d, age, 4, generatedPopulation, survivors);
                    break;
                case MALE_DEATH:
                    nMx = getDeathRates(d, 'm').getRate(age);
                    break;
                case FEMALE_DEATH:
                    nMx = getDeathRates(d, 'f').getRate(age);
                    break;
            }


//            int n = timePeriod.getCount();


            // EDIT - put nQx back in here
//            double nQx = (n * nMx) / (1 + (n * 0.5 * nMx));


            survivors = survivors * (1 - nMx);

            if(survivors - (int)survivors < 0.5) {
                survivors = (int) survivors;
            } else {
                survivors = (int) survivors + 1;
            }



            // TEMP ive taken the rounding out of here on survivors and a +1 off age - NOW PUT BACK IN
            survival.put(new IntegerRange(age + 1), survivors);

            age++;

        }


        return new OneDimensionDataDistribution(cohortYear.getYearDate(), "", "", survival);
    }

    @Override
    public OneDimensionDataDistribution getTimePeriodSurvivorTable(AdvancableDate startYear, CompoundTimeUnit timePeriod, EventType event) throws UnsupportedEventType {

        return null;
    }

    // Returns the survival table based on only using the statistics from the given startYear. Top survivor value will be 100,000.
    @Override
    public OneDimensionDataDistribution getTimePeriodSurvivorTable(Date startYear, int ageLimit, EventType event) throws UnsupportedEventType {

        Map<IntegerRange, Double> survival = new HashMap<>();

        double survivors = 1000;
        survival.put(new IntegerRange(0), survivors);


        for(int age = 0; age < ageLimit; age++) {

            double nMx = 0;

            switch (event) {
                case FIRST_BIRTH:
                case SECOND_BIRTH:
                case THIRD_BIRTH:
                case FOURTH_BIRTH:
                case FIFTH_BIRTH:
                    System.err.println("Not implemented - PopulationStatistics.getTimePeriodSurvivorTable");
                    break;
                case MALE_DEATH:
                    nMx = getDeathRates(startYear, 'm').getRate(age);
                    break;
                case FEMALE_DEATH:
                    nMx = getDeathRates(startYear, 'f').getRate(age);
                    break;
            }

            survivors = survivors * (1 - nMx);

            if(survivors - (int)survivors < 0.5) {
                survivors = (int) survivors;
            } else {
                survivors = (int) survivors + 1;
            }



            // TEMP ive taken the rounding out of here on survivors and a +1 off age - NOW PUT BACK IN
            survival.put(new IntegerRange(age + 1), survivors);


        }

        return new OneDimensionDataDistribution(startYear.getYearDate(), "", "", survival);
    }

    @Override
    public Collection<FailureTimeRow> getFailureAtTimesTable(AdvancableDate year, String denoteGroupAs, Date simulationEndDate, EventType event) {
        return null;
    }


    // TODO NEXT Make PopulationStatistics.getFailureAtTimesTable() a util method that takes in a survivor table and restuns the appropriate collection of FailureTimeRows
    @Override
    public Collection<FailureTimeRow> getFailureAtTimesTable(AdvancableDate year, String denoteGroupAs, Date simulationEndDate, EventType event, Double scalingFactor, int timeLimit, IPopulation generatedPopulation) throws UnsupportedEventType {

        Collection<FailureTimeRow> rows = new ArrayList<>();

        OneDimensionDataDistribution survivorTable = getCohortSurvivorTable(year, event, scalingFactor, timeLimit, generatedPopulation);

        double prevSurvivors = scalingFactor + 1;

        for(int i = 1; i < timeLimit; i++) {

            double currentSurvivors = survivorTable.getRate(i);
            double dead = prevSurvivors - currentSurvivors;

            int d = (int) dead;

            prevSurvivors -= d;

            for(int r = 0 ; r < d ; r++) {
                rows.add(new FailureTimeRow(i, true, denoteGroupAs));
            }

        }

        for(int s = 0; s < prevSurvivors; s++) {
            rows.add(new FailureTimeRow(timeLimit, false, denoteGroupAs));
        }

        return rows;
    }

    @Override
    public Collection<YearDate> getDataYearsInMap(EventType eventType) {
        switch(eventType) {

            case FIRST_BIRTH:
            case SECOND_BIRTH:
            case THIRD_BIRTH:
            case FOURTH_BIRTH:
            case FIFTH_BIRTH:
                return orderedBirth.keySet();
            case MALE_DEATH:
                return maleDeath.keySet();
            case FEMALE_DEATH:
                return femaleDeath.keySet();
            case SEPARATION:
                return separation.keySet();
            case PARTNERING:
                return partnering.keySet();
        }

        return Collections.emptyList();
    }

    @Override
    public OneDimensionDataDistribution getSeparationData(Date startYear, Date endYear) {
        return getSeparationByChildCountRates(startYear);
    }

    @Override
    public OneDimensionDataDistribution getSeparationData(AdvancableDate startYear, Date endYear, int childCap) {
        return getSeparationData(startYear, endYear);
    }

//    @Override
//    public OneDimensionDataDistribution getPartneringData(Date startYear, Date endYear, IntegerRange femaleAgeRange, Set<IntegerRange> maleAgeBrackets) {
//
//
//        return getPartneringRates(startYear).getData(femaleAgeRange.getValue());
//    }
//
//    @Override
//    public SelfCorrectingProportionalDistribution getPartneringData(Date startYear, Date endYear) {
//        return getPartneringRates(startYear);
//    }

    /*
    --------------------- Private Helper Methods ---------------------
     */

    private double calculateOrderedBirthRate(AdvancableDate startYear, Date currentDate, int age, int birthOrder, IPopulation generatedPopulation, double survivors) {
        SelfCorrectingTwoDimensionDataDistribution orderedBirthRates = getOrderedBirthRates(currentDate);

        OneDimensionDataDistribution aSOBR;
        try {
            aSOBR = orderedBirthRates.getData(age);
        } catch (InvalidRangeException e) {
            return 0.0;
        }

        int t = CollectionUtils.countPeopleInCollectionAliveOnDate(
                generatedPopulation.forceGetAllPersonsByTimePeriodAndSex(
                        startYear,
                        new CompoundTimeUnit(1, TimeUnit.YEAR),
                        'f'),
                currentDate);
        double r = aSOBR.getRate(birthOrder);
        return r;
//        return (r * t) / survivors;
    }

    private YearDate getNearestYearInMap(Date year, Map<YearDate, ?> map) {

        int minDifferenceInMonths = Integer.MAX_VALUE;
        YearDate nearestTableYear = null;

        ArrayList<YearDate> orderedKeySet = new ArrayList<>(map.keySet());
        Collections.sort(orderedKeySet);


        for (YearDate tableYear : orderedKeySet) {
            int difference = DateUtils.differenceInMonths(tableYear, year.getYearDate()).getCount();
            if (difference < minDifferenceInMonths) {
                minDifferenceInMonths = difference;
                nearestTableYear = tableYear;
            }
        }

        return nearestTableYear;

    }

    public int getMaxGestationPeriod() {
        return maxGestationPeriodDays;
    }

    public int getMinBirthSpacing() {
        return minBirthSpacingDays;
    }

    public int getMinGestationPeriod() {
        return minGestationPeriodDays;
    }

    public double getMaleProportionOfBirths() {
        return maleProportionOfBirths;
    }

    public double getMaxProportionBirthsDueToInfidelity() {
        return maxProportionBirthsDueToInfidelity;
    }


}
