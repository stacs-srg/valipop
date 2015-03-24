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
package uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.queries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.Link;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.LinkedChildbearingPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.LinkedPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.LinkedPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.LinkedSiblings;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.QueryType;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.ResultObject;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.UseCases;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.Utils;

public class PopulationQueries {

	LinkedPopulation population;

	public static void main(String[] args) {
		LinkedPopulation pop = UseCases.generateNuclearFamilyUseCase1();
		PopulationQueries pq = new PopulationQueries(pop);

		//		Utils.printResultSet(pq.getChildrenOf(3));
		//		Utils.printResultSet(pq.getChildrenOf(4));
		//		Utils.printResultSet(pq.getChildrenOf(5));
		//
		//		Utils.printResultSet(pq.getFatherOf(0));
		//		Utils.printResultSet(pq.getFatherOf(1));
		//		Utils.printResultSet(pq.getFatherOf(2));
		//		
		//		Utils.printResultSet(pq.getMotherOf(0));
		//		Utils.printResultSet(pq.getMotherOf(1));
		//		Utils.printResultSet(pq.getMotherOf(2));
		//		
		//		Utils.printResultSet(pq.getPartnerOf(4));

		Utils.printResultSet(pq.getPotentialFatherSideSiblingsOf(0));
		Utils.printResultSet(pq.getPotentialMotherSideSiblingsOf(0));
		//		Utils.printResultSet(pq.getPotentialFullSiblings(0));
	}

	public PopulationQueries(LinkedPopulation population) {
		this.population = population;		
	}

