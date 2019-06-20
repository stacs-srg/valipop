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
import uk.ac.standrews.cs.valipop.utils.addressLookup.Geography;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

        if(parents != null) {
            IPerson f = parents.getFemalePartner();
            IPerson m = parents.getMalePartner();

            if ((f.hasEmigrated() && f.getEmigrationDate().isBefore(birthDate)) || (m.hasEmigrated() && m.getEmigrationDate().isBefore(birthDate))) {
                System.out.print("");
            }
        }

        this.sex = sex;
        this.birthDate = birthDate;
        this.parents = parents;
        this.illegitimate = illegitimate;

        firstName = getForename(statistics, immigrant);
        surname = getSurname(statistics, immigrant);

        representation = firstName + " " + surname + " (" + id + ") " + birthDate;

        setOccupation(birthDate, statistics.getOccupation(Year.of(birthDate.getYear()), sex).getDistributionForAge(0).getSample());

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
        Address a =  getAddress(birthDate);
        return a == null ? "" : a.toString();
    }

    @Override
    public String getDeathPlace() {
        Address a =  getAddress(deathDate);
        return a == null ? "" : a.toString();
    }

    private TreeMap<LocalDate, String> occupationHistory = new TreeMap<>();

    @Override
    public String getOccupation(LocalDate onDate) {
        return occupationHistory.floorEntry(onDate).getValue();
    }

    @Override
    public void setOccupation(LocalDate onDate, String occupation) {
        occupationHistory.put(onDate, occupation);
    }

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
        if(onDate == null)
            return null;

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
    public void rollbackLastMove(Geography geography) {

        Address cancelledAddress = addressHistory.lastEntry().getValue();
        Set<IPerson> family = getChildrenOfAtAddress(this, cancelledAddress);
        family.add(this);

        // remove from curent abode and remove from address history
        for(IPerson person : family)
            person.cancelLastMove(geography);

        if(addressHistory.size() != 0) {
            // check previous abode
            Address previousAddress = addressHistory.lastEntry().getValue();

            if (!previousAddress.isCountry() && previousAddress.isInhabited()) {
                // if by family
                if (containsFamily(previousAddress, this)) {
                    // move back in
                    previousAddress.addInhabitants(family);
                } else {
                    // displace current residents at distance zero
                    previousAddress.displaceInhabitants();
                    previousAddress.addInhabitants(family);
                }
            } else if(previousAddress.isCountry()) {
                // if cancelling last move results in the 'new last address' being forign country then we need to give the
                // person (who is a  migrant) an address to live in from there emmigration date
                setAddress(immigrationDate, geography.getRandomEmptyAddress());
            } else {
                // move back in
                previousAddress.addInhabitants(family);
            }
        }

    }

    private Set<IPerson> getChildrenOfAtAddress(IPerson parent, Address address) {

        HashSet<IPerson> childrenAtAddress = new HashSet<>();

        for(IPerson person : address.getInhabitants()) {
            if(PopulationNavigation.childOf(parent, person)) {
                childrenAtAddress.add(person);
            }
        }

        return childrenAtAddress;
    }

    @Override
    public LocalDate cancelLastMove(Geography geography) {

        Map.Entry<LocalDate, Address> lastMove = addressHistory.lastEntry();
        LocalDate moveDate = lastMove.getKey();

        lastMove.getValue().removeInhabitant(this);
        addressHistory.remove(addressHistory.lastKey());

//        if(addressHistory.lastEntry() != null && addressHistory.lastEntry().getValue().isCountry()) {
//             if cancelling last move results in the 'new last address' being forign country then we need to give the
//             person (who is a  migrant) an address to live in from there emmigration date
//
//            setAddress(immigrationDate, geography.getNearestEmptyAddress(lastMove.getValue().getArea().getCentriod()));
//
//        }

        return moveDate;
    }

    @Override
    public boolean hasEmigrated() {
        return emigrationDate != null;
    }

    @Override
    public IPartnership getLastPartnership() {
        if(partnerships.size() != 0) {
            return partnerships.get(partnerships.size() - 1);
        }
        return null;
    }

    @Override
    public String getLastOccupation() {
        return getOccupation(LocalDate.MAX);
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
            return statistics.getMigrantForenameDistribution(Year.of(birthDate.getYear()), getSex()).getSample();
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
                return statistics.getMigrantSurnameDistribution(Year.of(birthDate.getYear())).getSample();
            } else {
                return statistics.getSurnameDistribution(Year.of(birthDate.getYear())).getSample();
            }
        }
    }
}
