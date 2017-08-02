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
package uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.entityExtensions;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.EventType;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.birth.NoChildrenOfDesiredOrder;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.death.NotDeadException;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.Population;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PersonUtils {

    boolean noRecentChildren(MonthDate currentDate, CompoundTimeUnit timePeriod);

    void recordPartnership(IPartnershipExtended partnership);

    boolean recordDeath(Date date, Population population);

    void causeEventInTimePeriod(EventType event, Date date, CompoundTimeUnit timePeriod);

    int ageAtDeath() throws NotDeadException;

    boolean aliveOnDate(Date date);

    int ageAtFirstChild() throws NoChildrenOfDesiredOrder;

    IPersonExtended getLastChild();

    int numberOfChildren();

    void keepFather(PeopleCollection population);

    void setParentsPartnership(IPartnershipExtended newParents);

    int numberOfChildrenFatheredChildren();

    IPartnershipExtended isInstigatorOfSeparationOfMothersPreviousPartnership();

    boolean isWidow(Date onDate);

    IPersonExtended getPartner(Date onDate);

    void giveChildren(int numberOfChildren, AdvancableDate onDate, CompoundTimeUnit birthTimeStep, Population population);

    void giveChildrenWithinLastPartnership(int numberOfChildren, AdvancableDate onDate, CompoundTimeUnit birthTimeStep, Population population);

    boolean toSeparate();

    void willSeparate(boolean b);

    int ageOnDate(Date date);

    boolean needsNewPartner(AdvancableDate currentDate);

    int numberOfChildrenInLatestPartnership();

    Collection<IPersonExtended> getAllChildren();

    Collection<IPersonExtended> getAllGrandChildren();

    Collection<IPersonExtended> getAllGreatGrandChildren();

    boolean diedInYear(YearDate year);

    Collection<IPartnershipExtended> getPartnershipsActiveInYear(YearDate year);

    boolean bornInYear(YearDate year);

    boolean aliveInYear(YearDate y);

    IPartnershipExtended getLastPartnership();

    Integer numberOfChildrenBirthedBeforeDate(YearDate y);

    boolean bornBefore(Date year);

    boolean bornOnDate(Date y);
}
