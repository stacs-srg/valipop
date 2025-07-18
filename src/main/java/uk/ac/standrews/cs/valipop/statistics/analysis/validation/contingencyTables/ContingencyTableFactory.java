/*
 * valipop - <https://github.com/stacs-srg/valipop>
 * Copyright © 2025 Systems Research Group, University of St Andrews (graham.kirby@st-andrews.ac.uk)
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
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableInstances.*;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure.ContingencyTable;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure.NoTableRowsException;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.ContingencyTree;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.ProgramTimer;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.logging.Logger;

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

        final ContingencyTree fullTree = new ContingencyTree(population, desired, config.getTS(), config.getT0(), config.getTE(), config.getCtTreeStepback(), config.getCtTreePrecision());

        MemoryUsageAnalysis.log();

        try {
            log.info("OBDModel --- Extracting and Outputting CTtables to files");

            // Sample from birth contingency table:

            // Source,YOB,Age,NPCIAP,CIY,Date,freq
            // STAT,1824,88,1,false,1912,0.17523938100498673
            // STAT,1893,46,2,true,1939,0.016814012025406378
            // SIM,1891,22,1,true,1913,7
            // STAT,1892,43,1,true,1935,0.01803166904527456
            // SIM,1867,15,0,false,1882,276
            // STAT,1827,99,0,false,1926,0.003839974016301987
            // SIM,1838,18,1,false,1856,7
            // STAT,1823,52,4+,false,1875,0.3615409449909666

            final BirthContingencyTable birthTable = new BirthContingencyTable(fullTree);
            outputToFile(birthTable, BIRTH_CONTINGENCY_TABLE_FILENAME, config);

            // Sample from multiple birth contingency table:

            // Source,YOB,Age,NCIY,Date,freq
            // SIM,1851,34,1,1885,1
            // STAT,1875,16,1,1890,5.1868390051713416
            // STAT,1861,44,2,1904,0.00407474240885941
            // STAT,1857,16,3,1872,4.4409124981923797E-4
            // SIM,1849,43,0,1892,295
            // SIM,1847,29,1,1876,31
            // STAT,1883,58,0,1940,72.18595183609412
            // SIM,1843,15,1,1858,14

            final MultipleBirthContingencyTable multipleBirthTable = new MultipleBirthContingencyTable(fullTree);
            outputToFile(multipleBirthTable, MULTIPLE_BIRTH_CONTINGENCY_TABLE_FILENAME, config);

            // Sample from partnership birth contingency table:

            // Source,YOB,Age,NPA,Date,freq
            // SIM,1896,0,na,1896,320
            // STAT,1868,71,na,1939,35.083409917177384
            // SIM,1794,114,na,1908,5
            // SIM,1875,18,na,1893,361
            // STAT,1839,17,35-39,1856,0.0017950908807901089
            // STAT,1876,90,na,1966,9.969241698506849
            // STAT,1906,62,na,1968,60.8582475966006
            // STAT,1890,48,20-24,1938,1.787898137552088E-4

            final PartnershipContingencyTable partnershipTable = new PartnershipContingencyTable(fullTree);
            outputToFile(partnershipTable, PARTNERSHIP_CONTINGENCY_TABLE_FILENAME, config);

            // Sample from separation contingency table:

            // Source,YOB,CIY,NCIP,Separated,Date,freq
            // STAT,1852,false,0,NA,1870,246.9690006155067
            // STAT,1852,false,0,NA,1868,279.53755621842754
            // STAT,1852,false,0,NA,1869,263.26206710975265
            // STAT,1852,false,0,NA,1860,312.48859067507
            // STAT,1852,false,0,NA,1861,312.438592500562
            // SIM,1891,false,0,NO,1906,15
            // SIM,1891,false,0,NO,1907,16
            // SIM,1891,false,0,NO,1916,4

            final SeparationContingencyTable separationTable = new SeparationContingencyTable(fullTree);
            outputToFile(separationTable, SEPARATION_CONTINGENCY_TABLE_FILENAME, config);

            // Sample from death contingency table:

            // Source,YOB,Sex,Age,Died,Date,freq
            // SIM,1861,M,26,false,1887,375
            // STAT,1879,F,81,false,1960,32.37717867579102
            // STAT,1924,F,43,false,1967,59.68535422346247
            // SIM,1904,F,21,false,1925,199
            // STAT,1835,M,40,false,1875,1.9413789757452387
            // STAT,1858,F,39,false,1897,93.38112876053815
            // STAT,1869,M,16,true,1885,0.03452359551769157
            // STAT,1884,M,62,false,1946,55.67931991318474

            final DeathContingencyTable deathTable = new DeathContingencyTable(fullTree);
            outputToFile(deathTable, DEATH_CONTINGENCY_TABLE_FILENAME, config);

        } catch (final IOException | NoTableRowsException e) {
            throw new RuntimeException(e);
        }

        summary.setCTRunTime(tableTimer.getRunTimeSeconds());
    }

    private static void outputToFile(final ContingencyTable table, final String fileName, final Config config) throws IOException, NoTableRowsException {

        MemoryUsageAnalysis.log();
        final Path path = config.getContingencyTablesPath().resolve(fileName);
        Config.mkBlankFile(path);
        final PrintStream ps = new PrintStream(path.toFile(), StandardCharsets.UTF_8);
        table.outputToFile(ps);
        MemoryUsageAnalysis.log();
    }
}
