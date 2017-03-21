package uk.ac.standrews.cs.digitising_scotland.linkage.importers.commaSeparated;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.digitising_scotland.linkage.normalisation.DateNormalisation;
import uk.ac.standrews.cs.digitising_scotland.linkage.normalisation.PlaceNormalisation;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
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
public abstract class CommaSeparatedMarriageImporter extends CommaSeparatedImporter {

    public abstract String[][] get_record_map();

    public abstract String[] get_unavailable_records();

    public abstract void addAvailableCompoundFields(final DataSet data, final List<String> record, final Marriage marriage);

    public abstract void addAvailableNormalisedFields(DataSet data, List<String> record, Marriage marriage);

    /**
     * Imports a set of marriage records from file to a bucket.
     *
     * @param marriages the bucket into which the new records should be put
     * @param marriages_source_path string path of file containing the source records in digitising scotland format
     * @return the number of records read in
     * @throws IOException if the data cannot be read from the file
     */
    public  int importDigitisingScotlandMarriages(IBucket<Marriage> marriages, String marriages_source_path) throws IOException, BucketException {

        DataSet data = new DataSet(Paths.get(marriages_source_path));
        int count = 0;

        for (List<String> record : data.getRecords()) {

            Marriage marriage_record = importDigitisingScotlandMarriage(data, record);
            marriages.makePersistent(marriage_record);
            count++;
        }

        return count;
    }

    private Marriage importDigitisingScotlandMarriage(DataSet data, List<String> record) {

        Marriage marriage = new Marriage();

        addAvailableSingleFields(data, record, marriage, get_record_map());
        addAvailableNormalisedFields(data, record, marriage);
        addAvailableCompoundFields(data, record, marriage);
        addUnavailableFields(marriage, get_unavailable_records());

        return marriage;
    }


}
