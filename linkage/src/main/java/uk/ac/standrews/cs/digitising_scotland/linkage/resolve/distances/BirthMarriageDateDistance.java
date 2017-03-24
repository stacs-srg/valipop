package uk.ac.standrews.cs.digitising_scotland.linkage.resolve.distances;

import org.simmetrics.metrics.Levenshtein;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.Distance;

import java.util.*;

public enum BirthMarriageDateDistance implements Distance<Birth> {

    LEVENSHTEIN_DATE_DISTANCE(
            ((b1, b2) -> levenshteinDistance(b1, b2, Birth.PARENTS_DAY_OF_MARRIAGE)),
            ((b1, b2) -> levenshteinDistance(b1, b2, Birth.PARENTS_MONTH_OF_MARRIAGE)),
            ((b1, b2) -> levenshteinDistance(b1, b2, Birth.PARENTS_YEAR_OF_MARRIAGE))),

    LEVENSHTEIN_DATE_DISTANCE_WITH_NULL_FILTERING(
            ((b1, b2) -> levenshteinDistanceWithNullFiltering(b1, b2, Birth.PARENTS_DAY_OF_MARRIAGE)),
            ((b1, b2) -> levenshteinDistanceWithNullFiltering(b1, b2, Birth.PARENTS_MONTH_OF_MARRIAGE)),
            ((b1, b2) -> levenshteinDistanceWithNullFiltering(b1, b2, Birth.PARENTS_YEAR_OF_MARRIAGE))),

    LEVENSHTEIN_DATE_DISTANCE_WITH_DIFFERENTIAL_NULL_FILTERING(
            ((b1, b2) -> levenshteinDistanceWithNullFiltering(b1, b2, Birth.PARENTS_DAY_OF_MARRIAGE, 1, 2)),
            ((b1, b2) -> levenshteinDistanceWithNullFiltering(b1, b2, Birth.PARENTS_MONTH_OF_MARRIAGE, 2, 3)),
            ((b1, b2) -> levenshteinDistanceWithNullFiltering(b1, b2, Birth.PARENTS_YEAR_OF_MARRIAGE, 1, 2))),

    COMPOSITE_DATE_DISTANCE(
            BirthMarriageDateDistance::numericalDayDistance,
            BirthMarriageDateDistance::numericalMonthDistance,
            BirthMarriageDateDistance::numericalYearDistance),

    COMPOSITE_DATE_DISTANCE_WITH_NULL_FILTERING(
            BirthMarriageDateDistance::numericalDayDistanceWithNullFiltering,
            BirthMarriageDateDistance::numericalMonthDistanceWithNullFiltering,
            BirthMarriageDateDistance::numericalYearDistanceWithNullFiltering),

    NUMERICAL_DATE_DISTANCE(
            BirthMarriageDateDistance::dateDistance),


    NUMERICAL_DATE_DISTANCE_THRESHOLD(
            BirthMarriageDateDistance::dateDistanceThreshold),


    NUMERICAL_YEAR_THRESHOLD1(
            (b1, b2) -> 0,
            (b1, b2) -> 0,
            (b1, b2) -> yearThresholdDistance(b1, b2, 1, 20)),

    NUMERICAL_YEAR_THRESHOLD2(
            (b1, b2) -> 0,
            (b1, b2) -> 0,
            (b1, b2) -> yearThresholdDistance(b1, b2, 2, 20)),

    NUMERICAL_YEAR_THRESHOLD3(
            (b1, b2) -> 0,
            (b1, b2) -> 0,
            (b1, b2) -> yearThresholdDistance(b1, b2, 1, 10)),

    NUMERICAL_YEAR_THRESHOLD4(
            (b1, b2) -> 0,
            (b1, b2) -> 0,
            (b1, b2) -> yearThresholdDistance(b1, b2, 2, 10)),

    NUMERICAL_YEAR_THRESHOLD5(
            (b1, b2) -> 0,
            (b1, b2) -> 0,
            (b1, b2) -> yearThresholdDistance(b1, b2, 3, 10)),

    NUMERICAL_YEAR_THRESHOLD6(
            (b1, b2) -> 0,
            (b1, b2) -> 0,
            (b1, b2) -> yearThresholdDistance(b1, b2, 4, 10));

    private static final float DAY_DIFFERENCE_NORMALISATION_FACTOR = 7.5f;  // Expected day difference = 15, normalise to 2-char difference.
    private static final float MONTH_DIFFERENCE_NORMALISATION_FACTOR = 2f;  // Expected month difference = 6, normalise to 3-char difference.
    private static final float YEAR_DIFFERENCE_NORMALISATION_FACTOR = 17.5f;   // Expected year difference = 35, normalise to 2-char difference.
    private static final float DATE_DIFFERENCE_NORMALISATION_FACTOR = 365;

