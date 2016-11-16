package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;


import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.sharder.Blocker;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.storr.impl.exceptions.*;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.ILXPFactory;
import uk.ac.standrews.cs.storr.interfaces.IRepository;

import java.io.IOException;

/**
 * This class blocks on streams of Role records.
 * The categories of blocking are first name of parents along with place of marriage and date of marriage over streams of Role records
 * This should form family groups
 * This pattern was suggested at Raasay Colloquium by Eilidh.
 * These are unique tags for all vital event records.
 * Created by al on 30/8/16
 */

public class FFNFLNMFNMMNPOMDOMOverBirth extends Blocker<Birth> {

    public FFNFLNMFNMMNPOMDOMOverBirth(final IBucket<Birth> birthsBucket, final IRepository output_repo, ILXPFactory<Birth> tFactory) throws BucketException, RepositoryException, IOException {

        super(birthsBucket.getInputStream(), output_repo, tFactory);
    }

    /**
     * @param record - a Person record to be blocked
     * @return the blocking keys based on Father's first name, last name, Mother's first name, last name, place of marriage
     */
    public String[] determineBlockedBucketNamesForRecord(final Birth record) throws NoSuitableBucketException {

        // Note will concat null strings into key if any fields are null - working hypothesis - this doesn't matter.

        StringBuilder builder = new StringBuilder();

        try {
            builder.append(record.get_fathers_forename());
            builder.append(record.get_fathers_surname());
            builder.append(record.get_mothers_forename());
            builder.append(record.get_mothers_maiden_surname());
            builder.append(record.get_POM());
            builder.append(record.get_DOM());
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

