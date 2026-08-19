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
import uk.ac.standrews.cs.valipop.conversion.GEDCOMImportAdapter;
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
        Arguments.of(TEST_RESOURCE_DIR.resolve("1855-2016-initial-200.config")),
        Arguments.of(TEST_RESOURCE_DIR.resolve("1855-2016-initial-300.config"))
    );

    private static final List<Arguments> slowConfigurations = List.of(
        Arguments.of(TEST_RESOURCE_DIR.resolve("1855-2016-initial-1000.config")),
        Arguments.of(TEST_RESOURCE_DIR.resolve("1855-2016-initial-10000.config"))
    );

    public static IPersonCollection makePopulation(final Path path) {

        try {
            if (path == null)
                return createDummyPopulation();

            if (path.getFileName().toString().endsWith(".ged")) {

                final GEDCOMImportAdapter population = new GEDCOMImportAdapter(path);
                population.setDescription("gedcom file=" + path);

                return population;
            }

            final Config config = new Config(path);

            final OBDModel model = new OBDModel(config);
            model.runSimulation();

            final IPersonCollection population = model.getPopulation().getPeople();
            population.setDescription("initial size=" + config.getTargetInitialPopulationSize() + ", seed=" + config.getSeed());

            return population;
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final Map<Path, IPersonCollection> populationCache = new HashMap<>();

    public static IPersonCollection getPopulation(final Path configPath) {

        if (!populationCache.containsKey(configPath))
            populationCache.put(configPath, makePopulation(configPath));

        return populationCache.get(configPath);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void nonExistentPersonIsntFound(final Path configPath) {

        checkNonExistentPersonIsntFound(getPopulation(configPath));
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void nonExistentPersonIsntFoundSlow(final Path configPath) {

        checkNonExistentPersonIsntFound(getPopulation(configPath));
    }

    private static void checkNonExistentPersonIsntFound(final IPersonCollection population) {

        assertNull(population.findPerson(-1));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void nonExistentPartnershipIsntFound(final Path configPath) {

        checkNonExistentPartnershipIsntFound(getPopulation(configPath));
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void nonExistentPartnershipIsntFoundSlow(final Path configPath) {

        checkNonExistentPartnershipIsntFound(getPopulation(configPath));
    }

    private static void checkNonExistentPartnershipIsntFound(final IPersonCollection population) {

        assertNull(population.findPartnership(-1));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void numberOfPeopleIsConsistent(final Path configPath) {

        checkNumberOfPeopleIsConsistent(getPopulation(configPath));
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void numberOfPeopleIsConsistentSlow(final Path configPath) {

        checkNumberOfPeopleIsConsistent(getPopulation(configPath));
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
    public void personIDsArentRepeated(final Path configPath) {

        checkPersonIDsArentRepeated(getPopulation(configPath));
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void personIDsArentRepeatedSlow(final Path configPath) {

        checkPersonIDsArentRepeated(getPopulation(configPath));
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
    public void numberOfPartnershipsIsConsistent(final Path configPath) {

        checkNumberOfPartnershipsIsConsistent(getPopulation(configPath));
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void numberOfPartnershipsIsConsistentSlow(final Path configPath) {

        checkNumberOfPartnershipsIsConsistent(getPopulation(configPath));
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
    public void partnershipIDsArentRepeated(final Path configPath) {

        checkPartnershipIDsArentRepeated(getPopulation(configPath));
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void partnershipIDsArentRepeatedSlow(final Path configPath) {

        checkPartnershipIDsArentRepeated(getPopulation(configPath));
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
    public void tooManyPersonIterations(final Path configPath) {

        checkTooManyPersonIterations(getPopulation(configPath));
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void tooManyPersonIterationsSlow(final Path configPath) {

        checkTooManyPersonIterations(getPopulation(configPath));
    }

    private static void checkTooManyPersonIterations(final IPersonCollection population) {

        assertThrows(NoSuchElementException.class, () ->
            doTooManyIterations(population.getPeople().iterator(), population.getNumberOfPeople()));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void tooManyPartnershipIterations(final Path configPath) {

        checkTooManyPartnershipIterations(getPopulation(configPath));
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void tooManyPartnershipIterationsSlow(final Path configPath) {

        checkTooManyPartnershipIterations(getPopulation(configPath));
    }

    private static void checkTooManyPartnershipIterations(final IPersonCollection population) {

        assertThrows(NoSuchElementException.class, () ->
            doTooManyIterations(population.getPartnerships().iterator(), population.getNumberOfPartnerships()));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void peopleCanBeFoundById(final Path configPath) {

        checkPeopleCanBeFoundById(getPopulation(configPath));
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void peopleCanBeFoundByIdSlow(final Path configPath) {

        checkPeopleCanBeFoundById(getPopulation(configPath));
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
    public void partnershipsCanBeFoundById(final Path configPath) {

        checkPartnershipsCanBeFoundById(getPopulation(configPath));
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void partnershipsCanBeFoundByIdSlow(final Path configPath) {

        checkPartnershipsCanBeFoundById(getPopulation(configPath));
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
    public void partnershipsConsistent(final Path configPath) {

        checkPartnershipsConsistent(getPopulation(configPath));
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void partnershipsConsistentSlow(final Path configPath) {

        checkPartnershipsConsistent(getPopulation(configPath));
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
    public void familiesNotTooLarge(final Path configPath) {

        checkFamiliesNotTooLarge(getPopulation(configPath));
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void familiesNotTooLargeSlow(final Path configPath) {

        checkFamiliesNotTooLarge(getPopulation(configPath));
    }

    private static void checkFamiliesNotTooLarge(final IPersonCollection population) {

        for (final IPartnership partnership : population.getPartnerships())
            assertTrue(partnership.getChildren().size() <= MAX_REASONABLE_FAMILY_SIZE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void birthsBeforeDeaths(final Path configPath) {

        checkBirthsBeforeDeaths(getPopulation(configPath));
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void birthsBeforeDeathsSlow(final Path configPath) {

        checkBirthsBeforeDeaths(getPopulation(configPath));
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
    public void sensibleAgeAtMarriages(final Path configPath) {

        checkSensibleAgeAtMarriages(getPopulation(configPath));
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void sensibleAgeAtMarriagesSlow(final Path configPath) {

        checkSensibleAgeAtMarriages(getPopulation(configPath));
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
    public void marriagesBeforeDeaths(final Path configPath) {

        checkMarriagesBeforeDeaths(getPopulation(configPath));
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void marriagesBeforeDeathsSlow(final Path configPath) {

        checkMarriagesBeforeDeaths(getPopulation(configPath));
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
    public void sexesConsistent(final Path configPath) {

        checkSexesConsistent(getPopulation(configPath));
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void sexesConsistentSlow(final Path configPath) {

        checkSexesConsistent(getPopulation(configPath));
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
    public void surnamesInheritedOnMaleLine(final Path configPath) {

        checkSurnamesInheritedOnMaleLine(getPopulation(configPath));
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void surnamesInheritedOnMaleLineSlow(final Path configPath) {

        checkSurnamesInheritedOnMaleLine(getPopulation(configPath));
    }

    private static void checkSurnamesInheritedOnMaleLine(final IPersonCollection population) {

        for (final IPerson person : population.getPeople())
            assertSurnameInheritedOnMaleLine(person);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void noSiblingPartners(final Path configPath) {

        checkNoSiblingPartners(getPopulation(configPath));
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void noSiblingPartnersSlow(final Path configPath) {

        checkNoSiblingPartners(getPopulation(configPath));
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
    public void noParentPartnerOfChild(final Path configPath) {

        checkNoParentPartnerOfChild(getPopulation(configPath));
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void noParentPartnerOfChildSlow(final Path configPath) {

        checkNoParentPartnerOfChild(getPopulation(configPath));
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
    public void parentsHaveSensibleAgesAtBirths(final Path configPath) {

        checkParentsHaveSensibleAgesAtBirths(getPopulation(configPath));
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void parentsHaveSensibleAgesAtBirthsSlow(final Path configPath) {

        checkParentsHaveSensibleAgesAtBirths(getPopulation(configPath));
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
    public void parentsAndChildrenConsistent(final Path configPath) {

        checkParentsAndChildrenConsistent(getPopulation(configPath));
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void parentsAndChildrenConsistentSlow(final Path configPath) {

        checkParentsAndChildrenConsistent(getPopulation(configPath));
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

    private static IPersonCollection createDummyPopulation() {

        return new IPersonCollection() {

            @Override
            public Iterable<IPerson> getPeople() {
                return List.of();
            }

            @Override
            public Iterable<IPartnership> getPartnerships() {
                return List.of();
            }

            @Override
            public IPerson findPerson(final int id) {
                return null;
            }

            @Override
            public IPartnership findPartnership(final int id) {
                return null;
            }

            @Override
            public int getNumberOfPeople() {
                return 0;
            }

            @Override
            public int getNumberOfPartnerships() {
                return 0;
            }

            @Override
            public LocalDate getStartDate() {
                return null;
            }

            @Override
            public LocalDate getEndDate() {
                return null;
            }

            @Override
            public void setDescription(final String description) {
            }

            @Override
            public Config getConfig() {
                return null;
            }

            @Override
            public String toString() {
                return "dummy gedcom file";
            }
        };
    }
}
