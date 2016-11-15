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


/**
 * Utility classes for importing records in digitising scotland format
 * Created by al on 8/11/2016.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class KilmarnockCommaSeparatedBirthImporter {
    
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

        Birth b = new Birth();
        b.put(Birth.ORIGINAL_ID, data.getValue(record, "ID"));
        b.put(Birth.SURNAME, data.getValue(record, "child's surname"));
        b.put(Birth.FORENAME, data.getValue(record, "child's forname(s)"));
        b.put(Birth.SEX, data.getValue(record, "sex"));
        b.put(Birth.YEAR_OF_REGISTRATION, data.getValue(record, "year of reg"));
        b.put(Birth.REGISTRATION_DISTRICT_NUMBER, data.getValue(record, "rd identifier"));
        b.put(Birth.REGISTRATION_DISTRICT_SUFFIX, data.getValue(record, "register identifier"));
        b.put(Birth.ENTRY, data.getValue(record, "entry no"));
        b.put(Birth.MOTHERS_MAIDEN_SURNAME, data.getValue(record, "mother's maiden surname"));
        b.put(Birth.BIRTH_DAY, data.getValue(record, "day"));
        b.put(Birth.BIRTH_MONTH, data.getValue(record, "month"));
        b.put(Birth.BIRTH_YEAR, data.getValue(record, "year"));
        b.put(Birth.BIRTH_ADDRESS, data.getValue(record, "address 1") + data.getValue(record, "address 2") + data.getValue(record, "address 3"));
        b.put(Birth.FATHERS_FORENAME, data.getValue(record, "father's forename"));
        b.put(Birth.FATHERS_SURNAME, data.getValue(record, "father's surname"));
        b.put(Birth.FATHERS_OCCUPATION, data.getValue(record, "father's occupation"));
        b.put(Birth.MOTHERS_FORENAME, data.getValue(record, "mother's forename"));
        b.put(Birth.PARENTS_DAY_OF_MARRIAGE, data.getValue(record, "day of parents' marriage"));
        b.put(Birth.PARENTS_MONTH_OF_MARRIAGE, data.getValue(record, "month of parents' marriage"));
        b.put(Birth.PARENTS_YEAR_OF_MARRIAGE, data.getValue(record, "year of parents' marriage"));
        b.put(Birth.PARENTS_PLACE_OF_MARRIAGE, data.getValue(record, "place of parent's marriage 1") + data.getValue(record, "place of parent's marriage 2") );
        b.put(Birth.ILLEGITIMATE_INDICATOR, data.getValue(record, "illegitimate"));
        b.put(Birth.INFORMANT, data.getValue(record, "forename of informant") + data.getValue(record, "surname of informant"));
        b.put(Birth.INFORMANT_DID_NOT_SIGN, data.getValue(record, "did informant  sign?"));

        System.out.println( b );

        return b;
    }

    public static void main(String[] args) throws RecordFormatException, BucketException, IOException {
        importDigitisingScotlandBirths(null,"/Users/al/Desktop/Digi Scotland/Kilmarnock data/kilmarnock_csv/births.csv",null);
    }
}