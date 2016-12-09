package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;

import uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.sharder.*;
import uk.ac.standrews.cs.storr.interfaces.*;

import static org.apache.commons.codec.language.RefinedSoundex.US_ENGLISH;

public abstract class AbstractBlocker<T extends ILXP> extends Blocker<T> {

    /**
     * @param input the stream over which to block
     * @param output_repo - the repository into which results are written
     * @param factory
     */
    public AbstractBlocker(final IInputStream<T> input, final IRepository output_repo, final ILXPFactory<T> factory) {

        super(input, output_repo, factory);
    }

    protected String concatenate(String... attributes) {

        StringBuilder builder = new StringBuilder();

        for (String attribute : attributes) {

            if (builder.length() > 0) {
                builder.append("-");
            }
            builder.append(attribute);
        }

        return clean(builder.toString());
    }

    protected String normaliseName(String name) {

        return US_ENGLISH.soundex(name);
    }

    protected String normalisePlace(String place) {

        if (place.equals("") || place.equals("na") || place.equals("ng")) {
            return "___";
        }
        else {
            return place;
        }
    }

    private String clean(final String s) {

        return s.replace("/", "_").replace("\"", "").replace(" ", "");
    }
}
