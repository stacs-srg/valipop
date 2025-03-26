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
package uk.ac.standrews.cs.valipop.gedcom;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.standrews.cs.valipop.export.IPopulationWriter;
import uk.ac.standrews.cs.valipop.export.PopulationConverter;
import uk.ac.standrews.cs.valipop.export.gedcom.GEDCOMPopulationAdapter;
import uk.ac.standrews.cs.valipop.export.gedcom.GEDCOMPopulationWriter;
import uk.ac.standrews.cs.valipop.simulationEntities.IPersonCollection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * E2£ tests of GEDCOM export.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class PopulationToGEDCOMTest extends AbstractExporterTest {

    static final String INTENDED_SUFFIX = ".ged";
    private static final int POPULATION_SIZE_LIMIT_FOR_EXPENSIVE_TESTS = 1000;

    @Before
    public void setup() throws IOException {

        actual_output = Files.createTempFile(null, INTENDED_SUFFIX);
        intended_output = Paths.get(TEST_DIRECTORY_PATH_STRING, "gedcom", file_name_root + INTENDED_SUFFIX);
    }

    public PopulationToGEDCOMTest(final IPersonCollection population, final String file_name) {

        super(population, file_name);
    }

    @Test
    public void GEDCOMExportIsAsExpected() throws Exception {

        final IPopulationWriter population_writer = new GEDCOMPopulationWriter(actual_output);

        try (PopulationConverter converter = new PopulationConverter(population, population_writer)) {
            converter.convert();
        }

        assertThatFilesHaveSameContent(actual_output, intended_output);
    }

    // Error (IO extended characters not supported in ASCII)
    // Probably need to remove special characters are change gedcom encoding
    @Ignore
    @Test
    public void reImportGivesSamePopulation() throws Exception {

        if (testingSmallPopulation()) {

            Path path1 = Files.createTempFile(null, ".ged");
            Path path2 = Files.createTempFile(null, ".ged");

            final IPopulationWriter population_writer1 = new GEDCOMPopulationWriter(path1);
            final IPopulationWriter population_writer2 = new GEDCOMPopulationWriter(path2);

            try (PopulationConverter converter = new PopulationConverter(population, population_writer1)) {
                converter.convert();
            }

            try {
                IPersonCollection imported = new GEDCOMPopulationAdapter(path1);
                try (PopulationConverter converter = new PopulationConverter(imported, population_writer2)) {
                    converter.convert();
                }
            } catch (IOException e) {
                System.err.println("Error reading path: " + path1.toString());
                throw e;
            }

            assertThatFilesHaveSameContent(path1, path2);
        }
    }

    private boolean testingSmallPopulation() {

        return Integer.parseInt(population.toString()) <= POPULATION_SIZE_LIMIT_FOR_EXPENSIVE_TESTS;
    }
}
