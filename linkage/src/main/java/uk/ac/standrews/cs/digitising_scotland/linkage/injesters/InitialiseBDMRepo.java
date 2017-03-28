package uk.ac.standrews.cs.digitising_scotland.linkage.injesters;

import uk.ac.standrews.cs.digitising_scotland.linkage.factory.BirthFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.DeathFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.MarriageFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.storr.impl.Store;
import uk.ac.standrews.cs.storr.impl.TypeFactory;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Module to initialise the store ready for ingesting of birth/death/marriage records.
 * Created by al on 22/3/2017.
 *
 * @author al@st-andrews.ac.uk
 */
public class InitialiseBDMRepo {

    public static final String BIRTHS_BUCKET_NAME = "birth_records";              // Name of bucket containing birth records (inputs).
    public static final String DEATHS_BUCKET_NAME = "death_records";              // Name of bucket containing death records (inputs).
    public static final String MARRIAGES_BUCKET_NAME = "marriage_records";        // Name of bucket containing marriage records (inputs).

    public static final String BIRTH_TYPE_NAME = "birth";
    public static final String DEATH_TYPE_NAME = "death";
    public static final String MARRIAGE_TYPE_NAME = "marriage";

    // Bucket declarations

    protected IBucket<BirthFamilyGT> births;                                // Bucket containing birth records (inputs).
    protected IBucket<Marriage> marriages;                                  // Bucket containing marriage records (inputs).
    protected IBucket<Death> deaths;                                        // Bucket containing death records (inputs).

    private static final String[] ARG_NAMES = {"store_path", "repo_name"};

    public InitialiseBDMRepo(String store_path, String repo_name) throws StoreException, IOException, RepositoryException {

        System.out.println("Creating BDM Repo named " + store_path);

        Path p = Paths.get(store_path);
        if (!p.toFile().isDirectory()) {
            throw new RepositoryException("Illegal store root specified");
        }

        IStore store = new Store(p);
        IRepository input_repo = store.makeRepository(repo_name);
        TypeFactory type_factory = store.getTypeFactory();

        IReferenceType birthType = type_factory.containsKey(BIRTH_TYPE_NAME) ? type_factory.getTypeWithName(BIRTH_TYPE_NAME) : type_factory.createType(BirthFamilyGT.class, BIRTH_TYPE_NAME);
        IReferenceType deathType = type_factory.containsKey(DEATH_TYPE_NAME) ? type_factory.getTypeWithName(DEATH_TYPE_NAME) : type_factory.createType(Death.class, DEATH_TYPE_NAME);
        IReferenceType marriageType = type_factory.containsKey(MARRIAGE_TYPE_NAME) ? type_factory.getTypeWithName(MARRIAGE_TYPE_NAME) : type_factory.createType(Marriage.class, MARRIAGE_TYPE_NAME);

        births = input_repo.makeBucket(BIRTHS_BUCKET_NAME, BucketKind.DIRECTORYBACKED, new BirthFactory(birthType.getId()));
        deaths = input_repo.makeBucket(DEATHS_BUCKET_NAME, BucketKind.DIRECTORYBACKED, new DeathFactory(deathType.getId()));
        marriages = input_repo.makeBucket(MARRIAGES_BUCKET_NAME, BucketKind.DIRECTORYBACKED, new MarriageFactory(marriageType.getId()));

        System.out.println("BDM Repo " + repo_name + " successfully initialised");
    }

    //***********************************************************************************

    public static void usage() {

        System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
    }

    public static void main(String[] args) throws Exception {

        if (args.length >= ARG_NAMES.length) {

            String store_path = args[0];
            String repo_name = args[1];
            new InitialiseBDMRepo(store_path, repo_name);

        } else {
            usage();
        }
    }
}
