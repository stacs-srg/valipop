package uk.ac.standrews.cs.digitising_scotland.linkage.experiments.distances;

import org.simmetrics.metrics.Levenshtein;
import uk.ac.standrews.cs.digitising_scotland.linkage.experiments.Experiment;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockExperiment;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.util.tools.PercentageProgressIndicator;
import uk.ac.standrews.cs.util.tools.ProgressIndicator;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;

public class KilmarnockDateComparisons extends KilmarnockExperiment {

    private static final String[] ARG_NAMES = {"births_source_path", "deaths_source_path", "marriages_source_path"};
    public static final int NUMBER_OF_BIRTHS_TO_PROCESS = 7000;
    private final Levenshtein levenshtein = new Levenshtein();

    private KilmarnockDateComparisons() throws Exception {

    }

    private void evaluateDateComparison() throws Exception {

        Set<BirthFamilyGT> birth_set = loadBirths();

        double total_distance_within_families = 0;
        double total_distance_between_families = 0;

        long count_of_pairs_within_families = 0;
        long count_of_pairs_between_families = 0;

        ProgressIndicator indicator = new PercentageProgressIndicator(10);
        indicator.setTotalSteps(birth_set.size());

        for (BirthFamilyGT birth1 : birth_set) {
            for (BirthFamilyGT birth2 : birth_set) {
                if (birth1 != birth2) {

                    float date_distance = dateDistance(birth1.getDateOfMarriage(), birth2.getDateOfMarriage());

                    if (birth1.get(BirthFamilyGT.FAMILY).equals(birth2.get(BirthFamilyGT.FAMILY))) {

                        total_distance_within_families += date_distance;
                        count_of_pairs_within_families++;

                    } else {

                        total_distance_between_families += date_distance;
                        count_of_pairs_between_families++;
                    }
                }
            }

            indicator.progressStep();
        }

        System.out.println(String.format("Average distance within families:  %.1f", total_distance_within_families / count_of_pairs_within_families));
        System.out.println(String.format("Average distance between families: %.1f", total_distance_between_families / count_of_pairs_between_families));
    }

    private float dateDistance(String date1, String date2) {

        return levenshtein.distance(date1, date2);
    }

    private Set<BirthFamilyGT> loadBirths() throws BucketException {

        Set<BirthFamilyGT> birth_set = new HashSet<>();

        int count = 0;

        for (BirthFamilyGT birth_record : births.getInputStream()) {
            birth_set.add(birth_record);
            if (++count >= NUMBER_OF_BIRTHS_TO_PROCESS) break;
        }

        return birth_set;
    }

    private void compute() throws Exception {

        timedRun("Evaluating date comparison", () -> {
            evaluateDateComparison();
            return null;
        });
    }

    //***********************************************************************************

    public static void main(String[] args) throws Exception {

        Experiment experiment = new Experiment(ARG_NAMES, args, MethodHandles.lookup().lookupClass());

        if (args.length >= ARG_NAMES.length) {

            String births_source_path = args[0];
            String deaths_source_path = args[1];
            String marriages_source_path = args[2];

            KilmarnockDateComparisons matcher = new KilmarnockDateComparisons();

            experiment.printDescription();

            matcher.ingestRecords(births_source_path, deaths_source_path, marriages_source_path);
            matcher.compute();

        } else {
            experiment.usage();
        }
    }
}
