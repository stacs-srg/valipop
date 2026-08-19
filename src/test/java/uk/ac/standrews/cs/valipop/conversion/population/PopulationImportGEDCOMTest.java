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
package uk.ac.standrews.cs.valipop.conversion.population;

import org.junit.jupiter.params.provider.Arguments;
import uk.ac.standrews.cs.valipop.population.PopulationPropertiesTest;

import java.nio.file.Path;
import java.util.List;

/**
 * These tests check that when various populations are imported from GEDCOM format, then the imported
 * population has the expected properties.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class PopulationImportGEDCOMTest extends PopulationPropertiesTest {

    private static final Path TEST_RESOURCE_DIR = Path.of("src/test/resources/valipop/conversion/population/import");

    // For future ValiPop development, see also:
    // https://web.archive.org/web/20231029182316/https://gedcomassessment.com/en/index.htm
    // which links to the test file:
    // https://web.archive.org/web/20230327132350/https://www.gedcomassessment.com/en/assess.ged
    //
    // This requires more complex GEDCOM processing than currently performed by ValiPop.

    private static final List<Arguments> configurations = List.of(
        Arguments.of(TEST_RESOURCE_DIR.resolve("kennedy.ged"))
    );

    private static final List<Arguments> slowConfigurations = List.of(
        Arguments.of((Object) null)
    );
}