    private static float numericalDayDistance(Birth b1, Birth b2) {

        String value1 = b1.getString(Birth.PARENTS_DAY_OF_MARRIAGE);
        String value2 = b2.getString(Birth.PARENTS_DAY_OF_MARRIAGE);

        return numericalDayDistance(value1, value2);
    }

    private static float numericalDayDistance(String value1, String value2) {

        try {
            int day1 = Integer.parseInt(value1);
            int day2 = Integer.parseInt(value2);

            return Math.abs((float) day1 - (float) day2) / DAY_DIFFERENCE_NORMALISATION_FACTOR;

        } catch (NumberFormatException e) {
            return 2;
        }
    }

    private static float numericalDayDistanceWithNullFiltering(Birth b1, Birth b2) {

        String value1 = b1.getString(Birth.PARENTS_DAY_OF_MARRIAGE).trim();
        String value2 = b2.getString(Birth.PARENTS_DAY_OF_MARRIAGE).trim();

        float d = distanceWithUnknownValues(value1, value2, 0, 0);
        if (d >= 0) return d;

        return numericalDayDistance(value1, value2);
    }

    private static float numericalMonthDistance(Birth b1, Birth b2) {

        String value1 = b1.getString(Birth.PARENTS_MONTH_OF_MARRIAGE);
        String value2 = b2.getString(Birth.PARENTS_MONTH_OF_MARRIAGE);

        return numericalMonthDistance(value1, value2);
    }

    private static float numericalMonthDistance(String value1, String value2) {

        Integer month1 = MONTH_NUMBERS.get(value1);
        Integer month2 = MONTH_NUMBERS.get(value2);

        if (month1 == null || month2 == null) return 2;

        return Math.abs((float) month1 - (float) month2) / MONTH_DIFFERENCE_NORMALISATION_FACTOR;
    }

    private static float numericalMonthDistanceWithNullFiltering(Birth b1, Birth b2) {

        String value1 = b1.getString(Birth.PARENTS_MONTH_OF_MARRIAGE).trim();
        String value2 = b2.getString(Birth.PARENTS_MONTH_OF_MARRIAGE).trim();

        float d = distanceWithUnknownValues(value1, value2, 0, 0);
        if (d >= 0) return d;

        return numericalMonthDistance(value1, value2);
    }

    private static float numericalYearDistance(Birth b1, Birth b2) {

        String value1 = b1.getString(Birth.PARENTS_YEAR_OF_MARRIAGE);
        String value2 = b2.getString(Birth.PARENTS_YEAR_OF_MARRIAGE);

        return numericalYearDistance(value1, value2);
    }

    private static float numericalYearDistance(String value1, String value2) {

        try {
            int year1 = convertToYear(Integer.parseInt(value1));
            int year2 = convertToYear(Integer.parseInt(value2));

            return Math.abs((float) year1 - (float) year2) / YEAR_DIFFERENCE_NORMALISATION_FACTOR;

        } catch (NumberFormatException e) {
            return 2;
        }
    }

