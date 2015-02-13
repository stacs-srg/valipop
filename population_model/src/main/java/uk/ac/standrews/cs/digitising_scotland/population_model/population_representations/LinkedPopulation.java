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
package uk.ac.standrews.cs.digitising_scotland.population_model.population_representations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.adapted_interfaces.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.adapted_interfaces.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.adapted_interfaces.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.util.ArrayManipulation;

public class LinkedPopulation implements IPopulation {

	private List<LinkedPerson> livingPeople = new ArrayList<LinkedPerson>();
	private List<LinkedChildbearingPartnership> partnerships = new ArrayList<LinkedChildbearingPartnership>();
	private List<LinkedMarriagePartnership> marriagePartnerships = new ArrayList<LinkedMarriagePartnership>();
	private List<LinkedSiblings> siblingsObjects = new ArrayList<LinkedSiblings>();

	String description;



	public LinkedPopulation(String description) {
		this.description = description;
	}


	/*
	 * Interface methods
	 */

	@Override
	public Iterable<IPerson> getPeople() {
		return new Iterable<IPerson>() {
			@Override
			public Iterator<IPerson> iterator() {


				final Iterator<LinkedPerson> iterator = livingPeople.iterator();

				return new Iterator<IPerson>() {

					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}

					@Override
					public IPerson next() {
						return (IPerson) iterator.next();
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
	public Iterable<IPartnership> getPartnerships() {
		return new Iterable<IPartnership>() {
			@Override
			public Iterator<IPartnership> iterator() {
				final Iterator<LinkedChildbearingPartnership> iterator = partnerships.iterator();

				return new Iterator<IPartnership>() {
					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}

					@Override
					public IPartnership next() {
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
	public IPerson findPerson(final int id) {

		for (LinkedPerson person : livingPeople) {
			if (person.getId() == id) {
				return person;

			}
		}
		return null;
	}


	@Override
	public IPartnership findPartnership(final int id) {

		final int index = ArrayManipulation.binarySplit(partnerships, new ArrayManipulation.SplitComparator<LinkedChildbearingPartnership>() {

			@Override
			public int check(final LinkedChildbearingPartnership partnership) {
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


	public void addPerson(LinkedPerson linkedPerson) {
		livingPeople.add(linkedPerson);		
	}

	public void addPartnership(LinkedChildbearingPartnership linkedPartnership) {
		partnerships.add(linkedPartnership);
	}

	public void addMarraigePartnership(LinkedMarriagePartnership linkedMarriagePartnership) {
		marriagePartnerships.add(linkedMarriagePartnership);
	}

	public void addSiblingsObject(LinkedSiblings linkedSiblingsObject) {
		siblingsObjects.add(linkedSiblingsObject);
	}

	public LinkedPerson findPersonByFirstName(final String name) {

		for (LinkedPerson person : livingPeople) {
			if (person.getFirstName().equals(name)) {
				return person;
			}
		}
		return null;
	}

	public LinkedChildbearingPartnership getPartnershipByRef(String ref) {

		for (LinkedChildbearingPartnership partnership : partnerships) {
			if (partnership.getRef().equals(ref)) {
				return partnership;
			}
		}
		return null;
	}
	
	public LinkedMarriagePartnership getMarraigePartnershipByRef(String ref) {

		for (LinkedMarriagePartnership partnership : marriagePartnerships) {
			if (partnership.getRef().equals(ref)) {
				return partnership;
			}
		}
		return null;
	}
	
	public LinkedSiblings getSiblingsObjectByRef(String ref) {

		for (LinkedSiblings siblings : siblingsObjects) {
			if (siblings.getRef().equals(ref)) {
				return siblings;
			}
		}
		return null;
	}

}
