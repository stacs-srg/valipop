package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.roles;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.AbstractLXP;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.jstore.impl.StoreFactory;
import uk.ac.standrews.cs.jstore.impl.TypeFactory;
import uk.ac.standrews.cs.jstore.impl.exceptions.BucketException;
import uk.ac.standrews.cs.jstore.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.jstore.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.jstore.impl.exceptions.StoreException;
import uk.ac.standrews.cs.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.jstore.interfaces.IRepository;
import uk.ac.standrews.cs.jstore.types.LXP_REF;
import uk.ac.standrews.cs.jstore.types.LXP_SCALAR;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

import static uk.ac.standrews.cs.jstore.types.LXPBaseType.LONG;
import static uk.ac.standrews.cs.jstore.types.LXPBaseType.STRING;

/**
 * Created by al on 03/10/2014.
 *
 * This class is deprecated - was part of a failed experiment.
 * Delete later
 *
 * Delete all files and this package.
 *
 */
public class RoleXX extends AbstractLXP {

    public enum role_played {principal, father, mother, bride, groom, grooms_father, grooms_mother, brides_father, brides_mother}

    // TODO Do we need to add SPOUSE in HERE??

    // Common labels

    @LXP_SCALAR(type = STRING)
    public static final String SURNAME = "surname";
    @LXP_SCALAR(type = STRING)
    public static final String FORENAME = "forename";
    @LXP_SCALAR(type = STRING)
    public static final String SEX = "sex";
    @LXP_REF(type = "LXP") // TODO check this
    public static final String ORIGINAL_RECORD = "original_record";
    @LXP_SCALAR(type = LONG)
    public static final String ORIGINAL_RECORD_TYPE = "original_record_type";
    @LXP_SCALAR(type = STRING)
    public static final String ROLE = "role";

    public RoleXX() {
        super();
    }

    public RoleXX(JSONReader reader) throws PersistentObjectException, IllegalKeyException {

        super(reader);
    }

    public RoleXX(String surname, String forename, String sex, role_played role, ILXP original_record, long original_record_type) throws StoreException {

        this();
        try {
            put(SURNAME, surname);
            put(FORENAME, forename);
            put(SEX, sex);
            put(ROLE, role.name());
            put(ORIGINAL_RECORD, original_record.getId());
            put(ORIGINAL_RECORD_TYPE, original_record_type);
        } catch (IllegalKeyException e) {
            ErrorHandling.error("Illegal key in LXP");
        }

    }

    // Getters

    private ILXP get_from_bucket_with_name(String bucketname, long oid) {
        try {
            IRepository repo = StoreFactory.getStore().getRepo("BDM_repo");
            IBucket bucket = repo.getBucket(bucketname);
            return bucket.getObjectById(oid);
        } catch (StoreException e) {
            ErrorHandling.exceptionError(e, "Store exception");
            return null;
        } catch (RepositoryException e) {
            ErrorHandling.exceptionError(e, "Repo exception");
            return null;
        } catch (BucketException e) {
            ErrorHandling.exceptionError(e, "Bucket exception");
            return null;
        }
    }

    // Basic selectors operate over data stored in this role

    public String get_surname() {
        return getString(SURNAME);
    }

    public String get_forename() {
        return getString(FORENAME);
    }

    public String get_sex() {
        return getString(SEX);
    }

    public role_played get_role() {
        return role_played.valueOf(getString(ROLE));
    }

    public ILXP get_original_record() {
        long oid = getLong(ORIGINAL_RECORD);
        long record_type = get_original_record_type();
        if (record_type == TypeFactory.getInstance().typeWithname("birth").getId()) {
            return get_from_bucket_with_name("births", oid);
        } else if (record_type == TypeFactory.getInstance().typeWithname("death").getId()) {
            return get_from_bucket_with_name("deaths", oid);
        } else if (record_type == TypeFactory.getInstance().typeWithname("marriage").getId()) {
            return get_from_bucket_with_name("marriages", oid);
        }

        return null; // should not reach
    }

    public long get_original_record_type() {
        return getLong(ORIGINAL_RECORD_TYPE);
    }

    public String get_fathers_forename() { return null; }

    public String get_fathers_surname() { return null; }

    public String get_fathers_occupation() { return null; }

    public String get_mothers_forename() { return null; }

    public String get_mothers_surname() { return null; }

    public String get_mothers_maiden_surname() { return null; }

    public String get_occupation() { return null; }
}
