package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.linkage.resolve.distances.GFNGLNBFNBMNPOMDOMDistanceOverBirth;

import java.util.ArrayList;
import java.util.Set;

/**
 * Essentially a set of siblings carrying an id.
 * Created by al on 28/02/2017.
 */
public class CentroidFamily extends Family {

    final static GFNGLNBFNBMNPOMDOMDistanceOverBirth metric = new GFNGLNBFNBMNPOMDOMDistanceOverBirth();

    protected BirthFamilyGT centroid = null; // centroid of family

    public CentroidFamily(BirthFamilyGT child) {
        super(child);
    }

    public String getPlaceOfMarriage() {
        check_centroid();
        return centroid.getPlaceOfMarriage();
    }

    public String getDayOfMarriage() {
        check_centroid();
        return centroid.getString( BirthFamilyGT.PARENTS_DAY_OF_MARRIAGE );
    }

    public String getMonthOfMarriage() {
        check_centroid();
        return centroid.getString( BirthFamilyGT.PARENTS_MONTH_OF_MARRIAGE );
    }

    public String getYearOfMarriage() {
        check_centroid();
        return centroid.getString( BirthFamilyGT.PARENTS_YEAR_OF_MARRIAGE );
    }

    public String getMothersMaidenSurname() {
        check_centroid();
        return centroid.getString( BirthFamilyGT.MOTHERS_MAIDEN_SURNAME );
    }

    public String getMothersForename() {
        check_centroid();
        return centroid.getString( BirthFamilyGT.MOTHERS_FORENAME );
    }

    public String getFathersSurname() {
        check_centroid();
        return centroid.getString( BirthFamilyGT.FATHERS_SURNAME );
    }

    public String getFathersForename() {
        return centroid.getString( BirthFamilyGT.FATHERS_FORENAME );
    }

    @Override
    protected void initParents(BirthFamilyGT child) {
        // don't do anything in this case.
    }

    @Override
    public Set<BirthFamilyGT> getSiblings() {
        return siblings;
    }

    @Override
    public void addSibling( BirthFamilyGT sibling ) {
        siblings.add(sibling);
        BirthFamilyGT centroid = null;
    }

    private void check_centroid() {
        if( centroid == null ) {
            centroid = findCentroid( new ArrayList( siblings ) );
        }
    }
    /**
     * Find the most centre node of a group of Births based on distances between parents marriage attributes
     * @param children a collection of births from which to find the centremost
     * @return the centre most birth in the collection according to the metric.
     */
    private BirthFamilyGT findCentroid(ArrayList<BirthFamilyGT> children ) {
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
