package model.simulationLogic;

import datastructure.population.PeopleCollection;
import datastructure.population.exceptions.InsufficientNumberOfPeopleException;
import datastructure.summativeStatistics.desired.PopulationStatistics;
import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import model.IPerson;
import utils.time.*;

import java.util.ArrayList;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PartneringLogic {



    public static void handlePartnering(PopulationStatistics desiredPopulationStatistics, Date currentTime, ArrayList<IPerson> mothersNeedingPartners, int mothersAge, PeopleCollection people) throws UnsupportedDateConversion, InsufficientNumberOfPeopleException {

        // TODO implement partnering - part 2

        // decide on new fathers
        // NUMBER_OF_FATHERS_NEEDED = MOTHERS_NEEEDING_FATHERS.size()
        // DATA - get age difference of parents at childs birth distribution (this is a subset/row of an ages in combination table)
        OneDimensionDataDistribution ageDistribution = desiredPopulationStatistics.getPartneringRates(currentTime).getData(mothersAge);

        int fathersNeeded = mothersNeedingPartners.size();

        ArrayList<IPerson> fathers = new ArrayList<>(mothersNeedingPartners.size());

        ArrayList<IntegerRange> ageRanges = new ArrayList<>(ageDistribution.getData().keySet());

        for(IntegerRange ageRange : ageRanges) {
            double exactFathersFromAgeBracket = ageDistribution.getData(ageRange.getValue()) * fathersNeeded;
            int fathersFromAgeBracket = (int) exactFathersFromAgeBracket;

            Date start = currentTime.getDateClock().advanceTime(new CompoundTimeUnit(ageRange.getMax(), TimeUnit.YEAR).negative());
            Date end = currentTime.getDateClock().advanceTime(new CompoundTimeUnit(ageRange.getMin(), TimeUnit.YEAR).negative());

            int yearsInAgeBracket = ageRange.getMax() - ageRange.getMin();

            int fathersPerYear = fathersFromAgeBracket / yearsInAgeBracket;


            int yearCount = 1;


            // Turn distribution into solid values based on the number of fathers required
            // select fathers and add to NEW_FATHERS
            for(DateClock yob = start.getDateClock(); DateUtils.dateBefore(yob, end); yob = yob.advanceTime(1, TimeUnit.YEAR)) {

                if(yearCount == yearsInAgeBracket) {
                    fathersPerYear = fathersFromAgeBracket - fathers.size();
                }

                fathers.addAll(people.getMales().removeNPersons(fathersPerYear, yob));
                yearCount++;

            }

        }




        // pair up MOTHERS_NEEDING_FATHERS with NEW_FATHERS
        for(int p = 0; p < mothersNeedingPartners.size(); p++) {
            IPerson father = fathers.get(p);
            // update new children info to give fathers
            mothersNeedingPartners.get(p).getLastChild().getParentsPartnership().setFather(father);
            people.addPerson(father);
        }


        // THIS IS NOW ASSIGNED ELSEWHERE? - find appropriate birth date for child


    }

}
