package uk.ac.standrews.cs.digitising_scotland.linkage.experiments.distances;

import org.simmetrics.metrics.Levenshtein;
import uk.ac.standrews.cs.digitising_scotland.linkage.experiments.Experiment;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockExperiment;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;

import java.lang.invoke.MethodHandles;
import java.util.*;

public class KilmarnockDateProfiling extends KilmarnockExperiment {

    private static final String[] ARG_NAMES = {"births_source_path", "deaths_source_path", "marriages_source_path"};
    private final Levenshtein levenshtein = new Levenshtein();

    private KilmarnockDateProfiling() throws Exception {
    }

    private void profileMarriageDays() throws BucketException {

        Set<BirthFamilyGT> birth_set = loadBirths();

        List<String> days = new ArrayList<>();

        for (BirthFamilyGT birth_record : birth_set) {
            days.add((String) birth_record.get(BirthFamilyGT.PARENTS_DAY_OF_MARRIAGE));
        }

        profileStrings(days);
    }

    private void profileMarriageMonths() throws BucketException {

        Set<BirthFamilyGT> birth_set = loadBirths();

        List<String> months = new ArrayList<>();

        for (BirthFamilyGT birth_record : birth_set) {
            months.add((String) birth_record.get(BirthFamilyGT.PARENTS_MONTH_OF_MARRIAGE));
        }

        profileStrings(months);
    }

    private void profileMarriageYears() throws BucketException {

        Set<BirthFamilyGT> birth_set = loadBirths();

        List<String> years = new ArrayList<>();

        for (BirthFamilyGT birth_record : birth_set) {
            years.add((String) birth_record.get(BirthFamilyGT.PARENTS_YEAR_OF_MARRIAGE));
        }

        profileStrings(years);
    }

    private void profileStrings(List<String> strings) {

        Map<String, Integer> map = new HashMap<>();

        for (String s : strings) {

            if (map.containsKey(s)) {
                map.put(s, map.get(s) + 1);
            } else {
                map.put(s, 1);
            }
        }

        Map<String, Integer> sorted_map = new TreeMap<>(new ValueComparator(map));
        sorted_map.putAll(map);

        for (Map.Entry<String, Integer> entry : sorted_map.entrySet()) {
            System.out.println(entry.getValue() + " \"" + entry.getKey() + "\"");
        }
    }

    private void compute() throws Exception {

        timedRun("Profiling marriage days", () -> {
            profileMarriageDays();
            return null;
        });

        timedRun("Profiling marriage months", () -> {
            profileMarriageMonths();
            return null;
        });

        timedRun("Profiling marriage years", () -> {
            profileMarriageYears();
            return null;
        });
    }

    static class ValueComparator implements Comparator<String> {

        Map<String, Integer> base;

        ValueComparator(Map<String, Integer> base) {
            this.base = base;
        }

        @Override
        public int compare(String a, String b) {
            Integer x = base.get(a);
            Integer y = base.get(b);
            if (x.equals(y)) {
                return a.compareTo(b);
            }
            return x.compareTo(y);
        }
    }

    //***********************************************************************************

    public static void main(String[] args) throws Exception {

        Experiment experiment = new Experiment(ARG_NAMES, args, MethodHandles.lookup().lookupClass());

        if (args.length >= ARG_NAMES.length) {

            String births_source_path = args[0];
            String deaths_source_path = args[1];
            String marriages_source_path = args[2];

            KilmarnockDateProfiling matcher = new KilmarnockDateProfiling();

            experiment.printDescription();

            matcher.ingestRecords(births_source_path, deaths_source_path, marriages_source_path);
            matcher.compute();

        } else {
            experiment.usage();
        }
    }
}
