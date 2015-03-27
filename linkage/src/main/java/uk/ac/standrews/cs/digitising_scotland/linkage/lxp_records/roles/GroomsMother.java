package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.roles;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Role;
import uk.ac.standrews.cs.jstore.impl.exceptions.StoreException;

/**
 * Created by al on 26/03/15.
 */
public class GroomsMother extends Role {

    public GroomsMother(Marriage marriage_record, long original_record_type) throws StoreException {

        super(marriage_record.getString(Marriage.GROOM_MOTHERS_MAIDEN_SURNAME), marriage_record.getString(Marriage.GROOM_MOTHERS_FORENAME), "F", role_played.grooms_mother, marriage_record, original_record_type);
    }


    @Override
    public String get_fathers_forename() {
        return null;
    }

    @Override
    public String get_fathers_surname() {
        return null;
    }

    @Override
    public String get_fathers_occupation() {
        return null;
    }

    @Override
    public String get_mothers_forename() {
        return null;
    }

    @Override
    public String get_mothers_surname() {
        return null;
    }

    @Override
    public String get_mothers_maiden_surname() {
        return null;
    }

    @Override
    public String get_occupation() {
        return null;
    }
}
