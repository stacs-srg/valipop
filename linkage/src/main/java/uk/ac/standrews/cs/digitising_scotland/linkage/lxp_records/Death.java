package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.jstore.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.jstore.types.LXPBaseType;
import uk.ac.standrews.cs.jstore.types.LXP_SCALAR;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

/**
 * Created by al on 03/10/2014.
 */
public class Death extends BirthDeath {

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String COD_A = "cod_a";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String COD_B = "cod_b";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String COD_C = "cod_c";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String DEATH_PLACE = "death_place";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BIRTH_DATE = "birth_date";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String DEATH_DAY = "death_day";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String DEATH_MONTH = "death_month";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String DEATH_YEAR = "death_year";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String AGE_AT_DEATH = "age_at_death";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String CHANGED_DEATH_AGE = "changed_death_age";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String OCCUPATION = "occupation";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String MARITAL_STATUS = "marital_status";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String SPOUSES_NAMES = "spouses_names";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String SPOUSES_OCCUPATIONS = "spouses_occupations";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String MOTHER_DECEASED = "mother_deceased";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String FATHER_DECEASED = "father_deceased";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String CERTIFYING_DOCTOR = "certifying_doctor";


    public Death() {
        super();
    }

    public Death(long persistent_Object_id, JSONReader reader) throws PersistentObjectException, IllegalKeyException {

        super(persistent_Object_id, reader);

    }

}

// When these types were encoded as JSON and read in this was the definition from the file deathType.jsn
//{"death_year":"string",
//        "sex":"string",
//        "changed_surname":"string",
//        "surname":"string",
//        "image_quality":"string",
//        "cod_a":"string",
//        "cod_b":"string",
//        "cod_c":"string",
//        "age_at_death":"string",
//        "changed_death_age":"string",
//        "death_month":"string",
//        "fathers_surname":"string",
//        "fathers_occupation":"string",
//        "spouses_names":"string",
//        "death_place":"string",
//        "father_deceased":"string",
//        "certifying_doctor":"string",
//        "REGISTRATION_DISTRICT_SUFFIX":"string",
//        "ENTRY":"string",
//        "mothers_surname":"string",
//        "marital_status":"string",
//        "TYPE":"string",
//        "spouses_occupations":"string",
//        "occupation":"string",
//        "forename":"string",
//        "birth_date":"string",
//        "REGISTRATION_DISTRICT_NUMBER":"string",
//        "mothers_maiden_surname":"string",
//        "mother_deceased":"string",
//        "corrected_entry":"string",
//        "changed_mothers_maiden_surname":"string",
//        "changed_forename":"string",
//        "mothers_forename":"string",
//        "death_day":"string",
//        "YEAR_OF_REGISTRATION":"string",
//        "fathers_forename":"string"
//        }