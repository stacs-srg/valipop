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
public class FFNFLNMFNMMNPOMDOMOverBirth extends AbstractBlocker<KillieBirth> {

    public FFNFLNMFNMMNPOMDOMOverBirth(final IBucket<KillieBirth> birthsBucket, final IRepository output_repo, ILXPFactory<KillieBirth> tFactory) throws BucketException, RepositoryException, IOException {

        super(birthsBucket.getInputStream(), output_repo, tFactory);
    }

    /**
     * @param record - a Person record to be blocked
     * @return the blocking keys based on Father's first name, last name, Mother's first name, last name, place of marriage
     */
    public String[] determineBlockedBucketNamesForRecord(final KillieBirth record) throws NoSuitableBucketException {

        // Note will concat null strings into key if any fields are null - working hypothesis - this doesn't matter.

        try {
            final String normalised_fathers_forename = normaliseName(record.getFathersForename());
            final String normalised_fathers_surname = normaliseName(record.getFathersSurname());

            final String normalised_mothers_forename = normaliseName(record.getMothersForename());
            final String normalised_mothers_maiden_surname = normaliseName(record.getMothersMaidenSurname());

            final String normalised_place_of_marriage = normalisePlace(record.getPlaceOfMarriage());
            final String date_of_marriage = record.getDateOfMarriage();

            String bucket_name = concatenate(normalised_fathers_forename, normalised_fathers_surname, normalised_mothers_forename, normalised_mothers_maiden_surname, normalised_place_of_marriage, date_of_marriage);
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
