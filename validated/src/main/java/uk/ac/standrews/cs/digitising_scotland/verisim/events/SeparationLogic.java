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
package uk.ac.standrews.cs.digitising_scotland.verisim.events;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.SeparationStatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.Population;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SeparationLogic {

    public static void handle(Map<Integer, ArrayList<IPersonExtended>> continuingPartnedFemalesByChildren,
                              CompoundTimeUnit consideredTimePeriod, Date currentDate, PopulationStatistics desiredPopulationStatistics, Population population) {

        // Consideration of separation is based on number of children in females current partnerships
        for(Integer numberOfChildren : continuingPartnedFemalesByChildren.keySet()) {

            // Get mothers with given number of children in current partnership
            ArrayList<IPersonExtended> mothers = continuingPartnedFemalesByChildren.get(numberOfChildren);

            // Get determined count for separations for this group of mothers
            SeparationStatsKey key = new SeparationStatsKey(numberOfChildren, mothers.size(), consideredTimePeriod, currentDate);
            SingleDeterminedCount dC = (SingleDeterminedCount) desiredPopulationStatistics.getDeterminedCount(key);

            int count = 0;

            // For each mother in this group
            for(IPersonExtended p : continuingPartnedFemalesByChildren.get(numberOfChildren)) {

                // If enough mothers have been separated then break
                if(count >= dC.getDeterminedCount()) {
                    break;
                }

                // else mark partnership for separation
                // TODO make this a date between now and next partnership - post stage?
                p.getLastPartnership().separate(p.getLastChild().getBirthDate_ex(), new CompoundTimeUnit(1, TimeUnit.MONTH));

                // TODO move next two lines of code into above method call?
                p.willSeparate(true);
                p.getLastChild().getParentsPartnership_ex().getMalePartner().willSeparate(true);
                count++;

            }

            // Return achieved statistics to the statistics handler
            population.getPopulationCounts().partnershipEnd(count);
            dC.setFufilledCount(count);
            desiredPopulationStatistics.returnAchievedCount(dC);

        }
    }
}
