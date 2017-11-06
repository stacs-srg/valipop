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
package uk.ac.standrews.cs.basic_model.population_representations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.ac.standrews.cs.basic_model.population_representations.adapted_interfaces.ILinkedPopulation;
import uk.ac.standrews.cs.basic_model.population_representations.adapted_interfaces.ILinkedChildbearingPartnership;
import uk.ac.standrews.cs.basic_model.population_representations.adapted_interfaces.ILinkedPerson;
import uk.ac.standrews.cs.basic_model.population_representations.data_structure.ChildbearingPartnership;
import uk.ac.standrews.cs.basic_model.population_representations.data_structure.LinkedPerson;
import uk.ac.standrews.cs.basic_model.population_representations.data_structure.MarriageBridge;
import uk.ac.standrews.cs.basic_model.population_representations.data_structure.SiblingBridge;
import uk.ac.standrews.cs.utilities.ArrayManipulation;

/**
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 *
 */
public class LinkedPopulation implements ILinkedPopulation {

	private List<LinkedPerson> livingPeople = new ArrayList<LinkedPerson>();
	private List<ChildbearingPartnership> partnerships = new ArrayList<ChildbearingPartnership>();
	private List<MarriageBridge> marriagePartnerships = new ArrayList<MarriageBridge>();
	private List<SiblingBridge> siblingsBridges = new ArrayList<SiblingBridge>();

	String description;

	public LinkedPopulation(String description) {
		this.description = description;
	}

	/*
	 * Interface methods
	 */

	@Override
	public Iterable<ILinkedPerson> getPeople() {
		return new Iterable<ILinkedPerson>() {
			@Override
			public Iterator<ILinkedPerson> iterator() {


				final Iterator<LinkedPerson> iterator = livingPeople.iterator();

				return new Iterator<ILinkedPerson>() {

					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}

					@Override
					public ILinkedPerson next() {
						return (ILinkedPerson) iterator.next();
					}

					@Override
					public void remove() {
						iterator.remove();
					}
				};
			}

		};
	}

	@Override
	public Iterable<ILinkedChildbearingPartnership> getPartnerships() {
		return new Iterable<ILinkedChildbearingPartnership>() {
			@Override
			public Iterator<ILinkedChildbearingPartnership> iterator() {
				final Iterator<ChildbearingPartnership> iterator = partnerships.iterator();

				return new Iterator<ILinkedChildbearingPartnership>() {
					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}

					@Override
					public ILinkedChildbearingPartnership next() {
						return iterator.next();
					}

					@Override
					public void remove() {
						iterator.remove();
					}
				};
			}
		};
	}

	@Override
	public ILinkedPerson findPerson(final int id) {

		for (LinkedPerson person : livingPeople) {
			if (person.getId() == id) {
				return person;

			}
		}
		return null;
	}


	@Override
	public ILinkedChildbearingPartnership findPartnership(final int id) {

		final int index = ArrayManipulation.binarySplit(partnerships, new ArrayManipulation.SplitComparator<ChildbearingPartnership>() {

			@Override
			public int check(final ChildbearingPartnership partnership) {
				return id - partnership.getId();
			}
		});

		return index >= 0 ? partnerships.get(index) : null;
	}

	@Override
	public int getNumberOfPeople() {
		return livingPeople.size();
	}

	@Override
	public int getNumberOfPartnerships() {
		return partnerships.size();
	}

	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public void setConsistentAcrossIterations(final boolean consistent_across_iterations) {

	}

	public int getNumberOfMarriagePartnerships() {
		return marriagePartnerships.size();
	}

	public void addPerson(LinkedPerson linkedPerson) {
		livingPeople.add(linkedPerson);		
	}

	public void addPartnership(ChildbearingPartnership linkedPartnership) {
		partnerships.add(linkedPartnership);
	}

	public void addMarraigePartnership(MarriageBridge linkedMarriagePartnership) {
		marriagePartnerships.add(linkedMarriagePartnership);
	}

	public void addSiblingsObject(SiblingBridge linkedSiblingsObject) {
		siblingsBridges.add(linkedSiblingsObject);
	}

	public LinkedPerson findPersonByFirstName(final String name) {

		for (LinkedPerson person : livingPeople) {
			if (person.getFirstName().equals(name)) {
				return person;
			}
		}
		return null;
	}

	public ChildbearingPartnership getPartnershipByRef(String ref) {

		for (ChildbearingPartnership partnership : partnerships) {
			if (partnership.getRef().equals(ref)) {
				return partnership;
			}
		}
		return null;
	}

	public MarriageBridge getMarraigePartnershipByRef(String ref) {

		for (MarriageBridge partnership : marriagePartnerships) {
			if (partnership.getRef().equals(ref)) {
				return partnership;
			}
		}
		return null;
	}

	public SiblingBridge getSiblingsObjectByRef(String ref) {

		for (SiblingBridge siblings : siblingsBridges) {
			if (siblings.getRef().equals(ref)) {
				return siblings;
			}
		}
		return null;
	}

	public int getNumberOfSiblingBridges() {
		return siblingsBridges.size();
	}

}
