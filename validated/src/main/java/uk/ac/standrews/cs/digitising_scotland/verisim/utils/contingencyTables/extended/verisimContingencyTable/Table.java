package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.ContingencyTable;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.RunnableNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes.DiedNodeDouble;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes.SeparationNodeDouble;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes.SourceNodeDouble;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.IntNodes.SourceNodeInt;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.SourceType;

import java.util.LinkedList;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Table extends Node<String, SourceType, Number, Number> implements ContingencyTable {

    private LinkedList<RunnableNode> deathTasks = new LinkedList<>();
    private LinkedList<RunnableNode> otherTasks = new LinkedList<>();

    private LinkedList<RunnableNode> delayedTasks = new LinkedList<>();



    private PopulationStatistics expected;

    private Date endDate;

    private SourceNodeInt simNode;
    private SourceNodeDouble statNode;

    public Table(PeopleCollection population, PopulationStatistics expected, Date startDate, Date endDate) {
        this.expected = expected;
        this.endDate = endDate;

//        for(IPersonExtended p : population.getAll()) {
//            processPerson(p, new YearDate(0), SourceType.SIM);
//
//            if(p.aliveOnDate(new ExactDate(31, 12, 1854))) {
//                processPerson(p, new YearDate(0), SourceType.STAT);
//            }
//
//        }

        YearDate prevY = new YearDate(startDate.getYear() - 1);
        for(IPersonExtended person : population.getPeople()) {
            if(person.aliveInYear(prevY)) {
                if (prevY.getYear() == startDate.getYear() - 1) {
                    processPerson(person, prevY, SourceType.STAT);
                }
            }

        }

        for (YearDate y = startDate.getYearDate(); DateUtils.dateBefore(y, endDate);
             y = y.advanceTime(1, TimeUnit.YEAR).getYearDate()) {

            // for every person in population
            for(IPersonExtended person : population.getPeople()) {

                // who was alive or died in the year of consideration
                if(person.aliveInYear(y)) {
                    processPerson(person, y, SourceType.SIM);
                }
            }
        }



        executeDelayedTasks();

        System.out.println("TREE MADE");

    }

    public Table() {

    }


    public Date getEndDate() {
        return endDate;
    }


    public PopulationStatistics getInputStats() {
        return expected;
    }

    public Node addChildA(SourceType childOption) {
        if(childOption == SourceType.SIM) {
            simNode = new SourceNodeInt(childOption, this);
            return simNode;
        } else {
            statNode = new SourceNodeDouble(childOption, this);
            return statNode;
        }

    }

    public Node getChild(SourceType option) throws ChildNotFoundException {
        if(option == SourceType.SIM) {
            if(simNode != null) {
                return simNode;
            } else {
                throw new ChildNotFoundException();
            }


        } else {
            if (statNode != null) {
                return statNode;
            } else {
                throw new ChildNotFoundException();
            }
        }
    }


    @Override
    public void addDelayedTask(RunnableNode node) {
//        delayedTasks.addLast(node);

        if(node instanceof DiedNodeDouble) {
            deathTasks.add(node);
        } else {
            otherTasks.add(node);
        }

    }

    @Override
    public void executeDelayedTasks() {

//        while(!delayedTasks.isEmpty()) {
//            RunnableNode n = delayedTasks.removeFirst();
//            System.out.println(n.toString());
//            n.runTask();
//        }


        while(!deathTasks.isEmpty()) {
            RunnableNode n = deathTasks.removeFirst();
            System.out.println(n.toString());
            n.runTask();
        }


        while(!otherTasks.isEmpty()) {
            RunnableNode n = otherTasks.removeFirst();
            System.out.println(n.toString());
            n.runTask();
        }

    }

    public void processPerson(IPersonExtended person, Date currentDate, SourceType source) {

        try {
            getChild(source).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChildA(source).processPerson(person, currentDate);
        }

    }

    @Override
    public Node<SourceType, ?, Number, ?> addChild(SourceType childOption) { return null; }

    @Override
    public Node<SourceType, ?, Number, ?> addChild(SourceType childOption, Number initCount) {
        return null;
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

    }

    @Override
    public Node getRootNode() {
        return null;
    }

    @Override
    public void incCount(Number byCount) {

    }

    @Override
    public void incCountByOne() {

    }
}
