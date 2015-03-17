package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.StoreException;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

/**
 * Created by al on 03/10/2014.
 *
 * TODO need to check on typing here (see more below).
 * This type has no declared labels or types.
 * Is this ok?
 * What about the mapping between oids and references in the fields person1 and person2 - is this OK?
 * Need to check on this - al 17-3-15.
 *
 */
public class Relationship extends AbstractLXP {

    /*
     * Denotes the relationship between person1 and person2.
     */
    public enum RelationShipKind{
        FatherOf,
        MotherOf,
        BabyOf
    };

    public Relationship( Person p1 , RelationShipKind relationship_type, Person p2 , ILXP evidence) {  // TODO need to think about evidence and how to encode.
        try {
            this.put("person1",p1.getId() );
            this.put("person2", p2.getId() );
            this.put("relationship", relationship_type.toString());
            this.put( "evidence", evidence.getId() );

        } catch (IllegalKeyException e) {
            e.printStackTrace();
        }
    }



    public Relationship() throws StoreException {
        super();
    }

    public Relationship(long label_id, JSONReader reader) throws PersistentObjectException, IllegalKeyException, StoreException {

        super(reader);

    }


}
