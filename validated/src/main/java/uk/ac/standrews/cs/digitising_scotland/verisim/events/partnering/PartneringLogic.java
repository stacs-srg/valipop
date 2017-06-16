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
package uk.ac.standrews.cs.digitising_scotland.verisim.events.partnering;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.SeparationLogic;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.PartneringStatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.IntegerRangeToIntegerSet;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.LabeledValueSet;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PartneringLogic {

    public static void handle(Collection<IPerson> needingPartners, PopulationStatistics desiredPopulationStatistics, Date currentDate, CompoundTimeUnit consideredTimePeriod, Population population) throws InsufficientNumberOfPeopleException {

        int forNFemales = needingPartners.size();

        if(forNFemales != 0) {

            LinkedList<IPerson> women = new LinkedList<>(needingPartners);

            int age = women.getFirst().ageOnDate(currentDate);

            PartneringStatsKey key = new PartneringStatsKey(age, forNFemales, consideredTimePeriod, currentDate);

            MultipleDeterminedCount determinedCounts = desiredPopulationStatistics.getPartneringRates(currentDate).determineCount(key);

            LabeledValueSet<IntegerRange, Integer> partnerCounts = determinedCounts.getDeterminedCount();
            LabeledValueSet<IntegerRange, Integer> achievedPartnerCounts = new IntegerRangeToIntegerSet(partnerCounts.getLabels(), 0);
//            LabeledValueSet<IntegerRange, Integer> shortfallCounts = new IntegerRangeToIntegerSet(partnerCounts.getRowLabels(), 0);

            LabeledValueSet<IntegerRange, Integer> availableMen = new IntegerRangeToIntegerSet(partnerCounts.getLabels(), 0);

            // this section gets all the men in the age ranges we may need to look at
            Map<IntegerRange, LinkedList<IPerson>> allMen = new HashMap<>();

            for(IntegerRange iR: partnerCounts.getLabels()) {
                AdvancableDate yobOfOlderEndOfIR = getYobOfOlderEndOfIR(iR, currentDate);
                CompoundTimeUnit iRLength = getIRLength(iR);

                Collection<IPerson> m = population.getLivingPeople().getMales().getAllPersonsInTimePeriod(yobOfOlderEndOfIR, iRLength);

                allMen.put(iR, new LinkedList<>(m));
                availableMen.update(iR, m.size());
            }

            LabeledValueSet<IntegerRange, Double> shortfallCounts = partnerCounts.valuesSubtractValues(availableMen);

            // this section redistributes the determined partner counts based on the number of available men in each age range
            while(shortfallCounts.countPositiveValues() != 0) {
                LabeledValueSet<IntegerRange, Double> zeroedNegShortfalls = shortfallCounts.zeroNegativeValues();
                int numberOfRangesWithSpareMen = shortfallCounts.countNegativeValues();
                double totalShortfall = zeroedNegShortfalls.getSumOfValues();
                double shortfallToShare = totalShortfall / (double) numberOfRangesWithSpareMen;
                partnerCounts = partnerCounts.valuesAddNWhereCorrespondingLabelNegativeInLVS(shortfallToShare, shortfallCounts).valuesSubtractValues(zeroedNegShortfalls).controlledRoundingMaintainingSum();
                shortfallCounts = partnerCounts.valuesSubtractValues(availableMen);
            }

            ArrayList<ProposedPartnership> proposedPartnerships = new ArrayList<>();

            for(IntegerRange iR: partnerCounts.getLabels()) {

                int determinedCount = partnerCounts.get(iR);

                LinkedList<IPerson> men = allMen.get(iR);
                IPerson head = null; // keeps track of first man seen to prevent infinite loop

                Collection<IPerson> unmatchedFemales = new ArrayList<>();

                // Keep going until enough females have been matched for this iR
                while(determinedCount > 0) {
                    IPerson man = men.pollFirst();
                    IPerson woman;

                    if(!women.isEmpty()) {
                        woman = women.pollFirst();
                    } else {
                        break;
                    }

                    if(man == head) {
                        unmatchedFemales.add(woman);
                        head = null;
                        if(!women.isEmpty()) {
                            woman = women.pollFirst();
                        } else {
                            break;
                        }
                    }

                    if(eligible(man, woman, population, desiredPopulationStatistics)) {
                        proposedPartnerships.add(new ProposedPartnership(man, woman, iR));
                        determinedCount--;
                        head = null;
                    } else {
                        if(head == null) {
                            head = man;
                        }
                        men.addLast(man);
                        women.addFirst(woman);
                    }
                }

                women.addAll(unmatchedFemales);
                achievedPartnerCounts.add(iR, partnerCounts.get(iR) - determinedCount);
//                shortfallCounts.add(iR, determinedCount);

            }

            if(!women.isEmpty()) {
                // We have not been able to find eligible partners for all the women, now look to perform swaps where possible

                for(IntegerRange uR : partnerCounts.getLabels()) {

                    while(partnerCounts.get(uR) - achievedPartnerCounts.get(uR) > 0) {

                        ppLoop:
                        for(ProposedPartnership pp : proposedPartnerships) {

                            IPerson f = pp.getFemale();

                            for(IPerson m : allMen.get(uR)) {

                                if(eligible(m, f, population, desiredPopulationStatistics) && !inPPs(m, proposedPartnerships)) {

                                    for(IPerson uf : women) {

                                        if(eligible(pp.getMale(), uf, population, desiredPopulationStatistics)) {
                                            // husband swap
                                            proposedPartnerships.add(new ProposedPartnership(pp.getMale(), uf, pp.getMalesRange()));
                                            pp.setMale(m, uR);
                                            women.remove(uf);
                                            achievedPartnerCounts.update(uR, achievedPartnerCounts.getValue(uR) + 1);
                                            if(achievedPartnerCounts.get(uR) == 0) {
                                                break ppLoop;
                                            }
                                            break;
                                        }

                                    }

                                    for(IPerson uf : women) {

                                        for(IPerson m2 : allMen.get(pp.getMalesRange())) {

                                            if(eligible(m2, uf, population, desiredPopulationStatistics) && !inPPs(m2, proposedPartnerships)) {
                                                // husband swap
                                                proposedPartnerships.add(new ProposedPartnership(m2, uf, pp.getMalesRange()));
                                                pp.setMale(m, uR);
                                                women.remove(uf);
                                                achievedPartnerCounts.update(uR, achievedPartnerCounts.getValue(uR) + 1);
                                                if(achievedPartnerCounts.get(uR) == 0) {
                                                    break ppLoop;
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if(!women.isEmpty()) {
                for (IPerson uf : women) {
                    for (IntegerRange iR : partnerCounts.getLabels()) {
                        for (IPerson m : allMen.get(iR)) {
                            if(eligible(m, uf, population, desiredPopulationStatistics) && !inPPs(m, proposedPartnerships)) {
                                proposedPartnerships.add(new ProposedPartnership(m, uf, iR));
                            }
                        }
                    }
                }
            }

            if(!women.isEmpty()) {
                throw new InsufficientNumberOfPeopleException("No man to partner this woman to...");
            }

            Map<Integer, ArrayList<IPerson>> partneredFemalesByChildren = new HashMap<>();

            for(ProposedPartnership pp : proposedPartnerships) {
                IPerson mother = pp.getFemale();
                IPartnership partnershipNeedingFather = mother.getLastChild().getParentsPartnership();

                partnershipNeedingFather.setFather(pp.getMale());
                int numChildrenInPartnership = partnershipNeedingFather.getChildren().size();

                try {
                    partneredFemalesByChildren.get(numChildrenInPartnership).add(mother);
                } catch (NullPointerException e) {
                    partneredFemalesByChildren.put(numChildrenInPartnership, new ArrayList<>(Collections.singleton(mother)));
                }

            }



            SeparationLogic.handle(partneredFemalesByChildren, consideredTimePeriod, currentDate, desiredPopulationStatistics, population);

        }
    }

    private static boolean inPPs(IPerson p, ArrayList<ProposedPartnership> pps) {

        for(ProposedPartnership pp : pps) {
            if(pp.getMale() == p || pp.getFemale() == p) {
                return true;
            }
        }
        return false;
    }

    private static boolean eligible(IPerson man, IPerson woman, Population population, PopulationStatistics desiredPopulationStatistics) {

        return maleAvailable(man, population, desiredPopulationStatistics) && legallyEligible(man, woman);

    }


    private static boolean maleAvailable(IPerson man, Population population,
                                         PopulationStatistics desiredPopulationStatistics) {

        if(man.toSeparate()) {
            return true;
        }

        double permissableIllegitimateBirths = desiredPopulationStatistics.getMaxProportionBirthsDueToInfidelity() * population.getPopulationCounts().getCreatedPeople();
        if(population.getPopulationCounts().getIllegitimateBirths() < permissableIllegitimateBirths) {
            population.getPopulationCounts().newIllegitimateBirth();
            return true;
        } else {
            return false;
        }

    }

    private static boolean legallyEligible(IPerson man, IPerson woman) {

        if(man
            .getParentsPartnership() != null) {

            //  Mother
            if(man
                    .getParentsPartnership().getFemalePartner() == woman)
                return false;

            //  Sister and half sister
            if(man
                    .getParentsPartnership().getMalePartner().getAllChildren().contains(woman))
                return false;

            if(man
                    .getParentsPartnership().getFemalePartner().getAllChildren().contains(woman))
                return false;

            //  Brother's daughter
            //  Sister's daughter
            for(IPerson sibling : man.getParentsPartnership().getChildren()) {
                if(sibling.getAllChildren().contains(woman))
                    return false;
            }

            //  Former wife of father
            for(IPartnership fathersPart : man
                    .getParentsPartnership().getMalePartner().getPartnerships()) {

                if(fathersPart.getFemalePartner() == woman)
                    return false;
            }


            // grand parents - fathers side

            if(man
                    .getParentsPartnership().getMalePartner()
                    .getParentsPartnership() != null) {

                //  Father's mother
                if(man
                        .getParentsPartnership().getMalePartner()
                        .getParentsPartnership().getFemalePartner() == woman)
                    return false;

                // Father's sister
                // (Father's father's children)
                if(man
                        .getParentsPartnership().getMalePartner()
                        .getParentsPartnership().getMalePartner().getAllChildren().contains(woman))
                    return false;

                // (Father's mothers's children)
                if(man
                        .getParentsPartnership().getMalePartner()
                        .getParentsPartnership().getFemalePartner().getAllChildren().contains(woman))
                    return false;

                //  Former wife of father's father
                for(IPartnership gFathersPart : man
                        .getParentsPartnership().getMalePartner()
                        .getParentsPartnership().getMalePartner().getPartnerships()) {

                    if(gFathersPart.getFemalePartner() == woman)
                        return false;
                }

                // great grand parents - fathers father parents

                //  Father's father's mother
                IPartnership ffP = man
                        .getParentsPartnership().getMalePartner()
                        .getParentsPartnership().getMalePartner()
                        .getParentsPartnership();

                if(ffP != null && ffP.getFemalePartner() == woman)
                    return false;

                //  Father's mother's mother
                IPartnership fmP = man
                        .getParentsPartnership().getMalePartner()
                        .getParentsPartnership().getFemalePartner()
                        .getParentsPartnership();

                if(fmP != null && fmP.getFemalePartner() == woman)
                    return false;

            }

            // grand parents - mothers side

            if( man
                    .getParentsPartnership().getFemalePartner()
                    .getParentsPartnership() != null) {

                //  Mother's mother
                if(man
                        .getParentsPartnership().getFemalePartner()
                        .getParentsPartnership().getFemalePartner() == woman)
                    return false;

                //  Mother's sister
                // (Mother's father's children)
                if(man
                        .getParentsPartnership().getFemalePartner()
                        .getParentsPartnership().getMalePartner().getAllChildren().contains(woman))
                    return false;

                // (Mother's mothers's children)
                if(man
                        .getParentsPartnership().getFemalePartner()
                        .getParentsPartnership().getFemalePartner().getAllChildren().contains(woman))
                    return false;

                //  Former wife of mother's father
                for(IPartnership gFathersPart : man
                        .getParentsPartnership().getFemalePartner()
                        .getParentsPartnership().getMalePartner().getPartnerships()) {

                    if(gFathersPart.getFemalePartner() == woman)
                        return false;
                }

                //  Mother's father's mother
                IPartnership mfP = man
                        .getParentsPartnership().getFemalePartner()
                        .getParentsPartnership().getMalePartner()
                        .getParentsPartnership();

                if(mfP != null && mfP.getFemalePartner() == woman)
                    return false;

                //  Mother's mother's mother
                IPartnership mmP = man
                        .getParentsPartnership().getFemalePartner()
                        .getParentsPartnership().getFemalePartner()
                        .getParentsPartnership();

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
        for(IPerson child : man.getAllChildren()) {
            for (IPartnership childsPart : child.getPartnerships()) {
                if(childsPart.getFemalePartner() == woman)
                    return false;
            }
        }

        //  Former wife of son's son
        //  Former wife of daughter's son
        for(IPerson gChild: man.getAllGrandChildren()) {
            for(IPartnership gChildPart : gChild.getPartnerships()) {
                if(gChildPart.getFemalePartner() == woman)
                    return false;
            }
        }

        // Wives parents checks

        //  of former wife - level1
        for(IPartnership part : man.getPartnerships()) {

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
        for(IPartnership part : man.getPartnerships()) {

            //  Mother of former wife
            IPartnership p = part.getFemalePartner().getParentsPartnership();
            if(p != null && p.getFemalePartner() == woman)
                return false;

        }

        //  of former wife - level3
        for(IPartnership part : man.getPartnerships()) {

            //  Mother of father of former wife
            IPartnership p1 = part.getFemalePartner().getParentsPartnership();
            if(p1 != null) {
                IPartnership p3 = p1.getMalePartner().getParentsPartnership();
                if (p3 != null && p3.getFemalePartner() == woman)
                    return false;
            }

            //  Mother of mother of former wife
            IPartnership p2 = part.getFemalePartner().getParentsPartnership();
            if(p1 != null) {
                IPartnership p3 = p1.getFemalePartner().getParentsPartnership();
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

    private static AdvancableDate getYobOfOlderEndOfIR(IntegerRange iR, Date currentDate) {

        int yob = currentDate.getYear() - iR.getMax();

        return new YearDate(yob);
    }

}
