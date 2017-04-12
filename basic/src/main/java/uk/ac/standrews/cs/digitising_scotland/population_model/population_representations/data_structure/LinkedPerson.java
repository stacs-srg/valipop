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
package uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.data_structure;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.adapted_interfaces.ILinkedPerson;
import uk.ac.standrews.cs.utilities.DateManipulation;

/**
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 *
 */
public class LinkedPerson implements ILinkedPerson {

	private int id;
	private String firstName;
	private String surname = null;
	private String occupation;
	private String causeOfDeath;
	private char sex;
	private List<Link> childbearingPartnerships = new ArrayList<Link>();
	private List<Link> marriagePartnerships = new ArrayList<Link>();
	private List<Link> siblingBridges = new ArrayList<Link>();
	private Link parentPartnershipLink;

	// Days since epoch (1600)
	private int dayOfBirth;
	private int dayOfDeath;

	public LinkedPerson(int id, String firstName, String surname, char sex) {
		this.id = id;
		this.firstName = firstName;
		this.surname = surname;
		this.sex = sex;
	}

	public void setPartnershipLinks(List<Link> partnerships) {
		this.childbearingPartnerships = partnerships;    	
	}

	public void addPartnershipLink(Link partnership) {
		childbearingPartnerships.add(partnership);    	
	}

	public void setMarriageLinks(List<Link> partnerships) {
		this.marriagePartnerships = partnerships;    	
	}

	public void addMarriageLink(Link partnership) {
		marriagePartnerships.add(partnership);    	
	}

	public void setSiblingLinks(List<Link> siblings) {
		this.siblingBridges = siblings;    	
	}

	public void addSiblingLink(Link partnership) {
		siblingBridges.add(partnership);    	
	}



	public void setParentPartnershipLink(Link parentPartnershipLink) {
		this.parentPartnershipLink = parentPartnershipLink;
	}

	/*
	 * INTERFACE METHODS
	 */

	 @Override
	 public int getId() {
		return id;
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public String getSurname() {
		return surname;
	}

	@Override
	public char getSex() {
		return sex;
	}

	@Override
	public Date getBirthDate() {
		return DateManipulation.daysToDate(dayOfBirth);
	}

	@Override
	public String getBirthPlace() {
		return null;
	}

	@Override
	public Date getDeathDate() {
		return DateManipulation.daysToDate(dayOfDeath);
	}

	@Override
	public String getDeathPlace() {
		return null;
	}

	@Override
	public String getDeathCause() {
		return causeOfDeath;
	}

	@Override
	public String getOccupation() {
		return occupation;
	}

	@Override
	public List<Link> getChildBearingPartnerships() {
		return childbearingPartnerships;
	}

	@Override
	public Link getParentsPartnershipLink() {
		return parentPartnershipLink;
	}


	public List<Link> getMarraigePartnerships() {
		return marriagePartnerships;
	}

	public List<Link> getSiblings() {
		return siblingBridges;
	}

}
