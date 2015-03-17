package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.KeyNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.StoreException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.TypeMismatchFoundException;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.types.LXP_SCALAR;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

import static uk.ac.standrews.cs.digitising_scotland.jstore.types.LXPBaseType.STRING;

/**
 * Created by al on 03/10/2014.
 */
public class Person extends AbstractLXP {

    // Person labels

    @LXP_SCALAR(type = STRING)
    public static final String SURNAME = "surname";
    @LXP_SCALAR(type = STRING)
    public static final String FORENAME = "forename";
    @LXP_SCALAR(type = STRING)
    public static final String SEX = "sex";
    @LXP_SCALAR(type = STRING)
    public static final String FATHERS_FORENAME = "fathers_forename";
    @LXP_SCALAR(type = STRING)
    public static final String FATHERS_SURNAME = "fathers_surname";
    @LXP_SCALAR(type = STRING)
    public static final String FATHERS_OCCUPATION = "fathers_occupation";
    @LXP_SCALAR(type = STRING)
    public static final String MOTHERS_FORENAME = "mothers_forename";
    @LXP_SCALAR(type = STRING)
    public static final String MOTHERS_SURNAME = "mothers_surname";
    @LXP_SCALAR(type = STRING)
    public static final String MOTHERS_MAIDEN_SURNAME = "mothers_maiden_surname";
    //    @LXP_SCALAR(type = STRING)
//    public static final String CHANGED_SURNAME = "changed_surname";
//    @LXP_SCALAR(type = STRING)
//    public static final String CHANGED_FORENAME = "changed_forename";
//    @LXP_SCALAR(type = STRING)
//    public static final String CHANGED_MOTHERS_MAIDEN_SURNAME = "changed_mothers_maiden_surname";
    @LXP_SCALAR(type = STRING)
    public static final String ORIGINAL_RECORD_ID = "original_record_id";
    @LXP_SCALAR(type = STRING)
    public static final String ROLE = "role";
    @LXP_SCALAR(type = STRING)
    public static final String OCCUPATION = "occupation";

    public Person() {
        super();
    }

    public Person(JSONReader reader) throws PersistentObjectException, IllegalKeyException {

        super(reader);
    }

    public Person(String surname, String forename, String sex, String fathers_forename, String fathers_surname, String fathers_occupation, String mothers_forename, String mothers_surname, String mothers_maiden_surname, String original_record_id, String role, String occupation) throws StoreException {

        this();
        try {
            put(SURNAME, surname);
            put(FORENAME, forename);
            put(SEX, sex);
            put(FATHERS_FORENAME, fathers_forename);
            put(FATHERS_SURNAME, fathers_surname);
            put(FATHERS_OCCUPATION, fathers_occupation);
            put(MOTHERS_FORENAME, mothers_forename);
            put(MOTHERS_SURNAME, mothers_surname);
            put(MOTHERS_MAIDEN_SURNAME, mothers_maiden_surname);
            put(ORIGINAL_RECORD_ID, original_record_id);
            put(ROLE, role);
            put(OCCUPATION, occupation);
        } catch (IllegalKeyException e) {
            ErrorHandling.error("Illegal key in LXP");
        }

    }

    public static Person createPersonFromOwnBirthDeath(ILXP BD_record) throws KeyNotFoundException, TypeMismatchFoundException, StoreException {// TODO rewrite as typed

        String surname = BD_record.getString(SURNAME);
        String forename = BD_record.getString(FORENAME);
        String sex = BD_record.getString(SEX);
        String fathers_forename = BD_record.getString(FATHERS_FORENAME);

        String fathers_surname = BD_record.getString(FATHERS_SURNAME);
        if (fathers_surname.equals("0")) {
            fathers_surname = BD_record.getString(SURNAME);           // TODO move this code elsewhere
        }

        String fathers_occupation = BD_record.getString(FATHERS_OCCUPATION);
        String mothers_forename = BD_record.getString(MOTHERS_FORENAME);

        String mothers_surname = BD_record.getString(MOTHERS_SURNAME);
        if (mothers_surname.equals("0")) {
            mothers_surname = BD_record.getString(SURNAME);        // TODO move this code elsewhere
        }

        String mothers_maiden_surname = BD_record.getString(MOTHERS_MAIDEN_SURNAME);
        String original_record_id = Long.toString(BD_record.getId());
        String original_record_type = "Birth";
        String role = "baby";
        String occupation = "";

        return new Person(surname, forename, sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                original_record_id, role, occupation);
    }

