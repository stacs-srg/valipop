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
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class Utils {

	public static void print(LinkedPerson person) {
		System.out.println(person.getFirstName());
	}

	public static void print(List<LinkedPerson> list) {
		for(LinkedPerson p : list) {
			System.out.print(p.getFirstName() + " ");
		}
		System.out.println();
	}

	public static void printPersons(Link[] array) {
		for(Link l : array) {
			System.out.println(l.getLinkedPerson().getFirstName() + " @H " + l.getHeuriticOfLinkValue() + " by " + l.getLinkedIntermediaryObject().getRef());
		}
		System.out.println();
	}

	public static void printResultSet(ResultObject[] array) {
		
		if(array.length == 0)
			return;
		
		if(array[0].getFailedTestPersonRoot() != null) {
			System.out.println("No found results for " + array[0].getQueryType().toString() + " of " + array[0].getFailedTestPersonRoot().getFirstName());
			return;
		}
		
		System.out.println("Possible " + array[0].getQueryType().toString() + " of " + array[0].getRootLink().getLinkedPerson().getFirstName());
		QueryType qt = array[0].getQueryType();
				
		if(qt == QueryType.FATHERS_SIDE_SIBLINGS || qt == QueryType.MOTHERS_SIDE_SIBLINGS) {
			
			for(ResultObject r : array) {
				Link bL = r.getBranchLink();
				System.out.println(bL.getLinkedPerson().getFirstName() + " @H " + r.getCombinedHeuristic() + " by " + r.getIntermidiaryLinks1()[0].getLinkedPerson().getFirstName() + " with " + r.getSupportingSiblingBridges().length + " SSB");
			}
			
		} else if (qt == QueryType.FULL_SIBLINGS) {
			
			for(ResultObject r : array) {
				Link bL = r.getBranchLink();
				System.out.println(bL.getLinkedPerson().getFirstName() + " @H " + r.getCombinedHeuristic() + " by " + r.getIntermidiaryLinks1()[0].getLinkedPerson().getFirstName() + " & " + r.getIntermidiaryLinks2()[0].getLinkedPerson().getFirstName() + " with " + r.getSupportingSiblingBridges().length + " supporting sibling bridges");
			}
			
		} else if(qt == QueryType.CB_PARTNERS) {
			
			for(ResultObject r : array) {
				Link bL = r.getBranchLink();
				System.out.println(bL.getLinkedPerson().getFirstName() + " @H " + r.getCombinedHeuristic() + " by " + bL.getLinkedIntermediaryObject().getRef() + " with " + r.getSupportingMarriageBridges().length + " SMB");
			}
			
		} else {
		
			for(ResultObject r : array) {
				Link bL = r.getBranchLink();
				System.out.println(bL.getLinkedPerson().getFirstName() + " @H " + r.getCombinedHeuristic() + " by " + bL.getLinkedIntermediaryObject().getRef());
			}
				
		}
		System.out.println();
	}

	public static ResultObject[] orderResults(PriorityQueue<ResultObject> pq) {

		ResultObject[] temp = pq.toArray(new ResultObject[pq.size()]);
		Arrays.sort(temp);
		return temp;

	}

	public static LinkedSiblings[] joinArraysSkippingDuplicates(LinkedSiblings[] a, LinkedSiblings[] b) {
		ArrayList<LinkedSiblings> ret = new ArrayList<LinkedSiblings>();
		int c = 0;
		for(LinkedSiblings a1 : a) {
			ret.add(a1);
		}
		for(LinkedSiblings b1 : b) {
			for(LinkedSiblings iA : a) {
				if(iA.getId() == b1.getId()) {
					break;
				}
				ret.add(b1);
			}
		}
		
		LinkedSiblings[] temp = ret.toArray(new LinkedSiblings[ret.size()]);
		Arrays.sort(temp);
		return temp;
	}

	
}
