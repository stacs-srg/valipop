package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.storr.impl.exceptions.*;
import uk.ac.standrews.cs.storr.interfaces.*;

import java.io.*;

/**
 * This class blocks on streams of Role records.
 * The categories of blocking are: firstname, lastname
 * <p/>
 *
 * Created by al on 17/08/16.
 */
public class FNLNOverActor extends AbstractBlocker<Role> {

    public FNLNOverActor(final IBucket<Role> roleBucket, final IRepository output_repo, ILXPFactory<Role> tFactory) throws BucketException, RepositoryException, IOException {

        super(roleBucket.getInputStream(), output_repo, tFactory);
    }

    /**
     * @param record - a Person record to be blocked
     * @return the blocking keys - one for baby and one for FATHER
     */
    public String[] determineBlockedBucketNamesForRecord(final Role record) {

        // Only operates over role records.

        final String normalised_forename = normaliseName(record.getForename());
        final String normalised_surname = normaliseName(record.getSurname());

        String bucket_name = concatenate(normalised_forename, normalised_surname);
        return new String[]{bucket_name};
    }
}

