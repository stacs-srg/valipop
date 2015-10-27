package uk.ac.standrews.cs.digitising_scotland.record_classification.cli;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;

/**
 * @author Masih Hajiarab Derkani
 */
public final class CAIParameters {

    private CAIParameters() { throw new UnsupportedOperationException(); }

    //TODO move option name constants here

    public static final class ClassifierParameter {

        /** The short name of the option that specifies the {@link ClassifierSupplier classifier}. */
        public static final String SHORT = "-c";

        /** The long name of the option that specifies the {@link ClassifierSupplier classifier}. */
        public static final String LONG = "--classifier";
    }
}
