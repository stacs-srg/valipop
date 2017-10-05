package uk.ac.standrews.cs.digitising_scotland.verisim.implementations;

import uk.ac.standrews.cs.digitising_scotland.verisim.Config;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.CTtree;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.DoubleComparer;
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
import java.util.LinkedList;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MinimaSearch {

    static double startBF;
    static double step;

    static double maxAbsBF;

    public static void main(String[] args) throws StatsException, IOException, InvalidInputFileException {

        switch(args[0]) {

            case "A":
                runSearch(5200000, "src/main/resources/scotland_test_population", 0.0, 0.5, "minima-scot-b", 3);
                break;
            case "B":
                runSearch(1850000, "src/main/resources/proxy-scotland-population-JA", 0.0, 0.5, "minima-ja-b", 3);
                break;
            }

    }

    @SuppressWarnings("Duplicates")
    private static void runSearch(int populationSize, String dataFiles, double startBF, double step, String runPurpose, int repeatRuns) throws IOException, InvalidInputFileException, StatsException {

        MinimaSearch.startBF = startBF;
        MinimaSearch.step = step;

        double rf = 0.5;
        CompoundTimeUnit iw = new CompoundTimeUnit(40, TimeUnit.YEAR);
        int minBirthSpacing = 147;
        double maxInfid = 0.2;
        double df = 0.0;
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

                int n = 0;
                double totalV = 0.0;

                for( ; n < repeatRuns; n++) {

                    if(n == 1) {
                        CTtree.reuseExpectedValues(true);
                    }

                    String startTime = FileUtils.getDateTime();
                    OBDModel.setUpFileStructureAndLogs(runPurpose, startTime, results_save_location);

                    Config config = new Config(tS, t0, tE, populationSize, set_up_br, set_up_dr,
                            simulation_time_step, dataFiles, results_save_location, runPurpose,
                            minBirthSpacing, maxInfid, bf, df, rf, iw, output_record_format, startTime);

                    OBDModel model = new OBDModel(startTime, config);
                    model.runSimulation();
                    model.analyseAndOutputPopulation(false);
                    double v = getV(FileUtils.getContingencyTablesPath().toString(), model.getDesiredPopulationStatistics().getOrderedBirthRates(new YearDate(0)).getLargestLabel().getValue());

                    v = v / model.getPopulation().getPopulationCounts().getCreatedPeople() * 1E6;

                    model.getSummaryRow().setV(v);
                    model.getSummaryRow().outputSummaryRowToFile();

                    totalV += v;

                }

                double avgV = totalV / n;

                logBFtoV(bf, avgV);

                CTtree.reuseExpectedValues(false);

            }
        } catch (IOException e) {
            String message = "Model failed due to Input/Output exception, check that this program has " +
                    "permission to read or write on disk. Also, check supporting input files are present at location " +
                    "specified in config setup code : " + e.getMessage();
            throw new IOException(message, e);
        } catch (InvalidInputFileException e) {
            String message = "Model failed due to an invalid formatting/content of input file, see message: " + e.getMessage();
            throw new InvalidInputFileException(message, e);
        } catch (StatsException e) {
            String message = "Stats failure - could not execute RScript command - do you have R installed?";
            throw new StatsException(message);
        }

    }

//    private static boolean jumpingPhase = false;
//
//    private static double jumpOut() {
//        // called when minima found
//        jumpingPhase = true;
//
//
//
//        return
//
//    }

    private static double getNextBFValue() {

        if(points.size() == 0) {
            System.out.println("Next BF : " + startBF);
            return startBF;
        } else if(points.size() == 1) {
            System.out.println("Next BF : " + (startBF + step));
            return startBF + step;
        } else {

            BFVPoint penultimatePoint = points.get(points.size() - 2);
            BFVPoint lastPoint = points.get(points.size() - 1);

            double dyOverdx = (lastPoint.y_v - penultimatePoint.y_v) / (lastPoint.x_bf - penultimatePoint.x_bf);

            double newBF;

            double direction = dyOverdx / Math.abs(dyOverdx);

            if(!DoubleComparer.equal(0, dyOverdx, 0.0000001)) {
                // if sloped

                newBF = lastPoint.x_bf - (step * direction);

            } else {
                // if flat line

                newBF = penultimatePoint.x_bf - (penultimatePoint.x_bf - lastPoint.x_bf) / 2;
                step = step / 2;

            }

            BFVPoint match = containsValue(points, newBF);

            if(match == null) {
                System.out.println("Next BF : " + newBF);
                return newBF;
            } else {

                if(match.y_v > lastPoint.y_v) {
                    // up slope to match point - thus split
                    step = step / 2;
                    newBF = lastPoint.x_bf + step;
                } else if(match.y_v < lastPoint.y_v) {
                    // down slope to match point - thus jump
                    points.remove(match);
                    points.addLast(match);
                    newBF = match.x_bf - (step * direction);

                    BFVPoint match2 = containsValue(points, newBF);

                    if(match2 != null) {
                        // if newBF has already been used, then split in gap
                        step = step / 2;
                        newBF = match.x_bf - (step * direction);
                    }

                } else {
                    // if equal v
                    step = step / 2;
                    newBF = lastPoint.x_bf + step;
                }

            }

            System.out.println("Next BF : " + newBF);
            return newBF;
        }
    }

    private static BFVPoint containsValue(LinkedList<BFVPoint> points, double newBF) {

        for(BFVPoint point : points) {
            if(DoubleComparer.equal(point.x_bf, newBF, 0.000000001)) {
                return point;
            }
        }

        return null;
    }

//    private static PriorityQueue

    static LinkedList<BFVPoint> points = new LinkedList<>();

    private static void logBFtoV(double bf, double v) {

        points.addLast(new BFVPoint(bf, v));

    }


    private static double getV(String pathOfTablesDir, int maxBirthingAge) throws StatsException, IOException {

        String[] commands = {"Rscript", "src/main/resources/analysis-r/geeglm/dev-minima-search.R", pathOfTablesDir, String.valueOf(maxBirthingAge)};
        ProcessBuilder pb = new ProcessBuilder(commands);

        Process proc;

        int count = 0;
        String result = null;

        proc = pb.start();

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        String s = null;

        while((s = stdInput.readLine())!=null) {
            result = s;
            count ++;
        }

        if(count != 1) {
            throw new StatsException();
        }

        String[] res = result.split(" ");

        return Double.parseDouble(res[1]);

    }

}
