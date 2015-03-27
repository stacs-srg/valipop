package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.roles;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthDeath;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Role;
import uk.ac.standrews.cs.jstore.impl.exceptions.StoreException;

/**
 * Created by al on 26/03/15.
 */
public class Mother extends Role {

    public Mother(BirthDeath BD_record, long original_record_type) throws StoreException {

        super(BD_record.getString(BirthDeath.MOTHERS_SURNAME), BD_record.getString(BirthDeath.MOTHERS_FORENAME), "F", role_played.mother, BD_record, original_record_type);
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
