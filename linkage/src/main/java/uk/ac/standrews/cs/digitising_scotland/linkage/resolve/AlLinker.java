package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.jstore.impl.StoreFactory;
import uk.ac.standrews.cs.jstore.impl.TypeFactory;
import uk.ac.standrews.cs.jstore.impl.exceptions.*;
import uk.ac.standrews.cs.jstore.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.EventImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.blocking.MultipleBlockerOverPerson;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.BirthFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.MarriageFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.PairPersonFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.RoleFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * Attempt to create a linking framework
 * Created by al on 6/8/2014.
 */
public class AlLinker {


    // Repositories and stores

    private static String input_repo_name = "BDM_repo";                         // input repository containing event records
    private static String linkage_repo_name = "linkage_repo";                   // repository for linked records
    private static String blocked_people_repo_name = "blocked_people_repo";     // repository for blocked records

    private IStore store;
    private IRepository input_repo;             // Repository containing buckets of BDM records
    private IRepository linkage_repo;
    private IRepository blocked_people_repo;

    // Bucket declarations

    private IBucket<Birth> births;                     // Bucket containing birth records (inputs).
    private IBucket<Marriage> marriages;               // Bucket containing marriage records (inputs).
    private IBucket<Death> deaths;                     // Bucket containing death records (inputs).

    private IBucket<Role> people;                     // Bucket containing roles extracted from BDM records
    private IBucket<Pair<Role>> lineage;              // Bucket containing pairs of potentially linked toles

    // Paths to sources

    private static String source_base_path = "src/test/resources/BDMSet1";          // Path to source of vital event records in Digitising Scotland format
    private static String births_name = "birth_records";                            // Name of bucket containing birth records (inputs).
    private static String marriages_name = "marriage_records";                      // Name of bucket containing marriage records (inputs).
    private static String deaths_name = "death_records";                            // Name of bucket containing marriage records (inputs).
    private static String lineage_name = "lineage";

    // Names of buckets

    private static String role_name = "roles";                                   // Name of bucket containing roles extracted from BDM records

    private IReferenceType birthType;
    private IReferenceType deathType;
    private IReferenceType marriageType;
    private IReferenceType roleType;
    private IReferenceType pairpersonType;

    private BirthFactory birthFactory;
    private RoleFactory roleFactory;
    private MarriageFactory marriageFactory;
    private PairPersonFactory pairpersonFactory;


    public AlLinker(String births_source_path, String deaths_source_path, String marriages_source_path) throws BucketException, RepositoryException, RecordFormatException, JSONException, IOException, PersistentObjectException, StoreException, KeyNotFoundException, TypeMismatchFoundException, IllegalKeyException {

        initialise();
        injestBDMRecords(births_source_path, deaths_source_path, marriages_source_path );
        block();
        link();
        System.out.println( "Linkage completed" );

    }

    private void initialise() throws StoreException, IOException, RepositoryException, RecordFormatException, JSONException {

        Path store_path = Files.createTempDirectory(null);

        StoreFactory.setStorePath(store_path.toString()); // TODO sort out PATH and String and File
        store = StoreFactory.makeStore();

        System.out.println( "Store path = " + store_path );

        input_repo = store.makeRepository(input_repo_name);
        linkage_repo = store.makeRepository(linkage_repo_name);
        blocked_people_repo = store.makeRepository(blocked_people_repo_name);  // a repo of Buckets of records blocked by  first name, last name, sex

        initialiseTypes();
        initialiseFactories();

        births = input_repo.makeBucket(births_name, BucketKind.DIRECTORYBACKED);   // TODO make these type specific
        deaths = input_repo.makeBucket(deaths_name, BucketKind.DIRECTORYBACKED);   // TODO look for all occurances and change them to typed or generic
        marriages = input_repo.makeBucket(marriages_name, BucketKind.DIRECTORYBACKED);

        people = linkage_repo.makeBucket(role_name, BucketKind.DIRECTORYBACKED);
        lineage = linkage_repo.makeBucket(lineage_name, BucketKind.INDEXED, pairpersonFactory );
    }

    private void initialiseTypes() {

        TypeFactory tf = TypeFactory.getInstance();

        birthType = tf.createType(Birth.class, "birth");
        deathType = tf.createType(Death.class, "death");
        marriageType = tf.createType(Marriage.class, "marriage");

        roleType = tf.createType(Role.class, "role");

        pairpersonType = tf.createType( Pair.class,"pairperson" ); // TODO this is wrong?

    }

    private void initialiseFactories() {
        birthFactory = new BirthFactory(birthType.getId());
        marriageFactory = new MarriageFactory(marriageType.getId());
        roleFactory = new RoleFactory(roleType.getId());

        pairpersonFactory = new PairPersonFactory(pairpersonType.getId());
    }

