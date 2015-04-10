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
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.ChildbearingPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.MarriageBridge;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.LinkedPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.LinkedPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.SiblingBridge;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.QueryType;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.ResultObject;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.TextualResultObjectJustifier;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.UseCases;
import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.Utils;

public class PopulationQueries {

	LinkedPopulation population;
	
	double fmScalingFactor = 0.1;
	double fmMeasuredRateOfOccurance;
	double fmExpectedRateOfOccurance = 0.6;
	double fmSignificanceInCulture = 0.9;
	public static double fm;
	
	double fsScalingFactor = 0.1;
	double fsAvgNumChildrenExpectedInPopulation = 2.5;
	public static double fs = 0.7;
	
	double fcScalingFactor = 0.1;
	double fcExpectedRateOfOccurance = 0.6;
	public static double fc = 0.7;
	
	
	
	
	

	public static void main(String[] args) {
		LinkedPopulation pop = UseCases.generateNuclearFamilyUseCase13();
		PopulationQueries pq = new PopulationQueries(pop);

		for(int p = 0; p < pop.getNumberOfPeople(); p++) {
			System.out.println("------ Queries From Viewpoint of Person " + pop.findPerson(p).getFirstName() + " ------");

			Utils.printResultSet(pq.getChildrenOf(p));
			System.out.println(TextualResultObjectJustifier.stringExplanationOf(pq.getChildrenOf(p)));
			
			Utils.printResultSet(pq.getFatherOf(p));
			System.out.println(TextualResultObjectJustifier.stringExplanationOf(pq.getFatherOf(p)));
			
			Utils.printResultSet(pq.getMotherOf(p));
			System.out.println(TextualResultObjectJustifier.stringExplanationOf(pq.getMotherOf(p)));
			
			Utils.printResultSet(pq.getPotentialFatherSideSiblingsOf(p));
			System.out.println(TextualResultObjectJustifier.stringExplanationOf(pq.getPotentialFatherSideSiblingsOf(p)));
			
			Utils.printResultSet(pq.getPotentialMotherSideSiblingsOf(p));
			System.out.println(TextualResultObjectJustifier.stringExplanationOf(pq.getPotentialMotherSideSiblingsOf(p)));
			
			Utils.printResultSet(pq.getPotentialFullSiblings(p));
			System.out.println(TextualResultObjectJustifier.stringExplanationOf(pq.getPotentialFullSiblings(p)));
			
			Utils.printResultSet(pq.getPotentialSiblingsByBridges(p));
			System.out.println(TextualResultObjectJustifier.stringExplanationOf(pq.getPotentialSiblingsByBridges(p)));
			
			Utils.printResultSet(pq.getChildbearingPartnerOf(p));
			System.out.println(TextualResultObjectJustifier.stringExplanationOf(pq.getChildbearingPartnerOf(p)));
			
			Utils.printResultSet(pq.getPotentialMarriageByBridges(p));
			System.out.println(TextualResultObjectJustifier.stringExplanationOf(pq.getPotentialMarriageByBridges(p)));
			
			System.out.println("------ PERSON ENDS ------");
		}

	}

	public PopulationQueries(LinkedPopulation population) {
		this.population = population;
		
		initFm();
		initFs();
		initFc();
		
	}

	private void initFc() {
		fc = (fcScalingFactor * 2 * population.getNumberOfMarriagePartnerships()) / (population.getNumberOfPeople() * fcExpectedRateOfOccurance);
	}

	private void initFs() {

		if(fsAvgNumChildrenExpectedInPopulation < 2) {
			fs = 1;
		} else {
			double avg = fsAvgNumChildrenExpectedInPopulation;
			double top = (population.getNumberOfPeople() - 1) * ((avg - 2) * (avg - 2) + avg + 2.0);
			double expectNumSiblingBridges = top / (2 * (avg + 1));
			fs = (population.getNumberOfSiblingBridges() * fsScalingFactor) / expectNumSiblingBridges;
		}
		
	}

	private void initFm() {
		fmMeasuredRateOfOccurance = population.getNumberOfMarriagePartnerships() / (population.getNumberOfPeople() / 2.0f);
		fm = fmScalingFactor * (fmMeasuredRateOfOccurance / fmExpectedRateOfOccurance) * fmSignificanceInCulture;
	}

