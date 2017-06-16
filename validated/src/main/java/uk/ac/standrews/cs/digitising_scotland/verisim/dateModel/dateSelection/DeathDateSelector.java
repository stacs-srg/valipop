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
package uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateSelection;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPerson;

import java.util.Random;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DeathDateSelector implements DateSelector {

    private Random random = new Random();

    @Override
    public ExactDate selectDate(Date latestPossibleDate, CompoundTimeUnit consideredTimePeriod) {

        int possibleDays = DateUtils.getDaysInTimePeriod(latestPossibleDate, consideredTimePeriod.negative());

        int chosenDay = (-1) * random.nextInt(Math.abs(possibleDays));

        return DateUtils.calculateExactDate(latestPossibleDate, chosenDay);

    }

    @Override
    public ExactDate selectDate(Date latestPossibleDate, CompoundTimeUnit consideredTimePeriod, int imposedLimit) {

        if(imposedLimit == 0) {
            return latestPossibleDate.getExactDate();
        }

        int chosenDay = (-1) * random.nextInt(Math.abs(imposedLimit));

        return DateUtils.calculateExactDate(latestPossibleDate, chosenDay);

    }

    @Override
    public ExactDate selectDateLPD(AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod, Date latestPossibleDate) {

        // if specified latestPossibleDate is in consideredTimePeriod
        if (DateUtils.dateBeforeOrEqual(latestPossibleDate, currentDate.advanceTime(consideredTimePeriod))) {
            // then select date between currentDate and latestPossible date
            int days = DateUtils.differenceInDays(currentDate, latestPossibleDate);
            int chosenDay = random.nextInt(Math.abs(days));
            return DateUtils.calculateExactDate(currentDate, chosenDay);
        } else {
            // else all days in consideredTimePeriod are an option
            return selectDateUnrestricted(currentDate, consideredTimePeriod);
        }

    }

    @Override
    public ExactDate selectDateEPD(AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod, Date earliestPossibleDate) {

        // if specified earliestPossibleDate is in consideredTimePeriod
        if(DateUtils.dateBeforeOrEqual(currentDate, earliestPossibleDate)) {
            // The select date between earliestPossibleDate and currentDate + consideredTimePeriod
            int days = DateUtils.differenceInDays(earliestPossibleDate, currentDate.advanceTime(consideredTimePeriod));
            int chosenDay = random.nextInt(Math.abs(days));
            return DateUtils.calculateExactDate(currentDate, chosenDay);
        } else {
            // else all days in consideredTimePeriod are an option
            return selectDateUnrestricted(currentDate, consideredTimePeriod);
        }

    }

    @Override
    public ExactDate selectDate(IPerson p, PopulationStatistics desiredPopulationStatistics, AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod) {

        IPerson child = p.getLastChild();

        if(child != null) {

            Date birthDateOfLastChild = child.getBirthDate().getExactDate();

            if (Character.toLowerCase(p.getSex()) == 'm') {
                // If a male with a child then the man cannot die more than 9 months before the birth date
                Date ePD = DateUtils.calculateExactDate(birthDateOfLastChild, (-1) * desiredPopulationStatistics.getMaxGestationPeriod());
                return selectDateEPD(currentDate, consideredTimePeriod, ePD);
            } else {
                // If a female with a child then the cannot die before birth of child
                return selectDateEPD(currentDate, consideredTimePeriod, birthDateOfLastChild);
            }

        } else {
            return selectDateUnrestricted(currentDate, consideredTimePeriod);
        }

    }

    private ExactDate selectDateUnrestricted(AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod) {
        int days = DateUtils.getDaysInTimePeriod(currentDate, consideredTimePeriod);
        int chosenDay = random.nextInt(Math.abs(days));
        return DateUtils.calculateExactDate(currentDate, chosenDay);
    }


}
