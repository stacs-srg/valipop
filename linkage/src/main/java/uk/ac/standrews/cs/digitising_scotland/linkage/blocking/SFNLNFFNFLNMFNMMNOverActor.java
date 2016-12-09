package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.digitising_scotland.util.*;
import uk.ac.standrews.cs.storr.impl.exceptions.*;
import uk.ac.standrews.cs.storr.interfaces.*;

import java.io.*;

/**
 * This class blocks on streams of Role records.
 * The categories of blocking are sex, first name, last name and first name of parents over streams of Role records
 * This pattern was suggested at Raasay Colloquium by Eilidh.
 * These are unique tags for all vital event records.
 * TODO add in Date of parents marriage
 *
 * Created by al on 17/08/16.
 */
public class SFNLNFFNFLNMFNMMNOverActor extends AbstractBlocker<Role> {

    public SFNLNFFNFLNMFNMMNOverActor(final IBucket<Role> roleBucket, final IRepository output_repo, ILXPFactory<Role> tFactory) throws BucketException, RepositoryException, IOException {

        super(roleBucket.getInputStream(), output_repo, tFactory);
    }

    /**
     * @param record - a Person record to be blocked
     * @return the blocking keys - one for baby and one for FATHER
     */
    public String[] determineBlockedBucketNamesForRecord(final Role record) {

        // Note will concat nulls into key if any fields are null - working hypothesis - this doesn't matter.

        try {
            final String normalised_forename = normaliseName(record.getForename());
            final String normalised_surname = normaliseName(record.getSurname());
            final String normalised_father_forename = normaliseName(record.getFathersForename());
            final String normalised_father_surname = normaliseName(record.getFathersSurname());
            final String normalised_mother_forename = normaliseName(record.getMothersForename());
            final String normalised_mother_maiden_surname = normaliseName(record.getMothersMaidenSurname());

            String bucket_name = concatenate(record.getSex(), normalised_forename, normalised_surname, normalised_father_forename, normalised_father_surname, normalised_mother_forename, normalised_mother_maiden_surname);
            return new String[]{bucket_name};
        }
        catch (KeyNotFoundException e) {
            ErrorHandling.exceptionError(e, "Key not found");
            return new String[]{};
        }
        catch (TypeMismatchFoundException e) {
            ErrorHandling.exceptionError(e, "Type mismatch");
            return new String[]{};
        }
    }
}

