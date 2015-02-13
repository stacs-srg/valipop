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

import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.adapted_interfaces.IPartnership;

public abstract class AbstractLinkedPartnership implements IPartnership {

    protected Integer id;
    protected String ref;
    protected DirectLink[] male = new DirectLink[0];
    protected DirectLink[] female = new DirectLink[0];
	
    public void addPossibleMaleLink(LinkedPerson male, Evidence[] evidence, float linkHeuristic) {
		DirectLink[] temp = this.male.clone();
		DirectLink[] newArray = new DirectLink[temp.length + 1];
		int c = 0;
		for(DirectLink l : this.male) {
			newArray[c++] = l;
		}
		newArray[c] = new DirectLink(male, this, evidence, linkHeuristic);
		this.male = newArray;
	}
	
	public void addPossibleFemaleLink(LinkedPerson female, Evidence[] evidence, float linkHeuristic) {
		DirectLink[] temp = this.female.clone();
		DirectLink[] newArray = new DirectLink[temp.length + 1];
		int c = 0;
		for(DirectLink l : this.female) {
			newArray[c++] = l;
		}
		newArray[c] = new DirectLink(female, this, evidence, linkHeuristic);
		this.female = newArray;
	}
	
    public DirectLink[] getFemalePotentialPartnerLinks() {
        return female;
    }

    public DirectLink[] getMalePotentialPartnerLinks() {
        return male;
    }
	
}
