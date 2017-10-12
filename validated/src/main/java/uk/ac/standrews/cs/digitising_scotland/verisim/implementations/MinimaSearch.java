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
import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MinimaSearch {

    static double startBF;
    static double step;

    static double initStep;

    static double maxAbsBF = 3;

    static double topSearchBoundBF = maxAbsBF;
    static double bottomSearchBoundBF = -1 * maxAbsBF;

    static double hardLimitBottomBoundBF = -1 * Double.MAX_VALUE;

    static int pointInMinima = 3;

    static double minimumMeaningfulStep = 0.004;
    static double minimaSize = 5;
    static double intervalBoundV = 0.02;

    static Random rand = new Random();

    public static void main(String[] args) throws StatsException, IOException, InvalidInputFileException {

        step = 0.5;
        initStep = step;


//        try {
//
//            while(true) {
//                double bf = getNextBFValue();
//                Scanner scanner = new Scanner(System.in);
//                String v = scanner.nextLine();
//                logBFtoV(bf, new Double(v));
//                inMinima(bf);
//            }
//
//
//        } catch (SpaceExploredException e) {
//            System.out.println(e.getMessage());
//        }
        try {

//            runSearch(50000, "src/main/resources/scotland_test_population", 0.0, 0.5, "minima-test", 1);

            switch(args[0]) {
                case "A":
                    runSearch(5200000, "src/main/resources/scotland_test_population", 0.0, 0.5, "minima-scot-f", 3);
                    break;
                case "B":
                    runSearch(2500000, "src/main/resources/proxy-scotland-population-JA", -1.0, 0.5, "minima-ja-f", 3);
                    break;
            }

        } catch (SpaceExploredException e) {
            System.out.println("Space explored - check the results logs!");
        } catch (PreEmptiveOutOfMemoryWarning | OutOfMemoryError e) {
            System.out.println("Ran out of memory - not enough memory for 0 bf - increase JVM heap size using -Xmx argument");
        }

    }

    @SuppressWarnings("Duplicates")
    private static void runSearch(int populationSize, String dataFiles, double startBF, double step, String runPurpose, int repeatRuns) throws IOException, InvalidInputFileException, StatsException, SpaceExploredException, PreEmptiveOutOfMemoryWarning {

        MinimaSearch.startBF = startBF;
        MinimaSearch.step = step;
        MinimaSearch.initStep = step;

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

                    try {
                        model.runSimulation();
                        model.analyseAndOutputPopulation(false);
                        CTtree.clearStatNodeIfNessersary();
                        double v = getV(FileUtils.getContingencyTablesPath().toString(), model.getDesiredPopulationStatistics().getOrderedBirthRates(new YearDate(0)).getLargestLabel().getValue());

                        v = v / model.getPopulation().getPopulationCounts().getCreatedPeople() * 1E6;

                        model.getSummaryRow().setV(v);
                        model.getSummaryRow().outputSummaryRowToFile();

                        totalV += v;
                    } catch (PreEmptiveOutOfMemoryWarning | OutOfMemoryError e) {

                        hardLimitBottomBoundBF = bf + 0.1;
                        bottomSearchBoundBF = hardLimitBottomBoundBF;

                        if(bottomSearchBoundBF > topSearchBoundBF) {
                            throw new Error("Bottom bound larger then top bound - resulting from adaptions made due to memory limitations - try to increase JVm heap size (-Xmx) or reduce population size");
                        }

//                        if(bf < 0) {
//                            bottomSearchBoundBF = bf + 0.1;
//                        } else if(bf > 0) {
//                            topSearchBoundBF = bf - 0.1;
//                        } else {
//                            // bf == 0 and failing
//                            throw e;
//                        }
                        jumpingPhase = true;
                        model.getSummaryRow().setCompleted(false);
                        MemoryUsageAnalysis.reset();
                        model.getSummaryRow().setMaxMemoryUsage(MemoryUsageAnalysis.getMaxSimUsage());
                        model.getSummaryRow().outputSummaryRowToFile();
                        CTtree.clearStatNodeIfNessersary();
                        break;
                    }

                }

                Double avgV = totalV / n;

                if(!avgV.isNaN()) {
                    logBFtoV(bf, avgV);
                    inMinima(bf);
                }

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

    private static boolean jumpingPhase = false;

    private static double jumpOut() throws SpaceExploredException {
        // called when minima found

        step = initStep;

        int options = new Double(topSearchBoundBF - bottomSearchBoundBF / (initStep / 2)).intValue();

        double chosenBF;

        int counter = 0;

        do {

            if(counter > options) {
                throw new SpaceExploredException();
            }

            int chosen = rand.nextInt(options);

            chosenBF = chosen * (initStep / 2) + bottomSearchBoundBF;

            counter ++;

        } while ( containsValue(points, chosenBF) != null );

        jumpingPhase = false;

        BFVPoint nearestBF = getNearestPoint(chosenBF);
        points.remove(nearestBF);
        points.addLast(nearestBF);

        return chosenBF;

    }

    private static BFVPoint getNearestPoint(double chosenBF) {

        double minDistance = Double.MAX_VALUE;
        BFVPoint nearest = null;

        for(BFVPoint p : points) {
            double distance = Math.abs(p.x_bf - chosenBF);
            if(distance < minDistance) {
                minDistance = distance;
                nearest = p;
            }
        }

        return nearest;
    }

    private static Double inMinima(double currentBF) {

        if(points.size() >= pointInMinima) {
            // get two nearest neighbours on either side
            ArrayList<BFVPoint> consideredPoints = getNearestBFNeigbours(pointInMinima - 1, currentBF);
            ArrayList<BFVPoint> returns = new ArrayList<>();

            for(int i = 0; i < pointInMinima; i++) {

                List<BFVPoint> l;
                if(i + pointInMinima - 1 < consideredPoints.size()) {
                    l = consideredPoints.subList(i, i + pointInMinima);
                } else {
                    break;
                }

                BFVPoint ret = constitutesMinima(l);

                if(ret != null) {
                    returns.add(ret);
                }

            }

            if(returns.size() == 0) {
                return null;
            } else {
                BFVPoint minima = orderByV(returns).get(0);
                System.out.println("Minima found at: " + minima.x_bf + " --- v/M: " + minima.y_v);
                return minima.x_bf;
            }
        }

        return null;

    }

    // returns bf of minima
    public static BFVPoint constitutesMinima(List<BFVPoint> potentialMinimaSet) {

        if(potentialMinimaSet.size() != pointInMinima) {
            return null;
        }

        double bfWidth = Math.abs(potentialMinimaSet.get(0).x_bf - potentialMinimaSet.get(pointInMinima - 1).x_bf);

        if(bfWidth < minimumMeaningfulStep * minimaSize) {

            ArrayList<BFVPoint> orderedByV = orderByV(potentialMinimaSet);

            double avg = averageV(orderedByV);

            double lowerBound = avg * (1 - intervalBoundV);
            double upperBound = avg * (1 + intervalBoundV);

            if(lowerBound < orderedByV.get(0).y_v && orderedByV.get(pointInMinima - 1).y_v < upperBound) {
                jumpingPhase = true;
                return orderedByV.get(0);
            }

        }

        return null;

    }

    private static double averageV(List<BFVPoint> in) {

        double sum = 0.0;

        for(BFVPoint p : in) {
            sum += p.y_v;
        }

        return sum / in.size();

    }

    private static ArrayList<BFVPoint> orderByBF(List<BFVPoint> in) {
        ArrayList<BFVPoint> ordering = new ArrayList<>(in.size());

        for(BFVPoint p : in) {

            if(ordering.size() == 0) {
                ordering.add(p);
            } else {
                int i = 0;
                for(BFVPoint o : ordering) {
                    if(p.x_bf < o.x_bf) {
                        ordering.add(i, p);
                        break;
                    }
                    i++;
                }
                if(i == ordering.size()) {
                    ordering.add(p);
                }
            }
        }
        return ordering;
    }

    private static ArrayList<BFVPoint> orderByV(List<BFVPoint> in) {
        ArrayList<BFVPoint> ordering = new ArrayList<>(in.size());

        for(BFVPoint p : in) {

            if(ordering.size() == 0) {
                ordering.add(p);
            } else {
                int i = 0;
                for(BFVPoint o : ordering) {
                    if(p.y_v < o.y_v) {
                        ordering.add(i, p);
                        break;
                    }
                    i++;
                }
                if(i == ordering.size()) {
                    ordering.add(p);
                }

            }
        }
        return ordering;
    }

    private static ArrayList<BFVPoint> getNearestBFNeigbours(int width, double bf) {
        ArrayList<BFVPoint> selected = new ArrayList<>(width * 2 + 1);

        ArrayList<BFVPoint> ordering = orderByBF(points);

        int c = 0;
        for(BFVPoint p : ordering) {

            if(p.x_bf == bf) {

                if(c < width) {
                    for(int i = 0; i < c; i++) {
                        selected.add(ordering.get(i));
                    }
                } else {
                    for(int i = 0; i < width; i++) {
                        selected.add(ordering.get(c - width + i));
                    }
                }

                selected.add(p);

                if(c >= points.size() - width) {
                    for(int i = 0; i < points.size() - c; i++) {
                        selected.add(ordering.get(i + c));
                    }
                } else {
                    for(int i = 0; i < width; i++) {
                        selected.add(ordering.get(c + i + 1));
                    }
                }

            }
            c++;
        }


        return selected;
    }

    private static double getNextBFValue() throws SpaceExploredException {

        if(jumpingPhase) {
            double nextBF = jumpOut();
            System.out.println("Next BF : " + nextBF);
            return nextBF;
        }

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

            if(match != null) {

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

                    while(match2 != null) {
                        // if newBF has already been used, then split in gap
                        step = step / 2;
                        newBF = match.x_bf - (step * direction);
                        match2 = containsValue(points, newBF);
                    }

                } else {
                    // if equal v
                    step = step / 2;
                    newBF = lastPoint.x_bf + step;
                }

            }

            System.out.println("Next BF : " + newBF);

            if(newBF < bottomSearchBoundBF || newBF > topSearchBoundBF) {
                // we're out of bounds
                // the fact we're here means there may be a lower minima out width the specified search area.
                // if we can extend them and we havn't seen something to suggest we won't overrun the heap then we'll extend the bounds

                // otherwise lets continue the minima search at a random location - but jumping out


                if(newBF < 0) {
                    if(DoubleComparer.equal(bottomSearchBoundBF, hardLimitBottomBoundBF, 0.0000001)) {
                        bottomSearchBoundBF -= step * 2;
                        System.out.println("Suspected Minima out of search bounds - extending bottom search bound to : " + bottomSearchBoundBF);
                    } else {
                        System.out.println("Suspected Minima out of search bounds beyond : " + bottomSearchBoundBF);
                        System.out.println("Cannot extend search bound due to hard limit placed due to lack of heap space in earlier run - increase heap space to explore this area");
                        return jumpOut();
                    }
                } else {
                        topSearchBoundBF += step * 2;
                        System.out.println("Suspected Minima out of search bounds - extending top search bound to : " + topSearchBoundBF);
                }
            }

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
