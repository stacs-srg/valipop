package uk.ac.standrews.cs.valipop.utils.sourceEventRecords.egSkyeFormat;

import uk.ac.standrews.cs.basic_model.model.IPopulation;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.oldDSformat.BirthSourceRecord;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;

import java.util.Random;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class EGSkyeBirthSourceRecord extends BirthSourceRecord {

    private static Random rng = new Random();

    protected int familyID = -1;
    protected ExactDate birthDate;
    protected ExactDate registrationDate;
    protected String mothersOccupation = "";
    protected String illegitimate = "";
    protected String marriageBaby = "";
    protected String deathID = "";

    public EGSkyeBirthSourceRecord(IPersonExtended person, IPopulation population) {
        super(person, population);

        familyID = parents_partnership_id;
        birthDate = new ExactDate(person.getBirthDate_ex());

        if(parents_partnership_id != -1) {
            mothersOccupation = person.getParentsPartnership_ex().getFemalePartner().getOccupation();
        }

        int registrationDay = rng.nextInt(43);
        registrationDate = birthDate.advanceTime(registrationDay);

        illegitimate = person.isIllegitimate() ? "illegitimate" : "";

        marriageBaby = person.getMarriageBaby() ? "true" : "false";

        if(person.getDeathDate_ex() != null) {
            deathID = String.valueOf(uid);
        }

    }


    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder();

        append(builder, "", "", uid, "", familyID, parents_partnership_id, "",
                "", "", "", "", "",
                "", "", "", "", "", "", "",
                "", forename, surname, birthDate.toString(), "", "",
                sex, fathers_forename, fathers_surname, fathers_occupation, mothers_forename,
                mothers_maiden_surname, mothersOccupation, parents_marriage_date.getDay(),
                parents_marriage_date.getMonth(), parents_marriage_date.getYear(), parents_place_of_marriage,
                "", "", "",
                "", "", "", registrationDate.getDay(),
                registrationDate.getMonth(), registrationDate.getYear(), illegitimate, "SYNTHETIC DATA PRODUCED USING VALIPOP", "", "", "", "", deathID,
                "", "", marriageBaby);

        return builder.toString();
    }

    @Override
    public String getHeaders() {

        final StringBuilder builder = new StringBuilder();

        append(builder, "IOSBIRTH_Identifier", "corrected", "ID", "source", "family", "marriage", "line no",
                "RD Identifier", "IOS_RDIdentifier", "IOS_RSDIdentifier", "register identifier", "IOS_RegisterNumber",
                "IOS_Entry no", "IOS_RegisterYear", "sschild", "sxchild", "ssfather", "sxfather", "ssmother",
                "sxmother", "child's forname(s)", "child's surname", "birth date", "address 1", "address 2",
                "sex", "father's forename", "father's surname", "father's occupation", "mother's forename",
                "mother's maiden surname", "mother's occupation", "day of parents' marriage",
                "month of parents' marriage", "year of parents' marriage", "place of parent's marriage 1",
                "place of parent's marriage 2", "forename of informant", "surname of informant",
                "relationship of informant to child", "did inform sign?", "was inform present?", "day of reg",
                "month of reg", "year of reg", "illegit", "notes1", "notes2", "notes3", "repeats", "edits", "Death",
                "latepid", "latesch", "marriageBaby");

        return builder.toString();
    }
}
