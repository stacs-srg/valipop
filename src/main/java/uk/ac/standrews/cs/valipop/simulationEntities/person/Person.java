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
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.population.PopulationNavigation;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SexOption;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.ValipopDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvanceableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Person implements IPerson {

    private static RandomGenerator random = null;

    private static int nextId = 0;
    private int id;
    private SexOption sex;
    private ExactDate birthDate;
    private ExactDate deathDate = null;
    private List<IPartnership> partnerships = new ArrayList<>();
    private IPartnership parentsPartnership;

    private final String firstName;
    private final String surname;
    private final String representation;

    private String deathCause = "";

    private boolean illegitimate = false;
    private boolean toSeparate = false;
    private boolean marriageBaby = false;

    public Person(SexOption sex, ValipopDate birthDate, IPartnership parents, PopulationStatistics statistics) {

        initRandomGenerator(statistics);

        id = getNewId();

        this.sex = sex;
        this.birthDate = birthDate.getExactDate();
        this.parentsPartnership = parents;

        firstName = statistics.getForenameDistribution(getBirthDate(), getSex()).getSample();
        surname = getSurname(statistics);

        representation = firstName + " " + surname + ": " + id;
    }

    public Person(SexOption sex, ValipopDate birthDate, IPartnership parentsPartnership, PopulationStatistics statistics, boolean illegitimate) {

        this(sex, birthDate, parentsPartnership, statistics);
        this.illegitimate = illegitimate;
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
    public void recordDeath(ValipopDate date, PopulationStatistics statistics) {

        deathDate = date.getExactDate();

        int ageAtDeath = DateUtils.differenceInYears(birthDate, deathDate).getCount();
        deathCause = statistics.getDeathCauseRates(deathDate, getSex(), ageAtDeath).getSample();
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
    public boolean needsNewPartner(AdvanceableDate currentDate) {
        return partnerships.size() == 0 || toSeparate() || PopulationNavigation.lastPartnerDied(this, currentDate);
    }

    @Override
    public int numberOfChildrenInLatestPartnership() {
        return PopulationNavigation.getLastChild(this).getParentsPartnership().getChildren().size();
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
    public boolean diedInYear(YearDate year) {

        ValipopDate deathDate = getDeathDate();

        return deathDate != null && DateUtils.dateInYear(deathDate, year);
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

        ValipopDate birthDate = getBirthDate();

        return birthDate != null && DateUtils.dateInYear(birthDate, year);
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
    public boolean diedAfter(ValipopDate date) {

        return deathDate == null || DateUtils.dateBefore(date, deathDate);
    }

    @Override
    public void setMarriageBaby(boolean b) {
        marriageBaby = b;
    }

    @Override
    public boolean getMarriageBaby() {
        return marriageBaby;
    }

    private synchronized static void initRandomGenerator(PopulationStatistics statistics) {

        if (random == null) {
            random = statistics.getRandomGenerator();
        }
    }

    private static int getNewId() {
        return nextId++;
    }

    public static void resetIds() {
        nextId = 0;
    }

    private String getSurname(PopulationStatistics ps) {

        IPartnership parents = getParentsPartnership();

        if (parents != null) {
            return parents.getMalePartner().getSurname();
        }

        return ps.getSurnameDistribution(getBirthDate()).getSample();
    }
}
