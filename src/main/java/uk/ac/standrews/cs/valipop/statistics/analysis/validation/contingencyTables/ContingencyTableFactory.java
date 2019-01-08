package uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.implementations.MemoryUsageAnalysis;
import uk.ac.standrews.cs.valipop.simulationEntities.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.valipop.statistics.analysis.simulationSummaryLogging.SummaryRow;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableInstances.*;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure.CTtable;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure.NoTableRowsException;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.CTtree;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.ProgramTimer;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ContingencyTableFactory {

    public static final Logger log = Logger.getLogger(ContingencyTableFactory.class.getName());

    public static void generateContingencyTables(PeopleCollection population, PopulationStatistics desired,
                                                 Config config, SummaryRow summary, int startStepBack)  {

        ProgramTimer tableTimer = new ProgramTimer();

        // TODO revert back to T0?
        CTtree fullTree = new CTtree(population, desired, config.getTS(), config.getT0(), config.getTE(), startStepBack);

        MemoryUsageAnalysis.log();

        try {
            log.info("OBDModel --- Extracting and Outputting CTables to files");

            CTtableOB obTable = new CTtableOB(fullTree);
            outputToFile(obTable, "ob-CT.csv", config);

            CTtableMB mbTable = new CTtableMB(fullTree);
            outputToFile(mbTable, "mb-CT.csv", config);

            CTtablePart partTable = new CTtablePart(fullTree);
            outputToFile(partTable, "part-CT.csv", config);

            CTtableSep sepTable = new CTtableSep(fullTree);
            outputToFile(sepTable, "sep-CT.csv", config);

            CTtableDeath deathTable = new CTtableDeath(fullTree);
            outputToFile(deathTable, "death-CT.csv", config);

        } catch (IOException | NoTableRowsException e) {
            throw new RuntimeException(e);
        }

        summary.setCTRunTime(tableTimer.getRunTimeSeconds());
    }

    private static void outputToFile(CTtable table, String fileName, Config config) throws IOException, NoTableRowsException {

        MemoryUsageAnalysis.log();
        Path path = config.getContingencyTablesPath().resolve( fileName);
        Config.mkBlankFile(path);
        PrintStream ps = new PrintStream(path.toFile(), "UTF-8");
        table.outputToFile(ps);
        MemoryUsageAnalysis.log();
    }
}
