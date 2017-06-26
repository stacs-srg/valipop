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

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateBounds;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.MisalignedTimeDivisionError;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.exceptions.PersonNotFoundException;

import java.util.*;

/**
 * A PersonCollection contains a set of collections of people where the collections are organised by the year of birth
 * of the person.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public abstract class PersonCollection implements DateBounds {

    private AdvancableDate startDate;
    private uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date endDate;
    private CompoundTimeUnit divisionSize;

    /**
     * Instantiates a new PersonCollection. The dates specify the earliest and latest expected birth dates of
     * individuals in the PersonCollection. There is no hard enforcement of this as the bounds are intended to serve
     * mainly as a guide for when other things make use of the PersonCollection - e.g. producing plots, applying
     * validation statistics.
     *
     * @param startDate the start date
     * @param endDate   the end date
     */
    public PersonCollection(AdvancableDate startDate, uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date endDate, CompoundTimeUnit divisionSize) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.divisionSize = divisionSize;
    }

    /**
     * Gets all the people that exist in the underlying sub-structure of this PersonCollection. Likely to be expensive,
     * if needing a count of people in collection then check to see if the instance has an index and take a count using
     * the size of the index.
     *
     * @return All people in the PersonCollection
     */
    public abstract Collection<IPersonExtended> getAll();

    /**
     * Gets all the people in the PersonCollection who were born in the given year.
     *
     * @param firstDate the year of birth of the desired cohort
     * @return the desired cohort
     */
    // Was getByYear()
    public abstract Collection<IPersonExtended> getAllPersonsInTimePeriod(AdvancableDate firstDate, CompoundTimeUnit timePeriod);

    /**
     * Adds the given person to the PersonCollection.
     *
     * @param person the person to be added
     */
    abstract void addPerson(IPersonExtended person);

    /**
     * Removes the specified person from this PersonCollection.
     *
     * @param person the person to be removed
     * @throws PersonNotFoundException If the specified person is not found then an exception is thrown
     */
    abstract void removePerson(IPersonExtended person) throws PersonNotFoundException;

    /**
     * Counts and returns the number of people in the PersonCollection. This may be very expensive as it involves
     * combining the counts of many under-lying Collection objects. If the instance contains an index it will likely be
     * more efficient to take the size of the index as the count.
     *
     * @return the number of persons in the PersonCollection
     */
    abstract int getNumberOfPersons();

    /**
     * Counts and returns the number of people born in the given year in the PersonCollection. This may be very
     * expensive as it may involve combining the counts of many under-lying Collection objects. If the instance contains
     * an index it will likely be more efficient to take the size of the index as the count.
     *
     * @return the number of persons in the PersonCollection
     */
    public abstract int getNumberOfPersons(AdvancableDate firstDate, CompoundTimeUnit timePeriod);

    /**
     * Removes n people with the specified year of birth from the PersonCollection. If there are not enough people then
     * an exception is thrown.
     *
     * @param numberToRemove the number of people to remove
     * @param firstDate    the year of birth of those to remove
     * @param bestAttempt    returns the people that do exist even if there is not enough to meet numberToRemove
     * @return the random Collection of people who have been removed
     * @throws InsufficientNumberOfPeopleException If there are less people alive for the given year of birth than
     */
    @SuppressWarnings("Duplicates")
    public Collection<IPersonExtended> removeNPersons(int numberToRemove, AdvancableDate firstDate, CompoundTimeUnit timePeriod, boolean bestAttempt) throws InsufficientNumberOfPeopleException {

        int divisionsInPeriod = DateUtils.calcSubTimeUnitsInTimeUnit(divisionSize, timePeriod);

        if(divisionsInPeriod <= 0) {
            throw new MisalignedTimeDivisionError();
        }

        ArrayList<IPersonExtended> people = new ArrayList<>();
        MonthDate divisionDate = firstDate.getMonthDate();

        LinkedList<MonthDate> reusableDivisions = new LinkedList<>();

        // find all the division dates
        for(int i = 0; i < divisionsInPeriod; i++) {
            reusableDivisions.add(divisionDate);
            divisionDate = divisionDate.advanceTime(getDivisionSize());
        }

        // this by design rounds down
        int numberToRemoveFromDivision = (numberToRemove - people.size()) / reusableDivisions.size();

        // check variables to decide when to recalculate number to remove from each division at the current iteration
        int numberOfReusableDivisions = reusableDivisions.size();
        int divisionsUsed = 0;

        while(people.size() < numberToRemove) {

            if(reusableDivisions.isEmpty()) {
                if(bestAttempt) {
                    return people;
                } else {
                    throw new InsufficientNumberOfPeopleException("Not enought people in timeperiod to meet request of" +
                            numberToRemove + " females from " + firstDate.toString() + " and following time period " +
                            timePeriod.toString());
                }
            }


            // If every division has been sampled at the current level then and we are still short of people then we
            // need to recalculate the number to take from each division
            if(divisionsUsed == numberOfReusableDivisions) {
                // Reset check variables
                numberOfReusableDivisions = reusableDivisions.size();
                divisionsUsed = 0;

                double tempNumberToRemoveFromDivisions = (numberToRemove - people.size()) / (double) reusableDivisions.size();
                if(tempNumberToRemoveFromDivisions < 1) {
                    // in the case where we are down to the last couple of people (defined by the current number of
                    // reusable divisions minus 1) we proceed to remove 1 person from each interval in turn until we
                    // reach the required number of people to be removed.
                    numberToRemoveFromDivision = (int) Math.ceil(tempNumberToRemoveFromDivisions);
                } else {
                    numberToRemoveFromDivision = (int) tempNumberToRemoveFromDivisions;
                }
            }

            // dequeue division
            MonthDate consideredDivision = reusableDivisions.removeFirst();
            divisionsUsed ++;

            Collection<IPersonExtended> selectedPeople = removeNPersonsFromDivision(numberToRemoveFromDivision, consideredDivision);
            people.addAll(selectedPeople);

            // if more people in division keep note incase of shortfall in other divisions
            if(selectedPeople.size() >= numberToRemoveFromDivision) {
                // enqueue division is still containing people
                reusableDivisions.addLast(consideredDivision);
            }

        }

        return people;


    }

    private Collection<IPersonExtended> removeNPersonsFromDivision(int numberToRemove, AdvancableDate divisionDate) {

        // The selected people
        Collection<IPersonExtended> selectedPeople = new ArrayList<>();

        if (numberToRemove == 0) {
            return selectedPeople;
        }

        LinkedList<IPersonExtended> cohort = new LinkedList<>(getAllPersonsInTimePeriod(divisionDate, divisionSize));

        while (selectedPeople.size() < numberToRemove) {

            if(cohort.size() <= 0) {
                return selectedPeople;
            }

            IPersonExtended p = cohort.removeFirst();

            try {
                removePerson(p);
                selectedPeople.add(p);
            } catch (PersonNotFoundException e) {
                throw new ConcurrentModificationException();
            }

        }

        return selectedPeople;

    }

    @Override
    public AdvancableDate getStartDate() {
        return startDate;
    }

    @Override
    public uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date getEndDate() {
        return endDate;
    }

    @Override
    public void setStartDate(AdvancableDate startDate) {
        this.startDate = startDate;
    }

    @Override
    public void setEndDate(uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date endDate) {
        this.endDate = endDate;
    }


    public CompoundTimeUnit getDivisionSize() {
        return divisionSize;
    }

    public abstract TreeSet<AdvancableDate> getDivisionDates();

    public TreeSet<AdvancableDate> getDivisionDates(CompoundTimeUnit forTimeStep) {
        int jump = DateUtils.calcSubTimeUnitsInTimeUnit(getDivisionSize(), forTimeStep);

        if(jump == -1) {
            throw new MisalignedTimeDivisionError();
        }

        if(jump == 1) {
            return getDivisionDates();
        }

        int count = jump;

        TreeSet<AdvancableDate> allDivs = getDivisionDates();
        TreeSet<AdvancableDate> selectedDivs = new TreeSet<>();

        for(AdvancableDate div : allDivs) {

            if(count == jump) {
                selectedDivs.add(div);
                count = 0;
            }

            count++;
        }

        return selectedDivs;
    }

    public boolean checkDateAlignmentToDivisions(AdvancableDate date) {
         return DateUtils.matchesInterval(date, divisionSize, startDate);
    }

    public MonthDate resolveDateToCorrectDivisionDate(uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date date) {

        int dM = date.getMonth();
        int dY = date.getYear();

        int sM = startDate.getMonth();
        int sY = startDate.getYear();

        // Time unit in months
        int tsc = DateUtils.monthsInTimeUnit(divisionSize);

        int adm = (12 * ((dY - sY) % tsc)) + dM;

        int cm = (sM % tsc) + tsc * (int) Math.floor((adm - (sM % tsc)) / tsc);

        int absm = cm - 12 * ((dY - sY) % tsc);

        int iM = 12 + absm - 12 * (int) Math.ceil((absm / 12.0D));

        int iY = dY + (int) Math.ceil(absm / 12.0D) - 1;

        return new MonthDate(iM, iY);

    }

}
