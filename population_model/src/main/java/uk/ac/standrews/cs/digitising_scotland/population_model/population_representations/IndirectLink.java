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
package uk.ac.standrews.cs.digitising_scotland.population_model.population_representations;

public class IndirectLink implements Comparable<Float>{

	private Evidence[] provenance;
	private float heuriticOfLinkValue;

	private LinkedPerson linkedPerson;
	private LinkedSiblings linkedSiblingsObject;
	
	public IndirectLink(LinkedPerson person, LinkedSiblings linkedSiblings, Evidence[] records, float heuristic) {
		linkedPerson = person;
		linkedSiblingsObject = linkedSiblings;
		provenance = records;
		linkedPerson.addSiblingLink(this);
		heuriticOfLinkValue = heuristic;
	}
	
	public Evidence[] getProvenance() {
		return provenance;
	}

	public void setProvenance(Evidence[] provenance) {
		this.provenance = provenance;
	}
	
	public LinkedPerson getLinkedPerson() {
		return linkedPerson;
	}
	
	public void setLinkedPerson(LinkedPerson linkedPerson) {
		this.linkedPerson = linkedPerson;
	}
	
	public LinkedSiblings getLinkedSiblingObject() {
		return linkedSiblingsObject;
	}
	
	public void setLinkedSiblingObject(LinkedSiblings linkedPartnership) {
		this.linkedSiblingsObject = linkedPartnership;
	}

	/**
	 * @return the heuriticOfLinkValue
	 */
	public float getHeuriticOfLinkValue() {
		return heuriticOfLinkValue;
	}

	/**
	 * @param heuriticOfLinkValue the heuriticOfLinkValue to set
	 */
	public void setHeuriticOfLinkValue(float heuriticOfLinkValue) {
		this.heuriticOfLinkValue = heuriticOfLinkValue;
	}

	@Override
	public int compareTo(Float o) {
		if(o > heuriticOfLinkValue)
			return -1;
		else if (o < heuriticOfLinkValue)
			return 1;
		else
			return 0;
	}
	
}
