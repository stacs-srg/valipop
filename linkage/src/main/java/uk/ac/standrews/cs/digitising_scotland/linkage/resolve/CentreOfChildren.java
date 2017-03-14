package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.linkage.resolve.distances.GFNGLNBFNBMNPOMDOMDistanceOverBirth;

import java.util.ArrayList;

/**
 * Created by al on 13/03/2017.
 */
public class CentreOfChildren {

    final static GFNGLNBFNBMNPOMDOMDistanceOverBirth metric = new GFNGLNBFNBMNPOMDOMDistanceOverBirth();

    /**
     * Find the most centre node of a group of Births based on distances between parents marriage attributes
     * @param children a collection of births from which to find the centremost
     * @return the centre most birth in the collection according to the metric.
     */
    public BirthFamilyGT findCentroid(ArrayList<BirthFamilyGT> children ) {
        long[] distances = new long[children.size()]; // sums of distances between node i and other children
        for( BirthFamilyGT child : children ) {
            long distance = 0l;
            for( int i = 0; i < children.size(); i++ ) { // sum the distances to other children
                distance += metric.distance( child, children.get(i) );
            }
        }
        // now find the lowest total distance in diatances, hence the central child.
        int min_index = 0;
        for( int i = 0; i < distances.length; i++ ) {
            if( distances[i] < distances[min_index] ) {
                min_index = i;
            }
        }
        return children.get(min_index); // the child with the smallest distance to all other nodes
    }
}
