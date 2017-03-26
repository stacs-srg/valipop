package uk.ac.standrews.cs.digitising_scotland.linkage.importers.skye;

import uk.ac.standrews.cs.digitising_scotland.linkage.importers.commaSeparated.CommaSeparatedBirthImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.linkage.normalisation.DateNormalisation;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.util.List;

import static uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT.*;

/**
 * Utility classes for importing records in digitising scotland format
 * Created by al on 21/3/2017.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class SkyeCommaSeparatedBirthImporter extends CommaSeparatedBirthImporter {

    public static final String[][] RECORD_LABEL_MAP = {

            {ORIGINAL_ID, "ID"},

            {YEAR_OF_REGISTRATION, "year of reg"},

            {REGISTRATION_DISTRICT_SUFFIX, "RD Identifier"},

            {REGISTRATION_DISTRICT_SUFFIX, "register identifier"},

            {ENTRY, "IOS_Entry no"},

            // *********************************

            {FORENAME, "child's forname(s)"}, {SURNAME, "child's surname"},

            {SEX, "sex"},

            // *********************************

            {MOTHERS_FORENAME, "mother's forename"}, {MOTHERS_MAIDEN_SURNAME, "mother's maiden surname"},

            // *********************************

            {FATHERS_FORENAME, "father's forename"}, {FATHERS_SURNAME, "father's surname"},

            // *********************************

            {PARENTS_DAY_OF_MARRIAGE, "day of parents' marriage"},

            {PARENTS_MONTH_OF_MARRIAGE, "month of parents' marriage"},

            {PARENTS_YEAR_OF_MARRIAGE, "year of parents' marriage"},

            {PARENTS_PLACE_OF_MARRIAGE, "place of parent's marriage 1"},

            {FATHERS_OCCUPATION, "father's occupation"},

            {INFORMANT_DID_NOT_SIGN, "did inform sign?"},

            {FAMILY, "family"},

    };

    public static final String[] UNAVAILABLE_RECORD_LABELS = {

            // Fields not present in Kilmarnock dataset.

            ILLEGITIMATE_INDICATOR, CHANGED_FORENAME, CHANGED_SURNAME, MOTHERS_SURNAME, CHANGED_MOTHERS_MAIDEN_SURNAME, CORRECTED_ENTRY, IMAGE_QUALITY, BIRTH_ADDRESS, ADOPTION, ILLEGITIMATE_INDICATOR, BIRTH_YEAR, BIRTH_DAY
    };

    @Override
    public String[][] get_record_map() {
        return RECORD_LABEL_MAP;
    }

    @Override
    public String[] get_unavailable_records() {
        return UNAVAILABLE_RECORD_LABELS;
    }

    @Override
    public void addAvailableCompoundFields(DataSet data, List<String> record, BirthFamilyGT birth) {

        birth.put(BIRTH_ADDRESS, combineFields(data, record, "address 1", "address 2"));
        birth.put(INFORMANT, combineFields(data, record, "forename of informant", "surname of informant"));
    }

    @Override
    public void addAvailableNormalisedFields(DataSet data, List<String> record, BirthFamilyGT birth) {

        String dob = data.getValue(record, "birth date"); // These are of the form 7/4/1861, 25/4/1861 etc.
        String[] dob_parts = dob.split("/");

        if (dob_parts.length > 0) {
            birth.put(BIRTH_DAY, DateNormalisation.normaliseDay(dob_parts[0]));
        }
        if (dob_parts.length > 1) {
            birth.put(BIRTH_MONTH, DateNormalisation.normaliseMonth(dob_parts[1]));
        }
        if (dob_parts.length > 2) {
            birth.put(BIRTH_YEAR, dob_parts[2]);
        }
    }
}
