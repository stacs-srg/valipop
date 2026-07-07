/*
 * valipop - <https://github.com/stacs-srg/valipop>
 * Copyright © 2026 Systems Research Group, University of St Andrews (graham.kirby@st-andrews.ac.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.IntNodes;

import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure.Utilities;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.IntNode;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Node;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.SeparationOption;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.MultipleDeterminedCountByIntegerRange;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.PartneringStatsKey;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRange;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.InvalidRangeException;

import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.util.Set;

import static uk.ac.standrews.cs.valipop.simulationEntities.PopulationNavigation.ageOnDate;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SeparationNodeInt extends IntNode<SeparationOption, IntegerRange> {

    public SeparationNodeInt(final SeparationOption option, final NumberOfChildrenInPartnershipNodeInt parentNode, final int initCount) {
        super(option, parentNode, initCount);
    }

    @Override
    public void processPerson(final IPerson person, final LocalDate currentDate) {

        incCountByOne();

        final Integer newPartnerAge = getAgeOfPartnerInActivePartnership(person, currentDate);

        for (final Node<IntegerRange, ?, Integer, ?> node : getChildren()) {

            final IntegerRange range = node.getOption();
            final Boolean withinRange = isWithinRange(range, newPartnerAge);

            // if there is no new partner (thus newPartnerAge == null), or if new partner age is in the considered range,
            // then process this person  and return
            if (newPartnerAge == null || (withinRange != null && withinRange)) {
                node.processPerson(person, currentDate);
                return;
            }
        }

        // if we get here then the age range we want hasn't been created yet

        if (newPartnerAge == null) {
            // if no NPA then a 'na' range hasn't been created yet - so we create it
            addChild(new IntegerRange("na")).processPerson(person, currentDate);

        } else {

            // this accessing of the statistical code isn't to calculate new values - we just use it to get the age
            // ranges from the stats tables
            final int age = ((AgeNodeInt) getAncestor(new AgeNodeInt())).getOption().getValue();

            final double numberOfFemales = getCount();
            final Period timePeriod = Period.ofYears(1);

            final MultipleDeterminedCountByIntegerRange counts = (MultipleDeterminedCountByIntegerRange) getInputStats().getDeterminedCount(new PartneringStatsKey(age, numberOfFemales, timePeriod, currentDate), null);

            // getting the age range labels
            final Set<IntegerRange> options = counts.getRawUncorrectedCount().getLabels();

            // finding which the persons partner is in and creating it
            for (final IntegerRange o : options) {
                if (o.contains(newPartnerAge)) {
                    addChild(o).processPerson(person, currentDate);
                    return;
                }
            }
        }
    }

    private static Boolean isWithinRange(final IntegerRange range, final Integer newPartnerAge) {

        if (newPartnerAge == null) return null;

        try {
            return range.contains(newPartnerAge);
        } catch (final InvalidRangeException e) {
            return null;
        }
    }

    private static Integer getAgeOfPartnerInActivePartnership(final IPerson person, final LocalDate currentDate) {

        final IPartnership activePartnership = Utilities.getActivePartnership(person, currentDate);

        if (activePartnership != null && Utilities.startedInYear(activePartnership, Year.of(currentDate.getYear()))) {

            final IPerson partner = activePartnership.getPartnerOf(person);
            final LocalDate partnershipDate = activePartnership.getPartnershipDate();

            return ageOnDate(partner, partnershipDate);
        }
        else return null;
    }

    @Override
    public String getVariableName() {
        return "Separated";
    }

    @Override
    public Node<IntegerRange, ?, Integer, ?> makeChildInstance(final IntegerRange childOption, final Integer initCount) {
        return new NewPartnerAgeNodeInt(childOption, this, initCount);
    }
}
