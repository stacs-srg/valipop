/*
 * valipop - <https://github.com/stacs-srg/valipop>
 * Copyright © 2025 Systems Research Group, University of St Andrews (graham.kirby@st-andrews.ac.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure;

import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.PopulationNavigation;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.DoubleNodes.*;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.IntNodes.SourceNodeInt;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ContingencyTree extends Node<String, SourceType, Number, Number> {

    public static final Logger log = Logger.getLogger(ContingencyTree.class.getName());

    private final List<RunnableNode> deathTasks = new LinkedList<>();
    private final List<RunnableNode> ageTasks = new LinkedList<>();
    private final List<RunnableNode> numberOfChildrenInYearTasks = new LinkedList<>();
    private final List<RunnableNode> numberOfPreviousChildrenTasks = new LinkedList<>();
    private final List<RunnableNode> separationTasks = new LinkedList<>();

    public static double NODE_MIN_COUNT = 1E-66;

    private PopulationStatistics expectedStatistics;

    private LocalDate endDate;
    private LocalDate startDate;

    private SourceNodeInt observedNode;
    private SourceNodeDouble expectedNode = null;

    public ContingencyTree(final Iterable<IPerson> population, final PopulationStatistics expectedStatistics, final LocalDate startDate, final LocalDate zeroDate, final LocalDate endDate, final int startStepBack, final double precision) {

        ContingencyTree.NODE_MIN_COUNT = precision;

        this.expectedStatistics = expectedStatistics;
        this.startDate = startDate;
        this.endDate = endDate.minusYears(1);

        log.info("CTree --- Populating tree with observed population");

        for (LocalDate year = startDate; year.isBefore(endDate.minusYears(1)); year = year.plusYears(1)) {

            final LocalDate firstYearToProcess = zeroDate.minusYears(startStepBack);
            final LocalDate lastDayOfPreviousYear = LocalDate.of(year.getYear() - 1, 12, 31);

            for (final IPerson person : population) {

                if (PopulationNavigation.aliveOnDate(person, lastDayOfPreviousYear) && PopulationNavigation.inCountryOnDate(person, lastDayOfPreviousYear)) {

                    if (year.getYear() == firstYearToProcess.getYear())
                        processPerson(person, year, SourceType.STAT);

                    if (year.getYear() >= firstYearToProcess.getYear())
                        processPerson(person, year, SourceType.SIM);
                }
            }
        }

        executeDelayedTasks();

        log.info("CTree --- Tree completed");
    }

    public ContingencyTree() {
    }

    @SuppressWarnings("rawtypes")
    public Collection<Node> getLeafNodes() {

        final Collection<Node> childNodes = new ArrayList<>();

        childNodes.addAll(observedNode.getLeafNodes());
        childNodes.addAll(expectedNode.getLeafNodes());

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
        return expectedStatistics;
    }

    @SuppressWarnings("rawtypes")
    private Node addChildA(final SourceType childOption) {

        if (childOption == SourceType.SIM) {
            observedNode = new SourceNodeInt(childOption, this);
            return observedNode;
        } else {
            expectedNode = new SourceNodeDouble(childOption, this);
            return expectedNode;
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Node getChild(final SourceType option) throws ChildNotFoundException {

        if (option == SourceType.SIM)
            if (observedNode != null) return observedNode;
            else throw new ChildNotFoundException();

        else
            if (expectedNode != null) return expectedNode;
            else throw new ChildNotFoundException();
    }

    @Override
    public void addDelayedTask(final RunnableNode node) {

        if (node instanceof DiedNodeDouble) {
            deathTasks.add(node);
        } else if (node instanceof AgeNodeDouble) {
            ageTasks.add(node);
        } else if (node instanceof NumberOfChildrenInYearNodeDouble) {
            numberOfChildrenInYearTasks.add(node);
        } else if (node instanceof NumberOfPreviousChildrenInAnyPartnershipNodeDouble) {
            numberOfPreviousChildrenTasks.add(node);
        } else if (node instanceof SeparationNodeDouble) {
            separationTasks.add(node);
        }
    }

    private void executeDelayedTasks() {

        log.info("CTree --- Initialising tree - death nodes from seed");

        while (!deathTasks.isEmpty()) {

            deathTasks.removeFirst().run();
        }

        while (numberOfChildrenInYearTasks.size() + separationTasks.size() + numberOfPreviousChildrenTasks.size() + ageTasks.size() > 0) {

            while (numberOfChildrenInYearTasks.size() + separationTasks.size() + numberOfPreviousChildrenTasks.size() > 0) {

                while (!separationTasks.isEmpty())
                    separationTasks.removeFirst().run();

                while (!numberOfPreviousChildrenTasks.isEmpty())
                    numberOfPreviousChildrenTasks.removeFirst().run();
            }

            for (int i = 0; i < 2; i++) {
                if (ageTasks.isEmpty()) break;

                final RunnableNode node = ageTasks.removeFirst();
                log.info("CTree --- Creating nodes for year: " + ((YOBNodeDouble) ((AgeNodeDouble) node).getAncestor(new YOBNodeDouble())).getOption());
                node.run();
            }
        }
    }

    public void processPerson(final IPerson person, final LocalDate currentDate, final SourceType source) {

        try {
            getChild(source).processPerson(person, currentDate);
        }
        catch (final ChildNotFoundException e) {
            addChildA(source).processPerson(person, currentDate);
        }
    }

    @Override
    public Node<SourceType, ?, Number, ?> addChild(final SourceType childOption) {
        return null;
    }

    @Override
    public Node<SourceType, ?, Number, ?> addChild(final SourceType childOption, final Number initCount) {
        return null;
    }

    @Override
    public void processPerson(final IPerson person, final LocalDate currentDate) {

    }

    @Override
    public void incCount(final Number byCount) {

    }

    @Override
    public void incCountByOne() {

    }
}
