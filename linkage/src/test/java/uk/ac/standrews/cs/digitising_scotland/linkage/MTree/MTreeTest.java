package uk.ac.standrews.cs.digitising_scotland.linkage.MTree;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;


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
    public void add_one() {
        try {
            t.add( new Point( 0.0F, 0.0F ) );
        } catch (PreConditionException e) {
            fail( "precondition failure" );
        }
    }

    /**
     * add 2 points to the tree
     */
    @Test
    public void add_two() {
        try {

            t.add( new Point( 0.0F, 0.0F ) );
            t.add( new Point( 1.0F, 1.0F ) );

        } catch (PreConditionException e) {
            fail( "precondition failure" );
        }
    }


    /**
     * add 3 points to the tree - 3,4,5 triangle
     */
    @Test
    public void add_three() {
        try {

            t.add( new Point( 0.0F, 0.0F ) );
            t.add( new Point( 3.0F, 0.0F ) );
            t.add( new Point( 3.0F, 4.0F ) );

        } catch (PreConditionException e) {
            fail( "precondition failure" );
        }
    }

    /**
     * add 10 points to the tree
     */
    @Test
    public void add_ten() {
        try {
            for( int i = 0; i< 3; i++ ) {
                t.add( new Point( (float) i, 0.0F ) );
            }
        } catch (PreConditionException e) {
            fail( "precondition failure" );
        }
    }

    /**
     * add 25 points to the tree
     * 20 is level size.
     */
    @Test
    public void add_25() {
        try {
            for( int i = 0; i< 25; i++ ) {
                t.add( new Point( (float) i, 0.0F ) );
            }
        } catch (PreConditionException e) {
            fail( "precondition failure" );
        }
    }


    public class Point {

        public float x;
        public float y;

        public Point( float x, float y ) {
            this.x = x;
            this.y = y;
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