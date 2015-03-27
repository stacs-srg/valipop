package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.roles;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Role;
import uk.ac.standrews.cs.jstore.impl.exceptions.StoreException;
import uk.ac.standrews.cs.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthDeath;

/**
 * Created by al on 26/03/15.
 */
public class Principal extends Role {

    public Principal(ILXP BD_record, long original_record_type ) throws StoreException {

        super( BD_record.getString(BirthDeath.SURNAME), BD_record.getString(BirthDeath.FORENAME), BD_record.getString(BirthDeath.SEX), role_played.principal , BD_record, original_record_type );
    }

    public String get_fathers_forename() {
        return get_original_record().getString("fathers_forename");
    }

    public String get_fathers_surname() {
        return get_original_record().getString("fathers_surname");
    }

    public String get_fathers_occupation() {
        return get_original_record().getString("fathers_occupation");
    }

    public String get_mothers_forename() {
        return get_original_record().getString("mothers_forename");
    }

    public String get_mothers_surname() {
        return get_original_record().getString("mothers_surname");
    }

    public String get_mothers_maiden_surname() {
        return get_original_record().getString("mothers_maiden_surname");
    }

    public String get_occupation() {
        return get_original_record().getString("occupation");
    }
}
