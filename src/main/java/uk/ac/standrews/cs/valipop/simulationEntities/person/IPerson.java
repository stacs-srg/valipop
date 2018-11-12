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

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.events.death.NotDeadException;
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvanceableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;

import java.util.Collection;
import java.util.List;

/**
 * Interface for person objects.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public interface IPerson extends Comparable<IPerson> {

    /**
     * Representation of female sex.
     */
    char FEMALE = 'F';

    /**
     * Representation of male sex.
     */
    char MALE = 'M';

    /**
     * Gets the person's unique identifier. It can be assumed that identifiers are allocated in temporal
     * order, so an older person's identifier is always less than that of a younger person.
     * @return the person's unique identifier
     */
    int getId();

    /**
     * Gets the person's first name.
     * @return the person's first name
     */
    String getFirstName();

    /**
     * Gets the person's surname (family name).
     * @return the person's surname
     */
    String getSurname();

    /**
     * Gets the person's sex, either {@link #FEMALE} or {@link #MALE}.
     * @return the person's sex
     */
    char getSex();

    /**
     * Gets the person's date of birth.
     * @return the person's date of birth
     */
    java.util.Date getBirthDate();

    /**
     * Gets the person's place of birth, or null if not recorded.
     * @return the person's place of birth
     */
    String getBirthPlace();

    /**
     * Gets the person's date of death, or null if they are living.
     * @return the person's date of death
     */
    java.util.Date getDeathDate();

    /**
     * Gets the person's place of death, or null if not recorded.
     * @return the person's place of death
     */
    String getDeathPlace();

    /**
     * Gets the person's occupation, or null if not recorded.
     * @return the person's occupation
     */
    String getOccupation();

    /**
     * Gets the cause of the person's death, or null if not recorded.
     * @return the cause of the person's death
     */
    String getDeathCause();

    /**
     * Gets the identifiers of the person's partnerships, or null if none are recorded.
     * @return the identifiers of the person's partnerships
     */
    List<Integer> getPartnerships();

    /**
     * Gets the identifier of the person's parents' partnership, or -1 if none are recorded.
     * @return the identifier of the person's parents' partnership
     */
    int getParentsPartnership();

    /**
     * Gets the person's date of birth.
     *
     * @return the person's date of birth
     */
    ExactDate getBirthDate_ex();

    /**
     * Gets the person's date of death, or null if they are living.
     *
     * @return the person's date of death
     */
    ExactDate getDeathDate_ex();

    /**
     * Gets the identifiers of the person's partnerships, or null if none are recorded.
     *
     * @return the identifiers of the person's partnerships
     */
    List<IPartnership> getPartnerships_ex();

    /**
     * Gets the identifier of the person's parents' partnership, or -1 if none are recorded.
     *
     * @return the identifier of the person's parents' partnership
     */
    IPartnership getParentsPartnership_ex();

    boolean isIllegitimate();

    List<IPartnership> getPartnershipsBeforeDate(Date date);

    ExactDate getDateOfLastLegitimatePartnershipEventBeforeDate(ExactDate date);

    boolean isWidow(Date onDate);

    IPerson getPartner(Date onDate);

    boolean noRecentChildren(MonthDate currentDate, CompoundTimeUnit timePeriod);

    void recordPartnership(IPartnership partnership);

    boolean recordDeath(Date date, Population population, PopulationStatistics desiredPopulationStatistics);

    int ageAtDeath() throws NotDeadException;

    boolean aliveOnDate(Date date);

    IPerson getLastChild();

    void addChildrenToCurrentPartnership(int numberOfChildren, AdvanceableDate onDate, CompoundTimeUnit birthTimeStep, Population population, PopulationStatistics ps, Config config);

    boolean toSeparate();

    void willSeparate(boolean b);

    int ageOnDate(Date date);

    boolean needsNewPartner(AdvanceableDate currentDate);

    int numberOfChildrenInLatestPartnership();

    Collection<IPerson> getAllChildren();

    Collection<IPerson> getAllGrandChildren();

    Collection<IPerson> getAllGreatGrandChildren();

    boolean diedInYear(YearDate year);

    Collection<IPartnership> getPartnershipsActiveInYear(YearDate year);

    boolean bornInYear(YearDate year);

    boolean aliveInYear(YearDate y);

    IPartnership getLastPartnership();

    Integer numberOfChildrenBirthedBeforeDate(YearDate y);

    boolean bornBefore(Date year);

    boolean bornOnDate(Date y);

    Date getDateOfNextPostSeparationEvent(Date separationDate);

    Date getDateOfPreviousPreMarriageEvent(Date latestPossibleMarriageDate);

    boolean diedAfter(Date date);

    void setMarriageBaby(boolean b);

    boolean getMarriageBaby();
}
