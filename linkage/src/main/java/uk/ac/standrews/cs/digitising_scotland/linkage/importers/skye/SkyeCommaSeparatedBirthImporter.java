package uk.ac.standrews.cs.digitising_scotland.linkage.importers.skye;

import uk.ac.standrews.cs.digitising_scotland.linkage.importers.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.commaSeparated.CommaSeparatedBirthImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.linkage.normalisation.DateNormalisation;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT.*;

/**
 * Utility classes for importing records in digitising scotland format
 * Created by al on 8/11/2016.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class SkyeCommaSeparatedBirthImporter extends CommaSeparatedBirthImporter {

    public static final String[][] RECORD_LABEL_MAP = {

            {ORIGINAL_ID, "ID"},

            {YEAR_OF_REGISTRATION, "year of reg"},

            {REGISTRATION_DISTRICT_SUFFIX, "RD identifier"},

            {REGISTRATION_DISTRICT_SUFFIX, "register identifier"},

            {ENTRY, "IOS_Entry no"},

            // *********************************

            {FORENAME, "child's forname(s)"}, {SURNAME, "child's surname"},

            {SEX, "sex"},

            // *********************************

            {MOTHERS_FORENAME, "mother's forename"}, {MOTHERS_MAIDEN_SURNAME, "mother's maiden surname"},

            {BIRTH_YEAR, "year"}, {BIRTH_DAY, "day"},

            {ILLEGITIMATE_INDICATOR, "illegitimate"},

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

            {INFORMANT_DID_NOT_SIGN, "did informant  sign?"},

            {FAMILY, "family"},

    };

    public static final String[] UNAVAILABLE_RECORD_LABELS = {

            // Fields not present in Kilmarnock dataset.

                    CHANGED_FORENAME, CHANGED_SURNAME, MOTHERS_SURNAME, CHANGED_MOTHERS_MAIDEN_SURNAME, CORRECTED_ENTRY, IMAGE_QUALITY, BIRTH_ADDRESS, ADOPTION, ILLEGITIMATE_INDICATOR, BIRTH_YEAR, BIRTH_DAY
    };


    @Override
    public String[][] get_record_map(){
        return RECORD_LABEL_MAP;
    }

    @Override
    public String[] get_unavailable_records() {
        return UNAVAILABLE_RECORD_LABELS;
    }

    /**
     * @param births   the bucket from which to import
     * @param births_source_path containing the source records in digitising scotland format
     * @return the number of records read in
     * @throws IOException
     * @throws RecordFormatException
     * @throws BucketException
     */
    public int importDigitisingScotlandBirths(IBucket<BirthFamilyGT> births, String births_source_path) throws IOException, RecordFormatException, BucketException, IOException {

        DataSet data = new DataSet(Paths.get(births_source_path));
        int count = 0;

        for (List<String> record : data.getRecords()) {

            BirthFamilyGT birth_record = importDigitisingScotlandBirth(data, record);
            births.makePersistent(birth_record);
            count++;
        }

        return count;
    }

    /**
     * Fills in a record.
     */
    private BirthFamilyGT importDigitisingScotlandBirth(DataSet data, List<String> record) throws IOException, RecordFormatException, IllegalKeyException {

        BirthFamilyGT birth = new BirthFamilyGT();

        addAvailableSingleFields(data, record, birth, RECORD_LABEL_MAP);
        addAvailableNormalisedFields(data, record, birth);
        addAvailableCompoundFields(data, record, birth);
        addUnavailableFields(birth, UNAVAILABLE_RECORD_LABELS);

        return birth;
    }

    @Override
    public void addAvailableCompoundFields(DataSet data, List<String> record, BirthFamilyGT birth) {

        birth.put(BIRTH_ADDRESS, combineFields(data, record, "address 1", "address 2", "address 3"));
        birth.put(INFORMANT, combineFields(data, record, "forename of informant", "surname of informant"));

        // TODO what to do with birth date??
    }

    @Override
    public void addAvailableNormalisedFields(DataSet data, List<String> record, BirthFamilyGT birth) {

        birth.put(BIRTH_MONTH, DateNormalisation.normaliseMonth(data.getValue(record, "month")));
    }
}
