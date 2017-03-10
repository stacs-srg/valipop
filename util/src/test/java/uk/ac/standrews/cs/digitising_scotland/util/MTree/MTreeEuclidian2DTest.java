/*
 * Copyright 2015 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module util.
 *
 * util is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * util is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with util. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.util.MTree;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Created by al on 27/01/2017.
 */
public class MTreeEuclidian2DTest {

    MTree<Point> t;
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
    public void add_one() {
        t.add( new Point( 0.0F, 0.0F ) );
        assertTrue( t.size() == 1 );
        assertTrue( t.contains( new Point( 0.0F, 0.0F ) ) );
    }

    /**
     * add 3 points to the tree - 3,4,5 triangle
     */
    @Test
    public void add_three_345() {

        t.add( new Point( 0.0F, 0.0F ) );
        t.add( new Point( 3.0F, 0.0F ) );
        t.add( new Point( 3.0F, 4.0F ) );
        assertTrue( t.size() == 3 );
        assertTrue( t.contains( new Point( 0.0F, 0.0F ) ) );
        assertTrue( t.contains( new Point( 3.0F, 0.0F ) ) );
        assertTrue( t.contains( new Point( 3.0F, 4.0F ) ) );
        // t.showTree();
    }

    /**
     * add 15 points to the tree
     */
    @Test
    public void add_linear_15() {

        for( int i = 0; i< 15; i++ ) {
            t.add( new Point( (float) i, 0.0F ) );
        }
        assertTrue( t.size() == 15 );
        for( int i = 0; i< 15; i++ ) {
            t.contains( new Point((float) i, 0.0F) ) ;
        }
    }

    /**
     * add points to the tree
     * such that some will nest
     */
    @Test
    public void add_nested_points_depth_3() {
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

        assertTrue( t.size() == 9 );
        for( int i = 0; i< 3; i++ ) {
            assertTrue( t.contains( new Point( (float) i * 10, 0.0F ) ) );
        }
        // new lay down 20 points in a line - that are all close (4 away) to the first 20
        for( int i = 0; i< 3; i++ ) {
            assertTrue( t.contains( new Point( (float) (i * 10) + 4.0F, 0.0F ) ) );
        }
        // new lay down another 20 points in a line - that are all close (1 away) to the second 20
        for( int i = 0; i< 3; i++ ) {
            assertTrue( t.contains( new Point( (float) (i * 10) + 4.5F, 0.0F ) ) );
        }

        // t.showTree();
    }

    /**
     * add points to the tree
     * such that some will nest
     */
    private int add_squares() {

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
    public void test_squares() {

        int count = add_squares();

        assertTrue( t.size() == count );
        for( float step = 1.0F; step < 50.0F; step++ ) {

            assertTrue( t.contains(new Point( + step,  + step)) );
            assertTrue( t.contains(new Point( - step,  + step)) );
            assertTrue( t.contains(new Point( + step,  - step)) );
            assertTrue( t.contains(new Point( - step,  - step)) );
        }
    }

    /**
     * add points to the tree
     * such that some will nest
     */
    private void add_nested_points_60() {
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
    public void test_nested_points_60() {

        add_nested_points_60();

        assertTrue( t.size() == 60 );

        for( int i = 0; i< 20; i++ ) {
            assertTrue( t.contains( new Point( (float) i * 10, 0.0F ) ) );
        }
        // new lay down 20 points in a line - that are all close (4 away) to the first 20
        for( int i = 0; i< 20; i++ ) {
            assertTrue( t.contains( new Point( (float) (i * 10) + 4.0F, 0.0F ) ) );
        }
        // new lay down another 20 points in a line - that are all close (1 away) to the second 20
        for( int i = 0; i< 20; i++ ) {
            assertTrue( t.contains( new Point( (float) (i * 10) + 4.5F, 0.0F ) ) );
        }

        // t.showTree();
    }

    /**
     * test rangeSearch - performing range search on nested nodes - simple version.
     */
    @Test
    public void findClosetFrom60() {
        add_nested_points_60();
        Point p = new Point(15.0F, 0.0F);
        List<DataDistance<Point>> result = t.rangeSearch(p, 10.0F);
        List<Point> values = t.mapValues(result);
        assertTrue( result.size() == 6 );
        for( Point pp : values ) {
            assertTrue( t.contains( pp ) );                 // point added to the tree
            assertTrue( ed.distance( pp, p ) <= 10.0F );    // and it is in range.
        }
    }

    /**
     * test rangeSearch - finding nested nodes in nested squares - more complex version.
     */
    @Test
    public void findClosest_from_squares() {
        int count = add_squares();

        Point p = new Point(0.0F, 0.0F);

        // test search in ever increasing circles.
        for( float i = 1.0F; i < 50.0F; i++ ) {
            float search_circle = (float) Math.sqrt( i * i ) ; // requested_result_set_size of square plus a little to avoid float errors
            List<DataDistance<Point>> result = t.rangeSearch(p, search_circle);
            List<Point> values = t.mapValues(result);
            // System.out.println( "d=" + search_circle + " results requested_result_set_size =" + result.requested_result_set_size() + " results: " + result );
            for( Point pp : values ) {
                assertTrue( t.contains( pp ) );
                assertTrue( ed.distance( pp, p ) <= search_circle );    // and it is in range.
            }
        }
    }

    /**
     * test simple nearest neighbour search
     */
    @Test
    public void findClosest() {
        int count = add_squares();
        Point p = new Point(20.6F, 20.6F);
        DataDistance<Point> result = t.nearestNeighbour(p);
        //System.out.println(result);
        assertEquals( result.value, new Point(21.0F, 21.0F) ); // closest point to 20.6,20.6 - TODO better tests?
    }

    /**
     * test simple nearest neighbour search
     */
    @Test
    public void findClosestN() {
        int count = add_squares();
        Point p = new Point(0.0F, 0.0F);
        for( int i = 4; i < 50; i+=4 ) {
            // move out in squares of size 4, each loop should include 4 more nodes
            List<DataDistance<Point>> result = t.nearestN(p, i);
            List<Point> values = t.mapValues(result);
            assertTrue( result.size() == i );
            for( Point pp : values ) {
                assertTrue( t.contains( pp ) );   // TODO How to check that they are the right ones????
            }
            //System.out.println(result);
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