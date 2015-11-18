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
package uk.ac.standrews.cs.digitising_scotland.record_classification.cli;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static uk.ac.standrews.cs.digitising_scotland.record_classification.cli.ConfigurationJsonSerializer.*;

/**
 * @author Masih Hajirab Derkani
 */
public class ConfigurationJsonDeserializer extends JsonDeserializer<Configuration> {

    @Override
    public Configuration deserialize(final JsonParser in, final DeserializationContext context) throws IOException {

        Configuration configuration = new Configuration();
        expectCurrent(in, JsonToken.START_OBJECT);
        expectNext(in, JsonToken.FIELD_NAME);
        String field_name = in.getCurrentName();
        while (field_name != null) {

            switch (field_name) {
                case DEFAULT_CHARSET_SUPPLIER:
                    expectNext(in, JsonToken.VALUE_STRING);
                    configuration.setDefaultCharsetSupplier(in.readValueAs(CharsetSupplier.class));
                    break;
                case DEFAULT_DELIMITER:
                    expectNext(in, JsonToken.VALUE_STRING);
                    configuration.setDefaultDelimiter(in.readValueAs(Character.TYPE));
                    break;
                case SEED:
                    expectNext(in, JsonToken.VALUE_NULL, JsonToken.VALUE_NUMBER_INT);
                    configuration.setSeed(in.readValueAs(Long.class));
                    break;
                case PROCEED_ON_ERROR:
                    expectNext(in, JsonToken.VALUE_TRUE, JsonToken.VALUE_FALSE);
                    configuration.setProceedOnError(in.readValueAs(Boolean.TYPE));
                    break;
                case CLASSIFIER_SUPPLIER:
                    expectNext(in, JsonToken.VALUE_NULL, JsonToken.VALUE_STRING);
                    configuration.setClassifierSupplier(in.readValueAs(ClassifierSupplier.class));
                    break;
                case CLASSIFIER_SERIALIZATION_FORMAT:
                    expectNext(in, JsonToken.VALUE_STRING);
                    configuration.setClassifierSerializationFormat(in.readValueAs(SerializationFormat.class));
                    break;
                case DEFAULT_CSV_FORMAT_SUPPLIER:
                    expectNext(in, JsonToken.VALUE_STRING);
                    configuration.setDefaultCsvFormatSupplier(in.readValueAs(CsvFormatSupplier.class));
                    break;
                case DEFAULT_TRAINING_RATIO:
                    expectNext(in, JsonToken.VALUE_NUMBER_FLOAT);
                    configuration.setDefaultTrainingRatio(in.readValueAs(Double.TYPE));
                    break;
                case DEFAULT_INTERNAL_TRAINING_RATIO:
                    expectNext(in, JsonToken.VALUE_NUMBER_FLOAT);
                    configuration.setDefaultInternalTrainingRatio(in.readValueAs(Double.TYPE));
                    break;
                case DEFAULT_LOG_LEVEL_SUPPLIER:
                    expectNext(in, JsonToken.VALUE_STRING);
                    configuration.setDefaultLogLevelSupplier(in.readValueAs(LogLevelSupplier.class));
                    break;
                default:
                    throw new JsonParseException("unknown configuration parameter", in.getCurrentLocation());
            }
            expectNext(in, JsonToken.FIELD_NAME, JsonToken.END_OBJECT);
            field_name = in.getCurrentName();
        }

        readUnseenRecords(configuration);
        readGoldStandardRecords(configuration);
        readClassifier(configuration);

        return configuration;
    }

    private void readClassifier(final Configuration configuration) throws IOException {

        final SerializationFormat format = configuration.getClassifierSerializationFormat();
        final Path source = ConfigurationJsonSerializer.getSerializedClassifierPath(configuration, format);

        if (Files.isRegularFile(source)) {
            configuration.setClassifier(Serialization.load(source, Classifier.class, format));
        }
    }

    private void readUnseenRecords(Configuration configuration) throws IOException {

        final Bucket unseen_records = loadBucketIfPresent(configuration.getUnseenRecordsPath());
        configuration.setUnseenRecords(unseen_records);
    }

    private Bucket loadBucketIfPresent(Path source) throws IOException {

        return Files.isRegularFile(source) ? Configuration.loadBucket(source) : null;
    }

    private void readGoldStandardRecords(Configuration configuration) throws IOException {

        readTrainingRecords(configuration);
        readEvaluationRecords(configuration);
    }

    private void readTrainingRecords(Configuration configuration) throws IOException {

        final Bucket training_records = loadBucketIfPresent(configuration.getTrainingRecordsPath());
        configuration.setTrainingRecords(training_records);
    }

    private void readEvaluationRecords(Configuration configuration) throws IOException {

        final Bucket evaluation_records = loadBucketIfPresent(configuration.getEvaluationRecordsPath());
        configuration.setEvaluationRecords(evaluation_records);
    }

    private void expectCurrent(final JsonParser in, final JsonToken expected) throws JsonParseException {

        final JsonToken actual = in.getCurrentToken();
        expectToken(in, actual, expected);
    }

    private void expectToken(final JsonParser in, final JsonToken actual, final JsonToken... expected) throws JsonParseException {

        if (!Arrays.stream(expected).filter(actual::equals).findAny().isPresent()) {
            throw new JsonParseException(String.format("expected %s, found %s", Arrays.toString(expected), actual), in.getCurrentLocation());
        }
    }

    private void expectNext(final JsonParser in, JsonToken... expected) throws IOException {

        final JsonToken actual = in.nextToken();
        expectToken(in, actual, expected);
    }
}
