package uk.ac.standrews.cs.valipop.utils.sourceEventRecords.tdFormat;

import org.apache.commons.math3.random.JDKRandomGenerator;
import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.IPopulation;
import uk.ac.standrews.cs.valipop.simulationEntities.PopulationNavigation;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.SexOption;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.oldDSformat.DeathSourceRecord;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

import static uk.ac.standrews.cs.valipop.simulationEntities.PopulationNavigation.getLastPartnership;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class TDDeathSourceRecord extends DeathSourceRecord {

    private static Random rng = new Random();

    protected LocalDate deathDate;
    protected LocalDate registrationDate;
    protected String mothersOccupation;
    protected String marriageIDs;
    protected String deathAddress;

    private String DECEASED_IDENTITY = "";
    private String MOTHER_IDENTITY = "";
    private String FATHER_IDENTITY = "";
    private String SPOUSE_IDENTITY = "";
    private String BIRTH_RECORD_IDENTITY = "";
    private String PARENT_MARRIAGE_RECORD_IDENTITY = "";
    private String FATHER_BIRTH_RECORD_IDENTITY = "";
    private String MOTHER_BIRTH_RECORD_IDENTITY = "";
    private String SPOUSE_MARRIAGE_RECORD_IDENTITY = "";
    private String SPOUSE_BIRTH_RECORD_IDENTITY = "";

    public TDDeathSourceRecord(IPerson person, IPopulation population) {

        super(person, population);

        deathDate = person.getDeathDate();
        deathAddress = person.getAddress(deathDate).toString();

        String[] deathCauses = person.getDeathCause().split(" ");

        if(deathCauses.length >= 1)
            setDeathCauseA(deathCauses[0]);

        if(deathCauses.length >= 2)
            setDeathCauseB(deathCauses[1]);

        if(deathCauses.length >= 3)
            setDeathCauseC(deathCauses[2]);

        if (person.getParents() != null) {

            IPerson mother = person.getParents().getFemalePartner();
            mothersOccupation = mother.getOccupation(deathDate);

            IPerson father = person.getParents().getMalePartner();
            if (!PopulationNavigation.aliveOnDate(father, person.getDeathDate())) {
                // father is dead
                setFatherDeceased("D"); // deceased
            }

            fathers_surname = father.getSurname();

            if (!PopulationNavigation.aliveOnDate(mother, person.getDeathDate())) {
                // mother is dead
                setMotherDeceased("D"); // deceased
            }

            if(person.getParents().getMarriageDate() != null)
                PARENT_MARRIAGE_RECORD_IDENTITY = String.valueOf(person.getParents().getId());

            MOTHER_IDENTITY = String.valueOf(mother.getId());
            FATHER_IDENTITY = String.valueOf(father.getId());;
            FATHER_BIRTH_RECORD_IDENTITY = String.valueOf(father.getId());;
            MOTHER_BIRTH_RECORD_IDENTITY = String.valueOf(mother.getId());;
        }

        int registrationDay = rng.nextInt(9);
        registrationDate = deathDate.plus(registrationDay, ChronoUnit.DAYS);

        setMaritalStatus(identifyMaritalStatus(person));

        DECEASED_IDENTITY = uid;
        BIRTH_RECORD_IDENTITY = uid;

        LocalDate lastMarriageDate = LocalDate.MIN;

        for (IPartnership partnership : person.getPartnerships()) {

            LocalDate marriageDate = partnership.getMarriageDate();

            if(marriageDate != null && marriageDate.isAfter(lastMarriageDate)) {

                IPerson spouse = partnership.getPartnerOf(person);
                SPOUSE_IDENTITY = String.valueOf(spouse.getId());
                SPOUSE_BIRTH_RECORD_IDENTITY = String.valueOf(spouse.getId());

                String spousesName = spouse.getFirstName() + " " + spouse.getSurname();
                String spousesOccupation = spouse.getOccupation(person.getDeathDate());

                setSpousesNames(spousesName);
                setSpousesOccupations(spousesOccupation);
                marriageIDs = String.valueOf(partnership.getId());

                SPOUSE_MARRIAGE_RECORD_IDENTITY = String.valueOf(partnership.getId());

            }
        }

    }



    public String identifyMaritalStatus(IPerson deceased) {

        List<IPartnership> partnerships = deceased.getPartnerships();

        if (partnerships.size() == 0) {
            if (deceased.getSex() == SexOption.MALE) {
                return "B"; // bachelor
            } else {
                return "S"; // single/spinster
            }
        } else {
            if (getLastPartnership(deceased).getSeparationDate(new JDKRandomGenerator()) == null) {
                // not separated from last partner

                IPerson lastPartner = getLastPartnership(deceased).getPartnerOf(deceased);
                if (PopulationNavigation.aliveOnDate(lastPartner, deceased.getDeathDate())) {

                    // last spouse alive on death date of deceased
                    return partnerships.size() > 1 ? "R" : "M"; // married

                } else {
                    // last spouse dead on death date of deceased
                    return "W"; // widow/er
                }
            } else {
                // separated from last partner
                return "D"; // divorced
            }
        }
    }


    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder();

        append(builder,  uid, forename, surname, getOccupation(),
                getMaritalStatus(), sex, getSpousesNames(), getSpousesOccupations(),
                deathDate.getDayOfMonth(), deathDate.getMonth(), deathDate.getYear(),
                deathAddress, getDeathAge(), fathers_forename,
                fathers_surname, "", getFatherDeceased(), mothers_forename,
                mothers_maiden_surname, "", getMotherDeceased(), getDeathCauseA(), getDeathCauseB(), getDeathCauseC(),
                "SYNTHETIC DATA PRODUCED USING VALIPOP", uid, marriageIDs, DECEASED_IDENTITY, MOTHER_IDENTITY, FATHER_IDENTITY, SPOUSE_IDENTITY,
                BIRTH_RECORD_IDENTITY, PARENT_MARRIAGE_RECORD_IDENTITY, FATHER_BIRTH_RECORD_IDENTITY,
                MOTHER_BIRTH_RECORD_IDENTITY, SPOUSE_MARRIAGE_RECORD_IDENTITY, SPOUSE_BIRTH_RECORD_IDENTITY);

        return builder.toString();
    }

    @Override
    public String getHeaders() {

        final StringBuilder builder = new StringBuilder();

        append(builder, "ID", "forename(s) of deceased", "surname of deceased", "occupation",
                "marital status", "sex", "name of spouse", "spouse's occ",
                "day", "month", "year", "address", "age at death", "father's forename",
                "father's surname", "father's occupation", "if father deceased", "mother's forename",
                "mother's maiden surname", "mother's occupation", "if mother deceased", "death code A", "death code B",
                "death code C",
                "notes1", "Birth",
                "mar", "DECEASED_IDENTITY",
                "MOTHER_IDENTITY", "FATHER_IDENTITY", "SPOUSE_IDENTITY", "BIRTH_RECORD_IDENTITY",
                "PARENT_MARRIAGE_RECORD_IDENTITY", "FATHER_BIRTH_RECORD_IDENTITY", "MOTHER_BIRTH_RECORD_IDENTITY",
                "SPOUSE_MARRIAGE_RECORD_IDENTITY", "SPOUSE_BIRTH_RECORD_IDENTITY");

        return builder.toString();
    }
}
