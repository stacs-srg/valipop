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
import uk.ac.standrews.cs.valipop.utils.Logger;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateSelection.DateSelector;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Partnership implements IPartnership {

    private static Logger log = new Logger(Partnership.class);
    private static int nextId = 0;
    private int id;
    private IPerson male;
    private IPerson female;
    private List<IPerson> children = new ArrayList<>();

    private ExactDate partnershipDate;
    private ExactDate marriageDate = null;
    private ExactDate separationDate = null;
    private ExactDate earliestPossibleSeparationDate = null;

    private static DateSelector dateSelector = new DateSelector();

    public Partnership(IPerson male, IPerson female, ExactDate partnershipDate) {

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

        s += male.getId()+ " | ";
        s += male.getFirstName() + " ";
        s += male.getSurname() + " | ";
        s += male.getSex() + " | ";
        s += male.getBirthDate().toString() + " | ";
        s += male.getDeathDate() != null ? male.getDeathDate().toString() + "\n" : "no DOD\n";

        s += female.getId()+ " | ";
        s += female.getFirstName() + " ";
        s += female.getSurname() + " | ";
        s += female.getSex() + " | ";
        s += female.getBirthDate().toString() + " | ";
        s += female.getDeathDate() != null ? female.getDeathDate().toString() + "\n" : "no DOD\n";

        s += "----Children----\n";

        for(IPerson c : children) {
            s += c.getId()+ " | ";
            s += c.getFirstName() + " ";
            s += c.getSurname() + " | ";
            s += c.getSex() + " | ";
            s += c.getBirthDate().toString() + " | ";
            s += c.getDeathDate() != null ? c.getDeathDate().toString() + "\n" : "no DOD\n";
        }

        s += "--End Partnership: " + id + "--\n";

        return s;
    }

    public void setPartnershipDate(ExactDate startDate) {
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

    @Override
    public int getFemalePartnerId() {
        return getFemalePartner().getId();
    }

    @Override
    public int getMalePartnerId() {
        return getMalePartner().getId();
    }

    @Override
    public int getPartnerOf(int i) {
        if(getFemalePartnerId() == i) {
            return getMalePartnerId();
        } else {
            return getFemalePartnerId();
        }
    }

    @Override
    public java.util.Date getMarriageDate() {
        if(marriageDate == null) {
            return null;
        } else {
            return marriageDate.getDate();
        }
    }

    public void setMarriageDate(ExactDate marriageDate) {
        this.marriageDate = marriageDate;
    }

    @Override
    public ExactDate getMarriageDate_ex() {
        return marriageDate;
    }

    @Override
    public String getMarriagePlace() {
        return null;
    }

    @Override
    public List<Integer> getChildIds() {
        List<Integer> childrenIDs = new ArrayList<>();

        for(IPerson p : getChildren()) {
            childrenIDs.add(p.getId());
        }

        return childrenIDs;
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
    public IPerson getPartnerOf(IPerson id) {
        if (id.getSex() == 'm') {
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
    public ExactDate getPartnershipDate() {
        return partnershipDate;
    }

    @Override
    public ExactDate getSeparationDate(RandomGenerator random) {

        if(earliestPossibleSeparationDate == null) {
            return null;
        } else {

            if(separationDate == null) {

                Date maleMovedOnDate = male.getDateOfNextPostSeparationEvent(earliestPossibleSeparationDate);
                Date femaleMovedOnDate = female.getDateOfNextPostSeparationEvent(earliestPossibleSeparationDate);

                Date earliestMovedOnDate;

                if (maleMovedOnDate != null) {
                    if (femaleMovedOnDate != null) {
                        // if female not null and male not null
                        // pick earliest
                        if(DateUtils.dateBefore(maleMovedOnDate, femaleMovedOnDate)) {
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

                separationDate = dateSelector.selectDate(earliestPossibleSeparationDate, earliestMovedOnDate, random);
            }

            return separationDate;
        }
    }

    @Override
    public ExactDate getEarliestPossibleSeparationDate() {
        return earliestPossibleSeparationDate;
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
    public void separate(ExactDate currentDate, CompoundTimeUnit consideredTimePeriod) {

        earliestPossibleSeparationDate = currentDate;

        female.willSeparate(true);
        male.willSeparate(true);
    }

    @Override
    public IPerson getLastChild() {

        Date latestBirthDate = new YearDate(Integer.MIN_VALUE);
        IPerson latestChild = null;

        for(IPerson c : getChildren()) {
            if(DateUtils.dateBefore(latestBirthDate, c.getBirthDate_ex())) {
                latestBirthDate = c.getBirthDate_ex();
                latestChild = c;
            }
        }

        return latestChild;
    }
}
