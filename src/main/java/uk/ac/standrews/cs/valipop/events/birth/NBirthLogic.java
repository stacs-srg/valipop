/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop.events.birth;

import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.BirthStatsKey;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRangeToIntegerSet;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.events.EventLogic;
import uk.ac.standrews.cs.valipop.events.birth.partnering.SeparationLogic;
import uk.ac.standrews.cs.valipop.events.init.InitLogic;
import uk.ac.standrews.cs.valipop.events.birth.partnering.PartneringLogic;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.MultipleBirthStatsKey;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.FemaleCollection;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.exceptions.PersonNotFoundException;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.LabeledValueSet;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.IntegerRange;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NBirthLogic implements EventLogic {

    Random rand = new Random();

    private int tBirths = 0;

    @Override
    public int handleEvent(Config config, AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod,
                           Population population, PopulationStatistics desiredPopulationStatistics) throws InsufficientNumberOfPeopleException, PersonNotFoundException {

        int bornAtTS = 0;

        FemaleCollection femalesLiving = population.getLivingPeople().getFemales();
        Iterator<AdvancableDate> divDates = femalesLiving.getDivisionDates(consideredTimePeriod).iterator();

        AdvancableDate divDate;
        // For each division in the population data store upto the current date
        while(divDates.hasNext() && DateUtils.dateBeforeOrEqual(divDate = divDates.next(), currentDate)) {

            int age = DateUtils.differenceInYears(divDate.advanceTime(consideredTimePeriod), currentDate).getCount();
            Collection<NewMother> needingPartners = new ArrayList<>();

            int cohortSize = femalesLiving.getAllPersonsBornInTimePeriod(divDate, consideredTimePeriod).size();

            Set<IntegerRange> inputOrders = desiredPopulationStatistics.getOrderedBirthRates(currentDate).getColumnLabels();

            for(IntegerRange order : inputOrders) {

                Collection<IPersonExtended> people = femalesLiving.getByDatePeriodAndBirthOrder(divDate, consideredTimePeriod, order);

                BirthStatsKey key = new BirthStatsKey(age, order.getValue(), cohortSize, consideredTimePeriod, currentDate);
                SingleDeterminedCount determinedCount =
                        (SingleDeterminedCount) desiredPopulationStatistics.getDeterminedCount(key, config);

                int birthAdjust = 0;
                if(determinedCount.getDeterminedCount() == 0) {
                    birthAdjust = 0;
                } else {
//                    birthAdjust = Integer.parseInt(String.valueOf(Math.round(new Random().nextInt(cohortSize + 1) * config.getBirthFactor())));
//                    birthAdjust = -1;
//                    int bound = Integer.parseInt(String.valueOf(Math.round(1 / config.getBirthFactor())));

                    int adjuster = new Double(Math.ceil(config.getBirthFactor())).intValue();

                    int bound = 1000000;
                    if(rand.nextInt(bound) < Math.abs(config.getBirthFactor() / adjuster) * bound) {

                        if(config.getBirthFactor() < 0) {
                            birthAdjust = adjuster;
                        } else {
                            birthAdjust = -1 * adjuster;
                        }
                    }
                }

                int numberOfChildren = determinedCount.getDeterminedCount() + birthAdjust;

                // Make women into mothers

                MotherSet mothers = selectMothers(config, people, numberOfChildren, desiredPopulationStatistics,
                        currentDate, consideredTimePeriod, population);

                needingPartners.addAll(mothers.getNeedPartners());

                int childrenMade = mothers.getNewlyProducedChildren();

                bornAtTS += childrenMade;
                InitLogic.incrementBirthCount(childrenMade);

                if(childrenMade > birthAdjust) {
                    childrenMade = childrenMade - birthAdjust;
                } else {
                    childrenMade = 0;
                }

                determinedCount.setFufilledCount(childrenMade);
                desiredPopulationStatistics.returnAchievedCount(determinedCount);

            }

            // Partner females of age who don't have partners
            PartneringLogic.handle(needingPartners, desiredPopulationStatistics, currentDate, consideredTimePeriod, population, config);

        }

        tBirths += bornAtTS;

        return bornAtTS;

    }

    @Override
    public int getEventCount() {
        return tBirths;
    }

    @Override
    public void resetEventCount() {
        tBirths = 0;
    }

    private MotherSet selectMothers(Config config, Collection<IPersonExtended> females, int numberOfChildren,
                                    PopulationStatistics desiredPopulationStatistics, AdvancableDate currentDate,
                                    CompoundTimeUnit consideredTimePeriod, Population population) throws InsufficientNumberOfPeopleException {

        Collection<NewMother> needPartners = new ArrayList<>();
        Collection<IPersonExtended> havePartners = new ArrayList<>();

        if(females.size() == 0) {
            return new MotherSet(havePartners, needPartners);
        }

        ArrayList<IPersonExtended> femalesAL = new ArrayList<>(females);

        int ageOfMothers = femalesAL.get(0).ageOnDate(currentDate);

        MultipleDeterminedCount requiredBirths = calcNumberOfPreganciesOfMultipleBirth(ageOfMothers, numberOfChildren,
                desiredPopulationStatistics, currentDate, consideredTimePeriod, config);

        int childrenMade = 0;

        Map<Integer, ArrayList<IPersonExtended>> continuingPartneredFemalesByChildren = new HashMap<>();

        LabeledValueSet<IntegerRange, Integer> motherCountsByMaternities =
                new IntegerRangeToIntegerSet(requiredBirths.getDeterminedCount().getLabels(), 0);

        LabeledValueSet<IntegerRange, Integer> remainingMothersToFind = requiredBirths.getDeterminedCount().clone();


        IntegerRange highestBirthOption;

        try {
            highestBirthOption = remainingMothersToFind.getLargestLabelOfNoneZeroValue();
        } catch (NoSuchElementException e) {
            return new MotherSet(havePartners, needPartners);
        }

        Collections.shuffle(femalesAL);

        for(IPersonExtended female : femalesAL) {

            if(childrenMade >= numberOfChildren) {
                break;
            }

            if(eligible(female, desiredPopulationStatistics, currentDate)) {

                childrenMade += highestBirthOption.getValue();

                if(female.needsNewPartner(currentDate)) {
                    needPartners.add(new NewMother(female, highestBirthOption.getValue()));
                } else {

                    female.addChildrenToCurrentPartnership(highestBirthOption.getValue(), currentDate, consideredTimePeriod, population, desiredPopulationStatistics);
                    havePartners.add(female);

                    try {
                        continuingPartneredFemalesByChildren.get(female.numberOfChildrenInLatestPartnership()).add(female);
                    } catch (NullPointerException e) {
                        continuingPartneredFemalesByChildren.put(female.numberOfChildrenInLatestPartnership(), new ArrayList<>(Collections.singleton(female)));
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

        SeparationLogic.handle(continuingPartneredFemalesByChildren, consideredTimePeriod, currentDate, desiredPopulationStatistics, population, config);

        requiredBirths.setFufilledCount(motherCountsByMaternities);
        desiredPopulationStatistics.returnAchievedCount(requiredBirths);

        return new MotherSet(havePartners, needPartners, childrenMade);

    }

    private MultipleDeterminedCount calcNumberOfPreganciesOfMultipleBirth(int ageOfMothers, int numberOfChildren, PopulationStatistics desiredPopulationStatistics, AdvancableDate currentDate,
                                                                          CompoundTimeUnit consideredTimePeriod, Config config) {

        MultipleBirthStatsKey key = new MultipleBirthStatsKey(ageOfMothers, numberOfChildren, consideredTimePeriod, currentDate);
        return (MultipleDeterminedCount) desiredPopulationStatistics.getDeterminedCount(key, config);

    }

    private boolean eligible(IPersonExtended potentialMother, PopulationStatistics desiredPopulationStatistics, uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date currentDate) {

        IPersonExtended lastChild = potentialMother.getLastChild();

        if(lastChild != null) {
            ExactDate earliestDateOfNextChild = DateUtils.
                    calculateExactDate(lastChild.getBirthDate_ex(), desiredPopulationStatistics.getMinBirthSpacing());

            // Returns true if last child was born far enough in the past for another child to be born at currentDate
            return DateUtils.dateBefore(earliestDateOfNextChild, currentDate);
        } else {
            // i.e. there is no previous child and thus no limitation to birth
            return true;
        }
    }
}
