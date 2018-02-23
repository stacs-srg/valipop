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
package uk.ac.standrews.cs.valipop.simulationEntities.partnership;

import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.basic_model.model.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.entityExtensions.PartnershipUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPersonExtended;

import java.util.List;

/**
 * Interface for partnership objects.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public interface IPartnershipExtended extends IPartnership, PartnershipUtils {

    /**
     * Gets the female in the partnership.
     *
     * @return the female
     */
    IPersonExtended getFemalePartner();

    /**
     * Gets the male in the partnership.
     *
     * @return the male
     */
    IPersonExtended getMalePartner();

    /**
     * Gets the identifier of the partner of the person with the given identifier, or -1 if neither member
     * of this partnership has the given identifier.
     *
     * @param person the person
     * @return the identifier of the partner of the person with the given identifier
     */
    IPersonExtended getPartnerOf(IPersonExtended person);

    /**
     * Gets the identifiers of the partnership's child_ids, or null if none are recorded.
     *
     * @return the identifiers of the partnership's child_ids
     */
    List<IPersonExtended> getChildren();

    Date getPartnershipDate();

    Date getSeparationDate(RandomGenerator randomGenerator);

    void setMarriageDate(Date marriageDate);

    Date getMarriageDate_ex();
}
