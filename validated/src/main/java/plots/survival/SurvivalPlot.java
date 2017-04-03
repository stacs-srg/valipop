package plots.survival;

import config.Config;
import datastructure.summativeStatistics.generated.EventType;
import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import org.jfree.chart.ChartUtilities;
import plots.statgraphics.survival.SurvivalEstimatePlot;
import utils.time.Date;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;


/**
 * Extended from example provided at: http://www2.thu.edu.tw/~wenwei/statgraphics/doc/index.html?statgraphics/survival/SurvivalEstimatePlot.html
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SurvivalPlot {

    private OneDimensionDataDistribution expected;
    private OneDimensionDataDistribution observed;

    public SurvivalPlot(OneDimensionDataDistribution expected, OneDimensionDataDistribution observed) {
        this.expected = expected;
        this.observed = observed;
    }

    public void generatePlot(EventType event, Date d, Config config) throws IOException {

        double[] sortedTimeObserved;
        double[] sortedTimeExpected;
        double[] sortedSurvivalEstimateObserved;
        double[] sortedSurvivalEstimateExpected;

        sortedTimeObserved = getTimeDivisions(observed);
        sortedTimeExpected = getTimeDivisions(expected);

        sortedSurvivalEstimateObserved = getSurvivalEstimates(observed);
        sortedSurvivalEstimateExpected = getSurvivalEstimates(expected);


        String[] names = new String[]{"Observed", "Expected"};

        ChartUtilities.saveChartAsPNG(new File(Paths.get(config.getSavePathPlots().toString(), event.toString()).toString(), d.toOrderableString() + ".png"),
                new SurvivalEstimatePlot(names,
                        new double[][]{sortedTimeObserved, sortedTimeExpected},
                        new double[][]{sortedSurvivalEstimateObserved, sortedSurvivalEstimateExpected}).plot, 1680, 1050);
//
//        ChartUtilities.saveChartAsPNG(new File(Paths.get(config.getSavePathPlots().rowAsString(), event.rowAsString()).rowAsString(), d.toOrderableString() + "B.png"),
//                new SurvivalEstimatePlot(names,
//                        new double[][]{sortedTimeExpected},
//                        new double[][]{sortedSurvivalEstimateExpected}).plot, 1680, 1050);


//        PlotControl.addPlotFrame(new PlotFrame("Kaplan-Meier Estimate ",
//                new SurvivalEstimatePlot(names,
//                        new double[][]{sortedTimeObserved, sortedTimeExpected},
//                        new double[][]{sortedSurvivalEstimateObserved, sortedSurvivalEstimateExpected}).plot, 1680, 1050));

    }

    private double[] getSurvivalEstimates(OneDimensionDataDistribution observed) {

        try {
            Map<IntegerRange, Double> data = observed.getData();

            IntegerRange[] iRS = data.keySet().toArray(new IntegerRange[observed.getData().keySet().size()]);
            Arrays.sort(iRS, IntegerRange::compareTo);

            double[] estimates = new double[iRS.length];
            int i = 0;

            for (IntegerRange iR : iRS) {
                estimates[i++] = data.get(iR);
            }

            return estimates;
        } catch (NullPointerException e) {
            return new double[0];
        }

    }

    private double[] getTimeDivisions(OneDimensionDataDistribution observed) {

        try {
            Map<IntegerRange, Double> data = observed.getData();
            IntegerRange[] iRS = data.keySet().toArray(new IntegerRange[observed.getData().keySet().size()]);
            Arrays.sort(iRS, IntegerRange::compareTo);

            double[] times = new double[iRS.length];
            int i = 0;

            for (IntegerRange iR : iRS) {
                times[i++] = iR.getValue();
            }

            return times;
        } catch (NullPointerException e) {
            return new double[0];
        }

    }

}
