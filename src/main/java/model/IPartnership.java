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
package model;

import utils.PartnershipUtils;
import utils.time.Date;

import java.util.List;

/**
 * Interface for partnership objects.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public interface IPartnership extends Comparable<IPartnership>, PartnershipUtils {

    /**
     * Gets the partnership's unique identifier.
     *
     * @return the partnership's unique identifier
     */
    int getId();

    /**
     * Gets the female in the partnership.
     *
     * @return the female
     */
    IPerson getFemalePartner();

    /**
     * Gets the male in the partnership.
     *
     * @return the male
     */
    IPerson getMalePartner();

    /**
     * Gets the identifier of the partner of the person with the given identifier, or -1 if neither member
     * of this partnership has the given identifier.
     *
     * @param id the identifier
     * @return the identifier of the partner of the person with the given identifier
     */
    IPerson getPartnerOf(IPerson id);

    /**
     * Gets the identifiers of the partnership's child_ids, or null if none are recorded.
     *
     * @return the identifiers of the partnership's child_ids
     */
    List<IPerson> getChildren();

    Date getPartnershipDate();
}
