package uk.ac.standrews.cs.valipop.implementations;

import java.io.IOException;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PaperManifestoRunner {

    public static void main(String[] args) throws IOException, StatsException {

        String resultsPath = "/home/tsd4/population-model/src/main/resources/valipop/results/";
        String configPath = "/home/tsd4/population-model/src/main/resources/valipop/config/scot/paper/";

        CL_RunNModels.runNModels(new String[]{configPath + "config-250000.txt", resultsPath, "PAPER-MANI", "8"});
        CL_RunNModels.runNModels(new String[]{configPath + "config-500000.txt", resultsPath, "PAPER-MANI", "10"});
    }
}
