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

public class ResultObject implements Comparable<ResultObject> {

	private Link rootLink;
	private Link[] intermidiaryLinks1 = new Link[0];
	private Link[] intermidiaryLinks2 = new Link[0];
	private Link branchLink;

	private SiblingBridge[] supportingSiblingBridges = new SiblingBridge[0];
	private MarriageBridge[] supportingMarriageBridges = new MarriageBridge[0];
	
	private LinkedPerson failedTestPersonRoot = null;

	private float combinedHeuristic;
	private QueryType queryType;
	
	public ResultObject(QueryType queryType, Link rootLink, Link branchLink) {
		this.queryType = queryType;
		this.rootLink = rootLink;
		this.branchLink = branchLink;
		combinedHeuristic = calculateCombinedHeuristic();
	}
	
	public ResultObject(QueryType queryType, Link rootLink, Link[] intermidiaryLinks, Link branchLink) {
		this(queryType, rootLink, branchLink);
		this.intermidiaryLinks1 = intermidiaryLinks;
		combinedHeuristic = calculateCombinedHeuristic();
	}
	
	public ResultObject(QueryType queryType, Link rootLink, Link[] intermidiaryLinks1, Link[] intermidiaryLinks2, Link branchLink) {
		this(queryType, rootLink, branchLink);
		this.intermidiaryLinks1 = intermidiaryLinks1;
		this.intermidiaryLinks2 = intermidiaryLinks2;
		combinedHeuristic = calculateCombinedHeuristic();
	}
	
	
	public ResultObject(QueryType queryType, LinkedPerson person) {
		this.queryType = queryType;
		failedTestPersonRoot = person;
	}

	/*
	 * In case where 2 intermediary paths exist then takes the combined heuristic of the least likely path
	 * TODO Need to consider sibling bridges and marriages in here
	 */
	private float calculateCombinedHeuristic() {
		if(intermidiaryLinks1 == null) {
			return this.rootLink.getHeuriticOfLinkValue() * this.branchLink.getHeuriticOfLinkValue();
		} else {
			float temp = this.rootLink.getHeuriticOfLinkValue() * this.branchLink.getHeuriticOfLinkValue();
			for(Link l : intermidiaryLinks1) {
				temp = temp * l.getHeuriticOfLinkValue();
			}
			
			if(intermidiaryLinks2 != null) {
				float temp2 = this.rootLink.getHeuriticOfLinkValue() * this.branchLink.getHeuriticOfLinkValue();
				for(Link l : intermidiaryLinks2) {
					temp2 = temp2 * l.getHeuriticOfLinkValue();
				}
				if(temp2 < temp) {
					return temp2;
				}
			}
			return temp;
		}
	}

	@Override
	public int compareTo(ResultObject o) {
		return Float.compare(o.getCombinedHeuristic(), this.getCombinedHeuristic());
	}
	
	public Link[] getIntermidiaryLinks1() {
		return intermidiaryLinks1;
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

	public float getCombinedHeuristic() {
		return combinedHeuristic;
	}
	
	public QueryType getQueryType() {
		return queryType;
	}
	
	public SiblingBridge[] getSupportingSiblingBridges() {
		return supportingSiblingBridges;
	}

	public void setSupportingSiblingBridges(SiblingBridge[] siblingLinkBridges) {
		this.supportingSiblingBridges = siblingLinkBridges;
		combinedHeuristic = calculateCombinedHeuristic();
	}
	
	public MarriageBridge[] getSupportingMarriageBridges() {
		return supportingMarriageBridges;
	}

	public void setSupportingMarriageBridges(MarriageBridge[] marriageLinkBridges) {
		this.supportingMarriageBridges = marriageLinkBridges;
		combinedHeuristic = calculateCombinedHeuristic();
	}
	
	public LinkedPerson getFailedTestPersonRoot() {
		return failedTestPersonRoot;
	}
	
}
