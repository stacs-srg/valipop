package uk.ac.standrews.cs.digitising_scotland.linkage.experiments.distances;

import org.simmetrics.metrics.Levenshtein;
import uk.ac.standrews.cs.digitising_scotland.linkage.experiments.Experiment;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockExperiment;
import uk.ac.standrews.cs.util.tools.PercentageProgressIndicator;
import uk.ac.standrews.cs.util.tools.ProgressIndicator;

import java.lang.invoke.MethodHandles;
import java.util.Set;

public class KilmarnockDateComparisons extends KilmarnockExperiment {

    private static final String[] ARG_NAMES = {"births_source_path", "deaths_source_path", "marriages_source_path"};
    private final Levenshtein levenshtein = new Levenshtein();

    private KilmarnockDateComparisons() throws Exception {
    }

    private void evaluateDateComparison() throws Exception {

        for (int i = 1000; i < 8000; i += 1000) {
            evaluateDateComparison(i, 0);
        }
    }

    private void evaluateDateComparison(int number_of_births_to_process, int number_of_updates) throws Exception {

        Set<BirthFamilyGT> birth_set = loadBirths(number_of_births_to_process);

        double total_distance_within_families = 0;
        double total_distance_between_families = 0;

        long count_of_pairs_within_families = 0;
        long count_of_pairs_between_families = 0;

        ProgressIndicator indicator = new PercentageProgressIndicator(number_of_updates);
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

        System.out.println(String.format("Number of people considered:       %d", number_of_births_to_process));
        System.out.println(String.format("Average distance within families:  %.1f", total_distance_within_families / count_of_pairs_within_families));
        System.out.println(String.format("Average distance between families: %.1f", total_distance_between_families / count_of_pairs_between_families));
        System.out.println();
    }

    private float dateDistance(String date1, String date2) {

        return levenshtein.distance(date1, date2);
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
