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
package uk.ac.standrews.cs.valipop.simulationEntities.person;

import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.annotations.names.FirstNameGenerator;
import uk.ac.standrews.cs.valipop.annotations.names.NameGenerator;
import uk.ac.standrews.cs.valipop.annotations.names.SurnameGenerator;
import uk.ac.standrews.cs.valipop.events.death.NotDeadException;
import uk.ac.standrews.cs.valipop.simulationEntities.EntityFactory;
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.exceptions.PersonNotFoundException;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SexOption;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.MarriageStatsKey;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.IllegitimateBirthStatsKey;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.ValipopDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvanceableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Person implements IPerson {

    private static RandomGenerator random = null;
    private static NameGenerator firstNameGenerator = new FirstNameGenerator();
    private static NameGenerator surnameGenerator = new SurnameGenerator();

    private static int nextId = 0;
    private int id;
    private SexOption sex;
    private ExactDate birthDate;
    private ExactDate deathDate = null;
    private List<IPartnership> partnerships = new ArrayList<>();
    private IPartnership parentsPartnership = null;
    private String firstName;
    private String surname;

    private String deathCause = "";

    private boolean illegitimate = false;

    private boolean toSeparate = false;

    public boolean marriageBaby = false;

    // TODO extract as variable
    private static int earliestAgeOfMarriage = 16;

    public Person(SexOption sex, ValipopDate birthDate, IPartnership parentsPartnership, PopulationStatistics ps) {

        if (random == null) {
            random = ps.getRandomGenerator();
        }

        id = getNewId();

        this.sex = sex;
        this.birthDate = birthDate.getExactDate();
        this.parentsPartnership = parentsPartnership;

        setFirstName(firstNameGenerator.getName(this, ps));
        setSurname(surnameGenerator.getName(this, ps));
    }

    public Person(SexOption sex, ValipopDate birthDate, IPartnership parentsPartnership, PopulationStatistics ps, boolean illegitimate) {

        this(sex, birthDate, parentsPartnership, ps);
        this.illegitimate = illegitimate;
    }

    public String toString() {

        return firstName + " " + surname + ": " + id;
    }

    private static int getNewId() {
        return nextId++;
    }

    public static void resetIds() {
        nextId = 0;
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
    public ValipopDate getBirthDate() {
        return birthDate;
    }

    @Override
    public ValipopDate getDeathDate() {
        return deathDate;
    }

    @Override
    public List<IPartnership> getPartnerships() {
        return partnerships;
    }

    @Override
    public IPartnership getParentsPartnership() {
        return parentsPartnership;
    }

    @Override
    public boolean isIllegitimate() {
        return illegitimate;
    }

    @Override
    public List<IPartnership> getPartnershipsBeforeDate(ValipopDate date) {
        List<IPartnership> partnershipsBeforeDate = new ArrayList<>();

        for (IPartnership partnership : partnerships) {
            if (DateUtils.dateBefore(partnership.getPartnershipDate(), date)) {
                partnershipsBeforeDate.add(partnership);
            }
        }

        return partnershipsBeforeDate;
    }

    @Override
    public ValipopDate getDateOfLastLegitimatePartnershipEventBeforeDate(ValipopDate date) {

        ValipopDate latestDate;

        // Handle the leap year baby... TODO clean up date code in general - this really should be in the Date implementation
        ValipopDate temp = birthDate.getMonthDate().advanceTime(earliestAgeOfMarriage, TimeUnit.YEAR);
        if (temp.getMonth() == DateUtils.FEB && !DateUtils.isLeapYear(temp.getYear()) && birthDate.getDay() == DateUtils.DAYS_IN_LEAP_FEB) {
            latestDate = new ExactDate(birthDate.getDay() - 1, temp.getMonth(), temp.getYear());
        } else {
            latestDate = new ExactDate(birthDate.getDay(), temp.getMonth(), temp.getYear());
        }

        for (IPartnership partnership : partnerships) {
            if (DateUtils.dateBefore(partnership.getPartnershipDate(), date)) {
                List<IPerson> children = partnership.getChildren();
                if (!children.isEmpty()) {
                    if (!children.get(0).isIllegitimate()) {
                        // this partnership has legitimate children

                        // thus check separation date
                        ValipopDate sepDate = partnership.getEarliestPossibleSeparationDate();
                        if (sepDate != null && DateUtils.dateBefore(latestDate, sepDate)) {
                            latestDate = sepDate;
                        }

                        // partner death date
                        ValipopDate partnerDeath = partnership.getPartnerOf(this).getDeathDate();
                        if (partnerDeath != null && DateUtils.dateBefore(latestDate, partnerDeath)) {
                            latestDate = partnerDeath;
                        }
                    }
                } else {
                    System.err.println("Do we now have childless marriages? - If so write this code!");
                }
            }
        }

        return latestDate;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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
    public int compareTo(IPerson o) {
        return Integer.compare(id, o.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean noRecentChildren(MonthDate currentDate, CompoundTimeUnit timePeriod) {

        for (IPartnership p : getPartnerships()) {
            for (IPerson c : p.getChildren()) {
                if (DateUtils.dateBeforeOrEqual(currentDate.advanceTime(timePeriod), c.getBirthDate())) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void recordPartnership(IPartnership partnership) {
        partnerships.add(partnership);
    }

    @Override
    public boolean recordDeath(ValipopDate date, Population population, PopulationStatistics desiredPopulationStatistics) {

        deathDate = date.getExactDate();

        try {
            deathCause = desiredPopulationStatistics.getDeathCauseRates(deathDate, getSex(), ageAtDeath()).getSample();
        } catch (NotDeadException e) {
            throw new Error("Living dead person...");
        }

        return true;
    }

    @Override
    public int ageAtDeath() throws NotDeadException {
        if (deathDate == null) {
            throw new NotDeadException();
        }
        return DateUtils.differenceInYears(birthDate, deathDate).getCount();
    }

    @Override
    public boolean aliveOnDate(ValipopDate date) {

        if (DateUtils.dateBeforeOrEqual(birthDate, date)) {
            return deathDate == null || DateUtils.dateBefore(date, deathDate);
        }
        return false;
    }

    @Override
    public IPerson getLastChild() {

        ValipopDate latestChildBirthDate = new YearDate(Integer.MIN_VALUE);
        IPerson child = null;

        for (IPartnership p : partnerships) {
            for (IPerson c : p.getChildren()) {

                if (DateUtils.dateBeforeOrEqual(latestChildBirthDate, c.getBirthDate())) {
                    latestChildBirthDate = c.getBirthDate();
                    child = c;
                }
            }
        }

        return child;
    }

    @Override
    public boolean isWidow(ValipopDate onDate) {

        IPerson partner = getPartner(onDate);

        if (partner == null) {
            return false;
        } else {
            return !partner.aliveOnDate(onDate);
        }
    }

    @Override
    public IPerson getPartner(ValipopDate onDate) {

        IPartnership currentPartnership = null;

        for (IPartnership p : partnerships) {

            if (DateUtils.dateBeforeOrEqual(p.getPartnershipDate(), onDate)) {

                if (currentPartnership != null) {

                    if (DateUtils.dateBeforeOrEqual(currentPartnership.getPartnershipDate(), p.getPartnershipDate())) {
                        currentPartnership = p;
                    }

                } else {
                    currentPartnership = p;
                }
            }
        }

        if (currentPartnership == null) {
            return null;
        } else if (sex == SexOption.MALE) {
            return currentPartnership.getFemalePartner();
        } else {
            return currentPartnership.getMalePartner();
        }
    }

    @Override
    public void addChildrenToCurrentPartnership(int numberOfChildren, AdvanceableDate onDate, CompoundTimeUnit birthTimeStep, Population population, PopulationStatistics ps, Config config) {

        try {
            population.getLivingPeople().removePerson(this);
        } catch (PersonNotFoundException e) {
            e.printStackTrace();
        }

        IPerson lastChild = getLastChild();
        IPartnership last = lastChild.getParentsPartnership();
        IPerson child = null;

        ValipopDate birthDate = null;

        IPerson man = last.getMalePartner();

        for (int c = 0; c < numberOfChildren; c++) {
            if (birthDate == null) {
                child = EntityFactory.makePerson(onDate, birthTimeStep, last, population, ps);
                last.addChildren(Collections.singleton(child));
                birthDate = child.getBirthDate();
            } else {
                child = EntityFactory.makePerson(onDate, last, population, ps);
                last.addChildren(Collections.singleton(child));
            }
        }

        // record that child is legitimate
        IllegitimateBirthStatsKey illegitimateKey = new IllegitimateBirthStatsKey(man.ageOnDate(birthDate), numberOfChildren, birthTimeStep, birthDate);
        SingleDeterminedCount illegitimateCounts = (SingleDeterminedCount) ps.getDeterminedCount(illegitimateKey, config);
        illegitimateCounts.setFulfilledCount(0);
        ps.returnAchievedCount(illegitimateCounts);

        // decide if to cause marriage
        // Decide on marriage
        MarriageStatsKey marriageKey = new MarriageStatsKey(this.ageOnDate(birthDate), numberOfChildren, birthTimeStep, birthDate);
        SingleDeterminedCount marriageCounts = (SingleDeterminedCount) ps.getDeterminedCount(marriageKey, config);

        if (last.getMarriageDate() != null) {
            // is already married - so return as married
            marriageCounts.setFulfilledCount(numberOfChildren);
        } else {
            boolean toBeMarriedBirth = (int) Math.round(marriageCounts.getDeterminedCount() / (double) numberOfChildren) == 1;

            if (toBeMarriedBirth) {
                marriageCounts.setFulfilledCount(numberOfChildren);
                last.setMarriageDate(EntityFactory.marriageDateSelector.selectDate(lastChild.getBirthDate(), birthDate, ps.getRandomGenerator()));
                child.setMarriageBaby(true);
            } else {
                marriageCounts.setFulfilledCount(0);
            }
        }
        ps.returnAchievedCount(marriageCounts);

        population.getLivingPeople().addPerson(this);
    }

    @Override
    public boolean toSeparate() {
        return toSeparate;
    }

    @Override
    public void willSeparate(boolean b) {
        toSeparate = b;
    }

    @Override
    public int ageOnDate(ValipopDate currentDate) {
        if (birthDate.getDay() == 1 && birthDate.getMonth() == 1) {
            int age = DateUtils.differenceInYears(birthDate, currentDate).getCount() - 1;
            return age == -1 ? 0 : age;
        } else {
            return DateUtils.differenceInYears(birthDate, currentDate).getCount();
        }
    }

    @Override
    public boolean needsNewPartner(AdvanceableDate currentDate) {
        return partnerships.size() == 0 || toSeparate() || lastPartnerDied(currentDate);
    }

    private boolean lastPartnerDied(ValipopDate currentDate) {
        try {
            return !getLastChild().getParentsPartnership().getMalePartner().aliveOnDate(currentDate);
        } catch (NullPointerException e) {
            return true;
        }
    }

    @Override
    public int numberOfChildrenInLatestPartnership() {
        return getLastChild().getParentsPartnership().getChildren().size();
    }

    @Override
    public Collection<IPerson> getAllChildren() {
        Collection<IPerson> children = new ArrayList<>();

        for (IPartnership part : getPartnerships()) {
            children.addAll(part.getChildren());
        }

        return children;
    }

    @Override
    public Collection<IPerson> getAllGrandChildren() {
        Collection<IPerson> grandChildren = new ArrayList<>();

        Collection<IPerson> children = getAllChildren();

        for (IPerson c : children) {
            grandChildren.addAll(c.getAllChildren());
        }

        return grandChildren;
    }

    @Override
    public Collection<IPerson> getAllGreatGrandChildren() {
        Collection<IPerson> greatGrandChildren = new ArrayList<>();

        Collection<IPerson> grandChildren = getAllGrandChildren();

        for (IPerson gC : grandChildren) {
            greatGrandChildren.addAll(gC.getAllChildren());
        }

        return greatGrandChildren;
    }

    @Override
    public boolean diedInYear(YearDate year) {
        if (getDeathDate() == null) {
            return false;
        }

        return DateUtils.dateInYear(getDeathDate(), year);
    }

    @Override
    public Collection<IPartnership> getPartnershipsActiveInYear(YearDate year) {

        Collection<IPartnership> activePartnerships = new ArrayList<>();

        for (IPartnership part : getPartnerships()) {
            ValipopDate startDate = part.getPartnershipDate();

            if (DateUtils.dateInYear(startDate, year)) {
                activePartnerships.add(part);
            } else {
                for (IPerson p : part.getChildren()) {
                    if (DateUtils.dateInYear(p.getBirthDate(), year)) {
                        activePartnerships.add(part);
                        break;
                    }
                }
            }
        }

        return activePartnerships;
    }

    @Override
    public boolean bornInYear(YearDate year) {
        if (getBirthDate() == null) {
            return false;
        }

        return DateUtils.dateInYear(getBirthDate(), year);
    }

    @Override
    public boolean aliveInYear(YearDate y) {
        return bornInYear(y) || diedInYear(y) || aliveOnDate(y);
    }

    @Override
    public IPartnership getLastPartnership() {

        ValipopDate latestPartnershipDate = new YearDate(Integer.MIN_VALUE);
        IPartnership partnership = null;

        for (IPartnership p : partnerships) {
            if (DateUtils.dateBefore(latestPartnershipDate, p.getPartnershipDate())) {
                latestPartnershipDate = p.getPartnershipDate();
                partnership = p;
            }
        }
        return partnership;
    }

    @Override
    public Integer numberOfChildrenBirthedBeforeDate(YearDate y) {

        int count = 0;

        for (IPartnership p : getPartnerships()) {
            for (IPerson c : p.getChildren()) {
                if (DateUtils.dateBefore(c.getBirthDate(), y)) {
                    count++;
                }
            }
        }

        return count;
    }

    @Override
    public boolean bornBefore(ValipopDate date) {
        return DateUtils.dateBefore(getBirthDate(), date);
    }

    @Override
    public boolean bornOnDate(ValipopDate y) {
        return DateUtils.datesEqual(y, birthDate);
    }

    @Override
    public ValipopDate getDateOfNextPostSeparationEvent(ValipopDate separationDate) {

        ValipopDate earliestDate = null;

        for (IPartnership part : partnerships) {
            ValipopDate date = part.getPartnershipDate();
            if (DateUtils.dateBefore(separationDate, date)) {

                if (earliestDate == null || DateUtils.dateBefore(date, earliestDate)) {
                    earliestDate = date;
                }
            }

            date = part.getMarriageDate();

            if (date != null) {
                if (DateUtils.dateBefore(separationDate, date)) {

                    if (earliestDate == null || DateUtils.dateBefore(date, earliestDate)) {
                        earliestDate = date;
                    }
                }
            }
        }

        if (earliestDate == null) {
            earliestDate = deathDate;
        }

        return earliestDate;
    }

    @Override
    public ValipopDate getDateOfPreviousPreMarriageEvent(ValipopDate latestPossibleMarriageDate) {

        ValipopDate earliestPossibleMarriageDate =
                new ExactDate(birthDate.getMonthDate().advanceTime(16, TimeUnit.YEAR)).advanceTime(birthDate.getDay());

        if (partnerships.size() == 0) {
            return earliestPossibleMarriageDate;
        } else {

            ValipopDate latestEventDate = earliestPossibleMarriageDate;

            for (IPartnership p : partnerships) {

                if (p.getChildren().get(0).isIllegitimate()) {
                    // dont care
                } else {

                    ValipopDate sepDate = p.getEarliestPossibleSeparationDate();
                    ValipopDate spouseDeathDate = p.getPartnerOf(this).getDeathDate();

                    // TODO prevent selection of dates after latestPossibleMarriageDate

                    if (sepDate != null) {
                        if (DateUtils.dateBefore(latestEventDate, sepDate) && DateUtils.dateBefore(sepDate, latestPossibleMarriageDate)) {
                            latestEventDate = sepDate;
                        }
                    }

                    if (spouseDeathDate != null) {
                        if (DateUtils.dateBefore(latestEventDate, spouseDeathDate) && DateUtils.dateBefore(spouseDeathDate, latestPossibleMarriageDate)) {
                            latestEventDate = spouseDeathDate;
                        }
                    }
                }
            }

            return latestEventDate;
        }

        // we want to find the last event before the latestPossibleMarriageDate
        // the events we are looking for are:
        // - previous child with current partner
        // - date of separation from previous partner
        // - NOT interested in events where the partner produced illegitimate children
    }

    @Override
    public boolean diedAfter(ValipopDate date) {
        if (deathDate == null) {
            return true;
        }
        return DateUtils.dateBefore(date, deathDate);
    }

    @Override
    public void setMarriageBaby(boolean b) {
        marriageBaby = b;
    }

    @Override
    public boolean getMarriageBaby() {
        return marriageBaby;
    }
}
