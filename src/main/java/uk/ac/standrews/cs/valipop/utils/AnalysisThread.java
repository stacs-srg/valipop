package uk.ac.standrews.cs.valipop.utils;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.implementations.FactorSearch;
import uk.ac.standrews.cs.valipop.implementations.OBDModel;
import uk.ac.standrews.cs.valipop.implementations.StatsException;
import uk.ac.standrews.cs.valipop.implementations.minimaSearch.Control;
import uk.ac.standrews.cs.valipop.implementations.minimaSearch.MinimaSearch;
import uk.ac.standrews.cs.valipop.statistics.analysis.simulationSummaryLogging.SummaryRow;

import java.io.IOException;
import java.time.Year;

import static uk.ac.standrews.cs.valipop.implementations.minimaSearch.Minimise.GEEGLM;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AnalysisThread extends Thread {

    private int maxBirthingAge;
    private SummaryRow summaryRow;
    private int threadCount;

    private final Config config;

    public AnalysisThread(OBDModel model, Config config, int threadCount) {

        this.config = config;
        this.threadCount = threadCount;

        maxBirthingAge = model.getDesiredPopulationStatistics().getOrderedBirthRates(Year.of(0)).getLargestLabel().getValue();
        summaryRow = model.getSummaryRow();
    }

    @Override
    public void run() {

        threadCount++;

        ProgramTimer statsTimer = new ProgramTimer();

        double v = 99999;
        try {
            v = MinimaSearch.getV(GEEGLM, maxBirthingAge, Control.BF, config);
        } catch (IOException | StatsException e) {

            System.err.println("Error in AnalysisThread");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        summaryRow.setV(v);
        summaryRow.setStatsRunTime(statsTimer.getRunTimeSeconds());

        summaryRow.outputSummaryRowToFile();

        threadCount--;
    }
}
