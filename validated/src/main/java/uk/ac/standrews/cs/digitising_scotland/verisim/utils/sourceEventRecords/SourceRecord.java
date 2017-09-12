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
package uk.ac.standrews.cs.digitising_scotland.verisim.utils.sourceEventRecords;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.utilities.DateManipulation;

import java.util.Date;

/**
 * Created by graham on 13/05/2014.
 */
public abstract class SourceRecord {

    public static final String SEPARATOR = ",";

    protected String uid;
    protected String entry;
    protected String entry_corrected;

    protected String registration_year;
    protected String registration_district_number;
    protected String registration_district_suffix;

    protected String image_quality;

    public String getUid() {
        return uid;
    }

    public void setUid(final String uid) {
        this.uid = uid;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(final String entry) {
        this.entry = entry;
    }

    public String getEntryCorrected() {
        return entry_corrected;
    }

    public void setEntryCorrected(final String entry_corrected) {
        this.entry_corrected = entry_corrected;
    }

    public String getRegistrationYear() {
        return registration_year;
    }

    public void setRegistrationYear(final String registration_year) {
        this.registration_year = registration_year;
    }

    public String getRegistrationDistrictNumber() {
        return registration_district_number;
    }

    public void setRegistrationDistrictNumber(final String registration_district_number) {
        this.registration_district_number = registration_district_number;
    }

    public String getRegistrationDistrictSuffix() {
        return registration_district_suffix;
    }

    public void setRegistrationDistrictSuffix(final String registration_district_suffix) {
        this.registration_district_suffix = registration_district_suffix;
    }

    public String getImageQuality() {
        return image_quality;
    }

    public void setImageQuality(final String image_quality) {
        this.image_quality = image_quality;
    }

    protected String getMaidenSurname(IPopulation population, IPerson person) {

        int parents_partnership_id = person.getParentsPartnership();

        if (parents_partnership_id != -1) {

            IPartnership parents_partnership = population.findPartnership(parents_partnership_id);
            IPerson father = population.findPerson(parents_partnership.getMalePartnerId());
            return father.getSurname();

        } else return null;
    }

    protected String getRecordedParentsSurname(final String parents_surname, final String childs_surname) {

        return parents_surname.equals(childs_surname) ? "0" : parents_surname;
    }

    protected void append(final StringBuilder builder, final Object... fields) {

        for (Object field : fields) {
            append(builder, field != null ? field.toString().toUpperCase() : null);
        }
    }

    protected void append(final StringBuilder builder, final String field) {

        if (field != null) {
            builder.append(field);
        }
        builder.append(SEPARATOR);
    }

    protected int fullYearsBetween(Date d1, Date d2) {

        return d1.before(d2) ? DateManipulation.differenceInYears(d1, d2) : DateManipulation.differenceInYears(d2, d1);
    }

    /**
     * Created by graham on 14/05/2014.
     */
    public static class DateRecord {

        private String day;
        private String month;
        private String year;

        public String getDay() {
            return day;
        }

        public void setDay(final String day) {
            this.day = day;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(final String month) {
            this.month = month;
        }

        public String getYear() {
            return year;
        }

        public void setYear(final String year) {
            this.year = year;
        }
    }
}
