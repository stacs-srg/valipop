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

import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SexOption;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.ValipopDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Person implements IPerson {

    private static int nextId = 0;

    private int id;
    private SexOption sex;
    private ExactDate birthDate;
    private ExactDate deathDate = null;
    private List<IPartnership> partnerships = new ArrayList<>();
    private IPartnership parents;

    private final String firstName;
    private final String surname;
    private final String representation;
    private final boolean illegitimate;

    private String deathCause = "";

    public Person(SexOption sex, ValipopDate birthDate, IPartnership parents, PopulationStatistics statistics, boolean illegitimate) {

        id = getNewId();

        this.sex = sex;
        this.birthDate = birthDate.getExactDate();
        this.parents = parents;
        this.illegitimate = illegitimate;

        firstName = getForename(statistics);
        surname = getSurname(statistics);

        representation = firstName + " " + surname + ": " + id;
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
    public IPartnership getParents() {
        return parents;
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
    public Collection<IPerson> getAllChildren() {

        Collection<IPerson> children = new ArrayList<>();

        for (IPartnership partnership : getPartnerships()) {
            children.addAll(partnership.getChildren());
        }

        return children;
    }

    private static int getNewId() {
        return nextId++;
    }

    public static void resetIds() {
        nextId = 0;
    }

    private String getForename(PopulationStatistics statistics) {

        return statistics.getForenameDistribution(getBirthDate(), getSex()).getSample();
    }

    private String getSurname(PopulationStatistics statistics) {

        if (parents != null) {
            return parents.getMalePartner().getSurname();
        }
        else {
            return statistics.getSurnameDistribution(getBirthDate()).getSample();
        }
    }
}
