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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.steps;

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

/**
 * Loads gold standard records from a CSV file into a classification process {@link Context context}.
 *
 * @author Masih Hajiarab Derkani
 */
public class LoadGoldStandardFromCSVFile implements Step {

    private static final long serialVersionUID = 7742825393693404041L;
    private final File csv;
    private transient Charset charset;

    /**
     * Instantiates a new step which loads a gold standard CSV file into a classification process {@link Context context} with {@link StandardCharsets#UTF_8 UTF8} charset.
     *
     * @param string_path the csv to the CSV file
     */
    public LoadGoldStandardFromCSVFile(String string_path) {

        this(new File(string_path), StandardCharsets.UTF_8);
    }

    /**
     * Instantiates a new step which loads a gold standard CSV file into a classification process {@link Context context}.
     *
     * @param string_path the csv to the CSV file
     * @param charset the charset of the CSV file
     */
    public LoadGoldStandardFromCSVFile(String string_path, Charset charset) {

        this(new File(string_path), charset);
    }

    /**
     * Instantiates a new step which loads a gold standard CSV file into a classification process {@link Context context}.
     *
     * @param csv the csv to the CSV file
     * @param charset the charset of the CSV file
     */
    public LoadGoldStandardFromCSVFile(final File csv, Charset charset) {

        this.csv = csv;
        this.charset = charset;
    }

    @Override
    public void perform(final Context context) throws Exception {

        try (final BufferedReader reader = Files.newBufferedReader(csv.toPath(), charset)) {
            final Bucket gold_standard = new Bucket(reader);
            context.setGoldStandard(gold_standard);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {

        out.defaultWriteObject();
        out.writeObject(charset.toString());
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {

        in.defaultReadObject();
        final String charset_name = (String) in.readObject();
        charset = Charset.forName(charset_name);
    }
}
