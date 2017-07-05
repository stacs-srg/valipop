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
package uk.ac.standrews.cs.digitising_scotland.verisim.events;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public enum EventType {

    FIRST_BIRTH,
    SECOND_BIRTH,
    THIRD_BIRTH,
    FOURTH_BIRTH,
    FIFTH_BIRTH,
    MALE_DEATH,
    FEMALE_DEATH,
    SEPARATION,
    PARTNERING,
    CT

}
