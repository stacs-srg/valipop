/*
 * valipop - <https://github.com/stacs-srg/valipop>
 * Copyright © 2026 Systems Research Group, University of St Andrews (graham.kirby@st-andrews.ac.uk)
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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * These tests check that when various populations are generated, and exported in Graphviz format, then the file contains the expected content.
 *
 * @author Daniel Brathagen (dbrathagen@gmail.com)
 * @author Graham Kirby
 */
public class PopulationExportGraphvizTest extends PopulationExportTest {

    // Files can be checked for validity at: https://magjac.com/graphviz-visual-editor/

    private static final List<Arguments> configurations = List.of(
        Arguments.of("1855-2016-initial-200-graphviz.config"),
        Arguments.of("1855-2016-initial-300-graphviz.config")
    );

    private static final List<Arguments> slowConfigurations = List.of(
        Arguments.of("1855-2016-initial-1K-graphviz.config"),
        Arguments.of("1855-2016-initial-5K-graphviz.config")
    );

    @ParameterizedTest
    @FieldSource("configurations")
    public void populationExportedAsExpected(final String configPath) throws IOException, NoSuchAlgorithmException {

        checkPopulationExportedAsExpected(configPath);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void populationExportedAsExpectedSlow(final String configPath) throws IOException, NoSuchAlgorithmException {

        checkPopulationExportedAsExpected(configPath);
    }
}
