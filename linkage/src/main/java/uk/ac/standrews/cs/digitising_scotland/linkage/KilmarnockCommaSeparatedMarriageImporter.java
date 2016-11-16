package uk.ac.standrews.cs.digitising_scotland.linkage;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.digitising_scotland.util.*;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IReferenceType;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility classes for importing records in digitising scotland format
 * Created by al on 8/11/2016.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class KilmarnockCommaSeparatedMarriageImporter {

    /**
     * Imports a set of marriage records from file to a bucket.
     *
     * @param marriages the bucket into which the new records should be put
     * @param filename string path of file containing the source records in digitising scotland format
     * @param object_ids a list of object ids, to which the ids of the new records should be added
     *
     * @return the number of records read in
     * @throws IOException if the data cannot be read from the file
     */
    public static int importDigitisingScotlandMarriages(IBucket<Marriage> marriages, String filename, List<Long> object_ids) throws IOException {

        int count = 0;

        DataSet data = new DataSet(Paths.get(filename));

        for (List<String> record : data.getRecords()) {
            Marriage marriage = importDigitisingScotlandMarriage(data, record);
            try {
//                marriages.makePersistent(marriage);
//                object_ids.add(marriage.getId());
                count++;
            }
            catch (Exception e) {
                ErrorHandling.exceptionError(e, "Error making marriage record persistent: " + marriage);
            }
        }

        return count;
    }

    private static Marriage importDigitisingScotlandMarriage(DataSet data, List<String> record) {

        // Information that doesn't currently fit:

        // "place of marriage 1", "place of marriage 2", "place of marriage 3"
        // "groom's mother's occ"
        // "bride's mother's occ"
        // "groom's mother's other names"
        // "bride's mother's other name/s"


        Marriage marriage = new Marriage();

        marriage.put(Marriage.ORIGINAL_ID, data.getValue(record, "ID"));
        marriage.put(Marriage.YEAR_OF_REGISTRATION, data.getValue(record, "stryear"));
        marriage.put(Marriage.REGISTRATION_DISTRICT_NUMBER, data.getValue(record, "RD identifier"));
        marriage.put(Marriage.REGISTRATION_DISTRICT_SUFFIX, data.getValue(record, "register identifier"));
        marriage.put(Marriage.ENTRY, data.getValue(record, "entry number"));
        marriage.put(Marriage.DENOMINATION, data.getValue(record, "denomination"));

        marriage.put(Marriage.BRIDE_FORENAME, data.getValue(record, "forename of bride"));
        marriage.put(Marriage.BRIDE_SURNAME, data.getValue(record, "surname of bride"));

        marriage.put(Marriage.GROOM_FORENAME, data.getValue(record, "forename of groom"));
        marriage.put(Marriage.GROOM_SURNAME, data.getValue(record, "surname of groom"));

        marriage.put(Marriage.MARRIAGE_DAY, data.getValue(record, "day"));
        marriage.put(Marriage.MARRIAGE_MONTH, data.getValue(record, "month"));
        marriage.put(Marriage.MARRIAGE_YEAR, data.getValue(record, "year"));

        marriage.put(Marriage.BRIDE_AGE_OR_DATE_OF_BIRTH, data.getValue(record, "age of bride"));
        marriage.put(Marriage.GROOM_AGE_OR_DATE_OF_BIRTH, data.getValue(record, "age of groom"));

        marriage.put(Marriage.BRIDE_FATHERS_FORENAME, data.getValue(record, "bride's father's forename"));
        marriage.put(Marriage.BRIDE_FATHERS_SURNAME, data.getValue(record, "bride's father's surname"));
        marriage.put(Marriage.BRIDE_MOTHERS_FORENAME, data.getValue(record, "bride's mother's forename"));
        marriage.put(Marriage.BRIDE_MOTHERS_MAIDEN_SURNAME, data.getValue(record, "bride's mother's maiden surname"));

        marriage.put(Marriage.GROOM_FATHERS_FORENAME, data.getValue(record, "groom's father's forename"));
        marriage.put(Marriage.GROOM_FATHERS_SURNAME, data.getValue(record, "groom's father's surname"));
        marriage.put(Marriage.GROOM_MOTHERS_FORENAME, data.getValue(record, "groom's mother's forename"));
        marriage.put(Marriage.GROOM_MOTHERS_MAIDEN_SURNAME, data.getValue(record, "groom's mother's maiden surname"));

        marriage.put(Marriage.BRIDE_MARITAL_STATUS, data.getValue(record, "marital status of bride"));
        marriage.put(Marriage.BRIDE_ADDRESS, data.getValue(record, "address of bride 1") + " " + data.getValue(record, "address of bride 2") + " " + data.getValue(record, "address of bride 3"));
        marriage.put(Marriage.BRIDE_DID_NOT_SIGN, data.getValue(record, "did bride sign?"));
        marriage.put(Marriage.BRIDE_OCCUPATION, data.getValue(record, "occupation of bride"));
        marriage.put(Marriage.BRIDE_FATHER_OCCUPATION, data.getValue(record, "bride's father's occupation"));
        marriage.put(Marriage.BRIDE_FATHER_DECEASED, data.getValue(record, "if bride's father deceased"));
        marriage.put(Marriage.BRIDE_MOTHER_DECEASED, data.getValue(record, "if bride's mother deceased"));

        marriage.put(Marriage.GROOM_MARITAL_STATUS, data.getValue(record, "marital status of groom"));
        marriage.put(Marriage.GROOM_ADDRESS, data.getValue(record, "address of groom 1") + " " + data.getValue(record, "address of groom 2") + " " + data.getValue(record, "address of groom 3"));
        marriage.put(Marriage.GROOM_DID_NOT_SIGN, data.getValue(record, "did groom sign?"));
        marriage.put(Marriage.GROOM_OCCUPATION, data.getValue(record, "occupation of groom"));
        marriage.put(Marriage.GROOM_FATHERS_OCCUPATION, data.getValue(record, "groom's father's occupation"));
        marriage.put(Marriage.GROOM_FATHER_DECEASED, data.getValue(record, "if groom's father deceased"));
        marriage.put(Marriage.GROOM_MOTHER_DECEASED, data.getValue(record, "if groom's mother deceased"));

        System.out.println(marriage);

        return marriage;
    }

    public static void main(String[] args) throws RecordFormatException, BucketException, IOException {

        importDigitisingScotlandMarriages(null, "/Users/graham/Desktop/kilmarnock_linked/marriages.csv", null);
    }
}