	// Need to think about ways of paring parents into probably sets?
	public ResultObject[] getFatherOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);

		PriorityQueue<ResultObject> fathers = new PriorityQueue<ResultObject>();

		Link pP = p.getParentsPartnershipLink();

		if(pP == null) {
			ResultObject[] noResult = new ResultObject[1];
			noResult[0] = new ResultObject(QueryType.FATHERS, p);
			return noResult;
		}

		Link[] pFL = pP.getLinkedIntermediaryObject().getPerson1PotentialLinks();
		for(Link l : pFL) {
			fathers.add(new ResultObject(QueryType.FATHERS, p.getParentsPartnershipLink(), l));
		}

		return Utils.orderResults(fathers, QueryType.FATHERS, p);
	}

	public ResultObject[] getMotherOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);

		PriorityQueue<ResultObject> mothers = new PriorityQueue<ResultObject>();
		Link pP = p.getParentsPartnershipLink();

		if(pP == null) {
			ResultObject[] noResult = new ResultObject[1];
			noResult[0] = new ResultObject(QueryType.MOTHERS, p);
			return noResult;
		}

		Link[] pFL = pP.getLinkedIntermediaryObject().getPerson2PotentialLinks();
		for(Link l : pFL) {
			mothers.add(new ResultObject(QueryType.MOTHERS, p.getParentsPartnershipLink(), l));
		}

		return Utils.orderResults(mothers, QueryType.MOTHERS, p);
	}

	public ResultObject[] getChildbearingPartnerOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		PriorityQueue<ResultObject> partners = new PriorityQueue<ResultObject>();

		List<Link> possPartnership = p.getChildBearingPartnerships();

		for(Link l : possPartnership) {
			for(Link l2 : l.getLinkedIntermediaryObject().getOppositePersonsList(p)) {

				ArrayList<MarriageBridge> supportingMarriageBridges = new ArrayList<MarriageBridge>();

				for(Link l3 : p.getMarraigePartnerships()) {
					if(l3.getLinkedIntermediaryObject().getOppositePersonsList(p).length != 0)

						for(Link pMB : l3.getLinkedIntermediaryObject().getOppositePersonsList(p)) {
							// TODO This is going to need a rethink - the complexity is getting concerning

							if (pMB.getLinkedPerson().getId() == l2.getLinkedPerson().getId()) {
								supportingMarriageBridges.add((MarriageBridge) l3.getLinkedIntermediaryObject());
							}
						}
				}

				if(l2.getLinkedPerson().getId() != p.getId()) {
					ResultObject resultObject = new ResultObject(QueryType.CB_PARTNERS, l, l2);
					if(supportingMarriageBridges.size() != 0) {
						MarriageBridge[] temp = supportingMarriageBridges.toArray(new MarriageBridge[supportingMarriageBridges.size()]);
						Arrays.sort(temp);
						resultObject.setSupportingMarriageBridges(temp);
					}
					partners.add(resultObject);
				}
			}
		}
		return Utils.orderResults(partners, QueryType.CB_PARTNERS, p);
	}

	public ResultObject[] getPotentialMarriageByBridges(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		PriorityQueue<ResultObject> marriagePartners = new PriorityQueue<ResultObject>();

		for(Link l : p.getMarraigePartnerships()) {
			for(Link l2 : l.getLinkedIntermediaryObject().getOppositePersonsList(p)) {
				ResultObject resultObject = new ResultObject(QueryType.MARRIAGE_BRIDGE, l, l2);
				resultObject.setSupportingMarriageBridges(new MarriageBridge[]{(MarriageBridge) l2.getLinkedIntermediaryObject()});
				marriagePartners.add(resultObject);
			}
		}
		return Utils.orderResults(marriagePartners, QueryType.MARRIAGE_BRIDGE, p);
	}
	
	public ResultObject[] getPotentialSiblingsByBridges(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		PriorityQueue<ResultObject> siblings = new PriorityQueue<ResultObject>();

		for(Link l : p.getSiblings()) {
			for(Link l2 : l.getLinkedIntermediaryObject().getOppositePersonsList(p)) {
				ResultObject resultObject = new ResultObject(QueryType.SIBLING_BRIDGE, l, l2);
				resultObject.setSupportingSiblingBridges(new SiblingBridge[]{(SiblingBridge) l2.getLinkedIntermediaryObject()});
				
				siblings.add(resultObject);
			}
		}
		return Utils.orderResults(siblings, QueryType.SIBLING_BRIDGE, p);
	}

	public ResultObject[] getChildrenOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);
		PriorityQueue<ResultObject> children = new PriorityQueue<ResultObject>();
		List<Link> possPartnership = p.getChildBearingPartnerships();
		for(Link l : possPartnership) {
			children.add(new ResultObject(QueryType.CHILDREN, l, ((ChildbearingPartnership) l.getLinkedIntermediaryObject()).getChildLink()));
		}
		return Utils.orderResults(children, QueryType.CHILDREN, p);
	}

	/*
	 * This method considers the sibling found when the given persons father's offsping are considered - sibling bridges are only used in
	 * this method to further support a sibling identified from the fathers offspring.
	 * It makes little sense to include other sibling bridges here as it isn't possible to distinguish if they are siblings by the father or the mother only
	 */
	public ResultObject[] getPotentialXSideSiblingsOf(LinkedPerson person, Link[] x, QueryType queryType) {
		Link parentsPartnership = person.getParentsPartnershipLink();
		PriorityQueue<ResultObject> siblings = new PriorityQueue<ResultObject>();
		for(Link l : x) {
			List<Link> partnerships = l.getLinkedPerson().getChildBearingPartnerships();
			for(Link pL : partnerships) {
				Link childLink = ((ChildbearingPartnership) pL.getLinkedIntermediaryObject()).getChildLink();
				Link[] intermidiaryLinks = new Link[]{l, pL};

				// Adding sibling link consideration here
				ArrayList<SiblingBridge> supportingSiblingBridges = new ArrayList<SiblingBridge>();

				for(Link l2 : person.getSiblings()) {
					for(Link pSB : l2.getLinkedIntermediaryObject().getOppositePersonsList(person)) {
						// TODO This is going to need a rethink - the complexity is getting concerning
						if (pSB.getLinkedPerson().getId() == childLink.getLinkedPerson().getId()) {
							supportingSiblingBridges.add((SiblingBridge) l2.getLinkedIntermediaryObject());
						}
					}
				}

				if(childLink.getLinkedPerson().getId() != person.getId()) {
					ResultObject resultObject = new ResultObject(queryType, parentsPartnership, intermidiaryLinks, childLink);
					if(supportingSiblingBridges.size() != 0) {
						SiblingBridge[] temp = supportingSiblingBridges.toArray(new SiblingBridge[supportingSiblingBridges.size()]);
						Arrays.sort(temp);
						resultObject.setSupportingSiblingBridges(temp);
					}
					siblings.add(resultObject);
				}
			}
		}

		return Utils.orderResults(siblings, queryType, person);
	}

	public ResultObject[] getPotentialFatherSideSiblingsOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);

		Link pP = p.getParentsPartnershipLink();

		if(pP == null) {
			ResultObject[] noResult = new ResultObject[1];
			noResult[0] = new ResultObject(QueryType.FATHERS_SIDE_SIBLINGS, p);
			return noResult;
		}

		Link[] pFL = pP.getLinkedIntermediaryObject().getPerson1PotentialLinks();

		return getPotentialXSideSiblingsOf(p, pFL, QueryType.FATHERS_SIDE_SIBLINGS);
	}

	public ResultObject[] getPotentialMotherSideSiblingsOf(int person) {
		LinkedPerson p = (LinkedPerson) population.findPerson(person);

		Link pP = p.getParentsPartnershipLink();

		if(pP == null) {
			ResultObject[] noResult = new ResultObject[1];
			noResult[0] = new ResultObject(QueryType.MOTHERS_SIDE_SIBLINGS, p);
			return noResult;
		}

		Link[] pFL = pP.getLinkedIntermediaryObject().getPerson2PotentialLinks();


		return getPotentialXSideSiblingsOf(p, pFL, QueryType.MOTHERS_SIDE_SIBLINGS);
	}

	

	public ResultObject[] getPotentialFullSiblings(int person) {
		ResultObject[] fSideSibling = getPotentialFatherSideSiblingsOf(person);
		ResultObject[] mSideSibling = getPotentialMotherSideSiblingsOf(person);
		PriorityQueue<ResultObject> potentialFullSiblings = new PriorityQueue<ResultObject>();

		if(fSideSibling.length == 0 || mSideSibling.length == 0 || fSideSibling[0].getFailedTestPersonRoot() != null || mSideSibling[0].getFailedTestPersonRoot() != null) {
			ResultObject[] noResult = new ResultObject[1];
			noResult[0] = new ResultObject(QueryType.FULL_SIBLINGS, (LinkedPerson) population.findPerson(person));
			return noResult;
		}

		for(ResultObject f : fSideSibling) {
			int fatherSideId = f.getBranchLink().getLinkedPerson().getId();
			for(ResultObject m : mSideSibling){
				if(fatherSideId == m.getBranchLink().getLinkedPerson().getId()) {
					ResultObject resultObject = new ResultObject(QueryType.FULL_SIBLINGS, f.getRootLink(), f.getIntermidiaryLinks1(), m.getIntermidiaryLinks1(), f.getBranchLink());
					resultObject.setSupportingSiblingBridges(Utils.joinArraysSkippingDuplicates(f.getSupportingSiblingBridges(), m.getSupportingSiblingBridges()));
					potentialFullSiblings.add(resultObject);

				}
			}
		}
		return Utils.orderResults(potentialFullSiblings, QueryType.FULL_SIBLINGS, (LinkedPerson) population.findPerson(person));		

	}



}
