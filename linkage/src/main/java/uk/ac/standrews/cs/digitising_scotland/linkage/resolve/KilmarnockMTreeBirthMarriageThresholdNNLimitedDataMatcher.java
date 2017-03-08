package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

/**
 * Attempt to perform linking using MTree matching
 * File is derived from KilmarnockLinker.
 * Created by al on 17/2/1017
 */
public class KilmarnockMTreeBirthMarriageThresholdNNLimitedDataMatcher {

    //***********************************************************************************

    public static void main(String[] args) throws Exception {

        System.out.println( "Running KilmarnockMTreeBirthMarriageThresholdNNLimitedDataMatcher - restricted dataset" );
        String births_source_path = "/Digitising Scotland/KilmarnockBDM/births_post71.csv";
        String deaths_source_path = "/Digitising Scotland/KilmarnockBDM/deaths.csv";
        String marriages_source_path = "/Digitising Scotland/KilmarnockBDM/marriages_pre92.csv";

        KilmarnockMTreeBirthMarriageThresholdNNTruthChecker matcher = new KilmarnockMTreeBirthMarriageThresholdNNTruthChecker(births_source_path, deaths_source_path, marriages_source_path);
        matcher.compute();
    }
}
