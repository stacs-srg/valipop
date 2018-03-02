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
package uk.ac.standrews.cs.valipop.events.birth.partnering;

import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.SeparationStatsKey;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.Population;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SeparationLogic {

    public static void handle(Map<Integer, ArrayList<IPersonExtended>> continuingPartnedFemalesByChildren,
                              CompoundTimeUnit consideredTimePeriod, Date currentDate,
                              PopulationStatistics desiredPopulationStatistics, Population population, Config config) {

        // Consideration of separation is based on number of children in females current partnerships
        for(Map.Entry<Integer, ArrayList<IPersonExtended>> entry : continuingPartnedFemalesByChildren.entrySet()) {

            Integer numberOfChildren = entry.getKey();
            Integer ageOfMothers = 0;

            // Get mothers with given number of children in current partnership
            ArrayList<IPersonExtended> mothers = entry.getValue();

            if(mothers.size() != 0) {
                ageOfMothers = mothers.get(0).ageOnDate(currentDate);
            }

            // Get determined count for separations for this group of mothers
            SeparationStatsKey key = new SeparationStatsKey(numberOfChildren, ageOfMothers, mothers.size(), consideredTimePeriod, currentDate);
            SingleDeterminedCount dC = (SingleDeterminedCount) desiredPopulationStatistics.getDeterminedCount(key, config);

            int count = 0;

            // For each mother in this group
            for(IPersonExtended p : mothers) {

                // If enough mothers have been separated then break
                if(count >= dC.getDeterminedCount()) {
                    break;
                }

                // else mark partnership for separation
                p.getLastPartnership().separate(p.getLastChild().getBirthDate_ex(), new CompoundTimeUnit(1, TimeUnit.MONTH));

                count++;

            }

            // Return achieved statistics to the statistics handler
            population.getPopulationCounts().partnershipEnd(count);
            dC.setFufilledCount(count);
            desiredPopulationStatistics.returnAchievedCount(dC);

        }
    }
}
