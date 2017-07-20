package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CTtableFactory {

    public static CTtable produceOrderedBirthContingencyTable(CTtable fullTable) {

        CTtable table = new CTtable(fullTable);

        table.addDateColumn();

        table.deleteRowsWhere("Sex", "MALE");
        table.deleteVariable("Sex");

        table.deleteVariable("YOB");
        table.deleteVariable("Died");
        table.deleteVariable("PNCIP");
        table.deleteVariable("NCIY");
        table.deleteVariable("NCIP");
        table.deleteVariable("Separated");
        table.deleteVariable("NPA");

        table.collectLikeRows();

        return table;
    }

}
