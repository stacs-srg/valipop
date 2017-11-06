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
package uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.exceptions;

import uk.ac.standrews.cs.valipop.simulationEntities.person.IPersonExtended;

import java.util.Collection;

/**
 * The {@link InsufficientNumberOfPeopleException} is thrown when there is not enough people to meet a request made of
 * a PersonCollection data structure.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class InsufficientNumberOfPeopleException extends Exception {

    private final String message;

    /**
     * @param message the message
     */
    public InsufficientNumberOfPeopleException(String message) {
        this.message = message;
    }


    public InsufficientNumberOfPeopleException(String message, Collection<IPersonExtended> chosen) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
