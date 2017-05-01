package events.birth;

import config.Config;
import dateModel.Date;
import dateModel.DateUtils;
import dateModel.dateImplementations.AdvancableDate;
import dateModel.dateImplementations.ExactDate;
import dateModel.timeSteps.CompoundTimeUnit;
import events.EventLogic;
import events.SeparationLogic;
import events.init.InitLogic;
import populationStatistics.dataDistributionTables.determinedCounts.DeterminedCount;
import populationStatistics.dataDistributionTables.determinedCounts.MultipleDeterminedCount;
import populationStatistics.dataDistributionTables.determinedCounts.SingleDeterminedCount;
import populationStatistics.dataDistributionTables.statsKeys.BirthStatsKey;
import populationStatistics.dataDistributionTables.statsKeys.MultipleBirthStatsKey;
import populationStatistics.recording.PopulationStatistics;
import simulationEntities.person.IPerson;
import simulationEntities.population.dataStructure.FemaleCollection;
import simulationEntities.population.dataStructure.Population;
import simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;
import utils.specialTypes.IntegerRangeToIntegerSet;
import utils.specialTypes.LabeledValueSet;
import utils.specialTypes.integerRange.IntegerRange;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NBirthLogic implements EventLogic {

    public static int tBirths = 0;

    @Override
    public void handleEvent(Config config, AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod,
                            Population population, PopulationStatistics desiredPopulationStatistics) throws InsufficientNumberOfPeopleException {

        int bornAtTS = 0;

        FemaleCollection femalesLiving = population.getLivingPeople().getFemales();
        Iterator<AdvancableDate> divDates = femalesLiving.getDivisionDates(consideredTimePeriod).iterator();

        AdvancableDate divDate;
        // For each division in the population data store upto the current date
        while(divDates.hasNext() && DateUtils.dateBeforeOrEqual(divDate = divDates.next(), currentDate)) {

            int age = DateUtils.differenceInYears(divDate.advanceTime(consideredTimePeriod), currentDate).getCount();
//            Collection<IPerson> needingPartners = new ArrayList<>();

            Set<Integer> orders = femalesLiving.getBirthOrdersInDivision(divDate, consideredTimePeriod);

//            int totalFromDiv = 0;
//
//            for(Integer order : orders) {
//                Collection<IPerson> people = femalesLiving.getByDatePeriodAndBirthOrder(divDate, consideredTimePeriod, order);
//                totalFromDiv = people.size();
//            }

            for(Integer order : orders) {

                Collection<IPerson> people = femalesLiving.getByDatePeriodAndBirthOrder(divDate, consideredTimePeriod, order);
                int number = people.size();

                BirthStatsKey key = new BirthStatsKey(age, order, number, consideredTimePeriod, currentDate);
                SingleDeterminedCount determinedCount = (SingleDeterminedCount) desiredPopulationStatistics.getDeterminedCount(key);

                int numberOfChildren = determinedCount.getDeterminedCount();

                // Make women into mothers

                MotherSet mothers = selectMothers(config, people, numberOfChildren, desiredPopulationStatistics,
                        currentDate, consideredTimePeriod, population);

//                needingPartners.addAll(mothers.getNeedPartners());

                int childrenMade = mothers.getNewlyProducedChildren();

                bornAtTS += childrenMade;
                InitLogic.incrementBirthCount(childrenMade);

                determinedCount.setFufilledCount(childrenMade);
                desiredPopulationStatistics.returnAchievedCount(determinedCount);

            }
            // Partner females of age who don't have partners
//            PartneringLogic.handle(needingPartners);

        }

        tBirths += bornAtTS;
        System.out.print(bornAtTS + "\t");

    }

    private MotherSet selectMothers(Config config, Collection<IPerson> females, int numberOfChildren,
                                    PopulationStatistics desiredPopulationStatistics, AdvancableDate currentDate,
                                    CompoundTimeUnit consideredTimePeriod, Population population) throws InsufficientNumberOfPeopleException {

        Collection<IPerson> needPartners = new ArrayList<>();
        Collection<IPerson> havePartners = new ArrayList<>();

        if(females.size() == 0) {
            return new MotherSet(havePartners, needPartners);
        }

        ArrayList<IPerson> femalesAL = new ArrayList<>(females);

        int ageOfMothers = femalesAL.get(0).ageOnDate(currentDate);

        MultipleDeterminedCount requiredBirths = calcNumberOfPreganciesOfMultipleBirth(ageOfMothers, numberOfChildren,
                desiredPopulationStatistics, currentDate, consideredTimePeriod);

        int childrenMade = 0;

        int numberOfMothers = requiredBirths.getDeterminedCount().productOfLabelsAndValues().getSumOfValues();

        Map<Integer, ArrayList<IPerson>> continuingPartnedFemalesByChildren = new HashMap<>();

        LabeledValueSet<IntegerRange, Integer> motherCountsByMaternities =
                new IntegerRangeToIntegerSet(requiredBirths.getDeterminedCount().getLabels(), 0);

        LabeledValueSet<IntegerRange, Integer> remainingMothersToFind = requiredBirths.getDeterminedCount().clone();

//        for(IPerson f : femalesAL) {
//
//            if(childrenMade >= numberOfMothers) {
//                break;
//            }
//
//            if(eligible(f, config, currentDate)) {
//                f.giveChildren(1, currentDate, consideredTimePeriod, population);
//                childrenMade ++;
//                needPartners.add(f);
//                try {
//                    continuingPartnedFemalesByChildren.get(1).add(f);
//                } catch (NullPointerException e) {
//                    continuingPartnedFemalesByChildren.put(1, new ArrayList<>(Collections.singleton(f)));
//                }
//            }
//
//        }

//        motherCountsByMaternities.update(new IntegerRange(1), childrenMade);


        // break

        IntegerRange highestBirthOption;

        try {
            highestBirthOption = remainingMothersToFind.getLargestLabelOfNoneZeroValue();
        } catch (NoSuchElementException e) {
            return new MotherSet(havePartners, needPartners);
        }



        for(IPerson female : femalesAL) {

            if(childrenMade >= numberOfChildren) {
                break;
            }

            if(eligible(female, config, currentDate)) {
                female.giveChildren(highestBirthOption.getValue(), currentDate, consideredTimePeriod, population);
                female.getLastChild().getParentsPartnership().setFather(BirthLogic.getRandomFather(population, population.getLivingPeople().resolveDateToCorrectDivisionDate(female.getBirthDate()), consideredTimePeriod));
                childrenMade += highestBirthOption.getValue();

                if(female.needsPartner(currentDate)) {
                    needPartners.add(female);
                } else {
                    havePartners.add(female);

                    try {
                        continuingPartnedFemalesByChildren.get(female.numberOfChildrenInLatestPartnership()).add(female);
                    } catch (NullPointerException e) {
                        continuingPartnedFemalesByChildren.put(female.numberOfChildrenInLatestPartnership(), new ArrayList<>(Collections.singleton(female)));
                    }
                }

                // updates count of remaining mothers to find
                int furtherMothersNeededForMaternitySize = remainingMothersToFind.get(highestBirthOption) - 1;
                remainingMothersToFind.update(highestBirthOption, furtherMothersNeededForMaternitySize);

                // updates count of mother found
                motherCountsByMaternities
                        .update(highestBirthOption, motherCountsByMaternities.getValue(highestBirthOption) + 1);

                if(furtherMothersNeededForMaternitySize <= 0) {
                    try {
                        highestBirthOption = remainingMothersToFind.getLargestLabelOfNoneZeroValue();
                    } catch (NoSuchElementException e) {
                        // In this case we have created all the new mothers and children required
                        break;
                    }

                }
            }
        }

        SeparationLogic.handle(continuingPartnedFemalesByChildren);

        requiredBirths.setFufilledCount(motherCountsByMaternities);
        desiredPopulationStatistics.returnAchievedCount(requiredBirths);

        return new MotherSet(havePartners, needPartners, childrenMade);

    }

    private MultipleDeterminedCount calcNumberOfPreganciesOfMultipleBirth(int ageOfMothers, int numberOfChildren, PopulationStatistics desiredPopulationStatistics, AdvancableDate currentDate,
                                                                          CompoundTimeUnit consideredTimePeriod) {

        MultipleBirthStatsKey key = new MultipleBirthStatsKey(ageOfMothers, numberOfChildren, consideredTimePeriod, currentDate);
        return (MultipleDeterminedCount) desiredPopulationStatistics.getDeterminedCount(key);

    }

    private boolean eligible(IPerson potentialMother, Config config, Date currentDate) {

        IPerson lastChild = potentialMother.getLastChild();

        if(lastChild != null) {
            ExactDate earliestDateOfNextChild = DateUtils.
                    calculateExactDate(lastChild.getBirthDate(), config.getMinBirthSpacing());

            // Returns true if last child was born far enough in the past for another child to be born at currentDate
            return DateUtils.dateBefore(earliestDateOfNextChild, currentDate);
        } else {
            // i.e. there is no previous child and thus no limitation to birth
            return true;
        }
    }
}
