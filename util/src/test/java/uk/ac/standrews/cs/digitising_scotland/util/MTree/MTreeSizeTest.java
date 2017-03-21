package uk.ac.standrews.cs.digitising_scotland.util.MTree;

import org.junit.Ignore;

import java.util.Random;

/**
 * Note this is not a Unit test!
 * Designed to test the scalability of MTree implementation.
 * Created by al on 21/03/2017.
 */
public class MTreeSizeTest extends MTreeEuclidian2DTest {

    int initial = 500000; // 50 thousand
    int increment = 500000; // 50 thousand
    int max = 30000000; // 30 million.
    @Ignore
    public void loadtest() {

        for( int i = initial; i < max; i += increment ) {
            create_tree( i );
        }
    }

    /**
     * Create an MTree of the specified size.
     */
    private void create_tree(int size) {

        System.out.println( "Creating tree of size " + size );
        MTree<Point> t = new MTree<>( ed );

        Random random = new Random();
        for( int count = 0; count < size; count++ ) {
            Point p = new Point( random.nextFloat(), random.nextFloat());
            t.add(p);
        }
    }

}
