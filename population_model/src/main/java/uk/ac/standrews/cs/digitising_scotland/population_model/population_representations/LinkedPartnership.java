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

import java.util.Date;

import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.adapted_interfaces.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.adapted_interfaces.IPerson;

public class LinkedPartnership implements IPartnership {

    private Integer id;
    private String ref;
    private Link[] father = new Link[0];
    private Link[] mother = new Link[0];
    private Link child;
    
    public LinkedPartnership(int id, String ref) {
    	this.id = id;
    	this.ref = ref;
	}
    
    public void setChildLink(LinkedPerson child, Evidence[] records) {
    	this.child = new Link(child, this, records);
    }
    
    public String getRef() {
    	return ref;
    }
	
    @Override
    public int getId() {
        return id;
    }

    @Override
    public Link[] getFemalePotentialPartnerLinks() {
        return mother;
    }

    @Override
    public Link[] getMalePotentialPartnerLinks() {
        return father;
    }
    
    @Override
    public int compareTo(final IPartnership o) {
        if (this.equals(o)) {
            return 0;
        } else {
            return 1;
        }
    }

	@Override
	public Link getChildLink() {
		return child;
	}

	public void addPossibleFatherLink(LinkedPerson father, Evidence[] evidence, float linkHeuristic) {
		Link[] temp = this.father.clone();
		Link[] newArray = new Link[temp.length + 1];
		int c = 0;
		for(Link l : this.father) {
			newArray[c++] = l;
		}
		newArray[c] = new Link(father, this, evidence, linkHeuristic);
		this.father = newArray;
	}
	
	public void addPossibleMotherLink(LinkedPerson mother, Evidence[] evidence, float linkHeuristic) {
		Link[] temp = this.mother.clone();
		Link[] newArray = new Link[temp.length + 1];
		int c = 0;
		for(Link l : this.mother) {
			newArray[c++] = l;
		}
		newArray[c] = new Link(mother, this, evidence, linkHeuristic);
		this.mother = newArray;
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
