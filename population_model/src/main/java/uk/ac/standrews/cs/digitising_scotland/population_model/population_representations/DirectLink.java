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
public class DirectLink implements Comparable<Float> {

	private Evidence[] provenance;
	private float heuriticOfLinkValue;

	private LinkedPerson linkedPerson;
	private AbstractLinkedPartnership linkedPartnershipObject;

	public DirectLink(LinkedPerson person, AbstractLinkedPartnership partnership, Evidence[] records, float heuristic) {
		linkedPerson = person;
		linkedPartnershipObject = partnership;
		provenance = records;
		linkedPerson.addPartnershipLink(this);
		heuriticOfLinkValue = heuristic;
	}

	public DirectLink(LinkedPerson child, LinkedChildbearingPartnership partnership, Evidence[] records) {
		linkedPerson = child;
		linkedPartnershipObject = partnership;
		provenance = records;
		linkedPerson.setParentPartnershipLink(this);
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
	
	public AbstractLinkedPartnership getLinkedPartnership() {
		return linkedPartnershipObject;
	}
	
	public void setLinkedPartnership(LinkedChildbearingPartnership linkedPartnership) {
		this.linkedPartnershipObject = linkedPartnership;
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