    private static float yearThresholdDistance2(Birth b1, Birth b2, int year_threshold, int year_threshold_non_match_distance) {

        String value1 = b1.getString(Birth.PARENTS_YEAR_OF_MARRIAGE).trim();
        String value2 = b2.getString(Birth.PARENTS_YEAR_OF_MARRIAGE).trim();

        try {
            int year1 = convertToYear(Integer.parseInt(value1));
            int year2 = convertToYear(Integer.parseInt(value2));

            return Math.abs((float) year1 - (float) year2) <= year_threshold ? 0 : year_threshold_non_match_distance;

        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static float yearThresholdDistance(Birth b1, Birth b2, int year_threshold, int year_threshold_non_match_distance) {

        String value1 = b1.getString(Birth.PARENTS_YEAR_OF_MARRIAGE).trim();
        String value2 = b2.getString(Birth.PARENTS_YEAR_OF_MARRIAGE).trim();

        try {

            return previouslyCalculatedDistance(value1, value2);

        } catch (RuntimeException e) {

            float distance;
            try {
                int year1 = convertToYear(Integer.parseInt(value1));
                int year2 = convertToYear(Integer.parseInt(value2));

                distance = Math.abs((float) year1 - (float) year2) <= year_threshold ? 0 : year_threshold_non_match_distance;

            } catch (NumberFormatException e1) {
                distance = 0;
            }
            rememberDistance(value1, value2, distance);
            return distance;

        }
    }

    private static void rememberDistance(String value1, String value2, float distance) {

        Map<String, Float> inner_map = calculated_distances.computeIfAbsent(value1, k -> new HashMap<>());
        inner_map.put(value2, distance);
    }

    public static Map<String, Map<String, Float>> calculated_distances;

    public static void resetCache() {

        calculated_distances = new HashMap<>();
    }

    private static float previouslyCalculatedDistance(String value1, String value2) {

        return calculated_distances.get(value1).get(value2);
    }

    private static float numericalYearDistanceWithNullFiltering(Birth b1, Birth b2) {

        String value1 = b1.getString(Birth.PARENTS_YEAR_OF_MARRIAGE).trim();
        String value2 = b2.getString(Birth.PARENTS_YEAR_OF_MARRIAGE).trim();

        float d = distanceWithUnknownValues(value1, value2, 0, 0);
        if (d >= 0) return d;

        return numericalYearDistance(value1, value2);
    }

    private static int convertToYear(int two_digit_year) {

        return two_digit_year < 2 ? two_digit_year + 1900 : two_digit_year + 1800;
    }

    private static final Map<String, Integer> MONTH_NUMBERS;

    static {
        MONTH_NUMBERS = new HashMap<>();
        initializeMonthNumbers(MONTH_NUMBERS);

        resetCache();
    }

    private static void initializeMonthNumbers(Map<String, Integer> month_numbers) {

        month_numbers.put("jan", 1);
        month_numbers.put("feb", 2);
        month_numbers.put("mar", 3);
        month_numbers.put("apr", 4);
        month_numbers.put("april", 4);
        month_numbers.put("may", 5);
        month_numbers.put("jun", 6);
        month_numbers.put("june", 6);
        month_numbers.put("jul", 7);
        month_numbers.put("july", 7);
        month_numbers.put("aug", 8);
        month_numbers.put("sep", 9);
        month_numbers.put("sept", 9);
        month_numbers.put("oct", 10);
        month_numbers.put("nov", 11);
        month_numbers.put("dec", 12);
    }


    public static float dateDistance(Birth b1, Birth b2) {
        try {

            return Math.abs((float) dateInDays(b1) - (float) dateInDays(b2)) / DATE_DIFFERENCE_NORMALISATION_FACTOR;
        } catch (RuntimeException e) {
            return 0;
        }

    }

    public static float dateDistanceThreshold(Birth b1, Birth b2) {

        try {
            return Math.abs((float) dateInDays(b1) - (float) dateInDays(b2)) < 500 ? 0 : 20;

        } catch (RuntimeException e) {
            return 0;
        }
    }

    public static int dateInDays(Birth b) {

        int day = Integer.parseInt(b.getString(Birth.PARENTS_DAY_OF_MARRIAGE));
        int month = MONTH_NUMBERS.get(b.getString(Birth.PARENTS_MONTH_OF_MARRIAGE));
        int year = convertToYear(Integer.parseInt(b.getString(Birth.PARENTS_YEAR_OF_MARRIAGE)));

        return DateManipulation.dateToDays(year, month, day);

    }


    private static final Levenshtein LEVENSHTEIN = new Levenshtein();
    private static final Set<String> NULL_VALUES = new HashSet<>(Arrays.asList("", "na", "ng", "?", "n/a"));

    private Distance<Birth> day_distance;
    private Distance<Birth> month_distance;
    private Distance<Birth> year_distance;

    private final boolean composite;

    BirthMarriageDateDistance(Distance<Birth> day_distance) {

        this.day_distance = day_distance;
        composite = false;
    }

    BirthMarriageDateDistance(Distance<Birth> day_distance, Distance<Birth> month_distance, Distance<Birth> year_distance) {

        this.day_distance = day_distance;
        this.month_distance = month_distance;
        this.year_distance = year_distance;
        composite = true;
    }

    @Override
    public float distance(Birth a, Birth b) {

        return !composite ? day_distance.distance(a, b) : day_distance.distance(a, b) + month_distance.distance(a, b) + year_distance.distance(a, b);
    }

    private static float levenshteinDistance(Birth b1, Birth b2, String selector) {

        String value1 = b1.getString(selector);
        String value2 = b2.getString(selector);

        return LEVENSHTEIN.distance(value1, value2);
    }

    private static float levenshteinDistanceWithNullFiltering(Birth b1, Birth b2, String selector) {

        return levenshteinDistanceWithNullFiltering(b1, b2, selector, 0, 0);
    }

    private static float levenshteinDistanceWithNullFiltering(Birth b1, Birth b2, String selector, float distance_for_one_unknown, float distance_for_two_unknowns) {

        String value1 = b1.getString(selector).trim();
        String value2 = b2.getString(selector).trim();

        float d = distanceWithUnknownValues(value1, value2, distance_for_one_unknown, distance_for_two_unknowns);

        return d >= 0 ? d : LEVENSHTEIN.distance(value1, value2);
    }

    private static float distanceWithUnknownValues(String value1, String value2, float distance_for_one_unknown, float distance_for_two_unknowns) {

        boolean value1_unknown = NULL_VALUES.contains(value1);
        boolean value2_unknown = NULL_VALUES.contains(value2);

        if (value1_unknown && value2_unknown) return distance_for_two_unknowns;
        if (value1_unknown || value2_unknown) return distance_for_one_unknown;

        return -1;
    }
}
