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
package uk.ac.standrews.cs.valipop.utils;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.population.OBDModel;
import uk.ac.standrews.cs.valipop.population.StatsException;
import uk.ac.standrews.cs.valipop.population.minimaSearch.Control;
import uk.ac.standrews.cs.valipop.population.minimaSearch.MinimaSearch;
import uk.ac.standrews.cs.valipop.statistics.analysis.simulationSummaryLogging.SummaryRow;

import java.io.IOException;
import java.time.Year;

import static uk.ac.standrews.cs.valipop.population.minimaSearch.Minimise.GEEGLM;

/**
 * Invokes R analysis code in an asynchronous thread.
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AnalysisThread extends Thread {

    private final int maxBirthingAge;
    private final SummaryRow summaryRow;

    @SuppressWarnings("unused")
    private int threadCount;

    private final Config config;

    public AnalysisThread(final OBDModel model, final Config config, final int threadCount) {

        this.config = config;
        this.threadCount = threadCount;

        maxBirthingAge = model.getDesiredPopulationStatistics().getOrderedBirthRates(Year.of(0)).getLargestLabel().getValue();
        summaryRow = model.getSummaryRow();
    }

    @Override
    public void run() {

        threadCount++;

        final ProgramTimer statsTimer = new ProgramTimer();

        double v = 99999;
        try {
            v = MinimaSearch.getScore(GEEGLM, maxBirthingAge, Control.RF, config);
        } catch (final IOException | StatsException e) {

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
