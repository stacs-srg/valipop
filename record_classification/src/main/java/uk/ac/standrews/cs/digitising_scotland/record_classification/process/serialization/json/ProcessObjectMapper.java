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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.mahout.math.DenseMatrix;

import java.io.IOException;
import java.time.Duration;
import java.time.format.DateTimeParseException;

public class ProcessObjectMapper extends ObjectMapper {

    public ProcessObjectMapper() {

        // Needed for classes with private fields without setters e.g. Bucket.
        setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        // Needed for fields typed as interfaces or abstract classes e.g. 'classifier' in ClassificationContext.
        enableDefaultTyping();

        // Needed for getter methods that don't correspond to fields e.g. 'getDescription' in StringSimilarityMetricWrapper.
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // Needed for non-camel-case field names e.g. in ClassificationContext.
        setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

        // Needed for .
        disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // To make serialized data easier to read.
        enable(SerializationFeature.INDENT_OUTPUT);

        SimpleModule module = new SimpleModule();

        configureForDuration(module);
        configureForDenseMatrix(module);

        registerModule(module);
    }

    private void configureForDuration(SimpleModule module) {

        module.addSerializer(Duration.class, new JsonSerializer<Duration>() {
            @Override
            public void serialize(Duration duration, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

                jsonGenerator.writeString(duration.toString());
            }
        });
        module.addDeserializer(Duration.class, new JsonDeserializer<Duration>() {
            @Override
            public Duration deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

                try {
                    return Duration.parse(jsonParser.getText());
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        });
    }

    private void configureForDenseMatrix(SimpleModule module) {

        module.setMixInAnnotation(DenseMatrix.class, Mixin.class);
    }
}
