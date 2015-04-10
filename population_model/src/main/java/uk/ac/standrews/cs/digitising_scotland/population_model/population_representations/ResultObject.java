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

import org.apache.bcel.generic.NEWARRAY;

import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.queries.PopulationQueries;

public class ResultObject implements Comparable<ResultObject> {

	private Link rootLink;
	private Link[] intermediaryLinks1 = new Link[0];
	private Link[] intermidiaryLinks2 = new Link[0];
	private Link branchLink;

	private SiblingBridge[] supportingSiblingBridges = new SiblingBridge[0];
	private MarriageBridge[] supportingMarriageBridges = new MarriageBridge[0];

	private LinkedPerson failedTestPersonRoot = null;

	private float combinedCertaintyEstimate;
	private QueryType queryType;

	public ResultObject(QueryType queryType, Link rootLink, Link branchLink) {
		this.queryType = queryType;
		this.rootLink = rootLink;
		this.branchLink = branchLink;
		calculateCombinedCertaintyEstimate();
	}

	public ResultObject(QueryType queryType, Link rootLink, Link[] intermidiaryLinks, Link branchLink) {
		this(queryType, rootLink, branchLink);
		this.intermediaryLinks1 = intermidiaryLinks;
		calculateCombinedCertaintyEstimate();
	}

	public ResultObject(QueryType queryType, Link rootLink, Link[] intermediaryLinks1, Link[] intermediaryLinks2, Link branchLink) {
		this(queryType, rootLink, branchLink);
		this.intermediaryLinks1 = intermediaryLinks1;
		this.intermidiaryLinks2 = intermediaryLinks2;
		calculateCombinedCertaintyEstimate();
	}


	public ResultObject(QueryType queryType, LinkedPerson person) {
		this.queryType = queryType;
		failedTestPersonRoot = person;
	}

	/*
	 * In case where 2 intermediary paths exist then takes the combined heuristic of the least likely path
	 * TODO Need to consider sibling bridges and marriages in here
	 */
	private void calculateCombinedCertaintyEstimate() {

		switch (queryType) {
		case MOTHERS:
		case FATHERS:
			calculateParentQueryCertaintyEstimate();
			break;
		case CB_PARTNERS:
			break;
		case CHILDREN:
			break;

		case FATHERS_SIDE_SIBLINGS:
			break;
		case FULL_SIBLINGS:
			break;
		case MARRIAGE_BRIDGE:
			break;
		case MOTHERS_SIDE_SIBLINGS:
			break;
		case SIBLING_BRIDGE:
			calculateSiblingBridgeQueryCertaintyEstimate();
			break;
		default:
			break;


		}




		//		if(intermidiaryLinks1 == null) {
		//			return this.rootLink.getCertaintyEstimateOfLink() * this.branchLink.getCertaintyEstimateOfLink();
		//		} else {
		//			float temp = this.rootLink.getCertaintyEstimateOfLink() * this.branchLink.getCertaintyEstimateOfLink();
		//			for(Link l : intermidiaryLinks1) {
		//				temp = temp * l.getCertaintyEstimateOfLink();
		//			}
		//			
		//			if(intermidiaryLinks2 != null) {
		//				float temp2 = this.rootLink.getCertaintyEstimateOfLink() * this.branchLink.getCertaintyEstimateOfLink();
		//				for(Link l : intermidiaryLinks2) {
		//					temp2 = temp2 * l.getCertaintyEstimateOfLink();
		//				}
		//				if(temp2 < temp) {
		//					return temp2;
		//				}
		//			}
		//			return temp;
		//		}

	}

