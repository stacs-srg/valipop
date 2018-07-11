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
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.exceptions.PersonNotFoundException;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.MarriageStatsKey;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.IllegitimateBirthStatsKey;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.PartneringStatsKey;
import uk.ac.standrews.cs.valipop.utils.CollectionUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.IntegerRange;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.InvalidRangeException;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRangeToIntegerSet;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.LabelledValueSet;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRangeToDoubleSet;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.OperableLabelledValueSet;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PartneringLogic {

    public static int handle(Collection<NewMother> needingPartners, PopulationStatistics desiredPopulationStatistics,
                             AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod, Population population,
                             Config config) throws InsufficientNumberOfPeopleException, PersonNotFoundException {

        int forNFemales = needingPartners.size();

        if(forNFemales != 0) {

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
            Map<IntegerRange, LinkedList<IPersonExtended>> allMen = new HashMap<>();

            for(IntegerRange iR: partnerCounts.getLabels()) {
                AdvancableDate yobOfOlderEndOfIR = getYobOfOlderEndOfIR(iR, currentDate);
                CompoundTimeUnit iRLength = getIRLength(iR);

                LinkedList<IPersonExtended> m = new LinkedList<>(population.getLivingPeople().getMales().getAllPersonsBornInTimePeriod(yobOfOlderEndOfIR, iRLength));

                CollectionUtils.shuffle(m, desiredPopulationStatistics.getRandomGenerator());

                allMen.put(iR, m);
                availableMen.update(iR, m.size());
            }

            OperableLabelledValueSet<IntegerRange, Double> shortfallCounts =
                    new IntegerRangeToDoubleSet(partnerCounts.valuesSubtractValues(availableMen));

            // this section redistributes the determined partner counts based on the number of available men in each age range
            while(shortfallCounts.countPositiveValues() != 0) {
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
            for(IntegerRange iR: partnerCounts.getLabels()) {

                int determinedCount = partnerCounts.get(iR);

                LinkedList<IPersonExtended> men = allMen.get(iR);
                IPersonExtended head = null; // keeps track of first man seen to prevent infinite loop

                Collection<NewMother> unmatchedFemales = new ArrayList<>();

                // Keep going until enough females have been matched for this iR
                while(determinedCount > 0) {
                    IPersonExtended man = men.pollFirst();
                    NewMother woman;

                    if(!women.isEmpty()) {
                        woman = women.pollFirst();
                    } else {
                        break;
                    }

                    // if man is head of list - i.e. this is the second time round
                    if(man == head) {
                        // thus female has not been able to be matched
                        unmatchedFemales.add(woman);
                        head = null;

                        // get next woman to check for partnering
                        if(!women.isEmpty()) {
                            woman = women.pollFirst();
                        } else {
                            break;
                        }
                    }

                    // check if there is any reason why these people cannot lawfully be partnered...
                    if(eligible(man, woman.getNewMother(), woman.getNumberOfChildrenInMaternity(), population, desiredPopulationStatistics, currentDate, consideredTimePeriod, config)) {
                        // if they can - then note as a proposed partnership
                        proposedPartnerships.add(new ProposedPartnership(man, woman.getNewMother(), iR, woman.getNumberOfChildrenInMaternity()));
                        determinedCount--;
                        head = null;

                    } else {
                        // else we need to loop through more men - so keep track of the first man we looked at
                        if(head == null) {
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

            if(!women.isEmpty()) {
                for (int i = 0; i < women.size(); i++) {
                    NewMother uf = women.get(i);

                    nmLoop:
                    for (IntegerRange iR : partnerCounts.getLabels()) {
                        for (IPersonExtended m : allMen.get(iR)) {
                            if(eligible(m, uf.getNewMother(), uf.getNumberOfChildrenInMaternity(), population,
                                    desiredPopulationStatistics, currentDate, consideredTimePeriod, config)
                                    && !inPPs(m, proposedPartnerships)) {

                                proposedPartnerships.add(new ProposedPartnership(m, uf.getNewMother(), iR,
                                        uf.getNumberOfChildrenInMaternity()));

                                women.remove(uf);
                                i--;
                                break nmLoop;
                            }
                        }
                    }
                }
            }

            int cancelledChildren = 0;

            if(!women.isEmpty()) {
                for(NewMother m: women) {
                    IPersonExtended mother = m.getNewMother();

                    // update position in data structures
                    population.getLivingPeople().removePerson(mother);

                    cancelledChildren += m.getNumberOfChildrenInMaternity();
                    // cancel birth(s) as no father can be found
                    mother.getPartnerships_ex().remove(mother.getLastPartnership());

                    population.getLivingPeople().addPerson(mother);

                }
                System.out.println("CC: " + cancelledChildren);
            }

            LabelledValueSet<IntegerRange, Integer> returnPartnerCounts = determinedCounts.getZeroedCountsTemplate();

            Map<Integer, ArrayList<IPersonExtended>> partneredFemalesByChildren = new HashMap<>();

            for(ProposedPartnership pp : proposedPartnerships) {
                IPersonExtended mother = pp.getFemale();
                IPersonExtended father = pp.getMale();

                int numChildrenInPartnership = pp.getNumberOfChildren();

                // Decide on marriage
                MarriageStatsKey marriageKey = new MarriageStatsKey(mother.ageOnDate(currentDate), numChildrenInPartnership, consideredTimePeriod, currentDate);
                SingleDeterminedCount marriageCounts = (SingleDeterminedCount) desiredPopulationStatistics.getDeterminedCount(marriageKey, config);

                boolean isIllegitimate = !father.needsNewPartner(currentDate);
                boolean toBeMarriedBirth;

                if(!isIllegitimate) {
                    toBeMarriedBirth = (int) Math.round(marriageCounts.getDeterminedCount() / (double) numChildrenInPartnership) == 1;
                } else {
                    toBeMarriedBirth = false;
                }

                IPartnershipExtended marriage = EntityFactory.formNewChildrenInPartnership(numChildrenInPartnership, father, mother, currentDate, consideredTimePeriod, population, desiredPopulationStatistics, isIllegitimate, toBeMarriedBirth);

                // checks if marriage was possible
                if (marriage.getMarriageDate_ex() != null) {
                    marriageCounts.setFufilledCount(numChildrenInPartnership);
                } else {
                    marriageCounts.setFufilledCount(0);
                }

                desiredPopulationStatistics.returnAchievedCount(marriageCounts);

                IntegerRange maleAgeRange = resolveAgeToIR(pp.getMale(), returnPartnerCounts.getLabels(), currentDate);

                returnPartnerCounts.update(maleAgeRange, returnPartnerCounts.getValue(maleAgeRange) + 1);

                try {
                    partneredFemalesByChildren.get(numChildrenInPartnership).add(mother);
                } catch (NullPointerException e) {
                    partneredFemalesByChildren.put(numChildrenInPartnership, new ArrayList<>(Collections.singleton(mother)));
                }

            }

            determinedCounts.setFufilledCount(returnPartnerCounts);
            desiredPopulationStatistics.returnAchievedCount(determinedCounts);

            SeparationLogic.handle(partneredFemalesByChildren, consideredTimePeriod, currentDate, desiredPopulationStatistics, population, config);


            return cancelledChildren;
        }

        return 0;
    }

    private static IntegerRange resolveAgeToIR(IPersonExtended male, Set<IntegerRange> labels, uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date currentDate) {
        int age = male.ageOnDate(currentDate);

        for(IntegerRange iR : labels) {
            if(iR.contains(age)) {
                return iR;
            }
        }

//        if(currentDate.getDay() == 1 && currentDate.getMonth() == 1) {
//            age = male.ageOnDate(new ExactDate(31, 12, currentDate.getYear() - 1));
//
//            for(IntegerRange iR : labels) {
//                if(iR.contains(age)) {
//                    return iR;
//                }
//            }
//        }

        throw new InvalidRangeException("Male does not fit in expected ranges...");
    }

    private static boolean inPPs(IPersonExtended p, ArrayList<ProposedPartnership> pps) {

        for(ProposedPartnership pp : pps) {
            if(pp.getMale() == p || pp.getFemale() == p) {
                return true;
            }
        }
        return false;
    }

    private static boolean eligible(IPersonExtended man, IPersonExtended woman, int childrenInPregnancy,
                                    Population population, PopulationStatistics desiredPopulationStatistics,
                                    AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod, Config config) {

        population.getPopulationCounts().incEligibilityCheck();

        boolean eligible =  maleAvailable(man, childrenInPregnancy, population, desiredPopulationStatistics, currentDate, consideredTimePeriod, config) && legallyEligible(man, woman);

        if(!eligible) {
            population.getPopulationCounts().incFailedEligibilityCheck();
        }

        return eligible;

    }


    private static boolean maleAvailable(IPersonExtended man, int childrenInPregnancy, Population population,
                                         PopulationStatistics desiredPopulationStatistics, AdvancableDate currentDate,
                                         CompoundTimeUnit consideredTimePeriod, Config config) {

        // in the init period any partnering is allowed
//        if(InitLogic.inInitPeriod(currentDate)) {
        if(DateUtils.dateBeforeOrEqual(currentDate, new YearDate(1791))) {
            return true;
        }

        // Get acess to illegitimacy rates
        IllegitimateBirthStatsKey illegitimateKey = new IllegitimateBirthStatsKey(man.ageOnDate(currentDate), childrenInPregnancy, consideredTimePeriod, currentDate);
        SingleDeterminedCount illegitimateCounts = (SingleDeterminedCount) desiredPopulationStatistics.getDeterminedCount(illegitimateKey, config);
        int permitted = (int) Math.round(illegitimateCounts.getDeterminedCount() / (double) childrenInPregnancy);

        if(man.needsNewPartner(currentDate)) {
            // record the legitimate birth
            illegitimateCounts.setFufilledCount(0);
            desiredPopulationStatistics.returnAchievedCount(illegitimateCounts);
            return true;
        }

        if(permitted == 1) {
            // record the illegitimate birth
            illegitimateCounts.setFufilledCount(childrenInPregnancy);
            desiredPopulationStatistics.returnAchievedCount(illegitimateCounts);
            return true;
        }

        // no birth is to happen on account of this man - therefore we don't report any anchieved count for the statistic
        return false;

    }

    private static boolean legallyEligible(IPersonExtended man, IPersonExtended woman) {

        if(man
            .getParentsPartnership_ex() != null) {

            //  Mother
            if(man
                    .getParentsPartnership_ex().getFemalePartner() == woman)
                return false;

            //  Sister and half sister
            if(man
                    .getParentsPartnership_ex().getMalePartner().getAllChildren().contains(woman))
                return false;

            if(man
                    .getParentsPartnership_ex().getFemalePartner().getAllChildren().contains(woman))
                return false;

            //  Brother's daughter
            //  Sister's daughter
            for(IPersonExtended sibling : man.getParentsPartnership_ex().getChildren()) {
                if(sibling.getAllChildren().contains(woman))
                    return false;
            }

            //  Former wife of father
            for(IPartnershipExtended fathersPart : man
                    .getParentsPartnership_ex().getMalePartner().getPartnerships_ex()) {

                if(fathersPart.getFemalePartner() == woman)
                    return false;
            }


            // grand parents - fathers side

            if(man
                    .getParentsPartnership_ex().getMalePartner()
                    .getParentsPartnership_ex() != null) {

                //  Father's mother
                if(man
                        .getParentsPartnership_ex().getMalePartner()
                        .getParentsPartnership_ex().getFemalePartner() == woman)
                    return false;

                // Father's sister
                // (Father's father's children)
                if(man
                        .getParentsPartnership_ex().getMalePartner()
                        .getParentsPartnership_ex().getMalePartner().getAllChildren().contains(woman))
                    return false;

                // (Father's mothers's children)
                if(man
                        .getParentsPartnership_ex().getMalePartner()
                        .getParentsPartnership_ex().getFemalePartner().getAllChildren().contains(woman))
                    return false;

                //  Former wife of father's father
                for(IPartnershipExtended gFathersPart : man
                        .getParentsPartnership_ex().getMalePartner()
                        .getParentsPartnership_ex().getMalePartner().getPartnerships_ex()) {

                    if(gFathersPart.getFemalePartner() == woman)
                        return false;
                }

                // great grand parents - fathers father parents

                //  Father's father's mother
                IPartnershipExtended ffP = man
                        .getParentsPartnership_ex().getMalePartner()
                        .getParentsPartnership_ex().getMalePartner()
                        .getParentsPartnership_ex();

                if(ffP != null && ffP.getFemalePartner() == woman)
                    return false;

                //  Father's mother's mother
                IPartnershipExtended fmP = man
                        .getParentsPartnership_ex().getMalePartner()
                        .getParentsPartnership_ex().getFemalePartner()
                        .getParentsPartnership_ex();

                if(fmP != null && fmP.getFemalePartner() == woman)
                    return false;

            }

            // grand parents - mothers side

            if( man
                    .getParentsPartnership_ex().getFemalePartner()
                    .getParentsPartnership_ex() != null) {

                //  Mother's mother
                if(man
                        .getParentsPartnership_ex().getFemalePartner()
                        .getParentsPartnership_ex().getFemalePartner() == woman)
                    return false;

                //  Mother's sister
                // (Mother's father's children)
                if(man
                        .getParentsPartnership_ex().getFemalePartner()
                        .getParentsPartnership_ex().getMalePartner().getAllChildren().contains(woman))
                    return false;

                // (Mother's mothers's children)
                if(man
                        .getParentsPartnership_ex().getFemalePartner()
                        .getParentsPartnership_ex().getFemalePartner().getAllChildren().contains(woman))
                    return false;

                //  Former wife of mother's father
                for(IPartnershipExtended gFathersPart : man
                        .getParentsPartnership_ex().getFemalePartner()
                        .getParentsPartnership_ex().getMalePartner().getPartnerships_ex()) {

                    if(gFathersPart.getFemalePartner() == woman)
                        return false;
                }

                //  Mother's father's mother
                IPartnershipExtended mfP = man
                        .getParentsPartnership_ex().getFemalePartner()
                        .getParentsPartnership_ex().getMalePartner()
                        .getParentsPartnership_ex();

                if(mfP != null && mfP.getFemalePartner() == woman)
                    return false;

                //  Mother's mother's mother
                IPartnershipExtended mmP = man
                        .getParentsPartnership_ex().getFemalePartner()
                        .getParentsPartnership_ex().getFemalePartner()
                        .getParentsPartnership_ex();

                if(mmP != null && mmP.getFemalePartner() == woman)
                    return false;

            }
        }

        // Descendant checks

        //  Daughter
        if(man
                .getAllChildren().contains(woman))
            return false;

        //  Son's daughter
        //  Daughter's daughter
        if(man
                .getAllGrandChildren().contains(woman))
            return false;

        //  Son's son's daughter
        //  Son's daughter's daughter
        //  Daughter's son's daughter
        //  Daughter's daughter's daughter
        if(man.getAllGreatGrandChildren().contains(woman))
            return false;

        //  Former wife of son
        for(IPersonExtended child : man.getAllChildren()) {
            for (IPartnershipExtended childsPart : child.getPartnerships_ex()) {
                if(childsPart.getFemalePartner() == woman)
                    return false;
            }
        }

        //  Former wife of son's son
        //  Former wife of daughter's son
        for(IPersonExtended gChild: man.getAllGrandChildren()) {
            for(IPartnershipExtended gChildPart : gChild.getPartnerships_ex()) {
                if(gChildPart.getFemalePartner() == woman)
                    return false;
            }
        }

        // Wives parents checks

        //  of former wife - level1
        for(IPartnershipExtended part : man.getPartnerships_ex()) {

            //  Daughter of former wife
            if(part.getFemalePartner()
                    .getAllChildren().contains(woman))
                return false;

            //  Daughter of son of former wife
            //  Daughter of daughter of former wife
            if(part.getFemalePartner()
                    .getAllGrandChildren().contains(woman))
                return false;

        }

        //  of former wife - level2
        for(IPartnershipExtended part : man.getPartnerships_ex()) {

            //  Mother of former wife
            IPartnershipExtended p = part.getFemalePartner().getParentsPartnership_ex();
            if(p != null && p.getFemalePartner() == woman)
                return false;

        }

        //  of former wife - level3
        for(IPartnershipExtended part : man.getPartnerships_ex()) {

            //  Mother of father of former wife
            IPartnershipExtended p1 = part.getFemalePartner().getParentsPartnership_ex();
            if(p1 != null) {
                IPartnershipExtended p3 = p1.getMalePartner().getParentsPartnership_ex();
                if (p3 != null && p3.getFemalePartner() == woman)
                    return false;
            }

            //  Mother of mother of former wife
            if(p1 != null) {
                IPartnershipExtended p3 = p1.getFemalePartner().getParentsPartnership_ex();
                if (p3 != null && p3.getFemalePartner() == woman)
                    return false;
            }

        }

        // phew...
        return true;
    }

    private static CompoundTimeUnit getIRLength(IntegerRange iR) {

        int length = iR.getMax() - iR.getMin() + 1;

        return new CompoundTimeUnit(length, TimeUnit.YEAR);
    }

    private static AdvancableDate getYobOfOlderEndOfIR(IntegerRange iR, uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date currentDate) {

        int yob = currentDate.getYear() - iR.getMax() - 1;

        return new YearDate(yob);
    }

}
