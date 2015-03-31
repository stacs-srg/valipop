package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.jstore.impl.StoreFactory;
import uk.ac.standrews.cs.jstore.impl.StoreReference;
import uk.ac.standrews.cs.jstore.impl.TypeFactory;
import uk.ac.standrews.cs.jstore.impl.exceptions.*;
import uk.ac.standrews.cs.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.jstore.interfaces.IRepository;
import uk.ac.standrews.cs.jstore.types.LXP_REF;
import uk.ac.standrews.cs.jstore.types.LXP_SCALAR;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

import static uk.ac.standrews.cs.jstore.types.LXPBaseType.LONG;
import static uk.ac.standrews.cs.jstore.types.LXPBaseType.STRING;

/**
 * Created by al on 03/10/2014.
 */
public class Role extends AbstractLXP {

    public enum role_played { principal, father, mother, bride, groom, grooms_father, grooms_mother, brides_father, brides_mother }

    // TODO need to add SPOUSE in HERE


    // Person labels

    @LXP_SCALAR(type = STRING)
    public static final String SURNAME = "surname";
    @LXP_SCALAR(type = STRING)
    public static final String FORENAME = "forename";
    @LXP_SCALAR(type = STRING)
    public static final String SEX = "sex";
    @LXP_REF(type = "OID")
    public static final String ORIGINAL_RECORD = "original_record";
    @LXP_SCALAR(type = LONG)
    public static final String ORIGINAL_RECORD_TYPE = "original_record_type";
    @LXP_SCALAR(type = STRING)
    public static final String ROLE = "role";

    public Role() {
        super();
    }

    public Role(JSONReader reader) throws PersistentObjectException, IllegalKeyException {

        super(reader);
    }

    public Role( String surname, String forename, String sex, role_played role, StoreReference original_record_ref, long original_record_type ) throws StoreException {

        this();
        try {
            put(SURNAME, surname);
            put(FORENAME, forename);
            put(SEX, sex);
            put(ROLE, role.name() );
            put(ORIGINAL_RECORD, original_record_ref.toString() );
            put(ORIGINAL_RECORD_TYPE, original_record_type );
        } catch (IllegalKeyException e) {
            ErrorHandling.error("Illegal key in OID");
        }

    }

    public static Role createPersonFromOwnBirthDeath(StoreReference<ILXP> original_record_ref, long original_record_type ) throws StoreException, BucketException {

        ILXP original_record = original_record_ref.getReferend();

        String surname = original_record.getString(Birth.SURNAME);
        String forename = original_record.getString(Birth.FORENAME);
        String sex = original_record.getString(Birth.SEX);
        role_played role = role_played.principal;

        return new Role( surname, forename, sex, role, original_record_ref, original_record_type );
    }

    public static Role createFatherFromChildsBirthDeath(StoreReference<ILXP> original_record_ref, long original_record_type) throws StoreException, BucketException {

        ILXP BD_record = original_record_ref.getReferend();

        if (BD_record.getString(Birth.FATHERS_SURNAME).equals("")) {
            return null;
        }

        String surname = BD_record.getString(Birth.FATHERS_SURNAME);
        String forename = BD_record.getString(Birth.FATHERS_FORENAME);
        String sex = "M"; //  this is the father
        role_played role = role_played.father;

        return new Role(surname, forename, sex, role, original_record_ref, original_record_type );
    }

    public static Role createMotherFromChildsBirthDeath(StoreReference<ILXP>  original_record_ref, long original_record_type) throws StoreException, BucketException {

        ILXP BD_record = original_record_ref.getReferend();

        if (BD_record.getString(Birth.MOTHERS_SURNAME).equals("")) {
            return null;
        }

        String surname = BD_record.getString(Birth.MOTHERS_SURNAME);
        String forename = BD_record.getString(Birth.MOTHERS_FORENAME);
        String sex = "F"; //  this is the mother
        role_played role = role_played.mother;

        return new Role(surname, forename, sex, role, original_record_ref, original_record_type );

    }

    /**
     * Creates a Role record for the bride for a given marriage record
     *
     * @param marriage_record_ref a reference to a record from which to extract person information
     * @return a Person representing the bride
     */
    public static Role createBrideFromMarriageRecord(StoreReference<Marriage> marriage_record_ref) throws StoreException, BucketException {

        Marriage marriage_record = marriage_record_ref.getReferend();

        String surname = marriage_record.getString(Marriage.BRIDE_SURNAME);
        String forename = marriage_record.getString(Marriage.BRIDE_FORENAME);
        String sex = "F";
        role_played role = role_played.bride;

        return new Role(surname, forename, sex, role, marriage_record_ref, marriage_record.required_type_labelID );
    }

