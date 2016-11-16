package uk.ac.standrews.cs.digitising_scotland.linkage;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth.*;

/**
 * Utility classes for importing records in digitising scotland format
 * Created by al on 8/11/2016.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class KilmarnockCommaSeparatedBirthImporter {

    public static final String[][] RECORD_LABEL_MAP = {

            {ORIGINAL_ID, "ID"},
            {FORENAME, "child's forname(s)"},
            {SURNAME, "child's surname"},
            {SEX, "sex"},
            {YEAR_OF_REGISTRATION, "year of reg"},
            {REGISTRATION_DISTRICT_NUMBER, "rd identifier"},
            {REGISTRATION_DISTRICT_SUFFIX, "register identifier"},
            {ENTRY, "entry no"},
            {BIRTH_DAY, "day"},
            {BIRTH_MONTH, "month"},
            {BIRTH_YEAR, "year"},
            {FATHERS_FORENAME, "father's forename"},
            {FATHERS_SURNAME, "father's surname"},
            {FATHERS_OCCUPATION, "father's occupation"},
            {MOTHERS_FORENAME, "mother's forename"},
            {MOTHERS_MAIDEN_SURNAME, "mother's maiden surname"},
            {PARENTS_DAY_OF_MARRIAGE, "day of parents' marriage"},
            {PARENTS_MONTH_OF_MARRIAGE, "month of parents' marriage"},
            {PARENTS_YEAR_OF_MARRIAGE, "year of parents' marriage"},
            {ILLEGITIMATE_INDICATOR, "illegitimate"},
            {INFORMANT_DID_NOT_SIGN, "did informant  sign?"},
    };
    

    /**
     * @param births        the bucket from which to import
     * @param filename      containing the source records in digitising scotland format
     * @return the number of records read in
     * @throws IOException
     * @throws RecordFormatException
     * @throws BucketException
     */
    public static int importDigitisingScotlandBirths(IBucket<Birth> births, String filename, ArrayList<Long> oids) throws RecordFormatException, IOException, BucketException, IllegalKeyException {

        int count = 0;
        DataSet data = new DataSet(Paths.get(filename));
        for (List<String> record : data.getRecords()) {
            Birth b = importDigitisingScotlandBirth(data, record);
            try {
                births.makePersistent(b);
                oids.add(b.getId());
                count++;
            } catch (Exception e) {
                ErrorHandling.exceptionError(e, "Error making birth record persistent: " + b);
            }
        }

        return count;
    }

    /**
     * Fills in a OID record data from a file.
     */
    private static Birth importDigitisingScotlandBirth(DataSet data, List<String> record) throws IOException, RecordFormatException, IllegalKeyException {

        Birth birth = new Birth();

        for (String[] field : RECORD_LABEL_MAP) {
            birth.put(field[0], field[1]);
        }

        //  Concatenated fields
        
        birth.put(BIRTH_ADDRESS,data.getValue(record, "address 1") + data.getValue(record, "address 2") + data.getValue(record, "address 3" ));
        birth.put(INFORMANT,data.getValue(record, "forename of informant") + data.getValue(record, "surname of informant"));
        birth.put(PARENTS_PLACE_OF_MARRIAGE,data.getValue(record, "place of parent's marriage 1") + data.getValue(record, "place of parent's marriage 2"));

        // Unused fields - need to be empty for structure

        birth.put(CHANGED_FORENAME,"");
        birth.put(CHANGED_SURNAME,"");
        birth.put(MOTHERS_SURNAME,"");
        birth.put(CHANGED_MOTHERS_MAIDEN_SURNAME,"");
        birth.put(CORRECTED_ENTRY,"");
        birth.put(IMAGE_QUALITY,"");
        birth.put(BIRTH_ADDRESS,"");
        birth.put(ADOPTION,"");

        return birth;
    }

}