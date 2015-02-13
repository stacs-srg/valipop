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

public class LinkedSiblings {

	private Integer id;
	private String ref;
    private IndirectLink[] sibling1 = new IndirectLink[0];
    private IndirectLink[] sibling2 = new IndirectLink[0];
    private boolean fullSibling;
    Evidence[] provenance;
    
    public LinkedSiblings(int id, String ref) {
    	this.id = id;
    	this.ref = ref;
	}
    
    public void addPossibleSibling1Link(LinkedPerson sibling1, Evidence[] evidence, float linkHeuristic) {
		IndirectLink[] temp = this.sibling1.clone();
		IndirectLink[] newArray = new IndirectLink[temp.length + 1];
		int c = 0;
		for(IndirectLink l : this.sibling1) {
			newArray[c++] = l;
		}
		newArray[c] = new IndirectLink(sibling1, this, evidence, linkHeuristic);
		this.sibling1 = newArray;
	}
	
	public void addPossibleSibling2Link(LinkedPerson sibling2, Evidence[] evidence, float linkHeuristic) {
		IndirectLink[] temp = this.sibling2.clone();
		IndirectLink[] newArray = new IndirectLink[temp.length + 1];
		int c = 0;
		for(IndirectLink l : this.sibling2) {
			newArray[c++] = l;
		}
		newArray[c] = new IndirectLink(sibling2, this, evidence, linkHeuristic);
		this.sibling2 = newArray;
	}
	
    public IndirectLink[] getSibling1PotentialLinks() {
        return sibling1;
    }

    public IndirectLink[] getSibling2PotentialLinks() {
        return sibling2;
    }

	public Object getRef() {
		return ref;
	}
	
}
