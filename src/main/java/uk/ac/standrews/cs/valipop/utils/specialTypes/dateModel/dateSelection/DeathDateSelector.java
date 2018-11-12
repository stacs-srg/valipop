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
package uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateSelection;

import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvanceableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPersonExtended;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DeathDateSelector extends DateSelector {

    public ExactDate selectDate(IPersonExtended p, PopulationStatistics desiredPopulationStatistics, AdvanceableDate currentDate, CompoundTimeUnit consideredTimePeriod) {

        IPersonExtended child = p.getLastChild();

        if(child != null) {

            Date birthDateOfLastChild = child.getBirthDate_ex().getExactDate();

            if (Character.toLowerCase(p.getSex()) == 'm') {
                // If a male with a child then the man cannot die more than the minimum gestation period before the birth date
                Date ePD = DateUtils.calculateExactDate(birthDateOfLastChild, (-1) * desiredPopulationStatistics.getMinGestationPeriod());
                return selectDateRestrictedByEPD(currentDate, consideredTimePeriod, ePD, desiredPopulationStatistics.getRandomGenerator());
            } else {
                // If a female with a child then the cannot die before birth of child
                return selectDateRestrictedByEPD(currentDate, consideredTimePeriod, birthDateOfLastChild, desiredPopulationStatistics.getRandomGenerator());
            }

        } else {
            return selectDateRestrictedByEPD(currentDate, consideredTimePeriod, p.getBirthDate_ex(), desiredPopulationStatistics.getRandomGenerator());
        }

    }

    private ExactDate selectDateRestrictedByEPD(AdvanceableDate currentDate, CompoundTimeUnit consideredTimePeriod,
                                                Date earliestPossibleDate, RandomGenerator random) {

        // if specified earliestPossibleDate is in consideredTimePeriod
        if(DateUtils.dateBeforeOrEqual(currentDate, earliestPossibleDate)) {
            // The select date between earliestPossibleDate and currentDate + consideredTimePeriod
            return selectDate(earliestPossibleDate, currentDate.advanceTime(consideredTimePeriod), random);
        } else {
            // else all days in consideredTimePeriod are an option
            return selectDate(currentDate, consideredTimePeriod, random);
        }

    }


}
