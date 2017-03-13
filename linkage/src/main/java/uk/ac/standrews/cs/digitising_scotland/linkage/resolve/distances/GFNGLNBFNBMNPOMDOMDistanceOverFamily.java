package uk.ac.standrews.cs.digitising_scotland.linkage.resolve.distances;

import org.simmetrics.metrics.Levenshtein;
import uk.ac.standrews.cs.digitising_scotland.linkage.resolve.Family;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.Distance;

/**
 * Created by al on 06/03/2017.
 */
public class GFNGLNBFNBMNPOMDOMDistanceOverFamily implements Distance<Family> {

    Levenshtein levenshtein = new Levenshtein();

    @Override
    public float distance(Family f1, Family f2) {

        return FFNdistance(f1,f2) + FLNdistance(f1,f2) + MFNdistance(f1,f2) + MMNdistance(f1,f2) + POMdistance(f1,f2) + DOMdistance(f1,f2);
    }

    private float FFNdistance(Family f1, Family f2) {
        return levenshtein.distance( f1.getFathersSurname(), f2.getFathersSurname() );
    }

    private float FLNdistance(Family f1, Family f2) {
        return levenshtein.distance( f1.getFathersForename(), f2.getFathersForename() );
    }

    private float MFNdistance(Family f1, Family f2) {
        return levenshtein.distance( f1.getMothersForename(), f2.getMothersForename() );
    }

    private float MMNdistance(Family f1, Family f2) {
        return levenshtein.distance( f1.getMothersMaidenSurname(), f2.getMothersMaidenSurname() );
    }

    private float POMdistance(Family f1, Family f2) {
        return ( f1.getPlaceOfMarriage().equals( "ng") || f2.getPlaceOfMarriage().equals( "ng" ) ? 0 : levenshtein.distance( f1.getPlaceOfMarriage(), f2.getPlaceOfMarriage() ) );
    }

    private float DOMdistance(Family f1, Family f2) {
        float day_dist = f1.getDayOfMarriage().equals( "--") || f2.getDayOfMarriage().equals( "--" ) ? 0 : levenshtein.distance( f1.getDayOfMarriage(), f2.getDayOfMarriage() );
        float month_dist = f1.getMonthOfMarriage().equals( "---") || f2.getMonthOfMarriage().equals( "---" ) ? 0 : levenshtein.distance( f1.getMonthOfMarriage(), f2.getMonthOfMarriage() );
        float year_dist = f1.getYearOfMarriage().equals( "----") || f2.getYearOfMarriage().equals( "----" ) ? 0 : levenshtein.distance( f1.getYearOfMarriage(), f2.getYearOfMarriage() );
        return day_dist + month_dist + year_dist;
    }
}
