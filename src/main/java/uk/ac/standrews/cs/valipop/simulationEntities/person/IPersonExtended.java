/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
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
package uk.ac.standrews.cs.valipop.simulationEntities.person;


import uk.ac.standrews.cs.valipop.model.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.entityExtensions.PersonUtils;
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;

import java.util.List;

/**
 * Interface for person objects.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public interface IPersonExtended extends IPerson, Comparable<IPersonExtended>, PersonUtils {

    /**
     * Gets the person's date of birth.
     *
     * @return the person's date of birth
     */
    ExactDate getBirthDate_ex();

    /**
     * Gets the person's date of death, or null if they are living.
     *
     * @return the person's date of death
     */
    ExactDate getDeathDate_ex();

    /**
     * Gets the identifiers of the person's partnerships, or null if none are recorded.
     *
     * @return the identifiers of the person's partnerships
     */
    List<IPartnershipExtended> getPartnerships_ex();

    /**
     * Gets the identifier of the person's parents' partnership, or -1 if none are recorded.
     *
     * @return the identifier of the person's parents' partnership
     */
    IPartnershipExtended getParentsPartnership_ex();

    boolean isIllegitimate();

    List<IPartnershipExtended> getPartnershipsBeforeDate(Date date);

    ExactDate getDateOfLastLegitimatePartnershipEventBeforeDate(ExactDate date);
}
