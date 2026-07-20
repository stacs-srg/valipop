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
package uk.ac.standrews.cs.valipop.population;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import uk.ac.standrews.cs.valipop.exporting.PopulationExportFormat;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordExportFormat;

public class SerializableConfig implements Serializable {
    public String varPath;
    public String varOrderedBirthPaths;
    public String varMaleLifetablePaths;
    public String varMaleDeathCausesPaths;
    public String varFemaleLifetablePaths;
    public String varFemaleDeathCausesPaths;
    public String varMultipleBirthPaths;
    public String varAdulterousBirthPaths;
    public String varPartneringPaths;
    public String varSeparationPaths;
    public String varBirthRatioPaths;
    public String varMaleForenamePaths;
    public String varFemaleForenamePaths;
    public String varMigrantMaleForenamePaths;
    public String varMigrantFemaleForenamePaths;
    public String varMigrantSurnamePaths;
    public String varMigrationRatePaths;
    public String varSurnamePaths;
    public String varMarriagePaths;
    public String varGeographyPaths;
    public String varMaleOccupationPaths;
    public String varFemaleOccupationPaths;
    public String varMaleOccupationChangePaths;
    public String varFemaleOccupationChangePaths;
    public String globalSummaryPath;
    public String resultsSummaryPath;
    public String detailedResultsPath;
    public String recordsPath;
    public String graphsPath;
    public String contingencyTablesPath;
    public String runPath;
    public double initialisationBirthRate;
    public double initialisationDeathRate;
    public double recoveryFactor;
    public double proportionalRecoveryFactor;
    public boolean binomialSampling;
    public boolean deterministic;
    public boolean outputTables;
    public Period simulationTimeStep;
    public Period minBirthSpacing;
    public Period minGestationPeriod;
    public Period distributionGranularity;
    public String summaryResultsDirPath;
    public String resultsSavePath;
    public String geographyFilePath;
    public String projectPath;
    public int seed;
    public double overSizedGeographyFactor;
    public int contingencyTableStepback;
    public double contingencyTablePrecision;
    public String groupName;
    public RecordExportFormat outputRecordFormat;
    public PopulationExportFormat outputGraphFormat;
    public LocalDateTime simulationExecutionStartTime;
    public LocalDate initialisationStart;
    public LocalDate simulationStart;
    public LocalDate simulationEnd;
    public int targetInitialPopulationSize;

