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

import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ProposedPartnership {

    IPersonExtended male;
    IntegerRange malesRange;

    IPersonExtended female;

    int numberOfChildren;

    public ProposedPartnership(IPersonExtended male, IPersonExtended female, IntegerRange malesRange, int numberOfChildren) {
        this.male = male;
        this.female = female;
        this.malesRange = malesRange;
        this.numberOfChildren = numberOfChildren;
        male.willSeparate(false);
        female.willSeparate(false);
    }

    public void setMale(IPersonExtended newMale, IntegerRange newMalesRange) {
        this.male = newMale;
        this.malesRange = newMalesRange;
        male.willSeparate(false);
    }

    public IntegerRange getMalesRange() {
        return malesRange;
    }

    public IPersonExtended getFemale() {
        return female;
    }

    public IPersonExtended getMale() {
        return male;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }
}
