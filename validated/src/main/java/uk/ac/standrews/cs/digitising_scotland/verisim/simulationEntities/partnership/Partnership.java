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
package uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Partnership implements IPartnershipExtended {

    private static Logger log = LogManager.getLogger(Partnership.class);
    private static int nextId = 0;
    private int id;
    private IPersonExtended male;
    private IPersonExtended female;
    private List<IPersonExtended> children = new ArrayList<IPersonExtended>();

    private Date partnershipDate;
    private Date separationDate = null;

    public Partnership(IPersonExtended male, IPersonExtended female, Date partnershipDate) {

        this.id = getNewId();

        this.partnershipDate = partnershipDate;
        this.male = male;
        this.female = female;

    }

    public Partnership(IPersonExtended female, Date partnershipDate) {

        this.id = getNewId();

        this.partnershipDate = partnershipDate;
        this.female = female;

    }

    public Partnership(IPersonExtended female) {

        this.id = getNewId();

        this.female = female;

    }

    public void setPartnershipDate(Date startDate) {
        partnershipDate = startDate;
    }

    private static int getNewId() {
        return nextId++;
    }

    @Override
    public int getId() {
        return id;
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
        return getPartnershipDate().getDate();
    }

    @Override
    public String getMarriagePlace() {
        return null;
    }

    @Override
    public List<Integer> getChildIds() {
        List<Integer> childrenIDs = Collections.emptyList();

        for(IPersonExtended p : getChildren()) {
            childrenIDs.add(p.getId());
        }

        return childrenIDs;
    }

    @Override
    public IPersonExtended getFemalePartner() {
        return female;
    }

    @Override
    public IPersonExtended getMalePartner() {
        return male;
    }

    @Override
    public IPersonExtended getPartnerOf(IPersonExtended id) {
        if (id.getSex() == 'm') {
            return female;
        } else {
            return male;
        }
    }

    @Override
    public List<IPersonExtended> getChildren() {
        return children;
    }

    @Override
    public Date getPartnershipDate() {
        return partnershipDate;
    }

    @Override
    public Date getSeparationDate() {
        // TODO separation stuff

        return separationDate;
    }

    @Override
    public int compareTo(IPartnership o) {
        return this.id == o.getId() ? 0 : -1;
    }

    @Override
    public void addChildren(Collection<IPersonExtended> children) {
        this.children.addAll(children);
    }

    @Override
    public void setFather(IPersonExtended father) {
        this.male = father;
        father.recordPartnership(this);
    }

    @Override
    public void separate(Date currentDate, CompoundTimeUnit consideredTimePeriod) {
        // TODO move over to selecting a date between the currentDate and the date of next partnership

        separationDate = currentDate;

    }

    @Override
    public IPersonExtended getLastChild() {

        Date latestBirthDate = new YearDate(Integer.MIN_VALUE);
        IPersonExtended latestChild = null;

        for(IPersonExtended c : getChildren()) {
            if(DateUtils.dateBefore(latestBirthDate, c.getBirthDate_ex())) {
                latestBirthDate = c.getBirthDate_ex();
                latestChild = c;
            }
        }

        return latestChild;
    }
}
