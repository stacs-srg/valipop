package uk.ac.standrews.cs.digitising_scotland.linkage.resolve.distances;

import org.simmetrics.metrics.Levenshtein;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.Distance;

/**
 * Created by al on 06/03/2017.
 */
public class GFNGLNBFNBMNPOMDOMDistanceOverMarriage implements Distance<Marriage> {

    Levenshtein levenshtein = new Levenshtein();

    @Override
    public float distance(Marriage m1, Marriage m2) {

        return GFNdistance(m1,m2) + GLNdistance(m1,m2) + BFNdistance(m1,m2) + BLNdistance(m1,m2) + POMdistance(m1,m2) + DOMdistance(m1,m2);
    }

    private float GFNdistance(Marriage m1, Marriage m2) {
        return levenshtein.distance( m1.getGroomsForename(), m2.getGroomsForename() );
    }

    private float GLNdistance(Marriage m1, Marriage m2) {
        return levenshtein.distance( m1.getGroomsSurname(), m2.getGroomsSurname() );
    }

    private float BFNdistance(Marriage m1, Marriage m2) {
        return levenshtein.distance( m1.getBridesForename(), m2.getBridesForename() );
    }

    private float BLNdistance(Marriage m1, Marriage m2) {
        return levenshtein.distance( m1.getBridesSurname(), m2.getBridesSurname() );
    }

    private float POMdistance(Marriage m1, Marriage m2) {
        return ( m1.getPlaceOfMarriage().equals( "ng") || m2.getPlaceOfMarriage().equals( "ng" ) ? 0 : levenshtein.distance( m1.getPlaceOfMarriage(), m2.getPlaceOfMarriage() ) );
    }

    private float DOMdistance(Marriage m1, Marriage m2) {
        float day_dist = m1.getString( Marriage.MARRIAGE_DAY ).equals( "--") || m2.getString( Marriage.MARRIAGE_DAY ).equals( "--" ) ? 0 : levenshtein.distance( m1.getString( Marriage.MARRIAGE_DAY ), m2.getString( Marriage.MARRIAGE_DAY ) );
        float month_dist = m1.getString( Marriage.MARRIAGE_MONTH ).equals( "---") || m2.getString( Marriage.MARRIAGE_MONTH ).equals( "---" ) ? 0 : levenshtein.distance( m1.getString( Marriage.MARRIAGE_MONTH ), m2.getString( Marriage.MARRIAGE_MONTH ) );
        float year_dist = m1.getString( Marriage.MARRIAGE_YEAR ).equals( "----") || m2.getString( Marriage.MARRIAGE_YEAR ).equals( "----" ) ? 0 : levenshtein.distance( m1.getString( Marriage.MARRIAGE_YEAR ), m2.getString( Marriage.MARRIAGE_YEAR ) );
        return day_dist + month_dist + year_dist;
    }
}


