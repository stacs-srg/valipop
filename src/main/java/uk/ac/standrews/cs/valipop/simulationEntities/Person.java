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
package uk.ac.standrews.cs.valipop.simulationEntities;

import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.SexOption;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.addressLookup.Address;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Person implements IPerson {

    private static int nextId = 0;

    private int id;
    private SexOption sex;
    private LocalDate birthDate;
    private LocalDate deathDate = null;
    private List<IPartnership> partnerships = new ArrayList<>();
    private IPartnership parents;

    private final String firstName;
    private final String surname;
    private final String representation;
    private final boolean illegitimate;

    private String deathCause = "";

    TreeMap<LocalDate, Address> addressHistory = new TreeMap<>();

    public Person(SexOption sex, LocalDate birthDate, IPartnership parents, PopulationStatistics statistics, boolean illegitimate) {

        id = getNewId();

        this.sex = sex;
        this.birthDate = birthDate;
        this.parents = parents;
        this.illegitimate = illegitimate;

        firstName = getForename(statistics);
        surname = getSurname(statistics);

        representation = firstName + " " + surname + " (" + id + ") " + birthDate;
    }

    public String toString() {
        return representation;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public SexOption getSex() {
        return sex;
    }

    @Override
    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public LocalDate getDeathDate() {
        return deathDate;
    }

    @Override
    public void setDeathDate(final LocalDate deathDate) {

        this.deathDate = deathDate;
    }

    @Override
    public List<IPartnership> getPartnerships() {
        return partnerships;
    }

    @Override
    public IPartnership getParents() {
        return parents;
    }

    @Override
    public boolean isIllegitimate() {
        return illegitimate;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    // TODO Implement geography model
    @Override
    public String getBirthPlace() {
        return null;
    }

    @Override
    public String getDeathPlace() {
        return null;
    }

    // TODO Implement occupation assignment
    @Override
    public String getOccupation() {
        return null;
    }

    // TODO Implement death causes - does occupation, date, gender, location, etc. influence this?
    @Override
    public String getDeathCause() {
        return deathCause;
    }

    @Override
    public void setDeathCause(final String deathCause) {

        this.deathCause = deathCause;
    }

    @Override
    public int compareTo(IPerson other) {
        return Integer.compare(id, other.getId());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Person person = (Person) other;
        return id == person.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public void recordPartnership(IPartnership partnership) {
        partnerships.add(partnership);
    }

    @Override
    public Address getAddress(LocalDate onDate) {
        Map.Entry<LocalDate, Address> entry = addressHistory.floorEntry(onDate);
        if(entry != null)
            return entry.getValue();

        return null;
    }

    @Override
    public void setAddress(LocalDate onDate, Address address) {
        if(addressHistory.size() != 0) {
            getAddress(onDate).removeInhabitant(this);

            // if children get shuttled around before birth then remove old addresses
            if(addressHistory.get(onDate) != null) {
                addressHistory.remove(onDate);
            }
        }

        address.addInhabitant(this);
        addressHistory.put(onDate, address);
    }

    private static int getNewId() {
        return nextId++;
    }

    public static void resetIds() {
        nextId = 0;
    }

    private String getForename(PopulationStatistics statistics) {

        return statistics.getForenameDistribution(Year.of(birthDate.getYear()), getSex()).getSample();
    }

    private String getSurname(PopulationStatistics statistics) {

        if (parents != null) {
            return parents.getMalePartner().getSurname();
        }
        else {
            return statistics.getSurnameDistribution(Year.of(birthDate.getYear())).getSample();
        }
    }
}
