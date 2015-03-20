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

import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.Link;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.LinkedChildbearingPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.LinkedPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.LinkedPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.QueryType;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.ResultObject;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.UseCases;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.Utils;

public class PopulationQueries {

	LinkedPopulation population;

	public static void main(String[] args) {
		LinkedPopulation pop = UseCases.generateNuclearFamilyUseCase();
		PopulationQueries pq = new PopulationQueries(pop);

		Utils.printResultSet(pq.getChildrenOf(3));
		Utils.printResultSet(pq.getChildrenOf(4));
		Utils.printResultSet(pq.getChildrenOf(5));

		Utils.printResultSet(pq.getFatherOf(0));
		Utils.printResultSet(pq.getFatherOf(1));
		Utils.printResultSet(pq.getFatherOf(2));
		
		Utils.printResultSet(pq.getMotherOf(0));
		Utils.printResultSet(pq.getMotherOf(1));
		Utils.printResultSet(pq.getMotherOf(2));
		
		Utils.printResultSet(pq.getPartnerOf(4));

		Utils.printResultSet(pq.getPotentialFatherSideSiblingsOf(0));
		Utils.printResultSet(pq.getPotentialMotherSideSiblingsOf(0));
		Utils.printResultSet(pq.getPotentialFullSiblings(0));
	}
	
	public PopulationQueries(LinkedPopulation population) {
		this.population = population;		
	}

	// Need to think about ways of paring parents into probably sets?
	public ResultObject[] getFatherOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		
		PriorityQueue<ResultObject> fathers = new PriorityQueue<ResultObject>();
		
		Link[] pFL = p.getParentsPartnership().getLinkedPartnership().getPerson1PotentialPartnerLinks();
		for(Link l : pFL) {
			fathers.add(new ResultObject(QueryType.FATHERS, p.getParentsPartnership(), l));
		}
		
		return orderResults(fathers);
	}
	
	public ResultObject[] getMotherOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		
		PriorityQueue<ResultObject> mothers = new PriorityQueue<ResultObject>();
		
		Link[] pFL = p.getParentsPartnership().getLinkedPartnership().getPerson2PotentialPartnerLinks();
		for(Link l : pFL) {
			mothers.add(new ResultObject(QueryType.MOTHERS, p.getParentsPartnership(), l));
		}
		
		return orderResults(mothers);
	}
	
	public ResultObject[] getPartnerOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		PriorityQueue<ResultObject> partners = new PriorityQueue<ResultObject>();
		
		List<Link> possPartnership = p.getPartnerships();
		for(Link l : possPartnership) {
			if(p.getSex() == 'M') {
				for(Link l2 : l.getLinkedPartnership().getPerson2PotentialPartnerLinks()) {
					partners.add(new ResultObject(QueryType.CB_PARTNERS, l, l2));
				}
			} else if(p.getSex() == 'F') {
				for(Link l2 : l.getLinkedPartnership().getPerson1PotentialPartnerLinks()) {
					partners.add(new ResultObject(QueryType.CB_PARTNERS, l, l2));
				}
			}
		}
		
		return orderResults(partners);
	}
	
	// Need to think about way of grouping children into possible sets?
	public ResultObject[] getChildrenOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		PriorityQueue<ResultObject> children = new PriorityQueue<ResultObject>();
		List<Link> possPartnership = p.getPartnerships();
		for(Link l : possPartnership) {
			children.add(new ResultObject(QueryType.CHILDREN, l, ((LinkedChildbearingPartnership) l.getLinkedPartnership()).getChildLink()));
		}
		return orderResults(children);
	}
	
	public ResultObject[] getPotentialFatherSideSiblingsOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		Link parentsPartnership = p.getParentsPartnership();
		Link[] fathers = parentsPartnership.getLinkedPartnership().getPerson1PotentialPartnerLinks();
		PriorityQueue<ResultObject> siblings = new PriorityQueue<ResultObject>();
		for(Link l : fathers) {
			List<Link> partnerships = l.getLinkedPerson().getPartnerships();
			for(Link pL : partnerships) {
				Link childLink = ((LinkedChildbearingPartnership) pL.getLinkedPartnership()).getChildLink();
				Link[] intermidiaryLinks = new Link[]{l, pL};

				// Adding sibling link consideration here
//				Link[] supportingLinks;
//				
//				for(Link l : p.getSiblings()) {
//					
//				}
				
				if(childLink.getLinkedPerson().getId() != p.getId()) {
					siblings.add(new ResultObject(QueryType.FATHERS_SIDE_SIBLINGS, parentsPartnership, intermidiaryLinks, childLink));
				}
							}
		}
		return orderResults(siblings);
	}
	
	
	public ResultObject[] getPotentialMotherSideSiblingsOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		Link parentsPartnership = p.getParentsPartnership();
		Link[] mothers = parentsPartnership.getLinkedPartnership().getPerson2PotentialPartnerLinks();
		PriorityQueue<ResultObject> siblings = new PriorityQueue<ResultObject>();
		for(Link l : mothers) {
			List<Link> partnerships = l.getLinkedPerson().getPartnerships();
			for(Link pL : partnerships) {
				Link childLink = ((LinkedChildbearingPartnership) pL.getLinkedPartnership()).getChildLink();
				Link[] intermidiaryLinks = new Link[]{l, pL};
				if(childLink.getLinkedPerson().getId() != p.getId()) {
					siblings.add(new ResultObject(QueryType.MOTHERS_SIDE_SIBLINGS, parentsPartnership, intermidiaryLinks, childLink));
				}
			}
		}
		return orderResults(siblings);
	}
		
	public ResultObject[] getPotentialFullSiblings(int person) {
		ResultObject[] fSideSibling = getPotentialFatherSideSiblingsOf(person);
		ResultObject[] mSideSibling = getPotentialMotherSideSiblingsOf(person);
		PriorityQueue<ResultObject> potentialFullSiblings = new PriorityQueue<ResultObject>();
		
		for(ResultObject f : fSideSibling) {
			int fId = f.getBranchLink().getLinkedPerson().getId();
			for(ResultObject m : mSideSibling){
				if(fId == m.getBranchLink().getLinkedPerson().getId()) {
					
					potentialFullSiblings.add(new ResultObject(QueryType.FULL_SIBLINGS, f.getRootLink(), f.getIntermidiaryLinks1(), m.getIntermidiaryLinks1(), f.getBranchLink()));
					
				}
			}
		}
		return orderResults(potentialFullSiblings);		
		
	}
	
	public ResultObject[] orderResults(PriorityQueue<ResultObject> pq) {
		
		ResultObject[] temp = pq.toArray(new ResultObject[pq.size()]);
		Arrays.sort(temp);
		return temp;
		
	}
	

}
