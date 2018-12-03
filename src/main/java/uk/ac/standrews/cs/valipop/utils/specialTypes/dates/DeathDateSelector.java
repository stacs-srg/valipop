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
package uk.ac.standrews.cs.valipop.utils.specialTypes.dates;

import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.population.PopulationNavigation;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SexOption;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DeathDateSelector extends DateSelector {

    public DeathDateSelector(RandomGenerator random) {

        super(random);
    }

    public LocalDate selectDate(IPerson person, PopulationStatistics statistics, LocalDate currentDate, Period consideredTimePeriod) {

        IPerson child = PopulationNavigation.getLastChild(person);

        if (child != null) {

            LocalDate birthDateOfLastChild = child.getBirthDate();

            if (person.getSex() == SexOption.MALE) {

                // If a male with a child then the man cannot die more than the minimum gestation period before the birth date
                LocalDate earliestPossibleDate = birthDateOfLastChild.minus( statistics.getMinGestationPeriod(), ChronoUnit.DAYS);
                return selectDateRestrictedByEarliestPossibleDate(currentDate, consideredTimePeriod, earliestPossibleDate);

            } else {
                // If a female with a child then the cannot die before birth of child
                return selectDateRestrictedByEarliestPossibleDate(currentDate, consideredTimePeriod, birthDateOfLastChild);
            }

        } else {
            return selectDateRestrictedByEarliestPossibleDate(currentDate, consideredTimePeriod, person.getBirthDate());
        }
    }

    private LocalDate selectDateRestrictedByEarliestPossibleDate(LocalDate currentDate, Period consideredTimePeriod, LocalDate earliestPossibleDate) {

        // if specified earliestPossibleDate is in consideredTimePeriod
        if (!currentDate.isAfter( earliestPossibleDate)) {

            // The select date between earliestPossibleDate and currentDate + consideredTimePeriod
            return selectRandomDate(earliestPossibleDate, currentDate.plus(consideredTimePeriod));

        } else {
            // else all days in consideredTimePeriod are an option
            return selectRandomDate(currentDate, consideredTimePeriod);
        }
    }
}
