package uk.ac.standrews.cs.digitising_scotland.linkage.MTree;

import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.storr.interfaces.IRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * Created by al@st-andrews.ac.uk on 13/01/2017.
 * Code to implement an M-Tree.
 * Code based on ACM SAC Tutorial, March 2007 by Zezula, Amato, Dohnal - Similarity Search: The Metric Space Approach pp 129-
 * URL for tutorial: http://www.nmis.isti.cnr.it/amato/similarity-search-book/SAC-07-tutorial.pdf
 *
 */
public class MTree<T> {

    public static final int MAX_LEVEL_SIZE = 20;
    private final Distance<T> distance_wrapper;

    private IRepository repo;
    private Level root;

    public MTree( Distance<T> d )  {

        root = new Level();
        distance_wrapper = d;
    }

    /**
     * Find the closest N nodes to @param query.
     * @param query - some data for which to find the closest N neighbours
     * @param n the number of neighbours to return
     * @return n neighbours (or as many as possible)
     */
    public Iterator<T> closestN(T query, int n) {
        // TODO
        return null;
    }

    /**
     * Find the nodes withing @param r of @param T.
     * @param query - some data for which to find the neighbours within distance r
     * @param r the distance from query over which to search
     * @return all those nodes within r of @param T.
     */
    public List<T> rangeSearch(T query, float r) {

        return rootRangeSearch(query, r);

    }


    /**
     *
     * @param query - some data for which to find the nearest neighbour
     * @return the nearest neighbour of T.
     */
    public T nearestNeighbour(T query) {
        Iterator<T> it = closestN(query, 1);
        return it.next();
    }

    /**
     * Add some data to the MTree
     * @param data
     */
    public void add( T data ) throws PreConditionException {
        insert( root, null, rootisleaf(), data );
    }

    public void showTree() {
        showTree( root, 0 );
    }

    //------------------------- Private methods

    private void showTree( Level current_lvl, int indent ) {
        for( Node child : current_lvl.children ) {
            for( int i = 0 ; i < indent; i ++ ) {
                System.out.print( "\t" );
            }
            if( child.level.children.size() == 0 ) {
                System.out.println ( child.data + " R: " + child.radius + " d to parent: " + child.distance_to_parent + " isLeaf: " + child.isLeaf() );
            } else {
                System.out.println ( child.data + " R: " + child.radius + " d to parent: " + child.distance_to_parent + " isLeaf: " + child.isLeaf() + " children: " );
                showTree( child.level, indent + 1 );
            }
        }
    }

    private List<T> rootRangeSearch(T query, float r) {
        ArrayList<T> results = new ArrayList<T>();

        for( Node n : root.children ) {
            results.addAll( rangeSearch( n,query,r ) );
        }
        return results;
    }

    /**
     * Find the nodes withing @param RQ of @param T.
     * @param N - the node we are searching
     * @param Q the query data
     * @param RQ the search radius
     * @return all those nodes within RQ of @param T.
     *
     * Algorithm RangeSearch from https://en.wikipedia.org/wiki/M-tree
     * Input: Node N of M-Tree MT,  Q: query object, R(Q): search radius
     * Output: all the DB objects such that
     * d(O,j,Q) ≤ RQ(Q)
     *
     * {
     *   let Op be the parent object of node N;
     *
     *   if N is not a leaf then {
     *     for each entry(Or) in N do {
     *           if |d(Op,Q) − d(Or,Op)| ≤ R(Q)+R(Or) then {
     *             Compute d(Or,Q);
     * 			if d(Or,Q) ≤ RQ(Q)+R(Or)} then
     *               RangeSearch(*ptr(T(Or)),Q,R(Q));
     *           }
     *     }
     *   }
     *   else { // it is leaf
     *     for each entry(Oj) in N do {
     *           if |d(Op,Q) - d(Oj,Op)| ≤ R(Q) then {
     *             Compute d(Oj,Q);
     *             if d(Oj,Q) ≤ R(Q) then
     *               add oid(Oj) to the result;
     *           }
     *     }
     *   }
     * }
     */
    private ArrayList<T> rangeSearch(Node N, T Q, float RQ) {

        ArrayList<T> results = new ArrayList<T>();

        Node parent = N.parent;

        if( ! N.isLeaf() ) {
            for( Node child : N.level.children ) {
                float distanceQtoParent = parent == null ? Float.MAX_VALUE : distance_wrapper.distance(parent.data, Q);
                float distanceChildToParent = parent == null ? Float.MAX_VALUE : distance_wrapper.distance(child.data,parent.data);
                if( parent == null || Math.abs( distanceQtoParent - distanceChildToParent ) <= RQ + child.radius ) {
                    float distanceChildToQ = distance_wrapper.distance(child.data, Q);
                    if (distanceChildToQ <= RQ + child.radius) {
                        results.addAll(rangeSearch(child, Q, RQ));

                    }
                }

            }
        } else { // node is a leaf

            // LEAVES DO NOT HAVE CHILDREN!!!!!

            for( Node child : N.level.children ) {
               // float distanceQtoParent = parent == null ? Float.MAX_VALUE : distance_wrapper.distance(parent.data, Q);
               // float distanceChildToParent = parent == null ? Float.MAX_VALUE : distance_wrapper.distance(child.data,parent.data);
              //  if( parent == null || Math.abs( distanceQtoParent - distanceChildToParent ) <= RQ ) {
                    float distanceChildToQ = distance_wrapper.distance(child.data, Q);
                    if (distanceChildToQ <= RQ) {
                        results.add(child.data);
              //      }
                }
            }
        }
        return results;
    }

