package uk.ac.standrews.cs.digitising_scotland.linkage.MTree;

import org.junit.Before;
import org.junit.Test;

import java.util.List;


/**
 * Created by al on 27/01/2017.
 */
public class MTreeTest {

    MTree t;
    private EuclidianDistance ed;

    @Before
    public void setUp() throws Exception {

        ed = new EuclidianDistance();
        t = new MTree( ed );
    }

    /**
     * add a single point to the tree
     */
    @Test
    public void add_one() throws PreConditionException {
        t.add( new Point( 0.0F, 0.0F ) );
        assert( t.size() == 1 );
        assert( t.contains( new Point( 0.0F, 0.0F ) ) );
    }

    /**
     * add 3 points to the tree - 3,4,5 triangle
     */
    @Test
    public void add_three_345() throws PreConditionException {

            t.add( new Point( 0.0F, 0.0F ) );
            t.add( new Point( 3.0F, 0.0F ) );
            t.add( new Point( 3.0F, 4.0F ) );
            assert( t.size() == 3 );
            assert( t.contains( new Point( 0.0F, 0.0F ) ) );
            assert( t.contains( new Point( 3.0F, 0.0F ) ) );
            assert( t.contains( new Point( 3.0F, 4.0F ) ) );
        // t.showTree();
    }

    /**
     * add 15 points to the tree
     */
    @Test
    public void add_linear_15() throws PreConditionException {

        for( int i = 0; i< 15; i++ ) {
            t.add( new Point( (float) i, 0.0F ) );
        }
        assert( t.size() == 15 );
        for( int i = 0; i< 15; i++ ) {
            t.contains( new Point((float) i, 0.0F) ) ;
        }
    }

    /**
     * add points to the tree
     * such that some will nest
     */
    @Test
    public void add_nested_points_depth_3() throws PreConditionException {
        // lay down 20 points in a line
        for( int i = 0; i< 3; i++ ) {
            t.add( new Point( (float) i * 10, 0.0F ) );
        }
        // new lay down 20 points in a line - that are all close (4 away) to the first 20
        for( int i = 0; i< 3; i++ ) {
            t.add( new Point( (float) (i * 10) + 4.0F, 0.0F ) );
        }
        // new lay down another 20 points in a line - that are all close (1 away) to the second 20
        for( int i = 0; i< 3; i++ ) {
            t.add( new Point( (float) (i * 10) + 4.5F, 0.0F ) );
        }

        assert( t.size() == 9 );
        for( int i = 0; i< 3; i++ ) {
            assert( t.contains( new Point( (float) i * 10, 0.0F ) ) );
        }
        // new lay down 20 points in a line - that are all close (4 away) to the first 20
        for( int i = 0; i< 3; i++ ) {
            assert( t.contains( new Point( (float) (i * 10) + 4.0F, 0.0F ) ) );
        }
        // new lay down another 20 points in a line - that are all close (1 away) to the second 20
        for( int i = 0; i< 3; i++ ) {
            assert( t.contains( new Point( (float) (i * 10) + 4.5F, 0.0F ) ) );
        }

        // t.showTree();
    }

    /**
     * add points to the tree
     * such that some will nest
     */
    private int add_squares() throws PreConditionException {

        int count = 0;

        for (float step = 1.0F; step < 50.0F; step++) {

            t.add(new Point(+step, +step));
            count++;
            t.add(new Point(-step, +step));
            count++;
            t.add(new Point(+step, -step));
            count++;
            t.add(new Point(-step, -step));
            count++;
        }

        return count;
    }

    /**
     * test add points to the tree
     * such that some will nest
     */
    @Test
    public void test_squares() throws PreConditionException {

        int count = add_squares();

        assert( t.size() == count );
        for( float step = 1.0F; step < 50.0F; step++ ) {

            assert( t.contains(new Point( + step,  + step)) );
            assert( t.contains(new Point( - step,  + step)) );
            assert( t.contains(new Point( + step,  - step)) );
            assert( t.contains(new Point( - step,  - step)) );
        }
    }

    /**
     * add points to the tree
     * such that some will nest
     */
    private void add_nested_points_60() throws PreConditionException {
        // lay down 20 points in a line
        for (int i = 0; i < 20; i++) {
            t.add(new Point((float) i * 10, 0.0F));
            //t.showTree();
        }
        // new lay down 20 points in a line - that are all close (4 away) to the first 20
        for (int i = 0; i < 20; i++) {
            t.add(new Point((float) (i * 10) + 4.0F, 0.0F));
            //t.showTree();
        }
        // new lay down another 20 points in a line - that are all close (1 away) to the second 20
        for (int i = 0; i < 20; i++) {
            t.add(new Point((float) (i * 10) + 4.5F, 0.0F));
            //t.showTree();
        }
    }

    /**
     * test points added to the tree
     * such that some will nest
     */
    @Test
    public void test_nested_points_60() throws PreConditionException {

        add_nested_points_60();

        assert( t.size() == 60 );

        for( int i = 0; i< 20; i++ ) {
            assert( t.contains( new Point( (float) i * 10, 0.0F ) ) );
        }
        // new lay down 20 points in a line - that are all close (4 away) to the first 20
        for( int i = 0; i< 20; i++ ) {
            assert( t.contains( new Point( (float) (i * 10) + 4.0F, 0.0F ) ) );
        }
        // new lay down another 20 points in a line - that are all close (1 away) to the second 20
        for( int i = 0; i< 20; i++ ) {
            assert( t.contains( new Point( (float) (i * 10) + 4.5F, 0.0F ) ) );
        }

        // t.showTree();
    }

    @Test
    public void findClosetFrom60() throws PreConditionException {
        add_nested_points_60();
        Point p = new Point(15.0F, 0.0F);
        List<Point> result = t.rangeSearch(p, 10.0F);
        assert( result.size() == 6 );
        for( Point pp : result ) {
            assert( t.contains( pp ) );                 // point added to the tree
            assert( ed.distance( pp, p ) <= 10.0F );    // and it is in range.
        }
    }


    @Test
    public void findClosest_from_squares() throws PreConditionException {
        int count = add_squares();

        Point p = new Point(0.0F, 0.0F);

        // test search in ever increasing circles.
        for( float i = 1.0F; i < 50.0F; i++ ) {
            float search_circle = (float) Math.sqrt( i * i ) ; // size of square plus a little to avoid float errors
            List<Point> result = t.rangeSearch(p,search_circle);
            // System.out.println( "d=" + search_circle + " results size =" + result.size() + " results: " + result );
            for( Point pp : result ) {
                assert( t.contains( pp ) );
                assert( ed.distance( pp, p ) <= search_circle );    // and it is in range.
            }
        }
    }



    public class Point {

        public float x;
        public float y;

        public Point( float x, float y ) {
            this.x = x;
            this.y = y;
        }

        private final static float epsilon = 0.00000000001F;

        public String toString() { return "[" + x + "," + y + "]"; }

        public boolean equals( Object o ) {
            if( o instanceof Point ) {
                Point p = (Point) o;
                return this.x == p.x && this.y == p.y;
                // return (Math.abs(this.x - p.x) < epsilon) && (Math.abs(this.y - p.y) < epsilon);
            } else return false;
        }
    }

    public class EuclidianDistance implements Distance<Point> {

        public float distance(Point p1, Point p2) {
            float xdistance = p1.x - p2.x;
            float ydistance = p1.y - p2.y;

            return (float) Math.sqrt( ( xdistance * xdistance ) + ( ydistance * ydistance ) );
        }

    }
}