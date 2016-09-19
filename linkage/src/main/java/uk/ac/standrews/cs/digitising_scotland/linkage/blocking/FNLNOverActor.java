package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;


import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Role;
import uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.sharder.Blocker;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.ILXPFactory;
import uk.ac.standrews.cs.storr.interfaces.IRepository;

import java.io.IOException;

/**
 * This class blocks on streams of Role records.
 * The categories of blocking are: firstname, lastname
 * <p/>

 * Created by al on 17/08/16.
 */

public class FNLNOverActor extends Blocker<Role> {

    public FNLNOverActor(final IBucket<Role> roleBucket, final IRepository output_repo, ILXPFactory<Role> tFactory) throws BucketException, RepositoryException, IOException {

        super(roleBucket.getInputStream(), output_repo, tFactory);
    }

    /**
     * @param record - a Person record to be blocked
     * @return the blocking keys - one for baby and one for father
     */
    public String[] determineBlockedBucketNamesForRecord(final Role record) {

        // Only operates over role records

        String FN = removeNasties(record.get_forename());
        String LN = removeNasties( record.get_surname() );

        return new String[]{ FN + LN };
    }


    /**
     * @param key - a String key to be made into an acceptable bucket name
     * @return the cleaned up String
     */
    private String removeNasties(final String key) {
        return key.replace("/", "").replace( "\"", "" );
    }

}

