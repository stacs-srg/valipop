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
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.EventLogic;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.birth.NBirthLogic;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.death.NDeathLogic;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.init.InitLogic;
import org.apache.logging.log4j.Logger;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.inputted.DesiredPopulationStatisticsFactory;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.summaryData.SummaryRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.CustomLog4j;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.ProcessArgs;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.ProgramTimer;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.TableInstances.*;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.CTtree;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.NoTableRowsException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.fileUtils.InvalidInputFileException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.source_event_records.SourceRecordGenerator;

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

    private static Config config;
    private SummaryRow summary;

    private PopulationStatistics desired;

    Population population;

    private MonthDate currentTime;

    private EventLogic deathLogic = new NDeathLogic();
    private EventLogic birthLogic = new NBirthLogic();

    public static double BIRTH_FACTOR = 0;


    public static void main(String[] args) {

        String[] pArgs = ProcessArgs.process(args);
        if(!ProcessArgs.check(pArgs)) {
            System.err.println("Incorrect arguments given");
            System.exit(1);
        }

        double bfStart = Double.parseDouble(pArgs[4]);
        double bfStep = Double.parseDouble(pArgs[5]);
        double bfEnd = Double.parseDouble(pArgs[6]);

        for(double bf = bfStart; bf <= bfEnd; bf += bfStep) {

            BIRTH_FACTOR = bf;

            int validPopCount = 0;
            int failedPopCount = 0;

            while (validPopCount < Integer.parseInt(pArgs[3])) {

                if (failedPopCount > validPopCount * 100) {
                    System.out.println("Too many invalid pops - will now exit");
                    System.exit(1);
                }

                ProgramTimer simTimer = new ProgramTimer();


                PeopleCollection population = null;
                OBDModel sim = null;

                while (population == null) {

                    try {
                        sim = new OBDModel(pArgs[0], pArgs[2], FileUtils.getDateTime(), pArgs[1]);
                    } catch (IOException e) {
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

                    try {
                        population = sim.runSimulation();
                    } catch (InsufficientNumberOfPeopleException e) {
                        System.err.println("Simulation run incomplete due to insufficient number of people in population to " +
                                "perform requested events");
                        System.err.println(e.getMessage());
                        sim.summary.setSimRunTime(simTimer.getRunTimeSeconds());
                        sim.summary.setCompleted(false);
                        sim.summary.setBirthFactor(bf);

                        try {
                            FileUtils.writeSummaryRowToSummaryFiles(sim.summary);
                        } catch (IOException e1) {
                            System.err.println("Summary row could not be printed to summary files. See message: ");
                            System.err.println(e.getMessage());
                        }
                        NDeathLogic.tKilled = 0;
                        NBirthLogic.tBirths = 0;

                        simTimer = new ProgramTimer();
                        failedPopCount++;
                    }

                }

                sim.summary.setTotalPop(population.getNumberOfPeople());
                sim.summary.setSimRunTime(simTimer.getRunTimeSeconds());
                sim.summary.setBirthFactor(bf);

                ProgramTimer tableTimer = new ProgramTimer();

                CTtree fullTree = new CTtree(population, sim.desired, config.getT0(), config.getTE());


                PrintStream fullOutput;

                PrintStream obOutput;
                PrintStream mbOutput;
                PrintStream partOutput;
                PrintStream sepOutput;
                PrintStream deathOutput;
                try {
                    System.out.println("OBDModel --- Extracting and Outputting CTables to files");
                    CTtableOB obTable = new CTtableOB(fullTree, sim.desired);
                    Path obPath = FileUtils.mkBlankFile(FileUtils.getContingencyTablesPath(), "ob-CT.csv");
                    obOutput = new PrintStream(obPath.toFile());
                    obTable.outputToFile(obOutput);

                    CTtableMB mbTable = new CTtableMB(fullTree, sim.desired);
                    Path mbPath = FileUtils.mkBlankFile(FileUtils.getContingencyTablesPath(), "mb-CT.csv");
                    mbOutput = new PrintStream(mbPath.toFile());
                    mbTable.outputToFile(mbOutput);

                    CTtablePart partTable = new CTtablePart(fullTree, sim.desired);
                    Path partPath = FileUtils.mkBlankFile(FileUtils.getContingencyTablesPath(), "part-CT.csv");
                    partOutput = new PrintStream(partPath.toFile());
                    partTable.outputToFile(partOutput);

                    CTtableSep sepTable = new CTtableSep(fullTree, sim.desired);
                    Path sepPath = FileUtils.mkBlankFile(FileUtils.getContingencyTablesPath(), "sep-CT.csv");
                    sepOutput = new PrintStream(sepPath.toFile());
                    sepTable.outputToFile(sepOutput);

                    CTtableDeath deathTable = new CTtableDeath(fullTree);
                    Path deathPath = FileUtils.mkBlankFile(FileUtils.getContingencyTablesPath(), "death-CT.csv");
                    deathOutput = new PrintStream(deathPath.toFile());
                    deathTable.outputToFile(deathOutput);

//                System.out.println("OBDModel --- Outputting Full CTable to file");
//                Path fullPath = FileUtils.mkBlankFile(FileUtils.getContingencyTablesPath(), "full-CT.csv");
//                fullOutput = new PrintStream(fullPath.toFile());
//                new CTtableFull(fullTree, fullOutput);

                } catch (IOException e) {
                    throw new Error("failed to make CT files");
                } catch (NoTableRowsException e) {
                    e.printStackTrace();
                }

                sim.summary.setCTRunTime(tableTimer.getRunTimeSeconds());

                System.out.println("OBDModel --- Outputting BMD records");

                ProgramTimer recordTimer = new ProgramTimer();

                try {
                    new SourceRecordGenerator(population, FileUtils.pathToRecordsDir(config).toString()).generateEventRecords(new String[0]);
                } catch (Exception e) {
                    System.out.println("Record generation failed");
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }

                sim.summary.setRecordsRunTime(recordTimer.getRunTimeSeconds());

                try {
                    FileUtils.writeSummaryRowToSummaryFiles(sim.summary);
                } catch (IOException e) {
                    System.err.println("Summary row could not be printed to summary files. See message: ");
                    System.err.println(e.getMessage());
                }

                System.out.println("OBDModel --- Output complete");

                validPopCount++;
            }
        }
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
                config.getVarPath(), startTime, runPurpose, CODE_VERSION, config.getSimulationTimeStep(), config.getInputWidth(),
                config.getT0(), config.getTE(), DateUtils.differenceInDays(config.getT0(), config.getTE()));

    }

    public PeopleCollection runSimulation() throws InsufficientNumberOfPeopleException {



        while(DateUtils.dateBeforeOrEqual(currentTime, config.getTE())) {

            if(DateUtils.dateBeforeOrEqual(currentTime, config.getT0())) {
                summary.setStartPop(population.getLivingPeople().getNumberOfPeople());
            }

            System.out.print(currentTime.toString() + "\t");


            birthLogic.handleEvent(config, currentTime, config.getSimulationTimeStep(), population, desired);


            if (InitLogic.inInitPeriod(currentTime) &&
                    DateUtils.matchesInterval(currentTime, InitLogic.getTimeStep(), config.getTS())) {
                InitLogic.handleInitPeople(config, currentTime, population);
            } else if(!InitLogic.inInitPeriod(currentTime)) {
                System.out.print(0 + "\t");
            }

            deathLogic.handleEvent(config, currentTime, config.getSimulationTimeStep(), population, desired);

            if(inSimDates()) {
                population.getPopulationCounts().updateMaxPopulation(population.getLivingPeople().getNumberOfPeople());
            }

            currentTime = currentTime.advanceTime(config.getSimulationTimeStep());

            log.info("Time step completed " + currentTime.toString() + "    Population " + population.getLivingPeople().getNumberOfPersons());
            System.out.println(population.getLivingPeople().getNumberOfPeople() + "\t" + population.getDeadPeople().getNumberOfPeople());

        }


        System.out.println("TKilled\t" + NDeathLogic.tKilled);
        System.out.println("TBorn\t" + NBirthLogic.tBirths);
        System.out.println("Ratio\t" + NDeathLogic.tKilled / (double) NBirthLogic.tBirths);

        summary.setCompleted(true);
        summary.setEndPop(population.getLivingPeople().getNumberOfPeople());
        summary.setPeakPop(population.getPopulationCounts().getPeakPopulationSize());

        return population.getAllPeople();
    }

    private boolean inSimDates() {
        return DateUtils.dateBeforeOrEqual(config.getT0(), currentTime);
    }

}
