package uk.ac.standrews.cs.digitising_scotland.util.MTree;

import uk.ac.standrews.cs.util.tools.PercentageProgressIndicator;
import uk.ac.standrews.cs.util.tools.ProgressIndicator;

import java.util.Random;

/**
 * Note this is not a Unit test!
 * Designed to test the scalability of MTree implementation.
 * Created by al on 21/03/2017.
 */
public class MTreeSizeTest extends MTreeEuclidian2DTest {

    int initial = 5000000; // 1/2  million
    int increment = 5000000; // 1/2  million
    int max = 30000000; // 30 million.

    EuclidianDistance ed = new EuclidianDistance();

    public void loadtest() {

        for( int i = initial; i < max; i += increment ) {
            create_tree( i );
        }
    }

    /**
     * Create an MTree of the specified size.
     */
    private void create_tree(int size) {

        MTree<Point> tree = new MTree<Point>( ed );

        long time = System.currentTimeMillis();
        System.out.println( "Creating tree of size " + size );

        ProgressIndicator indicator = new PercentageProgressIndicator(10);
        indicator.setTotalSteps(size);

        Random random = new Random();
        for( int count = 0; count < size; count++ ) {
            tree.add(new Point( random.nextFloat(), random.nextFloat()));
            indicator.progressStep();
        }
        long elapsed = (System.currentTimeMillis() - time) / 1000;
        System.out.println("tree creation of size " + size + " - took " + elapsed + " seconds.");
    }

    public static void main( String args[] ) {
        new MTreeSizeTest().loadtest();
    }

}
