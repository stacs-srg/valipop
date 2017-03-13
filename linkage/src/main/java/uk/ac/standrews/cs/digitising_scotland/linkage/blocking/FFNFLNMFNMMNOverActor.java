package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.digitising_scotland.util.*;
import uk.ac.standrews.cs.storr.impl.exceptions.*;
import uk.ac.standrews.cs.storr.interfaces.*;

import java.io.*;

/**
 * This class blocks on streams of Role records.
 * The categories of blocking are first name of parents over streams of Role records
 * This should form family groups
 * This pattern was suggested at Raasay Colloquium by Eilidh.
 * These are unique tags for all vital event records.
 * TODO add in Date of parents marriage
 * Created by al on 30/8/16
 */

public class FFNFLNMFNMMNOverActor extends AbstractBlocker<Role> {

    public FFNFLNMFNMMNOverActor(final IBucket<Role> roleBucket, final IRepository output_repo, ILXPFactory<Role> tFactory) throws BucketException, RepositoryException, IOException {

        super(roleBucket.getInputStream(), output_repo, tFactory);
    }

    /**
     * @param record - a Person record to be blocked
     * @return the blocking keys - one for baby and one for FATHER
     */
    public String[] determineBlockedBucketNamesForRecord(final Role record) throws NoSuitableBucketException {

        switch (record.getRole()) {
            case PRINCIPAL:
                return determineBlockedBucketNamesForPrincipal(record);
            case FATHER:
                return determineBlockedBucketNamesForFather(record);
            case MOTHER:
                return determineBlockedBucketNamesForMother(record);

            default:
                throw new NoSuitableBucketException("No match");
        }
    }

    /*
     * @param record - a Person record to be blocked who is in the role of PRINCIPAL
     * @return the blocking key based on FNLN of mother and FATHER
     */
    public String[] determineBlockedBucketNamesForPrincipal(final Role record) throws NoSuitableBucketException {

        // Note will concat null strings into key if any fields are null - working hypothesis - this doesn't matter.

        try {
            final String normalised_father_forename = normaliseName(record.getFathersForename());
            final String normalised_father_surname = normaliseName(record.getFathersSurname());

            final String normalised_mother_forename = normaliseName(record.getMothersForename());
            final String normalised_mother_maiden_surname = normaliseName(record.getMothersMaidenSurname());

            String bucket_name = concatenate(normalised_father_forename, normalised_father_surname, normalised_mother_forename, normalised_mother_maiden_surname);
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

    /*
     * @param record - a Person record to be blocked who is in the role of FATHER
     * @return the blocking key based on FNLN of mother and FATHER
     */
    private String[] determineBlockedBucketNamesForFather(Role record) throws NoSuitableBucketException {

        // Note will concat null strings into key if any fields are null - working hypothesis - this doesn't matter.

        try {
            final String normalised_father_forename = record.getForename();
            final String normalised_father_surname = record.getSurname();

            // Get the mother's names from the original record.
            ILXP primary = record.getOriginalRecord();

            final String normalised_mother_forename = normaliseName((String) primary.get(BirthFamilyGT.MOTHERS_FORENAME));
            final String normalised_mother_maiden_surname = normaliseName((String) primary.get(BirthFamilyGT.MOTHERS_MAIDEN_SURNAME));

            String bucket_name = concatenate(normalised_father_forename, normalised_father_surname, normalised_mother_forename, normalised_mother_maiden_surname);
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

    /*
     * @param record - a Person record to be blocked who is in the role of mother
     * @return the blocking key based on FNLN of mother and FATHER
     */
    private String[] determineBlockedBucketNamesForMother(Role record) throws NoSuitableBucketException {

        // Note will concat null strings into key if any fields are null - working hypothesis - this doesn't matter.

        try {
            // Get the FATHER's names from the original record.
            ILXP primary = record.getOriginalRecord();

            final String normalised_father_forename = normaliseName((String) primary.get(BirthFamilyGT.FATHERS_FORENAME));
            final String normalised_father_surname = normaliseName((String) primary.get(BirthFamilyGT.FATHERS_SURNAME));

            final String normalised_mother_forename = normaliseName(record.getForename());
            final String normalised_mother_surname = normaliseName(record.getSurname());

            String bucket_name = concatenate(normalised_father_forename, normalised_father_surname, normalised_mother_forename, normalised_mother_surname);
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

