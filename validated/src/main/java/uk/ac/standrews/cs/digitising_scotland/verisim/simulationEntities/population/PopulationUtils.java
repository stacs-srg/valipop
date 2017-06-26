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
package uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population;


import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PopulationUtils {

//    Collection<IPersonExtended> getByYear(Date year);

//    Collection<IPersonExtended> getByYearAndSex(char sex, Date year);

    Collection<IPersonExtended> forceGetAllPersonsByTimePeriod(AdvancableDate firstDate, CompoundTimeUnit timePeriod);

    Collection<IPersonExtended> forceGetAllPersonsByTimePeriodAndSex(AdvancableDate firstDate, CompoundTimeUnit timePeriod, char sex);

}
