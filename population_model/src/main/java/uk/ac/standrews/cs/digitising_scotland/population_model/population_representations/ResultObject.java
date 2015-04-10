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
import java.util.List;

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
			calculateChildbearingPartnerQueryCertatintyEstimate();
			break;
		case CHILDREN:
			calculateChildrenQueryCertaintyEstimate();
			break;
		case FATHERS_SIDE_SIBLINGS:
		case MOTHERS_SIDE_SIBLINGS:
			calculateParentsSideSiblingQueryCertaintyEstimate();
			break;
		case FULL_SIBLINGS:
			calculateFullSiblingQueryCertaintyEstimate();
			break;
		case MARRIAGE_BRIDGE:
			calculateMarriageBridgeQueryCertaintyEstimate();
			break;
		case SIBLING_BRIDGE:
			calculateSiblingBridgeQueryCertaintyEstimate();
			break;
		default:
			break;


		}

	}

	private void calculateMarriageBridgeQueryCertaintyEstimate() {

		float totalEstimate = 0;

		Link a = rootLink;
		Link b = branchLink;

		totalEstimate = cE(a) * cE(b);
		
		List<Link> ds = a.getLinkedPerson().getChildBearingPartnerships();
		for(Link d : ds) {
			Link[] cs = d.getLinkedIntermediaryObject().getOppositePersonsList(a.getLinkedPerson());
			for(Link c : cs) {
				if(c.getLinkedPerson().getId() == b.getLinkedPerson().getId()) {
					totalEstimate += PopulationQueries.fc * cE(c) * cE(d);
				}
			}
		}
		
		combinedCertaintyEstimate = totalEstimate;
		
	}

	private void calculateFullSiblingQueryCertaintyEstimate() {
		float totalEstimate = 0;

		if(supportingSiblingBridges.length == 0) {
			combinedCertaintyEstimate = totalEstimate;
			return;
		}

		ArrayList<Float> p1E = new ArrayList<Float>();
		ArrayList<Float> p2E = new ArrayList<Float>();

		Link gL = null, hL = null, aL, bL;

		aL = rootLink.getLinkedPerson().getParentsPartnershipLink();
		bL = branchLink.getLinkedPerson().getParentsPartnershipLink();

		for(Link c : aL.getLinkedIntermediaryObject().getPerson1PotentialLinks()) {
			for(Link d : c.getLinkedPerson().getChildBearingPartnerships()) {
				if(d.getLinkedIntermediaryObject().getId() == branchLink.getLinkedIntermediaryObject().getId()) {
					Link cL = c;
					Link dL = d;
					totalEstimate += 0.5 * PopulationQueries.fs * (cE(cL) * cE(dL) * cE(bL) * cE(aL));

					for(Link i : cL.getLinkedPerson().getMarraigePartnerships()) {
						for(Link k : i.getLinkedIntermediaryObject().getOppositePersonsList(cL.getLinkedPerson())) {
							for(Link f : k.getLinkedPerson().getChildBearingPartnerships()) {
								if(f.getLinkedIntermediaryObject().getId() == branchLink.getLinkedIntermediaryObject().getId()) {
									totalEstimate += 0.5f * PopulationQueries.fm * cE(i) * cE(k);
								}
							}
						}
					}
				}
			}
		}

		for(Link e : aL.getLinkedIntermediaryObject().getPerson2PotentialLinks()) {
			for(Link f : e.getLinkedPerson().getChildBearingPartnerships()) {
				if(f.getLinkedIntermediaryObject().getId() == branchLink.getLinkedIntermediaryObject().getId()) {
					Link eL = e;
					Link fL = f;
					totalEstimate += 0.5 * PopulationQueries.fs * (cE(eL) * cE(fL) * cE(bL) * cE(aL));

					for(Link k : fL.getLinkedPerson().getMarraigePartnerships()) {
						for(Link i : k.getLinkedIntermediaryObject().getOppositePersonsList(fL.getLinkedPerson())) {
							for(Link f2 : i.getLinkedPerson().getChildBearingPartnerships()) {
								if(f2.getLinkedIntermediaryObject().getId() == branchLink.getLinkedIntermediaryObject().getId()) {
									totalEstimate += 0.5f * PopulationQueries.fm * cE(i) * cE(k);
								}
							}
						}
					}					
				}
			}
		}

		for(Link g : supportingSiblingBridges[0].getSibling1PotentialLinks()) {
			if(g.getLinkedPerson().getId() == rootLink.getLinkedPerson().getId()) {
				gL = g;
				for(Link h : supportingSiblingBridges[0].getSibling2PotentialLinks()) {
					if(h.getLinkedPerson().getId() == branchLink.getLinkedPerson().getId()) {
						hL = h;
					}
				}
			} else if(g.getLinkedPerson().getId() == branchLink.getLinkedPerson().getId()) {
				hL = g;
				for(Link actualG : supportingSiblingBridges[0].getSibling2PotentialLinks()) {
					if(actualG.getLinkedPerson().getId() == rootLink.getLinkedPerson().getId()) {
						gL = actualG;
					}
				}
			}
		}

		if(gL != null && hL != null) {
			totalEstimate += PopulationQueries.fs * cE(gL) * cE(hL);
		}

		combinedCertaintyEstimate = totalEstimate;

	}

	private void calculateParentsSideSiblingQueryCertaintyEstimate() {
		float totalEstimate = 0;

		if(supportingSiblingBridges.length == 0) {
			combinedCertaintyEstimate = totalEstimate;
			return;
		}

		ArrayList<Float> p1E = new ArrayList<Float>();
		ArrayList<Float> p2E = new ArrayList<Float>();

		Link gL = null, hL = null, aL, bL;

		aL = rootLink.getLinkedPerson().getParentsPartnershipLink();
		bL = branchLink.getLinkedPerson().getParentsPartnershipLink();

		for(Link c : aL.getLinkedIntermediaryObject().getPerson1PotentialLinks()) {
			for(Link d : c.getLinkedPerson().getChildBearingPartnerships()) {
				if(d.getLinkedIntermediaryObject().getId() == branchLink.getLinkedIntermediaryObject().getId()) {
					Link cL = c;
					Link dL = d;
					p1E.add(cE(cL) * cE(dL) * cE(bL) * cE(aL));

					for(Link i : cL.getLinkedPerson().getMarraigePartnerships()) {
						for(Link k : i.getLinkedIntermediaryObject().getOppositePersonsList(cL.getLinkedPerson())) {
							for(Link f : k.getLinkedPerson().getChildBearingPartnerships()) {
								if(f.getLinkedIntermediaryObject().getId() == branchLink.getLinkedIntermediaryObject().getId()) {
									totalEstimate += 0.5f * PopulationQueries.fm * cE(i) * cE(k);
								}
							}
						}
					}
				}
			}
		}

		for(Link e : aL.getLinkedIntermediaryObject().getPerson2PotentialLinks()) {
			for(Link f : e.getLinkedPerson().getChildBearingPartnerships()) {
				if(f.getLinkedIntermediaryObject().getId() == branchLink.getLinkedIntermediaryObject().getId()) {
					Link eL = e;
					Link fL = f;
					p2E.add(cE(eL) * cE(fL) * cE(bL) * cE(aL));

					for(Link k : fL.getLinkedPerson().getMarraigePartnerships()) {
						for(Link i : k.getLinkedIntermediaryObject().getOppositePersonsList(fL.getLinkedPerson())) {
							for(Link f2 : i.getLinkedPerson().getChildBearingPartnerships()) {
								if(f2.getLinkedIntermediaryObject().getId() == branchLink.getLinkedIntermediaryObject().getId()) {
									totalEstimate += 0.5f * PopulationQueries.fm * cE(i) * cE(k);
								}
							}
						}
					}					
				}
			}
		}
		
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

		for(Link g : supportingSiblingBridges[0].getSibling1PotentialLinks()) {
			if(g.getLinkedPerson().getId() == rootLink.getLinkedPerson().getId()) {
				gL = g;
				for(Link h : supportingSiblingBridges[0].getSibling2PotentialLinks()) {
					if(h.getLinkedPerson().getId() == branchLink.getLinkedPerson().getId()) {
						hL = h;
					}
				}
			} else if(g.getLinkedPerson().getId() == branchLink.getLinkedPerson().getId()) {
				hL = g;
				for(Link actualG : supportingSiblingBridges[0].getSibling2PotentialLinks()) {
					if(actualG.getLinkedPerson().getId() == rootLink.getLinkedPerson().getId()) {
						gL = actualG;
					}
				}
			}
		}

		if(gL != null && hL != null) {
			totalEstimate += PopulationQueries.fs * cE(gL) * cE(hL);
		}

		combinedCertaintyEstimate = totalEstimate;

	}

	private void calculateChildrenQueryCertaintyEstimate() {
		float totalEstimate = 0;

		Link a = rootLink;
		Link b = branchLink;

		totalEstimate = cE(a) * cE(b);

		List<Link> is = b.getLinkedPerson().getChildBearingPartnerships();
		for(Link i : is) {
			Link h = ((ChildbearingPartnership) i.getLinkedIntermediaryObject()).getChildLink();
			List<Link> gs = h.getLinkedPerson().getSiblings();
			for(Link g : gs) {
				Link[] fs = g.getLinkedIntermediaryObject().getOppositePersonsList(h.getLinkedPerson());
				for(Link f : fs) {
					if(f.getLinkedPerson().getId() == a.getLinkedPerson().getId()) {
						totalEstimate += PopulationQueries.fs * cE(f) * cE(g) * cE(h) * cE(i);
					}
				}
			}
		}

		List<Link> cs = b.getLinkedPerson().getMarraigePartnerships();
		for(Link c : cs) {
			Link[] ds = c.getLinkedIntermediaryObject().getOppositePersonsList(a.getLinkedPerson());
			for(Link d : ds) {
				List<Link> es = d.getLinkedPerson().getChildBearingPartnerships();
				for(Link e : es) {
					if(e.getLinkedIntermediaryObject().getId() == a.getLinkedIntermediaryObject().getId()) {
						totalEstimate += PopulationQueries.fm * cE(c) * cE(d) * cE(e);
					}
				}				
			}
		}

		combinedCertaintyEstimate = totalEstimate;

	}

	private void calculateChildbearingPartnerQueryCertatintyEstimate() {

		float totalEstimate = 0;

		Link a = rootLink;
		Link b = branchLink;

		totalEstimate = cE(a) * cE(b);

		List<Link> es = b.getLinkedPerson().getChildBearingPartnerships();
		for(Link e : es) {
			for(Link f : e.getLinkedIntermediaryObject().getOppositePersonsList(b.getLinkedPerson())) {
				if(f.getLinkedPerson().getId() == rootLink.getLinkedPerson().getId()) {
					float p2 = 0;
					Link i = ((ChildbearingPartnership) e.getLinkedIntermediaryObject()).getChildLink();
					List<Link> hs = i.getLinkedPerson().getSiblings();
					for(Link h : hs) {
						Link[] gs = h.getLinkedIntermediaryObject().getOppositePersonsList(h.getLinkedPerson());
						for(Link g : gs) {
							Link j = ((ChildbearingPartnership) a.getLinkedIntermediaryObject()).getChildLink();
							if(g.getLinkedPerson().getId() == j.getLinkedPerson().getId()) {
								p2 = (float) (PopulationQueries.fs * cE(i) * cE(h) * cE(g) * cE(j));

							}
						}
					}
					totalEstimate += PopulationQueries.fc * (cE(a) * cE(b) * cE(e) * cE(f) + p2);

				}
			}
		}

		List<Link> cs = a.getLinkedPerson().getMarraigePartnerships();
		for(Link c : cs) {
			Link[] ds = c.getLinkedIntermediaryObject().getOppositePersonsList(a.getLinkedPerson());
			for(Link d : ds) {
				List<Link> bs = d.getLinkedPerson().getChildBearingPartnerships();
				for(Link bT : bs) {
					if(bT.getLinkedIntermediaryObject().getId() == a.getLinkedIntermediaryObject().getId()) {
						totalEstimate += PopulationQueries.fm * cE(c) * cE(d) * cE(bT);
					}
				}
			}
		}

		combinedCertaintyEstimate = totalEstimate;

	}

	private float cE(Link l) {
		return l.getCertaintyEstimateOfLink();
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

		totalEstimate = cE(aL) * cE(bL);

		gL = aL.getLinkedPerson().getParentsPartnershipLink();
		hL = bL.getLinkedPerson().getParentsPartnershipLink();

		for(Link c : gL.getLinkedIntermediaryObject().getPerson1PotentialLinks()) {
			for(Link d : c.getLinkedPerson().getChildBearingPartnerships()) {
				if(d.getLinkedIntermediaryObject().getId() == branchLink.getLinkedIntermediaryObject().getId()) {
					Link cL = c;
					Link dL = d;
					if(supportingSiblingBridges[0].getSiblingType() == SiblingType.HALF_SIBLINGS) {
						p1E.add(cE(cL) * cE(dL) * cE(hL) * cE(gL));
					} else {
						totalEstimate += PopulationQueries.fs * (cE(cL) * cE(dL) * cE(hL) * cE(gL));
						for(Link i : cL.getLinkedPerson().getMarraigePartnerships()) {
							for(Link k : i.getLinkedIntermediaryObject().getOppositePersonsList(cL.getLinkedPerson())) {
								for(Link f : k.getLinkedPerson().getChildBearingPartnerships()) {
									if(f.getLinkedIntermediaryObject().getId() == branchLink.getLinkedIntermediaryObject().getId()) {
										totalEstimate += 0.5f * PopulationQueries.fm * cE(i) * cE(k);
									}
								}
							}
						}
					}
				}
			}
		}

		for(Link e : gL.getLinkedIntermediaryObject().getPerson2PotentialLinks()) {
			for(Link f : e.getLinkedPerson().getChildBearingPartnerships()) {
				if(f.getLinkedIntermediaryObject().getId() == branchLink.getLinkedIntermediaryObject().getId()) {
					Link eL = e;
					Link fL = f;
					if(supportingSiblingBridges[0].getSiblingType() == SiblingType.HALF_SIBLINGS) {
						p2E.add(cE(eL) * cE(fL) * cE(hL) * cE(gL));
					} else {
						totalEstimate += PopulationQueries.fs * (cE(eL) * cE(fL) * cE(hL) * cE(gL));
						for(Link k : fL.getLinkedPerson().getMarraigePartnerships()) {
							for(Link i : k.getLinkedIntermediaryObject().getOppositePersonsList(fL.getLinkedPerson())) {
								for(Link f2 : i.getLinkedPerson().getChildBearingPartnerships()) {
									if(f2.getLinkedIntermediaryObject().getId() == branchLink.getLinkedIntermediaryObject().getId()) {
										totalEstimate += 0.5f * PopulationQueries.fm * cE(i) * cE(k);
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
							cE = cE(c);
							break;
						}
					}
					dE = cE(d);
					eE = cE(e);

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
