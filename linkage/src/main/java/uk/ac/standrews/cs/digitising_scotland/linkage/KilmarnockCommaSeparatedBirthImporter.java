package uk.ac.standrews.cs.digitising_scotland.linkage;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.normalisation.normaliseDates;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT.*;

/**
 * Utility classes for importing records in digitising scotland format
 * Created by al on 8/11/2016.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class KilmarnockCommaSeparatedBirthImporter extends KilmarnockCommaSeparatedImporter {

    public static final String[][] RECORD_LABEL_MAP = {

                    {ORIGINAL_ID, "ID"},

                    {YEAR_OF_REGISTRATION, "year of reg"},

                    {REGISTRATION_DISTRICT_NUMBER, "rd identifier"},

                    {REGISTRATION_DISTRICT_SUFFIX, "register identifier"},

                    {ENTRY, "entry no"},

                    // *********************************

                    {FORENAME, "child's forname(s)"}, {SURNAME, "child's surname"},

                    {SEX, "sex"},

                    // *********************************

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

                    {FAMILY_BEWARE, "family beware" }

    };

    public static final String[] UNAVAILABLE_RECORD_LABELS = {

                    // Fields not present in Kilmarnock dataset.

                    CHANGED_FORENAME, CHANGED_SURNAME, MOTHERS_SURNAME, CHANGED_MOTHERS_MAIDEN_SURNAME, CORRECTED_ENTRY, IMAGE_QUALITY, BIRTH_ADDRESS, ADOPTION

    };

    /**
     * @param births the bucket from which to import
     * @param filename containing the source records in digitising scotland format
     * @return the number of records read in
     * @throws IOException
     * @throws RecordFormatException
     * @throws BucketException
     */
    public static int importDigitisingScotlandBirths(IBucket<BirthFamilyGT> births, String filename, ArrayList<Long> oids) throws RecordFormatException, IOException, BucketException, IllegalKeyException {

        int count = 0;
        DataSet data = new DataSet(Paths.get(filename));
        for (List<String> record : data.getRecords()) {
            BirthFamilyGT b = importDigitisingScotlandBirth(data, record);
            try {
                births.makePersistent(b);
                oids.add(b.getId());
                count++;
            }
            catch (Exception e) {
                ErrorHandling.exceptionError(e, "Error making birth record persistent: " + b);
            }
        }

        return count;
    }

    /**
     * Fills in a OID record data from a file.
     */
    private static BirthFamilyGT importDigitisingScotlandBirth(DataSet data, List<String> record) throws IOException, RecordFormatException, IllegalKeyException {

        BirthFamilyGT birth = new BirthFamilyGT();

        addAvailableSingleFields(data, record, birth, RECORD_LABEL_MAP);
        addAvailableNormalisedFields(data, record, birth);
        addAvailableCompoundFields(data, record, birth);
        addUnavailableFields(birth, UNAVAILABLE_RECORD_LABELS);

        return birth;
    }

    private static void addAvailableCompoundFields(final DataSet data, final List<String> record, final BirthFamilyGT birth) {

        birth.put(BIRTH_ADDRESS, combineFields(data, record, "address 1", "address 2", "address 3"));
        birth.put(INFORMANT, combineFields(data, record, "forename of informant", "surname of informant"));
        // birth.put(PARENTS_PLACE_OF_MARRIAGE, combineFields(data,record, "place of parent's marriage 1", "place of parent's marriage 2"));\
        // TODO look at this and decide what to do - create a cannonical field?
        // place of parent's marriage 1 is mostly the town name with some Nas and ngs plus some random stuff - use this for now,
    }

    private static void addAvailableNormalisedFields(DataSet data, List<String> record, BirthFamilyGT birth) {

        birth.put(BIRTH_MONTH, normaliseDates.normaliseMonth(data.getValue(record, "month")));
    }
}
