package utils;

import uk.ac.standrews.cs.util.dataset.DataSet;
import uk.ac.standrews.cs.util.dataset.Mapper;
import uk.ac.standrews.cs.util.tools.FileManipulation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class InputFileReader {

    private static final String TAB = "\t";
    private static final String COMMENT_INDICATOR = "#";

    public static Collection<String> getAllLines(Path path) {

        Collection<String> lines = new ArrayList<String>();
        String line;

        try (BufferedReader reader = Files.newBufferedReader(path)) {

            while((line = reader.readLine()) != null) {

                if (line.startsWith(COMMENT_INDICATOR)) {
                    continue;
                } else {
                    lines.add(line);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;


    }
}
