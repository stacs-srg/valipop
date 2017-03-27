package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.types.LXPBaseType;
import uk.ac.standrews.cs.storr.types.LXP_SCALAR;

/**
 * Birth Record extended wih Family ground truth for processing Kilmarnock and Isle of Skye datasets
 * <p>
 * Created by al on 03/10/2014.
 */
public class BirthFamilyGT extends Birth {

    // Fields need to be duplicated for reflective use to work.

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String ORIGINAL_ID = Birth.ORIGINAL_ID;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String FORENAME = Birth.FORENAME;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String CHANGED_FORENAME = Birth.CHANGED_FORENAME;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String SURNAME = Birth.SURNAME;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String CHANGED_SURNAME = Birth.CHANGED_SURNAME;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String SEX = Birth.SEX;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String FATHERS_FORENAME = Birth.FATHERS_FORENAME;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String FATHERS_SURNAME = Birth.FATHERS_SURNAME;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String MOTHERS_FORENAME = Birth.MOTHERS_FORENAME;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String MOTHERS_SURNAME = Birth.MOTHERS_SURNAME;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String MOTHERS_MAIDEN_SURNAME = Birth.MOTHERS_MAIDEN_SURNAME;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String CHANGED_MOTHERS_MAIDEN_SURNAME = Birth.CHANGED_MOTHERS_MAIDEN_SURNAME;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String FATHERS_OCCUPATION = Birth.FATHERS_OCCUPATION;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String YEAR_OF_REGISTRATION = Birth.YEAR_OF_REGISTRATION;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String ENTRY = Birth.ENTRY;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String REGISTRATION_DISTRICT_SUFFIX = Birth.REGISTRATION_DISTRICT_SUFFIX;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String REGISTRATION_DISTRICT_NUMBER = Birth.REGISTRATION_DISTRICT_NUMBER;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String CORRECTED_ENTRY = Birth.CORRECTED_ENTRY;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String IMAGE_QUALITY = Birth.IMAGE_QUALITY;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BIRTH_DAY = Birth.BIRTH_DAY;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BIRTH_MONTH = Birth.BIRTH_MONTH;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BIRTH_YEAR = Birth.BIRTH_YEAR;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BIRTH_ADDRESS = Birth.BIRTH_ADDRESS;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String ILLEGITIMATE_INDICATOR = Birth.ILLEGITIMATE_INDICATOR;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String ADOPTION = Birth.ADOPTION;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String PARENTS_DAY_OF_MARRIAGE = Birth.PARENTS_DAY_OF_MARRIAGE;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String PARENTS_MONTH_OF_MARRIAGE = Birth.PARENTS_MONTH_OF_MARRIAGE;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String PARENTS_YEAR_OF_MARRIAGE = Birth.PARENTS_YEAR_OF_MARRIAGE;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String PARENTS_PLACE_OF_MARRIAGE = Birth.PARENTS_PLACE_OF_MARRIAGE;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String INFORMANT_DID_NOT_SIGN = Birth.INFORMANT_DID_NOT_SIGN;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String INFORMANT = Birth.INFORMANT;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String FAMILY = "family";

    public BirthFamilyGT() {

        super();
    }

    public BirthFamilyGT(long persistent_Object_id, JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException, IllegalKeyException {

        super(persistent_Object_id, reader, repository, bucket);
    }
}