    /**
     * Creates a Role record for the groom for a given marriage record
     *
     * @param marriage_record_ref a reference to a record from which to extract person information
     * @return the Person representing the groom
     */
    public static Role createGroomFromMarriageRecord(StoreReference<Marriage> marriage_record_ref) throws StoreException, BucketException {

        Marriage marriage_record = marriage_record_ref.getReferend();

        String surname = marriage_record.getString(Marriage.GROOM_SURNAME);
        String forename = marriage_record.getString(Marriage.GROOM_FORENAME);
        String sex = "M";
        role_played role = role_played.groom;

        return new Role(surname, forename, sex, role, marriage_record_ref, marriage_record.required_type_labelID );

    }

    /**
     * Creates a Role record for the brides father for a given marriage record
     *
     * @param marriage_record_ref a reference to a record from which to extract person information
     * @return the Person representing the brides father
     */
    public static Role createBridesFatherFromMarriageRecord(StoreReference<Marriage> marriage_record_ref) throws StoreException, BucketException {

        Marriage marriage_record = marriage_record_ref.getReferend();

        String surname = marriage_record.getString(Marriage.BRIDE_FATHERS_SURNAME);
        if (surname.equals("0")) {
            surname = marriage_record.getString(Marriage.BRIDE_SURNAME); // TODO factor out - not sure where to
        }
        String forename = marriage_record.getString(Marriage.BRIDE_FATHERS_FORENAME);
        String sex = "M";
        role_played role = role_played.brides_father;

        return new Role(surname, forename, sex, role, marriage_record_ref, marriage_record.required_type_labelID );

    }

    /**
     * Creates a Role record for the brides mother for a given marriage record
     *
     * @param marriage_record_ref a reference to a record from which to extract person information
     * @return the Person representing the brides mother
     */
    public static Role createBridesMotherFromMarriageRecord(StoreReference<Marriage> marriage_record_ref) throws StoreException, BucketException {

        Marriage marriage_record = marriage_record_ref.getReferend();

        String surname = marriage_record.getString(Marriage.BRIDE_MOTHERS_MAIDEN_SURNAME);
        String forename = marriage_record.getString(Marriage.BRIDE_MOTHERS_FORENAME);
        String sex = "F";
        role_played role = role_played.brides_mother;

        return new Role(surname, forename, sex, role, marriage_record_ref, marriage_record.required_type_labelID );

    }

    /**
     * Creates a Role record for the grooms father for a given marriage record
     *
     * @param marriage_record_ref a reference to a record from which to extract person information
     * @return the Person representing the grooms father
     */
    public static Role createGroomsFatherFromMarriageRecord(StoreReference<Marriage> marriage_record_ref) throws StoreException, BucketException {

        Marriage marriage_record = marriage_record_ref.getReferend();

        String surname = marriage_record.getString(Marriage.GROOM_FATHERS_SURNAME);
        if (surname.equals("0")) {
            surname = marriage_record.getString(Marriage.GROOM_SURNAME); // TODO factor out - not sure where to
        }
        String forename = marriage_record.getString(Marriage.GROOM_FATHERS_FORENAME);
        String sex = "M";
        role_played role = role_played.grooms_father;

        return new Role(surname, forename, sex, role, marriage_record_ref, marriage_record.required_type_labelID );
    }

    /**
     * Creates a Role record for the groom's mother for a given marriage record
     *
     * @param marriage_record_ref a reference to a record from which to extract person information
     * @return the Person representing the grooms mother father
     */
    public static Role createGroomsMotherFromMarriageRecord(StoreReference<Marriage> marriage_record_ref) throws StoreException, BucketException {

        Marriage marriage_record = marriage_record_ref.getReferend();

        String surname = marriage_record.getString(Marriage.GROOM_MOTHERS_MAIDEN_SURNAME);
        String forename = marriage_record.getString(Marriage.GROOM_MOTHERS_FORENAME);
        String sex = "F";
        role_played role = role_played.grooms_mother;

        return new Role(surname, forename, sex, role, marriage_record_ref, marriage_record.required_type_labelID );
    }

    // Getters

    private ILXP get_from_bucket_with_name(String bucketname, long oid) {
        try {
            IRepository repo = StoreFactory.getStore().getRepo("BDM_repo");
            IBucket bucket = repo.getBucket( bucketname );
            return bucket.getObjectById( oid );
        } catch (StoreException e) {
            ErrorHandling.exceptionError(e, "Store exception");
            return null;
        } catch (RepositoryException e) {
            ErrorHandling.exceptionError( e,"Repo exception" );
            return null;
        } catch (BucketException e) {
            ErrorHandling.exceptionError(e, "Bucket exception");
            return null;
        }
    }

