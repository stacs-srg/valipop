package uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.standrews.cs.digitising_scotland.verisim.Config;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.simulationSummaryLogging.SummaryRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TableInstances.*;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TableStructure.NoTableRowsException;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.CTtree;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.ProgramTimer;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.implementations.MemoryUsageAnalysis;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ContigencyTableFactory {

    public static final Logger log = LogManager.getLogger(ContigencyTableFactory.class);

    public static void generateContigencyTables(PeopleCollection population, PopulationStatistics desired, Config config, SummaryRow summary) {

        ProgramTimer tableTimer = new ProgramTimer();

        CTtree fullTree = new CTtree(population, desired, config.getT0(), config.getTE());

        MemoryUsageAnalysis.log();

        PrintStream fullOutput;

        PrintStream obOutput;
        PrintStream mbOutput;
        PrintStream partOutput;
        PrintStream sepOutput;
        PrintStream deathOutput;
        try {
            log.info("OBDModel --- Extracting and Outputting CTables to files");
            CTtableOB obTable = new CTtableOB(fullTree, desired);
            Path obPath = FileUtils.mkBlankFile(FileUtils.getContingencyTablesPath(), "ob-CT.csv");
            obOutput = new PrintStream(obPath.toFile(), "UTF-8");
            obTable.outputToFile(obOutput);

            MemoryUsageAnalysis.log();

            CTtableMB mbTable = new CTtableMB(fullTree, desired);
            Path mbPath = FileUtils.mkBlankFile(FileUtils.getContingencyTablesPath(), "mb-CT.csv");
            mbOutput = new PrintStream(mbPath.toFile(), "UTF-8");
            mbTable.outputToFile(mbOutput);

            MemoryUsageAnalysis.log();

            CTtablePart partTable = new CTtablePart(fullTree, desired);
            Path partPath = FileUtils.mkBlankFile(FileUtils.getContingencyTablesPath(), "part-CT.csv");
            partOutput = new PrintStream(partPath.toFile(), "UTF-8");
            partTable.outputToFile(partOutput);

            MemoryUsageAnalysis.log();

            CTtableSep sepTable = new CTtableSep(fullTree, desired);
            Path sepPath = FileUtils.mkBlankFile(FileUtils.getContingencyTablesPath(), "sep-CT.csv");
            sepOutput = new PrintStream(sepPath.toFile(), "UTF-8");
            sepTable.outputToFile(sepOutput);

            MemoryUsageAnalysis.log();

            CTtableDeath deathTable = new CTtableDeath(fullTree);
            Path deathPath = FileUtils.mkBlankFile(FileUtils.getContingencyTablesPath(), "death-CT.csv");
            deathOutput = new PrintStream(deathPath.toFile(), "UTF-8");
            deathTable.outputToFile(deathOutput);

            MemoryUsageAnalysis.log();

            System.out.println("OBDModel --- Outputting Full CTable to file");
            Path fullPath = FileUtils.mkBlankFile(FileUtils.getContingencyTablesPath(), "full-CT.csv");
            fullOutput = new PrintStream(fullPath.toFile(), "UTF-8");
            new CTtableFull(fullTree, fullOutput);

            MemoryUsageAnalysis.log();

        } catch (IOException e) {
            throw new Error("failed to make CT files");
        } catch (NoTableRowsException e) {
            e.printStackTrace();
        }

        summary.setCTRunTime(tableTimer.getRunTimeSeconds());
    }

}
