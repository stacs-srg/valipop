package uk.ac.standrews.cs.digitising_scotland.linkage.importers.barSeparated;

import uk.ac.standrews.cs.digitising_scotland.linkage.importers.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.digitising_scotland.linkage.tools.DSFields;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IReferenceType;
import uk.ac.standrews.cs.storr.types.Types;
import uk.ac.standrews.cs.util.tools.FileManipulation;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * Utility classes for importing records in digitising scotland format - graham test
 * Created by al on 25/04/2014.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class BarSeparatedEventImporter {

    private static final String SEPARATOR = "\\|";

    /**
     * @param deaths the bucket from which to import
     * @param filename containing the source records in digitising scotland format
     * @param referencetype    the expected type of the records being imported
     * @return the number of records read in
     * @throws IOException
     * @throws RecordFormatException
     * @throws BucketException
     */
    public static int importDigitisingScotlandDeaths(IBucket<Death> deaths, String filename, IReferenceType referencetype) throws RecordFormatException, IOException, BucketException, IllegalKeyException {
        long counter = 0;
        try (final BufferedReader reader = Files.newBufferedReader(Paths.get(filename), FileManipulation.FILE_CHARSET)) {

            int count = 0;

            try {
                while (true) {
                    Death d = new Death();
                    importDigitisingScotlandRecord(d, reader, referencetype, DSFields.DEATH_FIELD_NAMES);
                    correctDeathFields(d);
                    try {
                        deaths.makePersistent(d);
                        count++;
                    } catch (BucketException e) {
                        ErrorHandling.exceptionError(e, "Error making death record persistent: " + d);
                    }
                }
            } catch (IOException e) {
                // expect this to be thrown when we getObjectById to the end.
            }
            return count;
        }
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
        try {
            try ( final BufferedReader reader = Files.newBufferedReader(Paths.get(filename), FileManipulation.FILE_CHARSET)) {

                int count = 0;

                try {
                    while (true) {
                        Marriage m = new Marriage();
                        try {
                            importDigitisingScotlandRecord(m, reader, referencetype, DSFields.MARRIAGE_FIELD_NAMES);
                            correctMarriageFields(m);

                        } catch (RecordFormatException e) {
                            ErrorHandling.exceptionError( e, "Record format error reading file: " + filename );
                        }
                        try {
                            marriages.makePersistent(m);
                            oids.add(m.getId());
                            count++;
                        } catch ( Exception e ) {
                            ErrorHandling.exceptionError( e, "Error making marriage record persistent: " + m );
                        }
                    }
                } catch (IOException e) {
                    // expect this to be thrown when we getObjectById to the end.
                }
                return count;
            }
        } catch (IOException e) {
            ErrorHandling.exceptionError( e, "Error opening buffered reader for file: " + filename );
            return 0;
        }
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
    public static int importDigitisingScotlandBirths(IBucket<BirthFamilyGT> births, String filename, IReferenceType referencetype) throws RecordFormatException, IOException, BucketException, IllegalKeyException {
        long counter = 0;
        try (final BufferedReader reader = Files.newBufferedReader(Paths.get(filename), FileManipulation.FILE_CHARSET)) {

            int count = 0;

            try {
                while (true) {
                    BirthFamilyGT b = new BirthFamilyGT();
                    importDigitisingScotlandRecord(b, reader, referencetype, DSFields.BIRTH_FIELD_NAMES );
                    correctBirthFields( b );
                    try {
                        births.makePersistent(b);
                        count++;
                    } catch ( BucketException e ) {
                        ErrorHandling.exceptionError( e, "Error making marriage record persistent: " + b );
                    }
                }
            } catch (IOException e) {
                // expect this to be thrown when we getObjectById to the end.
            }
            return count;
        }
    }

    /**
     * Corrects the 'optimised' fields in the Digitising Scotland encoded birth records
     * @param b the record to correct.
     */
    private static void correctBirthFields(BirthFamilyGT b) {
        if( b.get( BirthFamilyGT.MOTHERS_SURNAME ).equals("0" ) ) {
            b.put( BirthFamilyGT.MOTHERS_SURNAME, b.getString( BirthFamilyGT.SURNAME ) );
        }
        if( b.get( BirthFamilyGT.FATHERS_SURNAME ).equals("0" ) ) {
            b.put( BirthFamilyGT.FATHERS_SURNAME, b.getString( BirthFamilyGT.SURNAME ) );
        }
    }

    /**
     * Corrects the 'optimised' fields in the Digitising Scotland encoded death records
     * @param d the record to correct.
     */
    private static void correctDeathFields(Death d) {
        if( d.get( BirthFamilyGT.MOTHERS_SURNAME ).equals("0" ) ) {
            d.put( BirthFamilyGT.MOTHERS_SURNAME, d.getString( BirthFamilyGT.SURNAME ) );
        }
        if( d.get( BirthFamilyGT.FATHERS_SURNAME ).equals("0" ) ) {
            d.put( BirthFamilyGT.FATHERS_SURNAME, d.getString( BirthFamilyGT.SURNAME ) );
        }
    }

    /**
     * Corrects the 'optimised' fields in the Digitising Scotland encoded marriage records
     * @param m the record to correct.
     */
    private static void correctMarriageFields(Marriage m) {
        if( m.get( Marriage.GROOM_FATHERS_SURNAME ).equals("0" ) ) {
            m.put( Marriage.GROOM_FATHERS_SURNAME, m.getString( Marriage.GROOM_SURNAME ) );
        }
    }

    /**
     * Fills in a OID record data from a file.
     */
    private static void importDigitisingScotlandRecord(final LXP record, final BufferedReader reader, IReferenceType lxp_type, Iterable<String> field_names) throws IOException, RecordFormatException, IllegalKeyException {

        long record_type = lxp_type.getId();
        String line = reader.readLine();
        if (line == null) {
            throw new IOException("read in empty line"); // expected in the way this is called
        }

        try {

            Iterable<String> field_values = Arrays.asList(line.split(SEPARATOR, -1));
            addFields(field_names, field_values, record);

        } catch (NoSuchElementException e) {
            throw new RecordFormatException(e.getMessage());
        }
    }

    private static void addFields(final Iterable<String> field_names, final Iterable<String> field_values, final LXP record) throws IllegalKeyException {

        Iterator<String> value_iterator = field_values.iterator();
        for (String field_name : field_names) {
            addField(value_iterator.next(), field_name, record);
        }
    }

    private static void addField(final String field_value, final String field_name, final LXP record) throws IllegalKeyException {

        if (!Types.getTypeRep(record.getClass()).containsKey(field_name)) {
            throw new IllegalKeyException("Illegal key: " + field_name);
        }
        record.put(field_name, field_value);
    }

}
