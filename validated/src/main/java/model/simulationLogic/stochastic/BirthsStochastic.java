package model.simulationLogic.stochastic;

import datastructure.population.FemaleCollection;
import datastructure.population.PeopleCollection;
import datastructure.population.exceptions.InsufficientNumberOfPeopleException;
import datastructure.summativeStatistics.desired.PopulationStatistics;
import datastructure.summativeStatistics.structure.DataKey;
import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.SelfCorrectingOneDimensionDataDistribution;
import datastructure.summativeStatistics.structure.SelfCorrectingTwoDimensionDataDistribution;
import model.simulationEntities.IPartnership;
import model.simulationEntities.IPerson;
import model.simulationEntities.EntityFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.time.*;
import utils.time.Date;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class BirthsStochastic {

    public static Logger log = LogManager.getLogger(BirthsStochastic.class);

    public static Random random = new Random();

    // The purpose of this class is to:
        // take in a population
        // select mothers of the correct age and order to give birth
        // TODO Handle multiple births
        // create new children for each birth (these should have no fathers)
        // TODO If a shortage of females exist to mother children then these should be kept over to the next cohort



    public static Collection<IPartnership> handleBirths(FemaleCollection females, PopulationStatistics desired,
                                                        DateClock currentDate, CompoundTimeUnit birthTimeStep,
                                                        PeopleCollection population, CompoundTimeUnit eventTimeStep)
                                                        throws InsufficientNumberOfPeopleException {

        Collection<IPartnership> selectedForEvent = new ArrayList<>();

        // for females of each age bound
        SelfCorrectingTwoDimensionDataDistribution ratesTable = desired.getOrderedBirthRates(currentDate);
        ArrayList<IntegerRange> ageRanges = SharedNewLogic.getIntegerRangesInOrder(ratesTable);

        for(IntegerRange ageRange : ageRanges) {

            SelfCorrectingOneDimensionDataDistribution tableRow = ratesTable.getData(ageRange.getValue());
            Map<IntegerRange, Collection<IPerson>> femalesByOrders = new HashMap<>();
            int femalesOfAge = 0;

            // for each order
            ArrayList<IntegerRange> orders = SharedNewLogic.getIntegerRangesInOrder(tableRow);

            for(IntegerRange order : orders) {
                // get females to be mothers by rate for order by in age range
                Collection<IPerson> femalesOfAgeAndOrder = population.getFemales().getByAgeRangeAndOrder(ageRange, order, currentDate);
                femalesOfAge += femalesOfAgeAndOrder.size();
                femalesByOrders.put(order, femalesOfAgeAndOrder);
            }

            for(IntegerRange order : orders) {
                // get females to be mothers by rate for order by in age range
                Collection<IPerson> femalesOfAgeAndOrder = femalesByOrders.get(order);

                DataKey key = new DataKey(ageRange.getValue(), order.getValue(), orders.get(orders.size() - 1).getMax(), femalesOfAgeAndOrder.size());

                double rate = tableRow.getData(order.getValue()) * eventTimeStep.toDecimalRepresentation();
//                double rate = tableRow.getCorrectingData(key) * eventTimeStep.toDecimalRepresentation();

                int eventOccursNTimes = BernoulliApproach.chooseValue(femalesOfAge, rate, random);
//                int eventOccursNTimes = CalculatedApproach.chooseValue(femalesOfAge, rate);

                Collection<IPerson> mothersToBe;
                try {
                    mothersToBe = removeNPeople(eventOccursNTimes, femalesOfAgeAndOrder);
                } catch (InsufficientNumberOfPeopleException e) {
                    log.info(e.getMessage() + ": Current Date " + currentDate.toString() + " Age Range " + ageRange.toString() + " Order " + order);
                    mothersToBe = femalesOfAgeAndOrder;
                    Collection<IPerson> women = population.getFemales().getByAgeRangeAndOrder(new IntegerRange(15, 45), new IntegerRange(0, true), currentDate);
                    if(women.size() < eventOccursNTimes) {
                        throw new InsufficientNumberOfPeopleException(currentDate.toString() + " - Not enough women for births");
                    }
                    mothersToBe.addAll(SharedNewLogic.chooseNFromCollection(eventOccursNTimes - femalesOfAgeAndOrder.size(), women, random, log));
//                    key = new DataKey(key.getYLabel(), key.getXLabel(), key.getMaxXLabel(), )
                }


                tableRow.returnAppliedData(key, calculateBirthRate(mothersToBe.size(), femalesOfAgeAndOrder.size(), eventTimeStep));

                int seSize = selectedForEvent.size();
                int mSize = mothersToBe.size();
                int exp = seSize + mSize;

                for(IPerson mother : mothersToBe) {
                    selectedForEvent.add(EntityFactory.formNewPartnership(1, mother, currentDate, birthTimeStep, population));
                }

            }
        }

        log.info("Births handled: " + currentDate.toString() + " - " + selectedForEvent.size());

        return selectedForEvent;

    }

    private static double calculateBirthRate(int mothersToBe, int cohortSize, CompoundTimeUnit eventTimeStep) {
        if(cohortSize != 0) {
            return (mothersToBe / (double) cohortSize) / eventTimeStep.toDecimalRepresentation();
        } else {
            return 0;
        }
    }

    private static Collection<IPerson> removeNPeople(int nTimes, Collection<IPerson> people) throws InsufficientNumberOfPeopleException {

        ArrayList<IPerson> peopleAL = new ArrayList<>(people);

        Collection<IPerson> removed = new ArrayList<>();

        for(int i = 0; i < nTimes; i++) {

            if(peopleAL.size() == 0) {
                throw new InsufficientNumberOfPeopleException("Shortage of females to make into mothers (" + removed.size() + "/" + nTimes + ") ");
            }

            removed.add(peopleAL.remove(random.nextInt(peopleAL.size())));
        }

        return removed;
    }







}
