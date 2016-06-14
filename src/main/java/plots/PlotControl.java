package plots;

import plots.statgraphics.util.PlotFrame;
import plots.statgraphics.util.PlotFrameFactory;

import java.util.ArrayList;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PlotControl {

    private static ArrayList<PlotFrame> frames = new ArrayList<PlotFrame>();

    public static void addPlotFrame(PlotFrame plotFrame) {
        frames.add(plotFrame);
    }

    public static void showPlots() {
        PlotFrame[] pf = frames.toArray(new PlotFrame[frames.size()]);
        new PlotFrameFactory().putPlotFrame(pf);
    }

}
