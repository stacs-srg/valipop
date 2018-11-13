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
package uk.ac.standrews.cs.valipop.implementations;

import uk.ac.standrews.cs.utilities.DateManipulation;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.ValipopDate;

import java.util.Date;

/**
 * Defines various methods to do with population structure and constraints.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PopulationLogic {

    private static final int MINIMUM_MOTHER_AGE_AT_CHILDBIRTH = 12;
    private static final int MAXIMUM_MOTHER_AGE_AT_CHILDBIRTH = 55;
    private static final int MAX_GESTATION_IN_DAYS = 300;
    private static final int MINIMUM_FATHER_AGE_AT_CHILDBIRTH = 12;
    private static final int MAXIMUM_FATHER_AGE_AT_CHILDBIRTH = 100;

    /**
     * Checks whether the ages of the given parents are sensible for the given child.
     *
     * @param father the father
     * @param mother the mother
     * @param child  the child
     * @return true if the parents' ages are sensible
     */
    @SuppressWarnings("FeatureEnvy")
    public static boolean parentsHaveSensibleAgesAtChildBirth(final IPerson father, final IPerson mother, final IPerson child) {

        final ValipopDate mother_birth_date = mother.getBirthDate();
        final ValipopDate mother_death_date = mother.getDeathDate();

        final ValipopDate father_birth_date = father.getBirthDate();
        final ValipopDate father_death_date = father.getDeathDate();

        final ValipopDate child_birth_date = child.getBirthDate();

        return parentsHaveSensibleAgesAtChildBirth(father_birth_date, father_death_date, mother_birth_date, mother_death_date, child_birth_date);
    }

    private static boolean parentsHaveSensibleAgesAtChildBirth(final ValipopDate father_birth_date, final ValipopDate father_death_date, final ValipopDate mother_birth_date, final ValipopDate mother_death_date, final ValipopDate child_birth_date) {

        boolean motherAlive = motherAliveAtBirth(mother_death_date, child_birth_date);
        boolean motherNotTooYoung = motherNotTooYoungAtBirth(mother_birth_date, child_birth_date);
        boolean motherNotTooOld = motherNotTooOldAtBirth(mother_birth_date, child_birth_date);
        boolean fatherAliveAtConception = fatherAliveAtConception(father_death_date, child_birth_date);
        boolean fatherNotTooYoung = fatherNotTooYoungAtBirth(father_birth_date, child_birth_date);
        boolean fatherNotTooOld = fatherNotTooOldAtBirth(father_birth_date, child_birth_date);

        return motherAlive && motherNotTooYoung && motherNotTooOld && fatherAliveAtConception && fatherNotTooYoung && fatherNotTooOld;
    }

    private static boolean motherAliveAtBirth(final ValipopDate mother_death_date, final ValipopDate child_birth_date) {

        return mother_death_date == null || dateNotAfter(child_birth_date.getDate(), mother_death_date.getDate());
    }

    private static boolean motherNotTooYoungAtBirth(final ValipopDate mother_birth_date, final ValipopDate child_birth_date) {

        final int mothers_age_at_birth = parentsAgeAtChildBirth(mother_birth_date, child_birth_date);

        return notLessThan(mothers_age_at_birth, MINIMUM_MOTHER_AGE_AT_CHILDBIRTH);
    }

    private static boolean motherNotTooOldAtBirth(final ValipopDate mother_birth_date, final ValipopDate child_birth_date) {

        final int mothers_age_at_birth = parentsAgeAtChildBirth(mother_birth_date, child_birth_date);

        return notGreaterThan(mothers_age_at_birth, MAXIMUM_MOTHER_AGE_AT_CHILDBIRTH);
    }

    private static boolean fatherAliveAtConception(final ValipopDate father_death_date, final ValipopDate child_birth_date) {

        return father_death_date == null || dateNotAfter(child_birth_date.getDate(), DateManipulation.addDays(father_death_date.getDate(), MAX_GESTATION_IN_DAYS));
    }

    private static boolean fatherNotTooYoungAtBirth(final ValipopDate father_birth_date, final ValipopDate child_birth_date) {

        final int fathers_age_at_birth = parentsAgeAtChildBirth(father_birth_date, child_birth_date);

        return notLessThan(fathers_age_at_birth, MINIMUM_FATHER_AGE_AT_CHILDBIRTH);
    }

    private static boolean fatherNotTooOldAtBirth(final ValipopDate father_birth_date, final ValipopDate child_birth_date) {

        final int fathers_age_at_birth = parentsAgeAtChildBirth(father_birth_date, child_birth_date);

        return notGreaterThan(fathers_age_at_birth, MAXIMUM_FATHER_AGE_AT_CHILDBIRTH);
    }

    private static int parentsAgeAtChildBirth(final ValipopDate parent_birth_date, final ValipopDate child_birth_date) {

        return DateManipulation.differenceInYears(parent_birth_date.getDate(), child_birth_date.getDate());
    }

    private static boolean notLessThan(final int i1, final int i2) {

        return i1 >= i2;
    }

    private static boolean notGreaterThan(final int i1, final int i2) {

        return i1 <= i2;
    }

    private static boolean dateNotAfter(final Date date1, final Date date2) {

        return DateManipulation.differenceInDays(date1, date2) >= 0;
    }
}
