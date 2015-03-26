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

public class LinkedSiblings extends IntermediaryLinkObject {
	
    private SiblingType siblingType;
    
    public LinkedSiblings(int id, String ref) {
    	this.id = id;
    	this.ref = ref;
	}
    
    public void addPossibleSibling1Link(LinkedPerson sibling1, Evidence[] evidence, float linkHeuristic) {
		Link[] temp = this.person1.clone();
		Link[] newArray = new Link[temp.length + 1];
		int c = 0;
		for(Link l : this.person1) {
			newArray[c++] = l;
		}
		newArray[c] = new Link(sibling1, this, evidence, linkHeuristic);
		this.person1 = newArray;
	}
	
	public void addPossibleSibling2Link(LinkedPerson sibling2, Evidence[] evidence, float linkHeuristic) {
		Link[] temp = this.person2.clone();
		Link[] newArray = new Link[temp.length + 1];
		int c = 0;
		for(Link l : this.person2) {
			newArray[c++] = l;
		}
		newArray[c] = new Link(sibling2, this, evidence, linkHeuristic);
		this.person2 = newArray;
	}
	
    public Link[] getSibling1PotentialLinks() {
        return person1;
    }

    public Link[] getSibling2PotentialLinks() {
        return person2;
    }
    
    public int getId() {
        return id;
    }

}
