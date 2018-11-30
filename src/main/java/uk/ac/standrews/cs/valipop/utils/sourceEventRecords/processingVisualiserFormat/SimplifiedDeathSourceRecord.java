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
package uk.ac.standrews.cs.valipop.utils.sourceEventRecords.processingVisualiserFormat;

import uk.ac.standrews.cs.utilities.DateManipulation;
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.population.IPopulation;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.IndividualSourceRecord;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A representation of a Death Record in the form used by the Digitising Scotland Project.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SimplifiedDeathSourceRecord extends IndividualSourceRecord {

    public static final long FIRST_YEAR_DOB_PRESENT = 1966;
    private static final SimpleDateFormat DOB_DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

    private DateRecord death_date;

    private String death_age;
    private String death_age_changed;

    private String death_place;
    private String death_cause_a;
    private String death_cause_b;
    private String death_cause_c;
    private String certifying_doctor;

    private String birth_date;
    private String occupation;

    private String father_deceased;
    private String mother_deceased;

    private String marital_status;
    private String spouses_names;
    private String spouses_id = "";
    private String spouses_occupations;

    public SimplifiedDeathSourceRecord(final IPerson person, IPopulation population) {

        death_date = new DateRecord();

        // Attributes associated with individual
        setUid(String.valueOf(person.getId()));
        setSex(String.valueOf(person.getSex()));
        setForename(person.getFirstName());
        setSurname(person.getSurname());
        setOccupation(person.getOccupation());
        setDeathCauseA(person.getDeathCause());

        List<IPartnership> partnerships = person.getPartnerships();

        if (partnerships.size() != 0) {
            IPerson spouse = partnerships.get(partnerships.size() - 1).getPartnerOf(person);
            setSpousesNames(spouse.getFirstName() + " " + spouse.getSurname());
            setSpousesId(String.valueOf(spouse.getId()));
        }

        Date birth_date = person.getBirthDate().getDate();
        Date death_date = person.getDeathDate().getDate();

        processDates(birth_date, death_date);

        final IPartnership parents_partnership = person.getParents();
        if (parents_partnership != null) {

            setParentAttributes(person, population, parents_partnership);
        }
    }

    public String getDeathDay() {
        return death_date.getDay();
    }

    public void setDeathDay(final String death_day) {
        death_date.setDay(death_day);
    }

    public String getDeathMonth() {
        return death_date.getMonth();
    }

    public void setDeathMonth(final String death_month) {
        death_date.setMonth(death_month);
    }

    public String getDeathYear() {
        return death_date.getYear();
    }

    public void setDeathYear(final String death_year) {
        death_date.setYear(death_year);
    }

    public String getDeathAge() {
        return death_age;
    }

    public void setDeathAge(final String death_age) {
        this.death_age = death_age;
    }

    public String getDeathAgeChanged() {
        return death_age_changed;
    }

    public void setDeathAgeChanged(final String death_age_changed) {
        this.death_age_changed = death_age_changed;
    }

    public String getDeathPlace() {
        return death_place;
    }

    public void setDeathPlace(final String death_place) {
        this.death_place = death_place;
    }

    public String getDeathCauseA() {
        return death_cause_a;
    }

    public void setDeathCauseA(final String death_cause_a) {
        this.death_cause_a = death_cause_a;
    }

    public String getDeathCauseB() {
        return death_cause_b;
    }

    public void setDeathCauseB(final String death_cause_b) {
        this.death_cause_b = death_cause_b;
    }

    public String getDeathCauseC() {
        return death_cause_c;
    }

    public void setDeathCauseC(final String death_cause_c) {
        this.death_cause_c = death_cause_c;
    }

    public String getCertifyingDoctor() {
        return certifying_doctor;
    }

    public void setCertifyingDoctor(final String certifying_doctor) {
        this.certifying_doctor = certifying_doctor;
    }

    public String getBirthDate() {
        return birth_date;
    }

    public void setBirthDate(final String birth_date) {
        this.birth_date = birth_date;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(final String occupation) {
        this.occupation = occupation;
    }

    public String getFatherDeceased() {
        return father_deceased;
    }

    public void setFatherDeceased(final String father_deceased) {
        this.father_deceased = father_deceased;
    }

    public String getMotherDeceased() {
        return mother_deceased;
    }

    public void setMotherDeceased(final String mother_deceased) {
        this.mother_deceased = mother_deceased;
    }

    public String getMaritalStatus() {
        return marital_status;
    }

    public void setMaritalStatus(final String marital_status) {
        this.marital_status = marital_status;
    }

    public String getSpouses_id() {
        return spouses_id;
    }

    public void setSpousesId(String id) {
        this.spouses_id = id;
    }

    public String getSpousesNames() {
        return spouses_names;
    }

    public void setSpousesNames(final String spouses_names) {
        this.spouses_names = spouses_names;
    }

    public String getSpousesOccupations() {
        return spouses_occupations;
    }

    public void setSpousesOccupations(final String spouses_occupations) {

        this.spouses_occupations = spouses_occupations;
    }

    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder();

        int rnd;

        if (fathers_id != null) {
            rnd = new Random().nextInt(101);
            RelationshipsTable.relationshipsFather.add(new String[]{"Father", String.valueOf(uid), String.valueOf(fathers_id), String.valueOf(rnd), death_date.getDay() + "." + death_date.getMonth() + "." + death_date.getYear()});
        }

        if (mothers_id != null) {
            rnd = new Random().nextInt(101);
            RelationshipsTable.relationshipsMother.add(new String[]{"Mother", String.valueOf(uid), String.valueOf(mothers_id), String.valueOf(rnd), death_date.getDay() + "." + death_date.getMonth() + "." + death_date.getYear()});
        }

        if (fathers_id != null && mothers_id != null) {
            rnd = new Random().nextInt(101);
            RelationshipsTable.relationshipsMarriage.add(new String[]{"Marriage", String.valueOf(fathers_id), String.valueOf(mothers_id), String.valueOf(rnd), "-"});
        }

        if (!Objects.equals(spouses_id, "")) {
            rnd = new Random().nextInt(101);
            RelationshipsTable.relationshipsMarriage.add(new String[]{"Marriage", String.valueOf(uid), String.valueOf(spouses_id), String.valueOf(rnd), "-"});
        }


        append(builder, uid, forename + " " + surname, sex, fathers_id, fathers_forename + " " + fathers_surname,
                mothers_id, mothers_forename + " " + mothers_surname,
                spouses_id, spouses_names,
                death_date.getDay() + "." + death_date.getMonth() + "." + death_date.getYear(),
                death_place, registration_district_suffix, death_cause_a);

        return builder.toString();
    }

    private void processDates(final Date birth_date, final Date death_date) {

        if (death_date != null) {

            long death_year = DateManipulation.dateToYear(death_date);

            setDeathYear(String.valueOf(death_year));
            setDeathAge(String.valueOf(fullYearsBetween(birth_date, death_date)));

            if (death_year >= SimplifiedDeathSourceRecord.FIRST_YEAR_DOB_PRESENT) {
                setBirthDate(DateManipulation.formatDate(birth_date, DOB_DATE_FORMAT));
            }
        }
    }

    @Override
    public String getHeaders() {

        final StringBuilder builder = new StringBuilder();

        append(builder, "uid", "full_name", "sex", "fathers_id", "fathers_name", "mothers_id", "mothers_name",
                "spouses_id", "spouses_names", "death_date", "death_place", "registration_district_suffix", "death_cause_a");

        return builder.toString();
    }
}
