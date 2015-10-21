package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.command;

import com.beust.jcommander.*;
import org.junit.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.*;

import java.nio.file.*;
import java.util.*;
import java.util.function.*;

import static org.junit.Assert.*;

/**
 * @author Masih Hajiarab Derkani
 */
public class LoadCommandTest extends CommandTest {

    @Test(expected = ParameterException.class)
    public void testMissingSourceFailure() throws Exception {

        run(LoadCommand.NAME);
    }

    @Test(expected = ParameterException.class)
    public void testMissingCommandFailure() throws Exception {

        run(LoadCommand.NAME, LoadCommand.OPTION_SOURCE_SHORT, "test");
    }
}
