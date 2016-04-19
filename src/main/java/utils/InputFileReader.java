package utils;

import uk.ac.standrews.cs.util.dataset.DataSet;
import uk.ac.standrews.cs.util.dataset.Mapper;
import uk.ac.standrews.cs.util.tools.FileManipulation;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class InputFileReader {

    private static final String TAB = "\t";
    private static final String COMMENT_INDICATOR = "#";

    public static String[] getAllLines(String path) {

        ArrayList<String> lines = new ArrayList<String>();
        String line;



        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {

            while((line = reader.readLine())!=null) {

                if (line.startsWith(COMMENT_INDICATOR)) {
                    continue;
                } else {
                    lines.add(line);
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return lines.toArray(new String[lines.size()]);


    }
}
