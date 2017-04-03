package model.simulationLogic.stochastic;

import datastructure.population.PeopleCollection;
import datastructure.population.exceptions.InsufficientNumberOfPeopleException;
import datastructure.summativeStatistics.desired.PopulationStatistics;
import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.SelfCorrectingOneDimensionDataDistribution;
import datastructure.summativeStatistics.structure.SelfCorrectingTwoDimensionDataDistribution;
import model.simulationEntities.IPartnership;
import model.simulationEntities.IPerson;
import model.simulationLogic.Simulation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.time.Date;
import verify.Verify;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PartneringStochastic {

    public static Random random = new Random();

    public static Logger log = LogManager.getLogger(PartneringStochastic.class);
    // The purpose of this class is to:
        // Take in a population
        // A subset of females from that population who have recently 'given birth'
        // To assign to each of of these females a male of suitable age


    public static Collection<IPartnership> handlePartnering(Collection<IPartnership> partnershipsNeedingFathers, PopulationStatistics desiredPopulationStatistics, Date currentTime, PeopleCollection population) throws ValueNotInRangesException, InsufficientNumberOfPeopleException {

        // Get the female age ranges to sort into and split females into these

        SelfCorrectingTwoDimensionDataDistribution allRates = desiredPopulationStatistics.getPartneringRates(currentTime);

        ArrayList<IntegerRange> femaleAgeRanges = SharedNewLogic.getIntegerRangesInOrder(allRates);
        Map<IntegerRange, Collection<IPartnership>> femalesByAgeRange = sortPartnershipsByFemaleAge(partnershipsNeedingFathers, currentTime, femaleAgeRanges);

        for(IntegerRange ageRange : femaleAgeRanges) {
            SelfCorrectingOneDimensionDataDistribution ratesForAge = allRates.getData(ageRange.getValue());

            Collection<IPartnership> partnershipsOfAgeRange = femalesByAgeRange.get(ageRange);

            if (partnershipsOfAgeRange != null) {
                LinkedList<IPerson> chosenMales = new LinkedList<>();
                Map<IntegerRange, Integer> chosenValues = BinomialApproach.chooseValues(partnershipsOfAgeRange.size(), ratesForAge.getData(), random);

                for (IntegerRange maleAgeRange : chosenValues.keySet()) {
                    Integer numberOfMalesFromAgeRange = chosenValues.get(maleAgeRange);

                    LinkedList<IPerson> chosenMalesFromAgeRange = new LinkedList<>();
                    try {
                        chosenMalesFromAgeRange.addAll(SharedNewLogic.chooseNFromCollection(numberOfMalesFromAgeRange, population.getMales().getByAgeRange(ageRange, currentTime), random, log));
                    } catch (InsufficientNumberOfPeopleException e)  {
                        chosenMalesFromAgeRange.addAll(e.getAvailaiblePeople());
                    }

                    if (chosenMalesFromAgeRange.size() < numberOfMalesFromAgeRange) {
                        // TODO handle this shortage issue
                        Collection<IPerson> men = population.getMales().getByAgeRange(new IntegerRange(15, 100), currentTime);
                        if (men.size() < numberOfMalesFromAgeRange) {
                            throw new InsufficientNumberOfPeopleException(currentTime.toString() + " - Not enough men for partnering");
                        }
                        chosenMalesFromAgeRange.addAll(SharedNewLogic.chooseNFromCollection(numberOfMalesFromAgeRange - chosenMalesFromAgeRange.size(), men, random, log));
                    }

                    chosenMales.addAll(chosenMalesFromAgeRange);
                }

                for (IPartnership partnership : partnershipsOfAgeRange) {
                    IPerson father = chosenMales.removeFirst();
                    partnership.setFather(father);
                    father.recordPartnership(partnership);
                    Simulation.pc.newPartnership();
                }
            }
        }

        return partnershipsNeedingFathers;
    }



    public static Map<IntegerRange, Collection<IPartnership>> sortPartnershipsByFemaleAge(Collection<IPartnership> partnershipsNeedingFathers, Date currentTime, ArrayList<IntegerRange> femaleAgeRanges) throws ValueNotInRangesException {
        Map<IntegerRange, Collection<IPartnership>> femalesByAgeRange = new HashMap<>();

        for(IPartnership partnership : partnershipsNeedingFathers) {
            IntegerRange mothersAgeRange = getMothersAgeRange(currentTime, femaleAgeRanges, partnership);

            try {
                femalesByAgeRange.get(mothersAgeRange).add(partnership);
            } catch (NullPointerException e) {
                femalesByAgeRange.put(mothersAgeRange, new ArrayList<>());
                femalesByAgeRange.get(mothersAgeRange).add(partnership);
            }

        }
        return femalesByAgeRange;
    }

    public static IntegerRange getMothersAgeRange(Date currentTime, ArrayList<IntegerRange> femaleAgeRanges, IPartnership partnership) throws ValueNotInRangesException {
        IPerson mother = partnership.getFemalePartner();
        int mothersAge = mother.ageOnDate(currentTime);
        IntegerRange range = getRangeContainingValue(mothersAge, femaleAgeRanges);
        return range;
    }

    private static IntegerRange getRangeContainingValue(int value, ArrayList<IntegerRange> ranges) throws ValueNotInRangesException {

        for(IntegerRange range : ranges) {
            if(range.contains(value)) {
                return range;
            }
        }

        throw new ValueNotInRangesException("Specified value not in specified set of ranges - likely arrising from input distributions allowing birth to occur at an age where female partnering is not permitted");
    }


}
