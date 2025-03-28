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
package uk.ac.standrews.cs.valipop.export;

import java.io.IOException;
import java.nio.file.Path;

import uk.ac.standrews.cs.valipop.export.geojson.GeojsonPopulationWriter;
import uk.ac.standrews.cs.valipop.simulationEntities.IPersonCollection;

/**
 * Generates test cases for geojson export.
 *
 * @author Daniel Brathagen (dbrathagen@gmail.com)
 */
public class GeojsonTestCaseRecorder extends AbstractTestCaseRecorder {

    // The generated geojson files can be checked for validity at: https://geojson.tools/

    public static void main(final String[] args) throws Exception {

        new GeojsonTestCaseRecorder().recordTestCase();
    }

    @Override
    protected IPopulationWriter getPopulationWriter(final Path path, final IPersonCollection population) throws IOException {

        return new GeojsonPopulationWriter(path);
    }

    @Override
    protected String getIntendedOutputFileSuffix() {

        return ".geojson";
    }

    @Override
    protected String getDirectoryName() {
        return "geojson";
    }
}
