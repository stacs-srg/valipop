package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;


import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Role;
import uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.sharder.Blocker;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.storr.impl.exceptions.*;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.ILXP;
import uk.ac.standrews.cs.storr.interfaces.ILXPFactory;
import uk.ac.standrews.cs.storr.interfaces.IRepository;

import java.io.IOException;

/**
 * This class blocks on streams of Role records.
 * The categories of blocking are first name of parents over streams of Role records
 * This should form family groups
 * This pattern was suggested at Raasay Colloquium by Eilidh.
 * These are unique tags for all vital event records.
 * TODO add in Date of parents marriage
 * Created by al on 30/8/16
 */

public class FFNFLNMFNMMNOverActor extends Blocker<Role> {

    public FFNFLNMFNMMNOverActor(final IBucket<Role> roleBucket, final IRepository output_repo, ILXPFactory<Role> tFactory) throws BucketException, RepositoryException, IOException {

        super(roleBucket.getInputStream(), output_repo, tFactory);
    }

    /**
     * @param record - a Person record to be blocked
     * @return the blocking keys - one for baby and one for father
     */
    public String[] determineBlockedBucketNamesForRecord(final Role record) throws NoSuitableBucketException {
            switch ( record.get_role() ) {
                case principal:
                    return determineBlockedBucketNamesForPrincipal( record );
                case father:
                    return determineBlockedBucketNamesForFather( record );
                case mother:
                    return determineBlockedBucketNamesForMother( record );
                case bride:
                case groom:
                case grooms_father:
                case grooms_mother:
                case brides_father:
                case brides_mother:
                default:
                    throw new NoSuitableBucketException( "No match" );
            }
    }

    /*
     * @param record - a Person record to be blocked who is in the role of principal
     * @return the blocking key based on FNLN of mother and father
     */
    public String[] determineBlockedBucketNamesForPrincipal(final Role record) throws NoSuitableBucketException {

        // Note will concat null strings into key if any fields are null - working hypothesis - this doesn't matter.

        StringBuilder builder = new StringBuilder();

        try {
            builder.append(record.get_fathers_forename());
            builder.append(record.get_fathers_surname());
            builder.append(record.get_mothers_forename());
            builder.append(record.get_mothers_maiden_surname());
            return new String[]{removeNasties(builder.toString())};

        } catch (KeyNotFoundException e) {
            ErrorHandling.exceptionError(e, "Key not found");
            throw new NoSuitableBucketException( e );
        } catch (TypeMismatchFoundException e) {
            ErrorHandling.exceptionError(e, "Type mismatch");
            throw new NoSuitableBucketException( e );
        }
    }

    /*
     * @param record - a Person record to be blocked who is in the role of father
     * @return the blocking key based on FNLN of mother and father
     */
    private String[] determineBlockedBucketNamesForFather(Role record) throws NoSuitableBucketException {
        // Note will concat null strings into key if any fields are null - working hypothesis - this doesn't matter.

        StringBuilder builder = new StringBuilder();

        try {
            builder.append(record.get_forename());
            builder.append(record.get_surname());

            // now find the wife from the original record

            ILXP primary = record.get_original_record();

            builder.append( primary.get(Birth.MOTHERS_FORENAME) );
            builder.append( primary.get(Birth.MOTHERS_MAIDEN_SURNAME) );

            return new String[]{removeNasties(builder.toString())};

        } catch (KeyNotFoundException e) {
            ErrorHandling.exceptionError(e, "Key not found");
            throw new NoSuitableBucketException( e );
        } catch (TypeMismatchFoundException e) {
            ErrorHandling.exceptionError(e, "Type mismatch");
            throw new NoSuitableBucketException( e );
        }

    }

    /*
     * @param record - a Person record to be blocked who is in the role of mother
     * @return the blocking key based on FNLN of mother and father
     */
    private String[] determineBlockedBucketNamesForMother(Role record) throws NoSuitableBucketException {
        // Note will concat null strings into key if any fields are null - working hypothesis - this doesn't matter.

        StringBuilder builder = new StringBuilder();

        try {
            // first find the husband from the original record

            ILXP primary = record.get_original_record();

            builder.append( primary.get(Birth.FATHERS_FORENAME) );
            builder.append( primary.get(Birth.FATHERS_SURNAME) );

            // now the mothers names...

            builder.append(record.get_forename());
            builder.append(record.get_surname());

            return new String[]{removeNasties(builder.toString())};

        } catch (KeyNotFoundException e) {
            ErrorHandling.exceptionError(e, "Key not found");
            throw new NoSuitableBucketException( e );
        } catch (TypeMismatchFoundException e) {
            ErrorHandling.exceptionError(e, "Type mismatch");
            throw new NoSuitableBucketException( e );
        }
    }

    /**
     * @param key - a String key to be made into an acceptable bucket name
     * @return the cleaned up String
     */
    private String removeNasties(final String key) {
        return key.replace("/", "").replace( "\"", "" );
    }

}