    public SerializableConfig(
        final String varPath,
        final String varOrderedBirthPaths,
        final String varMaleLifetablePaths,
        final String varMaleDeathCausesPaths,
        final String varFemaleLifetablePaths,
        final String varFemaleDeathCausesPaths,
        final String varMultipleBirthPaths,
        final String varAdulterousBirthPaths,
        final String varPartneringPaths,
        final String varSeparationPaths,
        final String varBirthRatioPaths,
        final String varMaleForenamePaths,
        final String varFemaleForenamePaths,
        final String varMigrantMaleForenamePaths,
        final String varMigrantFemaleForenamePaths,
        final String varMigrantSurnamePaths,
        final String varMigrationRatePaths,
        final String varSurnamePaths,
        final String varMarriagePaths,
        final String varGeographyPaths,
        final String varMaleOccupationPaths,
        final String varFemaleOccupationPaths,
        final String varMaleOccupationChangePaths,
        final String varFemaleOccupationChangePaths,
        final String globalSummaryPath,
        final String resultsSummaryPath,
        final String detailedResultsPath,
        final String recordsPath,
        final String graphsPath,
        final String contingencyTablesPath,
        final String runPath,
        final double initialisationBirthRate,
        final double initialisationDeathRate,
        final double recoveryFactor,
        final double proportionalRecoveryFactor,
        final boolean binomialSampling,
        final boolean deterministic,
        final boolean outputTables,
        final Period simulationTimeStep,
        final Period minBirthSpacing,
        final Period minGestationPeriod,
        final Period distributionGranularity,
        final String summaryResultsDirPath,
        final String resultsSavePath,
        final String geographyFilePath,
        final String projectPath,
        final int seed,
        final double overSizedGeographyFactor,
        final int contingencyTableStepback,
        final double contingencyTablePrecision,
        final String groupName,
        final RecordExportFormat outputRecordFormat,
        final PopulationExportFormat outputGraphFormat,
        final LocalDateTime simulationExecutionStartTime,
        final LocalDate initialisationStart,
        final LocalDate simulationStart,
        final LocalDate simulationEnd,
        final int targetInitialPopulationSize
    ) {
        this.varPath                          =varPath;
        this.varOrderedBirthPaths             =varOrderedBirthPaths;
        this.varMaleLifetablePaths            =varMaleLifetablePaths;
        this.varMaleDeathCausesPaths          =varMaleDeathCausesPaths;
        this.varFemaleLifetablePaths          =varFemaleLifetablePaths;
        this.varFemaleDeathCausesPaths        =varFemaleDeathCausesPaths;
        this.varMultipleBirthPaths            =varMultipleBirthPaths;
        this.varAdulterousBirthPaths          =varAdulterousBirthPaths;
        this.varPartneringPaths               =varPartneringPaths;
        this.varSeparationPaths               =varSeparationPaths;
        this.varBirthRatioPaths               =varBirthRatioPaths;
        this.varMaleForenamePaths             =varMaleForenamePaths;
        this.varFemaleForenamePaths           =varFemaleForenamePaths;
        this.varMigrantMaleForenamePaths      =varMigrantMaleForenamePaths;
        this.varMigrantFemaleForenamePaths    =varMigrantFemaleForenamePaths;
        this.varMigrantSurnamePaths           =varMigrantSurnamePaths;
        this.varMigrationRatePaths            =varMigrationRatePaths;
        this.varSurnamePaths                  =varSurnamePaths;
        this.varMarriagePaths                 =varMarriagePaths;
        this.varGeographyPaths                =varGeographyPaths;
        this.varMaleOccupationPaths           =varMaleOccupationPaths;
        this.varFemaleOccupationPaths         =varFemaleOccupationPaths;
        this.varMaleOccupationChangePaths     =varMaleOccupationChangePaths;
        this.varFemaleOccupationChangePaths   =varFemaleOccupationChangePaths;
        this.globalSummaryPath                =globalSummaryPath;
        this.resultsSummaryPath               =resultsSummaryPath;
        this.detailedResultsPath              =detailedResultsPath;
        this.recordsPath                      =recordsPath;
        this.graphsPath                       =graphsPath;
        this.contingencyTablesPath            =contingencyTablesPath;
        this.runPath                          =runPath;
        this.initialisationBirthRate = initialisationBirthRate;
        this.initialisationDeathRate = initialisationDeathRate;
        this.recoveryFactor                   =recoveryFactor;
        this.proportionalRecoveryFactor       =proportionalRecoveryFactor;
        this.binomialSampling                 =binomialSampling;
        this.deterministic                    =deterministic;
        this.outputTables                     =outputTables;
        this.simulationTimeStep               =simulationTimeStep;
        this.minBirthSpacing                  =minBirthSpacing;
        this.minGestationPeriod               =minGestationPeriod;
        this.distributionGranularity = distributionGranularity;
        this.summaryResultsDirPath            =summaryResultsDirPath;
        this.resultsSavePath                  =resultsSavePath;
        this.geographyFilePath                =geographyFilePath;
        this.projectPath                      =projectPath;
        this.seed                             =seed;
        this.overSizedGeographyFactor         =overSizedGeographyFactor;
        this.contingencyTableStepback = contingencyTableStepback;
        this.contingencyTablePrecision = contingencyTablePrecision;
        this.groupName = groupName;
        this.outputRecordFormat               =outputRecordFormat;
        this.outputGraphFormat                =outputGraphFormat;
        this.simulationExecutionStartTime = simulationExecutionStartTime;
        this.initialisationStart = initialisationStart;
        this.simulationStart = simulationStart;
        this.simulationEnd = simulationEnd;
        this.targetInitialPopulationSize = targetInitialPopulationSize;
    }
}
