package uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables;

import uk.ac.standrews.cs.valipop.implementations.MemoryUsageAnalysis;
import uk.ac.standrews.cs.valipop.implementations.PreEmptiveOutOfMemoryWarning;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.valipop.statistics.analysis.simulationSummaryLogging.SummaryRow;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableInstances.*;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure.CTtable;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure.NoTableRowsException;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.CTtree;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.Logger;
import uk.ac.standrews.cs.valipop.utils.ProgramTimer;
import uk.ac.standrews.cs.valipop.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.valipop.Config;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ContingencyTableFactory {

    public static final Logger log = new Logger(ContingencyTableFactory.class);

    public static void generateContingencyTables(PeopleCollection population, PopulationStatistics desired,
                                                 Config config, SummaryRow summary, int zeroAdjustValue,
                                                 int startStepBack) throws PreEmptiveOutOfMemoryWarning {

        ProgramTimer tableTimer = new ProgramTimer();

        // TODO revert back to T0?
        CTtree fullTree = new CTtree(population, desired, config.getTS(), config.getT0(), config.getTE(), startStepBack);

        MemoryUsageAnalysis.log();

        try {
            log.info("OBDModel --- Extracting and Outputting CTables to files");

            CTtableOB obTable = new CTtableOB(fullTree, desired);
            outputToFile(obTable, "ob-CT.csv");

            CTtableMB mbTable = new CTtableMB(fullTree, desired);
            outputToFile(mbTable, "mb-CT.csv");

            CTtablePart partTable = new CTtablePart(fullTree, desired);
            outputToFile(partTable, "part-CT.csv");

            CTtableSep sepTable = new CTtableSep(fullTree, desired);
            outputToFile(sepTable, "sep-CT.csv");

            CTtableDeath deathTable = new CTtableDeath(fullTree);
            outputToFile(deathTable, "death-CT.csv");

        } catch (IOException e) {
            throw new Error("failed to make CT files");
        } catch (NoTableRowsException e) {
            e.printStackTrace();
        }

        summary.setCTRunTime(tableTimer.getRunTimeSeconds());
    }

    private static void outputToFile(CTtable table, String fileName) throws IOException, NoTableRowsException, PreEmptiveOutOfMemoryWarning {

        MemoryUsageAnalysis.log();
        Path path = FileUtils.mkBlankFile(FileUtils.getContingencyTablesPath(), fileName);
        PrintStream ps = new PrintStream(path.toFile(), "UTF-8");
        table.outputToFile(ps);
        MemoryUsageAnalysis.log();
    }
}
