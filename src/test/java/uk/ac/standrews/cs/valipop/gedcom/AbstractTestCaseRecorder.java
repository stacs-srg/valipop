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

import uk.ac.standrews.cs.basic_model.distributions.general.InconsistentWeightException;
import uk.ac.standrews.cs.basic_model.model.IDFactory;
import uk.ac.standrews.cs.basic_model.model.IPopulation;
import uk.ac.standrews.cs.basic_model.model.IPopulationWriter;
import uk.ac.standrews.cs.basic_model.model.PopulationConverter;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.implementations.OBDModel;

import uk.ac.standrews.cs.valipop.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordFormat;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Tests of Graphviz export.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public abstract class AbstractTestCaseRecorder {

    protected void recordTestCase() throws Exception {

        for (int i = 0; i < AbstractExporterTest.TEST_CASE_POPULATION_SIZES.length; i++) {

            IDFactory.resetId();

            final Path path = Paths.get(
                    AbstractExporterTest.TEST_DIRECTORY_PATH_STRING,
                    getDirectoryName(), AbstractExporterTest.TEST_CASE_FILE_NAME_ROOTS[i] + getIntendedOutputFileSuffix());

            String startTime = FileUtils.getDateTime();
            Config config = new Config(new MonthDate(1, 1599), new MonthDate(1, 1855),
                    new MonthDate(1, 2015), AbstractExporterTest.TEST_CASE_POPULATION_SIZES[i], 0.0133,
                    0.0122, new CompoundTimeUnit(1, TimeUnit.YEAR), "src/test/resources/valipop/test-pop",
                    "results", "DETERMINISTIC-TESTING", 147,
                    147, true, 0.0, 0.0, 1.0, 1.0,
                    new CompoundTimeUnit(1, TimeUnit.YEAR), RecordFormat.NONE, startTime, true);
            OBDModel sim = new OBDModel(startTime, config);
            sim.runSimulation();

            final IPopulation abstract_population = sim.getPopulation().getAllPeople();
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
