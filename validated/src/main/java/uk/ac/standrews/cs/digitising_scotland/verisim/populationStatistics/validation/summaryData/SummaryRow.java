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
package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.summaryData;


import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;

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

    private double factor;

    // Post

    private Path resultsDirectory;

    private int startPop;

    private int totalPop;
    private int endPop;
    private int peakPop;

    private boolean completed;

    private long simRunTime;
    private long ctRunTime;
    private long recordsRunTime;


    public SummaryRow(Path resultsDirectory,
                      String startTime,
                      String reason,
                      CompoundTimeUnit bTimestep,
                      CompoundTimeUnit dTimestep,
                      CompoundTimeUnit inputWidth,
                      Date startDate,
                      Date endDate,
                      int simLength) {

        this.resultsDirectory = resultsDirectory;
        this.startTime = startTime;
        this.reason = reason;
        this.inputWidth = inputWidth;
        this.startDate = startDate;
        this.endDate = endDate;
        this.simLength = simLength;

    }

    public SummaryRow(Path resultsDirectory,
                      String startTime,
                      String reason,
                      String codeVersion,
                      CompoundTimeUnit inputWidth,
                      Date startDate,
                      Date endDate,
                      int simLength,
                      double factor) {

        this.resultsDirectory = resultsDirectory;
        this.startTime = startTime;
        this.codeVersion = codeVersion;
        this.reason = reason;
        this.inputWidth = inputWidth;
        this.startDate = startDate;
        this.endDate = endDate;
        this.simLength = simLength;
        this.factor = factor;

    }


    public void setResultsDirectory(Path directory) {
        this.resultsDirectory = directory;
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

    public void setSimRunTime(long simRunTime) {
        this.simRunTime = simRunTime;
    }

    public void setCTRunTime(long ctRunTime) {
        this.ctRunTime = ctRunTime;
    }

    public void setRecordsRunTime(long recordsRunTime) {
        this.recordsRunTime = recordsRunTime;
    }

    public String toSeperatedString(char sep) {
        return startTime + sep + reason + sep + codeVersion + sep + totalPop + sep + completed + sep
                + simLength + sep + timestep + sep + inputWidth + sep + startPop + sep
                + endPop + sep + peakPop + sep + startDate + sep + endDate + sep + simRunTime + sep
                + ctRunTime + sep + recordsRunTime + sep + resultsDirectory + sep + factor + "\n";
    }

    public static String getSeparatedHeadings(char sep) {
        return "Start Time" + sep + "Reason" + sep + "Code Version" + sep + "Total Pop" + sep + "Completed" + sep
                + "Sim Length" + sep + "Timestep" + sep + "Input Width" + sep + "Start Pop" + sep
                + "End Pop" + sep + "Peak Pop" + sep + "Start Date" + sep + "End Date" + sep + "Sim Run time" + sep
                + "CT Run time" + sep + "Records Run time" + sep + "Results Directory" + sep + "Factor";
    }

}
