package validation;

import utils.time.CompoundTimeUnit;
import utils.time.Date;

import java.nio.file.Path;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SummaryRow {

    private String startTime;
    private String reason;

    private int totalPop;
    private double passed;
    private double completed;
    private double bPasses;
    private double mDPasses;
    private double fDPasses;
    private double pPasses;
    private double sPasses;
    private double mBPasses;
    private int simLength;

    private CompoundTimeUnit bTimestep;
    private CompoundTimeUnit dTimestep;
    private CompoundTimeUnit inputWidth;

    private int startPop;

    private int endPop;
    private int peakPop;

    private Date startDate;
    private Date endDate;

    private int runTime;

    private Path resultsDirectory;

    public SummaryRow(String startTime, String reason, int totalPop, double passed, double completed, double bPasses,
                      double mDPasses, double fDPasses, double pPasses, double sPasses, double mBPasses, int simLength,
                      CompoundTimeUnit bTimestep, CompoundTimeUnit dTimestep, CompoundTimeUnit inputWidth, int startPop,
                      int endPop, int peakPop, Date startDate, Date endDate, int runTime, Path resultsDirectory) {

        this.startTime = startTime;
        this.reason = reason;
        this.totalPop = totalPop;
        this.passed = passed;
        this.completed = completed;
        this.bPasses = bPasses;
        this.mDPasses = mDPasses;
        this.fDPasses = fDPasses;
        this.pPasses = pPasses;
        this.sPasses = sPasses;
        this.mBPasses = mBPasses;
        this.simLength = simLength;
        this.bTimestep = bTimestep;
        this.dTimestep = dTimestep;
        this.inputWidth = inputWidth;
        this.startPop = startPop;
        this.endPop = endPop;
        this.peakPop = peakPop;
        this.startDate = startDate;
        this.endDate = endDate;
        this.runTime = runTime;
        this.resultsDirectory = resultsDirectory;

    }

    public String toSeperatedString(char sep) {
        return startTime + sep + reason + sep + totalPop + sep + passed + sep + completed + sep + bPasses + sep +
                mDPasses + sep + fDPasses + sep + pPasses + sep + sPasses + sep + mBPasses + sep + simLength + sep +
                bTimestep + sep + dTimestep + sep + inputWidth + sep + startPop + sep + endPop + sep + peakPop + sep +
                startDate + sep + endDate + sep + runTime + sep + resultsDirectory;
    }

}
