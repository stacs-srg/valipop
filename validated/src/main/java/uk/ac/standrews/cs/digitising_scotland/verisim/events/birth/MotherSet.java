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
package uk.ac.standrews.cs.digitising_scotland.verisim.events.birth;

import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MotherSet {

    private Collection<IPersonExtended> havePartners;
    private Collection<IPersonExtended> needPartners;
    private int newlyProducedChildren;

    public MotherSet(Collection<IPersonExtended> havePartners, Collection<IPersonExtended> needPartners) {
        this.havePartners = havePartners;
        this.needPartners = needPartners;
    }

    public MotherSet(Collection<IPersonExtended> havePartners, Collection<IPersonExtended> needPartners, int newlyProducedChildren) {
        this(havePartners, needPartners);
        this.newlyProducedChildren = newlyProducedChildren;
    }

    public Collection<IPersonExtended> getHavePartners() {
        return havePartners;
    }

    public Collection<IPersonExtended> getNeedPartners() {
        return needPartners;
    }

    public int size() {
        return havePartners.size() + needPartners.size();
    }

    public int getNewlyProducedChildren() {
        return newlyProducedChildren;
    }
}
