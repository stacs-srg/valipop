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
package uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.IntNodes;

import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.InvalidRangeException;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.IntNode;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SeparationOption;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.PartneringStatsKey;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure.PersonCharacteristicsIdentifier;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.IntegerRange;

import java.util.Set;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SeparationNodeInt extends IntNode<SeparationOption, IntegerRange> {

    public SeparationNodeInt(SeparationOption option, NumberOfChildrenInPartnershipNodeInt parentNode, Integer initCount) {
        super(option, parentNode, initCount);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

        // TODO change this to resolve to the correct interger ranges

        incCountByOne();

        IPartnershipExtended activePartnership = PersonCharacteristicsIdentifier.getActivePartnership(person, currentDate);

        Integer newPartnerAge = null;

        if(activePartnership != null && PersonCharacteristicsIdentifier.startedInYear(activePartnership, currentDate.getYearDate())) {
            IPersonExtended partner = activePartnership.getPartnerOf(person);

            newPartnerAge = partner.ageOnDate(activePartnership.getPartnershipDate());
        }

        // check if the partner falls into one of the child ranges
        for(Node<IntegerRange, ?, Integer, ?> node : getChildren()) {

            Boolean in;
            try {
                in = node.getOption().contains(newPartnerAge);
            } catch (InvalidRangeException e) {
                in = null;
            }

            // if partners age is in the considered range then process this person using this NPA range and return
            if (in != null && in){
                node.processPerson(person, currentDate);
                return;
            }

            // if in is null due to range being 'na' and there is no new partner (thus NPA == null) then process this person using the current NPA range (na)
            if(newPartnerAge == null && in == null) {
                node.processPerson(person, currentDate);
                return;
            }

        }

        // if we get here then the age range we want hasn't been created yet

        if(newPartnerAge == null) {
            // if no NPA then a 'na' range hasn't been created yet - so we create it
            addChild(new IntegerRange("na")).processPerson(person, currentDate);
        } else {

            // this accessing of the statistical code isn't to calculate new values - we just use it to get the age
            // ranges from the stats tables
            Integer age = ((AgeNodeInt) getAncestor(new AgeNodeInt())).getOption().getValue();

            double numberOfFemales = getCount();
            CompoundTimeUnit timePeriod = new CompoundTimeUnit(1, TimeUnit.YEAR);

            MultipleDeterminedCount mDC = (MultipleDeterminedCount) getInputStats()
                    .getDeterminedCount(new PartneringStatsKey(age, numberOfFemales, timePeriod, currentDate), null);

            // getting the age range labels
            Set<IntegerRange> options = mDC.getRawUncorrectedCount().getLabels();

            // finding which the persons partner is in and creating it
            for (IntegerRange o : options) {
                if (o.contains(newPartnerAge)) {
                    addChild(o).processPerson(person, currentDate);
                    return;
                }
            }
        }



    }

    @Override
    public String getVariableName() {
        return "Separated";
    }

    @Override
    public Node<IntegerRange, ?, Integer, ?> makeChildInstance(IntegerRange childOption, Integer initCount) {
        return new NewPartnerAgeNodeInt(childOption, this, initCount);
    }
}
