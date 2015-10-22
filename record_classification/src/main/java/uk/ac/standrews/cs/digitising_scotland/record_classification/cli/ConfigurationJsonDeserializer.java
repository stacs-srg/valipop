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
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

import static uk.ac.standrews.cs.digitising_scotland.record_classification.cli.ConfigurationJsonSerializer.*;
import static uk.ac.standrews.cs.digitising_scotland.record_classification.cli.ConfigurationJsonSerializer.GoldStandardJsonSerializer.*;
import static uk.ac.standrews.cs.digitising_scotland.record_classification.cli.ConfigurationJsonSerializer.ResourceJsonSerializer.*;

/**
 * @author masih
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
                case DICTIONARIES:
                    populateResources(in, name -> configuration.newDictionary(name, false));
                    break;
                case STOP_WORDS:
                    populateResources(in, name -> configuration.newStopWords(name, false));
                    break;
                case UNSEENS:
                    populateResources(in, name -> configuration.newUnseen(name, false));
                    break;
                case GOLD_STANDARDS:
                    populateGoldStandards(in, configuration);
                    break;
                default:
                    throw new JsonParseException("unknown configuration parameter", in.getCurrentLocation());
            }
            expectNext(in, JsonToken.FIELD_NAME, JsonToken.END_OBJECT);
            field_name = in.getCurrentName();
        }

        return configuration;
    }

    private void populateResources(JsonParser in, final Consumer<String> handler) throws IOException {

        expectNext(in, JsonToken.START_ARRAY);

        JsonToken token = in.nextToken();
        while (token != JsonToken.END_ARRAY) {
            expectCurrent(in, JsonToken.START_OBJECT);
            expectNextFieldName(in, NAME);

            expectNext(in, JsonToken.VALUE_STRING);
            final String name = in.getValueAsString();

            handler.accept(name);

            expectNext(in, JsonToken.END_OBJECT);
            token = in.nextToken();
        }
    }

    private void expectCurrent(final JsonParser in, final JsonToken expected) throws JsonParseException {

        final JsonToken actual = in.getCurrentToken();
        expectToken(in, actual, expected);
    }

    private void populateGoldStandards(JsonParser in, Configuration configuration) throws IOException {

        expectNext(in, JsonToken.START_ARRAY);

        JsonToken token = in.nextToken();
        while (token != JsonToken.END_ARRAY) {
            expectCurrent(in, JsonToken.START_OBJECT);

            expectNextFieldName(in, NAME);
            expectNext(in, JsonToken.VALUE_STRING);
            final String name = in.getValueAsString();

            expectNextFieldName(in, TRAINING_RATIO);
            expectNext(in, JsonToken.VALUE_NUMBER_FLOAT);
            final double training_ratio = in.getValueAsDouble();

            configuration.newGoldStandard(name, training_ratio, false);

            expectNext(in, JsonToken.END_OBJECT);
            token = in.nextToken();
        }
    }

    private void expectNextFieldName(final JsonParser in, final String expected) throws IOException {

        expectNext(in, JsonToken.FIELD_NAME);
        final String actual = in.getCurrentName();
        if (!expected.equals(actual)) {
            throw new JsonParseException(String.format("expected field name called %s, found %s instead", expected, actual), in.getCurrentLocation());
        }
    }

    private void expectNext(final JsonParser in, JsonToken... expected) throws IOException {

        final JsonToken actual = in.nextToken();
        expectToken(in, actual, expected);
    }

    private void expectToken(final JsonParser in, final JsonToken actual, final JsonToken... expected) throws JsonParseException {

        if (!Arrays.stream(expected).filter(actual::equals).findAny().isPresent()) {
            throw new JsonParseException(String.format("expected %s, found %s", Arrays.toString(expected), actual), in.getCurrentLocation());
        }
    }
}
