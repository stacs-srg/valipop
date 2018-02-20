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
package uk.ac.standrews.cs.valipop.utils.sourceEventRecords;

import uk.ac.standrews.cs.basic_model.model.IPartnership;
import uk.ac.standrews.cs.basic_model.model.IPerson;
import uk.ac.standrews.cs.basic_model.model.IPopulation;

/**
 * Created by graham on 13/05/2014.
 */
public abstract class IndividualSourceRecord extends SourceRecord {

    protected String surname;
    protected String surname_changed;
    protected String forename;
    protected String forename_changed;

    protected String sex;

    protected String mothers_id;
    protected String mothers_forename;
    protected String mothers_surname;
    protected String mothers_maiden_surname;
    protected String mothers_maiden_surname_changed;

    protected String fathers_id;
    protected String fathers_forename;
    protected String fathers_surname;
    protected String fathers_occupation;

    protected void setParentAttributes(IPerson person, IPopulation population, IPartnership parents_partnership) {

        // Attributes associated with individual's parents.
        IPerson mother = population.findPerson(parents_partnership.getFemalePartnerId());
        IPerson father = population.findPerson(parents_partnership.getMalePartnerId());

        setMothersId(String.valueOf(mother.getId()));
        setMothersForename(mother.getFirstName());
        setMothersSurname(getRecordedParentsSurname(mother.getSurname(), person.getSurname()));
        setMothersMaidenSurname(getMaidenSurname(population, mother));

        setFathersId(String.valueOf(father.getId()));
        setFathersForename(father.getFirstName());
        setFathersSurname(getRecordedParentsSurname(father.getSurname(), person.getSurname()));
        setFathersOccupation(father.getOccupation());
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(final String surname) {
        this.surname = surname;
    }

    public String getSurnameChanged() {
        return surname_changed;
    }

    public void setSurnameChanged(final String surname_changed) {
        this.surname_changed = surname_changed;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(final String forename) {
        this.forename = forename;
    }

    public String getForenameChanged() {
        return forename_changed;
    }

    public void setForenameChanged(final String forename_changed) {
        this.forename_changed = forename_changed;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(final String sex) {
        this.sex = sex;
    }

    public String getMothersForename() {
        return mothers_forename;
    }

    public void setMothersForename(final String mothers_forename) {
        this.mothers_forename = mothers_forename;
    }

    public String getMothersSurname() {
        return mothers_surname;
    }

    public void setMothersSurname(final String mothers_surname) {
        this.mothers_surname = mothers_surname;
    }

    public String getMothersMaidenSurname() {
        return mothers_maiden_surname;
    }

    public void setMothersMaidenSurname(final String mothers_maiden_surname) {
        this.mothers_maiden_surname = mothers_maiden_surname;
    }

    public String getMothersMaidenSurnameChanged() {
        return mothers_maiden_surname_changed;
    }

    public void setMothersMaidenSurnameChanged(final String mothers_maiden_surname_changed) {
        this.mothers_maiden_surname_changed = mothers_maiden_surname_changed;
    }

    public String getFathersForename() {
        return fathers_forename;
    }

    public void setFathersForename(final String fathers_forename) {
        this.fathers_forename = fathers_forename;
    }

    public String getFathersSurname() {
        return fathers_surname;
    }

    public void setFathersSurname(final String fathers_surname) {
        this.fathers_surname = fathers_surname;
    }

    public String getFathersOccupation() {
        return fathers_occupation;
    }

    public void setFathersOccupation(final String fathers_occupation) {
        this.fathers_occupation = fathers_occupation;
    }

    public void setMothersId(final String id) {
        this.mothers_id = id;
    }

    public String getMothersId() {
        return mothers_id;
    }

    public void setFathersId(final String id) {
        this.fathers_id = id;
    }

    public String getFathersId() {
        return fathers_id;
    }
}
