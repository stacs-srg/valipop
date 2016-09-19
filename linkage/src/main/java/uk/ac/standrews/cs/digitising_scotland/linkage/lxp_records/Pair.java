package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import org.json.JSONException;
import org.json.JSONWriter;
import uk.ac.standrews.cs.digitising_scotland.linkage.interfaces.IPair;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.impl.exceptions.KeyNotFoundException;
import uk.ac.standrews.cs.storr.impl.exceptions.TypeMismatchFoundException;
import uk.ac.standrews.cs.storr.interfaces.ILXP;
import uk.ac.standrews.cs.storr.interfaces.IReferenceType;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by al on 19/06/2014.
 */
public class Pair<T extends ILXP> extends AbstractLXP implements IPair<T> {
    private long typeLabel;
    private T first;
    private T second;

    public Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public Pair(long persistent_Object_id, JSONReader reader, long required_type_labelID) throws PersistentObjectException, IllegalKeyException {
        super(persistent_Object_id, reader);
        this.typeLabel = required_type_labelID;
    }

    public T first() {
        return first;
    }

    public T second() {
        return second;
    }

    @Override
    public long getTypeLabel() {
        return typeLabel;
    }

    @Override
    public long getId() {
        return this.getId();
    }

    @Override
    public void serializeToJSON(JSONWriter writer) throws JSONException {
    }

    @Override
    public Object get(String label) throws KeyNotFoundException {
        if( label.equals("first")) {
            return first;
        }
        if( label.equals("second")) {
            return second;
        }
        throw new KeyNotFoundException( "Key not found: " + label );
    }

    @Override
    public String getString(String key) {
        return null;
    }

    @Override
    public double getDouble(String label) throws KeyNotFoundException, TypeMismatchFoundException {
        throw new KeyNotFoundException( "Key not found: " + label );
    }

    @Override
    public int getInt(String label) throws KeyNotFoundException, TypeMismatchFoundException {
        throw new KeyNotFoundException( "Key not found: " + label );
    }

    @Override
    public boolean getBoolean(String label) throws KeyNotFoundException, TypeMismatchFoundException {
        throw new KeyNotFoundException( "Key not found: " + label );
    }

    @Override
    public long getLong(String label) throws KeyNotFoundException, TypeMismatchFoundException {
        throw new KeyNotFoundException( "Key not found: " + label );
    }

    @Override
    public void put(String key, String value) {

    }

    @Override
    public void put(String label, double value) {

    }

    @Override
    public void put(String label, int value) {

    }

    @Override
    public void put(String label, boolean value) {

    }

    @Override
    public void put(String label, long value) {

    }

    @Override
    public boolean containsKey(String key) {
        return key.equals("first") || key.equals("second");
    }

    @Override
    public Set<String> getLabels() {
        String[] labels = new String[]{"first", "second"};
        return new HashSet<String>(Arrays.asList(labels));
    }

    @Override
    public void addTypeLabel(IReferenceType label) throws Exception {
        throw new Exception( "Cannot add new label to Pair");
    }


    @Override
    public ILXP create(long label_id, JSONReader reader) throws PersistentObjectException {
        throw new PersistentObjectException( "Cannot create Pairs in this manner");
    }


}
