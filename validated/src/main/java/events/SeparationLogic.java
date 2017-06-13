package events;

import dateModel.Date;
import dateModel.timeSteps.CompoundTimeUnit;
import populationStatistics.dataDistributionTables.determinedCounts.SingleDeterminedCount;
import populationStatistics.dataDistributionTables.statsKeys.SeparationStatsKey;
import populationStatistics.recording.PopulationStatistics;
import simulationEntities.person.IPerson;
import simulationEntities.population.dataStructure.Population;

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
