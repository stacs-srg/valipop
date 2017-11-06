/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
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
package uk.ac.standrews.cs.basic_model.population_representations.data_structure;

import java.util.Date;

import uk.ac.standrews.cs.basic_model.population_representations.adapted_interfaces.ILinkedMarriagePartnership;

/**
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 *
 */
public class MarriageBridge extends IntermediaryLinkObject implements ILinkedMarriagePartnership {

	public MarriageBridge(int id, String ref) {
		this.id = id;
		this.ref = ref;
	}

	public String getRef() {
		return ref;
	}

	public void addPossibleHusbandLink(LinkedPerson husband, Evidence[] evidence, float linkHeuristic) {
		addPossiblePerson1Link(husband, evidence, linkHeuristic);
	}

	public void addPossibleWifeLink(LinkedPerson wife, Evidence[] evidence, float linkHeuristic) {
		addPossiblePerson2Link(wife, evidence, linkHeuristic);
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public Link[] getPerson1PotentialLinks() {
		return person2;
	}

	@Override
	public Link[] getPerson2PotentialLinks() {
		return person1;
	}

	public int compareTo(final ILinkedMarriagePartnership o) {
		if (this.equals(o)) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public Date getMarriageDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMarriagePlace() {
		// TODO Auto-generated method stub
		return null;
	}


}
