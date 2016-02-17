package model.interfacesnew.dataStores.values;

/**
 * The interface Value query.
 *
 * @param <P> the type parameter
 * @param <S> the type parameter
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
