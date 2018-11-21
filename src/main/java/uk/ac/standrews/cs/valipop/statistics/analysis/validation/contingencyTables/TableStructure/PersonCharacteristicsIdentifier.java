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
package uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure;

import org.apache.commons.math3.random.JDKRandomGenerator;
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SeparationOption;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.ValipopDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PersonCharacteristicsIdentifier {

    private static final JDKRandomGenerator RANDOM_GENERATOR;
    private static final int DETERMINISTIC_SEED = 458457824;

    static {
        RANDOM_GENERATOR = new JDKRandomGenerator();
        RANDOM_GENERATOR.setSeed(DETERMINISTIC_SEED);
    }

    public static IPartnership getActivePartnership(IPerson person, ValipopDate currentDate) {

        ArrayList<IPartnership> partnershipsInYear = new ArrayList<>(person.getPartnershipsActiveInYear(currentDate.getYearDate()));

        if (partnershipsInYear.size() > 1) {
            throw new UnsupportedOperationException("Lots of partners in year - likely for a female to get this error");
        } else if (partnershipsInYear.size() == 0) {
            return null;
        } else {
            return partnershipsInYear.get(0);
        }
    }

    public static Integer getChildrenBirthedInYear(IPartnership activePartnership, YearDate year) {

        if (activePartnership == null) {
            return 0;
        }

        Collection<IPerson> children = activePartnership.getChildren();

        int count = 0;

        for (IPerson child : children) {
            if (child.bornInYear(year)) {
                count++;
            }
        }

        return count;
    }

    public static Integer getChildrenBirthedBeforeDate(IPartnership activePartnership, ValipopDate year) {

        if (activePartnership == null) {
            return 0;
        }

        Collection<IPerson> children = activePartnership.getChildren();

        int count = 0;

        for (IPerson child : children) {
            if (DateUtils.dateBefore(child.getBirthDate(), year)) {
                count++;
            }
        }

        return count;
    }

    public static SeparationOption toSeparate(IPartnership activePartnership, YearDate year) {

        if (activePartnership == null) {
            return SeparationOption.NA;
        }

        IPerson lastChild = activePartnership.getLastChild();

        if (!lastChild.bornInYear(year)) {
            return SeparationOption.NO;
        } else if (activePartnership.getSeparationDate(RANDOM_GENERATOR) != null) { // TODO Would this be better to use earliest possible sep date?
            return SeparationOption.YES;
        } else {
            return SeparationOption.NO;
        }
    }

    public static boolean startedInYear(IPartnership activePartnership, YearDate year) {

        return activePartnership.getPartnershipDate().getYear() == year.getYear();
    }
}
