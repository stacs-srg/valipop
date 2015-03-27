package uk.ac.standrews.cs.digitising_scotland.linkage.factory;


import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Role;
import uk.ac.standrews.cs.jstore.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.jstore.interfaces.ILXPFactory;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

/**
 * Created by al on 03/10/2014.
 *
 *
 * This class is deprecated - was part of a failed experiment.
 * Delete later
 */
public class RoleFactoryXX extends TFactory<Role> implements ILXPFactory<Role> {

    public RoleFactoryXX(long rolelabelID) {
        this.required_type_labelID = rolelabelID;
    }

    @Override
    public Role create(long label_id, JSONReader reader) throws PersistentObjectException, IllegalKeyException {
        Role role = new Role(reader);
//        try {
//            switch (role.get_role()) {
//                Role.role_played.principal:
//                return new Principal(role.get_original_record(), role.get_original_record_type());
//                father, mother, bride, groom, grooms_father, grooms_mother, brides_father, brides_mother
//            }
//        } catch ( StoreException e ) {
//            throw new PersistentObjectException( e.getMessage() );
//
//        }
        return null;
    }

}
