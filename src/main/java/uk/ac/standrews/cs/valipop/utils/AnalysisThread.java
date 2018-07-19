package uk.ac.standrews.cs.valipop.utils;

import uk.ac.standrews.cs.valipop.implementations.OBDModel;
import uk.ac.standrews.cs.valipop.implementations.StatsException;
import uk.ac.standrews.cs.valipop.implementations.minimaSearch.Control;
import uk.ac.standrews.cs.valipop.implementations.minimaSearch.MinimaSearch;
import uk.ac.standrews.cs.valipop.statistics.analysis.simulationSummaryLogging.SummaryRow;
import uk.ac.standrews.cs.valipop.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static uk.ac.standrews.cs.valipop.implementations.minimaSearch.Minimise.GEEGLM;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AnalysisThread extends Thread {

    private String runPurpose;
    Integer maxBirthingAge;
    SummaryRow summaryRow;
    Path resultsSummaryPath;
    String ctPath;
    String runPath;

    public AnalysisThread(OBDModel model, String runPurpose) {
        this.runPurpose = runPurpose;
        maxBirthingAge = model.getDesiredPopulationStatistics()
                .getOrderedBirthRates(new YearDate(0)).getLargestLabel().getValue();
        summaryRow = model.getSummaryRow();
        resultsSummaryPath = Paths.get(FileUtils.getResultsSummaryPath().toString());
        ctPath = new String(FileUtils.getContingencyTablesPath().toString());
        runPath = new String(FileUtils.getRunPath().toString());
    }

    @Override
    public void run() {

        ProgramTimer statsTimer = new ProgramTimer();

        double v = 0;
        try {
            v = MinimaSearch.getV(GEEGLM, maxBirthingAge, runPurpose, Control.BF, ctPath, runPath, summaryRow.getStartTime());
        } catch (IOException | StatsException e) {
            System.err.println("Error in AnalysisThread");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        summaryRow.setV(v);
        summaryRow.setStatsRunTime(statsTimer.getRunTimeSeconds());

        summaryRow.outputSummaryRowToFile(resultsSummaryPath);

    }

}
