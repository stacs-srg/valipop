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
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.SeparationStatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.Population;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SeparationLogic {

    public static void handle(Map<Integer, ArrayList<IPerson>> continuingPartnedFemalesByChildren,
                              CompoundTimeUnit consideredTimePeriod, Date currentDate, PopulationStatistics desiredPopulationStatistics, Population population) {

        for(Integer numberOfChildren : continuingPartnedFemalesByChildren.keySet()) {

            ArrayList<IPerson> mothers = continuingPartnedFemalesByChildren.get(numberOfChildren);

            SeparationStatsKey key = new SeparationStatsKey(numberOfChildren, mothers.size(), consideredTimePeriod, currentDate);

            SingleDeterminedCount dC = (SingleDeterminedCount) desiredPopulationStatistics.getDeterminedCount(key);

            int count = 0;

            for(IPerson p : continuingPartnedFemalesByChildren.get(numberOfChildren)) {

                if(count >= dC.getDeterminedCount()) {
                    break;
                }

                p.willSeparate(false);
                p.getLastChild().getParentsPartnership().getMalePartner().willSeparate(false);
                count++;

            }

            population.getPopulationCounts().partnershipEnd(count);
            dC.setFufilledCount(count);
            desiredPopulationStatistics.returnAchievedCount(dC);

        }
    }
}
