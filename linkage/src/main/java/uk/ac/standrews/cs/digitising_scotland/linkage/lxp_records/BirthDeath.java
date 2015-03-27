package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.jstore.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.jstore.types.LXPBaseType;
import uk.ac.standrews.cs.jstore.types.LXP_SCALAR;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

/**
 * Created by al on 26/03/15.
 */
public class BirthDeath extends AbstractLXP {

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

    public BirthDeath() {
        super();
    }

    public BirthDeath(long persistent_Object_id, JSONReader reader) throws PersistentObjectException, IllegalKeyException {

        super(persistent_Object_id, reader);

    }
}
