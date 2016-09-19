package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.storr.impl.StoreReference;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.ILXP;
import uk.ac.standrews.cs.storr.types.LXP_REF;
import uk.ac.standrews.cs.storr.types.LXP_SCALAR;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

import static uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Role.role_played.*;
import static uk.ac.standrews.cs.storr.types.LXPBaseType.LONG;
import static uk.ac.standrews.cs.storr.types.LXPBaseType.STRING;

/**
 * Created by al on 03/10/2014.
 */
public class Role extends AbstractLXP {

    private static final String SEPARATOR = "-";

    public enum role_played { principal, father, mother, bride, groom, grooms_father, grooms_mother, brides_father, brides_mother }

    // Person labels

    @LXP_SCALAR(type = STRING)
    public static final String SURNAME = "surname";
    @LXP_SCALAR(type = STRING)
    public static final String FORENAME = "forename";
    @LXP_SCALAR(type = STRING)
    public static final String SEX = "sex";
    @LXP_REF(type = "lxp")
    public static final String ORIGINAL_RECORD = "original_record";
    @LXP_SCALAR(type = LONG)
    public static final String ORIGINAL_RECORD_TYPE = "original_record_type";
    @LXP_SCALAR(type = STRING)
    public static final String ROLE = "role";

    public Role() {
        super();
    }

