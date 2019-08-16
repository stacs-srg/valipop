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

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.simulationEntities.IPopulation;
import uk.ac.standrews.cs.valipop.simulationEntities.dataStructure.PeopleCollection;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class PopulationTestCases {

    static List<Object[]> getTestCases()  {

        List<Object[]> testCases = new ArrayList<>();

        testCases.add(new Object[]{fullPopulation(1000, 56854687), 1000});
        testCases.add(new Object[]{fullPopulation(10000, 56854687), 10000});
        testCases.add(new Object[]{fullPopulation(20000, 56854687), 20000});
        testCases.add(new Object[]{fullPopulation(30000, 56854687), 30000});
        testCases.add(new Object[]{fullPopulation(40000, 56854687), 40000});
        testCases.add(new Object[]{fullPopulation(50000, 56854687), 50000});
        testCases.add(new Object[]{fullPopulation(10000, 56854688), 10000});
        testCases.add(new Object[]{fullPopulation(20000, 56854688), 20000});
        testCases.add(new Object[]{fullPopulation(30000, 56854688), 30000});

        return testCases;
    }

    private static IPopulation fullPopulation(final int t0PopulationSize, final int seed)  {

        LocalDate tS = LocalDate.of(1599, 1, 1);
        LocalDate t0 = LocalDate.of(1855, 1, 1);
        LocalDate tE = LocalDate.of(2016, 1, 1);

        Path varPath = Paths.get("src/test/resources/valipop/test-pop");
        String runPurpose = "general-structure-testing";

        Config config = new Config(tS, t0, tE, t0PopulationSize, varPath, Config.DEFAULT_RESULTS_SAVE_PATH, runPurpose,
                Config.DEFAULT_RESULTS_SAVE_PATH).setDeterministic(true).setSeed(seed);

        OBDModel model = new OBDModel(config);
        model.runSimulation();

        PeopleCollection population = model.getPopulation().getPeople();
        population.setDescription("initial size=" + t0PopulationSize + ", seed=" + seed);
        return population;
    }
}
