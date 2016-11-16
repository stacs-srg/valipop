package model.simulationLogic;

import datastructure.population.PeopleCollection;
import datastructure.population.exceptions.InsufficientNumberOfPeopleException;
import datastructure.summativeStatistics.desired.PopulationStatistics;
import datastructure.summativeStatistics.structure.AgeRangeWithExactFatherValue;
import datastructure.summativeStatistics.structure.DataKey;
import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import model.simulationEntities.IPartnership;
import model.simulationEntities.IPerson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.RemainderComparator;
import utils.time.*;
import utils.time.Date;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PartneringLogic {

    private static Random random = new Random();
    private static Logger log = LogManager.getLogger(PartneringLogic.class);


    public static void handlePartnering(PopulationStatistics desiredPopulationStatistics, Date currentTime, ArrayList<IPerson> mothersNeedingPartners, int mothersAge, PeopleCollection people) throws UnsupportedDateConversion, InsufficientNumberOfPeopleException {

        // TODO implement partnering - part 2

        // decide on new fathers
        // NUMBER_OF_FATHERS_NEEDED = MOTHERS_NEEEDING_FATHERS.size()
        // DATA - get age difference of parents at childs birth distribution (this is a subset/row of an ages in combination table)


        OneDimensionDataDistribution ageDistribution = desiredPopulationStatistics.getPartneringRates(currentTime).getData(mothersAge);

        int fathersNeeded = mothersNeedingPartners.size();

        ArrayList<IPerson> fathers = new ArrayList<>(fathersNeeded);

        ArrayList<IntegerRange> ageRanges = new ArrayList<>(ageDistribution.getData().keySet());

        PriorityQueue<AgeRangeWithExactFatherValue> availiableRanges = new PriorityQueue<>(ageRanges.size(), new RemainderComparator());
        int projectedFathersCount = 0;

        for(IntegerRange aR: ageRanges) {

            DataKey key = new DataKey(mothersAge, aR.getValue(), null, fathersNeeded);
            double exactFathersFromAgeBracket = desiredPopulationStatistics.getPartneringRates(currentTime).getCorrectingData(key) * fathersNeeded;
            projectedFathersCount += (int) exactFathersFromAgeBracket;
            availiableRanges.add(new AgeRangeWithExactFatherValue(key, aR, exactFathersFromAgeBracket));

        }

        int fufilledFathersCount = 0;

        // these still hold fathers until the
        ArrayList<AgeRangeWithExactFatherValue> emptyRanges = new ArrayList<>();

        // start of loop ?
        while (fufilledFathersCount < fathersNeeded) {

            if(availiableRanges.size() == 0) {
                // TODO - we should add the fathers be moved from the population before we throw - these will be in both emptyRanges and availiableRanges

                Date w_yob = currentTime.getDateClock().advanceTime(
                        new CompoundTimeUnit(mothersAge, TimeUnit.YEAR).negative());

                int menInPop = people.getMales().getNumberOfPersons();
                int womenInPop = people.getFemales().getNumberOfPersons();
                int womenOfAge = people.getFemales().getByYear(w_yob).size();



                throw new InsufficientNumberOfPeopleException("Not enough males to meet partner with mother cohort of "
                        + w_yob.getYear() + " \nWomen of age : " + womenOfAge + " \nWomen in pop: " + womenInPop + " \nMen in pop : " + menInPop);
            }

            PriorityQueue<AgeRangeWithExactFatherValue> usedRanges = new PriorityQueue<>(ageRanges.size(), new RemainderComparator());

            while (availiableRanges.size() > 0) {

                AgeRangeWithExactFatherValue aRTV = availiableRanges.poll();
                int numMen = (int) aRTV.getValue();

                if (projectedFathersCount < fathersNeeded) {
                    numMen++;
                    projectedFathersCount++;
                }

                Collection<IPerson> men = getNMenFromAgeRange(numMen, aRTV.getAgeRange(), currentTime, people);
                fufilledFathersCount += men.size();


//            if(men.size() < numMen) {
//                int shortfall = numMen - men.size();
//                fathersCount -= shortfall;
//            }

                aRTV.addFathers(men);
                usedRanges.add(aRTV);

            }

            ArrayList<AgeRangeWithExactFatherValue> usableRanges = new ArrayList<>();

            double sumOfUsableExactValues = 0;


//            System.out.println("ffc = " + fufilledFathersCount);
//            System.out.println("fn = " + fathersNeeded);

            while (usedRanges.size() > 0) {

                AgeRangeWithExactFatherValue aRTV = usedRanges.poll();

                if(fathersNeeded > fufilledFathersCount) {

                    if (aRTV.getValue() < (aRTV.getFathers().size() - aRTV.getCarriedFathers())) {
                        // The remainder in this range has already been rounded up and we have no reason to expect the range has
                        // been exhausted of men
//                    System.out.println(aRTV.rowAsString());
                        usableRanges.add(aRTV);
                    } else if ((int) aRTV.getValue() > (aRTV.getFathers().size() - aRTV.getCarriedFathers())) {
                        // In the previous round of finding men this range returned fewer men than required and therefore we can
                        // know that there are no men left in this range
//                        System.out.println("eR: " + currentTime.rowAsString() + " " + aRTV.rowAsString());
                        emptyRanges.add(aRTV);
                    } else {
                        // i.e. this ranges remainder hasn't been rounded up and to the best of our knowledge there are still
                        // men in this range
                        Collection<IPerson> man = getNMenFromAgeRange(1, aRTV.getAgeRange(), currentTime, people);

                        if (man.size() != 1) {
                            // This range is therefore empty
//                            System.out.println("eR: " + currentTime.rowAsString() + " " + aRTV.rowAsString());
                            emptyRanges.add(aRTV);
                        } else {
                            aRTV.addFathers(man);
                            fufilledFathersCount++;
//                        System.out.println(aRTV.rowAsString());
                            usableRanges.add(aRTV);
                            sumOfUsableExactValues += aRTV.getValue();
                        }

                    }

                } else {
                    // We've got all the fathers we need and so all we're doing in this loop is moving any last ranges
                    // from usedRanges to availiable ranges so that the range gets processed and thre selcted fathers
                    // added to the population
                    availiableRanges.add(aRTV);
                }

            }

            // Write repopulation of queue code here
            for(AgeRangeWithExactFatherValue range : usableRanges) {
//                System.out.println("uR: " + currentTime.rowAsString() + " " + range.rowAsString());
                range.updateCarriedFathers();
                double existingExactValue = range.getValue();
                int fathersNowNeeded = fathersNeeded - fufilledFathersCount;
                range.setValue(fathersNowNeeded * existingExactValue / sumOfUsableExactValues);
//                System.out.println("uR: " + currentTime.rowAsString() + " " + range.rowAsString());
                availiableRanges.add(range);
            }

        }

        for(AgeRangeWithExactFatherValue range : availiableRanges) {
            Collection<IPerson> men = range.getFathers();
            fathers.addAll(men);

            desiredPopulationStatistics.getPartneringRates(currentTime).returnAppliedData(range.getKey(), men.size() / (double) fathersNeeded);
        }

        for(AgeRangeWithExactFatherValue range : emptyRanges) {
            Collection<IPerson> men = range.getFathers();
            fathers.addAll(men);

            desiredPopulationStatistics.getPartneringRates(currentTime).returnAppliedData(range.getKey(), men.size() / (double) fathersNeeded);
        }

        // pair up MOTHERS_NEEDING_FATHERS with NEW_FATHERS
        for(int p = 0; p < mothersNeedingPartners.size(); p++) {
            IPerson father = fathers.get(p);
            // update new children info to give fathers
            IPartnership partnership = mothersNeedingPartners.get(p).getLastChild().getParentsPartnership();
            partnership.setFather(father);
            father.recordPartnership(partnership);
            people.addPerson(father);

            if(partnership.getMalePartner() == null) {
                System.out.println("Null Father 2");
            }
        }


        // THIS IS NOW ASSIGNED ELSEWHERE? - find appropriate birth date for child


    }

    private static Collection<IPerson> getNMenFromAgeRange(int numMen, IntegerRange ageRange, Date currentTime, PeopleCollection people) throws UnsupportedDateConversion {


        int yearsInAgeBracket = ageRange.getMax() - ageRange.getMin() + 1;
//        int menOfEachAge = numMen / yearsInAgeBracket;

        Collection<IPerson> fathersToBe = new ArrayList<>();


        Date start = currentTime.getDateClock().advanceTime(new CompoundTimeUnit(ageRange.getMax(), TimeUnit.YEAR).negative());
        Date end = currentTime.getDateClock().advanceTime(new CompoundTimeUnit(ageRange.getMin(), TimeUnit.YEAR).negative());

        boolean[] empty = new boolean[yearsInAgeBracket];
        int index = 0;

        while(numMen - fathersToBe.size() >= countAgesWithMen(empty)) {

            if(countAgesWithMen(empty) == 0) {
                log.info("Not enough men to complete partnering for time period " + start.toString() + " to " + end.toString());
                return fathersToBe;
            }

            int menOfEachAge = numMen / countAgesWithMen(empty);

            if(index >= yearsInAgeBracket) {
                index -= yearsInAgeBracket;
            }

            for (DateClock yob = start.getDateClock(); DateUtils.dateBefore(yob, end); yob = yob.advanceTime(1, TimeUnit.YEAR)) {

                if (!empty[index]) {
                    Collection<IPerson> menOfAge = null;
                    try {
                        menOfAge = people.getMales().removeNPersons(menOfEachAge, yob, true);
                    } catch (InsufficientNumberOfPeopleException e) {
                        throw new RuntimeException("PartneringLogic#getNMenFromAgeRange has reached an unreachable state");
                    }
                    if (menOfAge.size() < menOfEachAge) {
                        empty[index] = true;
                    }
                    fathersToBe.addAll(menOfAge);
                }

                index++;
            }
        }

        // here we are handling the case where numMen / countAgesWithMen(empty) evaluates to zero but there are still
        // the last couple of fathers to find
        while(fathersToBe.size() < numMen) {

            // TODO NEXT - fill me in!

            int agesWithMen = countAgesWithMen(empty);

            if(agesWithMen == 0) {
                log.info("Not enough men to complete partnering for time period " + start.toString() + " to " + end.toString());
                return fathersToBe;
            }

            int randomNth = random.nextInt(agesWithMen) + 1;

            int yearInAgeBracket = getIndexOfNthFalse(randomNth, empty);

            Date yob = start.getDateClock().advanceTime(yearInAgeBracket, TimeUnit.YEAR);

            try {
                fathersToBe.addAll(people.getMales().removeNPersons(1, yob, false));
            } catch (InsufficientNumberOfPeopleException e) {
                empty[yearInAgeBracket] = true;
            }

        }


        return fathersToBe;
    }

    private static int getIndexOfNthFalse(int randomNth, boolean[] array) {

        int index = 0;
        int count = 0;

        for(boolean b : array) {

            if(!b) {
                count ++;
            }

            if(count == randomNth) {
                return index;
            }

            index++;

        }

        throw new NoSuchElementException();
    }

    private static int countAgesWithMen(boolean[] array) {

        int c = 0;

        for(boolean b : array) {
            if(!b) {
                c++;
            }
        }

        return c;
    }

}
