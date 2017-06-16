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
package uk.ac.standrews.cs.digitising_scotland.verisim.utils;

import java.util.Objects;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ProcessArgs {

    public static String[] process(String[] args) {

        String[] processed = new String[3];

        try {
            processed[0] = args[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("No config file given as 1st arg");
        }

        try {
            processed[1] = args[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("No results write path given as 2nd arg");
        }

        try {
            processed[2] = args[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            processed[2] = "unstated";
        }

        return processed;

    }

    public static boolean check(String[] args) {

        return args.length == 3 && !Objects.equals(args[0], "") && !Objects.equals(args[1], "");

    }

}
