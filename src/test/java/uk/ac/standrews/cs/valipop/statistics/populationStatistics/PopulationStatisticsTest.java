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

import org.junit.Test;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.BirthStatsKey;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.MultipleBirthStatsKey;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRangeToDoubleSet;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PopulationStatisticsTest {

    // TODO what properties do these test?

    @Test
    public void testA() {

        Config config = new Config(Paths.get("src/test/resources/valipop/config-ps.txt"));
        PopulationStatistics ps = new PopulationStatistics(config);

        int age = 20;
        int order = 0;
        int cohortSize = 1000;
        Period consideredTimePeriod = Period.ofYears(1);
        LocalDate currentDate = LocalDate.of(1900, 1, 1);

        BirthStatsKey key = new BirthStatsKey(age, order, cohortSize, consideredTimePeriod, currentDate);
        SingleDeterminedCount determinedCount = (SingleDeterminedCount) ps.getDeterminedCount(key, null);

        int numberOfChildren = determinedCount.getDeterminedCount();

        MultipleBirthStatsKey keyM = new MultipleBirthStatsKey(age, numberOfChildren, consideredTimePeriod, currentDate);
        MultipleDeterminedCount mDC = (MultipleDeterminedCount) ps.getDeterminedCount(keyM, null);

        mDC.getDeterminedCount().getSumOfValues();
    }

    @Test
    public void testB() {

        Config config = new Config(Paths.get("src/test/resources/valipop/config-ps.txt"));
        PopulationStatistics ps = new PopulationStatistics(config);

        int age = 20;
        int order = 0;
        int cohortSize = 1000;
        Period consideredTimePeriod = Period.ofYears(1);
        LocalDate currentDate = LocalDate.of(1900, 1, 1);

        SingleDeterminedCount sDC = (SingleDeterminedCount) ps.getDeterminedCount(new BirthStatsKey(age, order, cohortSize, consideredTimePeriod, currentDate), null);

        double numberOfChildren = sDC.getRawUncorrectedCount();

        MultipleDeterminedCount mDc = (MultipleDeterminedCount) ps.getDeterminedCount(new MultipleBirthStatsKey(age, numberOfChildren, consideredTimePeriod, currentDate), null);

        double numberOfMothers = mDc.getRawUncorrectedCount().getSumOfValues();

        MultipleDeterminedCount mDC = (MultipleDeterminedCount) ps.getDeterminedCount(new MultipleBirthStatsKey(age, numberOfMothers, Period.ofYears(1), currentDate), null);

        new IntegerRangeToDoubleSet(mDC.getRawUncorrectedCount()).productOfLabelsAndValues().getSumOfValues();

        mDC.getRawUncorrectedCount();
    }
}
