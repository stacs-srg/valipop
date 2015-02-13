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
import java.util.List;
import java.util.PriorityQueue;

import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.Evidence;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.DirectLink;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.LinkedPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.LinkedPopulation;

public class PopulationQueries {

	LinkedPopulation population;

	public PopulationQueries(LinkedPopulation population) {
		this.population = population;		
	}

	// Need to think about ways of paring parents into probably sets?
	public DirectLink[] getFatherOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		DirectLink[] l = p.getParentsPartnership().getLinkedPartnership().getMalePotentialPartnerLinks();
		
		return orderArrayOfLinksByHeuristic(l);
	}

	public DirectLink[] getMotherOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		DirectLink[] l = p.getParentsPartnership().getLinkedPartnership().getFemalePotentialPartnerLinks();
		
		return orderArrayOfLinksByHeuristic(l);
	}
	
	public DirectLink[] getPartnerOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		PriorityQueue<DirectLink> partners = new PriorityQueue<DirectLink>();
		List<DirectLink> possPartnership = p.getPartnerships();
		for(DirectLink l : possPartnership) {
			DirectLink[] partnerLinks = null;
			if(p.getSex() == 'M') {
				partnerLinks = l.getLinkedPartnership().getFemalePotentialPartnerLinks();
			} else if(p.getSex() == 'F') {
				partnerLinks = l.getLinkedPartnership().getMalePotentialPartnerLinks();
			}
			
			for(DirectLink lP : partnerLinks) {
				partners.add(lP);
			}
		}
		
		return partners.toArray(new DirectLink[partners.size()]);
	}
	
	// Need to think about way of grouping children into possible sets?
	public DirectLink[] getChildrenOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		PriorityQueue<DirectLink> children = new PriorityQueue<DirectLink>();
		List<DirectLink> possPartnership = p.getPartnerships();
		for(DirectLink l : possPartnership) {
			children.add(l);
		}
		return children.toArray(new DirectLink[children.size()]);
	}
	
	public DirectLink[] getPotentialFatherSideSiblingsOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		DirectLink[] fathers = p.getParentsPartnership().getLinkedPartnership().getMalePotentialPartnerLinks();
		ArrayList<DirectLink> siblings = new ArrayList<DirectLink>();
		for(DirectLink l : fathers) {
			List<DirectLink> partnerships = l.getLinkedPerson().getPartnerships();
			for(DirectLink pL : partnerships) {
				DirectLink childLink = pL.getLinkedPartnership().getChildLink();
				Evidence[] records = new Evidence[pL.getProvenance().length + childLink.getProvenance().length];
				int c = 0;
				for(Evidence e : pL.getProvenance()) {
					records[c++] = e;
				}
				for(Evidence e : childLink.getProvenance()) {
					records[c++] = e;
				}
				siblings.add(new DirectLink(childLink.getLinkedPerson(), pL.getLinkedPartnership(), records, pL.getHeuriticOfLinkValue()));
			}
		}
		DirectLink[] t = siblings.toArray(new DirectLink[siblings.size()]);
		return orderArrayOfLinksByHeuristic(t);
	}
	
	public DirectLink[] getPotentialMotherSideSiblingsOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		DirectLink[] fathers = p.getParentsPartnership().getLinkedPartnership().getFemalePotentialPartnerLinks();
		ArrayList<DirectLink> siblings = new ArrayList<DirectLink>();
		for(DirectLink l : fathers) {
			List<DirectLink> partnerships = l.getLinkedPerson().getPartnerships();
			for(DirectLink pL : partnerships) {
				DirectLink childLink = pL.getLinkedPartnership().getChildLink();
				Evidence[] records = new Evidence[pL.getProvenance().length + childLink.getProvenance().length];
				int c = 0;
				for(Evidence e : pL.getProvenance()) {
					records[c++] = e;
				}
				for(Evidence e : childLink.getProvenance()) {
					records[c++] = e;
				}
				siblings.add(new DirectLink(childLink.getLinkedPerson(), pL.getLinkedPartnership(), records, pL.getHeuriticOfLinkValue()));
			}
		}
		DirectLink[] t = siblings.toArray(new DirectLink[siblings.size()]);
		return orderArrayOfLinksByHeuristic(t);
	}
	
	public DirectLink[] getPotentialFullSiblings(int person) {
		DirectLink[] fSideSibling = getPotentialFatherSideSiblingsOf(person);
		DirectLink[] mSideSibling = getPotentialMotherSideSiblingsOf(person);
		ArrayList<DirectLink> potentialFullSiblings = new ArrayList<DirectLink>();
		
		for(DirectLink f : fSideSibling) {
			int fId = f.getLinkedPerson().getId();
			for(DirectLink m : mSideSibling){
				if(fId == m.getLinkedPerson().getId()) {
					Evidence[] records = new Evidence[f.getProvenance().length + m.getProvenance().length];
					int c = 0;
					for(Evidence e : f.getProvenance()) {
						records[c++] = e;
					}
					for(Evidence e : m.getProvenance()) {
						records[c++] = e;
					}
					potentialFullSiblings.add(new DirectLink(m.getLinkedPerson(), m.getLinkedPerson().getParentsPartnership().getLinkedPartnership(), records, f.getHeuriticOfLinkValue() * m.getHeuriticOfLinkValue()));
				}
			}
		}
		DirectLink[] t = potentialFullSiblings.toArray(new DirectLink[potentialFullSiblings.size()]);
		return orderArrayOfLinksByHeuristic(t);		
		
	}
	
	private DirectLink[] orderArrayOfLinksByHeuristic(DirectLink[] links) {
		PriorityQueue<DirectLink> q = new PriorityQueue<DirectLink>();
		for(DirectLink l : links) {
			q.add(l);
		}
		return q.toArray(new DirectLink[q.size()]);
	}
	
	

}
