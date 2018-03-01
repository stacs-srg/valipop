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
package uk.ac.standrews.cs.valipop.simulationEntities.entityExtensions;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.events.death.NotDeadException;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.Population;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PersonUtils {

    boolean isWidow(Date onDate);

    IPersonExtended getPartner(Date onDate);

    boolean noRecentChildren(MonthDate currentDate, CompoundTimeUnit timePeriod);

    void recordPartnership(IPartnershipExtended partnership);

    boolean recordDeath(Date date, Population population);

    int ageAtDeath() throws NotDeadException;

    boolean aliveOnDate(Date date);

    IPersonExtended getLastChild();

    void addChildrenToCurrentPartnership(int numberOfChildren, AdvancableDate onDate, CompoundTimeUnit birthTimeStep, Population population, PopulationStatistics ps, Config config);

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

    Date getDateOfNextPostSeparationEvent(Date separationDate);

    Date getDateOfPreviousPreMarriageEvent(Date latestPossibleMarriageDate);

    boolean diedAfter(Date date);

    void setMarriageBaby(boolean b);

    boolean getMarriageBaby();
}
