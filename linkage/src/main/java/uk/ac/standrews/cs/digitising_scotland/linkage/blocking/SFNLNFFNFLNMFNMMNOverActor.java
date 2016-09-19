package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;


import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Role;
import uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.sharder.Blocker;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.KeyNotFoundException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.TypeMismatchFoundException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.ILXPFactory;
import uk.ac.standrews.cs.storr.interfaces.IRepository;

import java.io.IOException;

/**
 * This class blocks on streams of Role records.
 * The categories of blocking are sex, first name, last name and first name of parents over streams of Role records
 * This pattern was suggested at Raasay Colloquium by Eilidh.
 * These are unique tags for all vital event records.
 * TODO add in Date of parents marriage
 * Created by al on 30/8/16
 *
 * Created by al on 17/08/16.
 */

public class SFNLNFFNFLNMFNMMNOverActor extends Blocker<Role> {

    public SFNLNFFNFLNMFNMMNOverActor(final IBucket<Role> roleBucket, final IRepository output_repo, ILXPFactory<Role> tFactory) throws BucketException, RepositoryException, IOException {

        super(roleBucket.getInputStream(), output_repo, tFactory);
    }

    /**
     * @param record - a Person record to be blocked
     * @return the blocking keys - one for baby and one for father
     */
    public String[] determineBlockedBucketNamesForRecord(final Role record) {

        // Note will concat nulls into key if any fields are null - working hypothesis - this doesn't matter.

        StringBuilder builder = new StringBuilder();

        try {
            builder.append(record.get_sex());
            builder.append(record.get_forename());
            builder.append(record.get_surname());
            builder.append(record.get_fathers_forename());
            builder.append(record.get_fathers_surname());
            builder.append(record.get_mothers_forename());
            builder.append(record.get_mothers_maiden_surname());
//            builder.append(record.get_parents_date_of_marriage());
            return new String[]{removeNasties(builder.toString())};

        } catch (KeyNotFoundException e) {
            ErrorHandling.exceptionError(e, "Key not found");
            return new String[]{};
        } catch (TypeMismatchFoundException e) {
            ErrorHandling.exceptionError(e, "Type mismatch");
            return new String[]{};
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