    public static Person createFatherFromChildsBirthDeath(Person child, Long original_record_id) throws KeyNotFoundException, TypeMismatchFoundException, StoreException {// TODO rewrite as typed

        if (child.getString(FATHERS_SURNAME).equals("")) {
            return null;
        }

        String surname = child.getString(FATHERS_SURNAME);
        String forename = child.getString(FATHERS_FORENAME);
        String sex = "M"; //  this is the father
        String fathers_forename = ""; // unknown - father of father
        String fathers_surname = ""; //unknown - father of father  - could guess but no
        String fathers_occupation = ""; // unknown - father of father
        String mothers_forename = ""; // unknown - mother of father
        String mothers_surname = ""; //unknown - mother of father  - could guess but no
        String mothers_maiden_surname = ""; // unknown - mother of father

        String role = "father";
        String occupation = child.getString(FATHERS_OCCUPATION);

        return new Person(surname, forename, sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                original_record_id.toString(), role, occupation);
    }

    public static Person createMotherFromChildsBirthDeath(ILXP child, Long original_record_id) throws KeyNotFoundException, TypeMismatchFoundException, StoreException {// TODO rewrite as typed

        if (child.getString(FATHERS_SURNAME).equals("")) {
            return null;
        }

        String surname = child.getString(MOTHERS_SURNAME);
        String forename = child.getString(MOTHERS_FORENAME);
        String sex = "F"; //  this is the mother
        String fathers_forename = ""; // unknown - father of mother
        String fathers_surname = ""; //unknown - father of mother  - could guess but no

        String fathers_occupation = ""; // unknown - father of mother
        String mothers_forename = ""; // unknown - mother of mother
        String mothers_surname = ""; //unknown - mother of mother  - could guess but no

        String mothers_maiden_surname = ""; // unknown - mother of mother
        String changed_surname = ""; // unknown
        String changed_forename = ""; // unknown
        String changed_mothers_maiden_surname = ""; // unknown


        String role = "mother";
        String occupation = child.getString(FATHERS_OCCUPATION);

        return new Person(surname, forename, sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                original_record_id.toString(), role, occupation);

    }

    /**
     * Creates a Person record for the bride for a given marriage record
     *
     * @param marriage_record a record from which to extract person information
     * @return a Person representing the bride
     */
    public static Person createBrideFromMarriageRecord(Marriage marriage_record) throws KeyNotFoundException, TypeMismatchFoundException, StoreException {// TODO rewrite as typed

        String surname = marriage_record.getString(Marriage.BRIDE_SURNAME);
        String forename = marriage_record.getString(Marriage.BRIDE_FORENAME);
        String sex = "F"; //  this is the bride
        String fathers_forename = marriage_record.getString(Marriage.BRIDE_FATHERS_FORENAME);

        String fathers_surname = marriage_record.getString(Marriage.BRIDE_FATHERS_SURNAME);
        if (fathers_surname.equals("0")) {
            fathers_surname = marriage_record.getString(Marriage.BRIDE_SURNAME); // TODO factor out?
        }

        String fathers_occupation = ""; // TODO a problem here - marriage_record.getString(BRIDE_FATHER_OCCUPATION);
        String mothers_forename = marriage_record.getString(Marriage.BRIDE_MOTHERS_FORENAME);
        String mothers_surname = marriage_record.getString(Marriage.BRIDE_FATHERS_SURNAME);   // Assumes the mother's surname is same as father's - OK??
        if (mothers_surname.equals("0")) {
            mothers_surname = marriage_record.getString(Marriage.BRIDE_SURNAME);
        }

        String mothers_maiden_surname = ""; // unknown??    // TODO check this

        String original_record_id = Long.toString(marriage_record.getId());
        String original_record_type = "marriage";
        String role = "bride";
        String occupation = "";

        return new Person(surname, forename, sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                original_record_id, role, occupation);
    }

