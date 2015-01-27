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

import java.util.List;
import java.util.PriorityQueue;

import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.Link;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.LinkedPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.LinkedPopulation;

public class PopulationQueries {

	LinkedPopulation population;

	public PopulationQueries(LinkedPopulation population) {
		this.population = population;		
	}

	// Need to think about ways of paring parents into probably sets?
	public Link[] getFatherOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		Link[] l = p.getParentsPartnership().getLinkedPartnership().getMalePotentialPartnerLinks();
		
		return orderArrayOfLinksByHeuristic(l);
	}

	public Link[] getMotherOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		Link[] l = p.getParentsPartnership().getLinkedPartnership().getFemalePotentialPartnerLinks();
		
		return orderArrayOfLinksByHeuristic(l);
	}
	
	public Link[] getPartnerOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		PriorityQueue<Link> partners = new PriorityQueue<Link>();
		List<Link> possPartnership = p.getPartnerships();
		for(Link l : possPartnership) {
			Link[] partnerLinks = null;
			if(p.getSex() == 'M') {
				partnerLinks = l.getLinkedPartnership().getFemalePotentialPartnerLinks();
			} else if(p.getSex() == 'F') {
				partnerLinks = l.getLinkedPartnership().getMalePotentialPartnerLinks();
			}
			
			for(Link lP : partnerLinks) {
				partners.add(lP);
			}
		}
		
		return partners.toArray(new Link[partners.size()]);
	}
	
	// Need to think about way of grouping children into possible sets?
	public Link[] getChildrenOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		PriorityQueue<Link> children = new PriorityQueue<Link>();
		List<Link> possPartnership = p.getPartnerships();
		for(Link l : possPartnership) {
			children.add(l);
		}
		return children.toArray(new Link[children.size()]);
	}
	
	private Link[] orderArrayOfLinksByHeuristic(Link[] links) {
		PriorityQueue<Link> q = new PriorityQueue<Link>();
		for(Link l : links) {
			q.add(l);
		}
		return q.toArray(new Link[q.size()]);
	}
	
	

}
