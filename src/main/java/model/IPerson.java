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

import utils.PersonUtils;
import utils.time.Date;

import java.util.List;

/**
 * Interface for person objects.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public interface IPerson extends Comparable<IPerson>, PersonUtils {

    /**
     * Representation of female sex.
     */
    char FEMALE = 'F';

    /**
     * Representation of male sex.
     */
    char MALE = 'M';

    /**
     * Gets the person's unique identifier. It can be assumed that identifiers are allocated in temporal
     * order, so an older person's identifier is always less than that of a younger person.
     *
     * @return the person's unique identifier
     */
    int getId();

    /**
     * Gets the person's sex, either {@link #FEMALE} or {@link #MALE}.
     *
     * @return the person's sex
     */
    char getSex();

    /**
     * Gets the person's date of birth.
     *
     * @return the person's date of birth
     */
    Date getBirthDate();

    /**
     * Gets the person's date of death, or null if they are living.
     *
     * @return the person's date of death
     */
    Date getDeathDate();

    /**
     * Gets the identifiers of the person's partnerships, or null if none are recorded.
     *
     * @return the identifiers of the person's partnerships
     */
    List<IPartnership> getPartnerships();

    /**
     * Gets the identifier of the person's parents' partnership, or -1 if none are recorded.
     *
     * @return the identifier of the person's parents' partnership
     */
    IPartnership getParentsPartnership();

}
