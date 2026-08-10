/*
 * valipop - <https://github.com/stacs-srg/valipop>
 * Copyright © 2026 Systems Research Group, University of St Andrews (graham.kirby@st-andrews.ac.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop.population;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.IPersonCollection;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.SexOption;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests of properties of abstract population interface that should hold for all populations.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class PopulationPropertiesTest {

    private static final int MAX_REASONABLE_FAMILY_SIZE = 20;
    private static final int MINIMUM_MOTHER_AGE_AT_CHILDBIRTH = 12;
    private static final int MAXIMUM_MOTHER_AGE_AT_CHILDBIRTH = 55;
    private static final int MINIMUM_FATHER_AGE_AT_CHILDBIRTH = 12;
    private static final int MAX_GESTATION_IN_DAYS = 300;
    private static final int MINIMUM_AGE_AT_MARRIAGE = 14;

    private static final Path TEST_RESOURCE_DIR = Path.of("src/test/resources/valipop/population");

    private static final List<Arguments> configurations = List.of(
        makePopulation(TEST_RESOURCE_DIR.resolve("1855-2016-initial-200.config")),
        makePopulation(TEST_RESOURCE_DIR.resolve("1855-2016-initial-300.config"))
    );

    private static final List<Arguments> slowConfigurations = List.of(
        makePopulation(TEST_RESOURCE_DIR.resolve("1855-2016-initial-1000.config")),
        makePopulation(TEST_RESOURCE_DIR.resolve("1855-2016-initial-10000.config"))
    );

    public static Arguments makePopulation(final Path configPath) {

        try {
            final Config config = new Config(configPath);

            final OBDModel model = new OBDModel(config);
            model.runSimulation();

            final IPersonCollection population = model.getPopulation().getPeople();
            population.setDescription("initial size=" + config.getTargetInitialPopulationSize() + ", seed=" + config.getSeed());

            return Arguments.of(population);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void nonExistentPersonIsntFound(final IPersonCollection population) {

        checkNonExistentPersonIsntFound(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void nonExistentPersonIsntFoundSlow(final IPersonCollection population) {

        checkNonExistentPersonIsntFound(population);
    }

    private static void checkNonExistentPersonIsntFound(final IPersonCollection population) {

        assertNull(population.findPerson(-1));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void nonExistentPartnershipIsntFound(final IPersonCollection population) {

        checkNonExistentPartnershipIsntFound(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void nonExistentPartnershipIsntFoundSlow(final IPersonCollection population) {

        checkNonExistentPartnershipIsntFound(population);
    }

    private static void checkNonExistentPartnershipIsntFound(final IPersonCollection population) {

        assertNull(population.findPartnership(-1));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void numberOfPeopleIsConsistent(final IPersonCollection population) {

        checkNumberOfPeopleIsConsistent(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void numberOfPeopleIsConsistentSlow(final IPersonCollection population) {

        checkNumberOfPeopleIsConsistent(population);
    }

    private static void checkNumberOfPeopleIsConsistent(final IPersonCollection population) {

        int count = 0;
        for (final IPerson ignored : population.getPeople())
            count++;

        assertEquals(population.getNumberOfPeople(), count);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void personIDsArentRepeated(final IPersonCollection population) {

        checkPersonIDsArentRepeated(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void personIDsArentRepeatedSlow(final IPersonCollection population) {

        checkPersonIDsArentRepeated(population);
    }

    private static void checkPersonIDsArentRepeated(final IPersonCollection population) {
        final Set<Integer> ids = new HashSet<>();

        for (final IPerson person : population.getPeople()) {
            assertFalse(ids.contains(person.getId()));
            ids.add(person.getId());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void numberOfPartnershipsIsConsistent(final IPersonCollection population) {

        checkNumberOfPartnershipsIsConsistent(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void numberOfPartnershipsIsConsistentSlow(final IPersonCollection population) {

        checkNumberOfPartnershipsIsConsistent(population);
    }

    private static void checkNumberOfPartnershipsIsConsistent(final IPersonCollection population) {

        int count = 0;
        for (final IPartnership ignored : population.getPartnerships())
            count++;

        assertEquals(population.getNumberOfPartnerships(), count);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void partnershipIDsArentRepeated(final IPersonCollection population) {

        checkPartnershipIDsArentRepeated(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void partnershipIDsArentRepeatedSlow(final IPersonCollection population) {

        checkPartnershipIDsArentRepeated(population);
    }

    private static void checkPartnershipIDsArentRepeated(final IPersonCollection population) {

        final Set<Integer> ids = new HashSet<>();

        for (final IPartnership partnership : population.getPartnerships()) {
            assertFalse(ids.contains(partnership.getId()));
            ids.add(partnership.getId());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void tooManyPersonIterations(final IPersonCollection population) {

        checkTooManyPersonIterations(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void tooManyPersonIterationsSlow(final IPersonCollection population) {

        checkTooManyPersonIterations(population);
    }

    private static void checkTooManyPersonIterations(final IPersonCollection population) {

        assertThrows(NoSuchElementException.class, () ->
            doTooManyIterations(population.getPeople().iterator(), population.getNumberOfPeople()));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void tooManyPartnershipIterations(final IPersonCollection population) {

        checkTooManyPartnershipIterations(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void tooManyPartnershipIterationsSlow(final IPersonCollection population) {

        checkTooManyPartnershipIterations(population);
    }

    private static void checkTooManyPartnershipIterations(final IPersonCollection population) {

        assertThrows(NoSuchElementException.class, () ->
            doTooManyIterations(population.getPartnerships().iterator(), population.getNumberOfPartnerships()));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void peopleCanBeFoundById(final IPersonCollection population) {

        checkPeopleCanBeFoundById(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void peopleCanBeFoundByIdSlow(final IPersonCollection population) {

        checkPeopleCanBeFoundById(population);
    }

    private static void checkPeopleCanBeFoundById(final IPersonCollection population) {

        for (final IPerson person : population.getPeople()) {

            final IPerson retrievedPerson = population.findPerson(person.getId());
            assertEquals(person, retrievedPerson);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void partnershipsCanBeFoundById(final IPersonCollection population) {

        checkPartnershipsCanBeFoundById(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void partnershipsCanBeFoundByIdSlow(final IPersonCollection population) {

        checkPartnershipsCanBeFoundById(population);
    }

    private static void checkPartnershipsCanBeFoundById(final IPersonCollection population) {

        for (final IPartnership partnership : population.getPartnerships()) {

            final IPartnership retrievedPartnership = population.findPartnership(partnership.getId());
            assertEquals(partnership, retrievedPartnership);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void partnershipsConsistent(final IPersonCollection population) {

        checkPartnershipsConsistent(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void partnershipsConsistentSlow(final IPersonCollection population) {

        checkPartnershipsConsistent(population);
    }

    private static void checkPartnershipsConsistent(final IPersonCollection population) {

        final List<IPartnership> partnerships = new ArrayList<>();
        final List<IPerson> people = new ArrayList<>();

        for (final IPartnership partnership : population.getPartnerships())
            partnerships.add(partnership);

        for (final IPerson person : population.getPeople())
            people.add(person);

        for (final IPartnership partnership : partnerships) {
            assertTrue(people.contains(partnership.getMalePartner()));
            assertTrue(people.contains(partnership.getFemalePartner()));
            assertTrue(partnership.getMalePartner().getPartnerships().contains(partnership));
            assertTrue(partnership.getFemalePartner().getPartnerships().contains(partnership));
        }

        for (final IPerson person : people)
            for (final IPartnership partnership : person.getPartnerships()) {
                assertTrue(partnerships.contains(partnership));
                assertTrue(partnership.getMalePartner().equals(person) || partnership.getFemalePartner().equals(person));
            }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void familiesNotTooLarge(final IPersonCollection population) {

        checkFamiliesNotTooLarge(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void familiesNotTooLargeSlow(final IPersonCollection population) {

        checkFamiliesNotTooLarge(population);
    }

    private static void checkFamiliesNotTooLarge(final IPersonCollection population) {

        for (final IPartnership partnership : population.getPartnerships())
            assertTrue(partnership.getChildren().size() <= MAX_REASONABLE_FAMILY_SIZE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void birthsBeforeDeaths(final IPersonCollection population) {

        checkBirthsBeforeDeaths(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void birthsBeforeDeathsSlow(final IPersonCollection population) {

        checkBirthsBeforeDeaths(population);
    }

    private static void checkBirthsBeforeDeaths(final IPersonCollection population) {

        for (final IPerson person : population.getPeople())
            if (person.getBirthDate() != null && person.getDeathDate() != null) {

                final LocalDate deathDate = person.getDeathDate();
                final LocalDate birthDate = person.getBirthDate();

                assertFalse(birthDate.isAfter(deathDate));
            }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void sensibleAgeAtMarriages(final IPersonCollection population) {

        checkSensibleAgeAtMarriages(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void sensibleAgeAtMarriagesSlow(final IPersonCollection population) {

        checkSensibleAgeAtMarriages(population);
    }

    private static void checkSensibleAgeAtMarriages(final IPersonCollection population) {

        for (final IPerson person : population.getPeople())
            if (person.getBirthDate() != null) {

                final LocalDate birthDate = person.getBirthDate();

                for (final IPartnership partnership : person.getPartnerships())
                    if (partnership.getMarriageDate() != null) {

                        final LocalDate marriageDate = partnership.getMarriageDate();

                        final int ageAtMarriage = differenceInYears(birthDate, marriageDate);
                        assertTrue(ageAtMarriage >= MINIMUM_AGE_AT_MARRIAGE);
                    }
            }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void marriagesBeforeDeaths(final IPersonCollection population) {

        checkMarriagesBeforeDeaths(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void marriagesBeforeDeathsSlow(final IPersonCollection population) {

        checkMarriagesBeforeDeaths(population);
    }

    private static void checkMarriagesBeforeDeaths(final IPersonCollection population) {

        for (final IPerson person : population.getPeople())
            if (person.getDeathDate() != null) {

                final LocalDate deathDate = person.getDeathDate();

                for (final IPartnership partnership : person.getPartnerships())
                    if (partnership.getMarriageDate() != null) {

                        final LocalDate marriageDate = partnership.getMarriageDate();
                        assertFalse(marriageDate.isAfter(deathDate));
                    }
            }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void sexesConsistent(final IPersonCollection population) {

        checkSexesConsistent(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void sexesConsistentSlow(final IPersonCollection population) {

        checkSexesConsistent(population);
    }

    private static void checkSexesConsistent(final IPersonCollection population) {

        for (final IPartnership partnership : population.getPartnerships()) {

            if (partnership.getFemalePartner().getSex() != null)
                assertEquals(SexOption.FEMALE, partnership.getFemalePartner().getSex());
            if (partnership.getMalePartner().getSex() != null)
                assertEquals(SexOption.MALE, partnership.getMalePartner().getSex());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void surnamesInheritedOnMaleLine(final IPersonCollection population) {

        checkSurnamesInheritedOnMaleLine(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void surnamesInheritedOnMaleLineSlow(final IPersonCollection population) {

        checkSurnamesInheritedOnMaleLine(population);
    }

    private static void checkSurnamesInheritedOnMaleLine(final IPersonCollection population) {

        for (final IPerson person : population.getPeople())
            assertSurnameInheritedOnMaleLine(person);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void noSiblingPartners(final IPersonCollection population) {

        checkNoSiblingPartners(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void noSiblingPartnersSlow(final IPersonCollection population) {

        checkNoSiblingPartners(population);
    }

    private static void checkNoSiblingPartners(final IPersonCollection population) {

        for (final IPerson person : population.getPeople()) {

            // Include half-siblings.
            final Set<IPerson> siblings = new HashSet<>();

            for (final IPartnership partnership : person.getPartnerships())
                for (final IPerson child : partnership.getChildren()) {

                    assertNotPartnerOfAny(child, siblings);
                    siblings.add(child);
                }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void noParentPartnerOfChild(final IPersonCollection population) {

        checkNoParentPartnerOfChild(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void noParentPartnerOfChildSlow(final IPersonCollection population) {

        checkNoParentPartnerOfChild(population);
    }

    private static void checkNoParentPartnerOfChild(final IPersonCollection population) {

        for (final IPartnership partnership : population.getPartnerships())
            for (final IPerson child : partnership.getChildren()) {

                assertNotEquals(child, partnership.getFemalePartner());
                assertNotEquals(child, partnership.getMalePartner());
            }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void parentsHaveSensibleAgesAtBirths(final IPersonCollection population) {

        checkParentsHaveSensibleAgesAtBirths(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void parentsHaveSensibleAgesAtBirthsSlow(final IPersonCollection population) {

        checkParentsHaveSensibleAgesAtBirths(population);
    }

    private static void checkParentsHaveSensibleAgesAtBirths(final IPersonCollection population) {

        for (final IPartnership partnership : population.getPartnerships()) {

            final IPerson mother = partnership.getFemalePartner();
            final IPerson father = partnership.getMalePartner();

            for (final IPerson child : partnership.getChildren())
                assertParentsHaveSensibleAgesAtBirth(father, mother, child);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void parentsAndChildrenConsistent(final IPersonCollection population) {

        checkParentsAndChildrenConsistent(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void parentsAndChildrenConsistentSlow(final IPersonCollection population) {

        checkParentsAndChildrenConsistent(population);
    }

    private static void checkParentsAndChildrenConsistent(final IPersonCollection population) {

        for (final IPartnership partnership : population.getPartnerships())
            for (final IPerson child : partnership.getChildren()) {
                assertEquals(child.getParents(), partnership);
                assertPersonIsPresentInPopulation(child, population);
            }

        for (final IPerson person : population.getPeople()) {

            final IPartnership parents = person.getParents();

            if (parents != null) {
                assertTrue(parents.getChildren().contains(person));

                assertPersonIsPresentInPopulation(parents.getMalePartner(), population);
                assertPersonIsPresentInPopulation(parents.getFemalePartner(), population);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static void assertPersonIsPresentInPopulation(final IPerson person, final IPersonCollection population) {

        assertNotNull(population.findPerson(person.getId()));
    }

    private static void assertNotPartnerOfAny(final IPerson person, final Set<IPerson> people) {

        for (final IPerson another_person : people)
            assertFalse(isPartnerOf(person, another_person));
    }

    private static boolean isPartnerOf(final IPerson p1, final IPerson p2) {

        for (final IPartnership partnership : p1.getPartnerships())
            if (partnership.getPartnerOf(p1).equals(p2))
                return true;

        return false;
    }

    private static void assertSurnameInheritedOnMaleLine(final IPerson person) {

        if (person.getSex() == SexOption.MALE) {

            for (final IPartnership partnership : person.getPartnerships())
                for (final IPerson child : partnership.getChildren()) {

                    if (person.getSurname() != null && child.getSurname() != null)
                        assertEquals(person.getSurname(), child.getSurname());

                    if (child.getSex() == SexOption.MALE)
                        assertSurnameInheritedOnMaleLine(child);
                }
        }
    }

    private static void doTooManyIterations(final Iterator<?> iterator, final int number_available) {

        for (int i = 0; i < number_available + 1; i++)
            iterator.next();
    }

    private static void assertParentsHaveSensibleAgesAtBirth(final IPerson father, final IPerson mother, final IPerson child) {

        final LocalDate motherBirthDate = mother.getBirthDate();
        final LocalDate motherDeathDate = mother.getDeathDate();

        final LocalDate fatherBirthDate = father.getBirthDate();
        final LocalDate fatherDeathDate = father.getDeathDate();

        final LocalDate childBirthDate = child.getBirthDate();

        assertTrue(motherDeathDate == null || childBirthDate == null || !childBirthDate.isAfter(motherDeathDate));
        assertTrue(fatherDeathDate == null || childBirthDate == null || !childBirthDate.isAfter(fatherDeathDate.plusDays(MAX_GESTATION_IN_DAYS)));

        assertTrue(motherBirthDate == null || childBirthDate == null || differenceInYears(motherBirthDate, childBirthDate) >= MINIMUM_MOTHER_AGE_AT_CHILDBIRTH);
        assertTrue(motherBirthDate == null || childBirthDate == null || differenceInYears(motherBirthDate, childBirthDate) <= MAXIMUM_MOTHER_AGE_AT_CHILDBIRTH);
        assertTrue(fatherBirthDate == null || childBirthDate == null || differenceInYears(fatherBirthDate, childBirthDate) >= MINIMUM_FATHER_AGE_AT_CHILDBIRTH);
    }

    private static int differenceInYears(final LocalDate parent_birth_date, final LocalDate child_birth_date) {

        return Period.between(parent_birth_date, child_birth_date).getYears();
    }
}
