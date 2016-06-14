package plots.survival;

import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import org.jfree.chart.JFreeChart;
import plots.PlotControl;
import plots.statgraphics.GraphicalAnalysis;
import plots.statgraphics.survival.SurvivalEstimatePlot;
import plots.statgraphics.util.PlotFrame;
import plots.statgraphics.util.PlotFrameFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import static plots.statgraphics.util.Argument.DATA_NAMES;


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

    public void generatePlot() {

        double[] sortedTimeObserved;
        double[] sortedTimeExpected;
        double[] sortedSurvivalEstimateObserved;
        double[] sortedSurvivalEstimateExpected;

        sortedTimeObserved = getTimeDivisions(observed);
        sortedTimeExpected = getTimeDivisions(expected);

        sortedSurvivalEstimateObserved = getSurvivalEstimates(observed);
        sortedSurvivalEstimateExpected = getSurvivalEstimates(expected);


        String[] names = new String[]{"Observed", "Expected"};

        PlotControl.addPlotFrame(new PlotFrame("Kaplan-Meier Estimate Plot II",
                new SurvivalEstimatePlot(names,
                        new double[][]{sortedTimeObserved, sortedTimeExpected},
                        new double[][]{sortedSurvivalEstimateObserved, sortedSurvivalEstimateExpected}).plot, 500, 270));

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