	private void calculateSiblingBridgeQueryCertaintyEstimate() {
		float totalEstimate = 0;

		if(supportingSiblingBridges.length == 0) {
			combinedCertaintyEstimate = totalEstimate;
			return;
		}
		
		ArrayList<Float> p1E = new ArrayList<Float>();
		ArrayList<Float> p2E = new ArrayList<Float>();
		
		Link aL = null, bL = null, gL, hL;
		
		for(Link a : supportingSiblingBridges[0].getSibling1PotentialLinks()) {
			if(a.getLinkedPerson().getId() == rootLink.getLinkedPerson().getId()) {
				aL = a;
				for(Link b : supportingSiblingBridges[0].getSibling2PotentialLinks()) {
					if(b.getLinkedPerson().getId() == branchLink.getLinkedPerson().getId()) {
						bL = b;
					}
				}
			} else if(a.getLinkedPerson().getId() == branchLink.getLinkedPerson().getId()) {
				bL = a;
				for(Link actualA : supportingSiblingBridges[0].getSibling2PotentialLinks()) {
					if(actualA.getLinkedPerson().getId() == rootLink.getLinkedPerson().getId()) {
						aL = actualA;
					}
				}
			}
		}
		
		if(aL == null || bL == null) {
			
			totalEstimate = 0;
			return;
		}
		
		totalEstimate = aL.getCertaintyEstimateOfLink() * bL.getCertaintyEstimateOfLink();
		
		gL = aL.getLinkedPerson().getParentsPartnershipLink();
		hL = bL.getLinkedPerson().getParentsPartnershipLink();
		
		// null checks
		
		for(Link c : gL.getLinkedIntermediaryObject().getPerson1PotentialLinks()) {
			for(Link d : c.getLinkedPerson().getChildBearingPartnerships()) {
				if(d.getLinkedIntermediaryObject().getId() == branchLink.getLinkedIntermediaryObject().getId()) {
					Link cL = c;
					Link dL = d;
					if(supportingSiblingBridges[0].getSiblingType() == SiblingType.HALF_SIBLINGS) {
						p1E.add(cL.getCertaintyEstimateOfLink() * dL.getCertaintyEstimateOfLink() * hL.getCertaintyEstimateOfLink() * gL.getCertaintyEstimateOfLink());
					} else {
						totalEstimate += PopulationQueries.fs * (cL.getCertaintyEstimateOfLink() * dL.getCertaintyEstimateOfLink() * hL.getCertaintyEstimateOfLink() * gL.getCertaintyEstimateOfLink());
						for(Link i : cL.getLinkedPerson().getMarraigePartnerships()) {
							for(Link k : i.getLinkedIntermediaryObject().getOppositePersonsList(cL.getLinkedPerson())) {
								for(Link f : k.getLinkedPerson().getChildBearingPartnerships()) {
									if(f.getLinkedIntermediaryObject().getId() == branchLink.getLinkedIntermediaryObject().getId()) {
										totalEstimate += 0.5f * PopulationQueries.fm * i.getCertaintyEstimateOfLink() * k.getCertaintyEstimateOfLink();
									}
								}
							}
						}
					}
				}
			}
		}
		
		// null checks
		
		for(Link e : gL.getLinkedIntermediaryObject().getPerson2PotentialLinks()) {
			for(Link f : e.getLinkedPerson().getChildBearingPartnerships()) {
				if(f.getLinkedIntermediaryObject().getId() == branchLink.getLinkedIntermediaryObject().getId()) {
					Link eL = e;
					Link fL = f;
					if(supportingSiblingBridges[0].getSiblingType() == SiblingType.HALF_SIBLINGS) {
						p2E.add(eL.getCertaintyEstimateOfLink() * fL.getCertaintyEstimateOfLink() * hL.getCertaintyEstimateOfLink() * gL.getCertaintyEstimateOfLink());
					} else {
						totalEstimate += PopulationQueries.fs * (eL.getCertaintyEstimateOfLink() * fL.getCertaintyEstimateOfLink() * hL.getCertaintyEstimateOfLink() * gL.getCertaintyEstimateOfLink());
						for(Link k : fL.getLinkedPerson().getMarraigePartnerships()) {
							for(Link i : k.getLinkedIntermediaryObject().getOppositePersonsList(fL.getLinkedPerson())) {
								for(Link f2 : i.getLinkedPerson().getChildBearingPartnerships()) {
									if(f2.getLinkedIntermediaryObject().getId() == branchLink.getLinkedIntermediaryObject().getId()) {
										totalEstimate += 0.5f * PopulationQueries.fm * i.getCertaintyEstimateOfLink() * k.getCertaintyEstimateOfLink();
									}
								}
							}
						}					
					}
				}
			}
		}
		
		// assume al paired up
		
		if(supportingSiblingBridges[0].getSiblingType() == SiblingType.HALF_SIBLINGS) {
			int y = 0;
			for(Float x : p1E) {
				if(x > p2E.get(y++)) {
					totalEstimate += PopulationQueries.fs * x;
				} else {
					totalEstimate += PopulationQueries.fs * p2E.get(y-1);	
				}		
			}
		}
		
		combinedCertaintyEstimate = totalEstimate;
		
	}