    /**
     * Creates a Person record for the groom for a given marriage record
     *
     * @param marriage_record a record from which to extract person information
     * @return the Person representing the groom
     */
    public static Person createGroomFromMarriageRecord(ILXP marriage_record) throws KeyNotFoundException, TypeMismatchFoundException, StoreException {// TODO rewrite as typed

        String surname = marriage_record.getString(Marriage.GROOM_SURNAME);
        String forename = marriage_record.getString(Marriage.GROOM_FORENAME);
        String sex = "M"; //  this is the groom
        String fathers_forename = marriage_record.getString(Marriage.GROOM_FATHERS_FORENAME);

        String fathers_surname = marriage_record.getString(Marriage.GROOM_FATHERS_SURNAME);
        if (fathers_surname.equals("0")) {
            fathers_surname = marriage_record.getString(Marriage.GROOM_SURNAME); // TODO factor out?
        }

        String fathers_occupation = marriage_record.getString(Marriage.GROOM_FATHERS_OCCUPATION);
        String mothers_forename = marriage_record.getString(Marriage.GROOM_MOTHERS_FORENAME);
        String mothers_surname = marriage_record.getString(Marriage.GROOM_FATHERS_SURNAME);
        if (mothers_surname.equals("0")) {
            mothers_surname = marriage_record.getString(SURNAME);
        }

        String mothers_maiden_surname = ""; // unknown??    // TODO check this

        String original_record_id = Long.toString(marriage_record.getId());
        String role = "groom";
        String occupation = "";

        return new Person(surname, forename, sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                original_record_id, role, occupation);

    }

    public static Person createBridesFatherFromMarriageRecord(ILXP bride, ILXP marriage_record) throws KeyNotFoundException, TypeMismatchFoundException, StoreException {// TODO rewrite as typed

        String surname = marriage_record.getString(Marriage.BRIDE_FATHERS_SURNAME);
        if (surname.equals("0")) {
            surname = marriage_record.getString(Marriage.BRIDE_SURNAME); // TODO factor out?
        }

        String forename = marriage_record.getString(Marriage.BRIDE_FATHERS_FORENAME);
        String sex = "M"; //  this is the brides father

        String fathers_forename = ""; // unknown - father of bride's father
        String fathers_surname = ""; //unknown - father of bride's father
        String fathers_occupation = ""; // unknown - father of bride's father
        String mothers_forename = ""; // unknown - mother of bride's father
        String mothers_surname = ""; //unknown - mother of bride's father
        String mothers_maiden_surname = ""; // unknown - mother bride's father

        String original_record_id = Long.toString(marriage_record.getId());
        String role = "brides_father";
        String occupation = ""; // TODO a problem here - marriage_record.getString(BRIDE_FATHER_OCCUPATION);

        return new Person(surname, forename, sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                original_record_id, role, occupation);

    }

    public static Person createBridesMotherFromMarriageRecord(ILXP bride, ILXP marriage_record) throws KeyNotFoundException, TypeMismatchFoundException, StoreException { // TODO rewrite as typed

        String surname = marriage_record.getString(Marriage.BRIDE_MOTHERS_MAIDEN_SURNAME);  //<<<<<<<<<<<<< TODO see below
//        if( surname.equals("0") ) {
//            surname = marriage_record.getString(MarriageLabels.BRIDE_SURNAME); // TODO factor out?
//        }

        String forename = marriage_record.getString(Marriage.BRIDE_MOTHERS_FORENAME);
        String sex = "M"; //  this is the brides father

        String fathers_forename = ""; // unknown - father of bride's mother
        String fathers_surname = ""; //unknown - father of bride's mother

        String fathers_occupation = ""; // unknown - father of bride's mother
        String mothers_forename = ""; // unknown - mother of bride's mother
        String mothers_surname = ""; //unknown - mother of bride's mother
        String mothers_maiden_surname = ""; // unknown - mother bride's mother


        String original_record_id = Long.toString(marriage_record.getId());
        String role = "brides_mother";
        String occupation = ""; // unknown

        return new Person(surname, forename, sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                original_record_id, role, occupation);  // TODO Person should also have maiden name???

    }

