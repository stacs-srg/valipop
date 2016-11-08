package model.simulationLogic;

import config.Config;
import datastructure.population.PeopleCollection;
import datastructure.summativeStatistics.desired.PopulationStatistics;
import datastructure.summativeStatistics.structure.DataKey;
import datastructure.summativeStatistics.structure.SelfCorrectingOneDimensionDataDistribution;
import model.simulationEntities.IPerson;
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

    public static Collection<IPerson> handleSeparation(PopulationStatistics desiredPopulationStatistics, Date currentTime, List<IPerson> mothersNeedingProcessed, PeopleCollection people, Config config) {

        int partnershipCount = 0;

        for(IPerson p : people.getFemales().getAll()) {

            if(p.aliveOnDate(currentTime) && p.getPartnerships().size() != 0 && !p.isWidow(currentTime)) {
                // inc marriages count
                partnershipCount ++;

            }

        }

        List<IPerson> mothersNeedingPartners = new ArrayList<>();

        // DATA - get rate of separation by number of children had
        SelfCorrectingOneDimensionDataDistribution separationRates = desiredPopulationStatistics.getSeparationByChildCountRates(currentTime);
        DataKey[] keys = new DataKey[separationRates.getLargestLabel().getValue()];

        Integer[] childCounts = new Integer[separationRates.getLargestLabel().getValue()];
        for(int i = 0; i < childCounts.length; i++) {

            // Initialise child counts
            childCounts[i] = 0;

            // Set up data keys
            keys[i] = new DataKey(i+1, partnershipCount);
        }

        // Count number of mothers with each given number of children
        for(IPerson m : mothersNeedingProcessed) {
            int n = m.numberOfChildrenFatheredChildren();
            if(n > separationRates.getLargestLabel().getValue()) {
                n = separationRates.getLargestLabel().getValue();
            }
            childCounts[n-1]++;
        }

        double[] separationCounts = new double[childCounts.length];

        for(int i = 0; i < separationCounts.length; i++) {
            // calculate number to be seperated (note: is based upon number of partnerships in the population, not just locally)
            separationCounts[i] = (int) (separationRates.getCorrectingData(keys[i]) * partnershipCount * config.getBirthTimeStep().toDecimalRepresentation()) + 1;
            if(separationCounts[i] > childCounts[i]) {
                log.info("Not enough mothers in group to separate");
                separationCounts[i] = childCounts[i];
                separationRates.returnAppliedData(keys[i], (separationCounts[i] / (double) partnershipCount) / config.getBirthTimeStep().toDecimalRepresentation());
            } else {
                separationRates.returnAppliedData(keys[i], (separationCounts[i] / (double) partnershipCount) / config.getBirthTimeStep().toDecimalRepresentation());
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
                m.keepFather(people);
                mothersNeedingProcessed.remove(m);
            }
        }

        return mothersNeedingPartners;

    }

}
