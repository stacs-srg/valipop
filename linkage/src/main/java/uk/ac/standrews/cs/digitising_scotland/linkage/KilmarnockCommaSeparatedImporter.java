package uk.ac.standrews.cs.digitising_scotland.linkage;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.AbstractLXP;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.util.List;

/**
 * Utility classes for importing records in digitising scotland format
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class KilmarnockCommaSeparatedImporter {

    protected static void addAvailableSingleFields(final DataSet data, final List<String> record, final AbstractLXP lxp_record, final String[][] label_map) {

        for (String[] field : label_map) {
            try {
                lxp_record.put(field[0], data.getValue(record, field[1]));
            }
            catch (ArrayIndexOutOfBoundsException e) {
                throw e;
            }
        }
    }

    protected static void addUnavailableFields(final AbstractLXP lxp_record, final String[] unavailable_record_labels) {

        for (String field : unavailable_record_labels) {
            lxp_record.put(field, "");
        }
    }

    protected static String combineFields(final DataSet data, final List<String> record, String... source_field_labels) {

        StringBuilder builder = new StringBuilder();

        for (String source_field_label : source_field_labels) {

            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(data.getValue(record, source_field_label));
        }

        return builder.toString();
    }
}
