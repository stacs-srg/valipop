package model.simulationLogic;

import config.Config;
import datastructure.population.PeopleCollection;
import datastructure.summativeStatistics.desired.PopulationStatistics;
import datastructure.summativeStatistics.structure.DataKey;
import datastructure.summativeStatistics.structure.SelfCorrectingOneDimensionDataDistribution;
import model.simulationEntities.IPartnership;
import model.simulationEntities.IPerson;
import model.simulationEntities.PersonNotAliveException;
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

    public static Collection<IPartnership> handleSeparationOLD(PopulationStatistics desiredPopulationStatistics, Date currentTime, Collection<IPerson> mothersNeedingProcessed, PeopleCollection people, Config config) {



        ArrayList<IPerson> toBeProcessed = new ArrayList<>(mothersNeedingProcessed);

        int partnershipCount = Simulation.pc.getCurrentPartnerships();

        List<IPartnership> mothersNeedingPartners = new ArrayList<>();

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

        Collection<IPerson> processedMothers = new ArrayList<>();

        for(IPerson m : toBeProcessed) {
            int n = m.numberOfChildrenFatheredChildren();
            if(n == 0) {
                // If mothers first child then can be no previous father to consider = THIS IS WHERE THE LOST PARTNERSHIP ISSUE IS - TASK 2
                mothersNeedingPartners.add(m.getLastChild().getParentsPartnership());
                processedMothers.add(m);
            } else {
                if (n > separationRates.getLargestLabel().getValue()) {
                    n = separationRates.getLargestLabel().getValue();
                }
                childCounts[n - 1]++;
            }
        }

        toBeProcessed.removeAll(processedMothers);

        double[] separationCounts = new double[childCounts.length];

        for(int i = 0; i < separationCounts.length; i++) {
            // calculate number to be separated (note: is based upon number of partnerships in the population, not just locally)
            separationCounts[i] = (int) (separationRates.getCorrectingData(keys[i]) * partnershipCount * config.getBirthTimeStep().toDecimalRepresentation()) + 1;
            if(separationCounts[i] > childCounts[i]) {
                log.info("Not enough mothers in group to separate");
                separationCounts[i] = childCounts[i];
                separationRates.returnAppliedData(keys[i], (separationCounts[i] / (double) partnershipCount) / config.getBirthTimeStep().toDecimalRepresentation());
            } else {
                separationRates.returnAppliedData(keys[i], (separationCounts[i] / (double) partnershipCount) / config.getBirthTimeStep().toDecimalRepresentation());
            }
        }

        while(toBeProcessed.size() != 0) {

            IPerson m = toBeProcessed.get(0);

            int n = m.numberOfChildren();
            if(n > separationRates.getLargestLabel().getValue()) {
                n = separationRates.getLargestLabel().getValue();
            }

            // select mothers to separate with fathers and add to MOTHERS_NEEDING_FATHERS
            if(childCounts[n-1] > 0) {
                childCounts[n-1]--;
                mothersNeedingPartners.add(m.getLastChild().getParentsPartnership());
                toBeProcessed.remove(m);
                Simulation.pc.partnershipEnd();
            } else {
                // add the rest to MOTHERS_WITH_FATHERS
                try {
                    m.keepPreviousFatherForChild(m.getLastChild().getParentsPartnership(), people);
                } catch (PersonNotAliveException e) {
                    // This ending will have been counted at death of the male
                    mothersNeedingPartners.add(m.getLastChild().getParentsPartnership());
                    childCounts[n-1]++;
                }

                toBeProcessed.remove(m);
            }

        }

        return mothersNeedingPartners;

    }


    public static Collection<IPartnership> handleSeparation(PopulationStatistics desiredPopulationStatistics, Date currentTime, Collection<IPartnership> mothersNeedingProcessed, PeopleCollection people, Config config) {



        ArrayList<IPartnership> toBeProcessed = new ArrayList<>(mothersNeedingProcessed);

        int partnershipCount = Simulation.pc.getCurrentPartnerships();

        List<IPartnership> mothersNeedingPartners = new ArrayList<>();

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

        Collection<IPartnership> processedMothers = new ArrayList<>();

        for(IPartnership partnership : toBeProcessed) {

            IPerson m = partnership.getFemalePartner();

            int n = m.numberOfChildrenFatheredChildren();
            if(n == 0) {
                // If mothers first child then can be no previous father to consider = THIS IS WHERE THE LOST PARTNERSHIP ISSUE IS - TASK 2
                mothersNeedingPartners.add(partnership);
                processedMothers.add(partnership);
            } else {
                if (n > separationRates.getLargestLabel().getValue()) {
                    n = separationRates.getLargestLabel().getValue();
                }
                childCounts[n - 1]++;
            }
        }

        toBeProcessed.removeAll(processedMothers);

        double[] separationCounts = new double[childCounts.length];

        for(int i = 0; i < separationCounts.length; i++) {
            // calculate number to be separated (note: is based upon number of partnerships in the population, not just locally)
            separationCounts[i] = (int) (separationRates.getCorrectingData(keys[i]) * partnershipCount * config.getBirthTimeStep().toDecimalRepresentation()) + 1;
            if(separationCounts[i] > childCounts[i]) {
                log.info("Not enough mothers in group to separate");
                separationCounts[i] = childCounts[i];
                separationRates.returnAppliedData(keys[i], (separationCounts[i] / (double) partnershipCount) / config.getBirthTimeStep().toDecimalRepresentation());
            } else {
                separationRates.returnAppliedData(keys[i], (separationCounts[i] / (double) partnershipCount) / config.getBirthTimeStep().toDecimalRepresentation());
            }
        }

        while(toBeProcessed.size() != 0) {

            IPartnership partnership = toBeProcessed.get(0);
            IPerson m = partnership.getFemalePartner();

            int n = m.numberOfChildren();
            if(n > separationRates.getLargestLabel().getValue()) {
                n = separationRates.getLargestLabel().getValue();
            }

            // select mothers to separate with fathers and add to MOTHERS_NEEDING_FATHERS
            if(childCounts[n-1] > 0) {
                childCounts[n-1]--;
                mothersNeedingPartners.add(partnership);
                toBeProcessed.remove(partnership);
                Simulation.pc.partnershipEnd();
            } else {
                // add the rest to MOTHERS_WITH_FATHERS
                try {
                    m.keepPreviousFatherForChild(partnership, people);
                } catch (PersonNotAliveException e) {
                    // This ending will have been counted at death of the male
                    mothersNeedingPartners.add(partnership);
                    childCounts[n-1]++;
                }

                toBeProcessed.remove(partnership);
            }

        }

        return mothersNeedingPartners;

    }

}
