/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
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
package uk.ac.standrews.cs.digitising_scotland.verisim.utils.implementedSimulations.orderedBirthDeathModel;

import uk.ac.standrews.cs.digitising_scotland.verisim.config.Config;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.EventLogic;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.UnsupportedEventType;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.birth.NBirthLogic;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.death.NDeathLogic;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.init.InitLogic;
import org.apache.logging.log4j.Logger;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.inputted.DesiredPopulationStatisticsFactory;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.analytic.AnalyticsRunner;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.comparison.ComparativeAnalysis;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.summaryData.SummaryRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.CustomLog4j;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.ProcessArgs;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.ProgramTimer;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ContingencyTableGenerator;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.fileUtils.InvalidInputFileException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OBDModel {

    public static Logger log;

    private static Config config;
    private SummaryRow summary;

    private PopulationStatistics desired;

    Population population;

    private MonthDate currentTime;

    private EventLogic deathLogic = new NDeathLogic();
    private EventLogic birthLogic = new NBirthLogic();



    public static void main(String[] args) {

        String[] pArgs = ProcessArgs.process(args);
        if(!ProcessArgs.check(pArgs)) {
            System.err.println("Incorrect arguments given");
            System.exit(1);
        }

        ProgramTimer timer = new ProgramTimer();

        OBDModel sim = null;

        try {
            sim = new OBDModel(pArgs[0], pArgs[2], FileUtils.getDateTime(), pArgs[1]);
        } catch(IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Model failed due to Input/Output exception, check that this program has " +
                    "permission to read or write on disk. Also, check supporting input files are present at location " +
                    "specified in config file");
            System.exit(1);
        } catch (InvalidInputFileException e) {
            System.err.println("Model failed due to an invalid formatting/content of input file, see message: ");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        PeopleCollection population = null;
        try {
            population = sim.runSimulation();
        } catch (InsufficientNumberOfPeopleException e) {
            System.err.println("Simulation run incomplete due to insufficient number of people in population to " +
                    "perform requested events");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        PrintStream resultsOutput;
        try {
            resultsOutput = new PrintStream(FileUtils.getDetailedResultsPath().toFile());
        } catch (FileNotFoundException e) {
            throw new Error("Detailed Results File has been moved since it's creation at the beginning of " +
                    "model run", e);
        }

//        ComparativeAnalysis comparisonOfDesiredAndGenerated;
//        try {
//            comparisonOfDesiredAndGenerated = ComparativeAnalysis.performComparison(config, population, sim.desired);
//            sim.summary = comparisonOfDesiredAndGenerated.outputResults(resultsOutput, sim.summary);
//        } catch (UnsupportedEventType | IOException e) {
//            System.err.println("Comparative analysis failed");
//            System.err.println(e.getMessage());
//        }
//
//        AnalyticsRunner.runAnalytics(population, resultsOutput);

        sim.summary.setTotalPop(population.getNumberOfPeople());
        sim.summary.setRunTime(timer.getTimeMMSS());

        try {
            FileUtils.writeSummaryRowToSummaryFiles(sim.summary);
        } catch (IOException e) {
            System.err.println("Summary row could not be printed to summary files. See message: ");
            System.err.println(e.getMessage());
        }

        resultsOutput.close();

        ContingencyTableGenerator table = new ContingencyTableGenerator(population, sim.desired, config.getT0(), config.getTE());
        table.outputTable(config, table.getFullTable());

    }


    public OBDModel(String pathToConfigFile, String runPurpose, String startTime, String resultsPath) throws IOException, InvalidInputFileException {

        FileUtils.makeDirectoryStructure(runPurpose, startTime, resultsPath);
        log = CustomLog4j.setup(FileUtils.pathToLogDir(runPurpose, startTime, resultsPath), this);
        config = new Config(Paths.get(pathToConfigFile), runPurpose, startTime);


        // Set up simulation parameters
        currentTime = config.getTS();

        population = new Population(config);

        // get desired population info
        desired = DesiredPopulationStatisticsFactory.initialisePopulationStatistics(config);

        InitLogic.setUpInitParameters(config, desired);

        summary = new SummaryRow(Paths.get(config.getResultsSavePath().toString(), runPurpose, startTime),
                startTime, runPurpose, config.getSimulationTimeStep(), config.getSimulationTimeStep(), config.getInputWidth(),
                config.getT0(), config.getTE(), DateUtils.differenceInDays(config.getT0(), config.getTE()));

    }

    public PeopleCollection runSimulation() throws InsufficientNumberOfPeopleException {


        boolean first = true;
        while(DateUtils.dateBeforeOrEqual(currentTime, config.getTE())) {

            System.out.print(currentTime.toString() + "\t");

            if (DateUtils.matchesInterval(currentTime, config.getSimulationTimeStep(), config.getTS())) {
                birthLogic.handleEvent(config, currentTime, config.getSimulationTimeStep(), population, desired);
            }

            if (InitLogic.inInitPeriod(currentTime) &&
                    DateUtils.matchesInterval(currentTime, InitLogic.getTimeStep(), config.getTS())) {
                InitLogic.handleInitPeople(config, currentTime, population);
            } else if(!InitLogic.inInitPeriod(currentTime)) {
//                System.out.println("Init over");
//                first = false;
                System.out.print(0 + "\t");
            }

            if (DateUtils.matchesInterval(currentTime, config.getSimulationTimeStep(), config.getTS())) {
                deathLogic.handleEvent(config, currentTime, config.getSimulationTimeStep(), population, desired);
            }

            if(inSimDates()) {
                population.getPopulationCounts().updateMaxPopulation(population.getLivingPeople().getNumberOfPeople());
            }

            currentTime = currentTime.advanceTime(config.getSimulationTimeStep());

            log.info("Time step completed " + currentTime.toString() + "    Population " + population.getLivingPeople().getNumberOfPersons());
//            System.out.println("Time step completed " + currentTime.toString() + "    Population " + population.getLivingPeople().getNumberOfPersons());
            System.out.println(population.getLivingPeople().getNumberOfPeople() + "\t" + population.getDeadPeople().getNumberOfPeople());

        }


        System.out.println("TKilled\t" + NDeathLogic.tKilled);
        System.out.println("TBorn\t" + NBirthLogic.tBirths);
        System.out.println("Ratio\t" + NDeathLogic.tKilled / (double) NBirthLogic.tBirths);

        return population.getAllPeople();
    }

    private boolean inSimDates() {
        return DateUtils.dateBeforeOrEqual(config.getT0(), currentTime);
    }

}
