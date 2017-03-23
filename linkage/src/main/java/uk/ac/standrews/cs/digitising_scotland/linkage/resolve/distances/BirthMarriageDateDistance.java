package uk.ac.standrews.cs.digitising_scotland.linkage.resolve.distances;

import org.simmetrics.metrics.Levenshtein;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
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

    NUMERICAL_DATE_DISTANCE(
            BirthMarriageDateDistance::numericalDayDistance,
            BirthMarriageDateDistance::numericalMonthDistance,
            BirthMarriageDateDistance::numericalYearDistance),

//    NUMERICAL_DATE_DISTANCE_WITH_NULL_FILTERING(
//            BirthMarriageDateDistance::numericalDayDistanceWithNullFiltering,
//            BirthMarriageDateDistance::numericalMonthDistanceWithNullFiltering,
//            BirthMarriageDateDistance::numericalYearDistanceWithNullFiltering);
    ;

    private static final float DAY_DIFFERENCE_NORMALISATION_FACTOR = 7.5f;  // Expected day difference = 15, normalise to 2-char difference.
    private static final float MONTH_DIFFERENCE_NORMALISATION_FACTOR = 2f;  // Expected month difference = 6, normalise to 3-char difference.
    private static final float YEAR_DIFFERENCE_NORMALISATION_FACTOR = 17.5f;   // Expected year difference = 35, normalise to 2-char difference.

    private static float numericalDayDistance(Birth b1, Birth b2) {

        String value1 = b1.getString(Birth.PARENTS_DAY_OF_MARRIAGE);
        String value2 = b2.getString(Birth.PARENTS_DAY_OF_MARRIAGE);

        try {
            int day1 = Integer.parseInt(value1);
            int day2 = Integer.parseInt(value2);

            return Math.abs((float) day1 - (float) day2) / DAY_DIFFERENCE_NORMALISATION_FACTOR;
        } catch (NumberFormatException e) {
            return 2;
        }
    }

    private static float numericalMonthDistance(Birth b1, Birth b2) {

        String value1 = b1.getString(Birth.PARENTS_MONTH_OF_MARRIAGE);
        String value2 = b2.getString(Birth.PARENTS_MONTH_OF_MARRIAGE);

        Integer month1 = MONTH_NUMBERS.get(value1);
        Integer month2 = MONTH_NUMBERS.get(value2);

        if (month1 == null || month2 == null) return 2;

        return Math.abs((float) month1 - (float) month2) / MONTH_DIFFERENCE_NORMALISATION_FACTOR;
    }

    private static float numericalYearDistance(Birth b1, Birth b2) {

        String value1 = b1.getString(Birth.PARENTS_YEAR_OF_MARRIAGE);
        String value2 = b2.getString(Birth.PARENTS_YEAR_OF_MARRIAGE);

        try {
            int year1 = convertToYear(Integer.parseInt(value1));
            int year2 = convertToYear(Integer.parseInt(value2));

            return Math.abs((float) year1 - (float) year2) / YEAR_DIFFERENCE_NORMALISATION_FACTOR;
        } catch (NumberFormatException e) {
            return 2;
        }
    }

    private static int convertToYear(int two_digit_year) {

        return two_digit_year < 2 ? two_digit_year + 1900 : two_digit_year + 1800;
    }

    private static final Map<String, Integer> MONTH_NUMBERS = new HashMap<>();

    static {
        initializeMonthNumbers(MONTH_NUMBERS);
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


    private static final Levenshtein LEVENSHTEIN = new Levenshtein();
    private static final Set<String> NULL_VALUES = new HashSet<>(Arrays.asList("", "na", "ng", "?", "n/a"));

    private final Distance<Birth> day_distance;
    private final Distance<Birth> month_distance;
    private final Distance<Birth> year_distance;

    BirthMarriageDateDistance(Distance<Birth> day_distance, Distance<Birth> month_distance, Distance<Birth> year_distance) {

        this.day_distance = day_distance;
        this.month_distance = month_distance;
        this.year_distance = year_distance;
    }

    @Override
    public float distance(Birth a, Birth b) {

        return day_distance.distance(a, b) + month_distance.distance(a, b) + year_distance.distance(a, b);
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

        boolean value1_unknown = NULL_VALUES.contains(value1);
        boolean value2_unknown = NULL_VALUES.contains(value2);

        if (value1_unknown && value2_unknown) return distance_for_two_unknowns;
        if (value1_unknown || value2_unknown) return distance_for_one_unknown;

        return LEVENSHTEIN.distance(value1, value2);
    }
}
