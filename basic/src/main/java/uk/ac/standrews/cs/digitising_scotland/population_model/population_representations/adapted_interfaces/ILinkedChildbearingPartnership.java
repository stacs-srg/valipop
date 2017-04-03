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
package uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.adapted_interfaces;

import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.data_structure.Link;

/**
 * Interface for partnership objects.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface ILinkedChildbearingPartnership extends Comparable<ILinkedChildbearingPartnership> {

	/**
	 * Gets the partnership's unique identifier.
	 * @return the partnership's unique identifier
	 */
	int getId();

	/**
	 * Returns and array of Links for the female in the partnership.
	 * @return the possible Links for the female
	 */
	Link[] getPerson1PotentialLinks();

	/**
	 * Returns and array of Links for the male in the partnership.
	 * @return the possible Links of the male
	 */
	Link[] getPerson2PotentialLinks();

	/**
	 * Gets the identifiers of this partnership object's child_id, or null if none are recorded.
	 * @return the identifier of the partnership's child_id
	 */
	Link getChildLink();
}
