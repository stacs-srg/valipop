package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.types.LXPBaseType;
import uk.ac.standrews.cs.storr.types.LXP_SCALAR;

/**
 *
 * Birth Record extended wih Family ground truth for processing Kilmarnock and Isle of Skye datasets
 *
 * Created by al on 03/10/2014.
 */
public class BirthFamilyGT extends AbstractLXP {

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String ORIGINAL_ID = "ORIGINAL_ID";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String FORENAME = "forename";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String CHANGED_FORENAME = "changed_forename";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String SURNAME = "surname";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String CHANGED_SURNAME = "changed_surname";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String SEX = "sex";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String FATHERS_FORENAME = "fathers_forename";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String FATHERS_SURNAME = "fathers_surname";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String MOTHERS_FORENAME = "mothers_forename";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String MOTHERS_SURNAME = "mothers_surname";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String MOTHERS_MAIDEN_SURNAME = "mothers_maiden_surname";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String CHANGED_MOTHERS_MAIDEN_SURNAME = "changed_mothers_maiden_surname";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String FATHERS_OCCUPATION = "fathers_occupation";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String YEAR_OF_REGISTRATION = "YEAR_OF_REGISTRATION";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String ENTRY = "ENTRY";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String REGISTRATION_DISTRICT_SUFFIX = "REGISTRATION_DISTRICT_SUFFIX";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String REGISTRATION_DISTRICT_NUMBER = "REGISTRATION_DISTRICT_NUMBER";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String CORRECTED_ENTRY = "corrected_entry";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String IMAGE_QUALITY = "image_quality";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BIRTH_DAY = "birth_day";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BIRTH_MONTH = "birth_month";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BIRTH_YEAR = "birth_year";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BIRTH_ADDRESS = "birth_address";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String ILLEGITIMATE_INDICATOR = "illegitimate_indicator";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String ADOPTION = "adoption";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String PARENTS_DAY_OF_MARRIAGE = "parents_day_of_marriage";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String PARENTS_MONTH_OF_MARRIAGE = "parents_month_of_marriage";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String PARENTS_YEAR_OF_MARRIAGE = "parents_year_of_marriage";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String PARENTS_PLACE_OF_MARRIAGE = "parents_place_of_marriage";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String INFORMANT_DID_NOT_SIGN = "informant_did_not_sign";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String INFORMANT = "informant";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String FAMILY = "family";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String FAMILY_BEWARE = "family beware";

    public BirthFamilyGT() {

        super();
    }

    public BirthFamilyGT(long persistent_Object_id, JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException, IllegalKeyException {

        super(persistent_Object_id, reader, repository, bucket);
    }

    public String getFathersForename() {

        return getString(FATHERS_FORENAME);
    }

    public String getFathersSurname() {

        return getString(FATHERS_SURNAME);
    }

    public String getMothersForename() {

        return getString(MOTHERS_FORENAME);
    }

    public String getMothersMaidenSurname() {

        return getString(MOTHERS_MAIDEN_SURNAME);
    }

    public String getPlaceOfMarriage() {

        return getString(PARENTS_PLACE_OF_MARRIAGE);
    }

    public String getDateOfMarriage() {

        return cleanDate(getString(PARENTS_DAY_OF_MARRIAGE), getString(PARENTS_MONTH_OF_MARRIAGE), getString(PARENTS_YEAR_OF_MARRIAGE));
    }

    public String getDOB() {
        return getString(BIRTH_DAY) + "/" + getString(BIRTH_MONTH) + "/" + getString(BIRTH_YEAR);
    }

}
