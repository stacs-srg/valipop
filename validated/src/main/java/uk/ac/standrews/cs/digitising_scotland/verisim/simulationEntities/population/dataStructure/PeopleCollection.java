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
package uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.MisalignedTimeDivisionError;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.exceptions.UnsupportedDateConversion;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.exceptions.PersonNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.utils.AggregatePersonCollectionFactory;

import java.util.*;

/**
 * The class PeopleCollection is a concrete instance of the PersonCollection class. It provides the layout to structure
 * and index a population of males and females and provide access to them. The class also implements the IPopulation
 * interface (adapted to us object references rather than integer id references) allowing it to be used with our other
 * population suite tools.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PeopleCollection extends PersonCollection implements IPopulation {

    private String description = "";

    private MaleCollection males;
    private FemaleCollection females;

//    private final Map<Integer, IPersonExtended> peopleIndex = new HashMap<>();
    private final Map<Integer, IPartnershipExtended> partnershipIndex = new HashMap<>();

    private ArrayList<IPartnershipExtended> partTemp = new ArrayList<>();

    public PeopleCollection clone() {
        PeopleCollection clone = new PeopleCollection(getStartDate(), getEndDate(), getDivisionSize());

        for(IPersonExtended m : males.getAll()) {
            clone.addPerson(m);
        }

        for(IPersonExtended f : females.getAll()) {
            clone.addPerson(f);
        }

        for(Integer k : partnershipIndex.keySet()) {
            clone.addPartnershipToIndex(partnershipIndex.get(k));
        }

        clone.setDescription(description);

        return clone;
    }

    /**
     * Instantiates a new PersonCollection. The dates specify the earliest and latest expected birth dates of
     * individuals in the PersonCollection. There is no hard enforcement of this as the bounds are intended to serve
     * mainly as a guide for when other things make use of the PersonCollection - e.g. producing plots, applying
     * validation statistics.
     *
     * @param start the start
     * @param end   the end
     * @throws UnsupportedDateConversion the unsupported date conversion
     */
    public PeopleCollection(AdvancableDate start, uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date end, CompoundTimeUnit divisionSize) {
        super(start, end, divisionSize);

        males = new MaleCollection(start, end, divisionSize);
        females = new FemaleCollection(start, end, divisionSize);
    }

    /**
     * @return the part of the population data structure containing the males
     */
    public MaleCollection getMales() {
        return males;
    }

    /**
     * @return the part of the population data structure containing the females
     */
    public FemaleCollection getFemales() {
        return females;
    }

    /**
     * Adds partnership to  the partnership index.
     *
     * @param partnership the partnership
     */
    public void addPartnershipToIndex(IPartnershipExtended partnership) {
        partnershipIndex.put(partnership.getId(), partnership);
        partTemp.add(partnership);
    }

    public void removePartnershipFromIndex(IPartnershipExtended partnership) {
        partnershipIndex.remove(partnership.getId(), partnership);
        partTemp.remove(partnership);
    }

    /*
    -------------------- PersonCollection abstract methods --------------------
     */

    @Override
    public Collection<IPersonExtended> getAll() {
        return AggregatePersonCollectionFactory.makeCollectionOfPersons(females, males);
    }

    @Override
    public Collection<IPersonExtended> getAllPersonsInTimePeriod(AdvancableDate firstDate, CompoundTimeUnit timePeriod) {
        Collection<IPersonExtended> people =  males.getAllPersonsInTimePeriod(firstDate, timePeriod);
        people.addAll(females.getAllPersonsInTimePeriod(firstDate, timePeriod));
        return people;
    }

