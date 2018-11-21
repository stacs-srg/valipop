/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
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
package uk.ac.standrews.cs.valipop.export.gedcom;

import org.gedcom4j.model.*;
import uk.ac.standrews.cs.utilities.DateManipulation;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SexOption;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.ValipopDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvanceableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;

/**
 * Person implementation for a population represented in a GEDCOM file.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk>
 */
public class GEDCOMPerson implements IPerson {

    protected int id;
    private String first_name;
    protected String surname;
    protected SexOption sex;
    private java.util.Date birth_date;
    private String birth_place;
    private java.util.Date death_date;
    private String death_place;
    protected String death_cause;
    protected String occupation;
    private String string_rep;
    protected List<Integer> partnerships;
    private int parents_partnership_id;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getFirstName() {
        return first_name;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    @Override
    public SexOption getSex() {
        return sex;
    }

    @Override
    public String getBirthPlace() {
        return birth_place;
    }

    @Override
    public String getDeathPlace() {
        return death_place;
    }

    @Override
    public String getDeathCause() {
        return death_cause;
    }

    @Override
    public String getOccupation() {
        return occupation;
    }

    @SuppressWarnings("NonFinalFieldReferenceInEquals")
    @Override
    public boolean equals(final Object other) {
        return other instanceof IPerson && ((IPerson) other).getId() == id;
    }

    @SuppressWarnings("NonFinalFieldReferencedInHashCode")
    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return string_rep;
    }

    private static final String MALE_STRING = SexOption.MALE.toString();

    /**
     * Initialises the partnership.
     *
     * @param individual the GEDCOM person representation
     * @throws ParseException if the birth or death date is incorrectly formatted
     */
    GEDCOMPerson(final Individual individual) throws ParseException {

        setId(individual);
        setSex(individual);
        setNames(individual);
        setParents(individual);
        setEvents(individual);
        setOccupation(individual);
    }

    private void setId(final Individual individual) {

        id = GEDCOMPopulationWriter.idToInt(individual.xref);
    }

    private void setSex(final Individual individual) {

        sex = individual.sex.toString().equals(MALE_STRING) ? SexOption.MALE : SexOption.FEMALE;
    }

    private void setNames(final Individual individual) {

        final List<PersonalName> names = individual.names;

        first_name = findFirstNames(names);
        surname = findSurname(names);
    }

    private void setParents(final Individual individual) {

        final List<FamilyChild> families = individual.familiesWhereChild;
        parents_partnership_id = !families.isEmpty() ? GEDCOMPopulationWriter.idToInt(families.get(0).family.xref) : -1;
    }

    private void setEvents(final Individual individual) throws ParseException {

        for (final IndividualEvent event : individual.events) {

            switch (event.type) {

                case BIRTH:
                    birth_date = DateManipulation.parseDate(event.date.toString());
                    birth_place = event.place.placeName;
                    break;

                case DEATH:
                    death_date = DateManipulation.parseDate(event.date.toString());
                    death_place = event.place.placeName;
                    death_cause = event.cause.toString();
                    break;

                default:
                    break;
            }
        }
    }

    private void setOccupation(final Individual individual) {

        final List<IndividualAttribute> occupation_attributes = individual.getAttributesOfType(IndividualAttributeType.OCCUPATION);
        if (!occupation_attributes.isEmpty()) {
            occupation = occupation_attributes.get(0).description.toString();
        }
    }

    private static String findSurname(final List<PersonalName> names) {

        for (final PersonalName gedcom_name : names) {

            final String name = gedcom_name.toString();
            if (name.contains("/")) { // Slashes denote surname
                final int start = name.indexOf('/');
                final int end = name.lastIndexOf('/');
                if (end > start) {
                    return name.substring(start + 1, end);
                }
            }
        }
        return null;
    }

    private static String findFirstNames(final List<PersonalName> names) {

        final StringBuilder builder = new StringBuilder();

        for (final PersonalName gedcom_name : names) {

            if (builder.length() > 0) {
                builder.append(' ');
            }

            String name = gedcom_name.toString();
            if (name.contains("/")) { // Slashes denote surname
                final int start = name.indexOf('/');
                final int end = name.lastIndexOf('/');
                if (end > start) {
                    name = name.substring(0, start) + name.substring(end + 1, name.length());
                }
            }
            builder.append(name);
        }
        return builder.toString();
    }

    @Override
    public ValipopDate getBirthDate() {
        return null;
    }

    @Override
    public ValipopDate getDeathDate() {
        return null;
    }

    @Override
    public List<IPartnership> getPartnerships() {
        return null;
    }

    @Override
    public IPartnership getParentsPartnership() {
        return null;
    }

    @Override
    public boolean isIllegitimate() {
        return false;
    }

    @Override
    public void recordPartnership(IPartnership partnership) {

    }

    @Override
    public void recordDeath(ValipopDate date, PopulationStatistics desiredPopulationStatistics) {
    }

    @Override
    public boolean aliveOnDate(ValipopDate date) {
        return false;
    }

    @Override
    public IPerson getLastChild() {
        return null;
    }

    @Override
    public void addChildrenToCurrentPartnership(int numberOfChildren, AdvanceableDate onDate, CompoundTimeUnit birthTimeStep, Population population, PopulationStatistics ps, Config config) {

    }

    @Override
    public boolean toSeparate() {
        return false;
    }

    @Override
    public void willSeparate(boolean b) {

    }

    @Override
    public int ageOnDate(ValipopDate date) {
        return 0;
    }

    @Override
    public boolean needsNewPartner(AdvanceableDate currentDate) {
        return false;
    }

    @Override
    public int numberOfChildrenInLatestPartnership() {
        return 0;
    }

    @Override
    public Collection<IPerson> getAllChildren() {
        return null;
    }

    @Override
    public boolean diedInYear(YearDate year) {
        return false;
    }

    @Override
    public Collection<IPartnership> getPartnershipsActiveInYear(YearDate year) {
        return null;
    }

    @Override
    public boolean bornInYear(YearDate year) {
        return false;
    }

    @Override
    public IPartnership getLastPartnership() {
        return null;
    }

    @Override
    public Integer numberOfChildrenBirthedBeforeDate(YearDate y) {
        return null;
    }

    @Override
    public ValipopDate getDateOfNextPostSeparationEvent(ValipopDate separationDate) {
        return null;
    }

    @Override
    public boolean diedAfter(ValipopDate date) {
        return false;
    }

    @Override
    public void setMarriageBaby(boolean b) {

    }

    @Override
    public boolean getMarriageBaby() {
        return false;
    }

    @Override
    public int compareTo(IPerson o) {
        return 0;
    }
}