    // Basic selectors operate over data stored in this role

    public String get_surname() { return getString(SURNAME); }

    public String get_forename() { return getString(FORENAME); }

    public String get_sex() { return getString(SEX); }

    public role_played get_role() { return role_played.valueOf( getString(ROLE) ); }

    public ILXP get_original_record() {
        long oid = getLong(ORIGINAL_RECORD);
        long record_type = get_original_record_type();
        if( record_type == TypeFactory.getInstance().typeWithname( "birth" ).getId() ) {
            return get_from_bucket_with_name( "births", oid );
        } else if( record_type == TypeFactory.getInstance().typeWithname( "death" ).getId() ) {
            return get_from_bucket_with_name( "deaths", oid );
        } else if( record_type == TypeFactory.getInstance().typeWithname( "marriage" ).getId() ) {
            return get_from_bucket_with_name( "marriages", oid );
        }

        return null; // should not reach
    }

    public long get_original_record_type() { return getLong(ORIGINAL_RECORD_TYPE); }

    // Complex selectors operate over data stored in original record

    public String get_fathers_forename() {
        switch ( get_role() ) {
            case principal: {
                return get_original_record().getString("fathers_forename");
            }
            case bride: {
                return get_original_record().getString("bride_fathers_forename");
            }
            case groom: {
                return get_original_record().getString("groom_fathers_forename");
            }
            case father:
            case mother:
            case grooms_father:
            case grooms_mother:
            case brides_father:
            case brides_mother:
            default:
                return null; // rest return null - we don't know who they are
        }
    }

    public String get_fathers_surname() {
        switch ( get_role() ) {
            case principal: {
                return get_original_record().getString("fathers_surname");
            }
            case bride: {
                return get_original_record().getString("bride_fathers_surname");
            }
            case groom: {
                return get_original_record().getString("groom_fathers_surname");
            }
            case father:
            case mother:
            case grooms_father:
            case grooms_mother:
            case brides_father:
            case brides_mother:
            default:
                return null; // rest return null - we don't know who they are
        }
    }

    public String get_fathers_occupation() {
        switch ( get_role() ) {
            case principal: {
                return get_original_record().getString("fathers_occupation");
            }
            case bride: {
                return get_original_record().getString("bride_fathers_occupation");
            }
            case groom: {
                return get_original_record().getString("groom_fathers_occupation");
            }
            case father:
            case mother:
            case grooms_father:
            case grooms_mother:
            case brides_father:
            case brides_mother:
            default:
                return null; // rest return null - we don't know who they are
        }
    }

    public String get_mothers_forename() {
        switch ( get_role() ) {
            case principal: {
                return get_original_record().getString("mothers_forename");
            }
            case bride: {
                return get_original_record().getString("bride_mothers_forename");
            }
            case groom: {
                return get_original_record().getString("groom_mothers_forename");
            }
            case father:
            case mother:
            case grooms_father:
            case grooms_mother:
            case brides_father:
            case brides_mother:
            default:
                return null; // rest return null - we don't know who they are
        }
    }

    public String get_mothers_surname() {
        switch ( get_role() ) {
            case principal: {
                return get_original_record().getString("mothers_surname");
            }
            case bride: {
                return get_original_record().getString("bride_mothers_surname");
            }
            case groom: {
                return get_original_record().getString("groom_mothers_surname");
            }
            case father:
            case mother:
            case grooms_father:
            case grooms_mother:
            case brides_father:
            case brides_mother:
            default:
                return null; // rest return null - we don't know who they are
        }
    }

    public String get_mothers_maiden_surname() {
        switch ( get_role() ) {
            case principal: {
                return get_original_record().getString("mothers_maiden_surname");
            }
            case bride: {
                return get_original_record().getString("bride_mothers_maiden_surname");
            }
            case groom: {
                return get_original_record().getString("groom_mothers_maiden_surname");
            }
            case father:
            case mother:
            case grooms_father:
            case grooms_mother:
            case brides_father:
            case brides_mother:
            default:
                return null; // rest return null - we don't know who they are
        }
    }

    public String get_occupation() {
        switch ( get_role() ) {
            case principal: {
                return get_original_record().getString("occupation");
            }
            case groom:{
                return get_original_record().getString("groom_occupation");
            }
            case grooms_father: {
                return get_original_record().getString("groom_fathers_occupation");
            }

            case brides_father: {
                return get_original_record().getString("bride_fathers_occupation");
            }
            case father: {
                return get_original_record().getString("fathers_occupation");
            }
            case mother:
            case bride:
            case grooms_mother:
            case brides_mother:
            default:
                return null;
        }
    }
}
