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
package uk.ac.standrews.cs.valipop.simulationEntities.partnership;

import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SexOption;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.ValipopDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateSelection.DateSelector;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static uk.ac.standrews.cs.valipop.simulationEntities.population.PopulationNavigation.getDateOfNextPostSeparationEvent;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Partnership implements IPartnership {

    private static int nextId = 0;
    private int id;
    private IPerson male;
    private IPerson female;
    private List<IPerson> children = new ArrayList<>();

    private ValipopDate partnershipDate;
    private ValipopDate marriageDate = null;
    private ValipopDate separationDate = null;
    private ValipopDate earliestPossibleSeparationDate = null;

    public Partnership(final IPerson male, final IPerson female, final ValipopDate partnershipDate) {

        this(male, female);

        this.partnershipDate = partnershipDate;
    }

    public Partnership(final IPerson male, final IPerson female) {

        this.id = getNewId();

        this.male = male;
        this.female = female;
    }

    public String toString() {

        StringBuilder s = new StringBuilder();

        s.append("--Partnership: ");
        s.append(id).append("--\n");

        s.append(male.getId()).append(" | ");
        s.append(male.getFirstName()).append(" ");
        s.append(male.getSurname()).append(" | ");
        s.append(male.getSex()).append(" | ");
        s.append(male.getBirthDate().getDate()).append(" | ");
        s.append(male.getDeathDate() != null ? male.getDeathDate() + "\n" : "no DOD\n");

        s.append(female.getId()).append(" | ");
        s.append(female.getFirstName()).append(" ");
        s.append(female.getSurname()).append(" | ");
        s.append(female.getSex()).append(" | ");
        s.append(female.getBirthDate().getDate()).append(" | ");
        s.append(female.getDeathDate() != null ? female.getDeathDate() + "\n" : "no DOD\n");

        s.append("----Children----\n");

        for (IPerson c : children) {
            s.append(c.getId()).append(" | ");
            s.append(c.getFirstName()).append(" ");
            s.append(c.getSurname()).append(" | ");
            s.append(c.getSex()).append(" | ");
            s.append(c.getBirthDate().getDate()).append(" | ");
            s.append(c.getDeathDate() != null ? c.getDeathDate() + "\n" : "no DOD\n");
        }

        s.append("--End Partnership: ");
        s.append(id).append("--\n");

        return s.toString();
    }

    public void setPartnershipDate(final ValipopDate startDate) {
        partnershipDate = startDate;
    }

    private static synchronized int getNewId() {
        return nextId++;
    }

    @Override
    public int getId() {
        return id;
    }

    public static synchronized void resetIds() {
        nextId = 0;
    }

    public void setMarriageDate(final ValipopDate marriageDate) {
        this.marriageDate = marriageDate;
    }

    @Override
    public ValipopDate getMarriageDate() {
        return marriageDate;
    }

    @Override
    public String getMarriagePlace() {
        return null;
    }

    @Override
    public IPerson getFemalePartner() {
        return female;
    }

    @Override
    public IPerson getMalePartner() {
        return male;
    }

    @Override
    public IPerson getPartnerOf(final IPerson person) {

        return person.getSex() == SexOption.MALE ? female : male;
    }

    @Override
    public List<IPerson> getChildren() {
        return children;
    }

    @Override
    public ValipopDate getPartnershipDate() {
        return partnershipDate;
    }

    @Override
    public synchronized ValipopDate getSeparationDate(final RandomGenerator random) {

        if (earliestPossibleSeparationDate == null) return null;
        if (separationDate == null) setSeparationDate(random);

        return separationDate;
    }

    private void setSeparationDate(final RandomGenerator random) {

        final ValipopDate maleMovedOnDate = getDateOfNextPostSeparationEvent(male, earliestPossibleSeparationDate);
        final ValipopDate femaleMovedOnDate = getDateOfNextPostSeparationEvent(female, earliestPossibleSeparationDate);

        final ValipopDate earliestMovedOnDate;

        if (maleMovedOnDate != null) {

            if (femaleMovedOnDate != null) {
                earliestMovedOnDate = (DateUtils.dateBefore(maleMovedOnDate, femaleMovedOnDate)) ? maleMovedOnDate : femaleMovedOnDate;
            } else {
                earliestMovedOnDate = maleMovedOnDate;
            }

        } else {
            if (femaleMovedOnDate != null) {
                earliestMovedOnDate = femaleMovedOnDate;

            } else {

                // pick a date in the next 30 years
                earliestMovedOnDate = earliestPossibleSeparationDate.getYearDate().advanceTime(30, TimeUnit.YEAR);
            }
        }

        separationDate = new DateSelector(random).selectRandomDate(earliestPossibleSeparationDate, earliestMovedOnDate);
    }

    @Override
    public ValipopDate getEarliestPossibleSeparationDate() {
        return earliestPossibleSeparationDate;
    }

    @Override
    public void setEarliestPossibleSeparationDate(ValipopDate date) {
        earliestPossibleSeparationDate = date;
    }

    @Override
    public int compareTo(IPartnership o) {
        return Integer.compare(id, o.getId());
    }

    @Override
    public void addChildren(Collection<IPerson> children) {
        this.children.addAll(children);
    }
}
