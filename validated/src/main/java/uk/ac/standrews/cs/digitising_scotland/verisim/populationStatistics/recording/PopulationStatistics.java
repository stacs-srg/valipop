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
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateBounds;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.DeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting.ProportionalDistributionAdapter;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting.SelfCorrectingProportionalDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.*;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.comparison.EventRateTables;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting.SelfCorrectingOneDimensionDataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting.SelfCorrectingTwoDimensionDataDistribution;

import java.util.*;


/**
 * The PopulationStatistics holds data about the rate at which specified events occur to specified subsets of
 * members of the summative population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PopulationStatistics implements DateBounds, EventRateTables {

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
    private int minBirthSpacingDays = 1;
    private double maxProportionBirthsDueToInfidelity = 0.2;
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
    --------------------- Private Helper Methods ---------------------
     */


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
