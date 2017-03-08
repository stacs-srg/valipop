package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;

import static uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthMarriageRangeCSVGenerator.RANGE_MAX;

/**
 * Module to inject and analyse output created by KilmarnockMTreeBirthMarriageRangeCSVGenerator
 * This is a csv file containing rows number of matches between births and marriages at edit distance 1,2, ..
 * Created by al on 22/02/2017.
 * @author al@st-andrews.ac.uk
 */
public class KilmarnockBirthMarriageRangeSearchCSVAnalysis {

    private int num_births = 0;
    int[][] data_array;
    private static final int MAX_COUMN_INDEX = RANGE_MAX;

    public KilmarnockBirthMarriageRangeSearchCSVAnalysis(String csv_source_path ) {

        try {
            num_births = injestCsv( csv_source_path );
        } catch (IOException e) {
            ErrorHandling.error( "whilst injesting data");
        }
        analyse_data_array();
    }


    private int injestCsv(String csv_source_path) throws IOException {

        DataSet data_set = new DataSet(Paths.get(csv_source_path));
        num_births = data_set.getRecords().size();
        data_array = new int[num_births][MAX_COUMN_INDEX];
        int rows_processed = 0;


        for (List<String> row : data_set.getRecords()) {
            int data_column = 0;
            for( String entry : row ) {
                data_array[rows_processed][data_column++] = Integer.valueOf( entry );
            }
            rows_processed++;
        }
        return rows_processed;
    }

    private void analyse_data_array() {
        int no_matches = analyse_no_matches();
        int perfect = analyse_perfect_match();
        int unique = analyse_unique_matches();

        int[] d_to_any_match = analyse_distance_to_first_match( false );
        int[] d_to_first_unique = analyse_distance_to_first_match( true );
        int[] one_match_runs = analyse_single_match_runs();

        System.out.println( "No matches found = " + no_matches + " = " + no_matches * 100 / (float) num_births + "%" );
        System.out.println( "Number of perfect matches = " + perfect + " = " + perfect * 100 / (float) num_births + "%" );
        System.out.println( "Number of unique matches = " + unique + " = " + unique * 100 / (float) num_births + "%" );

        show( d_to_any_match,"Distance to any match" );
        show( d_to_first_unique,"Distance to first unique match" );
        show( one_match_runs,"Length of one Match runs ");

    }

    private void display(int[] arrai ) {
        int[] counter = new int[16];
        int running_total = 0;

        for( int i : arrai ) {
            counter[ i ]++;
        }
        for( int occurrences : counter ) {
            running_total += occurrences;
//            System.out.println( occurrences * 100 / (float) num_births + "%   running total = " + running_total * 100 / (float) num_births + "%" );
        }
    }

    private void show(int[] arrai, String title) {
        System.out.println(title);
        DecimalFormat df = new DecimalFormat("#.##");
        System.out.println( "\tAverage: " + df.format( mean( arrai ) ) );
        System.out.println( "\tSD: " + df.format( stddev( arrai ) ) );
        System.out.println();
        display( arrai );
    }

    /**
     * Returns the mean of the specified array.
     * @param numbers the array
     */
    private double mean( int[] numbers ) {
        double sum = 0;
        for(int i=0; i < numbers.length ; i++)
            sum = sum + numbers[i];

        return sum / numbers.length;
    }

    /**
     * Returns the std dev in the specified array.
     * @param numbers the array
     */
    private double stddev( int[] numbers ) {
        return Math.sqrt(variance(numbers));
    }

    /**
     * Returns the variance in the specified array.
     * @param  numbers the array
     * @return the sample variance in the array {@code numbers[]};
     */
    public double variance(int[] numbers) {
        if (numbers.length == 0) return Double.NaN;
        double avg = mean(numbers);
        double sum = 0.0;
        for (int i = 0; i < numbers.length; i++) {
            sum += (numbers[i] - avg) * (numbers[i] - avg);
        }
        return sum / (numbers.length - 1);
    }


    /**
     *
     * @return the number of matches before finding a 1.
     */
    private int[] analyse_distance_to_first_match( boolean require_unique ) {
        int[] result = new int[num_births];

        for( int row_index = 0; row_index < num_births ; row_index++ ) {
            result[row_index] = first_match_in_row( data_array[row_index],require_unique);
        }
        return result;
    }

    private int first_match_in_row(int[] row, boolean require_unique ) {
        for (int column_index = 0; column_index < MAX_COUMN_INDEX; column_index++ ) {
            if( require_unique ) {
                if (row[column_index] == 1) {
                    return column_index;
                }
            } else {
                if (row[column_index] >= 1) {
                    return column_index;
                }
            }
        }
        return MAX_COUMN_INDEX; // not found a match need to return max_index
    }

    /**
     *
     * @return the number of rows for which there are all ones with possibly some zeros
     * This is equivalent to the last column being 1.
     */
    private int analyse_unique_matches() {
        int counter = 0;
        for( int[] row : data_array ) {
            if( row[MAX_COUMN_INDEX-1] == 1 ) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * Counts the number of perfect matches - i.e. edit distance is zero.
     * This is equivalent to the first column being 1.
     */
    private int analyse_perfect_match() {
        int counter = 0;
        for( int[] row : data_array )
            if( row[0] == 1 ) {
                counter++;
            }
        return counter;
    }

    /**
     * Counts the number of rows that are all zeros.
     */
    private int analyse_no_matches() {
        int counter = 0;

        for( int row_index = 0 ; row_index < num_births; row_index++ ) {
            int[] row = data_array[row_index];
            boolean found = false;
            for( int val : row ) {
                if( val != 0 )  {
                    found = true;
                    break;
                }
            }
            if( ! found ) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * @return the length of single matches within a row
     * This is equivalent to the finding the first column greater than 1.
     */
    private int[] analyse_single_match_runs() {
        int[] result = new int[num_births];

        for( int row_index = 0; row_index < num_births ; row_index++ ) {
            int count = 0;
            for (int column_index = 0; column_index < MAX_COUMN_INDEX; column_index++ ) {
                if ( data_array[row_index][column_index] > 1 ) {
                    result[row_index] = count;
                    break;
                }
                if( data_array[row_index][column_index] == 1) {
                    count++;
                }
            }
        }
        return result;
    }




    public static void main(String[] args) throws Exception {

        String csv_source_path = "/Digitising Scotland/KilmarnockBDM/birthMarriageDistances.csv";

        new KilmarnockBirthMarriageRangeSearchCSVAnalysis( csv_source_path  );
    }
}
