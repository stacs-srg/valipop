/*
 * record_classification - Automatic record attribute classification.
 * Copyright © 2014-2016 Digitising Scotland project
 * (http://digitisingscotland.cs.st-andrews.ac.uk/)
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
package uk.ac.standrews.cs.digitising_scotland.record_classification.util;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Generates a {@link Stream stream} of the combinations for a collection of elements.
 *
 * @param <Element> the element type
 * @author Masih Hajiarab Derkani
 */
public interface CombinationGenerator<Element> extends Function<Collection<Element>, Stream<List<Element>>> {}
