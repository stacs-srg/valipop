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

public enum SiblingType {

	// A bridge identifying a pairing with ONLY one parent in common confirmed, 
	// implying also that the other parent is NOT common between the two individuals in the pairing
	CONFIRMED_HALF_SIBLINGS,
	// A bridge identifying a pairing with BOTH parents in common confirmed
	CONFIRMED_FULL_SIBLINGS,
	// A bridge identifiying a pairing with one parent in common confirmed 
	// but where the second parent is unknown - thus possibly being the same 
	// giving the possibility that the identified pairing could be full sublings
	CONFIRMED_HALF_POSSIBLE_FULL_SIBLINGS
	
}
