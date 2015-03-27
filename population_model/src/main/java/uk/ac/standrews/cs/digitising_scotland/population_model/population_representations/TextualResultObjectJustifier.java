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

public class TextualResultObjectJustifier {

	public static String stringExplanationOf(ResultObject[] resultObject) {

		String explanation = "";

		if(resultObject.length == 0) {
			explanation += "No result objects in given array";
			return explanation;
		}

		if(resultObject[0].getFailedTestPersonRoot() != null) {
			explanation += "This query indicates that no possible " + resultObject[0].getQueryType().toString() + " can be found for the individual " + resultObject[0].getFailedTestPersonRoot().getFirstName() + "\n";
			return explanation;
		}

		explanation += "The query pertains to the possible " + resultObject[0].getQueryType().toString() + " of the individual " + resultObject[0].getRootLink().getLinkedPerson().getFirstName() + ". \n";
		QueryType qt = resultObject[0].getQueryType();

		boolean first = true;
		
		if(qt == QueryType.FATHERS_SIDE_SIBLINGS || qt == QueryType.MOTHERS_SIDE_SIBLINGS) {

						
			for(ResultObject r : resultObject) {
				

				Link bL = r.getBranchLink();
				
				if(first) {
					first = false;
					explanation += "The most likely " + qt.toString() + " with a heuristic value of " + Float.toString(r.getCombinedHeuristic()) + " is individual " + bL.getLinkedPerson().getFirstName() + " (ID: " + bL.getLinkedPerson().getId() + ") supported by the evidence in records ";
					
					Evidence[] evidence = getRecords(r);
					int c = 0;
					for(Evidence e : evidence) {
						explanation += e.getId();
						if(c++ == evidence.length - 1) {
							explanation += ".";
						} if(c++ == evidence.length - 2) {
							explanation += " and ";
						} else {
							explanation += ", ";
						}
					}
				}
				
				// TODO the bit for the less likely siblings.
				
//				explanation += bL.getLinkedPerson().getFirstName() + " @H " + resultObject.getCombinedHeuristic() + " by " + resultObject.getIntermidiaryLinks1()[0].getLinkedPerson().getFirstName() + " with " + resultObject.getSupportingSiblingBridges().length + " SSB" + "\n";
			}

		} else if (qt == QueryType.FULL_SIBLINGS) {

			for(ResultObject r : resultObject) {
				Link bL = r.getBranchLink();
//				explanation += bL.getLinkedPerson().getFirstName() + " @H " + resultObject.getCombinedHeuristic() + " by " + resultObject.getIntermidiaryLinks1()[0].getLinkedPerson().getFirstName() + " & " + resultObject.getIntermidiaryLinks2()[0].getLinkedPerson().getFirstName() + " with " + resultObject.getSupportingSiblingBridges().length + " SSB" + "\n";
			}

		} else if(qt == QueryType.CB_PARTNERS) {

			for(ResultObject r : resultObject) {
				Link bL = r.getBranchLink();
//				explanation += bL.getLinkedPerson().getFirstName() + " @H " + resultObject.getCombinedHeuristic() + " by " + bL.getLinkedIntermediaryObject().getRef() + " with " + resultObject.getSupportingMarriageBridges().length + " SMB" + "\n";
			}

		} else {

			for(ResultObject r : resultObject) {
				Link bL = r.getBranchLink();
//				explanation += bL.getLinkedPerson().getFirstName() + " @H " + resultObject.getCombinedHeuristic() + " by " + bL.getLinkedIntermediaryObject().getRef() + "\n";
			}

		}
		return explanation;
	}

	private static Evidence[] getRecords(ResultObject r) {

		ArrayList<Evidence> records = new ArrayList<Evidence>();
		
		for(Evidence e : r.getBranchLink().getProvenance()) {
			records.add(e);
		}
		for(Link l : r.getIntermidiaryLinks1()) {
			for(Evidence e : l.getProvenance()) {
				
				records.add(e);
			}
		}
		for(Link l : r.getIntermidiaryLinks2()) {
			for(Evidence e : l.getProvenance()) {
				records.add(e);
			}
		}
		for(Evidence e : r.getRootLink().getProvenance()) {
			records.add(e);
		}
		
		return records.toArray(new Evidence[records.size()]);
	}

}
