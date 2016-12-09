package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.types.LXPBaseType;
import uk.ac.standrews.cs.storr.types.LXP_SCALAR;

/**
 * Created by al on 03/10/2014.
 */
public class Marriage extends AbstractLXP {

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String ORIGINAL_ID = "ORIGINAL_ID";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String GROOM_MOTHERS_MAIDEN_SURNAME = "groom_mothers_maiden_surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String GROOM_SURNAME = "groom_surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String GROOM_OCCUPATION = "groom_occupation";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BRIDE_FATHER_OCCUPATION = "bride_father_occupation";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String GROOM_FATHERS_FORENAME = "groom_fathers_forename";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String CHANGED_GROOM_FORENAME = "changed_groom_forename";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String IMAGE_QUALITY = "image_quality";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BRIDE_FATHERS_FORENAME = "bride_fathers_forename";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BRIDE_ADDRESS = "bride_address";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BRIDE_MOTHERS_MAIDEN_SURNAME = "bride_mothers_maiden_surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String GROOM_FATHERS_OCCUPATION = "groom_fathers_occupation";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String ENTRY = "ENTRY";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String GROOM_ADDRESS = "groom_address";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String MARRIAGE_MONTH = "marriage_month";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String MARRIAGE_YEAR = "marriage_year";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String GROOM_DID_NOT_SIGN = "groom_did_not_sign";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BRIDE_MARITAL_STATUS = "bride_marital_status";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String DENOMINATION = "denomination";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BRIDE_FATHER_DECEASED = "bride_father_deceased";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String GROOM_FORENAME = "groom_forename";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BRIDE_OCCUPATION = "bride_occupation";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String CHANGED_GROOM_SURNAME = "changed_groom_surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BRIDE_DID_NOT_SIGN = "bride_did_not_sign";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BRIDE_MOTHERS_FORENAME = "bride_mothers_forename";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BRIDE_MOTHER_DECEASED = "bride_mother_deceased";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String GROOM_MOTHERS_FORENAME = "groom_mothers_forename";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String GROOM_MOTHER_DECEASED = "groom_mother_deceased";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String REGISTRATION_DISTRICT_NUMBER = "REGISTRATION_DISTRICT_NUMBER";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String REGISTRATION_DISTRICT_SUFFIX = "REGISTRATION_DISTRICT_SUFFIX";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BRIDE_SURNAME = "bride_surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String GROOM_MARITAL_STATUS = "groom_marital_status";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BRIDE_FATHERS_SURNAME = "bride_fathers_surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BRIDE_AGE_OR_DATE_OF_BIRTH = "bride_age_or_date_of_birth";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String CHANGED_BRIDE_SURNAME = "changed_bride_surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String CORRECTED_ENTRY = "corrected_entry";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String CHANGED_BRIDE_FORENAME = "changed_bride_forename";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BRIDE_FORENAME = "bride_forename";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String GROOM_FATHERS_SURNAME = "groom_fathers_surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String GROOM_FATHER_DECEASED = "groom_father_deceased";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String GROOM_AGE_OR_DATE_OF_BIRTH = "groom_age_or_date_of_birth";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String YEAR_OF_REGISTRATION = "YEAR_OF_REGISTRATION";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String MARRIAGE_DAY = "marriage_day";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String PLACE_OF_MARRIAGE = "place_of_marriage";

    //******************** Constructors ********************

    public Marriage() {

        super();
    }

    public Marriage(long persistent_object_id, JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException, IllegalKeyException {

        super(persistent_object_id, reader, repository, bucket);
    }

    //******************** Selectors ********************

    public String getGroomsForename() {

        return getString(GROOM_FORENAME);
    }

    public String getGroomsSurname() {

        return getString(GROOM_SURNAME);
    }

    public String getBridesForename() {

        return getString(BRIDE_FORENAME);
    }

    public String getBridesSurname() {

        return getString(BRIDE_SURNAME);
    }

    public String getPlaceOfMarriage() {

        return getString(PLACE_OF_MARRIAGE);
    }

    public String getDateOfMarriage() {

        return cleanDate(getString(MARRIAGE_DAY) , getString(MARRIAGE_MONTH) , getString(MARRIAGE_YEAR));
    }
}
