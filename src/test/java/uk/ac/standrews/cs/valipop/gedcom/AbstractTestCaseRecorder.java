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

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.export.IPopulationWriter;
import uk.ac.standrews.cs.valipop.export.PopulationConverter;
import uk.ac.standrews.cs.valipop.implementations.OBDModel;
import uk.ac.standrews.cs.valipop.simulationEntities.IPopulation;
import uk.ac.standrews.cs.valipop.statistics.distributions.InconsistentWeightException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

public abstract class AbstractTestCaseRecorder {

    protected void recordTestCase() throws Exception {

        for (int i = 0; i < AbstractExporterTest.TEST_CASE_POPULATION_SIZES.length; i++) {

            final Path path = Paths.get(AbstractExporterTest.TEST_DIRECTORY_PATH_STRING, getDirectoryName(), AbstractExporterTest.TEST_CASE_FILE_NAME_ROOTS[i] + getIntendedOutputFileSuffix());

            Config config = new Config(
                    LocalDate.of(1599, 1, 1),
                    LocalDate.of(1855, 1, 1),
                    LocalDate.of(2015, 1, 1),
                    AbstractExporterTest.TEST_CASE_POPULATION_SIZES[i],
                    Paths.get("src/test/resources/valipop/test-pop"));

            config.setRunPurpose("DETERMINISTIC-TESTING").setDeterministic(true);

            OBDModel sim = new OBDModel(config);
            sim.runSimulation();

            final IPopulation abstract_population = sim.getPopulation().getPeople();
            final IPopulationWriter population_writer = getPopulationWriter(path, abstract_population);

            try (PopulationConverter converter = new PopulationConverter(abstract_population, population_writer)) {
                converter.convert();
            }
        }
    }

    protected abstract String getIntendedOutputFileSuffix();

    protected abstract String getDirectoryName();

    protected abstract IPopulationWriter getPopulationWriter(Path path, IPopulation population) throws IOException, InconsistentWeightException;
}
