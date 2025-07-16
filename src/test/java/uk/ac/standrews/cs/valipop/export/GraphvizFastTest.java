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
package uk.ac.standrews.cs.valipop.export;

import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.ac.standrews.cs.valipop.simulationEntities.IPersonCollection;

import java.util.List;

import static uk.ac.standrews.cs.valipop.config.TestCases.FAST_TEST_CASE_INITIAL_POPULATION_SIZES_FOR_GRAPHVIZ_TESTS;
import static uk.ac.standrews.cs.valipop.config.TestCases.getTestConfigurations;

/**
 * E2E tests of Graphviz export.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
@ParameterizedClass
@MethodSource("getTestCases")
public class GraphvizFastTest extends GraphvizTest {

    public GraphvizFastTest(final IPersonCollection population) {

        super(population);
    }

    static List<Arguments> getTestCases()  {

        return getTestConfigurations(FAST_TEST_CASE_INITIAL_POPULATION_SIZES_FOR_GRAPHVIZ_TESTS);
    }
}
