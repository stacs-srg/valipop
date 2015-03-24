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

/**
 * Link class, used to show a potentional connection between a person and a partnership along with a heuristic linkage value and evidence of link.
 * @author Tom Dalton
 *
 */
public class Link implements Comparable<Link> {

	private Evidence[] provenance;
	private float heuriticOfLinkValue;

	private LinkedPerson linkedPerson;
	private IntermediaryLinkObject intermediaryLinkedObject;

	public Link(LinkedPerson person, IntermediaryLinkObject partnership, Evidence[] records, float heuristic) {
		linkedPerson = person;
		intermediaryLinkedObject = partnership;
		provenance = records;
		if(partnership.getClass() == LinkedChildbearingPartnership.class) {
			linkedPerson.addPartnershipLink(this);
		} else if(partnership.getClass() == LinkedSiblings.class) {
			linkedPerson.addSiblingLink(this);
		}
		heuriticOfLinkValue = heuristic;
	}

	public Link(LinkedPerson child, LinkedChildbearingPartnership partnership, Evidence[] records) {
		linkedPerson = child;
		intermediaryLinkedObject = partnership;
		provenance = records;
		linkedPerson.setParentPartnershipLink(this);
		heuriticOfLinkValue = 1;
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
	
	public IntermediaryLinkObject getLinkedIntermediaryObject() {
		return intermediaryLinkedObject;
	}
	
	public void setLinkedPartnership(LinkedChildbearingPartnership linkedPartnership) {
		this.intermediaryLinkedObject = linkedPartnership;
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
	public int compareTo(Link o) {
		return Float.compare(o.getHeuriticOfLinkValue(), this.getHeuriticOfLinkValue());
	}


}
