package uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.filter;

import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.interfaces.IFilter;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;
import uk.ac.standrews.cs.storr.interfaces.ILXP;
import uk.ac.standrews.cs.storr.interfaces.IOutputStream;

/**
 * Created by al on 28/04/2014.
 */
public abstract class Filter<T extends ILXP> implements IFilter<T> {

    private final IInputStream<T> input;
    private final IOutputStream<T> output;

    public Filter(final IInputStream<T> input, final IOutputStream<T> output) {
        this.input = input;
        this.output = output;
    }

    public void apply() throws BucketException {

        for (T record : input) {
            if (select(record)) {
                output.add(record);
            }
        }
    }

    public IInputStream<T> getInput() {
        return input;
    }

    public IOutputStream<T> getOutput() {
        return output;
    }
}
