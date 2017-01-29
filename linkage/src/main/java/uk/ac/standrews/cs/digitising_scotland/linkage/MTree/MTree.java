package uk.ac.standrews.cs.digitising_scotland.linkage.MTree;

import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.storr.interfaces.IRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * Created by al on 13/01/2017.
 */
public class MTree<T> {

    public static final int MAX_LEVEL_SIZE = 20;
    private final Distance<T> distance_wrapper;

    private IRepository repo;
    private Node root;

    public MTree( Distance<T> d )  {

        root = new Node( null, null );
        distance_wrapper = d;
    }

    public Iterator<T> closestN(T query) {
        return null;
    }

    public T nearestNeighbour(T query) {
        return null;
    }

    /**
     * Add some data to the MTree
     * @param data
     */
    public void add( T data ) throws PreConditionException {
        insert( root, data );
    }

    /**
     * Insert some data into some level of the Tree.
     *
     * @param current - the Node of the uk.ac.standrews.cs.digitising_scotland.linkage.MTree into which new data should be inserted.
     * @param data  - the data to insert into the level
     */
    private void insert( Node current, T data ) throws PreConditionException {
        if( current.isLeaf() ) {
            if (current.isFull()) {
                split(current,data);
            } else {
                current.addChild( new Node(data, current.parent) ); // level is not yet full - put the data into the level
                float new_distance = distance_wrapper.distance(current.pivot, data);
                if( new_distance > current.radius ) {
                    current.radius = new_distance;
                }
            }
        } else {
            insert_into_most_appropriate( current, data );
        }
    }

    /**
     * Find the most appropriate node in which data should be inserted.
     * Choose child such that: No enlargement of radius is needed,
     * In case of ties, choose the closest one to the new node.
     * @param current - the node in which we are inserting the data into
     * @param data the new data.
     */
    private void insert_into_most_appropriate(Node current, T data) throws PreConditionException {
        Node most_appropriate_pivot = null;
        float best_distance = 0.0F;
        List<Node> children = current.children;
        // find the most appropriate child.
        for( Node child : children ) {
            float new_distance = distance_wrapper.distance(child.pivot, data);
            if (new_distance <= child.radius) { // new node falls in within this ball
                if (most_appropriate_pivot == null) {
                    // fits and we don't have any other candidates - so choose it
                    most_appropriate_pivot = child;
                    best_distance = new_distance;
                } else if (new_distance < best_distance) { // there is a candidate already
                        // this one is better
                        most_appropriate_pivot = child;
                        best_distance = new_distance;
                } else { // the two distances are equal
                    // choose the closest pivot to the new data
                    if( new_distance < distance_wrapper.distance( data, most_appropriate_pivot.pivot) ) {
                        // TODO not sure about logic here!!!!
                        // this one is better
                        most_appropriate_pivot = child;
                        best_distance = new_distance;
                    }
                }
            }
            // otherwise falls outside the current ball so ignore it.
        }

        insert( most_appropriate_pivot, data ); // do the insertion
    }


    private void split(Node N, T oN) throws PreConditionException {
        //    Split(N,oN): N is leaf oN is new
        //      Let S be the set containing all entries of N and oN
        //      Select pivots p1 and p2 from S
        //      Partition S to S1 and S2 according to p1 and p2
        //      Store S1 in N and S2 in a new allocated node N’
        //      If N isRoot
        //          Allocate a new root and store entries for p1, p2 there
        //      else
        //          (let Np and pp be the parent node and parent pivot of N)   Replace entry pp with p1
        //          If Np is full, then Split(Np,p2)
        //              else store p2 in node Np

        List<Node> S = N.children;
        N.children = new ArrayList<Node>(); // get rid of existing children of the node

        S.add(new Node(oN, N));  // add oN into children - now over full
        // but we are about to perform a split - makes computation easier.
        // Select two new pivot from the children (with data added).

        // Partition S to S1 and S2 according to p1 and p2:
        PairOfNodes chosen_pivots = selectPivots( S );

        Node p1 = chosen_pivots.n1; // keep names same as original pseudo code.
        Node p2 = chosen_pivots.n2;

        S.remove( p1 );     // remove the two pivots from the children on N  //<<<<<<<< ???????
        S.remove( p2 );     //<<<<<<<< ???????

        // Partition S to S1 and S2 according to p1 and p2
        List<Node>[] partition = partitionChildrenIntoPivots( p1,p2,S );   // are p1 and p2 supposed to be in S
        if( partition.length != 2 ) {
            ErrorHandling.error( "Wrong number of children" );
        }

        List<Node> s1 = partition[0]; // keep names same as original pseudo code.
        List<Node> s2 = partition[1]; // keep names same as original pseudo code.

        // Store s1 in N and s2 in a new allocated node N’
        for( Node n : s1 ) {
            N.addChild( n );
        }
        Node Nprime = new Node(p2.pivot, N.parent );
        for( Node n : s2 ) {
            Nprime.addChild( n );
        }

        // at this point...
        //p1 and p2 are still floating, children stored in partitions p1 and p2 are all allocated.
        // they both need to be put somewhere and the tree fixed up.

            if (N.isRoot()) {
                // it is a root
                // Allocate a new root
                // and store entries for p1 and p2 in it
                Node parent = N.parent;
                Node new_node = new Node(oN,parent );
                // this bit deals with the floating nodes p1 and p2
                new_node.addChild( p1 );
                new_node.addChild( p2 );
                // Store the new node in the parent
                parent.addChild( new_node );

            } else {
                // it is a leaf

                // let Np and pp be the parent node and parent pivot of N
                //  Replace entry pp with p1
                //  If Np is full, then Split(Np,p2) else store p2 in node Np

                Node Np = N.parent;
                Np.replacePivot( p1 );
                if( Np.isFull() ) {
                    split( Np, p1.pivot );
                }

            }

        }

