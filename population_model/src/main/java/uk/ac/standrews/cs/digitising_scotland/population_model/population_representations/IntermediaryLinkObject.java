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

public abstract class IntermediaryLinkObject {

    protected Integer id;
    protected String ref;
    protected Link[] person1 = new Link[0];
    protected Link[] person2 = new Link[0];
	
    public void addPossiblePerson1Link(LinkedPerson person1, Evidence[] evidence, float linkHeuristic) {
		Link[] temp = this.person1.clone();
		Link[] newArray = new Link[temp.length + 1];
		int c = 0;
		for(Link l : this.person1) {
			newArray[c++] = l;
		}
		newArray[c] = new Link(person1, this, evidence, linkHeuristic);
		this.person1 = newArray;
	}
	
	public void addPossiblePerson2Link(LinkedPerson person2, Evidence[] evidence, float linkHeuristic) {
		Link[] temp = this.person2.clone();
		Link[] newArray = new Link[temp.length + 1];
		int c = 0;
		for(Link l : this.person2) {
			newArray[c++] = l;
		}
		newArray[c] = new Link(person2, this, evidence, linkHeuristic);
		this.person2 = newArray;
	}
	
    public Link[] getPerson1PotentialPartnerLinks() {
        return person1;
    }

    public Link[] getPerson2PotentialPartnerLinks() {
        return person2;
    }
    
    public String getRef() {
    	return ref;
    }
	
}
