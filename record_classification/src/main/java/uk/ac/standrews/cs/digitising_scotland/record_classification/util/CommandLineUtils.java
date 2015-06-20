package uk.ac.standrews.cs.digitising_scotland.record_classification.util;

import com.beust.jcommander.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;

import java.io.*;

/**
 * @author Masih Hajiarab Derkani
 */
public final class CommandLineUtils {

    private CommandLineUtils() { throw new UnsupportedOperationException(); }

    public static class CleanerConverter implements IStringConverter<ConsistentCodingCleaner> {

        @Override
        public ConsistentCodingCleaner convert(final String value) {

            return ConsistentCodingCleaner.valueOf(value);
        }
    }

    public static class FileConverter implements IStringConverter<File> {

        @Override
        public File convert(final String value) {

            return new File(value);
        }
    }
}
