package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;


import uk.ac.standrews.cs.jstore.impl.exceptions.BucketException;
import uk.ac.standrews.cs.jstore.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.jstore.interfaces.ILXPFactory;
import uk.ac.standrews.cs.jstore.interfaces.IRepository;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Role;
import uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.sharder.Blocker;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class blocks on streams of Person records.
 * The categories of blocking are:
 * <p/>
 * 1.  FNLN     First name, Last name
 * 2.	FNLNMF   First name, Last name, Mothers First name
 * 3.	FNLNFF   First name, Last name, Fathers First name
 * 4.	FNLNMFFF First name, Last name, Mothers First name, Fathers First name
 * 5. 	FNMF     First name, Mothers First name
 * 6.  FNFF     First name, Fathers First name
 * 7.  FNFL     First name, Fathers Last name
 * 8.  MFMMFF   Mothers Fist name, Mothers Maiden name, Fathers First name  (not marriage)
 * <p/>
 * Created by al on 01/08/2014.
 */
public class MultipleBlockerOverRole extends Blocker<Role> {

    public MultipleBlockerOverRole(final IBucket<Role> roleBucket, final IRepository output_repo, ILXPFactory<Role> tFactory) throws BucketException, RepositoryException, IOException {

        super(roleBucket.getInputStream(), output_repo, tFactory);
    }

    /**
     * @param record - a Person record to be blocked
     * @return the blocking keys - one for baby and one for father
     */
    public String[] determineBlockedBucketNamesForRecord(final Role record) {

        // Only operates over role records

        String FN = record.get_forename();
        String LN = record.get_surname();
        String FF = record.get_fathers_forename();
        String FL = record.get_fathers_surname();
        FL = (FL == null || FL.equals("0")) ? LN : FL; // TODO fix these - fathers surname coded as "0" if same as baby
        String MF = record.get_mothers_forename();
        String MM = record.get_mothers_maiden_surname();

        String FNLN = removeNasties(FN + LN);
        String FNLNMF = removeNasties(FNLN + MF);
        String FNLNFF = removeNasties(FNLN + FF);
        String FNLNMFFF = removeNasties(FNLN + MF + FF);
        String FNMF = removeNasties(FN + MF);
        String FNFF = removeNasties(FN + FF);
        String FNFL = removeNasties(FN + FL);
        String MFMMFF = removeNasties(MF + MM + FF);

        String[] blocked_names = new String[]{FNLN, FNLNMF, FNLNFF, FNLNMFFF, FNMF, FNFF, FNFL, MFMMFF};
        return dedup(blocked_names);
    }

    private String[] dedup(String[] blocked_names) {

        ArrayList<String> deduped = new ArrayList<String>();
        for (String name : blocked_names) {
            if (!deduped.contains(name)) {
                deduped.add(name);
            }
        }

        return deduped.toArray(new String[0]);
    }

    /**
     * @param key - a String key to be made into an acceptable bucket name
     * @return the cleaned up String
     */
    private String removeNasties(final String key) {
        return key.replace("/", "");
    }

}

