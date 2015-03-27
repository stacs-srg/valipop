package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.roles;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Role;
import uk.ac.standrews.cs.jstore.impl.exceptions.StoreException;
import uk.ac.standrews.cs.jstore.interfaces.ILXP;

/**
 * Created by al on 26/03/15.
 */
public class Groom extends Role {

    public Groom(ILXP marriage_record, long original_record_type) throws StoreException {

        super( marriage_record.getString(Marriage.GROOM_SURNAME), marriage_record.getString(Marriage.GROOM_FORENAME), "M", role_played.groom , marriage_record, original_record_type );
    }

    @Override
    public String get_fathers_forename() {
        return get_original_record().getString("groom_fathers_forename");
    }

    @Override
    public String get_fathers_surname() {
        return get_original_record().getString("groom_fathers_surname");
    }

    @Override
    public String get_fathers_occupation() {
        return get_original_record().getString("groom_fathers_occupation");
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
