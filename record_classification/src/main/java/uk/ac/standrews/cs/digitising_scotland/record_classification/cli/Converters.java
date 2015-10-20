package uk.ac.standrews.cs.digitising_scotland.record_classification.cli;

import com.beust.jcommander.*;

/**
 * @author masih
 */
public final class Converters {

    private Converters() { throw new UnsupportedOperationException(); }

    
    public static class CharacterConverter implements IStringConverter<Character> {

        @Override
        public Character convert(final String value) {

            return value.charAt(0);
        }
    }
}
