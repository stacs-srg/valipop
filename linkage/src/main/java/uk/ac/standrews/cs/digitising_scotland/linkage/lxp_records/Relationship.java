package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.jstore.impl.StoreReference;
import uk.ac.standrews.cs.jstore.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.jstore.impl.exceptions.StoreException;
import uk.ac.standrews.cs.jstore.types.LXP_REF;
import uk.ac.standrews.cs.jstore.types.LXP_SCALAR;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

import static uk.ac.standrews.cs.jstore.types.LXPBaseType.STRING;

/**
 * Created by al on 17/8/16
 *
 */
public class Relationship extends AbstractLXP {

    public enum relationship_kind { fatherof, motherof, marriedto }

    @LXP_REF(type = "role")
    public static final String SUBJECT = "subject";
    @LXP_REF(type = "role")
    public static final String OBJECT = "object";
    @LXP_SCALAR(type = STRING)
    public static final String RELATIONSHIP = "relationship";
    @LXP_SCALAR(type = STRING)
    public static final String EVIDENCE = "evidence";

    public Relationship() {
        super();
    }

    public Relationship(long persistent_object_id, JSONReader reader) throws PersistentObjectException, IllegalKeyException {

        super(persistent_object_id, reader);
    }

    public Relationship( StoreReference subject, StoreReference object, relationship_kind relationship, String evidence  ) throws StoreException {

        this();
        try {
            put( SUBJECT, subject.toString() );
            put( OBJECT, subject.toString() );
            put(RELATIONSHIP, relationship.name() );
            put(EVIDENCE, evidence );

        } catch (IllegalKeyException e) {
            ErrorHandling.error("Illegal key in OID");
        }

    }


}
