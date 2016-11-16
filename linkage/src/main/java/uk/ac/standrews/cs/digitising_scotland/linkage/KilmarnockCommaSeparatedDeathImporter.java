package uk.ac.standrews.cs.digitising_scotland.linkage;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death.*;

/**
 * Utility classes for importing records in digitising scotland format
 * Created by al on 8/11/2016.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class KilmarnockCommaSeparatedDeathImporter {

    public static final String[][] RECORD_LABEL_MAP = {

        {ORIGINAL_ID, "ID"},
        {SURNAME, "surname of deceased"},
        {FORENAME, "forename(s) of deceased"},
        {OCCUPATION, "occupation"},
        {SEX, "sex"},
        {YEAR_OF_REGISTRATION, "year of reg"},
        {REGISTRATION_DISTRICT_NUMBER, "identifier"},
        {REGISTRATION_DISTRICT_SUFFIX, "register identifier"},
        {ENTRY, "entry no"},
        {MOTHERS_MAIDEN_SURNAME, "mother's maiden surname"},
        {DEATH_DAY, "day of reg"},
        {DEATH_MONTH, "month of reg"},
        {DEATH_YEAR, "year of reg"},
        {FATHERS_FORENAME, "father's forename"},
        {FATHERS_SURNAME, "father's surname"},
        {FATHERS_OCCUPATION, "father's occupation"},
        {MOTHERS_FORENAME, "mother's forename"},
        {DEATH_DAY, "day"},
        {DEATH_MONTH, "month"},
        {DEATH_YEAR, "year"},
        {AGE_AT_DEATH, "age at death"},
        {OCCUPATION, "occupation"},
        {MARITAL_STATUS, "marital status"},
        {SPOUSES_OCCUPATIONS, "spouse's occ"},
        {FATHER_DECEASED, "if father deceased"},
        {MOTHER_DECEASED, "if mother deceased"},
        {MOTHERS_SURNAME, "mother's maiden surname"},
        {MOTHERS_MAIDEN_SURNAME,"mothers_maiden_surname"}, // duplicated?
            {COD_A,"cause of death"}
    };


    /**
     * @param deaths        the bucket from which to import
     * @param filename      containing the source records in digitising scotland format
     * @return the number of records read in
     * @throws IOException
     * @throws RecordFormatException
     * @throws BucketException
     */
    public static int importDigitisingScotlandDeaths(IBucket<Death> deaths, String filename, ArrayList<Long> oids) throws RecordFormatException, IOException, BucketException, IllegalKeyException {

            int count = 0;
            DataSet data = new DataSet(Paths.get(filename));
            for (List<String> record : data.getRecords()) {
                Death d = importDigitisingScotlandDeath(data, record);
                try {
                    deaths.makePersistent(d);
                    oids.add(d.getId());
                    count++;
                } catch (Exception e) {
                    ErrorHandling.exceptionError(e, "Error making death record persistent: " + d);
                }
            }

            return count;
        }

    /**
     * Fills in a OID record data from a file.
     */
    private static Death importDigitisingScotlandDeath(DataSet data, List<String> record) throws IOException, RecordFormatException, IllegalKeyException {

        Death death = new Death();

        for (String[] field : RECORD_LABEL_MAP) {
            death.put(field[0], field[1]);
        }

        //  Concatenated fields

        death.put(SPOUSES_NAMES, data.getValue(record, "forename of spouse") + " " + data.getValue(record, "surname of spouse"));
        death.put(PLACE_OF_DEATH, data.getValue(record, "address 1") + "," + data.getValue(record, "address 2") + "," + data.getValue(record, "address 3"));

        // Unused fields - need to be empty for structure

        death.put(CHANGED_FORENAME,"");
        death.put(CHANGED_SURNAME,"");
        death.put(CHANGED_MOTHERS_MAIDEN_SURNAME,"");
        death.put(CORRECTED_ENTRY,"");
        death.put(IMAGE_QUALITY,"");
        death.put(CHANGED_DEATH_AGE,"");
        death.put(COD_B,"");
        death.put(COD_C,"");
        death.put(PLACE_OF_DEATH,"");
        death.put(DATE_OF_BIRTH,"");
        death.put(CERTIFYING_DOCTOR,"");
        
        return death;
    }
}