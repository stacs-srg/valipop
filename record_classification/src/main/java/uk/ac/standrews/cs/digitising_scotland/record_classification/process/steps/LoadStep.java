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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps;

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Charsets;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.Step;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;

/**
 * Loads gold standard records from a file into a classification process {@link ClassificationContext context}.
 *
 * @author Masih Hajiarab Derkani
 */
public abstract class LoadStep implements Step {

    private static final long serialVersionUID = 774282123424314041L;

    private final Path path;
    private final Charset charset;
    private final char delimiter;

    public static final Charsets DEFAULT_CHARSET = Charsets.UTF_8;

    public static final char DEFAULT_DELIMITER = ',';

    /**
     * Instantiates a new step which loads a gold standard CSV file into a classification process {@link ClassificationContext context}.
     *
     * @param path the file to the CSV file
     */
    public LoadStep(Path path) {

        this(path, DEFAULT_CHARSET.get(), DEFAULT_DELIMITER);
    }

    public LoadStep(Path path, Charset charset, char delimiter) {

        this.path = path;
        this.charset = charset;
        this.delimiter = delimiter;
    }

    @Override
    public void perform(final ClassificationContext context)  {

        try (final BufferedReader reader = Files.newBufferedReader(path, charset)) {

            getRecords(context).add(new Bucket(reader, delimiter));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Bucket getRecords(ClassificationContext context);
}
