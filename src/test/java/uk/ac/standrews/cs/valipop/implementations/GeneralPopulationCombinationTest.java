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
package uk.ac.standrews.cs.valipop.implementations;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import uk.ac.standrews.cs.valipop.simulationEntities.IPersonCollection;

import java.util.Collection;

@RunWith(Parameterized.class)
public class GeneralPopulationCombinationTest extends GeneralPopulationStructureTest {

    // The name string gives informative labels in the JUnit output.
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> generateData() throws Exception {

        return PopulationTestCases.getTestCases();
    }

    public GeneralPopulationCombinationTest(IPersonCollection population, int initialSize) {

        super(population, initialSize);
    }
}
