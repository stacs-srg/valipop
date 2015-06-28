/*
 * Copyright 2015 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.util;

import com.beust.jcommander.*;
import org.apache.commons.lang3.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.*;

import java.io.*;
import java.util.*;

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

    public static class ClassifierConverter implements IStringConverter<Classifier> {

        /** Names of classifiers supported by this converter. **/
        public static final Set<String> NAMES;
        private static final Map<String, Classifier> NAMED_CLASSIFIERS = new HashMap<>();

        static {
            NAMED_CLASSIFIERS.put("dummy", new DummyClassifier());
            NAMED_CLASSIFIERS.put("exact-match", new ExactMatchClassifier());
            NAMED_CLASSIFIERS.put("string-similarity-jarowinkler", new StringSimilarityClassifier(StringSimilarityMetric.JARO_WINKLER));

            NAMES = NAMED_CLASSIFIERS.keySet();
        }

        @Override
        public Classifier convert(final String value) {

            if (NAMED_CLASSIFIERS.containsKey(value)) {
                return copy(NAMED_CLASSIFIERS.get(value));
            }

            throw new ParameterException("no classifier named " + value);
        }

    }

    private static <T extends Serializable> T copy(T original) {

        return (T) SerializationUtils.deserialize(SerializationUtils.serialize(original));
    }
}
