/*
 * valipop - <https://github.com/stacs-srg/valipop>
 * Copyright © 2026 Systems Research Group, University of St Andrews (graham.kirby@st-andrews.ac.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.population.MemoryUsageAnalysis;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.statistics.analysis.simulationSummaryLogging.SummaryRow;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.specialisedTables.*;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.tables.ContingencyTable;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.tables.NoTableRowsException;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.ContingencyTree;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.ProgramTimer;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.logging.Logger;

import static uk.ac.standrews.cs.valipop.population.OBDModel.EXPORT_CHARSET;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ContingencyTableFactory {

    public static final Logger log = Logger.getLogger(ContingencyTableFactory.class.getName());

    public static final String BIRTH_CONTINGENCY_TABLE_FILENAME = "birth-contingency-table.csv";
    public static final String MULTIPLE_BIRTH_CONTINGENCY_TABLE_FILENAME = "multiple-birth-contingency-table.csv";
    public static final String PARTNERSHIP_CONTINGENCY_TABLE_FILENAME = "partnership-contingency-table.csv";
    public static final String SEPARATION_CONTINGENCY_TABLE_FILENAME = "separation-contingency-table.csv";
    public static final String DEATH_CONTINGENCY_TABLE_FILENAME = "death-contingency-table.csv";

    public static void generateContingencyTables(final Iterable<IPerson> population, final PopulationStatistics desired,
                                                 final Config config, final SummaryRow summary)  {

        final ProgramTimer tableTimer = new ProgramTimer();
        MemoryUsageAnalysis.log();

        final ContingencyTree fullTree = new ContingencyTree(population, desired, config.getInitialisationStart(), config.getSimulationStart(), config.getSimulationEnd(), config.getContingencyTableStepback(), config.getContingencyTablePrecision());
        final boolean shouldScaleTargetFrequencies = config.shouldScaleContingencyTableTargetFrequencies();

        try {
            log.info("OBDModel --- Extracting and Outputting contingency tables to files");

            // Sample from birth contingency table:

            // Source,YOB,Age,NPCIAP,CIY,Date,Frequency
            // SIMULATED,1724,130,1,false,1854,1
            // SIMULATED,1724,131,1,false,1855,1
            // SIMULATED,1724,132,1,false,1856,1
            // TARGET,1777,149,3,false,1926,0
            // TARGET,1777,149,3,true,1926,0
            // TARGET,1777,149,4+,false,1926,0

            final BirthContingencyTable birthTable = new BirthContingencyTable(fullTree);
            if (shouldScaleTargetFrequencies) birthTable.scaleTargetFrequencies();
            outputToFile(birthTable, BIRTH_CONTINGENCY_TABLE_FILENAME, config);

            // Sample from multiple birth contingency table:

            // Source,YOB,Age,NCIY,Date,Frequency
            // SIMULATED,1724,130,0,1854,1
            // SIMULATED,1724,131,0,1855,1
            // SIMULATED,1724,132,0,1856,1
            // TARGET,1846,37,3,1882,0
            // TARGET,1846,37,4,1882,0
            // TARGET,1846,38,0,1883,184

            final MultipleBirthContingencyTable multipleBirthTable = new MultipleBirthContingencyTable(fullTree);
            if (shouldScaleTargetFrequencies) multipleBirthTable.scaleTargetFrequencies();
            outputToFile(multipleBirthTable, MULTIPLE_BIRTH_CONTINGENCY_TABLE_FILENAME, config);

            // Sample from partnership contingency table:

            // Source,YOB,Age,PartnerAge,Date,Frequency
            // SIMULATED,1855,20,na,1875,160
            // SIMULATED,1855,21,na,1876,182
            // SIMULATED,1855,22,20-24,1877,13
            // TARGET,1853,24,na,1877,167
            // TARGET,1853,25,15-19,1878,0
            // TARGET,1853,25,20-24,1878,1

            final PartnershipContingencyTable partnershipTable = new PartnershipContingencyTable(fullTree);
            if (shouldScaleTargetFrequencies) partnershipTable.scaleTargetFrequencies();
            outputToFile(partnershipTable, PARTNERSHIP_CONTINGENCY_TABLE_FILENAME, config);

            // Sample from separation contingency table:

            // Source,YOB,CIY,NCIP,Separated,Date,Frequency
            // SIMULATED,1879,false,0,NO,1905,1
            // SIMULATED,1879,true,1,NO,1894,10
            // SIMULATED,1879,true,1,NO,1896,1
            // TARGET,1812,false,3,NA,1967,0
            // TARGET,1812,false,3,NA,1968,0
            // TARGET,1812,false,3,NA,1969,0

            final SeparationContingencyTable separationTable = new SeparationContingencyTable(fullTree);
            if (shouldScaleTargetFrequencies) separationTable.scaleTargetFrequencies();
            outputToFile(separationTable, SEPARATION_CONTINGENCY_TABLE_FILENAME, config);

            // Sample from death contingency table:

            // Source,YOB,Sex,Age,Died,Date,Frequency
            // SIMULATED,1719,M,135,false,1854,1
            // SIMULATED,1719,M,136,false,1855,1
            // SIMULATED,1719,M,137,false,1856,1
            // TARGET,1860,F,51,false,1911,156
            // TARGET,1860,F,51,true,1911,0
            // TARGET,1860,F,52,false,1912,156

            final DeathContingencyTable deathTable = new DeathContingencyTable(fullTree);
            if (shouldScaleTargetFrequencies) deathTable.scaleTargetFrequencies();
            outputToFile(deathTable, DEATH_CONTINGENCY_TABLE_FILENAME, config);

        } catch (final IOException | NoTableRowsException e) {
            throw new RuntimeException(e);
        }

        summary.setCTRunTime(tableTimer.getRunTimeSeconds());
    }

    private static void outputToFile(final ContingencyTable table, final String fileName, final Config config) throws IOException, NoTableRowsException {

        MemoryUsageAnalysis.log();
        final Path path = config.getContingencyTablesPath().resolve(fileName);

        Config.createFileIfDoesNotExist(path);

        try (final PrintStream printStream = new PrintStream(path.toFile(), EXPORT_CHARSET)) {
            table.outputToFile(printStream, config.getValidationFrequencyThreshold());
        }

        MemoryUsageAnalysis.log();
    }
}
