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
package uk.ac.standrews.cs.valipop.statistics.analysis.simulationSummaryLogging;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordFormat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SummaryRow {

    private static final String SEPARATOR = ",";

    private LocalDateTime startTime;
    private String reason;
    private String codeVersion;

    private Period inputWidth;
    private Period timestep;

    private LocalDate startDate;
    private LocalDate endDate;

    private int simLength;

    private int seed;

    private double birthFactor;
    private double deathFactor;
    private double recoveryFactor;
    private double proportionalRecoveryFactor;
    private Period minBirthSpacing;

    private RecordFormat outputRecordFormat;

    private int seedPop;

    private Path resultsDirectory;
    private Path inputsDirectory;

    private int startPop;
    private int totalPop;
    private int endPop;
    private int peakPop;

    private int ctTreeStepback;
    private double ctTreePrecision;

    private int eligibilityChecks;
    private int failedEligibilityChecks;

    private boolean completed = false;

    private double simRunTime;
    private double ctRunTime;
    private double recordsRunTime;
    private double statsRunTime;

    private boolean binomialSampling;

    private long maxMemoryUsage = -1L;
    private Double v = Double.NaN;

    private Config config;

    public SummaryRow(Config config, String codeVersion) {

        this.config = config;
        this.resultsDirectory = config.getResultsSavePath().resolve( config.getRunPurpose()).resolve( config.getStartTime().toString());
        this.inputsDirectory = config.getVarPath();
        this.startTime = config.getStartTime();
        this.reason = config.getRunPurpose();
        this.codeVersion = codeVersion;
        this.timestep = config.getSimulationTimeStep();
        this.inputWidth = config.getInputWidth();
        this.startDate = config.getT0();
        this.endDate = config.getTE();
        this.simLength = (int) DAYS.between(startDate, endDate);
        this.birthFactor = config.getBirthFactor();
        this.deathFactor = config.getDeathFactor();
        this.recoveryFactor = config.getRecoveryFactor();
        this.proportionalRecoveryFactor = config.getProportionalRecoveryFactor();
        this.binomialSampling = config.getBinomialSampling();
        this.minBirthSpacing = config.getMinBirthSpacing();
        this.outputRecordFormat = config.getOutputRecordFormat();
        this.seedPop = config.getT0PopulationSize();
        this.seed = config.getSeed();
        this.ctTreeStepback = config.getCtTreeStepback();
        this.ctTreePrecision = config.getCtTreePrecision();
    }

    public void setStartPop(int startPop) {
        this.startPop = startPop;
    }

    public void setTotalPop(int totalPop) {
        this.totalPop = totalPop;
    }

    public void setEndPop(int endPop) {
        this.endPop = endPop;
    }

    public void setPeakPop(int peakPop) {
        this.peakPop = peakPop;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setSimRunTime(double simRunTime) {
        this.simRunTime = simRunTime;
    }

    public void setCTRunTime(double ctRunTime) {
        this.ctRunTime = ctRunTime;
    }

    public void setRecordsRunTime(double recordsRunTime) {
        this.recordsRunTime = recordsRunTime;
    }

    public void setStatsRunTime(double statsRunTime) {
        this.statsRunTime = statsRunTime;
    }

    public void setMaxMemoryUsage(long maxUsage) {
        this.maxMemoryUsage = maxUsage;
    }

    public void setEligibilityChecks(int eligibilityChecks) {
        this.eligibilityChecks = eligibilityChecks;
    }

    public void setFailedEligibilityChecks(int failedEligibilityChecks) {
        this.failedEligibilityChecks = failedEligibilityChecks;
    }

    public String toString() {

        return makeRow(startTime, reason, codeVersion, inputsDirectory, totalPop, seedPop,
                completed, simLength, timestep, inputWidth, startPop,
                endPop, peakPop, startDate, endDate, simRunTime,
                ctRunTime, recordsRunTime, resultsDirectory, birthFactor,
                deathFactor, recoveryFactor, proportionalRecoveryFactor, binomialSampling,
                minBirthSpacing, (maxMemoryUsage / 1e6), outputRecordFormat.toString(),
                v.toString(), statsRunTime, eligibilityChecks, failedEligibilityChecks, seed, ctTreeStepback, ctTreePrecision) + "\n";
    }

    private static String makeRow(Object... values) {

        StringBuilder builder = new StringBuilder();

        for (int i = 0 ; i < values.length - 1; i ++) {
            Object value = values[i];

            builder.append(value);
            builder.append(SEPARATOR);
        }

        builder.append(values[values.length - 1]);

        return builder.toString();
    }

    public static String getSeparatedHeadings() {

        return makeRow("Start Time" ,"Reason", "Code Version", "Inputs Directory", "Total Pop",
                 "Seed Pop Size", "Completed", "Sim Length", "Timestep" ,
                 "Input Width", "Start Pop", "End Pop", "Peak Pop", "Start Date" ,
                 "End Date", "Sim Run time", "CT Run time", "Records Run time" ,
                 "Results Directory", "Birth Factor", "Death Factor", "Recovery Factor" ,
                 "Proportional Recovery Factor", "binomial Sampling", "Min Birth Spacing" ,
                 "Peak Memory Usage (MB)", "Output Record Format", "v/M", "Stats Run Time" ,
                 "Eligibility Checks", "Failed Eligibility Checks", "Seed", "CT Tree Stepback", "CT Tree Precision");
    }

    public void outputSummaryRowToFile() {
        try {
            Files.write(config.getGlobalSummaryPath(), toString().getBytes(), StandardOpenOption.APPEND);
            Files.write(config.getResultsSummaryPath(), toString().getBytes(), StandardOpenOption.APPEND);

        } catch (IOException e) {
            throw new RuntimeException("Summary row could not be printed to summary files", e);
        }
    }

    public void setV(double v) {
        this.v = v;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
}
