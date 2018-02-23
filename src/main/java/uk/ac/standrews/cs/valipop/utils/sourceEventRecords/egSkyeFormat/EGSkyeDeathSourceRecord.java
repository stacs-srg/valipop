package uk.ac.standrews.cs.valipop.utils.sourceEventRecords.egSkyeFormat;

import org.apache.commons.math3.random.JDKRandomGenerator;
import uk.ac.standrews.cs.basic_model.model.IPopulation;
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.oldDSformat.DeathSourceRecord;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;

import java.util.List;
import java.util.Random;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class EGSkyeDeathSourceRecord extends DeathSourceRecord {

    private static Random rng = new Random();

    protected ExactDate deathDate;
    protected ExactDate registrationDate;
    protected String mothersOccupation;

    public EGSkyeDeathSourceRecord(IPersonExtended person, IPopulation population) {
        super(person, population);

        deathDate = new ExactDate(person.getDeathDate_ex());

        if(person.getParentsPartnership() != -1) {
            mothersOccupation = person.getParentsPartnership_ex().getFemalePartner().getOccupation();

            if(!person.getParentsPartnership_ex().getMalePartner().aliveOnDate(person.getDeathDate_ex())) {
                // father is dead
                setFatherDeceased("D"); // deceased
            }

            if(!person.getParentsPartnership_ex().getFemalePartner().aliveOnDate(person.getDeathDate_ex())) {
                // mother is dead
                setMotherDeceased("D"); // deceased
            }
        }

        int registrationDay = rng.nextInt(9);
        registrationDate = deathDate.advanceTime(registrationDay);

        setMaritalStatus(identifyMarritalStatus(person));
        String[] spousesInfo = identifyNameAndOccupationOfSpouses(person);
        setSpousesNames(spousesInfo[0]);
        setSpousesOccupations(spousesInfo[1]);



    }

    public String identifyMarritalStatus(IPersonExtended deceased) {

        List<IPartnershipExtended> partnerships = deceased.getPartnerships_ex();

        if(partnerships.size() == 0) {
            if(Character.toLowerCase(deceased.getSex()) == 'm') {
                return "B"; // bachelor
            } else {
                return "S"; // single/spinster
            }
        } else {
            if(deceased.getLastPartnership().getSeparationDate(new JDKRandomGenerator()) == null) {
                // not separated from last partner
                if(deceased.getLastPartnership().getPartnerOf(deceased).aliveOnDate(deceased.getDeathDate_ex())) {
                    // last spouse alive on death date of deceased
                    if(partnerships.size() > 1) {
                        return "R"; // remarried
                    } else {
                        return "M"; // married
                    }
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

    public String[] identifyNameAndOccupationOfSpouses(IPersonExtended deceased) {
        String[] ret = new String[2];

        StringBuilder names = new StringBuilder();
        StringBuilder occupations = new StringBuilder();

        for(IPartnershipExtended partnership : deceased.getPartnerships_ex()) {

            IPersonExtended spouse = partnership.getPartnerOf(deceased);

            String spousesName = spouse.getFirstName() + " " + spouse.getSurname();
            String spousesOccupation = spouse.getOccupation();

            if(names.length() == 0) {
                names.append(spousesName);
                occupations.append(spousesOccupation);
            } else {
                names.append("+" + spousesName);
                occupations.append("+" + spousesOccupation);
            }

        }

        ret[0] = names.toString();
        ret[1] = occupations.toString();

        return ret;
    }


    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder();

        append(builder, "", "", uid, "", "", "",
                "", "", "", "", "",
                "", "", "", "", "", "", "", "",
                "", "", "", forename, surname, getOccupation(),
                getMaritalStatus(), sex, getSpousesNames(), getSpousesOccupations(), deathDate.toString(),
                getDeathDay(), getDeathMonth(), getDeathYear(), "", "", getDeathAge(), getDeathAge(), fathers_forename,
                fathers_surname, fathers_occupation, getFatherDeceased(), mothers_forename,
                mothers_maiden_surname, mothersOccupation, getMotherDeceased(), getDeathCauseA(),
                "", "", "", "",
                "", "", "", "",
                registrationDate.getDay(), registrationDate.getMonth(), registrationDate.getYear(), "", "", "", "", "", uid,
                "", "", "");

        return builder.toString();
    }

    @Override
    public String getHeaders() {

        final StringBuilder builder = new StringBuilder();

        append(builder, "IOSidentifier", "corrected", "ID", "source", "input", "identifier",
                "IOS_Rdindentifier", "IOS_RSDindentifier", "register identifier", "IOS_Regisdentifier", "entry number",
                "IOS_yearofregistration", "ssdec", "sxdec", "ssfather", "sxfather", "ssmother", "sxmother", "spousesn",
                "spousexn", "infxn", "infsn", "forename(s) of deceased", "surname of deceased", "occupation",
                "marital status", "sex", "name of spouse(s)", "spouse's occ", "death date",
                "day", "month", "year", "address 1", "address 2", "age at death", "agey", "father's forename",
                "father's surname", "father's occupation", "if father deceased", "mother's forename",
                "mother's maiden surname", "mother's occupation", "if mother deceased", "cause of death",
                "length of last illness", "medically certified", "doctor's name", "forename of informant",
                "surname of informant", "relationship of informant to deceased", "did inform sign?", "was inform pres?",
                "day of reg", "month of reg", "year of reg", "notes1", "notes2", "notes3", "repeats", "edits", "Birth",
                "earlypid", "earlysch", "mar");

        return builder.toString();
    }
}