    public Role(long persistent_object_id, JSONReader reader) throws PersistentObjectException, IllegalKeyException {

        super(persistent_object_id, reader);
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

    //*********************** Creator methods ***********************//


    public static Role createPersonFromOwnBirth(StoreReference<Birth> original_record_ref, long original_record_type ) throws StoreException, BucketException {

        Birth original_record = original_record_ref.getReferend();

        String surname = original_record.getString(Birth.SURNAME);
        String forename = original_record.getString(Birth.FORENAME);
        String sex = original_record.getString(Birth.SEX);
        role_played role = role_played.principal;

        return new Role( surname, forename, sex, role, original_record_ref, original_record_type );
    }

    public static Role createPersonFromOwnDeath(StoreReference<Death> original_record_ref, long original_record_type ) throws StoreException, BucketException {

        Death original_record = original_record_ref.getReferend();

        String surname = original_record.getString(Death.SURNAME);
        String forename = original_record.getString(Death.FORENAME);
        String sex = original_record.getString(Death.SEX);
        role_played role = role_played.principal;

        return new Role( surname, forename, sex, role, original_record_ref, original_record_type );
    }

    public static Role createFatherFromChildsBirth(StoreReference<Birth> original_record_ref, long original_record_type) throws StoreException, BucketException {

        ILXP BD_record = original_record_ref.getReferend();

        if (BD_record.getString(Birth.FATHERS_SURNAME).equals("")) {
            return null;
        }

        String surname = BD_record.getString(Birth.FATHERS_SURNAME);
        String forename = BD_record.getString(Birth.FATHERS_FORENAME);
        String sex = "M"; //  this is the father
        role_played role = father;

        return new Role(surname, forename, sex, role, original_record_ref, original_record_type );
    }

    public static Role createFatherFromChildsDeath(StoreReference<Death> original_record_ref, long original_record_type) throws StoreException, BucketException {

        ILXP BD_record = original_record_ref.getReferend();

        if (BD_record.getString(Birth.FATHERS_SURNAME).equals("")) {
            return null;
        }

        String surname = BD_record.getString(Death.FATHERS_SURNAME);
        String forename = BD_record.getString(Death.FATHERS_FORENAME);
        String sex = "M"; //  this is the father
        role_played role = father;

        return new Role(surname, forename, sex, role, original_record_ref, original_record_type );
    }

    public static Role createMotherFromChildsBirth(StoreReference<Birth>  original_record_ref, long original_record_type) throws StoreException, BucketException {

        ILXP BD_record = original_record_ref.getReferend();

        if (BD_record.getString(Birth.MOTHERS_SURNAME).equals("")) {
            return null;
        }

        String surname = BD_record.getString(Birth.MOTHERS_MAIDEN_SURNAME);
        String forename = BD_record.getString(Birth.MOTHERS_FORENAME);
        String sex = "F"; //  this is the mother
        role_played role = mother;

        return new Role(surname, forename, sex, role, original_record_ref, original_record_type );

    }

    public static Role createMotherFromChildsDeath(StoreReference<Death>  original_record_ref, long original_record_type) throws StoreException, BucketException {

        ILXP BD_record = original_record_ref.getReferend();

        if (BD_record.getString(Birth.MOTHERS_SURNAME).equals("")) {
            return null;
        }

        String surname = BD_record.getString(Death.MOTHERS_MAIDEN_SURNAME);
        String forename = BD_record.getString(Death.MOTHERS_FORENAME);
        String sex = "F"; //  this is the mother
        role_played role = mother;

        return new Role(surname, forename, sex, role, original_record_ref, original_record_type );

    }

    /**
     * Creates a Role record for the bride for a given marriage record
     *
     * @param marriage_record_ref a reference to a record from which to extract person information
     * @return a Person representing the bride
     */
    public static Role createBrideFromMarriageRecord(StoreReference<Marriage> marriage_record_ref, long original_record_type) throws StoreException, BucketException {

        Marriage marriage_record = marriage_record_ref.getReferend();

        String surname = marriage_record.getString(Marriage.BRIDE_SURNAME);
        String forename = marriage_record.getString(Marriage.BRIDE_FORENAME);
        String sex = "F";
        role_played role = bride;

        return new Role(surname, forename, sex, role, marriage_record_ref, original_record_type );
    }

    /**
     * Creates a Role record for the groom for a given marriage record
     *
     * @param marriage_record_ref a reference to a record from which to extract person information
     * @return the Person representing the groom
     */
    public static Role createGroomFromMarriageRecord(StoreReference<Marriage> marriage_record_ref, long original_record_type) throws StoreException, BucketException {

        Marriage marriage_record = marriage_record_ref.getReferend();

        String surname = marriage_record.getString(Marriage.GROOM_SURNAME);
        String forename = marriage_record.getString(Marriage.GROOM_FORENAME);
        String sex = "M";
        role_played role = groom;

        return new Role(surname, forename, sex, role, marriage_record_ref, original_record_type );

    }

    /**
     * Creates a Role record for the brides father for a given marriage record
     *
     * @param marriage_record_ref a reference to a record from which to extract person information
     * @return the Person representing the brides father
     */
    public static Role createBridesFatherFromMarriageRecord(StoreReference<Marriage> marriage_record_ref, long original_record_type) throws StoreException, BucketException {

        Marriage marriage_record = marriage_record_ref.getReferend();

        String surname = marriage_record.getString(Marriage.BRIDE_FATHERS_SURNAME);
        if (surname.equals("0")) {
            surname = marriage_record.getString(Marriage.BRIDE_SURNAME); // TODO factor out - not sure where to
        }
        String forename = marriage_record.getString(Marriage.BRIDE_FATHERS_FORENAME);
        String sex = "M";
        role_played role = role_played.brides_father;

        return new Role(surname, forename, sex, role, marriage_record_ref, original_record_type );

    }

    /**
     * Creates a Role record for the brides mother for a given marriage record
     *
     * @param marriage_record_ref a reference to a record from which to extract person information
     * @return the Person representing the brides mother
     */
    public static Role createBridesMotherFromMarriageRecord(StoreReference<Marriage> marriage_record_ref, long original_record_type) throws StoreException, BucketException {

        Marriage marriage_record = marriage_record_ref.getReferend();

        String surname = marriage_record.getString(Marriage.BRIDE_MOTHERS_MAIDEN_SURNAME);
        String forename = marriage_record.getString(Marriage.BRIDE_MOTHERS_FORENAME);
        String sex = "F";
        role_played role = role_played.brides_mother;

        return new Role(surname, forename, sex, role, marriage_record_ref, original_record_type );

    }

    /**
     * Creates a Role record for the grooms father for a given marriage record
     *
     * @param marriage_record_ref a reference to a record from which to extract person information
     * @return the Person representing the grooms father
     */
    public static Role createGroomsFatherFromMarriageRecord(StoreReference<Marriage> marriage_record_ref, long original_record_type) throws StoreException, BucketException {

        Marriage marriage_record = marriage_record_ref.getReferend();

        String surname = marriage_record.getString(Marriage.GROOM_FATHERS_SURNAME);
        if (surname.equals("0")) {
            surname = marriage_record.getString(Marriage.GROOM_SURNAME); // TODO factor out - not sure where to
        }
        String forename = marriage_record.getString(Marriage.GROOM_FATHERS_FORENAME);
        String sex = "M";
        role_played role = role_played.grooms_father;

        return new Role(surname, forename, sex, role, marriage_record_ref, original_record_type );
    }

    /**
     * Creates a Role record for the groom's mother for a given marriage record
     *
     * @param marriage_record_ref a reference to a record from which to extract person information
     * @return the Person representing the grooms mother father
     */
    public static Role createGroomsMotherFromMarriageRecord(StoreReference<Marriage> marriage_record_ref, long original_record_type) throws StoreException, BucketException {

        Marriage marriage_record = marriage_record_ref.getReferend();

        String surname = marriage_record.getString(Marriage.GROOM_MOTHERS_MAIDEN_SURNAME);
        String forename = marriage_record.getString(Marriage.GROOM_MOTHERS_FORENAME);
        String sex = "F";
        role_played role = role_played.grooms_mother;

        return new Role(surname, forename, sex, role, marriage_record_ref, original_record_type );
    }

    //*********************** Getter methods ***********************//

    // Basic selectors operate over data stored in this role

    public String get_surname() { return getString(SURNAME); }

    public String get_forename() { return getString(FORENAME); }

    public String get_sex() { return getString(SEX); }

    public role_played get_role() { return role_played.valueOf( getString(ROLE) ); }

    public ILXP get_original_record() {

        String serialised = getString(ORIGINAL_RECORD);
        StoreReference ref = new StoreReference( serialised );
        try {
            return ref.getReferend();
        } catch (BucketException e) {
            ErrorHandling.error( "Cannot deference: " + serialised );
            return null; // should not reach
        }
    }

    public long get_original_record_type() { return getLong(ORIGINAL_RECORD_TYPE); }

    // Complex selectors operate over data stored in original record

    public String get_fathers_forename() {
        switch ( get_role() ) {
            case principal: {
                return get_original_record().getString(Birth.FATHERS_FORENAME);
            }
            case bride: {
                return get_original_record().getString(Marriage.BRIDE_FATHERS_FORENAME);
            }
            case groom: {
                return get_original_record().getString(Marriage.GROOM_FATHERS_FORENAME);
            }
            case father:
            case mother:
            case grooms_father:
            case grooms_mother:
            case brides_father:
            case brides_mother:
            default:
                return ""; // rest return null - we don't know who they are
        }
    }

    public String get_fathers_surname() {
        switch ( get_role() ) {
            case principal: {
                return get_original_record().getString(Birth.FATHERS_SURNAME);
            }
            case bride: {
                return get_original_record().getString(Marriage.BRIDE_FATHERS_SURNAME);
            }
            case groom: {
                return get_original_record().getString(Marriage.GROOM_FATHERS_SURNAME);
            }
            case father:
            case mother:
            case grooms_father:
            case grooms_mother:
            case brides_father:
            case brides_mother:
            default:
                return ""; // rest return null - we don't know who they are
        }
    }

    public String get_fathers_occupation() {
        switch ( get_role() ) {
            case principal: {
                return get_original_record().getString(Birth.FATHERS_OCCUPATION);
            }
            case bride: {
                return get_original_record().getString(Marriage.BRIDE_FATHER_OCCUPATION);
            }
            case groom: {
                return get_original_record().getString(Marriage.BRIDE_FATHER_OCCUPATION);
            }
            case father:
            case mother:
            case grooms_father:
            case grooms_mother:
            case brides_father:
            case brides_mother:
            default:
                return ""; // rest return null - we don't know who they are
        }
    }

    public String get_mothers_forename() {
        switch ( get_role() ) {
            case principal: {
                return get_original_record().getString(Birth.MOTHERS_FORENAME);
            }
            case bride: {
                return get_original_record().getString(Marriage.BRIDE_MOTHERS_FORENAME);
            }
            case groom: {
                return get_original_record().getString(Marriage.GROOM_MOTHERS_FORENAME);
            }
            case father:
            case mother:
            case grooms_father:
            case grooms_mother:
            case brides_father:
            case brides_mother:
            default:
                return ""; // rest return null - we don't know who they are
        }
    }

    public String get_mothers_maiden_surname() {
        switch ( get_role() ) {
            case principal: {
                return get_original_record().getString(Birth.MOTHERS_MAIDEN_SURNAME);
            }
            case bride: {
                return get_original_record().getString(Marriage.BRIDE_MOTHERS_MAIDEN_SURNAME);
            }
            case groom: {
                return get_original_record().getString(Marriage.GROOM_MOTHERS_MAIDEN_SURNAME);
            }
            case father:
            case mother:
            case grooms_father:
            case grooms_mother:
            case brides_father:
            case brides_mother:
            default:
                return ""; // rest return null - we don't know who they are
        }
    }

    public String get_occupation() {
        switch ( get_role() ) {
            case principal: {
                return get_original_record().getString(Death.OCCUPATION); // ????
            }
            case groom:{
                return get_original_record().getString(Marriage.GROOM_OCCUPATION);
            }
            case grooms_father: {
                return get_original_record().getString(Marriage.GROOM_FATHERS_OCCUPATION);
            }

            case brides_father: {
                return get_original_record().getString(Marriage.BRIDE_OCCUPATION);
            }
            case father: {
                return get_original_record().getString(Birth.FATHERS_OCCUPATION);
            }
            case mother:
            case bride:
            case grooms_mother:
            case brides_mother:
            default:
                return "";
        }
    }

    public String get_POM() {
        switch ( get_role() ) {
            case principal: {
                return get_original_record().getString(Birth.PARENTS_PLACE_OF_MARRIAGE);
            }
            case groom:
            case bride: {
                return get_original_record().getString(Marriage.REGISTRATION_DISTRICT_NUMBER); // TODO IS THIS THE SAME AS PARENTS_PLACE_OF_MARRIAGE???
            }
            case brides_father:
            case father:
            case grooms_father:
            case mother:
            case grooms_mother:
            case brides_mother:
            default:
                return "";
        }
    }

    public String get_DOM() {
        switch ( get_role() ) {
            case principal: {
                ILXP rec = get_original_record();
                return rec.getString(Birth.PARENTS_DAY_OF_MARRIAGE) + SEPARATOR + rec.getString(Birth.PARENTS_MONTH_OF_MARRIAGE) + SEPARATOR + rec.getString(Birth.PARENTS_YEAR_OF_MARRIAGE);
            }
            case groom:
            case bride: {
                ILXP rec = get_original_record();
                return rec.getString( Marriage.MARRIAGE_DAY) + SEPARATOR + rec.getString( Marriage.MARRIAGE_MONTH) + SEPARATOR + rec.getString( Marriage.MARRIAGE_YEAR);
            }
            case brides_father:
            case father:
            case grooms_father:
            case mother:
            case grooms_mother:
            case brides_mother:
            default:
                return "";
        }

    }

    //*********************** utility methods ***********************//

    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("\tRole: " + this.get_role() + "\n");
        builder.append("\tSex: " + this.get_sex() + "\n");
        builder.append("\tfirstname: " + this.get_forename() + "\n");
        builder.append("\tsurname: " + this.get_surname() + "\n");
        builder.append("\tfather fn: " + this.get_fathers_forename() + "\n");
        builder.append("\tfather ln: " + this.get_fathers_surname() + "\n");
        builder.append("\tmother fn: " + this.get_mothers_forename() + "\n");
        builder.append("\tmother ln: " + this.get_mothers_maiden_surname() + "\n");
        return builder.toString();
    }


}