    public static Person createGroomsFatherFromMarriageRecord(ILXP groom, ILXP marriage_record) throws KeyNotFoundException, TypeMismatchFoundException, StoreException {// TODO rewrite as typed

        String surname = marriage_record.getString(Marriage.GROOM_FATHERS_SURNAME);
        if (surname.equals("0")) {
            surname = marriage_record.getString(Marriage.GROOM_SURNAME); // TODO factor out?
        }

        String forename = marriage_record.getString(Marriage.GROOM_FATHERS_FORENAME);
        String sex = "M"; //  this is the brides father
        String fathers_forename = ""; // unknown - father of groom's father
        String fathers_surname = ""; //unknown - father of groom's father
        String fathers_occupation = ""; // unknown - father of groom's father
        String mothers_forename = ""; // unknown - mother of groom's father
        String mothers_surname = ""; //unknown - mother of groom's father
        String mothers_maiden_surname = ""; // unknown - mother groom's father


        String original_record_id = Long.toString(marriage_record.getId());
        String role = "grooms_father";
        String occupation = marriage_record.getString(Marriage.GROOM_FATHERS_OCCUPATION);

        return new Person(surname, forename, sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                original_record_id, role, occupation);


    }

    public static Person createGroomsMotherFromMarriageRecord(ILXP groom, ILXP marriage_record) throws KeyNotFoundException, TypeMismatchFoundException, StoreException {// TODO rewrite as typed

        String surname = marriage_record.getString(Marriage.GROOM_MOTHERS_MAIDEN_SURNAME);  //<<<<<<<<<<<<< TODO see below
//        if( surname.equals("0") ) {
//            surname = marriage_record.getString(MarriageLabels.GROOM_SURNAME); // TODO factor out?
//        }

        String forename = marriage_record.getString(Marriage.GROOM_MOTHERS_FORENAME);
        String sex = "M"; //  this is the brides father
        String fathers_forename = ""; // unknown - father of bride's mother
        String fathers_surname = ""; //unknown - father of bride's mother
        String fathers_occupation = ""; // unknown - father of bride's mother
        String mothers_forename = ""; // unknown - mother of bride's mother
        String mothers_surname = ""; //unknown - mother of bride's mother
        String mothers_maiden_surname = ""; // unknown - mother bride's mother

        String original_record_id = Long.toString(marriage_record.getId());
        String role = "grooms_mother";
        String occupation = ""; // unknown

        return new Person(surname, forename, sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                original_record_id, role, occupation);  // TODO Person should also have maiden name???

    }

    // Getters

    private String get_val( String key ) {
        String value = "";
        try {
            value = this.getString(key);
        } catch (KeyNotFoundException e) {
            ErrorHandling.exceptionError( e, "No key: " + key + " in Person" );
        } catch (TypeMismatchFoundException e) {
            ErrorHandling.exceptionError( e, "Type mismatch for key: " + key + " in Person");
        }
        return value;
    }


    public String get_surname() { return get_val(SURNAME ); }

    public String get_forename() { return get_val( FORENAME ); }

    public String get_sex() { return get_val( SEX ); }

    public String get_fathers_forename() { return get_val( FATHERS_FORENAME ); }

    public String get_fathers_surname() { return get_val( FATHERS_SURNAME ); }

    public String get_fathers_occupation() { return get_val( FATHERS_OCCUPATION ); }

    public String get_mothers_forename() { return get_val( MOTHERS_FORENAME ); }

    public String get_mothers_surname() { return get_val( MOTHERS_SURNAME ); }

    public String get_mothers_maiden_surname() { return get_val( MOTHERS_MAIDEN_SURNAME ); }

    public String get_original_record_id() { return get_val( ORIGINAL_RECORD_ID ); }

    public String get_role() { return get_val( ROLE ); }

    public String get_occupation() { return get_val( OCCUPATION ); }
}
