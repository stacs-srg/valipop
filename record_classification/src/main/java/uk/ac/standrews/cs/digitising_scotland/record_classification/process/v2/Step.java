package uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2;

import java.io.*;

/**
 * Presents a step in a {@link ClassificationProcess classification process}.
 *
 * @author Masih Hajiarab Derkani
 */
public interface Step extends Serializable {

    /**
     * Performs this step in the given context.
     *
     * @param context the context in which to perform this step
     */
    void perform(Context context) throws Exception;
}
