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
package uk.ac.standrews.cs.valipop.events.birth.partnering;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.events.birth.NewMother;
import uk.ac.standrews.cs.valipop.simulationEntities.EntityFactory;
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.MarriageStatsKey;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.IllegitimateBirthStatsKey;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.PartneringStatsKey;
import uk.ac.standrews.cs.valipop.utils.CollectionUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.ValipopDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvanceableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.IntegerRange;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.InvalidRangeException;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRangeToDoubleSet;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRangeToIntegerSet;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.LabelledValueSet;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.OperableLabelledValueSet;

import java.util.*;

import static uk.ac.standrews.cs.valipop.simulationEntities.population.PopulationNavigation.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PartneringLogic {

    public static int handle(Collection<NewMother> needingPartners, PopulationStatistics desiredPopulationStatistics,
                             AdvanceableDate currentDate, CompoundTimeUnit consideredTimePeriod, Population population,
                             Config config) {

        int forNFemales = needingPartners.size();

        if (forNFemales != 0) {

            LinkedList<NewMother> women = new LinkedList<>(needingPartners);

            int age = women.getFirst().getNewMother().ageOnDate(currentDate);

            PartneringStatsKey key = new PartneringStatsKey(age, forNFemales, consideredTimePeriod, currentDate);

            MultipleDeterminedCount determinedCounts = (MultipleDeterminedCount) desiredPopulationStatistics.getDeterminedCount(key, config);

            OperableLabelledValueSet<IntegerRange, Integer> partnerCounts;
            LabelledValueSet<IntegerRange, Integer> achievedPartnerCounts;

            try {
                partnerCounts = new IntegerRangeToIntegerSet(determinedCounts.getDeterminedCount());
                achievedPartnerCounts = new IntegerRangeToIntegerSet(partnerCounts.getLabels(), 0);
            } catch (NullPointerException e) {
                throw new Error("Large population size has lead to accumalated errors in processing of Doubles that the " +
                        "sum of the underlying self correction array no longer approximates to a whole number - " +
                        "make DELTA bigger? Or use a data type that actually works...");
            }

            LabelledValueSet<IntegerRange, Integer> availableMen = new IntegerRangeToIntegerSet(partnerCounts.getLabels(), 0);

            // this section gets all the men in the age ranges we may need to look at
            Map<IntegerRange, LinkedList<IPerson>> allMen = new TreeMap<>();

            for (IntegerRange iR : partnerCounts.getLabels()) {
                AdvanceableDate yobOfOlderEndOfIR = getYobOfOlderEndOfIR(iR, currentDate);
                CompoundTimeUnit iRLength = getIRLength(iR);

                LinkedList<IPerson> m = new LinkedList<>(population.getLivingPeople().getMales().getAllPersonsBornInTimePeriod(yobOfOlderEndOfIR, iRLength));

                CollectionUtils.shuffle(m, desiredPopulationStatistics.getRandomGenerator());

                allMen.put(iR, m);
                availableMen.update(iR, m.size());
            }

            OperableLabelledValueSet<IntegerRange, Double> shortfallCounts =
                    new IntegerRangeToDoubleSet(partnerCounts.valuesSubtractValues(availableMen));

            // this section redistributes the determined partner counts based on the number of available men in each age range
            while (shortfallCounts.countPositiveValues() != 0) {
                LabelledValueSet<IntegerRange, Double> zeroedNegShortfalls = shortfallCounts.zeroNegativeValues();
                int numberOfRangesWithSpareMen = shortfallCounts.countNegativeValues();
                double totalShortfall = zeroedNegShortfalls.getSumOfValues();
                double shortfallToShare = totalShortfall / (double) numberOfRangesWithSpareMen;
                partnerCounts = new IntegerRangeToDoubleSet(partnerCounts.valuesAddNWhereCorrespondingLabelNegativeInLVS(shortfallToShare, shortfallCounts)
                        .valuesSubtractValues(zeroedNegShortfalls)).controlledRoundingMaintainingSum();
                shortfallCounts = new IntegerRangeToDoubleSet(partnerCounts.valuesSubtractValues(availableMen));
            }

            // TODO - upto - question: does infids affect NPA?

            ArrayList<ProposedPartnership> proposedPartnerships = new ArrayList<>();

            // for each age range of males
            for (IntegerRange iR : partnerCounts.getLabels()) {

                int determinedCount = partnerCounts.get(iR);

                LinkedList<IPerson> men = allMen.get(iR);
                IPerson head = null; // keeps track of first man seen to prevent infinite loop

                Collection<NewMother> unmatchedFemales = new ArrayList<>();

                // Keep going until enough females have been matched for this iR
                while (determinedCount > 0) {
                    IPerson man = men.pollFirst();
                    NewMother woman;

                    if (!women.isEmpty()) {
                        woman = women.pollFirst();
                    } else {
                        break;
                    }

                    // if man is head of list - i.e. this is the second time round
                    if (man == head) {
                        // thus female has not been able to be matched
                        unmatchedFemales.add(woman);
                        head = null;

                        // get next woman to check for partnering
                        if (!women.isEmpty()) {
                            woman = women.pollFirst();
                        } else {
                            break;
                        }
                    }

                    // check if there is any reason why these people cannot lawfully be partnered...
                    if (eligible(man, woman.getNewMother(), woman.getNumberOfChildrenInMaternity(), population, desiredPopulationStatistics, currentDate, consideredTimePeriod, config)) {
                        // if they can - then note as a proposed partnership
                        proposedPartnerships.add(new ProposedPartnership(man, woman.getNewMother(), iR, woman.getNumberOfChildrenInMaternity()));
                        determinedCount--;
                        head = null;

                    } else {
                        // else we need to loop through more men - so keep track of the first man we looked at
                        if (head == null) {
                            head = man;
                        }
                        men.addLast(man);
                        women.addFirst(woman);
                    }
                }

                women.addAll(unmatchedFemales);

                // note how many females have been partnered at this age range
                achievedPartnerCounts.add(iR, partnerCounts.get(iR) - determinedCount);
            }

            if (!women.isEmpty()) {
                for (int i = 0; i < women.size(); i++) {
                    NewMother uf = women.get(i);

                    nmLoop:
                    for (IntegerRange iR : partnerCounts.getLabels()) {
                        for (IPerson m : allMen.get(iR)) {
                            if (eligible(m, uf.getNewMother(), uf.getNumberOfChildrenInMaternity(), population, desiredPopulationStatistics, currentDate, consideredTimePeriod, config) && !inPPs(m, proposedPartnerships)) {

                                proposedPartnerships.add(new ProposedPartnership(m, uf.getNewMother(), iR, uf.getNumberOfChildrenInMaternity()));

                                women.remove(uf);
                                i--;
                                break nmLoop;
                            }
                        }
                    }
                }
            }

            int cancelledChildren = getCancelledChildren(population, women);

            LabelledValueSet<IntegerRange, Integer> returnPartnerCounts = determinedCounts.getZeroedCountsTemplate();

            Map<Integer, ArrayList<IPerson>> partneredFemalesByChildren = new HashMap<>();

            for (final ProposedPartnership partnership : proposedPartnerships) {

                final IPerson mother = partnership.getFemale();
                final IPerson father = partnership.getMale();

                final int numChildrenInPartnership = partnership.getNumberOfChildren();

                // Decide on marriage
                final MarriageStatsKey marriageKey = new MarriageStatsKey(mother.ageOnDate(currentDate), numChildrenInPartnership, consideredTimePeriod, currentDate);
                final SingleDeterminedCount marriageCounts = (SingleDeterminedCount) desiredPopulationStatistics.getDeterminedCount(marriageKey, config);

                final boolean isIllegitimate = !father.needsNewPartner(currentDate);
                final boolean toBeMarriedBirth = !isIllegitimate && (int) Math.round(marriageCounts.getDeterminedCount() / (double) numChildrenInPartnership) == 1;

                final IPartnership marriage = EntityFactory.formNewChildrenInPartnership(numChildrenInPartnership, father, mother, currentDate, consideredTimePeriod, population, desiredPopulationStatistics, isIllegitimate, toBeMarriedBirth);

                // checks if marriage was possible
                if (marriage.getMarriageDate() != null) {
                    marriageCounts.setFulfilledCount(numChildrenInPartnership);
                } else {
                    marriageCounts.setFulfilledCount(0);
                }

                desiredPopulationStatistics.returnAchievedCount(marriageCounts);

                final IntegerRange maleAgeRange = resolveAgeToIntegerRange(father, returnPartnerCounts.getLabels(), currentDate);
                returnPartnerCounts.update(maleAgeRange, returnPartnerCounts.getValue(maleAgeRange) + 1);

                addMotherToMap(partneredFemalesByChildren, mother, numChildrenInPartnership);
            }

            determinedCounts.setFulfilledCount(returnPartnerCounts);
            desiredPopulationStatistics.returnAchievedCount(determinedCounts);

            SeparationLogic.handle(partneredFemalesByChildren, consideredTimePeriod, currentDate, desiredPopulationStatistics, population, config);

            return cancelledChildren;
        }

        return 0;
    }

    private static void addMotherToMap(Map<Integer, ArrayList<IPerson>> partneredFemalesByChildren, IPerson mother, int numChildrenInPartnership) {

        if (partneredFemalesByChildren.containsKey(numChildrenInPartnership)) {
            partneredFemalesByChildren.get(numChildrenInPartnership).add(mother);
        } else {
            partneredFemalesByChildren.put(numChildrenInPartnership, new ArrayList<>(Collections.singleton(mother)));
        }
    }

    private static int getCancelledChildren(Population population, List<NewMother> women) {

        int cancelledChildren = 0;

        if (!women.isEmpty()) {
            for (NewMother m : women) {
                IPerson mother = m.getNewMother();

                // update position in data structures
                population.getLivingPeople().removePerson(mother);

                cancelledChildren += m.getNumberOfChildrenInMaternity();
                // cancel birth(s) as no father can be found
                mother.getPartnerships().remove(mother.getLastPartnership());

                population.getLivingPeople().addPerson(mother);
            }
        }
        return cancelledChildren;
    }

    private static IntegerRange resolveAgeToIntegerRange(IPerson male, Set<IntegerRange> labels, ValipopDate currentDate) {

        int age = male.ageOnDate(currentDate);

        for (IntegerRange iR : labels) {
            if (iR.contains(age)) {
                return iR;
            }
        }

        throw new InvalidRangeException("Male does not fit in expected ranges...");
    }

    private static boolean inPPs(IPerson p, List<ProposedPartnership> pps) {

        for (ProposedPartnership pp : pps) {
            if (pp.getMale() == p || pp.getFemale() == p) {
                return true;
            }
        }
        return false;
    }

    private static boolean eligible(IPerson man, IPerson woman, int childrenInPregnancy, Population population, PopulationStatistics desiredPopulationStatistics,
                                    AdvanceableDate currentDate, CompoundTimeUnit consideredTimePeriod, Config config) {

        population.getPopulationCounts().incEligibilityCheck();

        boolean eligible = maleAvailable(man, childrenInPregnancy, desiredPopulationStatistics, currentDate, consideredTimePeriod, config) && legallyEligibleToMarry(man, woman);

        if (!eligible) {
            population.getPopulationCounts().incFailedEligibilityCheck();
        }

        return eligible;
    }

    private static boolean maleAvailable(IPerson man, int childrenInPregnancy, PopulationStatistics desiredPopulationStatistics, AdvanceableDate currentDate,
                                         CompoundTimeUnit consideredTimePeriod, Config config) {

        // in the init period any partnering is allowed
        if (DateUtils.dateBeforeOrEqual(currentDate, new YearDate(1791))) {
            return true;
        }

        // Get access to illegitimacy rates
        IllegitimateBirthStatsKey illegitimateKey = new IllegitimateBirthStatsKey(man.ageOnDate(currentDate), childrenInPregnancy, consideredTimePeriod, currentDate);
        SingleDeterminedCount illegitimateCounts = (SingleDeterminedCount) desiredPopulationStatistics.getDeterminedCount(illegitimateKey, config);
        int permitted = (int) Math.round(illegitimateCounts.getDeterminedCount() / (double) childrenInPregnancy);

        if (man.needsNewPartner(currentDate)) {
            // record the legitimate birth
            illegitimateCounts.setFulfilledCount(0);
            desiredPopulationStatistics.returnAchievedCount(illegitimateCounts);
            return true;
        }

        if (permitted == 1) {
            // record the illegitimate birth
            illegitimateCounts.setFulfilledCount(childrenInPregnancy);
            desiredPopulationStatistics.returnAchievedCount(illegitimateCounts);
            return true;
        }

        // no birth is to happen on account of this man - therefore we don't report any achieved count for the statistic
        return false;
    }

    private static boolean legallyEligibleToMarry(final IPerson man, final IPerson woman) {

        try {
            exclude(femaleAncestorsOf(man), woman);
            exclude(femaleDescendantsOf(man), woman);
            exclude(sistersOf(man), woman);
            exclude(femaleAncestorsOf(descendantsOf(man)), woman);
            exclude(femaleDescendantsOf(ancestorsOf(man)), woman);
            exclude(partnersOf(maleAncestorsOf(man)), woman);
            exclude(partnersOf(maleDescendantsOf(man)), woman);
            exclude(partnersOf(brothersOf(man)), woman);
            exclude(femaleDescendantsOf(siblingsOf(man)), woman);
            exclude(femaleAncestorsOf(partnersOf(man)), woman);
            exclude(femaleDescendantsOf(partnersOf(man)), woman);
        }
        catch (RuntimeException e) {
            return false;
        }

        return true;
    }

    private static void exclude(Collection<IPerson> collection, IPerson person) {
        if (collection.contains(person)) throw new RuntimeException();
    }

    private static CompoundTimeUnit getIRLength(IntegerRange iR) {

        int length = iR.getMax() - iR.getMin() + 1;

        return new CompoundTimeUnit(length, TimeUnit.YEAR);
    }

    private static AdvanceableDate getYobOfOlderEndOfIR(IntegerRange iR, ValipopDate currentDate) {

        int yob = currentDate.getYear() - iR.getMax() - 1;

        return new YearDate(yob);
    }
}
