package uk.ac.standrews.cs.digitising_scotland.linkage.MTree;

import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.storr.interfaces.IRepository;

import java.util.ArrayList;
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
    private Node root = null;
    private int num_entries = 0;

    public MTree( Distance<T> d )  {

        distance_wrapper = d;
    }

    /**
     * @return the number of nodes in the tree
     * used to call calculateSize - may be needed in persistent version of this code?
     */
    public int size() {
        return num_entries;
    }

    /**
     * Find the closest N nodes to @param query.
     * @param query - some data for which to find the closest N neighbours
     * @param n the number of neighbours to return
     * @return n neighbours (or as many as possible)
     */
    public List<T> closestN(T query, int n) {
        ClosestSet results = new ClosestSet(n);
        closestN( root,n,query,results);
        return results.values();

    }

    /**
     * Find the nodes withing @param r of @param T.
     * @param query - some data for which to find the neighbours within distance r
     * @param r the distance from query over which to search
     * @return all those nodes within r of @param T.
     */
    public List<T> rangeSearch(T query, float r) {

        ArrayList<T> results = new ArrayList<T>();
        rangeSearch( root,query,r, results );
        return results;
    }


    /**
     * return the nearest neighbour to the query
     * @param query - some data for which to find the nearest neighbour
     * @return the nearest neighbour of T.
     */
    public T nearestNeighbour(T query) {
        return nearestNeighbour( root, null, query );
    }

    /**
     * @param data - some data for which to search
     * @return true if the tree contains the data
     */
    public boolean contains( T data ) {
        return contains( root, root.data );
    }

    /**
     * Add some data to the MTree
     * @param data the data to be added to the tree
     */
    public void add( T data ) throws PreConditionException {
        num_entries++;
        if( root == null ) {
            root = new Node( data, null );
        } else {
            add(root, data);
        }
    }

    /**
     * Debug method primarily but may be useful
     * Displays the tree.
     */
    public void showTree() {
        showTree( root, 0 );
        System.out.println( "----------------------");
    }

    //------------------------- Private methods

    /**
     * Find the number of nodes in a (sub) tree
     * @param node - the (sub) tree whose requested_result_set_size is required
     */
    private int calculateSize(Node node) {
        if( node == null ) {
            return 0;
        } else if( node.isLeaf() ) {
            return 1;
        } else {
            int size = 0;
            for( Node child : node.children) {
                size += calculateSize(child);
            }
            return size;
        }
    }

    /**
     * Displays the (subtree) tree rooted at @param node
     * @param node the node for which the subtree is to displayed
     * @param indent the amount of indent to be used in displaying the (sub) tree
     */
    private void showTree( Node node, int indent ) {

        if( node == null ) {
            print_indent( indent );
            System.out.println ( "null" );
        } else  if( node.children.size() == 0 ) {
            print_indent( indent );
            System.out.println ( node.data + " R: " + node.radius + " d to parent: " + node.distance_to_parent + " isLeaf: " + node.isLeaf() );
        } else {
            print_indent( indent );
            System.out.println ( node.data + " R: " + node.radius + " d to parent: " + node.distance_to_parent + " isLeaf: " + node.isLeaf() + " children: " );
            for( Node child : node.children) {
                showTree(child, indent + 1);
            }
        }
    }

    /**
     * Prints out indent in accordance with the param
     * @param indent the amount of indent to be displayed
     */
    private void print_indent( int indent ) {
        for( int i = 0 ; i < indent; i ++ ) {
            System.out.print( "\t" );
        }
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
    private void rangeSearch(Node N, T Q, float RQ, ArrayList<T> results) {

        Node parent = N.parent;

        if( ! N.isLeaf() ) {
            for( Node child : N.children) {
                float distanceQtoParent = parent == null ? Float.MAX_VALUE : distance_wrapper.distance(parent.data, Q);
                float distanceChildToParent = parent == null ? Float.MAX_VALUE : distance_wrapper.distance(child.data,parent.data);
                if( parent == null || Math.abs( distanceQtoParent - distanceChildToParent ) <= RQ + child.radius ) {
                    float distanceChildToQ = distance_wrapper.distance(child.data, Q);
                    if (distanceChildToQ <= RQ + child.radius) {
                        rangeSearch(child, Q, RQ,results);

                    }
                }

            }
        } else { // node is a leaf

                    float distanceNodeToQ = distance_wrapper.distance(N.data, Q);
                    if (distanceNodeToQ <= RQ) {
                        results.add(N.data);
                    }
         //       }
          //  }
        }
    }

    /**
     * @param query - some data for which to search
     * @return true if the tree contains the data
     */
    private boolean contains( Node node, T query ) {
        if( node.data.equals( query ) ) {
            return true;
        }
        if( node.isLeaf() ) { // is not equal and we are at a leaf;
            return false;
        }
        // node has children and is not equal itself.
        // see if the data is within range of node
        if( Math.abs( distance_wrapper.distance(node.data, query) - node.radius + Float.MIN_VALUE ) < Float.MIN_VALUE ) {
             // search data is outside of range
            return false;
        } else { // the data may be inside this ball
            // need to check children
            for( Node child : node.children ) {
                boolean found = contains( child,query );
                if( found ) {
                    return true;
                }
            }
            return false;
        }
    }


    /**
     * return the nearest neighbour to the query in the subtree rooted at @param node
     * @param node the root of the subtree in which to search
     * @param closest_thus_far - the closest neighbour to query that has been found in the recursive search
     * @param query - some data for which to find the nearest neighbour
     * @return the nearest neighbour of T.
     */
    private T nearestNeighbour(Node node, T closest_thus_far, T query) {
        if( node.data.equals( query ) ) {
            return node.data;
        }
        if( closest_thus_far == null ) {
            closest_thus_far = node.data;
        }
        if( node.isLeaf() ) { // is not equal and we are at a leaf
            if( distance_wrapper.distance(node.data, query) < distance_wrapper.distance(closest_thus_far, query) ) { // this node is closer
                return node.data;
            }
            return closest_thus_far; // we are not any closer.
        }
        // see if we need to check out the children;
        if( distance_wrapper.distance(node.data, query) - node.radius < distance_wrapper.distance(closest_thus_far, query) ) {
            // may be interesting results in the children
            for( Node child : node.children ) {
                T nn = nearestNeighbour( child,closest_thus_far,query );
                if( distance_wrapper.distance(nn, query)  < distance_wrapper.distance( closest_thus_far, query ) ) {
                    closest_thus_far = nn;
                }
            }
        }
        return closest_thus_far;
    }


    /**
     * Find the closest N nodes to @param query.
     * @param node the root of the tree in which the search is being carried out
     * @param query - some data for which to find the closest N neighbours
     * @param results the nearest nodes found thus far
     */
    private void closestN(Node node, int n, T query, ClosestSet results) {

        if( node.isLeaf() ) { // we are at a leaf - see if ths is closer than other nodes in results
            float node_distance = distance_wrapper.distance(node.data, query);
            if( results.size() < n ) { // fill up the list without checking until at capacity
                results.add_in_distance_order(node.data, node_distance);
            } else {
                if (node_distance < results.furthest_distance()) { // this node is closer
                    results.add_in_distance_order(node.data, node_distance);
                }
            }
            return; // leaves have no children so give up here.
        }
        // see if we need to check out the children;
        float node_distance = distance_wrapper.distance(node.data, query);
        if( results.size() == 0 || node_distance - node.radius < results.furthest_distance() ) {
            // may be nodes in tree closer than those in results
            for( Node child : node.children ) {

                closestN( child, n, query, results ); // have a look at the children
            }
        }
    }


    /**
     * Insert some data into a leaf of the Tree.
     *
     * @param node - the parent of the current node.
     * @param data  - the data to add into the children
     */
    private void leaf_insert( Node node, T data ) throws PreConditionException {

        if (( node.isFull() )) {
            split(node, data);
        } else {
            if( node.isEmpty() ) {
                node.addChild(new Node(node.data, node)); // we making a leaf into an intermediate node - add Node to its own children
            }
            node.addChild(new Node(data, node)); // children is not yet full - put the data into the children
        }
    }

    /**
     * Find the most appropriate node in which data should be inserted.
     * Choose child such that: No enlargement of radius is needed,
     * In case of ties, choose the closest one to the new node.
     * @param node - the node into which we are inserting the data
     * @param data the new data.
     * @return the radius of the child with the added node.
     */
    private void add(Node node, T data) throws PreConditionException {
        Node enclosing_pivot = null;
        Node closest_pivot = null;
        float smallest_distance = -1.0F; // illegal distance

        // find the most appropriate child.
        for( Node child : node.children ) {
            float new_distance = distance_wrapper.distance(child.data, data);

            if( new_distance < child.radius ) { // we are inside the radius of the current existing pivot - new node falls in within this ball

                if( new_distance < smallest_distance || smallest_distance == -1.0F ) { // we are closer to this pivot than any previous pivots

                    enclosing_pivot = child;
                    smallest_distance = new_distance;
                }

            } else if( enclosing_pivot == null ) { // not found any pivot within whose radius the new data falls

                if( closest_pivot == null ) {
                    // no candidates yet so make this one the closest
                    smallest_distance = new_distance;
                    closest_pivot = child;
                } else if( new_distance < smallest_distance ) { // this pivot is closer.
                    smallest_distance = new_distance;
                    closest_pivot = child;
                }
            }
        }

        if( enclosing_pivot == null ) { // didn't find an enclosing pivot - put it in the closest.

            if (closest_pivot == null || distance_wrapper.distance(node.data, data) <= smallest_distance) { // this node is closer to the new data
                leaf_insert(node, data);
            } else { // one of the children are closer
                float old_radius = closest_pivot.radius;
                add(closest_pivot, data);
                // now check to see if the radius has changed
                float new_radius = closest_pivot.radius;
                if( new_radius > old_radius ) { // the radius has grown
                    float new_distance_to_pivot = distance_wrapper.distance(node.data, closest_pivot.data);
                    // now check of the parent radius needs to be adjusted too.
                    if(  new_distance_to_pivot + new_radius > node.radius ) {
                        node.radius = new_distance_to_pivot + new_radius;
                    }
                }
            }
        } else { // we found an enclosing pivot - no need to make the radius bigger
             add(enclosing_pivot, data);
        }
    }


    /**
     * Helper method for splitting levels (children) of node in the tree
     * @param N the node which is being split
     * @param oN a new node being added
     * @throws PreConditionException
     */
    private void split(Node N, T oN) throws PreConditionException {
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

        List<Node> S = N.children;
        N.children = new ArrayList<Node>(); // get rid of existing children of the node

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
                if( Np.isFull() ) {
                    split( Np, p2.data);
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

    /**
     * This is the class used to build the M-tree.
     */
    public class Node {

        public T data;
        public float radius;
        public float distance_to_parent;
        public Node parent;      /// NOT SURE IF YOU NEED
        public ArrayList<Node> children;

        public Node(T oN, Node parent ) {
            data = oN;
            radius = 0.0F;
            distance_to_parent = parent == null ? 0 : distance_wrapper.distance( oN, parent.data);
            children = new ArrayList<Node>();
            this.parent = parent;
            //this.distance = distance;
        }

        /**
         * Adds a child to teh current node
         * Pre condition this is only called where a split has already occurred and parent is not full
         * @param newNode the node to add to the Node
         */
        protected float addChild(Node newNode) throws PreConditionException {

            if( isFull() ) {
                throw new PreConditionException( "Level is full" );
            }
            children.add( newNode );
            float newdistance = distance_wrapper.distance(this.data, newNode.data);
            newNode.distance_to_parent = newdistance;
            float new_radii = newdistance + newNode.radius;
            if( new_radii > radius ) {
                radius = new_radii;
            }
            return radius;
        }

        public boolean isLeaf() { return this.radius == 0.0F; }

        public boolean isFull() {
            return children.size() >= MTree.MAX_LEVEL_SIZE;
        }

        public boolean isEmpty() {
            return children.isEmpty();
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

    public class ClosestSet {

        ArrayList<DataDistance> closest;
        int requested_result_set_size;

        public ClosestSet(int n) {
            closest = new ArrayList<DataDistance>();
            requested_result_set_size = n;
        }

        public int size() { return closest.size(); }

        private void add_in_distance_order(T data, float distance) {

            System.out.println( "adding: " + data + " at distance: " + distance +  " to: " );
            System.out.println( "\t" + this.toString() );

            int index;
            if( closest.size() == 0 ) {
                closest.add( 0,new DataDistance( data, distance ) );
                return;
            }
            for( index = 0; index < closest.size();  index++ ) {
                DataDistance next = closest.get(index);
                if (distance <= next.dist) { // found right point to insert
                    closest.add( index,new DataDistance( data, distance ) );
                    check_evict();
                    return;
                }
            }
            closest.add( index,new DataDistance( data, distance ) ); // add at the end
            check_evict();
            // if we get here then the new element is further than rest so should not be added.
        }

        public float furthest_distance() {
            DataDistance furthest_element = closest.get(closest.size() - 1);
            return furthest_element.dist;
        }

        private void check_evict() {
            if (closest.size() > requested_result_set_size) {
                closest.remove(closest.size() - 1); // remove the last
            }
        }

        public String toString() {
            if( closest.size() == 0 ) {
                return "[]";
            }
            StringBuffer sb = new StringBuffer();
            sb.append( "[" );
            for( int i = 0; i < closest.size();  i++ ) {
                sb.append("\tdata: " + closest.get(i).val + "distance: " + closest.get(i).dist + "\n");
            }
            sb.append( "\t]" );
            return sb.toString();
        }

        public List<T> values() {
            ArrayList<T> result = new ArrayList<T>();
            for( DataDistance dd : closest ) {
                result.add( dd.val );
            }
            return result;
        }

        public class DataDistance {

            public T val;
            public float dist;

            public DataDistance( T data, float distance ) {
                this.val = data;
                this.dist = distance;
            }
        }
    }


}
