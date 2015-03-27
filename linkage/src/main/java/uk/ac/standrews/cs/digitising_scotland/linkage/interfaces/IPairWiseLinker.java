package uk.ac.standrews.cs.digitising_scotland.linkage.interfaces;


import uk.ac.standrews.cs.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.jstore.interfaces.IOutputStream;

/**
 * Created by al on 21/05/2014.
 */
public interface IPairWiseLinker<T extends ILXP> {

    void pairwiseUnify();

    float compare(T first, T second);

    /**
     * Adds a matched result to a result collection.
     * @param pair
     * @param differentness
     */
    void addToResults(final IPair pair, float differentness, final IOutputStream results);
}
