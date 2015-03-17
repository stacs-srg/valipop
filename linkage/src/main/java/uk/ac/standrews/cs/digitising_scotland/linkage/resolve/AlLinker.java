package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.StoreFactory;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.TypeFactory;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.*;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.EventImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.blocking.MultipleBlockerOverPerson;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.BirthFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.MarriageFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.PairPersonFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.PersonFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Relationship.RelationShipKind;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import static uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Relationship.RelationShipKind.FatherOf;
import static uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Relationship.RelationShipKind.MotherOf;

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
    private IBucket<Marriage> marriages;                  // Bucket containing marriage records (inputs).
    private IBucket<Death> deaths;                     // Bucket containing death records (inputs).
    private IBucket<Person> people;                     // Bucket containing people extracted from birth records
    private IBucket<Relationship> relationships;              // Bucket containing relationships between people
    private IBucket<Pair<Person>> lineage;             // Bucket containing pairs of potentially linked parents and child_ids

    // Paths to sources

    private static String source_base_path = "src/test/resources/BDMSet1";          // Path to source of vital event records in Digitising Scotland format

    private static String births_name = "birth_records";                            // Name of bucket containing birth records (inputs).
    private static String marriages_name = "marriage_records";                      // Name of bucket containing marriage records (inputs).
    private static String deaths_name = "death_records";                            // Name of bucket containing marriage records (inputs).
    private static String lineage_name = "lineage";


    // Names of buckets

    private static String people_name = "people";                                   // Name of bucket containing maximal people extracted from birth records
    private static String relationships_name = "relationships";                     // Name of bucket containing relationships between people

    private IReferenceType birthlabel;
    private IReferenceType deathlabel;
    private IReferenceType marriageLabel;
    private IReferenceType personLabel;
    private IReferenceType pairpersonlabel;

    private BirthFactory birthFactory;
    private PersonFactory personFactory;
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

        people = linkage_repo.makeBucket(people_name, BucketKind.DIRECTORYBACKED);
        relationships = linkage_repo.makeBucket(relationships_name, BucketKind.DIRECTORYBACKED);
        lineage = linkage_repo.makeBucket(lineage_name, BucketKind.INDEXED, pairpersonFactory );
    }

    private void initialiseTypes() {

        TypeFactory tf = TypeFactory.getInstance();

        birthlabel = tf.createType(Birth.class, "birth");
        deathlabel = tf.createType(Death.class, "death");
        personLabel = tf.createType(Person.class, "person");
        marriageLabel = tf.createType(Marriage.class, "marriage");

        pairpersonlabel = tf.createType( Pair.class,"pairperson" ); // TODO this is wrong?

    }

    private void initialiseFactories() {
        birthFactory = new BirthFactory(birthlabel.getId());
        personFactory = new PersonFactory(personLabel.getId());
        marriageFactory = new MarriageFactory(marriageLabel.getId());
        pairpersonFactory = new PairPersonFactory(pairpersonlabel.getId());
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

        EventImporter.importDigitisingScotlandBirths(births, births_source_path, birthlabel);
        EventImporter.importDigitisingScotlandDeaths(deaths, deaths_source_path, deathlabel);
        EventImporter.importDigitisingScotlandMarriages(marriages, marriages_source_path, marriageLabel );

        createPeopleAndRelationshipsFromBirths(births);
        createPeopleAndRelationshipsFromDeaths(deaths);
        createPeopleAndRelationshipsFromMarriages(marriages);
    }

    private void block() {
        try {
            IBlocker blocker = new MultipleBlockerOverPerson(people, blocked_people_repo, new PersonFactory(TypeFactory.getInstance().typeWithname("person").getId()));
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

        Iterator<IBucket<Person>> blocked_people_iterator = blocked_people_repo.getIterator(new PersonFactory(personLabel.getId()));


        while (blocked_people_iterator.hasNext()) {
            IBucket<Person> blocked_records = blocked_people_iterator.next();

            // Iterating over buckets of people with same first and last name and the same sex.

            BirthBirthLinker bdl = new BirthBirthLinker(blocked_records.getInputStream(), lineage.getOutputStream());
            bdl.pairwiseLink();
        }
    }


    /**
     * This method populates the people bucket with 1 person from each birth record (the relevant field names are the same in each)
     * For each record there will be 3 persons created - e.g. mother, father baby
     * Thus the people bucket will contain multiple copies of a person - one instance per record that they appear in.
     *
     * @param bucket - the bucket from which to take the inputs records
     */
    private void createPeopleAndRelationshipsFromBirths(IBucket bucket) throws BucketException, KeyNotFoundException, TypeMismatchFoundException, StoreException {

        IOutputStream<Person> people_stream = people.getOutputStream();
        IOutputStream<Relationship> relationships_stream = relationships.getOutputStream();

        IInputStream<Birth> stream = bucket.getInputStream();
        for (Birth birth_record : stream) {

            // add the people

            //      Person baby = createBaby(new Birth(birth_record), people_stream);

            Person baby = Person.createPersonFromOwnBirthDeath(birth_record);
            people_stream.add(baby);
            Person dad = Person.createFatherFromChildsBirthDeath(baby, birth_record.getId());
            if (dad != null) {
                people_stream.add(dad);
                addRelationship(dad, FatherOf, baby, birth_record, relationships_stream);
            }
            Person mum = Person.createMotherFromChildsBirthDeath(baby, birth_record.getId());
            if (mum != null) {
                people_stream.add(mum);
                addRelationship(dad, MotherOf, baby, birth_record, relationships_stream);
            }

            // Could add is_child but not now...
        }
    }

    /**
     * This method populates the people bucket with 1 person from each death record (the relevant field names are the same in each)
     * For each record there will be 3 persons created - e.g. mother, father baby
     * Thus the people bucket will contain multiple copies of a person - one instance per record that they appear in.
     *
     * @param bucket - the bucket from which to take the inputs records
     */
    private void createPeopleAndRelationshipsFromDeaths(IBucket bucket) throws BucketException, KeyNotFoundException, TypeMismatchFoundException, StoreException {

        IOutputStream<Person> people_stream = people.getOutputStream();
        IOutputStream<Relationship> relationships_stream = relationships.getOutputStream();

        IInputStream<Death> stream = bucket.getInputStream();
        for (Death death_record : stream) {

            // add the people

            //      Person baby = createBaby(new Birth(birth_record), people_stream);

            Person baby = Person.createPersonFromOwnBirthDeath(death_record);
            people_stream.add(baby);
            Person dad = Person.createFatherFromChildsBirthDeath(baby, death_record.getId());
            if (dad != null) {
                people_stream.add(dad);
                addRelationship(dad, FatherOf, baby, death_record, relationships_stream);
            }
            Person mum = Person.createMotherFromChildsBirthDeath(baby, death_record.getId());
            if (mum != null) {
                people_stream.add(mum);
                addRelationship(dad, MotherOf, baby, death_record, relationships_stream);
            }

            // Could add is_child but not now...
        }
    }

    private void createPeopleAndRelationshipsFromMarriages(IBucket<Marriage> bucket) throws BucketException, KeyNotFoundException, TypeMismatchFoundException, StoreException {

        IOutputStream<Person> people_stream = people.getOutputStream();
        IOutputStream<Relationship> relationships_stream = relationships.getOutputStream();

        IInputStream<Marriage> stream = bucket.getInputStream();
        for (Marriage marriage_record : stream) {

            // add the people

            Person bride = Person.createBrideFromMarriageRecord(marriage_record);
            people_stream.add(bride);
            Person groom = Person.createGroomFromMarriageRecord(marriage_record);
            people_stream.add(groom);

            Person grooms_mother = Person.createGroomsMotherFromMarriageRecord(groom, marriage_record);
            people_stream.add(grooms_mother);
            Person grooms_father = Person.createGroomsFatherFromMarriageRecord(groom, marriage_record);
            people_stream.add(grooms_father);
            Person brides_mother = Person.createBridesMotherFromMarriageRecord(bride, marriage_record);
            people_stream.add(brides_mother);
            Person brides_father = Person.createBridesFatherFromMarriageRecord(bride, marriage_record);
            people_stream.add(brides_father);

            // add the relationships

            addRelationship(grooms_father, FatherOf, groom, marriage_record, relationships_stream);
            addRelationship(grooms_mother, MotherOf, groom, marriage_record, relationships_stream);
            addRelationship(brides_father, FatherOf, bride, marriage_record, relationships_stream);
            addRelationship(brides_mother, MotherOf, bride, marriage_record, relationships_stream);
        }
    }


    private void addRelationship(Person person1, RelationShipKind relationship_type, Person person2, ILXP evidence, IOutputStream<Relationship> output) {

        if (person1 != null) {

                Relationship r = new Relationship( person1, relationship_type, person2, evidence );
                output.add(r);
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
