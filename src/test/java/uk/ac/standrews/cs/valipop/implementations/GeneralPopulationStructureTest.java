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
package uk.ac.standrews.cs.valipop.implementations;

import org.junit.Test;
import uk.ac.standrews.cs.utilities.DateManipulation;
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.population.IPopulation;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SexOption;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Tests of properties of abstract population interface that should hold for all populations.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public abstract class GeneralPopulationStructureTest {

    public static final int PEOPLE_ITERATION_SAMPLE_THRESHOLD = 40;
    public static final int PEOPLE_ITERATION_SAMPLE_START = 30;
    public static final int PARTNERSHIP_ITERATION_SAMPLE_THRESHOLD = 20;
    public static final int PARTNERSHIP_ITERATION_SAMPLE_START = 10;

    private IPopulation population;

    public GeneralPopulationStructureTest(final IPopulation population) {

        this.population = population;
    }

    @Test
    public void findNonExistentPerson() {

        assertNull(population.findPerson(-1));
    }

    @Test
    public void findNonExistentPartnership() {

        assertNull(population.findPartnership(-1));
    }

    @Test
    public void iterateOverPopulation() throws Exception {

        final Set<Integer> people = new HashSet<>();
        for (final IPerson person : population.getPeople()) {
            assertFalse(people.contains(person.getId()));
            people.add(person.getId());
        }
        assertEquals(population.getNumberOfPeople(), people.size());

        final Set<Integer> partnerships = new HashSet<>();
        for (final IPartnership partnership : population.getPartnerships()) {
            assertFalse(partnerships.contains(partnership.getId()));
            partnerships.add(partnership.getId());
        }
        assertEquals(population.getNumberOfPartnerships(), partnerships.size());
    }

    @Test(expected = NoSuchElementException.class)
    public void tooManyPersonIterations() throws Exception {

        doTooManyIterations(population.getPeople().iterator(), population.getNumberOfPeople());
    }

    @Test(expected = NoSuchElementException.class)
    public void tooManyPartnershipIterations() throws Exception {

        final Iterator<IPartnership> iterator = population.getPartnerships().iterator();
        final int numberOfPartnerships = population.getNumberOfPartnerships();

        doTooManyIterations(iterator, numberOfPartnerships);
    }

    @Test
    public void peopleRetrievedConsistently() throws Exception {

        // Check consistency after iteration, if the population is large enough to take a sample from the middle.

        if (population.getNumberOfPeople() > PEOPLE_ITERATION_SAMPLE_THRESHOLD) {
            final Iterator<IPerson> person_iterator = population.getPeople().iterator();

            for (int i = 0; i < PEOPLE_ITERATION_SAMPLE_START; i++) {
                person_iterator.next();
            }

            final IPerson[] sample = new IPerson[]{person_iterator.next(), person_iterator.next(), person_iterator.next(), person_iterator.next(), person_iterator.next()};
            assertRetrievedConsistently(sample);
        }

        // Check consistency during iteration.

        for (final IPerson person : population.getPeople()) {
            assertRetrievedConsistently(person);
        }
    }

    @Test
    public void partnershipsRetrievedConsistently() throws Exception {

        // Check consistency after iteration, if the population is large enough to take a sample from the middle.

        if (population.getNumberOfPartnerships() > PARTNERSHIP_ITERATION_SAMPLE_THRESHOLD) {

            final Iterator<IPartnership> partnership_iterator = population.getPartnerships().iterator();

            // Check consistency after iteration.
            for (int i = 0; i < PARTNERSHIP_ITERATION_SAMPLE_START; i++) {
                partnership_iterator.next();
            }

            final IPartnership[] sample = new IPartnership[]{partnership_iterator.next(), partnership_iterator.next(), partnership_iterator.next(), partnership_iterator.next(), partnership_iterator.next()};
            assertRetrievedConsistently(sample);
        }

        // Check consistency during iteration.

        for (final IPartnership partnership : population.getPartnerships()) {
            assertRetrievedConsistently(partnership);
        }
    }

    @Test
    public void birthsBeforeDeaths() {

        for (final IPerson person : population.getPeople()) {

            assertBirthBeforeDeath(person);
        }
    }

    @Test
    public void birthInfoConsistent() {

        for (final IPerson person : population.getPeople()) {

            assertBirthInfoConsistent(person);
        }
    }

    private static void assertBirthInfoConsistent(final IPerson person) {

        assertFalse(person.getBirthDate().getDate() == null && person.getBirthPlace() != null);
    }

    @Test
    public void deathInfoConsistent() {

        for (final IPerson person : population.getPeople()) {

            assertDeathInfoConsistent(person);
        }
    }

    private static void assertDeathInfoConsistent(final IPerson person) {

        assertFalse(!deathDateIsDefined(person) && (deathPlaceIsDefined(person) || deathCauseIsDefined(person)));
    }

    private static boolean deathDateIsDefined(IPerson person) {

        return person.getDeathDate() != null;
    }

    private static boolean deathPlaceIsDefined(IPerson person) {

        return person.getDeathPlace() != null && person.getDeathPlace().length() > 0;
    }

    private static boolean deathCauseIsDefined(IPerson person) {

        return person.getDeathCause() != null && person.getDeathCause().length() > 0;
    }

    @Test
    public void birthsBeforeMarriages() {

        for (final IPerson person : population.getPeople()) {

            assertBirthBeforeMarriages(person);
        }
    }

    @Test
    public void marriagesBeforeDeaths() {

        for (final IPerson person : population.getPeople()) {

            assertMarriagesBeforeDeath(person);
        }
    }

    @Test
    public void sexesConsistent() {

        for (final IPartnership partnership : population.getPartnerships()) {

            assertSexesConsistent(partnership);
        }
    }

    @Test
    public void surnamesInheritedOnMaleLine() {

        for (final IPerson person : population.getPeople()) {

            assertSurnameInheritedOnMaleLine(person);
        }
    }

    @Test
    public void noSiblingPartners() {

        for (final IPerson person : population.getPeople()) {

            assertNoneOfChildrenAreSiblingPartners(person);
        }
    }

    @Test
    public void noParentPartnerOfChild() {

        for (final IPartnership partnership : population.getPartnerships()) {

            assertParentNotPartnerOfChild(partnership);
        }
    }

    @Test
    public void parentsHaveSensibleAgesAtBirths() {

        for (final IPartnership partnership : population.getPartnerships()) {

            assertParentsHaveSensibleAgesAtBirth(partnership);
        }
    }

    @Test
    public void parentsAndChildrenConsistent() {

        for (final IPartnership partnership : population.getPartnerships()) {

            assertParentsAndChildrenConsistent(partnership);
        }
    }

    private void assertParentsAndChildrenConsistent(final IPartnership partnership) {

        for (final IPerson child : partnership.getChildren()) {

            assertEquals(child.getParentsPartnership(), partnership);
        }
    }

    private void assertParentsHaveSensibleAgesAtBirth(final IPartnership partnership) {

        final IPerson mother = partnership.getFemalePartner();
        final IPerson father = partnership.getMalePartner();

        for (final IPerson child : partnership.getChildren()) {

            assertTrue(PopulationLogic.parentsHaveSensibleAgesAtChildBirth(father, mother, child));
        }
    }

    private static void assertParentNotPartnerOfChild(final IPartnership partnership) {

        for (final IPerson child : partnership.getChildren()) {

            assertNotEquals(child, partnership.getFemalePartner());
            assertNotEquals(child, partnership.getMalePartner());
        }
    }

    private void assertNoneOfChildrenAreSiblingPartners(final IPerson person) {

        // Include half-siblings.
        final Set<IPerson> siblings = new HashSet<>();

        for (final IPartnership partnership : person.getPartnerships()) {

            for (final IPerson child : partnership.getChildren()) {

                assertNotPartnerOfAny(child, siblings);
                siblings.add(child);
            }
        }
    }

    private void assertSexesConsistent(final IPartnership partnership) {

        assertEquals(SexOption.FEMALE, partnership.getFemalePartner().getSex());
        assertEquals(SexOption.MALE, partnership.getMalePartner().getSex());
    }

    private void assertNotPartnerOfAny(final IPerson person, final Set<IPerson> people) {

        for (final IPerson another_person : people) {
            boolean partnerOf = isPartnerOf(person, another_person);
            assertFalse(partnerOf);
        }
    }

    private boolean isPartnerOf(final IPerson p1, final IPerson p2) {

        for (final IPartnership partnership : p1.getPartnerships()) {

            if (partnership.getPartnerOf(p1).equals(p2)) {
                return true;
            }
        }

        return false;
    }

    private void assertSurnameInheritedOnMaleLine(final IPerson person) {

        if (person.getSex() == SexOption.MALE) {

            for (final IPartnership partnership : person.getPartnerships()) {

                for (final IPerson child : partnership.getChildren()) {

                    assertEquals(person.getSurname(), child.getSurname());

                    if (child.getSex() == SexOption.MALE) {
                        assertSurnameInheritedOnMaleLine(child);
                    }
                }
            }
        }
    }

    private static void assertBirthBeforeDeath(final IPerson person) {

        if (person.getDeathDate() != null) {

            final Date death_date = person.getDeathDate().getDate();
            final Date birth_date = person.getBirthDate().getDate();
            int i = DateManipulation.differenceInYears(birth_date, death_date);
            assertTrue(i >= 0);
        }
    }

    private void assertBirthBeforeMarriages(final IPerson person) {

        if (person.getBirthDate() != null) {

            final Date birth_date = person.getBirthDate().getDate();

            for (final IPartnership partnership : person.getPartnerships()) {
                if (partnership.getMarriageDate() != null) {
                    final Date marriage_date = partnership.getMarriageDate().getDate();
                    assertTrue(DateManipulation.differenceInYears(birth_date, marriage_date) >= 0);
                }
            }
        }
    }

    private void assertMarriagesBeforeDeath(final IPerson person) {

        if (person.getDeathDate() != null) {

            final Date death_date = person.getDeathDate().getDate();

            for (final IPartnership partnership : person.getPartnerships()) {
                if (partnership.getMarriageDate() != null) {
                    final Date marriage_date = partnership.getMarriageDate().getDate();
                    assertTrue(DateManipulation.differenceInDays(marriage_date, death_date) >= 0);
                }
            }
        }
    }

    private void assertRetrievedConsistently(final IPerson[] sample) {

        for (final IPerson person : sample) {
            assertRetrievedConsistently(person);
        }
    }

    private void assertRetrievedConsistently(final IPerson person) {

        final int id = person.getId();
        final IPerson retrieved_person = population.findPerson(id);

        assertEquals(id, retrieved_person.getId());
    }

    private void assertRetrievedConsistently(final IPartnership[] sample) {

        for (final IPartnership partnership : sample) {
            assertRetrievedConsistently(partnership);
        }
    }

    private void assertRetrievedConsistently(final IPartnership partnership) {

        final int id = partnership.getId();
        final IPartnership retrieved_person = population.findPartnership(id);

        assertEquals(id, retrieved_person.getId());
    }

    private static void doTooManyIterations(final Iterator<?> iterator, final int number_available) {

        for (int i = 0; i < number_available + 1; i++) {
            iterator.next();
        }
    }
}