    private boolean rootisleaf() { return  ! root.isFull(); }

    /**
     * Insert some data into some level of the Tree.
     *
     * @param current_lvl - the Level of the MTree into which new data should be inserted.
     * @param current_node - the parent of the current node.
     * @param isLeaf whether we are at a leaf node or not.
     * @param data  - the data to insert into the level
     */
    private void insert( Level current_lvl, Node current_node, boolean isLeaf, T data ) throws PreConditionException {
        if( isLeaf ) {
            if (current_lvl.isFull()) {
                split(current_lvl, current_node, data);
            } else {
                current_lvl.add(new Node(data, current_node)); // level is not yet full - put the data into the level
                if( current_node != null ) { //**************** TODO Take if this out if we ever make root a node rather than a level.
                    float new_distance = distance_wrapper.distance(data, current_node.data);
                    if (new_distance > current_node.radius) {
                        current_node.radius = new_distance;
                    }
                }
            }
        } else {
            insert_into_most_appropriate( current_lvl, data );
        }
    }

    /**
     * Find the most appropriate node in which data should be inserted.
     * Choose child such that: No enlargement of radius is needed,
     * In case of ties, choose the closest one to the new node.
     * @param current_lvl - the level in which we are inserting the data into
     * @param data the new data.
     */
    private void insert_into_most_appropriate(Level current_lvl, T data) throws PreConditionException {
        Node enclosing_pivot = null;
        Node closest_pivot = null;
        float smallest_distance = -1.0F; // illegal
        List<Node> children = current_lvl.children;
        // find the most appropriate child.
        for( Node existing_pivot : children ) {
            float new_distance = distance_wrapper.distance(existing_pivot.data, data);

            if( new_distance < existing_pivot.radius ) { // we are inside the radius of the current existing pivot - new node falls in within this ball

                if( new_distance <  smallest_distance || smallest_distance == -1.0F ) { // we are closer to this pivot than any previous pivots

                    enclosing_pivot = existing_pivot;
                    smallest_distance = new_distance;
                }

            } else if( enclosing_pivot == null ) { // not found any pivot within whose radius the new data falls

                if( closest_pivot == null ) {
                    // no candidates yet so make this one the closest
                    smallest_distance = new_distance;
                    closest_pivot = existing_pivot;
                } else if( new_distance < smallest_distance ) { // this pivot is closer.
                    smallest_distance = new_distance;
                    closest_pivot = existing_pivot;
                }
            }
        }

        if( enclosing_pivot == null ) { // didn't find an enclosing pivot

            insert(closest_pivot.level, closest_pivot, closest_pivot.isLeaf(), data); // do the insertion of the new data

            // add the closet pivot to its own children - this code is from insert but types are all wrong!
            if (closest_pivot.level.isFull()) {
                split(closest_pivot.level, closest_pivot, closest_pivot.data );
            } //else {
            //    closest_pivot.level.add(new Node(closest_pivot.data, closest_pivot)); // level is not yet full - put the data into the level
            //}

            closest_pivot.radius = smallest_distance; // make the radius include the new (closest) node - order of assignment is critical - it is a leaf node before assignment

        } else { // we found an enclosing pivot - no need to make the radius bigger
            insert(enclosing_pivot.level, enclosing_pivot, enclosing_pivot.isLeaf(), data); // do the insertion
        }
    }