    /**
     * Takes two pivots and a list of Nodes and re-partitions into two Lists of nodes
     * Partitioning based on proximity - those nodes closest to p1 go into the  first partition
     * those nodes closest to p2 go intio the second.
     * @param p1 the first pivot
     * @param p2 the second pivot
     * @param s a list of nodes to partition.
     *
     * @return an array of two partitions of nodes
     */
    private List<Node>[] partitionChildrenIntoPivots(Node p1, Node p2, List<Node> s) {

        List<Node> partition1 = new ArrayList<Node>();
        List<Node> partition2 = new ArrayList<Node>();

        for( Node child : s ) {
            float r1 = distance_wrapper.distance(p1.pivot, child.pivot);
            float r2 = distance_wrapper.distance(p1.pivot, child.pivot);

            if( r1 < r2 ) {
                partition1.add(child);
            } else {
                partition2.add(child);
            }
        }
        return new List[]{ partition1, partition2 };
    }


    /**
     * Select pivots using M_RAD algorithm:
     * m_RAD – select p1, p2 with minimum (r1c + r2c)
     * @param s - a list of nodes from which to choose a pivot
     * @return an array containing two Nodes satisfying MRAD property
     */
    private PairOfNodes selectPivots(List<Node> s) throws PreConditionException {
        if( s.size() < 2 ) {
            throw new PreConditionException( "children list from which to select pivots is too small");
        }

        Node smallest_radius_node = s.get(0);
        Node next_smallest_radius_node = s.get(1);

        if( next_smallest_radius_node.radius < smallest_radius_node.radius ) { // swap them over
            Node temp = smallest_radius_node;
            smallest_radius_node = next_smallest_radius_node;
            next_smallest_radius_node = temp;
        }

        for( Node child : s.subList( 2,s.size() - 2 ) ) {
            if( child.radius < smallest_radius_node.radius ) {
                smallest_radius_node = child;
            } else if( child.radius < next_smallest_radius_node.radius ) {
                next_smallest_radius_node = child;
            }
        }

        return new PairOfNodes( smallest_radius_node, next_smallest_radius_node );
    }


    class Node {

        public T pivot;
        public float radius;
        public float distance_to_parent;
        public Node parent;      /// NOT SURE IF YOU NEED
        public List<Node> children;

        public Node(T oN, Node parent ) { //, Distance<D> distance) {
            pivot = oN;
            radius = 0.0F;
            distance_to_parent = parent == null ? 0 : distance_wrapper.distance( oN, parent.pivot);
            children = new ArrayList<Node>();
            this.parent = parent;
            //this.distance = distance;
        }


        /**
         *
         * Pre condition this is only called where a split has already occurred and parent is not full
         * @param newNode the node to add to the Node
         */
        protected void addChild(Node newNode) throws PreConditionException {
            if( this.isNew() ) {
                this.pivot = newNode.pivot;
            }

            if (isFull()) {
                throw new PreConditionException( "level is already full" );
            }
            children.add( newNode );
            float newdistance = distance_wrapper.distance(this.pivot, newNode.pivot);
            if( newdistance > radius ) {
                radius = newdistance;
            }
        }

        public void replacePivot(Node p1) {
        }

        public boolean isLeaf() { return this.radius == 0.0F; }

        public boolean isRoot() { return ! isLeaf(); }

        public boolean isFull() {
                return children.size() >= MTree.MAX_LEVEL_SIZE;
        }

        public boolean isEmpty() {
                return children.isEmpty();
        }

        public boolean isNew() {
                return pivot == null;
        }


    }

    public class PairOfNodes {
        public Node n1;
        public Node n2;

        public PairOfNodes( Node n1, Node n2 ) {
            this.n1 = n1;
            this.n2 = n2;
        }
    }

}
