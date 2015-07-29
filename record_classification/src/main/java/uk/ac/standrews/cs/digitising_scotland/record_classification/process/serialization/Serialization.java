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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.SerializationUtils;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.json.ProcessObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Serialization {

    private static final String SERIALIZED_CONTEXT_NAME = "context";

    private static final String JSON_SUFFIX = "json";
    private static final String JSON_COMPRESSED_SUFFIX = "json.gz";
    private static final String SERIALIZED_SUFFIX = "serialized";

    private static final ObjectMapper JSON_MAPPER = new ProcessObjectMapper();

    public static Path getSerializedContextPath(Path process_directory, String name, SerializationFormat serialization_format) {

        return getProcessWorkingDirectory(process_directory, name).resolve(SERIALIZED_CONTEXT_NAME + "." + getSerializedContextSuffix(serialization_format));
    }

    public static Path getProcessWorkingDirectory(Path process_directory, String process_name) {

        // If the process directory is not specified, create a new directory within
        // the current working directory.
        return process_directory == null ? Paths.get(process_name) : process_directory.resolve(process_name);
    }

    public static void persistContext(ClassificationContext context, Path process_directory, String process_name, SerializationFormat serialization_format) throws IOException {

        Path serialized_context_path = getSerializedContextPath(process_directory, process_name, serialization_format);

        if (serialization_format == SerializationFormat.JAVA_SERIALIZATION) {

            persistContextToSerializedFile(context, serialized_context_path);
        } else {
            persistContextToJSONFile(context, serialized_context_path, serialization_format);
        }
    }

    public static ClassificationContext loadContext(Path process_directory, String process_name, SerializationFormat serialization_format) throws IOException {

        Path serialized_context_path = getSerializedContextPath(process_directory, process_name, serialization_format);

        if (serialization_format == SerializationFormat.JAVA_SERIALIZATION) {

            return loadContextFromSerializedFile(serialized_context_path);
        } else {
            return loadContextFromJSONFile(serialized_context_path, serialization_format);
        }
    }

    private static void persistContextToSerializedFile(ClassificationContext context, Path serialized_context_path) throws IOException {

        final byte[] process_bytes = SerializationUtils.serialize(context);
        Files.write(serialized_context_path, process_bytes);
    }

    private static void persistContextToJSONFile(final ClassificationContext context, Path serialized_context_path, SerializationFormat serialization_format) throws IOException {

        OutputStream output_stream = Files.newOutputStream(serialized_context_path);
        if (serialization_format == SerializationFormat.JSON_COMPRESSED) {
            output_stream = new GZIPOutputStream(output_stream);
        }

        JSON_MAPPER.writeValue(output_stream, context);
    }

    private static ClassificationContext loadContextFromSerializedFile(Path serialized_context_path) throws IOException {

        if (!Files.isRegularFile(serialized_context_path)) {

            throw new IOException("expected context file '" + serialized_context_path + "' not found.");
        }

        final byte[] process_bytes = Files.readAllBytes(serialized_context_path);
        return (ClassificationContext) SerializationUtils.deserialize(process_bytes);
    }

    private static ClassificationContext loadContextFromJSONFile(Path serialized_context_path, SerializationFormat serialization_format) throws IOException {

        InputStream input_stream = Files.newInputStream(serialized_context_path);
        if (serialization_format == SerializationFormat.JSON_COMPRESSED) {
            input_stream = new GZIPInputStream(input_stream);
        }

        return JSON_MAPPER.readValue(input_stream, new TypeReference<ClassificationContext>() {
        });
    }

    private static String getSerializedContextSuffix(SerializationFormat serialization_format) {

        switch (serialization_format) {
            case JAVA_SERIALIZATION:
                return SERIALIZED_SUFFIX;
            case JSON:
                return JSON_SUFFIX;
            default:
                return JSON_COMPRESSED_SUFFIX;
        }
    }
}