	private void calculateParentQueryCertaintyEstimate() {
		float totalEstiamte;
		float aE = rootLink.getCertaintyEstimateOfLink();
		float bE = branchLink.getCertaintyEstimateOfLink();
		
		totalEstiamte = aE * bE;

		ArrayList<Link> ds = new ArrayList<Link>();

		for(Link c : branchLink.getLinkedPerson().getMarraigePartnerships()) {
			if(c.getLinkedIntermediaryObject().getOppositePersonsList(rootLink.getLinkedPerson()) == null)
				break;
			for(Link d : c.getLinkedIntermediaryObject().getOppositePersonsList(rootLink.getLinkedPerson())) {
				ds.add(d);
			}
		}

		for(Link d : ds) {
			for(Link e : d.getLinkedPerson().getChildBearingPartnerships()) {
				if(e.getLinkedIntermediaryObject().getId() == rootLink.getLinkedIntermediaryObject().getId()) {
					float cE = 0, dE, eE;
					for(Link c : d.getLinkedIntermediaryObject().getOppositePersonsList(d.getLinkedPerson())) {
						if(c.getLinkedPerson().getId() == branchLink.getLinkedPerson().getId()) {
							cE = c.getCertaintyEstimateOfLink();
							break;
						}
					}
					dE = d.getCertaintyEstimateOfLink();
					eE = e.getCertaintyEstimateOfLink();
					
					totalEstiamte += (PopulationQueries.fm * cE * dE * eE);
					
				}
			}
		}
		combinedCertaintyEstimate = totalEstiamte;

	}

	@Override
	public int compareTo(ResultObject o) {
		return Float.compare(o.getCombinedCertatintyEstimate(), this.getCombinedCertatintyEstimate());
	}

	public Link[] getIntermidiaryLinks1() {
		return intermediaryLinks1;
	}

	public Link[] getIntermidiaryLinks2() {
		return intermidiaryLinks2;
	}

	public Link getRootLink() {
		return rootLink;
	}

	public Link getBranchLink() {
		return branchLink;
	}

	public float getCombinedCertatintyEstimate() {
		return combinedCertaintyEstimate;
	}

	public QueryType getQueryType() {
		return queryType;
	}

	public SiblingBridge[] getSupportingSiblingBridges() {
		return supportingSiblingBridges;
	}

	public void setSupportingSiblingBridges(SiblingBridge[] siblingLinkBridges) {
		this.supportingSiblingBridges = siblingLinkBridges;
		calculateCombinedCertaintyEstimate();
	}

	public MarriageBridge[] getSupportingMarriageBridges() {
		return supportingMarriageBridges;
	}

	public void setSupportingMarriageBridges(MarriageBridge[] marriageLinkBridges) {
		this.supportingMarriageBridges = marriageLinkBridges;
		calculateCombinedCertaintyEstimate();
	}

	public LinkedPerson getFailedTestPersonRoot() {
		return failedTestPersonRoot;
	}

}
