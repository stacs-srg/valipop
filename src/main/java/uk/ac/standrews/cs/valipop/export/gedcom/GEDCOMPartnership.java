/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
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
package uk.ac.standrews.cs.valipop.export.gedcom;

import org.apache.commons.math3.random.RandomGenerator;
import org.gedcom4j.model.Family;
import org.gedcom4j.model.FamilyEvent;
import org.gedcom4j.model.FamilyEventType;
import org.gedcom4j.model.Individual;
import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;

import javax.annotation.Nonnull;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Partnership implementation for a population represented in a GEDCOM file.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk>
 */
public class GEDCOMPartnership implements IPartnership {

    protected int id;
    private int male_partner_id;
    private int female_partner_id;
    protected LocalDate marriage_date;
    private String marriage_place;
    protected List<Integer> child_ids;

    public int getId() {
        return id;
    }

    public int getPartnerOf(final int id) {
        return id == male_partner_id ? female_partner_id : id == female_partner_id ? male_partner_id : -1;
    }

    public String getMarriagePlace() {
        return marriage_place;
    }

    @SuppressWarnings("CompareToUsesNonFinalVariable")
    public int compareTo(@Nonnull final IPartnership other) {

        return Integer.compare(id, other.getId());
    }

    public boolean equals(final Object other) {
        return other instanceof IPartnership && compareTo((IPartnership) other) == 0;
    }

    @SuppressWarnings("NonFinalFieldReferencedInHashCode")
    public int hashCode() {
        return id;
    }

    /**
     * Initialises the partnership.
     *
     * @param family the GEDCOM family representation
     * @throws ParseException if the marriage date is incorrectly formatted
     */
    GEDCOMPartnership(final Family family) throws ParseException {

        id = getId(family.xref);
        male_partner_id = getId(family.husband.xref);
        female_partner_id = getId(family.wife.xref);

        setMarriage(family);
        setChildren(family);
    }

    private static int getId(final String reference) {

        return GEDCOMPopulationWriter.idToInt(reference);
    }

    private void setMarriage(final Family family) throws ParseException {

        for (final FamilyEvent event : family.events) {

            if (event.type == FamilyEventType.MARRIAGE) {
                marriage_date = LocalDate.parse(event.date.toString());
                marriage_place = event.place.placeName;
            }
        }
    }

    private void setChildren(final Family family) {

        child_ids = new ArrayList<>();
        for (final Individual child : family.children) {
            child_ids.add(GEDCOMPopulationWriter.idToInt(child.xref));
        }
    }

    public IPerson getFemalePartner() {
        return null;
    }

    public IPerson getMalePartner() {
        return null;
    }

    public IPerson getPartnerOf(IPerson person) {
        return null;
    }

    public List<IPerson> getChildren() {
        return null;
    }

    public LocalDate getPartnershipDate() {
        return null;
    }

    public LocalDate getSeparationDate(RandomGenerator randomGenerator) {
        return null;
    }

    public LocalDate getEarliestPossibleSeparationDate() {
        return null;
    }

    @Override
    public void setEarliestPossibleSeparationDate(LocalDate date) {

    }

    public void setMarriageDate(LocalDate marriageDate) {

    }

    @Override
    public LocalDate getMarriageDate() {
        return null;
    }

    @Override
    public void addChildren(Collection<IPerson> children) {

    }

    @Override
    public void setPartnershipDate(LocalDate startDate) {

    }
}
