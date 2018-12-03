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

import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.implementations.OBDModel;
import uk.ac.standrews.cs.valipop.simulationEntities.IPopulation;
import uk.ac.standrews.cs.valipop.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordFormat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
@RunWith(Parameterized.class)
public abstract class AbstractExporterTest {

    public static final String TEST_DIRECTORY_PATH_STRING = "src/test/resources/valipop/";

    protected final IPopulation population;
    protected final String file_name_root;

    protected Path actual_output = null;
    protected Path intended_output = null;

    @SuppressWarnings("MagicNumber")
    protected static final int[] TEST_CASE_POPULATION_SIZES = new int[]{10000, 50000};
    protected static final String[] TEST_CASE_FILE_NAME_ROOTS = new String[TEST_CASE_POPULATION_SIZES.length];

    static {
        for (int i = 0; i < TEST_CASE_FILE_NAME_ROOTS.length; i++) {
            TEST_CASE_FILE_NAME_ROOTS[i] = makeFileNameRoot(TEST_CASE_POPULATION_SIZES[i]);
        }
    }

    public AbstractExporterTest(final IPopulation population, final String file_name_root) {

        this.population = population;
        this.file_name_root = file_name_root;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> generateConfigurations() throws Exception {

        final Object[][] configurations = new Object[TEST_CASE_POPULATION_SIZES.length][];
        for (int i = 0; i < configurations.length; i++) {
            configurations[i] = makeTestConfiguration(TEST_CASE_POPULATION_SIZES[i], TEST_CASE_FILE_NAME_ROOTS[i]);
        }
        return Arrays.asList(configurations);
    }

    @After
    public void tearDown() throws IOException {

        Files.delete(actual_output);
    }

    private static Object[] makeTestConfiguration(final int population_size, final String file_name_root) throws Exception {

        String startTime = FileUtils.getDateTime();
        String purpose = "DETERMINISTIC-TESTING";
        OBDModel.setUpFileStructureAndLogs(purpose, startTime, "results");
        Config config = new Config(LocalDate.of( 1599,1,1), LocalDate.of(1855,1,1),
                LocalDate.of( 2015,1,1), population_size, 0.0133,
                0.0122, Period.ofYears(1), "src/test/resources/valipop/test-pop",
                "results", purpose, 147,
                147, true, 0.0, 0.0, 1.0, 1.0,
                Period.ofYears(1), RecordFormat.NONE, startTime, 0, true);

        OBDModel sim = new OBDModel(startTime, config);
        sim.runSimulation();

        final IPopulation population = sim.getPopulation().getAllPeople();
        population.setDescription(String.valueOf(population_size));

        return new Object[]{population, file_name_root};
    }

    private static String makeFileNameRoot(final int population_size) {

        return "file" + population_size;
    }
}
