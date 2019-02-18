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

import uk.ac.standrews.cs.valipop.simulationEntities.dataStructure.Population;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.SexOption;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.addressLookup.Address;

import java.time.LocalDate;
import java.time.Year;
import java.util.*;

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

    private TreeMap<LocalDate, Address> addressHistory = new TreeMap<>();
    private LocalDate emigrationDate = null;
    private LocalDate immigrationDate = null;

    public Person(SexOption sex, LocalDate birthDate, IPartnership parents, PopulationStatistics statistics, boolean illegitimate) {
        this(sex, birthDate, parents, statistics, illegitimate, false);
    }

    public Person(SexOption sex, LocalDate birthDate, IPartnership parents, PopulationStatistics statistics, boolean illegitimate, boolean immigrant) {

        id = getNewId();

        this.sex = sex;
        this.birthDate = birthDate;
        this.parents = parents;
        this.illegitimate = illegitimate;

        firstName = getForename(statistics, immigrant);
        surname = getSurname(statistics, immigrant);

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
    public void setParents(IPartnership parents) {
        if(this.parents == null) {
            this.parents = parents;
        }
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

    @Override
    public String getBirthPlace() {
        return getAddress(birthDate).toString();
    }

    @Override
    public String getDeathPlace() {
        return getAddress(deathDate).toString();
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
        if(addressHistory.size() != 0) { // Pass this bit if no previous address

            if(getAddress(onDate) != null)
                getAddress(onDate).removeInhabitant(this);

            // if children get shuttled around before birth then remove old addresses
            if(addressHistory.get(onDate) != null) {
                addressHistory.get(onDate).removeInhabitant(this);
                addressHistory.remove(onDate);
            } else if(addressHistory.ceilingEntry(onDate) != null) { // if theres a future move - from a forced illegitimacy move - we scratch that move
                addressHistory.ceilingEntry(onDate).getValue().removeInhabitant(this);
                addressHistory.remove(addressHistory.ceilingKey(onDate));
            }


        }

        address.addInhabitant(this);
        addressHistory.put(onDate, address);
    }

    @Override
    public LocalDate getEmigrationDate() {
        return emigrationDate;
    }

    @Override
    public void setEmigrationDate(LocalDate leavingDate) {
        if(leavingDate == null)
            System.out.print("");

        emigrationDate = leavingDate;
    }

    @Override
    public LocalDate getImmigrationDate() {
        return immigrationDate;
    }

    @Override
    public void setImmigrationDate(LocalDate arrivalDate) {
        immigrationDate = arrivalDate;
    }

    @Override
    public LocalDate getLastMoveDate() {
        try {
            return addressHistory.lastKey();
        } catch(NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public Collection<Address> getAllAddresses() {
        return addressHistory.values();
    }

    @Override
    public void rollbackLastMove() {

        // remove from curent abode and remove from address history
        cancelLastMove();

        if(addressHistory.size() != 0) {
            // check previous abode
            Address previousAddress = addressHistory.lastEntry().getValue();

            if (previousAddress.isInhabited()) {
                // if by family
                if (containsFamily(previousAddress, this)) {
                    // move back in
                    previousAddress.addInhabitant(this);
                } else {
                    // displace current residents at distance zero
                    previousAddress.displaceInhabitants();
                    previousAddress.addInhabitant(this);
                }
            } else {
                // move back in
                previousAddress.addInhabitant(this);
            }
        }

    }

    @Override
    public LocalDate cancelLastMove() {

        Map.Entry<LocalDate, Address> lastMove = addressHistory.lastEntry();

        lastMove.getValue().removeInhabitant(this);
        addressHistory.remove(addressHistory.lastKey());

        return lastMove.getKey();
    }

    private boolean containsFamily(Address address, Person person) {

        Collection<IPerson> family = PopulationNavigation.imidiateFamilyOf(person);

        for(IPerson inhabitant : address.getInhabitants()) {
            if(family.contains(inhabitant)) {
                return true;
            }
        }

        return false;
    }

    private static int getNewId() {
        return nextId++;
    }

    public static void resetIds() {
        nextId = 0;
    }

    private String getForename(PopulationStatistics statistics, boolean immigrant) {

        if(immigrant) {
            // TODO add in distributions
            return "Born";
        } else {
            return statistics.getForenameDistribution(Year.of(birthDate.getYear()), getSex()).getSample();
        }
    }

    private String getSurname(PopulationStatistics statistics, boolean immigrant) {

        if (parents != null) {
            return parents.getMalePartner().getSurname();
        }
        else {
            if(immigrant) {
                // TODO add in distributions
                return "Abroad";
            } else {
                return statistics.getSurnameDistribution(Year.of(birthDate.getYear())).getSample();
            }
        }
    }
}
