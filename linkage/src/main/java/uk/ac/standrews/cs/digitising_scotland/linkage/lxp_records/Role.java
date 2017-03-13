package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;
import uk.ac.standrews.cs.storr.impl.StoreReference;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.ILXP;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.types.LXP_REF;
import uk.ac.standrews.cs.storr.types.LXP_SCALAR;

import static uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Role.RolePlayed.*;
import static uk.ac.standrews.cs.storr.types.LXPBaseType.LONG;
import static uk.ac.standrews.cs.storr.types.LXPBaseType.STRING;

/**
 * Created by al on 03/10/2014.
 */
public class Role extends AbstractLXP {

    private static final String DATE_SEPARATOR = "-";
    private static final String MALE = "M";
    private static final String FEMALE = "F";

    public enum RolePlayed {PRINCIPAL, FATHER, MOTHER, BRIDE, GROOM, GROOMS_FATHER, GROOMS_MOTHER, BRIDES_FATHER, BRIDES_MOTHER}

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

    public Role(long persistent_object_id, JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException, IllegalKeyException {

        super(persistent_object_id, reader, repository, bucket);
    }

    public Role(String surname, String forename, String sex, RolePlayed role, StoreReference original_record_ref, long original_record_type) throws StoreException {

        this();

        try {
            put(SURNAME, surname);
            put(FORENAME, forename);
            put(SEX, sex);
            put(ROLE, role.name());
            put(ORIGINAL_RECORD, original_record_ref.toString());
            put(ORIGINAL_RECORD_TYPE, original_record_type);
        }
        catch (IllegalKeyException e) {
            ErrorHandling.error("Illegal key in OID");
        }
    }

    //*********************** Creator methods ***********************//

    public static Role createPersonFromOwnBirth(StoreReference<BirthFamilyGT> original_record_ref, long original_record_type) throws StoreException, BucketException {

        BirthFamilyGT original_record = original_record_ref.getReferend();

        String surname = original_record.getString(BirthFamilyGT.SURNAME);
        String forename = original_record.getString(BirthFamilyGT.FORENAME);
        String sex = original_record.getString(BirthFamilyGT.SEX);

        return new Role(surname, forename, sex, PRINCIPAL, original_record_ref, original_record_type);
    }

    public static Role createPersonFromOwnDeath(StoreReference<Death> original_record_ref, long original_record_type) throws StoreException, BucketException {

        Death original_record = original_record_ref.getReferend();

        String surname = original_record.getString(Death.SURNAME);
        String forename = original_record.getString(Death.FORENAME);
        String sex = original_record.getString(Death.SEX);

        return new Role(surname, forename, sex, PRINCIPAL, original_record_ref, original_record_type);
    }

    public static Role createFatherFromChildsBirth(StoreReference<BirthFamilyGT> original_record_ref, long original_record_type) throws StoreException, BucketException {

        ILXP BD_record = original_record_ref.getReferend();

        if (BD_record.getString(BirthFamilyGT.FATHERS_SURNAME).equals("")) {
            return null;
        }

        String surname = BD_record.getString(BirthFamilyGT.FATHERS_SURNAME);
        String forename = BD_record.getString(BirthFamilyGT.FATHERS_FORENAME);

        return new Role(surname, forename, MALE, FATHER, original_record_ref, original_record_type);
    }

    public static Role createFatherFromChildsDeath(StoreReference<Death> original_record_ref, long original_record_type) throws StoreException, BucketException {

        ILXP BD_record = original_record_ref.getReferend();

        if (BD_record.getString(BirthFamilyGT.FATHERS_SURNAME).equals("")) {
            return null;
        }

        String surname = BD_record.getString(Death.FATHERS_SURNAME);
        String forename = BD_record.getString(Death.FATHERS_FORENAME);

        return new Role(surname, forename, MALE, FATHER, original_record_ref, original_record_type);
    }

    public static Role createMotherFromChildsBirth(StoreReference<BirthFamilyGT> original_record_ref, long original_record_type) throws StoreException, BucketException {

        ILXP BD_record = original_record_ref.getReferend();

        if (BD_record.getString(BirthFamilyGT.MOTHERS_SURNAME).equals("")) {
            return null;
        }

        String surname = BD_record.getString(BirthFamilyGT.MOTHERS_MAIDEN_SURNAME);
        String forename = BD_record.getString(BirthFamilyGT.MOTHERS_FORENAME);

        return new Role(surname, forename, FEMALE, MOTHER, original_record_ref, original_record_type);
    }

    public static Role createMotherFromChildsDeath(StoreReference<Death> original_record_ref, long original_record_type) throws StoreException, BucketException {

        ILXP BD_record = original_record_ref.getReferend();

        if (BD_record.getString(BirthFamilyGT.MOTHERS_SURNAME).equals("")) {
            return null;
        }

        String surname = BD_record.getString(Death.MOTHERS_MAIDEN_SURNAME);
        String forename = BD_record.getString(Death.MOTHERS_FORENAME);

        return new Role(surname, forename, FEMALE, MOTHER, original_record_ref, original_record_type);
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

        return new Role(surname, forename, FEMALE, BRIDE, marriage_record_ref, original_record_type);
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

        return new Role(surname, forename, MALE, GROOM, marriage_record_ref, original_record_type);
    }

    /**
     * Creates a Role record for the brides FATHER for a given marriage record
     *
     * @param marriage_record_ref a reference to a record from which to extract person information
     * @return the Person representing the brides FATHER
     */
    public static Role createBridesFatherFromMarriageRecord(StoreReference<Marriage> marriage_record_ref, long original_record_type) throws StoreException, BucketException {

        Marriage marriage_record = marriage_record_ref.getReferend();

        String surname = marriage_record.getString(Marriage.BRIDE_FATHERS_SURNAME);
        if (surname.equals("0")) {
            surname = marriage_record.getString(Marriage.BRIDE_SURNAME); // TODO factor out - not sure where to
        }
        String forename = marriage_record.getString(Marriage.BRIDE_FATHERS_FORENAME);

        return new Role(surname, forename, MALE, BRIDES_FATHER, marriage_record_ref, original_record_type);
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

        return new Role(surname, forename, FEMALE, BRIDES_MOTHER, marriage_record_ref, original_record_type);
    }

    /**
     * Creates a Role record for the grooms FATHER for a given marriage record
     *
     * @param marriage_record_ref a reference to a record from which to extract person information
     * @return the Person representing the grooms FATHER
     */
    public static Role createGroomsFatherFromMarriageRecord(StoreReference<Marriage> marriage_record_ref, long original_record_type) throws StoreException, BucketException {

        Marriage marriage_record = marriage_record_ref.getReferend();

        String surname = marriage_record.getString(Marriage.GROOM_FATHERS_SURNAME);
        if (surname.equals("0")) {
            surname = marriage_record.getString(Marriage.GROOM_SURNAME); // TODO factor out - not sure where to
        }
        String forename = marriage_record.getString(Marriage.GROOM_FATHERS_FORENAME);

        return new Role(surname, forename, MALE, GROOMS_FATHER, marriage_record_ref, original_record_type);
    }

    /**
     * Creates a Role record for the groom's mother for a given marriage record
     *
     * @param marriage_record_ref a reference to a record from which to extract person information
     * @return the Person representing the grooms mother FATHER
     */
    public static Role createGroomsMotherFromMarriageRecord(StoreReference<Marriage> marriage_record_ref, long original_record_type) throws StoreException, BucketException {

        Marriage marriage_record = marriage_record_ref.getReferend();

        String surname = marriage_record.getString(Marriage.GROOM_MOTHERS_MAIDEN_SURNAME);
        String forename = marriage_record.getString(Marriage.GROOM_MOTHERS_FORENAME);

        return new Role(surname, forename, FEMALE, RolePlayed.GROOMS_MOTHER, marriage_record_ref, original_record_type);
    }

    //*********************** Getter methods ***********************//

    // Basic selectors operate over data stored in this role

    public String getSurname() {

        return getString(SURNAME);
    }

    public String getForename() {

        return getString(FORENAME);
    }

    public String getSex() {

        return getString(SEX);
    }

    public RolePlayed getRole() {

        return RolePlayed.valueOf(getString(ROLE));
    }

    public ILXP getOriginalRecord() {

        String serialised = getString(ORIGINAL_RECORD);
        StoreReference ref = new StoreReference(serialised);
        try {
            return ref.getReferend();
        }
        catch (BucketException e) {
            ErrorHandling.error("Cannot deference: " + serialised);
            return null; // should not reach
        }
    }

    public long getOriginalRecordType() {

        return getLong(ORIGINAL_RECORD_TYPE);
    }

    // Complex selectors operate over data stored in original record

    public String getFathersForename() {

        switch (getRole()) {

            case PRINCIPAL: {
                return getOriginalRecord().getString(BirthFamilyGT.FATHERS_FORENAME);
            }
            case BRIDE: {
                return getOriginalRecord().getString(Marriage.BRIDE_FATHERS_FORENAME);
            }
            case GROOM: {
                return getOriginalRecord().getString(Marriage.GROOM_FATHERS_FORENAME);
            }

            default:
                return ""; // rest return null - we don't know who they are
        }
    }

    public String getFathersSurname() {

        switch (getRole()) {

            case PRINCIPAL: {
                return getOriginalRecord().getString(BirthFamilyGT.FATHERS_SURNAME);
            }
            case BRIDE: {
                return getOriginalRecord().getString(Marriage.BRIDE_FATHERS_SURNAME);
            }
            case GROOM: {
                return getOriginalRecord().getString(Marriage.GROOM_FATHERS_SURNAME);
            }

            default:
                return ""; // rest return null - we don't know who they are
        }
    }

    public String getFathersOccupation() {

        switch (getRole()) {

            case PRINCIPAL: {
                return getOriginalRecord().getString(BirthFamilyGT.FATHERS_OCCUPATION);
            }
            case BRIDE: {
                return getOriginalRecord().getString(Marriage.BRIDE_FATHER_OCCUPATION);
            }
            case GROOM: {
                return getOriginalRecord().getString(Marriage.BRIDE_FATHER_OCCUPATION);
            }

            default:
                return ""; // rest return null - we don't know who they are
        }
    }

    public String getMothersForename() {

        switch (getRole()) {

            case PRINCIPAL: {
                return getOriginalRecord().getString(BirthFamilyGT.MOTHERS_FORENAME);
            }
            case BRIDE: {
                return getOriginalRecord().getString(Marriage.BRIDE_MOTHERS_FORENAME);
            }
            case GROOM: {
                return getOriginalRecord().getString(Marriage.GROOM_MOTHERS_FORENAME);
            }

            default:
                return ""; // rest return null - we don't know who they are
        }
    }

    public String getMothersMaidenSurname() {

        switch (getRole()) {

            case PRINCIPAL: {
                return getOriginalRecord().getString(BirthFamilyGT.MOTHERS_MAIDEN_SURNAME);
            }
            case BRIDE: {
                return getOriginalRecord().getString(Marriage.BRIDE_MOTHERS_MAIDEN_SURNAME);
            }
            case GROOM: {
                return getOriginalRecord().getString(Marriage.GROOM_MOTHERS_MAIDEN_SURNAME);
            }

            default:
                return ""; // rest return null - we don't know who they are
        }
    }

    public String getOccupation() {

        switch (getRole()) {

            case PRINCIPAL: {
                return getOriginalRecord().getString(Death.OCCUPATION); // ????
            }
            case GROOM: {
                return getOriginalRecord().getString(Marriage.GROOM_OCCUPATION);
            }
            case GROOMS_FATHER: {
                return getOriginalRecord().getString(Marriage.GROOM_FATHERS_OCCUPATION);
            }

            case BRIDES_FATHER: {
                return getOriginalRecord().getString(Marriage.BRIDE_OCCUPATION);
            }
            case FATHER: {
                return getOriginalRecord().getString(BirthFamilyGT.FATHERS_OCCUPATION);
            }

            default:
                return "";
        }
    }

    public String getPlaceOfMarriage() {

        switch (getRole()) {

            case PRINCIPAL: {
                return getOriginalRecord().getString(BirthFamilyGT.PARENTS_PLACE_OF_MARRIAGE);
            }
            case GROOM:
            case BRIDE: {
                return getOriginalRecord().getString(Marriage.REGISTRATION_DISTRICT_NUMBER); // TODO IS THIS THE SAME AS PARENTS_PLACE_OF_MARRIAGE???
            }

            default:
                return "";
        }
    }

    public String getDateOfMarriage() {

        switch (getRole()) {

            case PRINCIPAL: {
                return extractDateOfMarriageFromBirthRecord(getOriginalRecord());
            }
            case GROOM:
            case BRIDE: {
                return extractDateOfMarriageFromMarriageRecord(getOriginalRecord());
            }

            default:
                return "";
        }
    }

    private String extractDateOfMarriageFromBirthRecord(final ILXP record) {

        return record.getString(BirthFamilyGT.PARENTS_DAY_OF_MARRIAGE) + DATE_SEPARATOR + record.getString(BirthFamilyGT.PARENTS_MONTH_OF_MARRIAGE) + DATE_SEPARATOR + record.getString(BirthFamilyGT.PARENTS_YEAR_OF_MARRIAGE);
    }

    private String extractDateOfMarriageFromMarriageRecord(final ILXP record) {

        return record.getString(Marriage.MARRIAGE_DAY) + DATE_SEPARATOR + record.getString(Marriage.MARRIAGE_MONTH) + DATE_SEPARATOR + record.getString(Marriage.MARRIAGE_YEAR);
    }

    //*********************** utility methods ***********************//

    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append("\tRole: " + this.getRole() + "\n");
        builder.append("\tSex: " + this.getSex() + "\n");
        builder.append("\tfirstname: " + this.getForename() + "\n");
        builder.append("\tsurname: " + this.getSurname() + "\n");
        builder.append("\tFATHER fn: " + this.getFathersForename() + "\n");
        builder.append("\tFATHER ln: " + this.getFathersSurname() + "\n");
        builder.append("\tmother fn: " + this.getMothersForename() + "\n");
        builder.append("\tmother ln: " + this.getMothersMaidenSurname() + "\n");
        return builder.toString();
    }
}
