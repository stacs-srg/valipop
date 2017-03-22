package uk.ac.standrews.cs.digitising_scotland.linkage.resolve.distances;

import org.simmetrics.metrics.Levenshtein;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.Distance;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DateDistance {

    public static final Levenshtein LEVENSHTEIN = new Levenshtein();

    public static final Distance<Birth> DAY_DISTANCE = (b1, b2) -> {

        String day1 = b1.getString(Birth.PARENTS_DAY_OF_MARRIAGE);
        String day2 = b2.getString(Birth.PARENTS_DAY_OF_MARRIAGE);

        return day1.equals("--") || day2.equals("--") ? 0 : LEVENSHTEIN.distance(day1, day2);
    };

    public static final Distance<Birth> MONTH_DISTANCE = (b1, b2) -> {

        String month1 = b1.getString(Birth.PARENTS_MONTH_OF_MARRIAGE);
        String month2 = b2.getString(Birth.PARENTS_MONTH_OF_MARRIAGE);

        return month1.equals("---") || month2.equals("---") ? 0 : LEVENSHTEIN.distance(month1, month2);
    };

    public static final Distance<Birth> YEAR_DISTANCE = (b1, b2) -> {

        String year1 = b1.getString(Birth.PARENTS_YEAR_OF_MARRIAGE);
        String year2 = b2.getString(Birth.PARENTS_YEAR_OF_MARRIAGE);

        return year1.equals("----") || year2.equals("----") ? 0 : LEVENSHTEIN.distance(year1, year2);
    };

    public static final Distance<Birth> ORIGINAL_DATE_DISTANCE = (b1, b2) -> DAY_DISTANCE.distance(b1, b2) + MONTH_DISTANCE.distance(b1, b2) + YEAR_DISTANCE.distance(b1, b2);


    public static final Distance<Birth> DAY_DISTANCE2 = (b1, b2) -> {

        String day1 = b1.getString(Birth.PARENTS_DAY_OF_MARRIAGE);
        String day2 = b2.getString(Birth.PARENTS_DAY_OF_MARRIAGE);

        return LEVENSHTEIN.distance(day1, day2);
    };

    public static final Distance<Birth> MONTH_DISTANCE2 = (b1, b2) -> {

        String month1 = b1.getString(Birth.PARENTS_MONTH_OF_MARRIAGE);
        String month2 = b2.getString(Birth.PARENTS_MONTH_OF_MARRIAGE);

        return LEVENSHTEIN.distance(month1, month2);
    };

    public static final Distance<Birth> YEAR_DISTANCE2 = (b1, b2) -> {

        String year1 = b1.getString(Birth.PARENTS_YEAR_OF_MARRIAGE);
        String year2 = b2.getString(Birth.PARENTS_YEAR_OF_MARRIAGE);

        return LEVENSHTEIN.distance(year1, year2);
    };

    public static final Distance<Birth> LEVENSHTEIN_DATE_DISTANCE = (b1, b2) -> DAY_DISTANCE2.distance(b1, b2) + MONTH_DISTANCE2.distance(b1, b2) + YEAR_DISTANCE2.distance(b1, b2);

    private static final Set<String> NULL_VALUES_FOR_DAYS = new HashSet<>(Arrays.asList("", " ", "  ", "na", "ng", "?", "n/a"));

    public static final Distance<Birth> DAY_DISTANCE_WITH_NULL_FILTERING = (b1, b2) -> {

        String day1 = b1.getString(Birth.PARENTS_DAY_OF_MARRIAGE);
        String day2 = b2.getString(Birth.PARENTS_DAY_OF_MARRIAGE);

        return NULL_VALUES_FOR_DAYS.contains(day1) || NULL_VALUES_FOR_DAYS.contains(day2) ? 0 : LEVENSHTEIN.distance(day1, day2);
    };

    private static final Set<String> NULL_VALUES_FOR_MONTHS = new HashSet<>(Arrays.asList("  "));

    public static final Distance<Birth> MONTH_DISTANCE_WITH_NULL_FILTERING = (b1, b2) -> {

        String month1 = b1.getString(Birth.PARENTS_MONTH_OF_MARRIAGE);
        String month2 = b2.getString(Birth.PARENTS_MONTH_OF_MARRIAGE);

        return NULL_VALUES_FOR_MONTHS.contains(month1) || NULL_VALUES_FOR_MONTHS.contains(month2) ? 0 : LEVENSHTEIN.distance(month1, month2);
    };

    private static final Set<String> NULL_VALUES_FOR_YEARS = new HashSet<>(Arrays.asList("", "na", "ng", " "));

    public static final Distance<Birth> YEAR_DISTANCE_WITH_NULL_FILTERING = (b1, b2) -> {

        String year1 = b1.getString(Birth.PARENTS_YEAR_OF_MARRIAGE);
        String year2 = b2.getString(Birth.PARENTS_YEAR_OF_MARRIAGE);

        return NULL_VALUES_FOR_YEARS.contains(year1) || NULL_VALUES_FOR_YEARS.contains(year2) ? 0 : LEVENSHTEIN.distance(year1, year2);
    };

    public static final Distance<Birth> LEVENSHTEIN_DATE_DISTANCE_WITH_NULL_FILTERING = (b1, b2) -> DAY_DISTANCE_WITH_NULL_FILTERING.distance(b1, b2) + MONTH_DISTANCE_WITH_NULL_FILTERING.distance(b1, b2) + YEAR_DISTANCE_WITH_NULL_FILTERING.distance(b1, b2);

    // Profile of day of marriage strings from Kilmarnock births:
    //    1 "10th"
    //    1 "32"
    //    1 "35"
    //    1 "57"
    //    1 "5th"
    //    1 "60"
    //    1 "?"
    //    1 "beith"
    //    1 "n/a"
    //    1 "nan"
    //    1 "sanquahar"
    //    1 "tradeston"
    //    2 "61"
    //    3 "67"
    //    3 "ayr"
    //    3 "n"
    //    4 "see notes"
    //    30 " "
    //    459 "na"
    //    469 "18"
    //    495 "20"
    //    506 "13"
    //    509 "9"
    //    519 "19"
    //    537 "8"
    //    543 "17"
    //    562 "15"
    //    564 "16"
    //    573 "22"
    //    573 "23"
    //    586 "11"
    //    610 "24"
    //    613 "10"
    //    616 "21"
    //    617 "12"
    //    617 "14"
    //    667 "7"
    //    684 "6"
    //    784 "5"
    //    812 "4"
    //    813 "3"
    //    828 "26"
    //    847 "27"
    //    861 "2"
    //    938 "28"
    //    952 "30"
    //    967 "25"
    //    1124 "29"
    //    1181 "1"
    //    2403 "ng"
    //    2793 "31"
    //    11752 ""


    // months
    //    1 "  "
    //    1 " feb"
    //    1 " mar"
    //    1 " sep"
    //    1 "a"
    //    1 "au73"
    //    1 "blank"
    //    1 "dw"
    //    1 "jne"
    //    1 "june or july"
    //    1 "juol"
    //    1 "ma"
    //    1 "mar "
    //    1 "octg"
    //    1 "sew"
    //    2 " jul"
    //    2 " jun"
    //    2 "d"
    //    3 "jun "
    //    4 "see notes"
    //    6 "june "
    //    9 "ap"
    //    10 " "
    //    15 "may "
    //    42 "oct "
    //    214 "april"
    //    253 "ng"
    //    300 "sept"
    //    451 "na"
    //    547 "july"
    //    801 "june"
    //    847 "may"
    //    1848 "feb"
    //    1910 "sep"
    //    2027 "oct"
    //    2083 "jan"
    //    2133 "mar"
    //    2228 "aug"
    //    2258 "apr"
    //    2592 ""
    //    2696 "nov"
    //    3523 "jul"
    //    4806 "jun"
    //    6804 "dec"

    // years
//    1 "  "
//    1 "15"
//    1 "25"
//    1 "26"
//    1 "27"
//    1 "28"
//    1 "29"
//    1 "32"
//    1 "33"
//    1 "4"
//    1 "53 or 54"
//    1 "54 (see notes)"
//    1 "55 or 56"
//    1 "5?"
//    1 "60 (see notes)"
//    1 "64 or 69"
//    1 "712"
//    1 "7u0"
//    1 "?6"
//    1 "about 43"
//    1 "about 51"
//    1 "jun"
//    1 "nr (see notes)"
//    1 "nr (splot)"
//    2 "154"
//    2 "3"
//    2 "6"
//    3 "01"
//    3 "36"
//    4 "31"
//    7 "see notes"
//    8 "34"
//    8 "38"
//    9 "37"
//    20 "ng"
//    21 "39"
//    27 "40"
//    53 "41"
//    53 "42"
//    59 "43"
//    89 "44"
//    91 "00"
//    102 "45"
//    118 "46"
//    155 "47"
//    189 "48"
//    200 "99"
//    251 "49"
//    275 " "
//    284 "50"
//    285 "98"
//    327 "51"
//    363 "52"
//    374 "54"
//    376 "97"
//    385 "53"
//    410 "96"
//    446 "na"
//    451 "94"
//    460 "55"
//    477 "95"
//    502 "93"
//    567 "58"
//    577 "56"
//    606 "92"
//    624 "88"
//    632 "57"
//    633 "91"
//    647 "90"
//    651 "87"
//    657 "89"
//    678 "79"
//    681 "85"
//    706 "86"
//    712 "68"
//    743 "59"
//    754 "77"
//    788 "78"
//    804 "81"
//    817 "67"
//    819 "70"
//    826 "82"
//    832 "61"
//    837 "80"
//    850 "63"
//    855 "62"
//    870 "60"
//    872 "66"
//    874 "84"
//    884 "65"
//    887 "76"
//    890 "64"
//    919 "69"
//    929 "71"
//    963 "75"
//    966 "83"
//    1015 "73"
//    1116 "74"
//    1127 "72"
//    1908 ""

}
