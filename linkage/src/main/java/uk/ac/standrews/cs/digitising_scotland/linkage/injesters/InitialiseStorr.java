package uk.ac.standrews.cs.digitising_scotland.linkage.injesters;

import uk.ac.standrews.cs.storr.impl.Store;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by al on 22/3/2017.
 * @author al@st-andrews.ac.uk
 */
public class InitialiseStorr {

    private static final String[] ARG_NAMES = {"store_path"};

    private InitialiseStorr(String store_path) throws StoreException, IOException, RepositoryException {

        System.out.println( "Creating Storr in " + store_path );

        new Store(Paths.get(store_path));
        System.out.println( "Storr successfully created in " + store_path );
    }

    //***********************************************************************************

    public static void usage() {

        System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
    }

    public static void main(String[] args) throws Exception {

        if (args.length >= ARG_NAMES.length) {

            String store_path = args[0];
            new InitialiseStorr( store_path );

        } else {
            usage();
        }
    }
}