//    @Override
//    public Collection<IPersonExtended> getByYear(Date yearOfBirth) {
//        Collection<IPersonExtended> people = males.getByYear(yearOfBirth);
//        people.addAll(females.getByYear(yearOfBirth));
//        return people;
//    }
//
//    @Override
//    public Collection<IPersonExtended> getByYearAndSex(char sex, Date year) {
//        if (Character.toLowerCase(sex) == 'm') {
//            return getMales().getByYear(year);
//        } else {
//            return getFemales().getByYear(year);
//        }
//    }

    @Override
    public void addPerson(IPersonExtended person) {
//        peopleIndex.put(person.getId(), person);
        if (person.getSex() == 'm') {
            males.addPerson(person);
        } else {
            females.addPerson(person);
        }
    }

    @Override
    public void removePerson(IPersonExtended person) throws PersonNotFoundException {
//        peopleIndex.remove(person.getId());
        if (person.getSex() == 'm') {
            males.removePerson(person);
        } else {
            females.removePerson(person);
        }
    }

    @Override
    public int getNumberOfPersons() {
        return getAll().size();
    }

    @Override
    public int getNumberOfPersons(AdvancableDate firstDate, CompoundTimeUnit timePeriod) {
        return males.getNumberOfPersons(firstDate, timePeriod) + females.getNumberOfPersons(firstDate, timePeriod);
    }

    @Override
    public TreeSet<AdvancableDate> getDivisionDates() {
        return females.getDivisionDates();
    }

    /*
    -------------------- IPopulation interface methods --------------------
     */

    @Override
    public Iterable<IPersonExtended> getPeople() {
        return getAll();
    }

    @Override
    public Iterable<IPartnershipExtended> getPartnerships() {

        // TODO Is this temp object needed?
        return partTemp;
    }

//    @Override
//    public IPersonExtended findPerson(int id) {
//        return peopleIndex.get(id);
//    }

    @Override
    public IPartnershipExtended findPartnership(int id) {
        return partnershipIndex.get(id);
    }

    @Override
    public int getNumberOfPeople() {
        return getAll().size();
    }


    @Override
    public int getNumberOfPartnerships() {
        return partnershipIndex.size();
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setConsistentAcrossIterations(boolean consistent_across_iterations) {

    }

    @Override
    public Collection<IPersonExtended> forceGetAllPersonsByTimePeriod(AdvancableDate firstDate, CompoundTimeUnit timePeriod) {
        Collection<IPersonExtended> people = new ArrayList<>();
        people.addAll(forceGetAllPersonsByTimePeriodAndSex(firstDate, timePeriod, 'm'));
        people.addAll(forceGetAllPersonsByTimePeriodAndSex(firstDate, timePeriod, 'f'));

        return people;
    }

    @Override
    public Collection<IPersonExtended> forceGetAllPersonsByTimePeriodAndSex(AdvancableDate firstDate, CompoundTimeUnit timePeriod, char sex) {
        Collection<IPersonExtended> people = new ArrayList<>();

        if(Character.toLowerCase(sex) == 'm') {
            try {
                people = getMales().getAllPersonsInTimePeriod(firstDate, timePeriod);
            } catch (MisalignedTimeDivisionError e) {
                people.addAll(getPeopleBetweenDates(getMales(), firstDate, firstDate.advanceTime(timePeriod)));
            }
        } else if(Character.toLowerCase(sex) == 'f') {
            try {
                people = getFemales().getAllPersonsInTimePeriod(firstDate, timePeriod);
            } catch (MisalignedTimeDivisionError e) {
                people.addAll(getPeopleBetweenDates(getMales(), firstDate, firstDate.advanceTime(timePeriod)));
            }
        }

        return people;
    }

    private Collection<IPersonExtended> getPeopleBetweenDates(PersonCollection collection,
                                                              AdvancableDate firstDateOfInterest,
                                                              AdvancableDate lastDateOfInterest) {

        Collection<IPersonExtended> people = new ArrayList<>();

        AdvancableDate firstDivOfInterest = resolveDateToCorrectDivisionDate(firstDateOfInterest);
        AdvancableDate lastDivOfInterest = resolveDateToCorrectDivisionDate(lastDateOfInterest);

        AdvancableDate consideredDate = firstDivOfInterest;

        while(DateUtils.dateBeforeOrEqual(consideredDate, lastDivOfInterest)) {

            Collection<IPersonExtended> temp = collection.getAllPersonsInTimePeriod(consideredDate, getDivisionSize());

            if(DateUtils.datesEqual(firstDivOfInterest, consideredDate)) {

                for(IPersonExtended p : temp) {

                    if(!DateUtils.dateBefore(p.getBirthDate_ex(), firstDateOfInterest)) {
                        people.add(p);
                    }

                }

            } else if(DateUtils.datesEqual(lastDivOfInterest, consideredDate)){

                for(IPersonExtended p : temp) {

                    if(DateUtils.dateBefore(p.getBirthDate_ex(), lastDateOfInterest)) {
                        people.add(p);
                    }

                }

            } else {
                people.addAll(temp);
            }

            consideredDate = consideredDate.advanceTime(getDivisionSize());
        }

        return people;
    }
}
