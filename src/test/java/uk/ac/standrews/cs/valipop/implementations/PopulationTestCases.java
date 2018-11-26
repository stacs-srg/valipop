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
import uk.ac.standrews.cs.valipop.simulationEntities.population.IPopulation;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.valipop.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordFormat;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvanceableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;

import java.util.ArrayList;
import java.util.List;

class PopulationTestCases {

    static List<Object[]> getTestCases() throws Exception {

        List<Object[]> testCases = new ArrayList<>();

        testCases.add(new Object[]{fullPopulation(10000, 56854687), 10000});
        testCases.add(new Object[]{fullPopulation(20000, 56854687), 20000});
        testCases.add(new Object[]{fullPopulation(30000, 56854687), 30000});
        testCases.add(new Object[]{fullPopulation(40000, 56854687), 40000});
        testCases.add(new Object[]{fullPopulation(50000, 56854687), 50000});
        testCases.add(new Object[]{fullPopulation(10000, 56854688), 10000});
        testCases.add(new Object[]{fullPopulation(20000, 56854688), 20000});
        testCases.add(new Object[]{fullPopulation(30000, 56854688), 30000});
        testCases.add(new Object[]{fullPopulation(40000, 56854688), 40000});
        testCases.add(new Object[]{fullPopulation(50000, 56854688), 50000});
        testCases.add(new Object[]{fullPopulation(50000, 23425234), 50000});

        return testCases;
    }

    private static IPopulation fullPopulation(final int t0PopulationSize, int seed) throws Exception {

        AdvanceableDate tS = new MonthDate("1/1/1599");
        AdvanceableDate t0 = new MonthDate(" 1/1/1855");
        AdvanceableDate tE = new MonthDate("1/1/2016");
        double setUpBR = 0.0133;
        double setUpDR = 0.0122;
        CompoundTimeUnit simulationTimeStep = new CompoundTimeUnit("1y");
        String varPath = "src/test/resources/valipop/test-pop";
        String resultsSavePath = "results";
        String runPurpose = "general-structure-testing";
        int minBirthSpacing = 147;
        int minGestationPeriodDays = 0;
        boolean binomialSampling = true;
        double birthFactor = 0.0;
        double deathFactor = 0.0;
        double recoveryFactor = 1.0;
        double proportionalRecoveryFactor = 0;
        CompoundTimeUnit inputWidth = new CompoundTimeUnit("10y");
        RecordFormat outputRecordFormat = RecordFormat.NONE;
        String startTime = FileUtils.getDateTime();
        boolean deterministic = true;

        OBDModel.setUpFileStructureAndLogs(runPurpose, startTime, resultsSavePath);

        Config config = new Config(tS, t0, tE, t0PopulationSize, setUpBR, setUpDR, simulationTimeStep, varPath, resultsSavePath, runPurpose,
                minBirthSpacing, minGestationPeriodDays, binomialSampling, birthFactor, deathFactor, recoveryFactor, proportionalRecoveryFactor,
                inputWidth, outputRecordFormat, startTime, seed, deterministic);

        OBDModel model = new OBDModel(startTime, config);
        model.runSimulation();

        PeopleCollection population = model.getPopulation().getAllPeople();
        population.setDescription("initial size=" + t0PopulationSize + ", seed=" + seed);
        return population;
    }
}
