package uk.ac.standrews.cs.digitising_scotland.linkage;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
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
public class KilmarnockCommaSeparatedBirthImporter {

    /**
     * @param deaths        the bucket from which to import
     * @param filename      containing the source records in digitising scotland format
     * @param referencetype the expected type of the records being imported
     * @return the number of records read in
     * @throws IOException
     * @throws RecordFormatException
     * @throws BucketException
     */
    public static int importDigitisingScotlandDeaths(IBucket<Death> deaths, String filename, IReferenceType referencetype) throws RecordFormatException, IOException, BucketException, IllegalKeyException {
        long counter = 0;
//        try (final BufferedReader reader = Files.newBufferedReader(Paths.get(filename), FileManipulation.FILE_CHARSET)) {
//
//            int count = 0;
//
//            try {
//                while (true) {
//                    Death d = new Death();
//                    importDigitisingScotlandRecord(d, reader, referencetype, DSFields.DEATH_FIELD_NAMES);
//                    correctDeathFields(d);
//                    try {
//                        deaths.makePersistent(d);
//                        count++;
//                    } catch (BucketException e) {
//                        ErrorHandling.exceptionError(e, "Error making death record persistent: " + d);
//                    }
//                }
//            } catch (IOException e) {
//                // expect this to be thrown when we getObjectById to the end.
//            }
//            return count;
//        }
        return 0;
    }

    /**
     * @param marriages     the bucket from which to import
     * @param filename      containing the source records in digitising scotland format
     * @param referencetype the expected type of the records being imported
     * @return the number of records read in
     * @throws IOException
     * @throws RecordFormatException
     * @throws BucketException
     */
    public static int importDigitisingScotlandMarriages(IBucket<Marriage> marriages, String filename, IReferenceType referencetype, ArrayList<Long> oids) {
        long counter = 0;
//        try {
//            try (final BufferedReader reader = Files.newBufferedReader(Paths.get(filename), FileManipulation.FILE_CHARSET)) {
//
//                int count = 0;
//
//                try {
//                    while (true) {
//                        Marriage m = new Marriage();
//                        try {
//                            importDigitisingScotlandRecord(m, reader, referencetype, DSFields.MARRIAGE_FIELD_NAMES);
//                            correctMarriageFields(m);
//
//                        } catch (RecordFormatException e) {
//                            ErrorHandling.exceptionError(e, "Record format error reading file: " + filename);
//                        }
//                        try {
//                            marriages.makePersistent(m);
//                            oids.add(m.getId());
//                            count++;
//                        } catch (Exception e) {
//                            ErrorHandling.exceptionError(e, "Error making marriage record persistent: " + m);
//                        }
//                    }
//                } catch (IOException e) {
//                    // expect this to be thrown when we getObjectById to the end.
//                }
//                return count;
//            }
//        } catch (IOException e) {
//            ErrorHandling.exceptionError(e, "Error opening buffered reader for file: " + filename);
//            return 0;
//        }
        return 0;
    }

    /**
     * @param births        the bucket from which to import
     * @param filename      containing the source records in digitising scotland format
     * @param referencetype the expected type of the records being imported
     * @return the number of records read in
     * @throws IOException
     * @throws RecordFormatException
     * @throws BucketException
     */
    public static int importDigitisingScotlandBirths(IBucket<Birth> births, String filename, IReferenceType referencetype) throws RecordFormatException, IOException, BucketException, IllegalKeyException {

        int count = 0;
        DataSet data = new DataSet( Paths.get(filename) );
        for( List<String> record : data.getRecords() ) {
            Birth b = importDigitisingScotlandBirth( data, record );
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

        // 	family	family beware	parents' marriage	date of birth	cs	cx	fs	fx	ms	mx	parmaryear	parmarplace
        // line number	rd identifier	register identifier	entry no
        // 	father's surname	father's occupation	mother's forename		mother's occupation
        // year of parents' marriage	place of parent's marriage 1	place of parent's marriage 2	forename of informant	surname of informant
        // relationship of informant to child	did informant  sign?	was informant present?	day of reg	month of reg	year of reg	illegitimate	notes	mother's previous married name
        // address of informant	address of informant 2	address of informant 3
        // declaration	fathers dom	fathers dom2	fathers dom3	mothers dom	mothers dom2	mothers dom3	edits	death	pid71	sch71

    }

    public static void main(String[] args) throws RecordFormatException, BucketException, IOException {
        importDigitisingScotlandBirths(null,"/Users/al/Desktop/Digi Scotland/Kilmarnock data/kilmarnock_csv/births.csv",null);
    }
}