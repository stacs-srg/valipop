package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;

import uk.ac.standrews.cs.digitising_scotland.linkage.factory.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.digitising_scotland.util.*;
import uk.ac.standrews.cs.storr.impl.*;
import uk.ac.standrews.cs.storr.impl.exceptions.*;
import uk.ac.standrews.cs.storr.interfaces.*;

import java.io.*;

/**
 * This class blocks based on persons' first name, last name and first name of parents over streams of BirthFamilyGT records
 * Created by al on 02/05/2014. x
 */
public class FNLFFMFOverBirths extends AbstractBlocker<BirthFamilyGT> {

    public FNLFFMFOverBirths(final IBucket<BirthFamilyGT> birthsBucket, final IRepository output_repo) throws BucketException, RepositoryException, IOException {

        super(birthsBucket.getInputStream(), output_repo, new BirthFactory(TypeFactory.getInstance().typeWithname("BirthFamilyGT").getId()));
    }

    @Override
    public String[] determineBlockedBucketNamesForRecord(final BirthFamilyGT record) {

        // Note will concat nulls into key if any fields are null - working hypothesis - this doesn't matter.

        try {
            final String normalised_forename = normaliseName(record.getString(BirthFamilyGT.FORENAME));
            final String normalised_surname = normaliseName(record.getString(BirthFamilyGT.SURNAME));
            final String normalised_father_forename = normaliseName(record.getString(BirthFamilyGT.FATHERS_FORENAME));
            final String normalised_mother_forename = normaliseName(record.getString(BirthFamilyGT.MOTHERS_FORENAME));

            String bucket_name = concatenate(normalised_forename, normalised_surname, normalised_father_forename, normalised_mother_forename);
            return new String[]{bucket_name};
        }
        catch (KeyNotFoundException e) {
            ErrorHandling.exceptionError(e, "Key not found");
            return new String[]{};
        }
        catch (TypeMismatchFoundException e) {
            ErrorHandling.exceptionError(e, "Type mismatch");
            return new String[]{};
        }
    }
}
