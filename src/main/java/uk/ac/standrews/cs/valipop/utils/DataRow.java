package uk.ac.standrews.cs.valipop.utils;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
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
            values.put(labels[c].trim(), row[c].trim());
        }
    }

    public String getValue(String label) {
        return values.get(label);
    }

    public LocalDate getLocalDate(String label) throws InvalidInputFileException {
        try {
            return LocalDate.parse(getValue(label));
        } catch(DateTimeParseException e) {
            throw new InvalidInputFileException("Date incorrectly formatted");
        }
    }

    public int getInt(String label) throws InvalidInputFileException {
        try {
            return Integer.valueOf(getValue(label));
        } catch (NumberFormatException e) {
            throw new InvalidInputFileException("Integer not an integer");
        }
    }

    public Path getPath(String label) throws InvalidInputFileException {
        try {
            return Paths.get(getValue(label));
        } catch (InvalidPathException e) {
            throw new InvalidInputFileException("Path not a Path");
        }
    }

    public Set<String> getLabels() {
        return values.keySet();
    }

    public void setValue(String label, String value) {
        values.put(label, value);
    }

    public double getDouble(String label) throws InvalidInputFileException {
        try {
            return Double.valueOf(getValue(label));
        } catch (NumberFormatException e) {
            throw new InvalidInputFileException("Double not a Double");
        }
    }

    public Period getPeriod(String label) throws InvalidInputFileException {
        try {
            return Period.parse(getValue(label));
        } catch (DateTimeParseException e) {
            throw new InvalidInputFileException("Period not a Period");
        }
    }

    public boolean getBoolean(String label) {
        return Boolean.valueOf(getValue(label));
    }

    public String toString(List<String> order) {
        StringBuilder sb = new StringBuilder();

        for(String label : order) {
            sb.append(label);
            sb.append(": ");
            sb.append(values.get(label));
            sb.append(" ");
        }

        return sb.toString();
    }
}
