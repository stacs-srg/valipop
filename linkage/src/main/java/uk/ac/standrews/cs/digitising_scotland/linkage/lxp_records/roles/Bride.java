package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.roles;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Role;
import uk.ac.standrews.cs.jstore.impl.exceptions.StoreException;
import uk.ac.standrews.cs.jstore.interfaces.ILXP;

/**
 * Created by al on 26/03/15.
 */
public class Bride extends Role {

    public Bride(ILXP marriage_record, long original_record_type ) throws StoreException {

        super( marriage_record.getString(Marriage.BRIDE_SURNAME), marriage_record.getString(Marriage.BRIDE_FORENAME), "F", role_played.bride , marriage_record, original_record_type );
    }

    @Override
    public String get_fathers_forename() {
        return get_original_record().getString("bride_fathers_forename");
    }

    @Override
    public String get_fathers_surname() {
        return get_original_record().getString("bride_fathers_surname");
    }

    @Override
    public String get_fathers_occupation() {
        return get_original_record().getString("bride_fathers_occupation");
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
