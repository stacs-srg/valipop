package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables;

import uk.ac.standrews.cs.digitising_scotland.verisim.config.Config;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.EventType;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ContingencyTableGenerator {

//    private TableNode<String,       // Root
//            TableNode<Boolean,      // Synthetic / Statistics
//            TableNode<YearDate,     // Year
//            TableNode<Boolean,      // Male / Female
//            TableNode<Integer,      // Age
//            TableNode<Boolean,      // Died / Alive
//            TableNode<Integer,      // Children in active partnership
//            TableNode<Boolean,      // Separated
//            TableNode<Integer,      // New partner age
//            TableNode<Integer,      // Children birthed in previous years
//            TableNode<Integer,      // Children birthed in year
//                    ?>>>>>>>>>>>    // Count
//                        t = new TableNode<>("ROOT");

    private TableNode<String, Boolean> table = new TableNode<>("ROOT");

    public TableNode getFullTable() {
        return table;
    }

    private static final String SEP = ",";

    public ContingencyTableGenerator(IPopulation population, PopulationStatistics expectedStatistics,
                                     Date startDate, Date endDate) {

        // For every year
        for (YearDate y = startDate.getYearDate(); DateUtils.dateBefore(y, endDate);
             y = y.advanceTime(1, TimeUnit.YEAR).getYearDate()) {

            // for every person in population
            for(IPersonExtended person : population.getPeople()) {

                // who was alive or died in the year of consideration
                if(person.aliveInYear(y)) {
                    addPersonToTree(person, true, y, table);
                }
            }
        }

    }


//    public TableNode transformTableForDeath(TableNode<String, Boolean> oTable, )

    private IntegerRange[] ageRanges = {
            new IntegerRange(0,14),
            new IntegerRange(15,19),
            new IntegerRange(20,24),
            new IntegerRange(25,29),
            new IntegerRange(30,34),
            new IntegerRange(35,39),
            new IntegerRange(40,44),
            new IntegerRange(45,49),
            new IntegerRange(50, true)
    };

    private IntegerRange[] childrenSepRanges = {
            new IntegerRange(0),
            new IntegerRange(1),
            new IntegerRange(2),
            new IntegerRange(3),
            new IntegerRange(4),
            new IntegerRange(5, true)
    };

    private IntegerRange[] childrenOBRanges = {
            new IntegerRange(0),
            new IntegerRange(1),
            new IntegerRange(2),
            new IntegerRange(3),
            new IntegerRange(4, true)
    };

    private IntegerRange discritiseValue(int value, IntegerRange[] ranges) {

        for(IntegerRange iR : ranges) {
            if(iR.contains(value)) {
                return iR;
            }
        }

        return null;
    }

    private boolean boolValue(int value) {
        return value != 0;
    }

    public void outputTable(Config config, TableNode<String, Boolean> t) {

        PrintWriter table = null;
        try {
            table = new PrintWriter("table-CT-wF.csv", "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        PrintStream table = new PrintStream(Paths.get("Desktop", "table.dat"));

                //FileUtils.setupDatFileAsStream(EventType.CT, "table", config);

//        table.println("source, yob, sex, age, died, children birthed in partnership, separated, new partner age, " +
//                "previous children birthed, children birthed in year");

        table.println(
                        "source, " +    // 0
                        "yob, " +       // 1
                        "sex, " +       // 2
                        "age, age.dis, " +      // 3
                        "died, " +              // 4
                        "children.birthed.in.partnership, children.birthed.in.partnership.dis, " +       // 5
                        "separated, " +         // 6
                        "new.partner.age, new.partner.age.dis, " +  // 7
                        "previous.children.birthed, previous.children.birthed.dis, " +      // 8
                        "children.birthed.in.year, children.birthed.in.year.bool, " +       // 9
                        "freq");

        LinkedList<String> descent = new LinkedList<>();

        for(TableNode source : t.getChildren()) {

            // Stat / sim
            TableNode<Boolean, Integer> n = (TableNode<Boolean, Integer>) source;

            Boolean synthetic = n.getValue();

            if(synthetic) {
                descent.add("sim");
            } else {
                descent.add("stats");
            }

            for(TableNode yob : n.getChildren()) {

                // yob
                TableNode<YearDate, Boolean> n1 = (TableNode<YearDate, Boolean>) yob;

                descent.add(Integer.toString(n1.getValue().getYear()));

                for(TableNode sex : n1.getChildren()) {

                    // sex
                    TableNode<Boolean, Integer> n2 = (TableNode<Boolean, Integer>) sex;
                    Boolean male = n2.getValue();

                    if(male) {
                        descent.add("Male");
                    } else {
                        descent.add("Female");
                    }

                    for(TableNode age : n2.getChildren()) {

                        // age
                        TableNode<Integer, Boolean> n3 = (TableNode<Integer, Boolean>) age;

                        descent.add(n3.getValue().toString());

                        for(TableNode died: n3.getChildren()) {

                            // Died / Alive
                            TableNode<Boolean, Integer> n4 = (TableNode<Boolean, Integer>) died;
                            Boolean dead = n4.getValue();

                            if(dead) {
                                descent.add("Y");
                            } else {
                                descent.add("N");
                            }

                            for(TableNode childrenInActivePartnership : n4.getChildren()) {

                                // Children in active partnership
                                TableNode<Integer, Boolean> n5 = (TableNode<Integer, Boolean>) childrenInActivePartnership;
                                descent.add(n5.getValue().toString());

                                for(TableNode separated : n5.getChildren()) {

                                    // Separated
                                    TableNode<Boolean, Integer> n6 = (TableNode<Boolean, Integer>) separated;

                                    Boolean s = n6.getValue();

                                    if(s == null) {
                                        descent.add("NA");
                                    } else if(s) {
                                        descent.add("T");
                                    } else {
                                        descent.add("F");
                                    }

                                    for(TableNode newPartnerAge : n6.getChildren()) {

                                        TableNode<Integer, Integer> n7 = (TableNode<Integer, Integer>) newPartnerAge;

                                        Integer a = n7.getValue();
                                        if(a == null) {
                                            descent.add("None");
                                        } else {
                                            descent.add(a.toString());
                                        }



                                        for(TableNode prevChildren : n7.getChildren()) {

                                            TableNode<Integer, Integer> n8 = (TableNode<Integer, Integer>) prevChildren;

                                            descent.add(n8.getValue().toString());

                                            for(TableNode childrenInYear : n8.getChildren()) {

                                                TableNode<Integer, ?> n9 = (TableNode<Integer, ?>) childrenInYear;

                                                descent.add(n9.getValue().toString());


//                                                for(int i = 0; i < n9.getCount(); i++) {
                                                    int c = 0;
                                                    for (String str : descent) {
                                                        switch(c) {
                                                            case 3:
                                                            case 7:
                                                                table.print(str + SEP);
                                                                if(!Objects.equals(str, "None")) {
                                                                    table.print(discritiseValue(new Integer(str), ageRanges) + SEP);
                                                                } else {
                                                                    table.print(str + SEP);
                                                                }
                                                                break;
                                                            case 5:
                                                                table.print(str + SEP);
                                                                table.print(discritiseValue(new Integer(str), childrenSepRanges) + SEP);
                                                                break;
                                                            case 8:
                                                                table.print(str + SEP);
                                                                table.print(discritiseValue(new Integer(str), childrenOBRanges) + SEP);
                                                                break;
                                                            case 9:
                                                                table.print(str + SEP);
                                                                table.print(boolValue(new Integer(str)) + SEP);
                                                                break;
                                                            default:
                                                                table.print(str + SEP);
                                                        }

                                                        c++;
                                                    }
//                                                }
                                                table.println(n9.getCount());

                                                descent.removeLast();
                                            }
                                            descent.removeLast();
                                        }
                                        descent.removeLast();
                                    }
                                    descent.removeLast();
                                }
                                descent.removeLast();
                            }
                            descent.removeLast();
                        }
                        descent.removeLast();
                    }
                    descent.removeLast();
                }
                descent.removeLast();
            }
            descent.removeLast();
        }

    }

    public void addPersonToTree(IPersonExtended person, Boolean synthetic, YearDate y, TableNode t) {

        TableNode node;

        // Get Synthetic / Statistics node

        try {
            node = t.getChild(synthetic);
        } catch (IsLeafException | ChildNotFoundException e) {
            node = t.addChild(synthetic);
        }

        // Get node for year of birth

        YearDate birthYear = person.getBirthDate_ex().getYearDate();
        try {
            node = node.getChild(birthYear);
        } catch (IsLeafException | ChildNotFoundException e) {
            node = node.addChild(birthYear);
        }

        // Get node for sex

        boolean sex = convertSexToBoolean(person.getSex());
        try {
            node = node.getChild(sex);
        } catch (IsLeafException | ChildNotFoundException e) {
            node = node.addChild(sex);
        }

        // Get node for age

        Integer age = person.ageOnDate(y);
        try {
            node = node.getChild(age);
        } catch (IsLeafException | ChildNotFoundException e) {
            node = node.addChild(age);
        }

        // Get node for Died / Alive

        boolean died = person.diedInYear(y);
        try {
            node = node.getChild(died);
        } catch (IsLeafException | ChildNotFoundException e) {
            node = node.addChild(died);
        }

        Collection<IPartnershipExtended> partnershipsFromYear = person.getPartnershipsActiveInYear(y);

        if(partnershipsFromYear.size() > 1) {
            if(sex) {
                System.out.println("Male multiple active partners in year: " + partnershipsFromYear.size());
            } else {
                System.out.println("Female multiple active partners in year: " + partnershipsFromYear.size());
            }
        }

        // Get node for Children birthed in Active partnership

        IPartnershipExtended activePartnership = getActivePartnership(partnershipsFromYear);

        int childrenInActivePartnership = 0;

        if(activePartnership != null) {
            childrenInActivePartnership = activePartnership.getChildren().size();
        }

        try {
            node = node.getChild(childrenInActivePartnership);
        } catch (IsLeafException | ChildNotFoundException e) {
            node = node.addChild(childrenInActivePartnership);
        }

        Integer childrenBirthedInYear = getChildrenBirthedInYear(activePartnership, y);

        // Get node for separated
        Boolean activePartnershipToSeparateWithNoFurtherChildren = null;
        if(childrenBirthedInYear != 0) {
            activePartnershipToSeparateWithNoFurtherChildren = toSeparate(activePartnership, y);
        }

        try {
            node = node.getChild(activePartnershipToSeparateWithNoFurtherChildren);
        } catch (IsLeafException | ChildNotFoundException e) {
            node = node.addChild(activePartnershipToSeparateWithNoFurtherChildren);
        }

        // Get node for new partner age
        Integer newPartnerAge = null;

        if(activePartnership != null && startedInYear(activePartnership, y)) {
            IPersonExtended partner = activePartnership.getPartnerOf(person);
            newPartnerAge = partner.ageOnDate(activePartnership.getPartnershipDate());
        }

        try {
            node = node.getChild(newPartnerAge);
        } catch (IsLeafException | ChildNotFoundException e) {
            node = node.addChild(newPartnerAge);
        }

        // Get node for children birthed in previous years with anyone
        Collection<IPartnershipExtended> partnershipsInPastYears = person.getPartnerships_ex();
        Integer prevChildren = sumChildrenBirthedFromPrevYears(partnershipsInPastYears, y);

        try {
            node = node.getChild(prevChildren);
        } catch (IsLeafException | ChildNotFoundException e) {
            node = node.addChild(prevChildren);
        }

        // Get node for children birthed in year

        try {
            node = node.getChild(childrenBirthedInYear);
        } catch (IsLeafException | ChildNotFoundException e) {
            node = node.addChild(childrenBirthedInYear);
        }

        // Inc freq
        node.incrementCount(1);

    }

    private Integer sumChildrenBirthedFromPrevYears(Collection<IPartnershipExtended> partnershipsInPastYears, YearDate y) {

        int count = 0;

        for(IPartnershipExtended p : partnershipsInPastYears) {
            for(IPersonExtended c : p.getChildren()) {
                if(DateUtils.dateBefore(c.getBirthDate_ex(), y)) {
                    count ++;
                }
            }
        }

        return count;
    }

    private boolean startedInYear(IPartnershipExtended activePartnership, YearDate y) {

        Date startDate = activePartnership.getPartnershipDate();

        return !DateUtils.dateBefore(startDate, y) && DateUtils.dateBefore(startDate, y.advanceTime(1, TimeUnit.YEAR));
    }

    private Boolean toSeparate(IPartnershipExtended activePartnership, YearDate y) {

        if(activePartnership == null) {
            return null;
        }

        IPersonExtended lastChild = activePartnership.getLastChild();

        if (!lastChild.bornInYear(y)) {
            return false;
        } else if (activePartnership.getSeparationDate() != null) {
            return true;
        } else {
            return false;
        }

    }

    private Integer getChildrenBirthedInYear(IPartnershipExtended activePartnership, YearDate year) {

        if(activePartnership == null) {
            return 0;
        }

        Collection<IPersonExtended> children = activePartnership.getChildren();

        int c = 0;

        for(IPersonExtended child : children) {
            if(child.bornInYear(year)) {
                c++;
            }
        }

        return c;

    }

    private IPartnershipExtended getActivePartnership(Collection<IPartnershipExtended> partnershipsFromYear) {

        IPartnershipExtended latestPartnershipInYear = null;
        Date latestDate = new YearDate(Integer.MIN_VALUE);

        for(IPartnershipExtended p : partnershipsFromYear) {
            if(DateUtils.dateBefore(latestDate, p.getPartnershipDate())) {
                latestDate = p.getPartnershipDate();
                latestPartnershipInYear = p;
            }
        }

        return latestPartnershipInYear;

    }

    private Boolean convertSexToBoolean(char sex) {

        char c = Character.toUpperCase(sex);

        if(c == 'M') {
            return true;
        } else if(c == 'F') {
            return false;
        }

        // Incorrect sex assigned
        throw new Error();

    }

}
