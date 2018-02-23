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
package uk.ac.standrews.cs.valipop.utils.sourceEventRecords.processingVisuliserFormat;

import uk.ac.standrews.cs.basic_model.model.IPartnership;
import uk.ac.standrews.cs.basic_model.model.IPerson;
import uk.ac.standrews.cs.basic_model.model.IPopulation;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.oldDSformat.SourceRecord;
import uk.ac.standrews.cs.utilities.DateManipulation;

import java.util.Date;
import java.util.Random;

/**
 * A representation of a Marriage Record in the form used by the Digitising Scotland Project.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SimplifiedMarriageSourceRecord extends SourceRecord {

    private DateRecord marriage_date;

    private String denomination;

    private String groom_id;
    private String groom_forename;
    private String groom_forename_changed;
    private String groom_surname;
    private String groom_surname_changed;
    private String groom_did_not_sign;

    private String groom_address;
    private String groom_age_or_date_of_birth;
    private String groom_occupation;
    private String groom_marital_status;

    private String groom_fathers_id;
    private String groom_fathers_forename;
    private String groom_fathers_surname;
    private String groom_father_deceased;

    private String groom_mothers_id;
    private String groom_mothers_forename;
    private String groom_mothers_maiden_surname;
    private String groom_mother_deceased;
    private String groom_fathers_occupation;

    private String bride_id;
    private String bride_forename;
    private String bride_forename_changed;
    private String bride_surname;
    private String bride_surname_changed;
    private String bride_did_not_sign;

    private String bride_address;
    private String bride_age_or_date_of_birth;
    private String bride_occupation;
    private String bride_marital_status;

    private String bride_fathers_id;
    private String bride_fathers_forename;
    private String bride_fathers_surname;
    private String bride_father_deceased;

    private String bride_mothers_id;
    private String bride_mothers_forename;
    private String bride_mothers_maiden_surname;
    private String bride_mother_deceased;
    private String bride_father_occupation;

    public SimplifiedMarriageSourceRecord(final IPartnership partnership, final IPopulation population) {

        marriage_date = new DateRecord();

        setUid(String.valueOf(partnership.getId()));

        IPerson bride = population.findPerson(partnership.getFemalePartnerId());
        IPerson groom = population.findPerson(partnership.getMalePartnerId());

        final Date start_date = partnership.getMarriageDate();

        setMarriageDay(String.valueOf(DateManipulation.dateToDay(start_date)));
        setMarriageMonth(String.valueOf(DateManipulation.dateToMonth(start_date)));
        setMarriageYear(String.valueOf(DateManipulation.dateToYear(start_date)));

        setGroomId(String.valueOf(groom.getId()));
        setGroomForename(groom.getFirstName());
        setGroomSurname(groom.getSurname());
        setGroomOccupation(groom.getOccupation());
        setGroomAgeOrDateOfBirth(String.valueOf(fullYearsBetween(groom.getBirthDate(), start_date)));

        setBrideId(String.valueOf(bride.getId()));
        setBrideForename(bride.getFirstName());
        setBrideSurname(bride.getSurname());
        setBrideOccupation(bride.getOccupation());
        setBrideAgeOrDateOfBirth(String.valueOf(fullYearsBetween(bride.getBirthDate(), start_date)));

        final int groom_parents_partnership_id = groom.getParentsPartnership();
        if (groom_parents_partnership_id != -1) {

            IPartnership groom_parents_partnership = population.findPartnership(groom_parents_partnership_id);

            IPerson groom_mother = population.findPerson(groom_parents_partnership.getFemalePartnerId());
            IPerson groom_father = population.findPerson(groom_parents_partnership.getMalePartnerId());

            setGroomFatherId(String.valueOf(groom_father.getId()));
            setGroomFathersForename(groom_father.getFirstName());
            setGroomFathersSurname(getRecordedParentsSurname(groom_father.getSurname(), groom.getSurname()));
            setGroomFathersOccupation(groom_father.getOccupation());

            setGroomMotherId(String.valueOf(groom_mother.getId()));
            setGroomMothersForename(groom_mother.getFirstName());
            setGroomMothersMaidenSurname(getMaidenSurname(population, groom_mother));
        }

        final int bride_parents_partnership_id = bride.getParentsPartnership();
        if (bride_parents_partnership_id != -1) {

            IPartnership bride_parents_partnership = population.findPartnership(bride_parents_partnership_id);

            IPerson bride_mother = population.findPerson(bride_parents_partnership.getFemalePartnerId());
            IPerson bride_father = population.findPerson(bride_parents_partnership.getMalePartnerId());

            setBrideFatherId(String.valueOf(bride_father.getId()));
            setBrideFathersForename(bride_father.getFirstName());
            setBrideFathersSurname(getRecordedParentsSurname(bride_father.getSurname(), bride.getSurname()));
            setBrideFatherOccupation(bride_father.getOccupation());

            setBrideMotherId(String.valueOf(bride_mother.getId()));
            setBrideMothersForename(bride_mother.getFirstName());
            setBrideMothersMaidenSurname(getMaidenSurname(population, bride_mother));
        }
    }

    public String getMarriageDay() {
        return marriage_date.getDay();
    }

    public void setMarriageDay(final String marriage_day) {
        marriage_date.setDay(marriage_day);
    }

    public String getMarriageMonth() {
        return marriage_date.getMonth();
    }

    public void setMarriageMonth(final String marriage_month) {
        marriage_date.setMonth(marriage_month);
    }

    public String getMarriageYear() {
        return marriage_date.getYear();
    }

    public void setMarriageYear(final String marriage_year) {
        marriage_date.setYear(marriage_year);
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(final String denomination) {
        this.denomination = denomination;
    }

    public String getGroomForename() {
        return groom_forename;
    }

    public void setGroomId(String id) {
        this.groom_id = id;
    }

    public void setGroomFatherId(String id) {
        this.groom_fathers_id = id;
    }

    public void setGroomMotherId(String id) {
        this.groom_mothers_id = id;
    }

    public String getGroomId() {
         return groom_id;
    }

    public String getGroomFatherId() {
        return groom_fathers_id;
    }

    public String getGroomMotherId() {
        return groom_mothers_id;
    }

    public void setGroomForename(final String groom_forename) {
        this.groom_forename = groom_forename;
    }

    public String getGroomForenameChanged() {
        return groom_forename_changed;
    }

    public void setGroomForenameChanged(final String groom_forename_changed) {
        this.groom_forename_changed = groom_forename_changed;
    }

    public String getGroomSurname() {
        return groom_surname;
    }

    public void setGroomSurname(final String groom_surname) {
        this.groom_surname = groom_surname;
    }

    public String getGroomSurnameChanged() {
        return groom_surname_changed;
    }

    public void setGroomSurnameChanged(final String groom_surname_changed) {
        this.groom_surname_changed = groom_surname_changed;
    }

    public String getGroomDidNotSign() {
        return groom_did_not_sign;
    }

    public void setGroomDidNotSign(final String groom_did_not_sign) {
        this.groom_did_not_sign = groom_did_not_sign;
    }

    public String getGroomAddress() {
        return groom_address;
    }

    public void setGroomAddress(final String groom_address) {
        this.groom_address = groom_address;
    }

    public String getGroomAgeOrDateOfBirth() {
        return groom_age_or_date_of_birth;
    }

    public void setGroomAgeOrDateOfBirth(final String groom_age_or_date_of_birth) {
        this.groom_age_or_date_of_birth = groom_age_or_date_of_birth;
    }

    public String getGroomOccupation() {
        return groom_occupation;
    }

    public void setGroomOccupation(final String groom_occupation) {
        this.groom_occupation = groom_occupation;
    }

    public String getGroomMaritalStatus() {
        return groom_marital_status;
    }

    public void setGroomMaritalStatus(final String groom_marital_status) {
        this.groom_marital_status = groom_marital_status;
    }

    public String getGroomFathersForename() {
        return groom_fathers_forename;
    }

    public void setGroomFathersForename(final String groom_fathers_forename) {
        this.groom_fathers_forename = groom_fathers_forename;
    }

    public String getGroomFathersSurname() {
        return groom_fathers_surname;
    }

    public void setGroomFathersSurname(final String groom_fathers_surname) {
        this.groom_fathers_surname = groom_fathers_surname;
    }

    public String getGroomFatherDeceased() {
        return groom_father_deceased;
    }

    public void setGroomFatherDeceased(final String groom_father_deceased) {
        this.groom_father_deceased = groom_father_deceased;
    }

    public String getGroomMothersForename() {
        return groom_mothers_forename;
    }

    public void setGroomMothersForename(final String groom_mothers_forename) {
        this.groom_mothers_forename = groom_mothers_forename;
    }

    public String getGroomMothersMaidenSurname() {
        return groom_mothers_maiden_surname;
    }

    public void setGroomMothersMaidenSurname(final String groom_mothers_maiden_surname) {
        this.groom_mothers_maiden_surname = groom_mothers_maiden_surname;
    }

    public String getGroomMotherDeceased() {
        return groom_mother_deceased;
    }

    public void setGroomMotherDeceased(final String groom_mother_deceased) {
        this.groom_mother_deceased = groom_mother_deceased;
    }

    public String getGroomFathersOccupation() {
        return groom_fathers_occupation;
    }

    public void setGroomFathersOccupation(final String groom_fathers_occupation) {
        this.groom_fathers_occupation = groom_fathers_occupation;
    }

    public void setBrideId(String id) {
        this.bride_id = id;
    }

    public void setBrideFatherId(String id) {
        this.bride_fathers_id = id;
    }

    public void setBrideMotherId(String id) {
        this.bride_mothers_id = id;
    }

    public String getBrideId() {
        return bride_id;
    }

    public String getBrideFatherId() {
        return bride_fathers_id;
    }

    public String getBrideMotherId() {
        return bride_mothers_id;
    }

    public String getBrideForename() {
        return bride_forename;
    }

    public void setBrideForename(final String bride_forename) {
        this.bride_forename = bride_forename;
    }

    public String getBrideForenameChanged() {
        return bride_forename_changed;
    }

    public void setBrideForenameChanged(final String bride_forename_changed) {
        this.bride_forename_changed = bride_forename_changed;
    }

    public String getBrideSurname() {
        return bride_surname;
    }

    public void setBrideSurname(final String bride_surname) {
        this.bride_surname = bride_surname;
    }

    public String getBrideSurnameChanged() {
        return bride_surname_changed;
    }

    public void setBrideSurnameChanged(final String bride_surname_changed) {
        this.bride_surname_changed = bride_surname_changed;
    }

    public String getBrideDidNotSign() {
        return bride_did_not_sign;
    }

    public void setBrideDidNotSign(final String bride_did_not_sign) {
        this.bride_did_not_sign = bride_did_not_sign;
    }

    public String getBrideAddress() {
        return bride_address;
    }

    public void setBrideAddress(final String bride_address) {
        this.bride_address = bride_address;
    }

    public String getBrideAgeOrDateOfBirth() {
        return bride_age_or_date_of_birth;
    }

    public void setBrideAgeOrDateOfBirth(final String bride_age_or_date_of_birth) {
        this.bride_age_or_date_of_birth = bride_age_or_date_of_birth;
    }

    public String getBrideOccupation() {
        return bride_occupation;
    }

    public void setBrideOccupation(final String bride_occupation) {
        this.bride_occupation = bride_occupation;
    }

    public String getBrideMaritalStatus() {
        return bride_marital_status;
    }

    public void setBrideMaritalStatus(final String bride_marital_status) {
        this.bride_marital_status = bride_marital_status;
    }

    public String getBrideFathersForename() {
        return bride_fathers_forename;
    }

    public void setBrideFathersForename(final String bride_fathers_Forename) {
        this.bride_fathers_forename = bride_fathers_Forename;
    }

    public String getBrideFathersSurname() {
        return bride_fathers_surname;
    }

    public void setBrideFathersSurname(final String bride_fathers_surname) {
        this.bride_fathers_surname = bride_fathers_surname;
    }

    public String getBrideFatherDeceased() {
        return bride_father_deceased;
    }

    public void setBrideFatherDeceased(final String bride_father_deceased) {
        this.bride_father_deceased = bride_father_deceased;
    }

    public String getBrideMothersForename() {
        return bride_mothers_forename;
    }

    public void setBrideMothersForename(final String bride_mothers_forename) {
        this.bride_mothers_forename = bride_mothers_forename;
    }

    public String getBrideMothersMaidenSurname() {
        return bride_mothers_maiden_surname;
    }

    public void setBrideMothersMaidenSurname(final String bride_mothers_maiden_surname) {
        this.bride_mothers_maiden_surname = bride_mothers_maiden_surname;
    }

    public String getBrideMotherDeceased() {
        return bride_mother_deceased;
    }

    public void setBrideMotherDeceased(final String bride_mother_deceased) {
        this.bride_mother_deceased = bride_mother_deceased;
    }

    public String getBrideFatherOccupation() {
        return bride_father_occupation;
    }

    public void setBrideFatherOccupation(final String bride_father_occupation) {
        this.bride_father_occupation = bride_father_occupation;
    }

    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder();

        if(groom_id != null && bride_id != null) {
            int rnd = new Random().nextInt(101);
            RelationshipsTable.relationshipsMarriage.add(new String[]{"Marriage", String.valueOf(groom_id), String.valueOf(bride_id), String.valueOf(rnd), marriage_date.getDay() + "." + marriage_date.getMonth() + "." + marriage_date.getYear()});
        }
        append(builder,
                groom_id, groom_forename + " " + groom_surname,
                groom_fathers_id, groom_fathers_forename + " " + groom_fathers_surname,
                groom_mothers_id, groom_mothers_forename + " " + groom_mothers_maiden_surname,
                bride_id, bride_forename + " " + bride_surname,
                bride_fathers_id, bride_fathers_forename + " " + bride_fathers_surname,
                bride_mothers_id, bride_mothers_forename + " " + bride_mothers_maiden_surname,
                marriage_date.getDay() + "." + marriage_date.getMonth() + "." + marriage_date.getYear(),
                "", registration_district_suffix);


        return builder.toString();
    }

    @Override
    public String getHeaders() {
        final StringBuilder builder = new StringBuilder();

        append(builder,"groom_id", "groom_name", "groom_fathers_id", "groom_fathers_name", "groom_mothers_id",
                "groom_mothers_name", "bride_id", "bride_name", "bride_fathers_id", "bride_fathers_name",
                "bride_mothers_id", "bride_mothers_name", "marriage_date", "", "registration_district_suffix");

        return builder.toString();
    }
}
