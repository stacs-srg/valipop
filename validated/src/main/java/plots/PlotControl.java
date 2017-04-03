package plots;

import plots.statgraphics.util.PlotFrame;
import plots.statgraphics.util.PlotFrameFactory;
import plots.statgraphics.util.SavePlot;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PlotControl {

    private static ArrayList<PlotFrame> frames = new ArrayList<PlotFrame>();
    private static ArrayList<SavePlot> save = new ArrayList<SavePlot>();

    public static void addPlotFrame(PlotFrame plotFrame) {
        frames.add(plotFrame);
    }

    public static void showPlots() {
        PlotFrame[] pf = frames.toArray(new PlotFrame[frames.size()]);
        new PlotFrameFactory().putPlotFrame(pf);

    }

//    public static void addSavePlot(SavePlot plot) {
//        save.add(plot);
//    }
//
//    public static void savePlots() {
//
//        for(SavePlot plot : save) {
//            plot.savePlotAsSVG(new File(plot.));
//        }
//    }

}
