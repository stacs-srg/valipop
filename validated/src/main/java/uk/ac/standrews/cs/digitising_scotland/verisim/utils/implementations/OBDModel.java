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
package uk.ac.standrews.cs.digitising_scotland.verisim.utils.implementations;

import uk.ac.standrews.cs.digitising_scotland.verisim.config.Config;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.EventLogic;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.birth.NBirthLogic;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.death.NDeathLogic;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.init.InitLogic;
import org.apache.logging.log4j.Logger;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.inputted.DesiredPopulationStatisticsFactory;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.analytic.AnalyticsRunner;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.summaryData.SummaryRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.exceptions.PersonNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.CustomLog4j;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.ProcessArgs;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.ProgramTimer;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.TableInstances.*;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.TreeStructure.CTtree;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.TableStructure.NoTableRowsException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.fileUtils.InvalidInputFileException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.sourceEventRecords.RecordGenerator;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.sourceEventRecords.SourceRecordGenerator;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.sourceEventRecords.processingVisuliserFormat.RelationshipsTable;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.sourceEventRecords.processingVisuliserFormat.SimplifiedSourceRecordGenerator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OBDModel {

    public static final String CODE_VERSION = "dev-bf";

    public static Logger log;

    private Config config;
    private SummaryRow summary;

    private PopulationStatistics desired;

    private Population population;

    private AdvancableDate currentTime;

    private EventLogic deathLogic = new NDeathLogic();
    private EventLogic birthLogic = new NBirthLogic();


    public static void setUpFileStructureAndLogs(String runPurpose, String startTime, String resultsPath) throws IOException {
        // This has to be ran prior to creating the Config file as the file structure and log file need to be created
        // prior to the first logging event that occurs when generating the config
        // And errors in this method are sent to standard error

        FileUtils.makeDirectoryStructure(runPurpose, startTime, resultsPath);
        log = CustomLog4j.setup(FileUtils.pathToLogDir(runPurpose, startTime, resultsPath), new OBDModel());
    }

    public OBDModel() {}

    public OBDModel(String startTime, Config config) throws IOException, InvalidInputFileException {

        this.config = config;

        // Set up simulation parameters
        currentTime = config.getTS();

        population = new Population(config);

        // get desired population info
        desired = DesiredPopulationStatisticsFactory.initialisePopulationStatistics(config);

        InitLogic.setUpInitParameters(config, desired);

        summary = new SummaryRow(Paths.get(config.getResultsSavePath().toString(), config.getRunPurpose(), startTime),
                config.getVarPath(), startTime, config.getRunPurpose(), CODE_VERSION, config.getSimulationTimeStep(), config.getInputWidth(),
                config.getT0(), config.getTE(), DateUtils.differenceInDays(config.getT0(), config.getTE()),
                config.getBirthFactor(), config.getDeathFactor(), config.getRecoveryFactor(),
                config.getMaxProportionOBirthsDueToInfidelity(), config.getMinBirthSpacing(), config.getOutputRecordFormat());

    }

    public void runSimulation() {

        ProgramTimer simTimer = new ProgramTimer();

        boolean runPassed = false;

        while (!runPassed) {

            try {
                runPassed = runSimulationTimeLoop();

            } catch (InsufficientNumberOfPeopleException e) {
                System.err.println("Simulation run incomplete due to insufficient number of people in population to " +
                        "perform requested events");
                System.err.println(e.getMessage());

                summary.setSimRunTime(simTimer.getRunTimeSeconds());
                summary.setCompleted(false);

                summary.outputSummaryRowToFile();
                NDeathLogic.tKilled = 0;
                NBirthLogic.tBirths = 0;

                simTimer = new ProgramTimer();

            } catch (PersonNotFoundException e) {
                throw new Error("Expected person not found in simulation - fatal error", e);
            }

        }

        MemoryUsageAnalysis.log();

        summary.setTotalPop(population.getAllPeople().getNumberOfPeople());
        summary.setSimRunTime(simTimer.getRunTimeSeconds());

    }

    private boolean runSimulationTimeLoop() throws InsufficientNumberOfPeopleException, PersonNotFoundException {

        while(DateUtils.dateBeforeOrEqual(currentTime, config.getTE())) {

            MemoryUsageAnalysis.log();

            String yearLine = "";

            if(DateUtils.dateBeforeOrEqual(currentTime, config.getT0())) {
                summary.setStartPop(population.getLivingPeople().getNumberOfPeople());
            }

            yearLine += currentTime.toString() + "\t";

            int bornAtTS = birthLogic.handleEvent(config, currentTime, config.getSimulationTimeStep(), population, desired);
            yearLine += bornAtTS + "\t";

            if (InitLogic.inInitPeriod(currentTime) &&
                    DateUtils.matchesInterval(currentTime, InitLogic.getTimeStep(), config.getTS())) {

                int initAtTS = InitLogic.handleInitPeople(config, currentTime, population);
                yearLine += initAtTS + "\t";

            } else if(!InitLogic.inInitPeriod(currentTime)) {
                yearLine += 0 + "\t";
            }

            int killedAtTS = deathLogic.handleEvent(config, currentTime, config.getSimulationTimeStep(), population, desired);
            yearLine += killedAtTS + "\t";

            if(inSimDates()) {
                population.getPopulationCounts().updateMaxPopulation(population.getLivingPeople().getNumberOfPeople());
            }

            currentTime = currentTime.advanceTime(config.getSimulationTimeStep());

            yearLine += population.getLivingPeople().getNumberOfPeople() + "\t" + population.getDeadPeople().getNumberOfPeople();
            log.info(yearLine);

        }


        log.info("TKilled\t" + NDeathLogic.tKilled);
        log.info("TBorn\t" + NBirthLogic.tBirths);
        log.info("Ratio\t" + NDeathLogic.tKilled / (double) NBirthLogic.tBirths);

        summary.setCompleted(true);
        summary.setEndPop(population.getLivingPeople().getNumberOfPeople());
        summary.setPeakPop(population.getPopulationCounts().getPeakPopulationSize());

        return true;
    }

    public void analyseAndOutputPopulation() {

        generateContigencyTables(population.getAllPeople(), this, config);

        ProgramTimer recordTimer = new ProgramTimer();
        RecordGenerator.outputRecords(config.getOutputRecordFormat(), FileUtils.getRecordsDirPath().toString(),
                population.getAllPeople());
        summary.setRecordsRunTime(recordTimer.getRunTimeSeconds());


        try {
            AnalyticsRunner.runAnalytics(population.getAllPeople(), new PrintStream(FileUtils.getDetailedResultsPath().toFile()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        MemoryUsageAnalysis.log();

        summary.setMaxMemoryUsage(MemoryUsageAnalysis.getMaxSimUsage());
        MemoryUsageAnalysis.reset();

        summary.outputSummaryRowToFile();

        log.info("OBDModel --- Output complete");

    }

    private static void generateContigencyTables(PeopleCollection population, OBDModel sim, Config config) {
        ProgramTimer tableTimer = new ProgramTimer();

        CTtree fullTree = new CTtree(population, sim.desired, config.getT0(), config.getTE());

        MemoryUsageAnalysis.log();

//        PrintStream fullOutput;

        PrintStream obOutput;
        PrintStream mbOutput;
        PrintStream partOutput;
        PrintStream sepOutput;
        PrintStream deathOutput;
        try {
            log.info("OBDModel --- Extracting and Outputting CTables to files");
            CTtableOB obTable = new CTtableOB(fullTree, sim.desired);
            Path obPath = FileUtils.mkBlankFile(FileUtils.getContingencyTablesPath(), "ob-CT.csv");
            obOutput = new PrintStream(obPath.toFile());
            obTable.outputToFile(obOutput);

            MemoryUsageAnalysis.log();

            CTtableMB mbTable = new CTtableMB(fullTree, sim.desired);
            Path mbPath = FileUtils.mkBlankFile(FileUtils.getContingencyTablesPath(), "mb-CT.csv");
            mbOutput = new PrintStream(mbPath.toFile());
            mbTable.outputToFile(mbOutput);

            MemoryUsageAnalysis.log();

            CTtablePart partTable = new CTtablePart(fullTree, sim.desired);
            Path partPath = FileUtils.mkBlankFile(FileUtils.getContingencyTablesPath(), "part-CT.csv");
            partOutput = new PrintStream(partPath.toFile());
            partTable.outputToFile(partOutput);

            MemoryUsageAnalysis.log();

            CTtableSep sepTable = new CTtableSep(fullTree, sim.desired);
            Path sepPath = FileUtils.mkBlankFile(FileUtils.getContingencyTablesPath(), "sep-CT.csv");
            sepOutput = new PrintStream(sepPath.toFile());
            sepTable.outputToFile(sepOutput);

            MemoryUsageAnalysis.log();

            CTtableDeath deathTable = new CTtableDeath(fullTree);
            Path deathPath = FileUtils.mkBlankFile(FileUtils.getContingencyTablesPath(), "death-CT.csv");
            deathOutput = new PrintStream(deathPath.toFile());
            deathTable.outputToFile(deathOutput);

            MemoryUsageAnalysis.log();

//                System.out.println("OBDModel --- Outputting Full CTable to file");
//                Path fullPath = FileUtils.mkBlankFile(FileUtils.getContingencyTablesPath(), "full-CT.csv");
//                fullOutput = new PrintStream(fullPath.toFile());
//                new CTtableFull(fullTree, fullOutput);

            MemoryUsageAnalysis.log();

        } catch (IOException e) {
            throw new Error("failed to make CT files");
        } catch (NoTableRowsException e) {
            e.printStackTrace();
        }

        sim.summary.setCTRunTime(tableTimer.getRunTimeSeconds());
    }


    private boolean inSimDates() {
        return DateUtils.dateBeforeOrEqual(config.getT0(), currentTime);
    }

    public Population getPopulation() {
        return population;
    }

}
