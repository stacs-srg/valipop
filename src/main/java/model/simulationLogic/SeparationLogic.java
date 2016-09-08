package model.simulationLogic;

import datastructure.summativeStatistics.desired.PopulationStatistics;
import datastructure.summativeStatistics.structure.DataKey;
import datastructure.summativeStatistics.structure.SelfCorrectingOneDimensionDataDistribution;
import model.IPerson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.time.Date;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SeparationLogic {

    public static Logger log = LogManager.getLogger(SeparationLogic.class);

    public static Collection<IPerson> handleSeparation(PopulationStatistics desiredPopulationStatistics, Date currentTime, int partnershipCount, List<IPerson> mothersNeedingProcessed) {

        List<IPerson> mothersNeedingPartners = new ArrayList<>();

        // DATA - get rate of separation by number of children had
        SelfCorrectingOneDimensionDataDistribution separationRates = desiredPopulationStatistics.getSeparationByChildCountRates(currentTime);
        DataKey[] keys = new DataKey[separationRates.getLargestLabel().getValue()];

        Integer[] childCounts = new Integer[separationRates.getLargestLabel().getValue()];
        for(int i = 0; i < childCounts.length; i++) {
            childCounts[i] = 0;
            keys[i] = new DataKey(i+1, partnershipCount);
        }

        for(IPerson m : mothersNeedingProcessed) {
            int n = m.numberOfChildrenFatheredChildren();
            if(n > separationRates.getLargestLabel().getValue()) {
                n = separationRates.getLargestLabel().getValue();
            }
            childCounts[n-1]++;
        }

        double[] separationCounts = new double[childCounts.length];

        for(int i = 0; i < separationCounts.length; i++) {
            separationCounts[i] = (int) (separationRates.getCorrectingData(keys[i]) * partnershipCount);
            if(separationCounts[i] > childCounts[i]) {
                log.info("Not enough mothers in group to separate");
                separationCounts[i] = childCounts[i];
                separationRates.returnAppliedData(keys[i], separationCounts[i] / (double) partnershipCount);
            }
        }

        while(mothersNeedingProcessed.size() != 0) {

            IPerson m = mothersNeedingProcessed.get(0);

            int n = m.numberOfChildren();
            if(n > separationRates.getLargestLabel().getValue()) {
                n = separationRates.getLargestLabel().getValue();
            }
            // select mothers to separate with fathers and add to MOTHERS_NEEDING_FATHERS
            if(childCounts[n-1] > 0) {
                childCounts[n-1]--;
                mothersNeedingPartners.add(m);
                mothersNeedingProcessed.remove(m);
            } else {
                // add the rest to MOTHERS_WITH_FATHERS
                m.keepFather();

                mothersNeedingProcessed.remove(m);
            }
        }

        return mothersNeedingPartners;

    }

}
