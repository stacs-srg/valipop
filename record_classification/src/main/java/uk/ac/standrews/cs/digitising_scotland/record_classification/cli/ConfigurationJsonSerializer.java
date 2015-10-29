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
import org.apache.commons.lang3.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * @author Masih Hajiarab Derkani
 */
class ConfigurationJsonSerializer extends JsonSerializer<Configuration> {

    private static final ResourceJsonSerializer RESOURCE_JSON_SERIALIZER = new ResourceJsonSerializer();
    private static final GoldStandardJsonSerializer GOLD_STANDARD_JSON_SERIALIZER = new GoldStandardJsonSerializer();
    protected static final String DEFAULT_CHARSET_SUPPLIER = "default_charset_supplier";
    protected static final String DEFAULT_DELIMITER = "default_delimiter";
    protected static final String SEED = "seed";
    protected static final String PROCEED_ON_ERROR = "proceed_on_error";
    protected static final String CLASSIFIER_SUPPLIER = "classifier_supplier";
    protected static final String CLASSIFIER_SERIALIZATION_FORMAT = "classifier_serialization_format";
    protected static final String DEFAULT_CSV_FORMAT_SUPPLIER = "default_csv_format_supplier";
    protected static final String DEFAULT_TRAINING_RATIO = "default_training_ratio";
    protected static final String DEFAULT_INTERNAL_TRAINING_RATIO = "default_internal_training_ratio";
    protected static final String UNSEENS = "unseens";
    protected static final String GOLD_STANDARDS = "gold_standards";
    public static final String SERIALIZED_CLASSIFIER_FILE_NAME_PREFIX = "classifier";

    @Override
    public void serialize(final Configuration configuration, final JsonGenerator out, final SerializerProvider serializers) throws IOException {

        out.writeStartObject();

        out.writeObjectField(DEFAULT_CHARSET_SUPPLIER, configuration.getDefaultCharsetSupplier());
        out.writeObjectField(DEFAULT_DELIMITER, configuration.getDefaultDelimiter());
        out.writeObjectField(SEED, configuration.getSeed());
        out.writeBooleanField(PROCEED_ON_ERROR, configuration.isProceedOnErrorEnabled());
        out.writeObjectField(CLASSIFIER_SUPPLIER, configuration.getClassifierSupplier());
        out.writeObjectField(CLASSIFIER_SERIALIZATION_FORMAT, configuration.getClassifierSerializationFormat());
        out.writeObjectField(DEFAULT_CSV_FORMAT_SUPPLIER, configuration.getDefaultCsvFormatSupplier());
        out.writeNumberField(DEFAULT_TRAINING_RATIO, configuration.getDefaultTrainingRatio());
        out.writeNumberField(DEFAULT_INTERNAL_TRAINING_RATIO, configuration.getDefaultInternalTrainingRatio());

        writeResourceList(UNSEENS, configuration.getUnseens(), out, serializers);
        writeGoldStandards(configuration, out, serializers);
        writeClassifier(configuration, out, serializers);

        out.writeEndObject();
    }

    private void writeClassifier(final Configuration configuration, final JsonGenerator out, final SerializerProvider serializers) throws IOException {

        final Optional<Classifier> classifier = configuration.getClassifier();
        if (classifier.isPresent()) {
            final SerializationFormat format = configuration.getClassifierSerializationFormat();

            final Path destination = getSerializedClassifierPath(format);
            Serialization.persist(destination, classifier.get(), format);
        }
    }

    private void writeGoldStandards(final Configuration configuration, final JsonGenerator out, final SerializerProvider serializers) throws IOException {

        out.writeArrayFieldStart(GOLD_STANDARDS);
        for (Configuration.GoldStandard gold_standard : configuration.getGoldStandards()) {
            GOLD_STANDARD_JSON_SERIALIZER.serialize(gold_standard, out, serializers);
        }
        out.writeEndArray();
    }

    private void writeResourceList(String field_name, final List<? extends Configuration.Resource> resources, final JsonGenerator out, final SerializerProvider serializers) throws IOException {

        out.writeArrayFieldStart(field_name);
        for (Configuration.Resource resource : resources) {
            RESOURCE_JSON_SERIALIZER.serialize(resource, out, serializers);
        }
        out.writeEndArray();
    }

    static Path getSerializedClassifierPath(SerializationFormat format) {

        switch (format) {
            case JAVA_SERIALIZATION:
                return Configuration.CLI_HOME.resolve(SERIALIZED_CLASSIFIER_FILE_NAME_PREFIX + ".object");
            case JSON:
                return Configuration.CLI_HOME.resolve(SERIALIZED_CLASSIFIER_FILE_NAME_PREFIX + ".json");
            case JSON_COMPRESSED:
                return Configuration.CLI_HOME.resolve(SERIALIZED_CLASSIFIER_FILE_NAME_PREFIX + ".object");
            default:
                throw new RuntimeException("unsupported classifier serialization format: " + format);
        }
    }

    static class ResourceJsonSerializer extends JsonSerializer<Configuration.Resource> {

        public static final String NAME = "name";

        @Override
        public void serialize(final Configuration.Resource resource, final JsonGenerator out, final SerializerProvider serializers) throws IOException {

            out.writeStartObject();
            out.writeStringField(NAME, resource.getName());
            out.writeEndObject();

            resource.persist();
        }
    }

    static class GoldStandardJsonSerializer extends JsonSerializer<Configuration.GoldStandard> {

        public static final String TRAINING_RATIO = "training_ratio";

        @Override
        public void serialize(final Configuration.GoldStandard gold_standard, final JsonGenerator out, final SerializerProvider serializers) throws IOException {

            out.writeStartObject();
            out.writeStringField(ResourceJsonSerializer.NAME, gold_standard.getName());
            out.writeNumberField(TRAINING_RATIO, gold_standard.getTrainingRatio());
            out.writeEndObject();

            gold_standard.persist();
        }
    }

}
