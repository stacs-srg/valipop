package model.interfacesnew.dataStores.query;

/**
 * A ValueQuery is used to define a particular value to be retrieved from a data store.
 *
 * @param <P> the Primary Variable
 * @param <S> the Secondary Variable
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface ValueQuery<P, S> {

    /**
     * Primary variable p.
     *
     * @return the p
     */
    P primaryVariable();

    /**
     * Secondary variable s.
     *
     * @return the s
     */
    S secondaryVariable();

}
