package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.util;

import com.google.common.io.*;
import com.google.common.io.Files;
import edu.umd.cs.findbugs.charsets.*;
import org.junit.*;
import org.junit.rules.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.dataset.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import static org.junit.Assert.*;

/**
 * @author masih
 */
public class ArgumentsTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();
    
    @Test
    public void testCommentRecognition() throws Exception {
        
        assertTrue(Arguments.isComment("# some comment"));
        assertTrue(Arguments.isComment("         # some comment"));
        assertTrue(Arguments.isComment("\t# some comment"));
        assertFalse(Arguments.isComment("not some comment"));
        assertFalse(Arguments.isComment("\tnot some comment"));
        assertFalse(Arguments.isComment("    not some comment"));

    }

    @Test
    public void testCommandParse() throws Exception {

        final Path commands = temp.newFile().toPath();

        final TestResource commands_with_comment = new TestResource(Arguments.class, "test_batch_with_comments.txt");
        commands_with_comment.copy(commands, StandardCharsets.UTF_8);

        final TestResource commands_without_comment = new TestResource(Arguments.class, "test_batch_parsed_one_argument_per_line.txt");
        final List<String> expected = commands_without_comment.readLines();
        
        final List<String[]> actual = Arguments.parseBatchCommandFile(commands, StandardCharsets.UTF_8).collect(Collectors.toList());

        int index = 0;
        for (String[] arguments : actual) {
            for (String argument : arguments) {
                assertEquals(expected.get(index++), argument);
            }
        }
    }
}
