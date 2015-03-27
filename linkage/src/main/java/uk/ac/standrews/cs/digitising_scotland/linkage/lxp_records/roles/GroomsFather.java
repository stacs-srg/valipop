package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.roles;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Role;
import uk.ac.standrews.cs.jstore.impl.exceptions.StoreException;

/**
 * Created by al on 26/03/15.
 */
public class GroomsFather extends Role {

    public GroomsFather(Marriage marriage_record, long original_record_type) throws StoreException {

        super(marriage_record.getString(
                marriage_record.getString(Marriage.GROOM_FATHERS_SURNAME)).equals("0") ? marriage_record.getString(Marriage.GROOM_SURNAME) : marriage_record.getString(Marriage.GROOM_FATHERS_SURNAME),
                marriage_record.getString(Marriage.GROOM_FATHERS_FORENAME), "M", role_played.grooms_father, marriage_record, original_record_type);

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
