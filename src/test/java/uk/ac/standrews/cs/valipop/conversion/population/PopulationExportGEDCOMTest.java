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
package uk.ac.standrews.cs.valipop.conversion.population;

import gedinline.main.GedInlineValidator;
import org.gedcom4j.exception.GedcomParserException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.conversion.IPopulationWriter;
import uk.ac.standrews.cs.valipop.conversion.PopulationConverter;
import uk.ac.standrews.cs.valipop.conversion.GEDCOMImportAdapter;
import uk.ac.standrews.cs.valipop.conversion.GEDCOMExportAdapter;
import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.IPersonCollection;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ac.standrews.cs.valipop.Config.POPULATION_EXPORT_DIR_NAME;
import static uk.ac.standrews.cs.valipop.population.PopulationPropertiesTest.makePopulation;

/**
 * These tests check that when various populations are generated, and exported in GEDCOM format, then the files are valid and contain the expected content.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class PopulationExportGEDCOMTest extends PopulationExportTest {

    private static final List<Arguments> configurations = List.of(
        makePopulation(TEST_RESOURCE_DIR.resolve("1855-2016-initial-200-gedcom.config"))
//        makePopulation(TEST_RESOURCE_DIR.resolve("1855-2016-initial-300-gedcom.config"))
    );

    private static final List<Arguments> slowConfigurations = List.of(
        makePopulation(TEST_RESOURCE_DIR.resolve("1855-2016-initial-1K-gedcom.config"))
    );

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @FieldSource("configurations")
    public void GEDCOMIsValid(final IPersonCollection population) throws IOException {

        checkGEDCOMIsValid(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void GEDCOMIsValidSlow(final IPersonCollection population) throws IOException {

        checkGEDCOMIsValid(population);
    }

    @ParameterizedTest
    @FieldSource("configurations")
    public void exportImportGivesEquivalentPopulation(final IPersonCollection population) throws IOException, GedcomParserException {

        checkExportImportGivesEquivalentPopulation(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void exportImportGivesEquivalentPopulationSlow(final IPersonCollection population) throws IOException, GedcomParserException {

        checkExportImportGivesEquivalentPopulation(population);
    }

    @ParameterizedTest
    @FieldSource("configurations")
    public void exportImportExportGivesSamePopulationFile(final IPersonCollection population) throws Exception {

        checkExportImportExportGivesSamePopulationFile(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void exportImportExportGivesSamePopulationFileSlow(final IPersonCollection population) throws Exception {

        checkExportImportExportGivesSamePopulationFile(population);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static void checkGEDCOMIsValid(final IPersonCollection population) throws IOException {

        final Config config = population.getConfig();

        final Path populationExportDirPath = config.getRunPath().resolve(POPULATION_EXPORT_DIR_NAME);
        final Path exportedFilePath = populationExportDirPath.resolve("population.ged");
        final Path validation_output_file = Files.createFile(populationExportDirPath.resolve("validation_output.txt"));

        final GedInlineValidator validator = new GedInlineValidator(new File(exportedFilePath.toString()), new PrintWriter(validation_output_file.toString()));
        assertTrue(validator.validate());
        assertEquals(0, validator.getNumberOfWarnings(), "GEDCOM validation warnings count");
    }

    private static void checkExportImportGivesEquivalentPopulation(final IPersonCollection population) throws IOException, GedcomParserException {

        final Config config = population.getConfig();

        final Path populationExportDirPath = config.getRunPath().resolve(POPULATION_EXPORT_DIR_NAME);
        final Path exportedFilePath = populationExportDirPath.resolve("population.ged");

        final IPersonCollection imported = new GEDCOMImportAdapter(exportedFilePath);
        assertEqualPopulations(population, imported);
    }

    private static void checkExportImportExportGivesSamePopulationFile(final IPersonCollection population) throws Exception {

        final Config config = population.getConfig();

        final Path populationExportDirPath = config.getRunPath().resolve(POPULATION_EXPORT_DIR_NAME);
        final Path exportedFilePath = populationExportDirPath.resolve("population.ged");
        final Path reExportedFilePath = populationExportDirPath.resolve("population2.ged");

        final IPersonCollection imported = new GEDCOMImportAdapter(exportedFilePath);

        final IPopulationWriter population_writer2 = new GEDCOMExportAdapter(reExportedFilePath);

        try (final PopulationConverter converter = new PopulationConverter(imported, population_writer2)) {
            converter.convert();
        }

        assertThatFilesHaveSameContent(exportedFilePath, reExportedFilePath);
    }

    private static void assertEqualPopulations(final IPersonCollection population1, final IPersonCollection population2) {

        final int numberOfPeople1 = population1.getNumberOfPeople();
        final int numberOfPeople2 = population2.getNumberOfPeople();
        assertEquals(numberOfPeople1, numberOfPeople2);

        final List<IPerson> population1_people = new ArrayList<>();
        population1.getPeople().forEach(population1_people::add);

        final List<IPerson> population2_people = new ArrayList<>();
        population2.getPeople().forEach(population2_people::add);

        final int size1 = population1_people.size();
        final int size2 = population2_people.size();
        assertEquals(size1, size2);

        population1_people.sort(Comparable::compareTo);
        population2_people.sort(Comparable::compareTo);

        for (int i = 0; i < population1_people.size(); i++)
            assertEqualPeople(population1_people.get(i), population2_people.get(i));
    }

    private static void assertEqualPeople(final IPerson person1, final IPerson person2) {

        assertEqualPersonalDetails(person1, person2);

        assertEqualPartnerships(person1.getParents(), person2.getParents());

        final List<IPartnership> person1_partnerships = new ArrayList<>(person1.getPartnerships());
        final List<IPartnership> person2_partnerships = new ArrayList<>(person2.getPartnerships());

        person1_partnerships.sort(Comparable::compareTo);
        person2_partnerships.sort(Comparable::compareTo);

        for (int i = 0; i < person1_partnerships.size(); i++)
            assertEqualPartnerships(person1_partnerships.get(i), person2_partnerships.get(i));
    }

    private static void assertEqualPersonalDetails(final IPerson person1, final IPerson person2) {

        assertEquals(person1.getId(), person2.getId());
        assertEquals(person1.getFirstName(), person2.getFirstName());
        assertEquals(person1.getSurname(), person2.getSurname());
        assertEquals(person1.getSex(), person2.getSex());
        assertEquals(person1.getBirthDate(), person2.getBirthDate());
        assertEquals(person1.getBirthPlace(), person2.getBirthPlace());
        assertEquals(person1.getDeathDate(), person2.getDeathDate());
        assertEquals(person1.getDeathPlace(), person2.getDeathPlace());
        assertEquals(person1.getDeathCause(), person2.getDeathCause());
        assertEquals(person1.getLastOccupation(), person2.getLastOccupation());
    }

    private static void assertEqualPartnerships(final IPartnership partnership1, final IPartnership partnership2) {

        if (partnership1 != null || partnership2 != null) {

            if (partnership1 == null || partnership2 == null) fail();

            assertEquals(partnership1.getId(), partnership2.getId());
            assertEquals(partnership1.getMarriageDate(), partnership2.getMarriageDate());
            assertEquals(partnership1.getMarriagePlace(), partnership2.getMarriagePlace());

            assertEqualPersonalDetails(partnership1.getFemalePartner(), partnership2.getFemalePartner());
            assertEqualPersonalDetails(partnership1.getMalePartner(), partnership2.getMalePartner());

            final List<IPerson> partnership1_children = new ArrayList<>(partnership1.getChildren());
            final List<IPerson> partnership2_children = new ArrayList<>(partnership1.getChildren());

            partnership1_children.sort(Comparable::compareTo);
            partnership2_children.sort(Comparable::compareTo);

            for (int i = 0; i < partnership1_children.size(); i++)
                assertEqualPersonalDetails(partnership1_children.get(i), partnership2_children.get(i));
        }
    }
}
