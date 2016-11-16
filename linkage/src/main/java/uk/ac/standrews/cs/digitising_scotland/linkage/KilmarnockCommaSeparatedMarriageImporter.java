package uk.ac.standrews.cs.digitising_scotland.linkage;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.digitising_scotland.util.*;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage.*;

/**
 * Utility classes for importing records in digitising scotland format
 * Created by al on 8/11/2016.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class KilmarnockCommaSeparatedMarriageImporter {

    public static final String[][] RECORD_LABEL_MAP = {

                    // Information that doesn't currently fit:

                    // "place of marriage 1", "place of marriage 2", "place of marriage 3"
                    // "groom's mother's occ"
                    // "bride's mother's occ"
                    // "groom's mother's other names"
                    // "bride's mother's other name/s"


                    {ORIGINAL_ID, "ID"},

                    {YEAR_OF_REGISTRATION, "stryear"},

                    {REGISTRATION_DISTRICT_NUMBER, "RD identifier"},

                    {REGISTRATION_DISTRICT_SUFFIX, "register identifier"},

                    {ENTRY, "entry number"},

                    {DENOMINATION, "denomination"},

                    // *********************************

                    {BRIDE_FORENAME, "forename of bride"}, {BRIDE_SURNAME, "surname of bride"},

                    // *********************************

                    {GROOM_FORENAME, "forename of groom"}, {GROOM_SURNAME, "surname of groom"},

                    // *********************************

                    {MARRIAGE_DAY, "day"}, {MARRIAGE_MONTH, "month"}, {MARRIAGE_YEAR, "year"},

                    // *********************************

                    {BRIDE_AGE_OR_DATE_OF_BIRTH, "age of bride"}, {GROOM_AGE_OR_DATE_OF_BIRTH, "age of groom"},

                    // *********************************

                    {BRIDE_FATHERS_FORENAME, "bride's father's forename"},

                    {BRIDE_FATHERS_SURNAME, "bride's father's surname"},

                    {BRIDE_MOTHERS_FORENAME, "bride's mother's forename"},

                    {BRIDE_MOTHERS_MAIDEN_SURNAME, "bride's mother's maiden surname"},

                    // *********************************

                    {GROOM_FATHERS_FORENAME, "groom's father's forename"},

                    {GROOM_FATHERS_SURNAME, "groom's father's surname"},

                    {GROOM_MOTHERS_FORENAME, "groom's mother's forename"},

                    {GROOM_MOTHERS_MAIDEN_SURNAME, "groom's mother's maiden surname"},

                    // *********************************

                    {BRIDE_MARITAL_STATUS, "marital status of bride"},

                    {BRIDE_DID_NOT_SIGN, "did bride sign?"},

                    {BRIDE_OCCUPATION, "occupation of bride"},

                    {BRIDE_FATHER_OCCUPATION, "bride's father's occupation"},

                    {BRIDE_FATHER_DECEASED, "if bride's father deceased"},

                    {BRIDE_MOTHER_DECEASED, "if bride's mother deceased"},

                    // *********************************

                    {GROOM_MARITAL_STATUS, "marital status of groom"},

                    {GROOM_DID_NOT_SIGN, "did groom sign?"},

                    {GROOM_OCCUPATION, "occupation of groom"},

                    {GROOM_FATHERS_OCCUPATION, "groom's father's occupation"},

                    {GROOM_FATHER_DECEASED, "if groom's father deceased"},

                    {GROOM_MOTHER_DECEASED, "if groom's mother deceased"},

    };

    /**
     * Imports a set of marriage records from file to a bucket.
     *
     * @param marriages the bucket into which the new records should be put
     * @param filename string path of file containing the source records in digitising scotland format
     * @param object_ids a list of object ids, to which the ids of the new records should be added
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

        Marriage marriage = new Marriage();

        for (String[] field : RECORD_LABEL_MAP) {
            marriage.put(field[0], data.getValue(record, field[1]));
        }

        marriage.put(BRIDE_ADDRESS, data.getValue(record, "address of bride 1") + " " + data.getValue(record, "address of bride 2") + " " + data.getValue(record, "address of bride 3"));
        marriage.put(GROOM_ADDRESS, data.getValue(record, "address of groom 1") + " " + data.getValue(record, "address of groom 2") + " " + data.getValue(record, "address of groom 3"));

        System.out.println(marriage);

        return marriage;
    }

    public static void main(String[] args) throws RecordFormatException, BucketException, IOException {

        importDigitisingScotlandMarriages(null, "/Digitising Scotland/KilmarnockBDM/marriages.csv", null);
    }
}