    private void split(Level current_lvl, Node N, T oN) throws PreConditionException {
        //    Split(N,oN): N is leaf oN is new
        //      Let S be the set containing all entries of N and oN
        //      Select pivots p1 and p2 from S
        //      Partition S to S1 and S2 according to p1 and p2
        //      Store S1 in N and S2 in a new allocated node N’
        //      If N isRoot
        //          Allocate a new root and store entries for p1, p2 there
        //      else
        //          (let Np and pp be the parent node and parent data of N)   Replace entry pp with p1
        //          If Np is full, then Split(Np,p2)
        //              else store p2 in node Np

        List<Node> S = current_lvl.children;
        current_lvl.children = new ArrayList<Node>(); // get rid of existing children of the node

        S.add(new Node(oN, N));  // add oN into children - now over full
        // but we are about to perform a split - makes computation easier.
        // Select two new data from the children (with data added).

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
        Node Nprime = new Node(p2.data, N.parent );
        for( Node n : s2 ) {
            Nprime.addChild( n );
        }

        // at this point...
        //p1 and p2 are still floating, children stored in partitions p1 and p2 are all allocated.
        // they both need to be put somewhere and the tree fixed up.

            if (! N.isLeaf()) {
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

                // let Np and pp be the parent node and parent data of N
                //  Replace entry pp with p1
                //  If Np is full, then Split(Np,p2) else store p2 in node Np

                Node Np = N.parent;
                T pp = Np.data;
                Np.data = p1.data;
                if( Np.level.isFull() ) {
                    split( Np.level, Np, p2.data);
                } else {
                    Np.addChild(p2);
                }

            }

        }

    /**
     * Takes two pivots and a list of Nodes and re-partitions into two Lists of nodes
     * Partitioning based on proximity - those nodes closest to p1 go into the  first partition
     * those nodes closest to p2 go intio the second.
     * @param p1 the first data
     * @param p2 the second data
     * @param s a list of nodes to partition.
     *
     * @return an array of two partitions of nodes
     */
    private List<Node>[] partitionChildrenIntoPivots(Node p1, Node p2, List<Node> s) {

        List<Node> partition1 = new ArrayList<Node>();
        List<Node> partition2 = new ArrayList<Node>();

        for( Node child : s ) {
            float r1 = distance_wrapper.distance(p1.data, child.data);
            float r2 = distance_wrapper.distance(p1.data, child.data);

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
     * @param s - a list of nodes from which to choose a data
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

    //----------------------- Helper classes

    public class Level {

        public List<Node> children;

        public Level() {
            children = new ArrayList<Node>();
        }

        public boolean isFull() {
            return children.size() >= MTree.MAX_LEVEL_SIZE;
        }

        public boolean isEmpty() {
            return children.isEmpty();
        }

        public void add(Node newNode) throws PreConditionException {
            if( isFull() ) {
                throw new PreConditionException( "Level is full" );
            }
            children.add( newNode );
        }
    }

    public class Node {

        public T data;
        public float radius;
        public float distance_to_parent;
        public Node parent;      /// NOT SURE IF YOU NEED
        public Level level;

        public Node(T oN, Node parent ) { //, Distance<D> distance) {
            data = oN;
            radius = 0.0F;
            distance_to_parent = parent == null ? 0 : distance_wrapper.distance( oN, parent.data);
            level = new Level();
            this.parent = parent;
            //this.distance = distance;
        }

        /**
         *
         * Pre condition this is only called where a split has already occurred and parent is not full
         * @param newNode the node to add to the Node
         */
        protected void addChild(Node newNode) throws PreConditionException {

            level.add( newNode );
            float newdistance = distance_wrapper.distance(this.data, newNode.data);
            if( newdistance > radius ) {
                radius = newdistance;
            }
        }

        public boolean isLeaf() { return this.radius == 0.0F; }

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
