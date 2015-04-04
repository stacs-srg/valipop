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

import java.util.Date;

import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.Link;

public interface ILinkedMarriagePartnership {

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
     * Gets the date of the marriage between the partners in this partnership, or null if they are not married.
     * @return the date of the marriage of this partnership
     */
    Date getMarriageDate();

    /**
     * Gets the place of marriage, or null if not recorded.
     * @return the place of marriage
     */
    String getMarriagePlace();
	
}
