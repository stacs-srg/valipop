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

import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.PopulationNavigation;
import uk.ac.standrews.cs.valipop.simulationEntities.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.DoubleNodes.*;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.IntNodes.SourceNodeInt;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.ContingencyTable;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.RunnableNode;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SourceType;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.Logger;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

    public static double NODE_MIN_COUNT = 1E-66;

    private PopulationStatistics expected;

    private LocalDate endDate;
    private LocalDate startDate;

    private SourceNodeInt simNode;
    private SourceNodeDouble statNode = null;

    public CTtree(PeopleCollection population, PopulationStatistics expected, LocalDate startDate, LocalDate zeroDate, LocalDate endDate, int startStepBack) {

        this.expected = expected;
        this.startDate = startDate;
        this.endDate = endDate.minus(2, ChronoUnit.YEARS);

        LocalDate prevY = zeroDate.minus(startStepBack, ChronoUnit.YEARS);

        log.info("CTree --- Populating tree with observed population");

        for (LocalDate y = startDate; y.isBefore(endDate); y = y.plus(1, ChronoUnit.YEARS)) {

            LocalDate prevDay = LocalDate.of(y.getYear() - 1, 12, 31);

            // for every person in population
            for (IPerson person : population.getPeople()) {

                if (prevY.getYear() == y.getYear() && PopulationNavigation.aliveOnDate(person, prevDay)) {

                    processPerson(person, y, SourceType.STAT);
                    processPerson(person, y, SourceType.SIM);
                }

                if (prevY.getYear() < y.getYear() && PopulationNavigation.aliveOnDate(person, prevDay)) {

                    processPerson(person, y, SourceType.SIM);
                }
            }
        }

        executeDelayedTasks();

        log.info("CTree --- Tree completed");
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

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public PopulationStatistics getInputStats() {
        return expected;
    }

    public Node addChildA(SourceType childOption) {

        if (childOption == SourceType.SIM) {
            simNode = new SourceNodeInt(childOption, this);
            return simNode;
        } else {
            statNode = new SourceNodeDouble(childOption, this);
            return statNode;
        }
    }

    public Node getChild(SourceType option) throws ChildNotFoundException {

        if (option == SourceType.SIM) {
            if (simNode != null) {
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

        if (node instanceof DiedNodeDouble) {
            deathTasks.add(node);
        } else if (node instanceof AgeNodeDouble) {
            ageTasks.add(node);
        } else if (node instanceof NumberOfChildrenInYearNodeDouble) {
            nciyTasks.add(node);
        } else if (node instanceof NumberOfPreviousChildrenInAnyPartnershipNodeDouble) {
            nciapTasks.add(node);
        } else if (node instanceof SeparationNodeDouble) {
            sepTasks.add(node);
        }
    }

    @Override
    public void executeDelayedTasks() {

        log.info("CTree --- Initialising tree - death nodes from seed");

        while (!deathTasks.isEmpty()) {
            RunnableNode n = deathTasks.removeFirst();

            n.runTask();
        }

        while (nciyTasks.size() + sepTasks.size() + nciapTasks.size() + ageTasks.size() != 0) {

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

            for (int i = 0; i < 2; i++) {
                if (ageTasks.isEmpty()) {
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

    public void processPerson(IPerson person, LocalDate currentDate, SourceType source) {

        try {
            getChild(source).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChildA(source).processPerson(person, currentDate);
        }
    }

    @Override
    public Node<SourceType, ?, Number, ?> addChild(SourceType childOption) {
        return null;
    }

    @Override
    public Node<SourceType, ?, Number, ?> addChild(SourceType childOption, Number initCount) {
        return null;
    }

    @Override
    public void processPerson(IPerson person, LocalDate currentDate) {

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
