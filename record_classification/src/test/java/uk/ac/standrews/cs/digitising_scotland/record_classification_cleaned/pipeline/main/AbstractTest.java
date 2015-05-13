package uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.pipeline.main;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.junit.Before;
import org.slf4j.LoggerFactory;

public class AbstractTest {

    public static final String PACKAGE_ROOT = "uk.ac.standrews.cs.digitising_scotland";

    @Before
    public void setup() {
        Logger logger = (Logger) LoggerFactory.getLogger(PACKAGE_ROOT);
        logger.setLevel(Level.INFO);
    }

    protected static String getResourceFilePath(Class the_class, String resource_file_name) {

        return the_class.getResource(resource_file_name).getFile();
    }
}
