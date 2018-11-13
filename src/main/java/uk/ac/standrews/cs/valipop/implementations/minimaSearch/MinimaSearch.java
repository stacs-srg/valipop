package uk.ac.standrews.cs.valipop.implementations.minimaSearch;

import uk.ac.standrews.cs.valipop.statistics.distributions.general.InconsistentWeightException;
import uk.ac.standrews.cs.valipop.implementations.*;
import uk.ac.standrews.cs.valipop.utils.DoubleComparer;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.utils.ProcessArgs;
import uk.ac.standrews.cs.valipop.utils.ProgramTimer;
import uk.ac.standrews.cs.valipop.utils.RCaller;
import uk.ac.standrews.cs.valipop.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.valipop.utils.fileUtils.InvalidInputFileException;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordFormat;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvanceableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MinimaSearch {

    static double startFactor;
    static double step;

    static double initStep;

    static double maxAbsFactor = 4;

    static double topSearchBoundFactor = maxAbsFactor;
    static double bottomSearchBoundFactor = -1 * maxAbsFactor;

    static double hardLimitBottomBoundFactor = -1 * Double.MAX_VALUE;

    static int pointsInMinima = 3;

    static double minimumMeaningfulStep = 0.04;
    static double minimaSize = 5;
    static double intervalBoundV = 0.02;

    static Random rand = new Random();

    static String results_save_location = "src/main/resources/valipop/results/";

    static CompoundTimeUnit simulation_time_step = new CompoundTimeUnit(1, TimeUnit.YEAR);
    static AdvanceableDate tS = new YearDate(1691);
    static AdvanceableDate t0 = new YearDate(1855);
    static AdvanceableDate tE = new YearDate(2015);
    static double set_up_br = 0.0133;
    static double set_up_dr = 0.0122;

    static double rf = 0.5;
    static double prf = 0.5;
    static CompoundTimeUnit iw = new CompoundTimeUnit(10, TimeUnit.YEAR);
    static int minBirthSpacing = 147;

    static double bf = 0.0;
    static double df = 0.0;

    static double nanAsemtote = 1E6;

    public static void main(String[] args) throws StatsException, IOException, InvalidInputFileException, InconsistentWeightException {

        String[] pArgs = ProcessArgs.process(args, "MINIMA_SEARCH");
        if (!ProcessArgs.check(pArgs, "MINIMA_SEARCH")) {
            System.err.println("Incorrect arguments given");
            throw new Error("Incorrect arguments given");
        }

        String dataFiles = pArgs[0];
        int seedSize = Integer.valueOf(pArgs[1]);
        String runPurpose = pArgs[2];
        Minimise minimise = Minimise.resolve(pArgs[3]);
        Control control = Control.resolve(pArgs[4]);
        double startFactor = Double.valueOf(pArgs[5]);
        double step = Double.valueOf(pArgs[6]);
        int repeats = Integer.valueOf(pArgs[7]);

        try {
            runSearch(seedSize, dataFiles, startFactor, step, runPurpose, repeats, minimise, control);

        } catch (SpaceExploredException e) {
            System.out.println("Space explored - check the results logs!");
        } catch (PreEmptiveOutOfMemoryWarning | OutOfMemoryError e) {
            System.out.println("Ran out of memory - not enough memory for 0 factor - increase JVM heap size using -Xmx argument");
        }
    }

    private static void runSearch(int populationSize, String dataFiles, double startFactor, double step, String runPurpose, int repeatRuns, Minimise minimiseFor, Control controlBy) throws IOException, InvalidInputFileException, StatsException, SpaceExploredException, PreEmptiveOutOfMemoryWarning, InconsistentWeightException {

        MinimaSearch.startFactor = startFactor;
        MinimaSearch.step = step;
        MinimaSearch.initStep = step;

        setControllingFactor(controlBy, startFactor);

        nanAsemtote = nanAsemtote * repeatRuns;

        RecordFormat output_record_format = RecordFormat.NONE;

        try {

            while (true) {

                setControllingFactor(controlBy, getNextFactorValue());

                int n = 0;
                double totalV = 0.0;


                for (; n < repeatRuns; n++) {

                    String startTime = FileUtils.getDateTime();
                    OBDModel.setUpFileStructureAndLogs(runPurpose, startTime, results_save_location);

                    Config config = new Config(tS, t0, tE, populationSize, set_up_br, set_up_dr,
                            simulation_time_step, dataFiles, results_save_location, runPurpose,
                            minBirthSpacing, minBirthSpacing, true, bf, df, rf, prf, iw, output_record_format, startTime, 0, false);

                    OBDModel model = new OBDModel(startTime, config);

                    try {
                        model.runSimulation();
                        model.analyseAndOutputPopulation(false);

                        Integer maxBirthingAge = model.getDesiredPopulationStatistics().getOrderedBirthRates(new YearDate(0)).getLargestLabel().getValue();
                        double v = getV(minimiseFor, maxBirthingAge, runPurpose, controlBy, model.getSummaryRow().getStartTime());

                        // Failed population run may get a NaN from the V calc
                        if (Double.isNaN(v)) {
                            v = nanAsemtote;
                        }

                        // convert to v per million people (to standardise due to varying population sizes)
                        v = v / model.getPopulation().getPopulationCounts().getCreatedPeople() * 1E6;

                        model.getSummaryRow().setV(v);
                        model.getSummaryRow().outputSummaryRowToFile();

                        if (minimiseFor != Minimise.GEEGLM) {
                            ProgramTimer statsTimer = new ProgramTimer();

                            RCaller.generateAnalysisHTML(FileUtils.getRunPath().toString(),
                                    model.getDesiredPopulationStatistics().getOrderedBirthRates(
                                            new YearDate(0)).getLargestLabel().getValue(),
                                    runPurpose + " - " + controlBy.toString() + ": "
                                            + String.valueOf(getControllingFactor(controlBy)));

                            model.getSummaryRow().setStatsRunTime(statsTimer.getRunTimeSeconds());
                        }

                        totalV += v;

                    } catch (PreEmptiveOutOfMemoryWarning | OutOfMemoryError e) {

                        handleRecoveryFromOutOfMemory(getControllingFactor(controlBy), model);

                        break;
                    }
                }

                Double avgV = totalV / n;

                if (!avgV.isNaN()) {
                    logFactortoV(getControllingFactor(controlBy), avgV);
                    inMinima(getControllingFactor(controlBy));

                } else {
                    logFactortoV(getControllingFactor(controlBy), nanAsemtote);
                }

            }
        } catch (IOException e) {
            String message = "Model failed due to Input/Output exception, check that this program has " +
                    "permission to read or write on disk. Also, check supporting input files are present at location " +
                    "specified in config setup code : " + e.getMessage();
            throw new IOException(message, e);
        } catch (StatsException e) {
            String message = "Stats failure - could not execute RScript command - do you have R installed?";
            throw new StatsException(message);
        }
    }

    public static double getV(Minimise minimiseFor, Integer maxBirthingAge, String runPurpose, Control controlBy, String startTime) throws IOException, StatsException {

        switch (minimiseFor) {

            case ALL:
                return RCaller.getV(FileUtils.getContingencyTablesPath().toString(), maxBirthingAge);
            case OB:
                return RCaller.getObV(FileUtils.getContingencyTablesPath().toString(), maxBirthingAge);
            case GEEGLM:
                String title = runPurpose + " - " + controlBy.toString() + ": " + String.valueOf(getControllingFactor(controlBy));
                return RCaller.getGeeglmV(title, FileUtils.getRunPath().toString(),
                        FileUtils.getContingencyTablesPath().toString(), maxBirthingAge, startTime);
        }

        throw new StatsException(minimiseFor + " - minimisation for this test is not implemented");
    }

    public static double getV(Minimise minimiseFor, Integer maxBirthingAge, String runPurpose, Control controlBy, String ctPath, String runPath, String startTime) throws IOException, StatsException {

        switch (minimiseFor) {

            case ALL:
                return RCaller.getV(FileUtils.getContingencyTablesPath().toString(), maxBirthingAge);
            case OB:
                return RCaller.getObV(FileUtils.getContingencyTablesPath().toString(), maxBirthingAge);
            case GEEGLM:
                String title = runPurpose + " - " + controlBy.toString() + ": " + String.valueOf(getControllingFactor(controlBy));
                return RCaller.getGeeglmV(title, runPath,
                        ctPath, maxBirthingAge, startTime);
        }

        throw new StatsException(minimiseFor + " - minimisation for this test is not implemented");
    }

    public static double getControllingFactor(Control controlBy) {

        switch (controlBy) {

            case BF:
                return bf;

            case DF:
                return df;
        }

        throw new InvalidParameterException(controlBy.toString() + " did not resolve to a known parameter");
    }

    public static void setControllingFactor(Control controlBy, double startFactor) {

        switch (controlBy) {

            case BF:
                bf = startFactor;
                break;
            case DF:
                df = startFactor;
                break;
        }
    }

    public static void handleRecoveryFromOutOfMemory(double factor, OBDModel model) {
        hardLimitBottomBoundFactor = factor + 0.1;
        bottomSearchBoundFactor = hardLimitBottomBoundFactor;

        System.out.println("Out of memory - setting jumpingPhase = true - memory usage: " + MemoryUsageAnalysis.getMaxSimUsage());
        jumpingPhase = true;

        model.getSummaryRow().setCompleted(false);
        model.getSummaryRow().setMaxMemoryUsage(MemoryUsageAnalysis.getMaxSimUsage());
        MemoryUsageAnalysis.reset();
        model.getSummaryRow().outputSummaryRowToFile();

        if (bottomSearchBoundFactor > topSearchBoundFactor) {
            throw new Error("Bottom bound larger then top bound - resulting from adaptions made due to memory limitations - try to increase JVm heap size (-Xmx) or reduce population size");
        }
    }

    private static boolean jumpingPhase = false;

    private static double jumpOut() throws SpaceExploredException {
        // called when minima found

        step = initStep;

        int options = new Double((topSearchBoundFactor - bottomSearchBoundFactor) / (initStep / 2.0)).intValue();

        double chosenFactor;

        int counter = 0;

        do {
            if (counter >= options) {
                throw new SpaceExploredException();
            }

            System.out.println("Selecting new starting point");
            System.out.println(topSearchBoundFactor);
            System.out.println(bottomSearchBoundFactor);
            System.out.println(initStep);
            System.out.println(options);
            int chosen = rand.nextInt(options);

            chosenFactor = chosen * (initStep / 2) + bottomSearchBoundFactor;

            counter++;

        } while (containsValue(points, chosenFactor) != null);

        jumpingPhase = false;

        FVPoint nearestFactor = getNearestPoint(chosenFactor);
        points.remove(nearestFactor);
        points.addLast(nearestFactor);

        return chosenFactor;
    }

    private static FVPoint getNearestPoint(double chosenFactor) {

        double minDistance = Double.MAX_VALUE;
        FVPoint nearest = null;

        for (FVPoint p : points) {
            double distance = Math.abs(p.x_f - chosenFactor);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = p;
            }
        }

        return nearest;
    }

    private static Double inMinima(double currentFactor) {

        if (points.size() >= pointsInMinima) {
            // get two nearest neighbours on either side
            ArrayList<FVPoint> consideredPoints = getNearestFactorNeigbours(pointsInMinima - 1, currentFactor);
            ArrayList<FVPoint> returns = new ArrayList<>();

            for (int i = 0; i < pointsInMinima; i++) {

                List<FVPoint> l;
                if (i + pointsInMinima - 1 < consideredPoints.size()) {
                    l = consideredPoints.subList(i, i + pointsInMinima);
                } else {
                    break;
                }

                FVPoint ret = constitutesMinima(l);

                if (ret != null) {
                    returns.add(ret);
                }
            }

            if (returns.size() == 0) {
                return null;
            } else {
                FVPoint minima = orderByV(returns).get(0);
                System.out.println("Minima found at: " + minima.x_f + " --- v/M: " + minima.y_v);
                return minima.x_f;
            }
        }

        return null;
    }

    // returns factor of minima
    public static FVPoint constitutesMinima(List<FVPoint> potentialMinimaSet) {

        if (potentialMinimaSet.size() != pointsInMinima) {
            return null;
        }

        double factorWidth = Math.abs(potentialMinimaSet.get(0).x_f - potentialMinimaSet.get(pointsInMinima - 1).x_f);

        if (factorWidth < minimumMeaningfulStep * minimaSize) {

            ArrayList<FVPoint> orderedByV = orderByV(potentialMinimaSet);

            double avg = averageV(orderedByV);

            double lowerBound = avg * (1 - intervalBoundV);
            double upperBound = avg * (1 + intervalBoundV);

            if (lowerBound < orderedByV.get(0).y_v && orderedByV.get(pointsInMinima - 1).y_v < upperBound) {
                System.out.println("Minima identified - setting jumpingPhase = true");
                jumpingPhase = true;
                return orderedByV.get(0);
            }
        }

        return null;
    }

    private static double averageV(List<FVPoint> in) {

        double sum = 0.0;

        for (FVPoint p : in) {
            sum += p.y_v;
        }

        return sum / in.size();
    }

    private static ArrayList<FVPoint> orderByFactor(List<FVPoint> in) {
        ArrayList<FVPoint> ordering = new ArrayList<>(in.size());

        for (FVPoint p : in) {

            if (ordering.size() == 0) {
                ordering.add(p);
            } else {
                int i = 0;
                for (FVPoint o : ordering) {
                    if (p.x_f < o.x_f) {
                        ordering.add(i, p);
                        break;
                    }
                    i++;
                }
                if (i == ordering.size()) {
                    ordering.add(p);
                }
            }
        }
        return ordering;
    }

    private static ArrayList<FVPoint> orderByV(List<FVPoint> in) {
        ArrayList<FVPoint> ordering = new ArrayList<>(in.size());

        for (FVPoint p : in) {

            if (ordering.size() == 0) {
                ordering.add(p);
            } else {
                int i = 0;
                for (FVPoint o : ordering) {
                    if (p.y_v < o.y_v) {
                        ordering.add(i, p);
                        break;
                    }
                    i++;
                }
                if (i == ordering.size()) {
                    ordering.add(p);
                }

            }
        }
        return ordering;
    }

    private static ArrayList<FVPoint> getNearestFactorNeigbours(int width, double factor) {
        ArrayList<FVPoint> selected = new ArrayList<>(width * 2 + 1);

        ArrayList<FVPoint> ordering = orderByFactor(points);

        int c = 0;
        for (FVPoint p : ordering) {

            if (p.x_f == factor) {

                if (c < width) {
                    for (int i = 0; i < c; i++) {
                        selected.add(ordering.get(i));
                    }
                } else {
                    for (int i = 0; i < width; i++) {
                        selected.add(ordering.get(c - width + i));
                    }
                }

                selected.add(p);

                if (c >= points.size() - width) {
                    for (int i = 0; i < points.size() - c; i++) {
                        selected.add(ordering.get(i + c));
                    }
                } else {
                    for (int i = 0; i < width; i++) {
                        selected.add(ordering.get(c + i + 1));
                    }
                }

            }
            c++;
        }


        return selected;
    }

    public static double getNextFactorValue() throws SpaceExploredException {

        if (jumpingPhase) {
            double nextFactor = jumpOut();
            return nextFactor;
        }

        if (points.size() == 0) {
            return startFactor;
        } else if (points.size() == 1) {
            return startFactor + step;
        } else {

            FVPoint penultimatePoint = points.get(points.size() - 2);
            FVPoint lastPoint = points.get(points.size() - 1);

            double dyOverdx = (lastPoint.y_v - penultimatePoint.y_v) / (lastPoint.x_f - penultimatePoint.x_f);

            double newFactor;

            double direction = dyOverdx / Math.abs(dyOverdx);

            if (!DoubleComparer.equal(0, dyOverdx, 0.0000001)) {
                // if sloped

                newFactor = lastPoint.x_f - (step * direction);

            } else {
                // if flat line

                newFactor = penultimatePoint.x_f - (penultimatePoint.x_f - lastPoint.x_f) / 2;
                step = step / 2;
            }

            FVPoint match = containsValue(points, newFactor);

            if (match != null) {

                if (match.y_v > lastPoint.y_v) {
                    // up slope to match point - thus split
                    step = step / 2;
                    newFactor = lastPoint.x_f + step;
                } else if (match.y_v < lastPoint.y_v) {
                    // down slope to match point - thus jump
                    points.remove(match);
                    points.addLast(match);
                    newFactor = match.x_f - (step * direction);

                    FVPoint match2 = containsValue(points, newFactor);

                    while (match2 != null) {
                        // if newFactor has already been used, then split in gap
                        step = step / 2;
                        newFactor = match.x_f - (step * direction);
                        match2 = containsValue(points, newFactor);
                    }

                } else {
                    // if equal v
                    step = step / 2;
                    newFactor = lastPoint.x_f + step;
                }
            }

            if (newFactor < bottomSearchBoundFactor || newFactor > topSearchBoundFactor) {
                // we're out of bounds
                // the fact we're here means there may be a lower minima out width the specified search area.
                // if we can extend them and we havn't seen something to suggest we won't overrun the heap then we'll extend the bounds

                // otherwise lets continue the minima search at a random location - but jumping out

                if (newFactor < 0) {
                    if (DoubleComparer.equal(bottomSearchBoundFactor, hardLimitBottomBoundFactor, 0.0000001)) {
                        bottomSearchBoundFactor -= step * 2;
//                        System.out.println("Suspected Minima out of search bounds - extending bottom search bound to : " + bottomSearchBoundFactor);
                    } else {
//                        System.out.println("Suspected Minima out of search bounds beyond : " + bottomSearchBoundFactor);
//                        System.out.println("Cannot extend search bound due to hard limit placed due to lack of heap space in earlier run - increase heap space to explore this area");
                        return jumpOut();
                    }
                } else {
                    topSearchBoundFactor += step * 2;
//                        System.out.println("Suspected Minima out of search bounds - extending top search bound to : " + topSearchBoundFactor);
                }
            }

            return newFactor;
        }
    }

    private static FVPoint containsValue(LinkedList<FVPoint> points, double newFactor) {

        for (FVPoint point : points) {
            if (DoubleComparer.equal(point.x_f, newFactor, 0.000000001)) {
                return point;
            }
        }

        return null;
    }

    static LinkedList<FVPoint> points = new LinkedList<>();

    public static void logFactortoV(double factor, double v) {

        points.addLast(new FVPoint(factor, v));

    }
}
