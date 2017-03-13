package uk.ac.standrews.cs.digitising_scotland.linkage.resolve.distances;

import org.simmetrics.metrics.Levenshtein;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.Distance;

/**
 * Created by al on 06/03/2017.
 */
public class GFNGLNBFNBMNPOMDOMDistanceOverBirth implements Distance<BirthFamilyGT> {

    Levenshtein levenshtein = new Levenshtein();

    @Override
    public float distance(BirthFamilyGT b1, BirthFamilyGT m2) {

        return FFNdistance(b1,m2) + FLNdistance(b1,m2) + MFNdistance(b1,m2) + MMNdistance(b1,m2) + POMdistance(b1,m2) + DOMdistance(b1,m2);
    }

    private float FFNdistance(BirthFamilyGT b1, BirthFamilyGT m2) {
        return levenshtein.distance( b1.getFathersSurname(), m2.getFathersSurname() );
    }

    private float FLNdistance(BirthFamilyGT b1, BirthFamilyGT m2) {
        return levenshtein.distance( b1.getFathersForename(), m2.getFathersForename() );
    }

    private float MFNdistance(BirthFamilyGT b1, BirthFamilyGT m2) {
        return levenshtein.distance( b1.getMothersForename(), m2.getMothersForename() );
    }

    private float MMNdistance(BirthFamilyGT b1, BirthFamilyGT m2) {
        return levenshtein.distance( b1.getMothersMaidenSurname(), m2.getMothersMaidenSurname() );
    }

    private float POMdistance(BirthFamilyGT b1, BirthFamilyGT m2) {
        return ( b1.getPlaceOfMarriage().equals( "ng") || m2.getPlaceOfMarriage().equals( "ng" ) ? 0 : levenshtein.distance( b1.getPlaceOfMarriage(), m2.getPlaceOfMarriage() ) );
    }

    private float DOMdistance(BirthFamilyGT b1, BirthFamilyGT m2) {
        float day_dist = b1.getString( BirthFamilyGT.PARENTS_DAY_OF_MARRIAGE ).equals( "--") || m2.getString( BirthFamilyGT.PARENTS_DAY_OF_MARRIAGE ).equals( "--" ) ? 0 : levenshtein.distance( b1.getString( BirthFamilyGT.PARENTS_DAY_OF_MARRIAGE ), m2.getString( BirthFamilyGT.PARENTS_DAY_OF_MARRIAGE ) );
        float month_dist = b1.getString( BirthFamilyGT.PARENTS_MONTH_OF_MARRIAGE ).equals( "---") || m2.getString( BirthFamilyGT.PARENTS_MONTH_OF_MARRIAGE ).equals( "---" ) ? 0 : levenshtein.distance( b1.getString( BirthFamilyGT.PARENTS_MONTH_OF_MARRIAGE ), m2.getString( BirthFamilyGT.PARENTS_MONTH_OF_MARRIAGE ) );
        float year_dist = b1.getString( BirthFamilyGT.PARENTS_YEAR_OF_MARRIAGE).equals( "----") || m2.getString( BirthFamilyGT.PARENTS_YEAR_OF_MARRIAGE ).equals( "----" ) ? 0 : levenshtein.distance( b1.getString( BirthFamilyGT.PARENTS_YEAR_OF_MARRIAGE ), m2.getString( BirthFamilyGT.PARENTS_YEAR_OF_MARRIAGE ) );
        return day_dist + month_dist + year_dist;
    }
}
