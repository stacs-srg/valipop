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


/**
 * Utility classes for importing records in digitising scotland format
 * Created by al on 8/11/2016.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class KilmarnockCommaSeparatedDeathImporter {

    // AL IS DOING THIS ONE

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

        death.put(Death.ORIGINAL_ID, data.getValue(record, "ID"));
        death.put(Death.SURNAME, data.getValue(record, "surname of deceased"));
        death.put(Death.FORENAME, data.getValue(record, "forename(s) of deceased"));
        death.put(Death.OCCUPATION, data.getValue(record, "occupation"));
        death.put(Death.SEX, data.getValue(record, "sex"));
        death.put(Death.YEAR_OF_REGISTRATION, data.getValue(record, "year of reg"));
        death.put(Death.REGISTRATION_DISTRICT_NUMBER, data.getValue(record, "identifier"));
        death.put(Death.REGISTRATION_DISTRICT_SUFFIX, data.getValue(record, "register identifier"));
        death.put(Death.ENTRY, data.getValue(record, "entry no"));
        death.put(Death.MOTHERS_MAIDEN_SURNAME, data.getValue(record, "mother's maiden surname"));
        death.put(Death.DEATH_DAY, data.getValue(record, "day of reg"));
        death.put(Death.DEATH_MONTH, data.getValue(record, "month of reg"));
        death.put(Death.DEATH_YEAR, data.getValue(record, "year of reg"));
        death.put(Death.FATHERS_FORENAME, data.getValue(record, "father's forename"));
        death.put(Death.FATHERS_SURNAME, data.getValue(record, "father's surname"));
        death.put(Death.FATHERS_OCCUPATION, data.getValue(record, "father's occupation"));
        death.put(Death.MOTHERS_FORENAME, data.getValue(record, "mother's forename"));
        death.put(Death.DEATH_DAY, data.getValue(record, "day"));
        death.put(Death.DEATH_MONTH, data.getValue(record, "month"));
        death.put(Death.DEATH_YEAR, data.getValue(record, "year"));
        death.put(Death.AGE_AT_DEATH, data.getValue(record, "age at death"));
        death.put(Death.OCCUPATION, data.getValue(record, "occupation"));
        death.put(Death.MARITAL_STATUS, data.getValue(record, "marital status"));
        death.put(Death.SPOUSES_NAMES, data.getValue(record, "forename of spouse") + " " + data.getValue(record, "surname of spouse"));
        death.put(Death.SPOUSES_OCCUPATIONS, data.getValue(record, "spouse's occ"));
        death.put(Death.PLACE_OF_DEATH, data.getValue(record, "address 1") + "," + data.getValue(record, "address 2") + "," +data.getValue(record, "address 3"));
        death.put(Death.FATHER_DECEASED, data.getValue(record, "if father deceased"));
        death.put(Death.MOTHER_DECEASED, data.getValue(record, "if mother deceased"));
        death.put(Death.MOTHERS_SURNAME, data.getValue(record, "mother's maiden surname"));
        death.put(Death.COD_A, data.getValue(record, "cause of death"));

        System.out.println( death );

        return death;


    }

    public static void main(String[] args) throws RecordFormatException, BucketException, IOException {
        importDigitisingScotlandDeaths(null,"/Users/al/Desktop/Digi Scotland/Kilmarnock data/kilmarnock_csv/deaths.csv",null);
    }
}