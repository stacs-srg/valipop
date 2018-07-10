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


import uk.ac.standrews.cs.valipop.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordFormat;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SummaryRow {

    private String startTime;
    private String reason;
    private String codeVersion;

    private CompoundTimeUnit inputWidth;
    private CompoundTimeUnit timestep;

    private Date startDate;
    private Date endDate;

    private int simLength;

    private double birthFactor;
    private double deathFactor;
    private double recoveryFactor;
    private double proportionalRecoveryFactor;
    private int minBirthSpacing;

    private RecordFormat outputRecordFormat = RecordFormat.NONE;

    private int seedPop;

    // Post

    private Path resultsDirectory;
    private String inputsDirectory;

    private int startPop;

    private int totalPop;
    private int endPop;
    private int peakPop;

    private boolean completed;

    private double simRunTime;
    private double ctRunTime;
    private double recordsRunTime;
    private double statsRunTime;

    private boolean binominalSampling;

    private long maxMemoryUsage = -1L;
    private Double v = Double.NaN;


    public SummaryRow(Path resultsDirectory,
                      String inputsDirectory,
                      String startTime,
                      String reason,
                      String codeVersion,
                      CompoundTimeUnit timestep,
                      CompoundTimeUnit inputWidth,
                      Date startDate,
                      Date endDate,
                      int simLength,
                      double birthFactor,
                      double deathFactor,
                      double recoveryFactor,
                      double proportionalRecoveryFactor,
                      boolean binominalSampling,
                      int minBirthSpacing,
                      RecordFormat outputFormat,
                      int seedPopSize) {

        this.resultsDirectory = resultsDirectory;
        this.inputsDirectory = inputsDirectory;
        this.startTime = startTime;
        this.codeVersion = codeVersion;
        this.timestep = timestep;
        this.reason = reason;
        this.inputWidth = inputWidth;
        this.startDate = startDate;
        this.endDate = endDate;
        this.simLength = simLength;
        this.birthFactor = birthFactor;
        this.deathFactor = deathFactor;
        this.recoveryFactor = recoveryFactor;
        this.binominalSampling = binominalSampling;
        this.minBirthSpacing = minBirthSpacing;
        this.outputRecordFormat = outputFormat;
        this.proportionalRecoveryFactor = proportionalRecoveryFactor;
        this.seedPop = seedPopSize;

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

    public String toSeperatedString(char sep) {
        return startTime + sep + reason + sep + codeVersion + sep + inputsDirectory + sep + totalPop + sep + seedPop
                + sep + completed + sep + simLength + sep + timestep + sep + inputWidth + sep + startPop + sep
                + endPop + sep + peakPop + sep + startDate + sep + endDate + sep + simRunTime + sep
                + ctRunTime + sep + recordsRunTime + sep + resultsDirectory + sep + birthFactor + sep
                + deathFactor + sep + recoveryFactor + sep + proportionalRecoveryFactor + sep + binominalSampling + sep
                + minBirthSpacing + sep + (maxMemoryUsage / 1e6) + sep + outputRecordFormat.toString() + sep
                + v.toString() + sep + statsRunTime + "\n";
    }

    public static String getSeparatedHeadings(char sep) {
        return "Start Time" + sep + "Reason" + sep + "Code Version" + sep + "Inputs Directory" + sep + "Total Pop"
                + sep + "Seed Pop Size" + sep + "Completed" + sep + "Sim Length" + sep + "Timestep" + sep
                + "Input Width" + sep + "Start Pop" + sep + "End Pop" + sep + "Peak Pop" + sep + "Start Date" + sep
                + "End Date" + sep + "Sim Run time" + sep + "CT Run time" + sep + "Records Run time" + sep
                + "Results Directory" + sep + "Birth Factor" + sep + "Death Factor" + sep + "Recovery Factor" + sep
                + "Proportional Recovery Factor" + sep + "Binominal Sampling" + sep + "Min Birth Spacing" + sep
                + "Peak Memory Usage (MB)" + sep + "Output Record Format" + sep + "v/M" + sep + "Stats Run Time";
    }

    public void outputSummaryRowToFile() {
        try {
            FileUtils.writeSummaryRowToSummaryFiles(this);
        } catch (IOException e) {
            System.err.println("Summary row could not be printed to summary files. See message: ");
            System.err.println(e.getMessage());
        }
    }

    public void outputSummaryRowToFile(Path currentResultsSummaryPath) {
        try {
            FileUtils.writeSummaryRowToSummaryFiles(this, currentResultsSummaryPath);
        } catch (IOException e) {
            System.err.println("Summary row could not be printed to summary files. See message: ");
            System.err.println(e.getMessage());
        }
    }



    public void setV(double v) {
        this.v = v;
    }
}
