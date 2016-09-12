package model.simulationLogic;

import datastructure.population.PeopleCollection;
import datastructure.population.exceptions.InsufficientNumberOfPeopleException;
import datastructure.summativeStatistics.desired.PopulationStatistics;
import datastructure.summativeStatistics.structure.DataKey;
import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import model.IPerson;
import utils.time.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PartneringLogic {



    public static void handlePartnering(PopulationStatistics desiredPopulationStatistics, Date currentTime, ArrayList<IPerson> mothersNeedingPartners, int mothersAge, PeopleCollection people) throws UnsupportedDateConversion {

        // TODO implement partnering - part 2

        // decide on new fathers
        // NUMBER_OF_FATHERS_NEEDED = MOTHERS_NEEEDING_FATHERS.size()
        // DATA - get age difference of parents at childs birth distribution (this is a subset/row of an ages in combination table)


        OneDimensionDataDistribution ageDistribution = desiredPopulationStatistics.getPartneringRates(currentTime).getData(mothersAge);

        int fathersNeeded = mothersNeedingPartners.size();

        ArrayList<IPerson> fathers = new ArrayList<>(mothersNeedingPartners.size());

        ArrayList<IntegerRange> ageRanges = new ArrayList<>(ageDistribution.getData().keySet());

        PriorityQueue<AgeRangeToValue> exactValues = new PriorityQueue<AgeRangeToValue>(ageRanges.size(), new RemainderComparator());
        int fathersCount = 0;

        for(IntegerRange aR: ageRanges) {

            DataKey key = new DataKey(mothersAge, aR.getValue(), null, fathersNeeded);
            double exactFathersFromAgeBracket = desiredPopulationStatistics.getPartneringRates(currentTime).getCorrectingData(key) * fathersNeeded;
            fathersCount += (int) exactFathersFromAgeBracket;
            exactValues.add(new AgeRangeToValue(aR, exactFathersFromAgeBracket));

        }

        while(exactValues.size() > 0) {

            AgeRangeToValue aRTV = exactValues.poll();
            int numMen = (int) aRTV.getValue();

            if(fathersCount < fathersNeeded) {
                numMen++;
                fathersCount++;
            }

            fathers.addAll(getNMenFromAgeRange(numMen, aRTV.getAgeRange()));

        }

//        for(IntegerRange ageRange : ageRanges) {
//
//            DataKey key = new DataKey(mothersAge, ageRange.getValue(), null, fathersNeeded);
//
//            double exactFathersFromAgeBracket = desiredPopulationStatistics.getPartneringRates(currentTime).getCorrectingData(key) * fathersNeeded;
//            int fathersFromAgeBracket = (int) exactFathersFromAgeBracket;
//
//            Date start = currentTime.getDateClock().advanceTime(new CompoundTimeUnit(ageRange.getMax(), TimeUnit.YEAR).negative());
//            Date end = currentTime.getDateClock().advanceTime(new CompoundTimeUnit(ageRange.getMin(), TimeUnit.YEAR).negative());
//
//            int yearsInAgeBracket = ageRange.getMax() - ageRange.getMin() + 1;
//
//            int fathersPerYear = (fathersFromAgeBracket / yearsInAgeBracket) + 1;
//
//            System.out.println(fathersFromAgeBracket + " (" + exactFathersFromAgeBracket + ")    // " + yearsInAgeBracket + " = " + fathersPerYear);
//
//            int yearCount = 1;
//
//            int fathersCount = 0;
//
//
//            // Turn distribution into solid values based on the number of fathers required
//            // select fathers and add to NEW_FATHERS
//            for(DateClock yob = start.getDateClock(); DateUtils.dateBefore(yob, end); yob = yob.advanceTime(1, TimeUnit.YEAR)) {
//
//                boolean applied = false;
//                fathersPerYear = (fathersFromAgeBracket / yearsInAgeBracket) + 1;
//
//                while(!applied && fathersPerYear > 0) {
//
//                    if (yearCount == yearsInAgeBracket) {
//                        fathersPerYear = fathersFromAgeBracket - fathers.size();
//                    }
//
//                    try {
//                        fathers.addAll(people.getMales().removeNPersons(fathersPerYear, yob));
//                        fathersCount += fathersPerYear;
//                        yearCount++;
//                        applied = true;
//
//                    } catch (InsufficientNumberOfPeopleException e) {
//                        fathersPerYear--;
//                    }
//
//
//                }
//
//            }

//            desiredPopulationStatistics.getPartneringRates(currentTime).returnAppliedData(key, fathersCount / (double) fathersNeeded);

//        }


        // TODO NEXT - How are we getting here with no men

        System.out.println(fathers.size() + " / " + mothersNeedingPartners.size());

        // pair up MOTHERS_NEEDING_FATHERS with NEW_FATHERS
        for(int p = 0; p < mothersNeedingPartners.size(); p++) {
            IPerson father = fathers.get(p);
            // update new children info to give fathers
            mothersNeedingPartners.get(p).getLastChild().getParentsPartnership().setFather(father);
            people.addPerson(father);
        }


        // THIS IS NOW ASSIGNED ELSEWHERE? - find appropriate birth date for child


    }

    private static Collection<IPerson> getNMenFromAgeRange(int numMen, IntegerRange ageRange) {

        // TODO NEXT - fill me in!

        return null;
    }

}
