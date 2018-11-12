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
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.exceptions.PersonNotFoundException;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.MarriageStatsKey;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.IllegitimateBirthStatsKey;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvanceableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Person implements IPersonExtended {

    private static RandomGenerator random = null;
    private static NameGenerator firstNameGenerator = new FirstNameGenerator();
    private static NameGenerator surnameGenerator = new SurnameGenerator();

    private static int nextId = 0;
    private int id;
    private char sex;
    private ExactDate birthDate;
    private ExactDate deathDate = null;
    private List<IPartnershipExtended> partnerships = new ArrayList<>();
    private IPartnershipExtended parentsPartnership = null;
    private String firstName;
    private String surname;

    private String deathCause = "";

    private boolean illegitimate = false;

    private boolean toSeparate = false;

    public boolean marriageBaby = false;

    // TODO extract as variable
    private static int earliestAgeOfMarriage = 16;

    public Person(char sex, Date birthDate, IPartnershipExtended parentsPartnership, PopulationStatistics ps) {

        if (random == null) {
            random = ps.getRandomGenerator();
        }

        id = getNewId();

        this.sex = Character.toLowerCase(sex);
        this.birthDate = birthDate.getExactDate();
        this.parentsPartnership = parentsPartnership;

        setFirstName(firstNameGenerator.getName(this, ps));
        setSurname(surnameGenerator.getName(this, ps));
    }

    public Person(char sex, Date birthDate, IPartnershipExtended parentsPartnership, PopulationStatistics ps, boolean illegitimate) {
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
    public char getSex() {
        return sex;
    }

    @Override
    public java.util.Date getBirthDate() {
        return getBirthDate_ex().getDate();
    }

    @Override
    public ExactDate getBirthDate_ex() {
        return birthDate;
    }

    @Override
    public ExactDate getDeathDate_ex() {
        return deathDate;
    }

    @Override
    public List<IPartnershipExtended> getPartnerships_ex() {
        return partnerships;
    }

    @Override
    public IPartnershipExtended getParentsPartnership_ex() {
        return parentsPartnership;
    }

    @Override
    public boolean isIllegitimate() {
        return illegitimate;
    }

    @Override
    public List<IPartnershipExtended> getPartnershipsBeforeDate(Date date) {
        List<IPartnershipExtended> partnershipsBeforeDate = new ArrayList<>();

        for (IPartnershipExtended partnership : partnerships) {
            if (DateUtils.dateBefore(partnership.getPartnershipDate(), date)) {
                partnershipsBeforeDate.add(partnership);
            }
        }

        return partnershipsBeforeDate;
    }

    @Override
    public ExactDate getDateOfLastLegitimatePartnershipEventBeforeDate(ExactDate date) {

        ExactDate latestDate;

        // Handle the leap year baby... TODO clean up date code in general - this really should be in the Date implementation
        Date temp = birthDate.getMonthDate().advanceTime(earliestAgeOfMarriage, TimeUnit.YEAR);
        if (temp.getMonth() == DateUtils.FEB && !DateUtils.isLeapYear(temp.getYear()) && birthDate.getDay() == DateUtils.DAYS_IN_LEAP_FEB) {
            latestDate = new ExactDate(birthDate.getDay() - 1, temp.getMonth(), temp.getYear());
        } else {
            latestDate = new ExactDate(birthDate.getDay(), temp.getMonth(), temp.getYear());
        }

        for (IPartnershipExtended partnership : partnerships) {
            if (DateUtils.dateBefore(partnership.getPartnershipDate(), date)) {
                List<IPersonExtended> children = partnership.getChildren();
                if (!children.isEmpty()) {
                    if (!children.get(0).isIllegitimate()) {
                        // this partnership has legitimate children

                        // thus check separation date
                        ExactDate sepDate = partnership.getEarliestPossibleSeparationDate();
                        if (sepDate != null && DateUtils.dateBefore(latestDate, sepDate)) {
                            latestDate = sepDate;
                        }

                        // partner death date
                        ExactDate partnerDeath = partnership.getPartnerOf(this).getDeathDate_ex();
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
    public java.util.Date getDeathDate() {
        if (getDeathDate_ex() == null) {
            return null;
        } else {
            return getDeathDate_ex().getDate();
        }
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
    public List<Integer> getPartnerships() {

        List<Integer> partnerIDs = new ArrayList<>();

        for (IPartnershipExtended partnership : getPartnerships_ex()) {
            partnerIDs.add(partnership.getId());
        }

        return partnerIDs;
    }

    @Override
    public int getParentsPartnership() {
        if (getParentsPartnership_ex() == null) {
            return -1;
        } else {
            return getParentsPartnership_ex().getId();
        }
    }

    @Override
    public int compareTo(IPersonExtended o) {
        return this.id == o.getId() ? 0 : -1;
    }

    @Override
    public boolean noRecentChildren(MonthDate currentDate, CompoundTimeUnit timePeriod) {

        for (IPartnershipExtended p : getPartnerships_ex()) {
            for (IPersonExtended c : p.getChildren()) {
                if (DateUtils.dateBeforeOrEqual(currentDate.advanceTime(timePeriod), c.getBirthDate_ex())) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void recordPartnership(IPartnershipExtended partnership) {
        partnerships.add(partnership);
    }

    @Override
    public boolean recordDeath(Date date, Population population, PopulationStatistics desiredPopulationStatistics) {

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
    public boolean aliveOnDate(Date date) {

        if (DateUtils.dateBeforeOrEqual(birthDate, date)) {
            return deathDate == null || DateUtils.dateBefore(date, deathDate);
        }
        return false;
    }

    @Override
    public IPersonExtended getLastChild() {

        Date latestChildBirthDate = new YearDate(Integer.MIN_VALUE);
        IPersonExtended child = null;

        for (IPartnershipExtended p : partnerships) {
            for (IPersonExtended c : p.getChildren()) {

                if (DateUtils.dateBeforeOrEqual(latestChildBirthDate, c.getBirthDate_ex())) {
                    latestChildBirthDate = c.getBirthDate_ex();
                    child = c;
                }
            }
        }

        return child;
    }

    @Override
    public boolean isWidow(Date onDate) {

        IPersonExtended partner = getPartner(onDate);

        if (partner == null) {
            return false;
        } else {
            return !partner.aliveOnDate(onDate);
        }
    }

    @Override
    public IPersonExtended getPartner(Date onDate) {

        IPartnershipExtended currentPartnership = null;

        for (IPartnershipExtended p : partnerships) {

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
        } else if (sex == MALE) {
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

        IPersonExtended lastChild = getLastChild();
        IPartnershipExtended last = lastChild.getParentsPartnership_ex();
        IPersonExtended child = null;

        Date birthDate = null;

        IPersonExtended man = last.getMalePartner();

        for (int c = 0; c < numberOfChildren; c++) {
            if (birthDate == null) {
                child = EntityFactory.makePerson(onDate, birthTimeStep, last, population, ps);
                last.addChildren(Collections.singleton(child));
                birthDate = child.getBirthDate_ex();
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

        if (last.getMarriageDate_ex() != null) {
            // is already married - so return as married
            marriageCounts.setFulfilledCount(numberOfChildren);
        } else {
            boolean toBeMarriedBirth = (int) Math.round(marriageCounts.getDeterminedCount() / (double) numberOfChildren) == 1;

            if (toBeMarriedBirth) {
                marriageCounts.setFulfilledCount(numberOfChildren);
                last.setMarriageDate(EntityFactory.marriageDateSelector.selectDate(lastChild.getBirthDate_ex(), birthDate, ps.getRandomGenerator()));
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
    public int ageOnDate(Date currentDate) {
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

    private boolean lastPartnerDied(Date currentDate) {
        try {
            return !getLastChild().getParentsPartnership_ex().getMalePartner().aliveOnDate(currentDate);
        } catch (NullPointerException e) {
            return true;
        }
    }

    @Override
    public int numberOfChildrenInLatestPartnership() {
        return getLastChild().getParentsPartnership_ex().getChildren().size();
    }

    @Override
    public Collection<IPersonExtended> getAllChildren() {
        Collection<IPersonExtended> children = new ArrayList<>();

        for (IPartnershipExtended part : getPartnerships_ex()) {
            children.addAll(part.getChildren());
        }

        return children;
    }

    @Override
    public Collection<IPersonExtended> getAllGrandChildren() {
        Collection<IPersonExtended> grandChildren = new ArrayList<>();

        Collection<IPersonExtended> children = getAllChildren();

        for (IPersonExtended c : children) {
            grandChildren.addAll(c.getAllChildren());
        }

        return grandChildren;
    }

    @Override
    public Collection<IPersonExtended> getAllGreatGrandChildren() {
        Collection<IPersonExtended> greatGrandChildren = new ArrayList<>();

        Collection<IPersonExtended> grandChildren = getAllGrandChildren();

        for (IPersonExtended gC : grandChildren) {
            greatGrandChildren.addAll(gC.getAllChildren());
        }

        return greatGrandChildren;
    }

    @Override
    public boolean diedInYear(YearDate year) {
        if (getDeathDate_ex() == null) {
            return false;
        }

        return DateUtils.dateInYear(getDeathDate_ex(), year);
    }

    @Override
    public Collection<IPartnershipExtended> getPartnershipsActiveInYear(YearDate year) {

        Collection<IPartnershipExtended> activePartnerships = new ArrayList<>();

        for (IPartnershipExtended part : getPartnerships_ex()) {
            Date startDate = part.getPartnershipDate();

            if (DateUtils.dateInYear(startDate, year)) {
                activePartnerships.add(part);
            } else {
                for (IPersonExtended p : part.getChildren()) {
                    if (DateUtils.dateInYear(p.getBirthDate_ex(), year)) {
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
        if (getBirthDate_ex() == null) {
            return false;
        }

        return DateUtils.dateInYear(getBirthDate_ex(), year);
    }

    @Override
    public boolean aliveInYear(YearDate y) {
        return bornInYear(y) || diedInYear(y) || aliveOnDate(y);
    }

    @Override
    public IPartnershipExtended getLastPartnership() {

        Date latestPartnershipDate = new YearDate(Integer.MIN_VALUE);
        IPartnershipExtended partnership = null;

        for (IPartnershipExtended p : partnerships) {
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

        for (IPartnershipExtended p : getPartnerships_ex()) {
            for (IPersonExtended c : p.getChildren()) {
                if (DateUtils.dateBefore(c.getBirthDate_ex(), y)) {
                    count++;
                }
            }
        }

        return count;
    }

    @Override
    public boolean bornBefore(Date date) {
        return DateUtils.dateBefore(getBirthDate_ex(), date);
    }

    @Override
    public boolean bornOnDate(Date y) {
        return DateUtils.datesEqual(y, birthDate);
    }

    @Override
    public Date getDateOfNextPostSeparationEvent(Date separationDate) {

        Date earliestDate = null;

        for (IPartnershipExtended part : partnerships) {
            Date date = part.getPartnershipDate();
            if (DateUtils.dateBefore(separationDate, date)) {

                if (earliestDate == null || DateUtils.dateBefore(date, earliestDate)) {
                    earliestDate = date;
                }
            }

            date = part.getMarriageDate_ex();

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
    public Date getDateOfPreviousPreMarriageEvent(Date latestPossibleMarriageDate) {

        ExactDate earliestPossibleMarriageDate =
                new ExactDate(birthDate.getMonthDate().advanceTime(16, TimeUnit.YEAR)).advanceTime(birthDate.getDay());

        if (partnerships.size() == 0) {
            return earliestPossibleMarriageDate;
        } else {

            Date latestEventDate = earliestPossibleMarriageDate;

            for (IPartnershipExtended p : partnerships) {

                if (p.getChildren().get(0).isIllegitimate()) {
                    // dont care
                } else {

                    ExactDate sepDate = p.getEarliestPossibleSeparationDate();
                    Date spouseDeathDate = p.getPartnerOf(this).getDeathDate_ex();

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
    public boolean diedAfter(Date date) {
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
