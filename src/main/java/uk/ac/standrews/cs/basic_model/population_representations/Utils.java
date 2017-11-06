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
package uk.ac.standrews.cs.basic_model.population_representations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import uk.ac.standrews.cs.basic_model.population_representations.data_structure.LinkedPerson;
import uk.ac.standrews.cs.basic_model.population_representations.data_structure.SiblingBridge;
import uk.ac.standrews.cs.basic_model.population_representations.types.QueryType;
import uk.ac.standrews.cs.basic_model.population_representations.data_structure.Link;
import uk.ac.standrews.cs.basic_model.population_representations.results.ResultObject;

/**
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 *
 */
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
			System.out.println(l.getLinkedPerson().getFirstName() + " @H " + l.getCertaintyEstimateOfLink() + " by " + l.getLinkedIntermediaryObject().getRef());
		}
		System.out.println();
	}

	public static void printResultSet(ResultObject[] array) {

		if(array.length == 0)
			return;

		if(array[0].getFailedTestPersonRoot() != null) {
			System.out.println("No found results for " + array[0].getQueryType().toString() + " of " + array[0].getFailedTestPersonRoot().getFirstName());
			System.out.println();
			return;
		}

		System.out.println("Possible " + array[0].getQueryType().toString() + " of " + array[0].getRootLink().getLinkedPerson().getFirstName());
		QueryType qt = array[0].getQueryType();

		if(qt == QueryType.FATHERS_SIDE_SIBLINGS || qt == QueryType.MOTHERS_SIDE_SIBLINGS) {

			for(ResultObject r : array) {
				Link bL = r.getBranchLink();
				System.out.println(bL.getLinkedPerson().getFirstName() + " @H " + r.getCombinedCertatintyEstimate() + " by " + r.getIntermidiaryLinks1()[0].getLinkedPerson().getFirstName() + " with " + r.getSupportingSiblingBridges().length + " SSB");
			}

		} else if (qt == QueryType.FULL_SIBLINGS) {

			for(ResultObject r : array) {
				Link bL = r.getBranchLink();
				System.out.println(bL.getLinkedPerson().getFirstName() + " @H " + r.getCombinedCertatintyEstimate() + " by " + r.getIntermidiaryLinks1()[0].getLinkedPerson().getFirstName() + " & " + r.getIntermidiaryLinks2()[0].getLinkedPerson().getFirstName() + " with " + r.getSupportingSiblingBridges().length + " SSB");
			}

		} else if(qt == QueryType.CB_PARTNERS) {

			for(ResultObject r : array) {
				Link bL = r.getBranchLink();
				System.out.println(bL.getLinkedPerson().getFirstName() + " @H " + r.getCombinedCertatintyEstimate() + " by " + bL.getLinkedIntermediaryObject().getRef() + " with " + r.getSupportingMarriageBridges().length + " SMB");
			}

		} else {

			for(ResultObject r : array) {
				Link bL = r.getBranchLink();
				System.out.println(bL.getLinkedPerson().getFirstName() + " @H " + r.getCombinedCertatintyEstimate() + " by " + bL.getLinkedIntermediaryObject().getRef());
			}

		}
		System.out.println();
	}

	public static ResultObject[] orderResults(PriorityQueue<ResultObject> pq, QueryType queryType, LinkedPerson person) {
		if(pq.size() == 0) {
			return new ResultObject[]{new ResultObject(queryType, person)};
		}
		ResultObject[] temp = pq.toArray(new ResultObject[pq.size()]);
		Arrays.sort(temp);
		return temp;

	}

	public static SiblingBridge[] joinArraysSkippingDuplicates(SiblingBridge[] a, SiblingBridge[] b) {
		ArrayList<SiblingBridge> ret = new ArrayList<SiblingBridge>();
		int c = 0;
		for(SiblingBridge a1 : a) {
			ret.add(a1);
		}
		for(SiblingBridge b1 : b) {
			for(SiblingBridge iA : a) {
				if(iA.getId() == b1.getId()) {
					break;
				}
				ret.add(b1);
			}
		}

		SiblingBridge[] temp = ret.toArray(new SiblingBridge[ret.size()]);
		Arrays.sort(temp);
		return temp;
	}


}