	// Need to think about ways of paring parents into probably sets?
	public ResultObject[] getFatherOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);

		PriorityQueue<ResultObject> fathers = new PriorityQueue<ResultObject>();

		Link[] pFL = p.getParentsPartnership().getLinkedIntermediaryObject().getPerson1PotentialPartnerLinks();
		for(Link l : pFL) {
			fathers.add(new ResultObject(QueryType.FATHERS, p.getParentsPartnership(), l));
		}

		return Utils.orderResults(fathers);
	}

	public ResultObject[] getMotherOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);

		PriorityQueue<ResultObject> mothers = new PriorityQueue<ResultObject>();

		Link[] pFL = p.getParentsPartnership().getLinkedIntermediaryObject().getPerson2PotentialPartnerLinks();
		for(Link l : pFL) {
			mothers.add(new ResultObject(QueryType.MOTHERS, p.getParentsPartnership(), l));
		}

		return Utils.orderResults(mothers);
	}

	public ResultObject[] getPartnerOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		PriorityQueue<ResultObject> partners = new PriorityQueue<ResultObject>();

		List<Link> possPartnership = p.getPartnerships();
		for(Link l : possPartnership) {
			if(p.getSex() == 'M') {
				for(Link l2 : l.getLinkedIntermediaryObject().getPerson2PotentialPartnerLinks()) {
					partners.add(new ResultObject(QueryType.CB_PARTNERS, l, l2));
				}
			} else if(p.getSex() == 'F') {
				for(Link l2 : l.getLinkedIntermediaryObject().getPerson1PotentialPartnerLinks()) {
					partners.add(new ResultObject(QueryType.CB_PARTNERS, l, l2));
				}
			}
		}

		return Utils.orderResults(partners);
	}

	// Need to think about way of grouping children into possible sets?
	public ResultObject[] getChildrenOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		PriorityQueue<ResultObject> children = new PriorityQueue<ResultObject>();
		List<Link> possPartnership = p.getPartnerships();
		for(Link l : possPartnership) {
			children.add(new ResultObject(QueryType.CHILDREN, l, ((LinkedChildbearingPartnership) l.getLinkedIntermediaryObject()).getChildLink()));
		}
		return Utils.orderResults(children);
	}

	/*
	 * This method considers the sibling found when the given persons father's offsping are considered - sibling bridges are only used in
	 * this method to further support a sibling identified from the fathers offspring.
	 * It makes little sense to include other sibling bridges here as it isn't possible to distinguish if they are siblings by the father or the mother only
	 */
	public ResultObject[] getPotentialXSideSiblingsOf(LinkedPerson person, Link[] x) {
		Link parentsPartnership = person.getParentsPartnership();
		PriorityQueue<ResultObject> siblings = new PriorityQueue<ResultObject>();
		for(Link l : x) {
			List<Link> partnerships = l.getLinkedPerson().getPartnerships();
			for(Link pL : partnerships) {
				Link childLink = ((LinkedChildbearingPartnership) pL.getLinkedIntermediaryObject()).getChildLink();
				Link[] intermidiaryLinks = new Link[]{l, pL};

				// Adding sibling link consideration here
				ArrayList<LinkedSiblings> supportingSiblingBridges = new ArrayList<LinkedSiblings>();

				for(Link l2 : person.getSiblings()) {
					for(Link pSL : l2.getLinkedIntermediaryObject().getOppositePersonsList(person)) {
						// TODO This is going to need a rethink - the complexity is getting concerning
						if (pSL.getLinkedPerson().getId() == childLink.getLinkedPerson().getId()) {
							supportingSiblingBridges.add((LinkedSiblings) l2.getLinkedIntermediaryObject());
						}
					}
				}

				if(childLink.getLinkedPerson().getId() != person.getId()) {
					ResultObject resultObject = new ResultObject(QueryType.FATHERS_SIDE_SIBLINGS, parentsPartnership, intermidiaryLinks, childLink);
					if(supportingSiblingBridges.size() != 0) {
						LinkedSiblings[] temp = supportingSiblingBridges.toArray(new LinkedSiblings[supportingSiblingBridges.size()]);
						Arrays.sort(temp);
						resultObject.setSupportingSiblingBridges(temp);
					}
					siblings.add(resultObject);
				}
			}
		}

		return Utils.orderResults(siblings);
	}
	
	public ResultObject[] getPotentialFatherSideSiblingsOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		return getPotentialXSideSiblingsOf(p, p.getParentsPartnership().getLinkedIntermediaryObject().getPerson1PotentialPartnerLinks());
	}
		
	public ResultObject[] getPotentialMotherSideSiblingsOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		return getPotentialXSideSiblingsOf(p, p.getParentsPartnership().getLinkedIntermediaryObject().getPerson2PotentialPartnerLinks());
	}

	public ResultObject[] getPotentialSiblingsByBridges(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		PriorityQueue<ResultObject> siblings = new PriorityQueue<ResultObject>();

		for(Link l : p.getSiblings()) {
			for(Link l2 : l.getLinkedIntermediaryObject().getOppositePersonsList(p)) {
				ResultObject resultObject = new ResultObject(QueryType.SIBLING_BRIGE, l, l2);
				resultObject.setSupportingSiblingBridges(new LinkedSiblings[]{(LinkedSiblings) l2.getLinkedIntermediaryObject()});
				siblings.add(resultObject);
			}
		}
		return Utils.orderResults(siblings);
	}

	public ResultObject[] getPotentialFullSiblings(int person) {
		ResultObject[] fSideSibling = getPotentialFatherSideSiblingsOf(person);
		ResultObject[] mSideSibling = getPotentialMotherSideSiblingsOf(person);
		PriorityQueue<ResultObject> potentialFullSiblings = new PriorityQueue<ResultObject>();

		for(ResultObject f : fSideSibling) {
			int fatherSideId = f.getBranchLink().getLinkedPerson().getId();
			for(ResultObject m : mSideSibling){
				if(fatherSideId == m.getBranchLink().getLinkedPerson().getId()) {
					ResultObject resultObject = new ResultObject(QueryType.FULL_SIBLINGS, f.getRootLink(), f.getIntermidiaryLinks1(), m.getIntermidiaryLinks1(), f.getBranchLink());
					resultObject.setSupportingSiblingBridges((LinkedSiblings[]) Utils.joinArrays(f.getSupportingSiblingBridges(), m.getSupportingSiblingBridges()));
					potentialFullSiblings.add(resultObject);

				}
			}
		}
		return Utils.orderResults(potentialFullSiblings);		

	}

}
