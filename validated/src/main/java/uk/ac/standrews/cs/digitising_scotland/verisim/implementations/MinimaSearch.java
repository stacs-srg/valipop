package uk.ac.standrews.cs.digitising_scotland.verisim.implementations;

import uk.ac.standrews.cs.digitising_scotland.verisim.Config;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.fileUtils.InvalidInputFileException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.sourceEventRecords.RecordFormat;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.timeSteps.TimeUnit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.PriorityQueue;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MinimaSearch {

    @SuppressWarnings("Duplicates")
    private static void runSearch(int size, String dataFiles) throws IOException, InvalidInputFileException {


        double rf = 0.5;
        CompoundTimeUnit iw = new CompoundTimeUnit(40, TimeUnit.YEAR);
        int minBirthSpacing = 147;
        double maxInfid = 0.2;
        double df = 0.0;
        String runPurpose = "minima-search";
        String results_save_location = "src/main/resources/results/";

        CompoundTimeUnit simulation_time_step = new CompoundTimeUnit(1, TimeUnit.YEAR);
        AdvancableDate tS = new YearDate(1599);
        AdvancableDate t0 = new YearDate(1855);
        AdvancableDate tE = new YearDate(2015);
        double set_up_br = 0.0133;
        double set_up_dr = 0.0122;

        RecordFormat output_record_format = RecordFormat.NONE;


        try {

            while(true) {

                double bf = getNextBFValue();

                String startTime = FileUtils.getDateTime();
                OBDModel.setUpFileStructureAndLogs(runPurpose, startTime, results_save_location);

                Config config = new Config(tS, t0, tE, size, set_up_br, set_up_dr,
                        simulation_time_step, dataFiles, results_save_location, runPurpose,
                        minBirthSpacing, maxInfid, bf, df, rf, iw, output_record_format, startTime);

                OBDModel model = new OBDModel(startTime, config);
                model.runSimulation();
                model.analyseAndOutputPopulation();



            }
        } catch (IOException e) {
            String message = "Model failed due to Input/Output exception, check that this program has " +
                    "permission to read or write on disk. Also, check supporting input files are present at location " +
                    "specified in config setup code : " + e.getMessage();
            throw new IOException(message, e);
        } catch (InvalidInputFileException e) {
            String message = "Model failed due to an invalid formatting/content of input file, see message: " + e.getMessage();
            throw new InvalidInputFileException(message, e);
        }

    }

    private static double getNextBFValue() {
        return 0;
    }

//    private static PriorityQueue

    private static void logBFtoV(double bf, double v) {

    }


    private static int getV(String pathOfTablesDir, int maxBirthingAge) throws IOException, StatsException {

        Runtime rt = Runtime.getRuntime();
        String[] commands = {"src/main/resources/analysis-r/geeglm/dev-minima-search.R", pathOfTablesDir, String.valueOf(maxBirthingAge)};
        Process proc = rt.exec(commands);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        int count = 0;

        String result = null;

        String s = null;
        while((s = stdInput.readLine())!=null) {
            result = s;
            count ++;
        }


        if(count != 1) {
            throw new StatsException();
        }

        String[] res = result.split(" ");

        return Integer.parseInt(res[1]);

    }

}
