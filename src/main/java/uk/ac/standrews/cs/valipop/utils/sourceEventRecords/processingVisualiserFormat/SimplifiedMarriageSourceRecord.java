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

import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.population.IPopulation;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.oldDSformat.SourceRecord;

import java.time.LocalDate;
import java.util.Random;

/**
 * A representation of a Marriage Record in the form used by the Digitising Scotland Project.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SimplifiedMarriageSourceRecord extends SourceRecord {

    private LocalDate marriage_date;

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

    SimplifiedMarriageSourceRecord(final IPartnership partnership, final IPopulation population) {

        setUid(String.valueOf(partnership.getId()));

        IPerson bride = partnership.getFemalePartner();
        IPerson groom = partnership.getMalePartner();

        marriage_date = partnership.getMarriageDate();

        setGroomId(String.valueOf(groom.getId()));
        setGroomForename(groom.getFirstName());
        setGroomSurname(groom.getSurname());
        setGroomOccupation(groom.getOccupation());
        setGroomAgeOrDateOfBirth(String.valueOf(fullYearsBetween(groom.getBirthDate(), marriage_date)));

        setBrideId(String.valueOf(bride.getId()));
        setBrideForename(bride.getFirstName());
        setBrideSurname(bride.getSurname());
        setBrideOccupation(bride.getOccupation());
        setBrideAgeOrDateOfBirth(String.valueOf(fullYearsBetween(bride.getBirthDate(), marriage_date)));

        IPartnership groom_parents_partnership = groom.getParents();
        if (groom_parents_partnership != null) {

            IPerson groom_mother = groom_parents_partnership.getFemalePartner();
            IPerson groom_father = groom_parents_partnership.getMalePartner();

            setGroomFatherId(String.valueOf(groom_father.getId()));
            setGroomFathersForename(groom_father.getFirstName());
            setGroomFathersSurname(getRecordedParentsSurname(groom_father.getSurname(), groom.getSurname()));
            setGroomFathersOccupation(groom_father.getOccupation());

            setGroomMotherId(String.valueOf(groom_mother.getId()));
            setGroomMothersForename(groom_mother.getFirstName());
            setGroomMothersMaidenSurname(getMaidenSurname(population, groom_mother));
        }

        IPartnership bride_parents_partnership = bride.getParents();
        if (bride_parents_partnership != null) {

            IPerson bride_mother = bride_parents_partnership.getFemalePartner();
            IPerson bride_father = bride_parents_partnership.getMalePartner();

            setBrideFatherId(String.valueOf(bride_father.getId()));
            setBrideFathersForename(bride_father.getFirstName());
            setBrideFathersSurname(getRecordedParentsSurname(bride_father.getSurname(), bride.getSurname()));
            setBrideFatherOccupation(bride_father.getOccupation());

            setBrideMotherId(String.valueOf(bride_mother.getId()));
            setBrideMothersForename(bride_mother.getFirstName());
            setBrideMothersMaidenSurname(getMaidenSurname(population, bride_mother));
        }
    }

    private void setGroomId(String id) {
        this.groom_id = id;
    }

    private void setGroomFatherId(String id) {
        this.groom_fathers_id = id;
    }

    private void setGroomMotherId(String id) {
        this.groom_mothers_id = id;
    }

    private void setGroomForename(final String groom_forename) {
        this.groom_forename = groom_forename;
    }

    private void setGroomSurname(final String groom_surname) {
        this.groom_surname = groom_surname;
    }

    private void setGroomAgeOrDateOfBirth(final String groom_age_or_date_of_birth) {
        this.groom_age_or_date_of_birth = groom_age_or_date_of_birth;
    }

    private void setGroomOccupation(final String groom_occupation) {
        this.groom_occupation = groom_occupation;
    }

    private void setGroomFathersForename(final String groom_fathers_forename) {
        this.groom_fathers_forename = groom_fathers_forename;
    }

    private void setGroomFathersSurname(final String groom_fathers_surname) {
        this.groom_fathers_surname = groom_fathers_surname;
    }

    private void setGroomMothersForename(final String groom_mothers_forename) {
        this.groom_mothers_forename = groom_mothers_forename;
    }

    private void setGroomMothersMaidenSurname(final String groom_mothers_maiden_surname) {
        this.groom_mothers_maiden_surname = groom_mothers_maiden_surname;
    }

    private void setGroomFathersOccupation(final String groom_fathers_occupation) {
        this.groom_fathers_occupation = groom_fathers_occupation;
    }

    private void setBrideId(String id) {
        this.bride_id = id;
    }

    private void setBrideFatherId(String id) {
        this.bride_fathers_id = id;
    }

    private void setBrideMotherId(String id) {
        this.bride_mothers_id = id;
    }

    private void setBrideForename(final String bride_forename) {
        this.bride_forename = bride_forename;
    }

    private void setBrideSurname(final String bride_surname) {
        this.bride_surname = bride_surname;
    }

    private void setBrideAgeOrDateOfBirth(final String bride_age_or_date_of_birth) {
        this.bride_age_or_date_of_birth = bride_age_or_date_of_birth;
    }

    private void setBrideOccupation(final String bride_occupation) {
        this.bride_occupation = bride_occupation;
    }

    private void setBrideFathersForename(final String bride_fathers_Forename) {
        this.bride_fathers_forename = bride_fathers_Forename;
    }

    private void setBrideFathersSurname(final String bride_fathers_surname) {
        this.bride_fathers_surname = bride_fathers_surname;
    }

    private void setBrideMothersForename(final String bride_mothers_forename) {
        this.bride_mothers_forename = bride_mothers_forename;
    }

    private void setBrideMothersMaidenSurname(final String bride_mothers_maiden_surname) {
        this.bride_mothers_maiden_surname = bride_mothers_maiden_surname;
    }

    private void setBrideFatherOccupation(final String bride_father_occupation) {
        this.bride_father_occupation = bride_father_occupation;
    }

    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder();

        if (groom_id != null && bride_id != null) {
            int rnd = new Random().nextInt(101);
            RelationshipsTable.relationshipsMarriage.add(new String[]{"Marriage", String.valueOf(groom_id), String.valueOf(bride_id), String.valueOf(rnd), marriage_date.getDayOfMonth() + "." + marriage_date.getMonth() + "." + marriage_date.getYear()});
        }
        append(builder,
                groom_id, groom_forename + " " + groom_surname,
                groom_fathers_id, groom_fathers_forename + " " + groom_fathers_surname,
                groom_mothers_id, groom_mothers_forename + " " + groom_mothers_maiden_surname,
                bride_id, bride_forename + " " + bride_surname,
                bride_fathers_id, bride_fathers_forename + " " + bride_fathers_surname,
                bride_mothers_id, bride_mothers_forename + " " + bride_mothers_maiden_surname,
                marriage_date.getDayOfMonth() + "." + marriage_date.getMonth() + "." + marriage_date.getYear(),
                "", registration_district_suffix);


        return builder.toString();
    }

    @Override
    public String getHeaders() {
        final StringBuilder builder = new StringBuilder();

        append(builder, "groom_id", "groom_name", "groom_fathers_id", "groom_fathers_name", "groom_mothers_id",
                "groom_mothers_name", "bride_id", "bride_name", "bride_fathers_id", "bride_fathers_name",
                "bride_mothers_id", "bride_mothers_name", "marriage_date", "", "registration_district_suffix");

        return builder.toString();
    }
}
