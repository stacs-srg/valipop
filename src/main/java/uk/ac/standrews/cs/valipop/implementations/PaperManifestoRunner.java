package uk.ac.standrews.cs.valipop.implementations;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PaperManifestoRunner {


    public static void main(String[] args) {

        String resultsPath = "/home/tsd4/population-model/src/main/resources/valipop/results/";
        String configPath = "/home/tsd4/population-model/src/main/resources/valipop/config/scot/paper/";

        CL_RunNModels.runNModels(new String[]{configPath + "config-7812.txt", resultsPath, "PAPER-MANI", "100"});
        CL_RunNModels.runNModels(new String[]{configPath + "config-15625.txt", resultsPath, "PAPER-MANI", "100"});
        CL_RunNModels.runNModels(new String[]{configPath + "config-31250.txt", resultsPath, "PAPER-MANI", "50"});
        CL_RunNModels.runNModels(new String[]{configPath + "config-62500.txt", resultsPath, "PAPER-MANI", "50"});
        CL_RunNModels.runNModels(new String[]{configPath + "config-125000.txt", resultsPath, "PAPER-MANI", "50"});
        CL_RunNModels.runNModels(new String[]{configPath + "config-250000.txt", resultsPath, "PAPER-MANI", "10"});
        CL_RunNModels.runNModels(new String[]{configPath + "config-500000.txt", resultsPath, "PAPER-MANI", "10"});


    }

}
