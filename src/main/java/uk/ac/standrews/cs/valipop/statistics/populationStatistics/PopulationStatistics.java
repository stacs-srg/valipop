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
package uk.ac.standrews.cs.valipop.statistics.populationStatistics;

import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.valipop.statistics.distributions.general.EnumeratedDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.*;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.AgeDependantEnumeratedDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.ProportionalDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.ValiPopEnumeratedDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting.SelfCorrectingProportionalDistribution;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.ValipopDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.DeterminedCount;

import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.EventRateTables;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting.SelfCorrectingOneDimensionDataDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting.SelfCorrectingTwoDimensionDataDistribution;

import java.util.*;

/**
 * The PopulationStatistics holds data about the rate at which specified events occur to specified subsets of
 * members of the summative population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PopulationStatistics implements EventRateTables {

    private Map<YearDate, SelfCorrectingOneDimensionDataDistribution> maleDeath;
    private Map<YearDate, SelfCorrectingOneDimensionDataDistribution> femaleDeath;
    private Map<YearDate, SelfCorrectingProportionalDistribution> partnering;
    private Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> orderedBirth;
    private Map<YearDate, ProportionalDistribution> multipleBirth;
    private Map<YearDate, SelfCorrectingOneDimensionDataDistribution> illegitimateBirth;
    private Map<YearDate, SelfCorrectingOneDimensionDataDistribution> marriage;
    private Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> separation;

    private Map<YearDate, Double> sexRatioBirth;

    private Map<YearDate, ValiPopEnumeratedDistribution> maleForename;
    private Map<YearDate, ValiPopEnumeratedDistribution> femaleForename;

    private Map<YearDate, ValiPopEnumeratedDistribution> surname;

    private Map<YearDate, AgeDependantEnumeratedDistribution> maleDeathCauses;
    private Map<YearDate, AgeDependantEnumeratedDistribution> femaleDeathCauses;

    // Population Constants
    private int minGestationPeriodDays = 147;
    private int minBirthSpacingDays = 147;
    private RandomGenerator randomGenerator;

    public PopulationStatistics(RandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    public PopulationStatistics(Map<YearDate, SelfCorrectingOneDimensionDataDistribution> maleDeath,
                                Map<YearDate, AgeDependantEnumeratedDistribution> maleDeathCauses,
                                Map<YearDate, SelfCorrectingOneDimensionDataDistribution> femaleDeath,
                                Map<YearDate, AgeDependantEnumeratedDistribution> femaleDeathCauses,
                                Map<YearDate, SelfCorrectingProportionalDistribution> partnering,
                                Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> orderedBirth,
                                Map<YearDate, ProportionalDistribution> multipleBirth,
                                Map<YearDate, SelfCorrectingOneDimensionDataDistribution> illegitimateBirth,
                                Map<YearDate, SelfCorrectingOneDimensionDataDistribution> marriage,
                                Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> separation,
                                Map<YearDate, Double> sexRatioBirths,
                                Map<YearDate, ValiPopEnumeratedDistribution> maleForename,
                                Map<YearDate, ValiPopEnumeratedDistribution> femaleForename,
                                Map<YearDate, ValiPopEnumeratedDistribution> surname,
                                int minBirthSpacingDays,
                                int minGestationPeriodDays,
                                RandomGenerator randomGenerator) {

        this.maleDeath = maleDeath;
        this.maleDeathCauses = maleDeathCauses;
        this.femaleDeath = femaleDeath;
        this.femaleDeathCauses = femaleDeathCauses;
        this.partnering = partnering;
        this.orderedBirth = orderedBirth;
        this.multipleBirth = multipleBirth;
        this.illegitimateBirth = illegitimateBirth;
        this.marriage = marriage;
        this.separation = separation;
        this.sexRatioBirth = sexRatioBirths;

        this.maleForename = maleForename;
        this.femaleForename = femaleForename;
        this.surname = surname;

        this.minBirthSpacingDays = minBirthSpacingDays;
        this.minGestationPeriodDays = minGestationPeriodDays;

        this.randomGenerator = randomGenerator;
    }

    /*
    -------------------- EventRateTables interface methods --------------------
     */

    public DeterminedCount getDeterminedCount(StatsKey key, Config config) {

        if (key instanceof DeathStatsKey) {
            DeathStatsKey k = (DeathStatsKey) key;
            return getDeathRates(k.getDate(), k.getSex()).determineCount(k, config);
        }

        if (key instanceof BirthStatsKey) {
            BirthStatsKey k = (BirthStatsKey) key;
            return getOrderedBirthRates(k.getDate()).determineCount(k, config);
        }

        if (key instanceof MultipleBirthStatsKey) {
            MultipleBirthStatsKey k = (MultipleBirthStatsKey) key;
            return getMultipleBirthRates(k.getDate()).determineCount(k, config);
        }

        if (key instanceof IllegitimateBirthStatsKey) {
            IllegitimateBirthStatsKey k = (IllegitimateBirthStatsKey) key;
            return getIllegitimateBirthRates(k.getDate()).determineCount(k, config);
        }

        if (key instanceof MarriageStatsKey) {
            MarriageStatsKey k = (MarriageStatsKey) key;
            return getMarriageRates(k.getDate()).determineCount(k, config);
        }

        if (key instanceof SeparationStatsKey) {
            SeparationStatsKey k = (SeparationStatsKey) key;
            return getSeparationByChildCountRates(k.getDate()).determineCount(k, config);
        }

        if (key instanceof PartneringStatsKey) {
            PartneringStatsKey k = (PartneringStatsKey) key;
            return getPartneringRates(k.getDate()).determineCount(k, config);
        }

        throw new Error("Key based access not implemented for key class: " + key.getClass().toGenericString());
    }

    public void returnAchievedCount(DeterminedCount achievedCount) {

        if (achievedCount.getKey() instanceof DeathStatsKey) {
            DeathStatsKey k = (DeathStatsKey) achievedCount.getKey();
            getDeathRates(k.getDate(), k.getSex()).returnAchievedCount(achievedCount);
            return;
        }

        if (achievedCount.getKey() instanceof BirthStatsKey) {
            BirthStatsKey k = (BirthStatsKey) achievedCount.getKey();
            getOrderedBirthRates(k.getDate()).returnAchievedCount(achievedCount);
            return;
        }

        if (achievedCount.getKey() instanceof MultipleBirthStatsKey) {
            MultipleBirthStatsKey k = (MultipleBirthStatsKey) achievedCount.getKey();
            getMultipleBirthRates(k.getDate()).returnAchievedCount(achievedCount);
            return;
        }

        if (achievedCount.getKey() instanceof IllegitimateBirthStatsKey) {
            IllegitimateBirthStatsKey k = (IllegitimateBirthStatsKey) achievedCount.getKey();
            getIllegitimateBirthRates(k.getDate()).returnAchievedCount(achievedCount);
            return;
        }

        if (achievedCount.getKey() instanceof MarriageStatsKey) {
            MarriageStatsKey k = (MarriageStatsKey) achievedCount.getKey();
            getMarriageRates(k.getDate()).returnAchievedCount(achievedCount);
            return;
        }

        if (achievedCount.getKey() instanceof SeparationStatsKey) {
            SeparationStatsKey k = (SeparationStatsKey) achievedCount.getKey();
            getSeparationByChildCountRates(k.getDate()).returnAchievedCount(achievedCount);
            return;
        }

        if (achievedCount.getKey() instanceof PartneringStatsKey) {
            PartneringStatsKey k = (PartneringStatsKey) achievedCount.getKey();
            getPartneringRates(k.getDate()).returnAchievedCount(achievedCount);
            return;
        }

        throw new Error("Key based access not implemented for key class: "
                + achievedCount.getKey().getClass().toGenericString());
    }

    @Override
    public SelfCorrectingOneDimensionDataDistribution getDeathRates(ValipopDate year, char gender) {
        if (Character.toLowerCase(gender) == 'm') {
            return maleDeath.get(getNearestYearInMap(year.getYearDate(), maleDeath));
        } else {
            return femaleDeath.get(getNearestYearInMap(year.getYearDate(), femaleDeath));
        }
    }

    @Override
    public EnumeratedDistribution getDeathCauseRates(ValipopDate year, char gender, int age) {
        if (Character.toLowerCase(gender) == 'm') {
            return maleDeathCauses.get(getNearestYearInMap(year.getYearDate(), maleDeathCauses)).getDistributionForAge(age);
        } else {
            return femaleDeathCauses.get(getNearestYearInMap(year.getYearDate(), femaleDeathCauses)).getDistributionForAge(age);
        }
    }

    @Override
    public SelfCorrectingProportionalDistribution getPartneringRates(ValipopDate year) {
        return partnering.get(getNearestYearInMap(year.getYearDate(), partnering));
    }

    @Override
    public SelfCorrectingOneDimensionDataDistribution getIllegitimateBirthRates(ValipopDate year) {
        return illegitimateBirth.get(getNearestYearInMap(year.getYearDate(), illegitimateBirth));
    }

    @Override
    public SelfCorrectingOneDimensionDataDistribution getMarriageRates(ValipopDate year) {
        return marriage.get(getNearestYearInMap(year.getYearDate(), marriage));
    }

    @Override
    public SelfCorrectingTwoDimensionDataDistribution getOrderedBirthRates(ValipopDate year) {
        return orderedBirth.get(getNearestYearInMap(year.getYearDate(), orderedBirth));
    }

    @Override
    public ProportionalDistribution getMultipleBirthRates(ValipopDate year) {
        return multipleBirth.get(getNearestYearInMap(year.getYearDate(), multipleBirth));
    }

    @Override
    public SelfCorrectingTwoDimensionDataDistribution getSeparationByChildCountRates(ValipopDate year) {
        return separation.get(getNearestYearInMap(year.getYearDate(), separation));
    }

    @Override
    public EnumeratedDistribution getForenameDistribution(ValipopDate year, char gender) {
        if (Character.toLowerCase(gender) == 'm') {
            return maleForename.get(getNearestYearInMap(year.getYearDate(), maleForename));
        } else {
            return femaleForename.get(getNearestYearInMap(year.getYearDate(), femaleForename));
        }
    }

    @Override
    public EnumeratedDistribution getSurnameDistribution(ValipopDate year) {
        return surname.get(getNearestYearInMap(year.getYearDate(), surname));
    }

    @Override
    public double getMaleProportionOfBirths(ValipopDate onDate) {
        return sexRatioBirth.get(getNearestYearInMap(onDate, sexRatioBirth));
    }

    private YearDate getNearestYearInMap(ValipopDate year, Map<YearDate, ?> map) {

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

    public int getMinBirthSpacing() {
        return minBirthSpacingDays;
    }

    public int getMinGestationPeriod() {
        return minGestationPeriodDays;
    }

    public RandomGenerator getRandomGenerator() {
        return randomGenerator;
    }
}
