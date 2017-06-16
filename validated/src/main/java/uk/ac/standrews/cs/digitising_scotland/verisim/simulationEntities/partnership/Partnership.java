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

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPerson;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Partnership implements IPartnership {

    private static Logger log = LogManager.getLogger(Partnership.class);
    private static int nextId = 0;
    private int id;
    private IPerson male;
    private IPerson female;
    private List<IPerson> children = new ArrayList<IPerson>();

    private Date partnershipDate;

    public Partnership(IPerson male, IPerson female, Date partnershipDate) {

        this.id = getNewId();

        this.partnershipDate = partnershipDate;
        this.male = male;
        this.female = female;

    }

    public Partnership(IPerson female, Date partnershipDate) {

        this.id = getNewId();

        this.partnershipDate = partnershipDate;
        this.female = female;

    }

    private static int getNewId() {
        return nextId++;
    }

    @Override
    public int getId() {
        return id;
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
    public Date getPartnershipDate() {
        return partnershipDate;
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
    public void setFather(IPerson father) {
        this.male = father;
        father.recordPartnership(this);
    }
}
