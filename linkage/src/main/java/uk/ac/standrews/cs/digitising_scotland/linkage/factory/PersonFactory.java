package uk.ac.standrews.cs.digitising_scotland.linkage.factory;


import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXPFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Role;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

/**
 * Created by al on 03/10/2014.
 */
public class PersonFactory extends TFactory<Role> implements ILXPFactory<Role> {


    public PersonFactory(long personlabelID) {
        this.required_type_labelID = personlabelID;
    }


    @Override
    public Role create(long label_id, JSONReader reader) throws PersistentObjectException, IllegalKeyException {
        return new Role(reader);
    }

}
