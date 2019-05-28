package uk.ac.standrews.cs.valipop.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DataRow {

    private Map<String, String> values = new HashMap<>();

    public DataRow(String labelsRow, String csvRow) throws InvalidInputFileException {
        String[] labels = labelsRow.split(",");
        String[] row = csvRow.split(",");

        if(labels.length != row.length) throw new InvalidInputFileException("Differing number of rows to labels");

        for(int c = 0; c < labels.length; c++) {
            values.put(labels[c], row[c]);
        }
    }

    public String getValue(String label) {
        return values.get(label);
    }

    public Set<String> getLabels() {
        return values.keySet();
    }

}
