package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.summaryData;


import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;

import java.nio.file.Path;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SummaryRow {

    private Path resultsDirectory;

    private String startTime;
    private String reason;

    private CompoundTimeUnit bTimestep;
    private CompoundTimeUnit dTimestep;
    private CompoundTimeUnit inputWidth;

    private Date startDate;
    private Date endDate;

    private int simLength;

    // Post

    private int startPop;

    private int totalPop;
    private int endPop;
    private int peakPop;

    private double passed;
    private double completed;

    private double bPasses;
    private double mDPasses;
    private double fDPasses;

    private double pPasses;
    private double sPasses;
    private double mBPasses;

    private String runTime;


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
        this.bTimestep = bTimestep;
        this.dTimestep = dTimestep;
        this.inputWidth = inputWidth;
        this.startDate = startDate;
        this.endDate = endDate;
        this.simLength = simLength;

    }

    public void setStartPop(int startPop) {
        this.startPop = startPop;
    }

    public void setPeakPop(int peakPop) {
        this.peakPop = peakPop;
    }

    public void setEndPop(int endPop) {
        this.endPop = endPop;
    }

    public void setTotalPop(int totalPop) {
        this.totalPop = totalPop;
    }

    public void setPassed(double passed) {
        this.passed = passed;
    }

    public void setCompleted(double completed) {
        this.completed = completed;
    }

    public void setbPasses(double bPasses) {
        this.bPasses = bPasses;
    }

    public void setmDPasses(double mDPasses) {
        this.mDPasses = mDPasses;
    }

    public void setfDPasses(double fDPasses) {
        this.fDPasses = fDPasses;
    }

    public void setpPasses(double pPasses) {
        this.pPasses = pPasses;
    }

    public void setsPasses(double sPasses) {
        this.sPasses = sPasses;
    }

    public void setmBPasses(double mBPasses) {
        this.mBPasses = mBPasses;
    }

    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }

    //    public SummaryRow(String startTime, String reason, int totalPop, double passed, double completed, double bPasses,
//                      double mDPasses, double fDPasses, double pPasses, double sPasses, double mBPasses, int simLength,
//                      CompoundTimeUnit bTimestep, CompoundTimeUnit dTimestep, CompoundTimeUnit inputWidth, int startPop,
//                      int endPop, int peakPop, Date startDate, Date endDate, int runTime, Path resultsDirectory) {
//
//        this.startTime = startTime;
//        this.reason = reason;
//        this.totalPop = totalPop;
//        this.passed = passed;
//        this.completed = completed;
//        this.bPasses = bPasses;
//        this.mDPasses = mDPasses;
//        this.fDPasses = fDPasses;
//        this.pPasses = pPasses;
//        this.sPasses = sPasses;
//        this.mBPasses = mBPasses;
//        this.simLength = simLength;
//        this.bTimestep = bTimestep;
//        this.dTimestep = dTimestep;
//        this.inputWidth = inputWidth;
//        this.startPop = startPop;
//        this.endPop = endPop;
//        this.peakPop = peakPop;
//        this.startDate = startDate;
//        this.endDate = endDate;
//        this.runTime = runTime;
//        this.resultsDirectory = resultsDirectory;
//
//    }

    public String toSeperatedString(char sep) {
        return startTime + sep + reason + sep + totalPop + sep + passed + sep + completed + sep + bPasses + sep +
                mDPasses + sep + fDPasses + sep + pPasses + sep + sPasses + sep + mBPasses + sep + simLength + sep +
                bTimestep + sep + dTimestep + sep + inputWidth + sep + startPop + sep + endPop + sep + peakPop + sep +
                startDate + sep + endDate + sep + runTime + sep + resultsDirectory + "\n";
    }

    public static String getSeparatedHeadings(char sep) {
        return "Start Time" + sep + "Reason" + sep + "Total Pop" + sep + "Passed" + sep + "Completed" + sep + "B Passes"
                + sep + "MD Passes" + sep + "FD Passes" + sep + "P Passes" + sep + "S Passes" + sep + "MB Passes" + sep
                + "Sim Length" + sep + "B Timestep" + sep + "D Timestep" + sep + "Input Width" + sep + "Start Pop" + sep
                + "End Pop" + sep + "Peak Pop" + sep + "Start Date" + sep + "End Date" + sep + "Run time" + sep
                + "Results Directory";
    }

}
