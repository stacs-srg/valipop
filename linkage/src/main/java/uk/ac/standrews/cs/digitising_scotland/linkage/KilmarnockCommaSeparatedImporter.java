package uk.ac.standrews.cs.digitising_scotland.linkage;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.digitising_scotland.util.*;
import uk.ac.standrews.cs.storr.impl.exceptions.*;
import uk.ac.standrews.cs.storr.interfaces.*;
import uk.ac.standrews.cs.util.dataset.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth.*;

/**
 * Utility classes for importing records in digitising scotland format
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class KilmarnockCommaSeparatedImporter {

    protected static void addAvailableSingleFields(final DataSet data, final List<String> record, final AbstractLXP lxp_record, final String[][] label_map) {

        for (String[] field : label_map) {
            lxp_record.put(field[0], data.getValue(record, field[1]));
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
