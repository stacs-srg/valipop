package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.digitising_scotland.util.*;
import uk.ac.standrews.cs.storr.impl.exceptions.*;
import uk.ac.standrews.cs.storr.interfaces.*;

import java.io.*;

/**
 * This class blocks on streams of Role records.
 * The categories of blocking are first name of parents along with place of marriage and date of marriage over streams of Role records
 * This should form family groups
 * This pattern was suggested at Raasay Colloquium by Eilidh.
 * These are unique tags for all vital event records.
 * Created by al on 30/8/16
 */
public class FFNFLNMFNMMNPOMDOMOverMarriage extends AbstractBlocker<Marriage> {

    public FFNFLNMFNMMNPOMDOMOverMarriage(final IBucket<Marriage> birthsBucket, final IRepository output_repo, ILXPFactory<Marriage> tFactory) throws BucketException, RepositoryException, IOException {

        super(birthsBucket.getInputStream(), output_repo, tFactory);
    }

    /**
     * @param record - a Person record to be blocked
     * @return the blocking keys based on Father's first name, last name, Mother's first name, last name, place of marriage
     */
    public String[] determineBlockedBucketNamesForRecord(final Marriage record) throws NoSuitableBucketException {

        // Note will concat null strings into key if any fields are null - working hypothesis - this doesn't matter.

        try {
            final String normalised_groom_forename = normaliseName(record.getGroomsForename());
            final String normalised_groom_surname = normaliseName(record.getGroomsSurname());
            final String normalised_bride_forename = normaliseName(record.getBridesForename());
            final String normalised_bride_surname = normaliseName(record.getBridesSurname());

            final String normalised_place_of_marriage = normalisePlace(record.getPlaceOfMarriage());
            final String date_of_marriage = record.getDateOfMarriage();

            String bucket_name = concatenate(normalised_groom_forename, normalised_groom_surname, normalised_bride_forename, normalised_bride_surname, normalised_place_of_marriage, date_of_marriage);
            return new String[]{bucket_name};
        }
        catch (KeyNotFoundException e) {
            ErrorHandling.exceptionError(e, "Key not found");
            throw new NoSuitableBucketException(e);
        }
        catch (TypeMismatchFoundException e) {
            ErrorHandling.exceptionError(e, "Type mismatch");
            throw new NoSuitableBucketException(e);
        }
    }
}
