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
package uk.ac.standrews.cs.valipop.exporting.gedcom;

import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.Family;
import org.gedcom4j.model.Individual;
import org.gedcom4j.parser.GedcomParser;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.IPersonCollection;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides abstract interface to a population represented in a GEDCOM file.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class GEDCOMPopulationAdapter implements IPersonCollection {

    private String description;

    private final List<IPerson> people;
    private final List<IPartnership> partnerships;

    /**
     * Initialises the adapter for a given GEDCOM file.
     *
     * @param path the path of the GEDCOM file
     * @throws IOException           if the file cannot be accessed
     * @throws GedcomParserException if the GEDCOM file is not well formed
     */
    public GEDCOMPopulationAdapter(final Path path) throws IOException, GedcomParserException {

        final GedcomParser parser = new GedcomParser();
        parser.load(path.toString());

        people = parser.getGedcom().getIndividuals().values().stream().
            map(individual -> new GEDCOMPerson(individual, this)).
            map(person -> (IPerson) person).
            sorted().
            collect(Collectors.toList());

        partnerships = parser.getGedcom().getFamilies().values().stream().
            filter(family -> family.getHusband() != null).
            filter(family -> family.getWife() != null).
            map(family -> new GEDCOMPartnership(family, this)).
            map(partnership -> (IPartnership) partnership).
            sorted().
            collect(Collectors.toList());
    }

    @Override
    public Iterable<IPerson> getPeople() {

        return people;
    }

    @Override
    public Iterable<IPartnership> getPartnerships() {

        return partnerships;
    }

    @Override
    public IPerson findPerson(final int id) {

        for (final IPerson person : getPeople())
            if (person.getId() == id)
                return person;

        return null;
    }

    @Override
    public IPartnership findPartnership(final int id) {

        if (id == -1) return null;

        for (final IPartnership partnership : getPartnerships())
            if (partnership.getId() == id)
                return partnership;

        return null;
    }

    @Override
    public int getNumberOfPeople() {

        return people.size();
    }

    @Override
    public int getNumberOfPartnerships() {

        return partnerships.size();
    }

    @Override
    public LocalDate getStartDate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public LocalDate getEndDate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDescription(final String description) {

        this.description = description;
    }

    @Override
    public Config getConfig() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {

        return description;
    }
}
