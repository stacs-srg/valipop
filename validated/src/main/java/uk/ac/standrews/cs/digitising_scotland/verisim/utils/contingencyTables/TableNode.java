package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables;

import java.util.Collection;
import java.util.HashMap;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class TableNode<ParentType, ChildType> {

    ParentType value;
    HashMap<ChildType, TableNode<ChildType, ?>> children = new HashMap<>();

    int count = 0;

    public TableNode(ParentType value, Collection<ChildType> children) {
        this.value = value;

        for(ChildType child : children) {
            this.children.put(child, new TableNode<>(child));
        }

    }

    public TableNode(ParentType value) {
        this.value = value;
    }

    public TableNode<ChildType, ?> addChild(ChildType child) {
        TableNode<ChildType, ?> node = new TableNode<>(child);
        children.put(child, node);
        return node;
    }

    public Collection<TableNode<ChildType, ?>> getChildren() {
        return children.values();
    }

    public ParentType getValue() {
        return value;
    }

    public int getCount() {
        return count;
    }

    public void incrementCount(int i) {
        count += i;
    }

    public TableNode<ChildType, ?> getChild(ChildType value) throws IsLeafException, ChildNotFoundException {

        if(children != null) {
            TableNode<ChildType, ?> node = children.get(value);

            if(node == null) {
                throw new ChildNotFoundException();
            } else {
                return node;
            }

        } else {
            throw new IsLeafException();
        }

    }
}
