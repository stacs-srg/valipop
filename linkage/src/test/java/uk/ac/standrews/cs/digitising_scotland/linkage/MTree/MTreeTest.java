package uk.ac.standrews.cs.digitising_scotland.linkage.MTree;

import org.junit.Before;
import org.junit.Test;


/**
 * Created by al on 27/01/2017.
 */
public class MTreeTest {

    MTree t;

    @Before
    public void setUp() throws Exception {

        t = new MTree( new EuclidianDistance() );
    }

    /**
     * add a single point to the tree
     */
    @Test
    public void add_one() throws PreConditionException {
            t.add( new Point( 0.0F, 0.0F ) );
    }

    /**
     * add 3 points to the tree - 3,4,5 triangle
     */
    @Test
    public void add_three_345() throws PreConditionException {

            t.add( new Point( 0.0F, 0.0F ) );
            t.add( new Point( 3.0F, 0.0F ) );
            t.add( new Point( 3.0F, 4.0F ) );
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
       //  t.showTree();
    }

    /**
     * add points to the tree
     * such that some will nest
     */
    @Test
    public void add_nested_points_3() throws PreConditionException {
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
        // This should create some nested radii.

        // t.showTree();
    }

    /**
     * add points to the tree
     * such that some will nest
     */
    @Test
    public void add_nested_points_60() throws PreConditionException {
            // lay down 20 points in a line
            for( int i = 0; i< 0; i++ ) {
                t.add( new Point( (float) i * 10, 0.0F ) );
                t.showTree();
            }
            // new lay down 20 points in a line - that are all close (4 away) to the first 20
            for( int i = 0; i< 20; i++ ) {
                t.add( new Point( (float) (i * 10) + 4.0F, 0.0F ) );
                t.showTree();
            }
            // new lay down another 20 points in a line - that are all close (1 away) to the second 20
            for( int i = 0; i< 20; i++ ) {
                t.add( new Point( (float) (i * 10) + 4.5F, 0.0F ) );
                t.showTree();
            }
            // This should create some nested radii.

            // t.showTree();
    }

    /**
     * add points to the tree
     * such that some will nest
     */
    @Test
    public void add_squares() throws PreConditionException {
        float x_start = 0.0F;
        float y_start = 0.0F;
        t.add( new Point( x_start, y_start ) );

        for( float step = 1.0F; step < 10.0F; step++ ) {

            t.add(new Point(y_start + step, x_start + step));
            t.add(new Point(y_start - step, x_start + step));
            t.add(new Point(y_start + step, x_start - step));
            t.add(new Point(y_start - step, x_start - step));
        }

        t.showTree();
    }


        @Test
    public void find_close() throws PreConditionException {
        add_nested_points_60();
        Point p = new Point(2.0F, 0.0F);
        System.out.println(  t.rangeSearch( p, 5.0F ) );
    }



    public class Point {

        public float x;
        public float y;

        public Point( float x, float y ) {
            this.x = x;
            this.y = y;
        }

        public String toString() { return "[" + x + "," + y + "]"; }
    }

    public class EuclidianDistance implements Distance<Point> {

        public float distance(Point p1, Point p2) {
            float xdistance = p1.x - p2.x;
            float ydistance = p1.y - p2.y;

            return (float) Math.sqrt( ( xdistance * xdistance ) + ( ydistance * ydistance ) );
        }

    }





}