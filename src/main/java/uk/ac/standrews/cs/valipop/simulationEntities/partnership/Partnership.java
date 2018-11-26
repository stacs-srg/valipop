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
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
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

    public Partnership(IPerson male, IPerson female, ValipopDate partnershipDate) {

        this(male, female);

        this.partnershipDate = partnershipDate;
    }

    public Partnership(IPerson male, IPerson female) {

        this.id = getNewId();

        this.male = male;
        this.female = female;
    }

    public String toString() {
        String s = "";

        s += "--Partnership: " + id + "--\n";

        s += male.getId() + " | ";
        s += male.getFirstName() + " ";
        s += male.getSurname() + " | ";
        s += male.getSex() + " | ";
        s += male.getBirthDate().getDate() + " | ";
        s += male.getDeathDate() != null ? male.getDeathDate() + "\n" : "no DOD\n";

        s += female.getId() + " | ";
        s += female.getFirstName() + " ";
        s += female.getSurname() + " | ";
        s += female.getSex() + " | ";
        s += female.getBirthDate().getDate() + " | ";
        s += female.getDeathDate() != null ? female.getDeathDate() + "\n" : "no DOD\n";

        s += "----Children----\n";

        for (IPerson c : children) {
            s += c.getId() + " | ";
            s += c.getFirstName() + " ";
            s += c.getSurname() + " | ";
            s += c.getSex() + " | ";
            s += c.getBirthDate().getDate() + " | ";
            s += c.getDeathDate() != null ? c.getDeathDate() + "\n" : "no DOD\n";
        }

        s += "--End Partnership: " + id + "--\n";

        return s;
    }

    public void setPartnershipDate(ValipopDate startDate) {
        partnershipDate = startDate;
    }

    private static int getNewId() {
        return nextId++;
    }

    @Override
    public int getId() {
        return id;
    }

    public static void resetIds() {
        nextId = 0;
    }

    public void setMarriageDate(ValipopDate marriageDate) {
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
    public IPerson getPartnerOf(IPerson person) {

        if (person.getSex() == SexOption.MALE) {
            return female;
        } else {
            return male;
        }
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
    public ValipopDate getSeparationDate(RandomGenerator random) {

        if (earliestPossibleSeparationDate == null) {
            return null;
        } else {

            if (separationDate == null) {

                ValipopDate maleMovedOnDate = getDateOfNextPostSeparationEvent(male, earliestPossibleSeparationDate);
                ValipopDate femaleMovedOnDate = getDateOfNextPostSeparationEvent(female, earliestPossibleSeparationDate);

                ValipopDate earliestMovedOnDate;

                if (maleMovedOnDate != null) {
                    if (femaleMovedOnDate != null) {
                        // if female not null and male not null
                        // pick earliest
                        if (DateUtils.dateBefore(maleMovedOnDate, femaleMovedOnDate)) {
                            earliestMovedOnDate = maleMovedOnDate;
                        } else {
                            earliestMovedOnDate = femaleMovedOnDate;
                        }

                    } else {
                        // if male not null and female null - take male date
                        earliestMovedOnDate = maleMovedOnDate;
                    }

                } else {
                    if (femaleMovedOnDate != null) {
                        // if male null and female not null - take female
                        earliestMovedOnDate = femaleMovedOnDate;
                    } else {
                        // if male null and female null
                        // pick a date in the next 30 years
                        earliestMovedOnDate = earliestPossibleSeparationDate.getYearDate().advanceTime(30, TimeUnit.YEAR);
                    }
                }

                separationDate = new DateSelector(random).selectRandomDate(earliestPossibleSeparationDate, earliestMovedOnDate);
            }

            return separationDate;
        }
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
        return this.id == o.getId() ? 0 : -1;
    }

    @Override
    public void addChildren(Collection<IPerson> children) {
        this.children.addAll(children);
    }

    @Override
    public IPerson getLastChild() {

        ValipopDate latestBirthDate = new YearDate(Integer.MIN_VALUE);
        IPerson latestChild = null;

        for (IPerson child : getChildren()) {
            if (DateUtils.dateBefore(latestBirthDate, child.getBirthDate())) {
                latestBirthDate = child.getBirthDate();
                latestChild = child;
            }
        }

        return latestChild;
    }
}
