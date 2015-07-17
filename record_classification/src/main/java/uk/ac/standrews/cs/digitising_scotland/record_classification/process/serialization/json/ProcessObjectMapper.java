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
