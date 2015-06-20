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
