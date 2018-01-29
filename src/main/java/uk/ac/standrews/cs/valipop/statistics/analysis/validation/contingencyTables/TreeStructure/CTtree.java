/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure;

import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.DoubleNodes.*;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SourceType;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.Logger;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.IntNodes.SourceNodeInt;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.ContingencyTable;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.RunnableNode;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CTtree extends Node<String, SourceType, Number, Number> implements ContingencyTable {

    public static final Logger log = new Logger(CTtree.class);

    private LinkedList<RunnableNode> deathTasks = new LinkedList<>();
    private LinkedList<RunnableNode> ageTasks = new LinkedList<>();
    private LinkedList<RunnableNode> nciyTasks = new LinkedList<>();
    private LinkedList<RunnableNode> nciapTasks = new LinkedList<>();
    private LinkedList<RunnableNode> sepTasks = new LinkedList<>();

    public static final double NODE_MIN_COUNT = 1E-66;

    private PopulationStatistics expected;

    private Date endDate;
    private Date startDate;

    private SourceNodeInt simNode;
    private static SourceNodeDouble statNode = null;


    private static boolean reuseExpValues = false;

    public static void reuseExpectedValues(boolean reuse) {
        reuseExpValues = reuse;

        clearStatNodeIfNessersary();

    }

    public static void clearStatNodeIfNessersary() {
        if(!reuseExpValues) {
            statNode = null;
        }
    }

    public CTtree(PeopleCollection population, PopulationStatistics expected, Date startDate, Date endDate, int startStepBack) {
        this.expected = expected;
        this.startDate = startDate;
        this.endDate = new YearDate(endDate.getYear() - 2);


        log.info("CTree --- Check for reuse of expected values : " + reuseExpValues);

        if(statNode == null || !reuseExpValues) {
            log.info("CTree --- Populating expected side of tree with seed");
            YearDate prevY = new YearDate(startDate.getYear() - startStepBack);
            for (IPersonExtended person : population.getPeople_ex()) {
                if (person.aliveInYear(prevY)) {
                    processPerson(person, prevY, SourceType.STAT);
                }
            }

            executeDelayedTasks();

            removeInitPop();
        }


        log.info("CTree --- Populating tree with observed population");
        for (YearDate y = startDate.getYearDate(); DateUtils.dateBefore(y, endDate);
             y = y.advanceTime(1, TimeUnit.YEAR).getYearDate()) {

            // for every person in population
            for (IPersonExtended person : population.getPeople_ex()) {

                // who was alive or died in the year of consideration
                if (person.bornInYear(y) && person.diedInYear(y)
                        ||
                        person.aliveInYear(y) && !person.bornInYear(y)
                        ||
                        person.bornOnDate(y)) {

                    processPerson(person, y, SourceType.SIM);
                }
            }
        }




        log.info("CTree --- Tree completed");

    }

    private void removeInitPop() {



    }

    public CTtree() {

    }

    public Collection<Node> getLeafNodes() {

        Collection<Node> childNodes = new ArrayList<>();

        childNodes.addAll(simNode.getLeafNodes());
        childNodes.addAll(statNode.getLeafNodes());

        return childNodes;

    }

    @Override
    public String getVariableName() {
        return null;
    }


    public Date getEndDate() {
        return endDate;
    }

    public Date getStartDate() {
        return startDate;
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

        if(node instanceof DiedNodeDouble) {
            deathTasks.add(node);
        } else if(node instanceof AgeNodeDouble) {
            ageTasks.add(node);
        } else if(node instanceof NumberOfChildrenInYearNodeDouble) {
            nciyTasks.add(node);
        } else if(node instanceof NumberOfPreviousChildrenInAnyPartnershipNodeDouble) {
            nciapTasks.add(node);
        } else if(node instanceof SeparationNodeDouble) {
            sepTasks.add(node);
        }

    }

    @Override
    public void executeDelayedTasks() {

        log.info("CTree --- Initialising tree - death nodes from seed");

        while(!deathTasks.isEmpty()) {
            RunnableNode n = deathTasks.removeFirst();

            n.runTask();
        }

        while(nciyTasks.size() + sepTasks.size() + nciapTasks.size() + ageTasks.size() != 0) {

            while (nciyTasks.size() + sepTasks.size() + nciapTasks.size() != 0) {

                while (!sepTasks.isEmpty()) {
                    RunnableNode n = sepTasks.removeFirst();
                    n.runTask();
                }

                while (!nciapTasks.isEmpty()) {
                    RunnableNode n = nciapTasks.removeFirst();
                    n.runTask();
                }

            }

            int i = 0;

            for( ; i < 2; i ++) {
                if(ageTasks.isEmpty()) {
                    break;
                }
                RunnableNode n = ageTasks.removeFirst();
                AgeNodeDouble a = (AgeNodeDouble) n;
                YOBNodeDouble y = (YOBNodeDouble) a.getAncestor(new YOBNodeDouble());
                log.info("CTree --- Creating nodes for year: " + y.getOption().toString());
                n.runTask();
            }

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