    /**
     * Import the birth,death, marriage records
     * Initialises the people bucket with the people injected - one record for each person referenced in the original record
     * Initialises the known(100% certain) relationships between people and stores the relationships in the relationships bucket
     * @param births_source_path
     * @param deaths_source_path
     * @param marriages_source_path
     */
    private void injestBDMRecords(String births_source_path, String deaths_source_path, String marriages_source_path) throws BucketException, RecordFormatException, JSONException, IOException, KeyNotFoundException, PersistentObjectException, TypeMismatchFoundException, IllegalKeyException, StoreException {

        EventImporter.importDigitisingScotlandBirths(births, births_source_path, birthType);
        EventImporter.importDigitisingScotlandDeaths(deaths, deaths_source_path, deathType);
        EventImporter.importDigitisingScotlandMarriages(marriages, marriages_source_path, marriageType);

        createRolesFromBirths(births);
        createRolesFromDeaths(deaths);       // TODO can we add spouse?
        createRolesFromMarriages(marriages);
    }

    private void block() {
        try {
            IBlocker blocker = new MultipleBlockerOverPerson(people, blocked_people_repo, roleFactory);
            blocker.apply();
        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (BucketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void link() throws BucketException {

        Iterator<IBucket<Role>> blocked_people_iterator = blocked_people_repo.getIterator(roleFactory);


        while (blocked_people_iterator.hasNext()) {
            IBucket<Role> blocked_records = blocked_people_iterator.next();

            // Iterating over buckets of people with same first and last name and the same sex.

            PeopleUnifier pl = new PeopleUnifier(blocked_records.getInputStream(), lineage.getOutputStream());
            pl.pairwiseUnify();
        }
    }


    /**
     * This method populates the roles bucket
     * For each record in the Births bucket there will be 3 roles created - e.g. mother, father baby
     *
     * @param bucket - the bucket from which to take the inputs records
     */
    private void createRolesFromBirths(IBucket bucket) throws BucketException, KeyNotFoundException, TypeMismatchFoundException, StoreException {

        IOutputStream<Role> people_stream = people.getOutputStream();
        IInputStream<Birth> stream = bucket.getInputStream();

        for (Birth birth_record : stream) {

            Role baby = Role.createPersonFromOwnBirthDeath(birth_record, birthType.getId() );
            people_stream.add(baby);
            Role dad = Role.createFatherFromChildsBirthDeath(birth_record, birthType.getId() );
            if (dad != null) {
                people_stream.add(dad);
            }
            Role mum = Role.createMotherFromChildsBirthDeath( birth_record, birthType.getId() );
            if (mum != null) {
                people_stream.add(mum);
            }
        }
    }

    /**
     * This method populates the roles bucket
     * For each record in the Deaths bucket there will be 3 roles created - e.g. mother, father baby
     *
     * @param bucket - the bucket from which to take the inputs records
     */
    private void createRolesFromDeaths(IBucket bucket) throws BucketException, KeyNotFoundException, TypeMismatchFoundException, StoreException {

        IOutputStream<Role> people_stream = people.getOutputStream();
        IInputStream<Death> stream = bucket.getInputStream();

        for (Death death_record : stream) {

            // add the people

            //      Person baby = createBaby(new Birth(birth_record), people_stream);

            Role baby = Role.createPersonFromOwnBirthDeath( death_record, deathType.getId() );
            people_stream.add(baby);
            Role dad = Role.createFatherFromChildsBirthDeath( death_record, deathType.getId() );
            if (dad != null) {
                people_stream.add(dad);
            }
            Role mum = Role.createMotherFromChildsBirthDeath( death_record, deathType.getId() );
            if (mum != null) {
                people_stream.add(mum);
            }
        }
    }

    /**
     * This method populates the roles bucket
     * For each record in the Marriages bucket there will be 6 roles created - e.g. bride, groom plus the parents of each
     *
     * @param bucket - the bucket from which to take the inputs records
     */
    private void createRolesFromMarriages(IBucket<Marriage> bucket) throws BucketException, KeyNotFoundException, TypeMismatchFoundException, StoreException {

        IOutputStream<Role> people_stream = people.getOutputStream();

        IInputStream<Marriage> stream = bucket.getInputStream();
        for (Marriage marriage_record : stream) {

            Role bride = Role.createBrideFromMarriageRecord(marriage_record);
            people_stream.add(bride);
            Role groom = Role.createGroomFromMarriageRecord(marriage_record);
            people_stream.add(groom);
            Role grooms_mother = Role.createGroomsMotherFromMarriageRecord(marriage_record);
            people_stream.add(grooms_mother);
            Role grooms_father = Role.createGroomsFatherFromMarriageRecord(marriage_record);
            people_stream.add(grooms_father);
            Role brides_mother = Role.createBridesMotherFromMarriageRecord(marriage_record);
            people_stream.add(brides_mother);
            Role brides_father = Role.createBridesFatherFromMarriageRecord(marriage_record);
            people_stream.add(brides_father);

        }
    }

    /**
     * **************************************************************************************************************
     */

    public static void main(String[] args) throws Exception, KeyNotFoundException, TypeMismatchFoundException, IllegalKeyException {

        String births_source_path =    "/Users/al/Documents/intelliJ/BDMSet1/birth_records.txt";
        String deaths_source_path =    "/Users/al/Documents/intelliJ/BDMSet1/death_records.txt";
        String marriages_source_path = "/Users/al/Documents/intelliJ/BDMSet1/marriage_records.txt";

        new AlLinker(births_source_path, deaths_source_path, marriages_source_path);
    }
}
