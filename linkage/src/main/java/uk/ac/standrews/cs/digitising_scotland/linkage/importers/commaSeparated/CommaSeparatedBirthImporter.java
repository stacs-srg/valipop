package uk.ac.standrews.cs.digitising_scotland.linkage.importers.commaSeparated;

import uk.ac.standrews.cs.digitising_scotland.linkage.importers.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * Utility classes for importing records in digitising scotland format
 * Created by al on 8/11/2016.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public abstract class CommaSeparatedBirthImporter extends CommaSeparatedImporter {

    public abstract String[][] get_record_map();

    public abstract String[] get_unavailable_records();

    public abstract void addAvailableCompoundFields(final DataSet data, final List<String> record, final BirthFamilyGT birth);

    public abstract void addAvailableNormalisedFields(DataSet data, List<String> record, BirthFamilyGT birth);

    /**
     * @param births   the bucket from which to import
     * @param births_source_path containing the source records in digitising scotland format
     * @return the number of records read in
     * @throws IOException
     * @throws RecordFormatException
     * @throws BucketException
     */
    public  int importDigitisingScotlandBirths(IBucket<BirthFamilyGT> births, String births_source_path) throws IOException, RecordFormatException, BucketException {

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

        addAvailableSingleFields(data, record, birth, get_record_map());
        addAvailableNormalisedFields(data, record, birth);
        addAvailableCompoundFields(data, record, birth);
        addUnavailableFields(birth, get_unavailable_records());

        return birth;
    }
}
