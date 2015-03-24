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

import java.util.List;

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
		
		System.out.println("Possible " + array[0].getQueryType().toString() + " of " + array[0].getRootLink().getLinkedPerson().getFirstName());
				
		if(array[0].getQueryType() == QueryType.FATHERS_SIDE_SIBLINGS || array[0].getQueryType() == QueryType.MOTHERS_SIDE_SIBLINGS) {
			
			for(ResultObject r : array) {
				Link bL = r.getBranchLink();
				System.out.println(bL.getLinkedPerson().getFirstName() + " @H " + r.getCombinedHeuristic() + " by " + r.getIntermidiaryLinks1()[0].getLinkedPerson().getFirstName());
			}
			
		} else if (array[0].getQueryType() == QueryType.FULL_SIBLINGS) {
			
			for(ResultObject r : array) {
				Link bL = r.getBranchLink();
				System.out.println(bL.getLinkedPerson().getFirstName() + " @H " + r.getCombinedHeuristic() + " by " + r.getIntermidiaryLinks1()[0].getLinkedPerson().getFirstName() + " & " + r.getIntermidiaryLinks2()[0].getLinkedPerson().getFirstName());
			}
			
		} else {
		
			for(ResultObject r : array) {
				Link bL = r.getBranchLink();
				System.out.println(bL.getLinkedPerson().getFirstName() + " @H " + r.getCombinedHeuristic() + " by " + bL.getLinkedIntermediaryObject().getRef());
			}
				
		}
		System.out.println();
	}

	private static boolean isSiblingQueries(QueryType queryType) {
		if(queryType == QueryType.FATHERS_SIDE_SIBLINGS)
			return true;
		if(queryType == QueryType.MOTHERS_SIDE_SIBLINGS)
			return true;
		if(queryType == QueryType.FULL_SIBLINGS)
			return true;
		return false;
	}

	
}
