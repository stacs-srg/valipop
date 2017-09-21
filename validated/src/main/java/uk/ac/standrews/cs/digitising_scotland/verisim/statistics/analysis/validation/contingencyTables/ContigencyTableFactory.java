package uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.standrews.cs.digitising_scotland.verisim.Config;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.simulationSummaryLogging.SummaryRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TableInstances.*;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TableStructure.CTtable;
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

    public static void generateContigencyTables(PeopleCollection population, PopulationStatistics desired,
                                                Config config, SummaryRow summary, int zeroAdjustValue,
                                                int startStepBack) {

        ProgramTimer tableTimer = new ProgramTimer();

        CTtree fullTree = new CTtree(population, desired, config.getT0(), config.getTE(), startStepBack);

        MemoryUsageAnalysis.log();

        try {
            log.info("OBDModel --- Extracting and Outputting CTables to files");

            CTtableOB obTable = new CTtableOB(fullTree, desired);
//            outputToFile(obTable, 0, "ob-CT-zav-0.csv");
            outputToFile(obTable, 1, "ob-CT-zav-1.csv");

            CTtableMB mbTable = new CTtableMB(fullTree, desired);
//            outputToFile(mbTable, 0, "mb-CT-zav-0.csv");
            outputToFile(mbTable, 1, "mb-CT-zav-1.csv");

            CTtablePart partTable = new CTtablePart(fullTree, desired);
//            outputToFile(partTable, 0, "part-CT-zav-0.csv");
            outputToFile(partTable, 1, "part-CT-zav-1.csv");

            CTtableSep sepTable = new CTtableSep(fullTree, desired);
//            outputToFile(sepTable, 0, "sep-CT-zav-0.csv");
            outputToFile(sepTable, 1, "sep-CT-zav-1.csv");

            CTtableDeath deathTable = new CTtableDeath(fullTree);
//            outputToFile(deathTable, 0, "death-CT-zav-0.csv");
            outputToFile(deathTable, 1, "death-CT-zav-1.csv");


//            System.out.println("OBDModel --- Outputting Full CTable to file");
//            zeroAdjustValue = 0;
//            String fN = "full-CT-zav-" + String.valueOf(zeroAdjustValue)+ ".csv";
//            Path fullPath = FileUtils.mkBlankFile(FileUtils.getContingencyTablesPath(), fN);
//            fullOutput = new PrintStream(fullPath.toFile(), "UTF-8");
//            new CTtableFull(fullTree, fullOutput, zeroAdjustValue);
//

        } catch (IOException e) {
            throw new Error("failed to make CT files");
        } catch (NoTableRowsException e) {
            e.printStackTrace();
        }

        summary.setCTRunTime(tableTimer.getRunTimeSeconds());
    }

    private static void outputToFile(CTtable table, int zeroAdjustValue, String fileName) throws IOException, NoTableRowsException {
        MemoryUsageAnalysis.log();
        Path path = FileUtils.mkBlankFile(FileUtils.getContingencyTablesPath(), fileName);
        PrintStream ps = new PrintStream(path.toFile(), "UTF-8");
        table.outputToFile(ps, zeroAdjustValue);
        MemoryUsageAnalysis.log();
    }

}
