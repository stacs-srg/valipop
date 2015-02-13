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

public class LinkedChildbearingPartnership extends AbstractLinkedPartnership implements IPartnership {

    private Link child;
    
    public LinkedChildbearingPartnership(int id, String ref) {
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

	public void addPossibleMaleLink(LinkedPerson father, Evidence[] evidence, float linkHeuristic) {
		Link[] temp = this.male.clone();
		Link[] newArray = new Link[temp.length + 1];
		int c = 0;
		for(Link l : this.male) {
			newArray[c++] = l;
		}
		newArray[c] = new Link(father, this, evidence, linkHeuristic);
		this.male = newArray;
	}
	
	public void addPossibleFemaleLink(LinkedPerson mother, Evidence[] evidence, float linkHeuristic) {
		Link[] temp = this.female.clone();
		Link[] newArray = new Link[temp.length + 1];
		int c = 0;
		for(Link l : this.female) {
			newArray[c++] = l;
		}
		newArray[c] = new Link(mother, this, evidence, linkHeuristic);
		this.female = newArray;
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
