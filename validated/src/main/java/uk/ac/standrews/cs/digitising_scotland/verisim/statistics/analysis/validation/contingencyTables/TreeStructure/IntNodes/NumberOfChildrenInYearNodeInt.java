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
package uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.IntNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.IntNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NumberOfChildrenInYearNodeInt extends IntNode<Integer, IntegerRange> {

    public NumberOfChildrenInYearNodeInt(Integer option, ChildrenInYearNodeInt parentNode, Integer initCount) {
        super(option, parentNode, initCount);
    }

    public NumberOfChildrenInYearNodeInt() {
        super();
    }


    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {
        incCountByOne();

        int prevChildren = ((PreviousNumberOfChildrenInPartnershipNodeInt)
                getAncestor(new PreviousNumberOfChildrenInPartnershipNodeInt())).getOption().getValue();

        int childrenThisYear = ((NumberOfChildrenInYearNodeInt)
                getAncestor(new NumberOfChildrenInYearNodeInt())).getOption();

        int ncip = prevChildren + childrenThisYear;
        IntegerRange range = resolveToChildRange(ncip);

        try {
            getChild(range).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(range).processPerson(person, currentDate);
        }
    }

    @Override
    public String getVariableName() {
        return "NCIY";
    }

    @Override
    public Node<IntegerRange, ?, Integer, ?> makeChildInstance(IntegerRange childOption, Integer initCount) {
        return new NumberOfChildrenInPartnershipNodeInt(childOption, this, initCount);
    }

    @SuppressWarnings("Duplicates")
    private IntegerRange resolveToChildRange(Integer ncip) {

        for(Node<IntegerRange, ?, ?, ?> aN : getChildren()) {
            if(aN.getOption().contains(ncip)) {
                return aN.getOption();
            }
        }

        YearDate yob = ((YOBNodeInt) getAncestor(new YOBNodeInt())).getOption();
        Integer age = ((AgeNodeInt) getAncestor(new AgeNodeInt())).getOption().getValue();

        Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

        Collection<IntegerRange> sepRanges = getInputStats().getSeparationByChildCountRates(currentDate).getLabels();

        for(IntegerRange o : sepRanges) {
            if(o.contains(ncip)) {
                return o;
            }
        }

        if(ncip == 0) {
            return new IntegerRange(0);
        }

        throw new Error("Did not resolve any permissable ranges");
    }
}
