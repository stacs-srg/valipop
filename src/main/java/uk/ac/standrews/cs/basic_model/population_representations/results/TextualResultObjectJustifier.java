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
package uk.ac.standrews.cs.basic_model.population_representations.results;

import java.util.ArrayList;

import uk.ac.standrews.cs.basic_model.population_representations.data_structure.Evidence;
import uk.ac.standrews.cs.basic_model.population_representations.data_structure.Link;
import uk.ac.standrews.cs.basic_model.population_representations.data_structure.MarriageBridge;
import uk.ac.standrews.cs.basic_model.population_representations.data_structure.SiblingBridge;
import uk.ac.standrews.cs.basic_model.population_representations.types.QueryType;

/**
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 *
 */
public class TextualResultObjectJustifier {

	public static String stringExplanationOf(ResultObject[] resultObject) {

		String explanation = "";

		if(resultObject.length == 0) {
			explanation += "No result objects in given array";
			return explanation;
		}

		if(resultObject[0].getFailedTestPersonRoot() != null) {
			explanation += "This query indicates that no possible " + queryTypeS(resultObject[0]) + " can be found for the person " + resultObject[0].getFailedTestPersonRoot().getFirstName() + "\n";
			return explanation;
		}

		explanation += "The query pertains to the possible " + queryTypeS(resultObject[0]) + " of the person " + personFNS(resultObject[0].getRootLink()) + sNL();
		QueryType qt = resultObject[0].getQueryType();

		boolean first = true;

		if(qt == QueryType.FATHERS_SIDE_SIBLINGS || qt == QueryType.MOTHERS_SIDE_SIBLINGS) {


			for(ResultObject r : resultObject) {

				Link bL = r.getBranchLink();

				if(first) {
					first = false;
					explanation += "The most likely " + queryTypeS(qt) + " with a certainty estimation of " + combinedCertaintyEstimateS(r) + " is person " + personFNS(bL) + " ";
					explanation +=  personIDS(bL) + " with person " + interL1PersonFNS(r, 0) + " " + interL1PersonIDS(r, 0) + " as the common parent, supported by the evidence in records ";
				} else {
					explanation += "Person "  + personFNS(bL) + " " + personIDS(bL) + " is also identified as a possible " + queryTypeS(qt);
					explanation += " with a certainty estimation of " + combinedCertaintyEstimateS(r) + " with person " + interL1PersonFNS(r, 0);
					explanation += " " + interL1PersonIDS(r, 0) + " as the common parent, supported by the evidence in records ";
				}

				explanation += addEvidenceToString(r);
				explanation += addSiblingBridgesToString(r) + nL();

				//	explanation += bL.getLinkedPerson().getFirstName() + " @H " + resultObject.getCombinedHeuristic() + " by " + resultObject.getIntermidiaryLinks1()[0].getLinkedPerson().getFirstName() + " with " + resultObject.getSupportingSiblingBridges().length + " SSB" + "\n";
			}

		} else if (qt == QueryType.FULL_SIBLINGS) {

			for(ResultObject r : resultObject) {
				Link bL = r.getBranchLink();

				if(first) {
					first = false;
					explanation += "The most likely " + queryTypeS(qt) + " with a certainty estimation of " + combinedCertaintyEstimateS(r) + " is person " + personFNS(bL) + " ";
					explanation +=  personIDS(bL) + " with persons " + interL1PersonFNS(r, 0) + " " + interL1PersonIDS(r, 0) + " & " + interL2PersonFNS(r, 0);
					explanation += " " + interL2PersonIDS(r, 0) + " as the common parents, supported by the evidence in records ";
				} else {
					explanation += "Person "  + personFNS(bL) + " " + personIDS(bL) + " is also identified as a possible " + queryTypeS(qt);
					explanation += " with a certainty estimation of " + combinedCertaintyEstimateS(r) + " with persons " + interL1PersonFNS(r, 0);
					explanation += " " + interL1PersonIDS(r, 0) + " & " + interL2PersonFNS(r, 0) + " " + interL2PersonIDS(r, 0);
					explanation += " as the common parents, supported by the evidence in records ";
				}

				explanation += addEvidenceToString(r);
				explanation += addSiblingBridgesToString(r) + nL();

				//				explanation += bL.getLinkedPerson().getFirstName() + " @H " + resultObject.getCombinedHeuristic() + " by " + resultObject.getIntermidiaryLinks1()[0].getLinkedPerson().getFirstName() + " & " + resultObject.getIntermidiaryLinks2()[0].getLinkedPerson().getFirstName() + " with " + resultObject.getSupportingSiblingBridges().length + " SSB" + "\n";
			}

		} else if(qt == QueryType.CB_PARTNERS) {

			for(ResultObject r : resultObject) {
				Link bL = r.getBranchLink();

				if(first) {
					first = false;
					explanation += "The most likely " + queryTypeS(qt) + " with a certainty estimation of " + combinedCertaintyEstimateS(r) + " is person " + personFNS(bL) + " ";
					explanation +=  personIDS(bL) + " with partnership " + partnershipIDS(r) + " as the joining object, supported by the evidence in records ";
				} else {
					explanation += "Person "  + personFNS(bL) + " " + personIDS(bL) + " is also identified as a possible " + queryTypeS(qt);
					explanation += " with a certainty estimation of " + combinedCertaintyEstimateS(r) + " with partnership " + partnershipIDS(r);
					explanation += " as the joining object, supported by the evidence in records ";
				}

				explanation += addEvidenceToString(r);
				explanation += addMarriageBridgesToString(r) + nL();

				//				explanation += bL.getLinkedPerson().getFirstName() + " @H " + resultObject.getCombinedHeuristic() + " by " + bL.getLinkedIntermediaryObject().getRef() + " with " + resultObject.getSupportingMarriageBridges().length + " SMB" + "\n";
			}

		} else if(qt == QueryType.SIBLING_BRIDGE) {

			for(ResultObject r : resultObject) {
				Link bL = r.getBranchLink();

				if(first) {
					first = false;
					explanation += "The most likely sibling with a certainty estimation of " + combinedCertaintyEstimateS(r) + " is person " + personFNS(bL) + " ";
					explanation +=  personIDS(bL) + " based upon sibling bridge " + siblingBridgeIDS(r) + ", supported by the evidence in records ";
				} else {
					explanation += "Person "  + personFNS(bL) + " " + personIDS(bL) + " is also identified as a possible sibling";
					explanation += " with a certainty estimation of " + combinedCertaintyEstimateS(r) + " based upon sibling bridge " + siblingBridgeIDS(r);
					explanation += " , supported by the evidence in records ";
				}

				explanation += addEvidenceToString(r) + nL();

			}

		} else if(qt == QueryType.MARRIAGE_BRIDGE) {

			for(ResultObject r : resultObject) {
				Link bL = r.getBranchLink();

				if(first) {
					first = false;
					explanation += "The most likely spouse with a certainty estimation of " + combinedCertaintyEstimateS(r) + " is person " + personFNS(bL) + " ";
					explanation +=  personIDS(bL) + " based upon marriage bridge " + marriageBridgeIDS(r) + ", supported by the evidence in records ";
				} else {
					explanation += "Person "  + personFNS(bL) + " " + personIDS(bL) + " is also identified as a possible spouse";
					explanation += " with a certainty estimation of " + combinedCertaintyEstimateS(r) + " based upon marriage bridge " + marriageBridgeIDS(r);
					explanation += " , supported by the evidence in records ";
				}

				explanation += addEvidenceToString(r) + nL();

			}

		} else {

			for(ResultObject r : resultObject) {
				Link bL = r.getBranchLink();

				if(first) {
					first = false;
					explanation += "The most likely " + queryTypeS(qt) + " with a certainty estimation of " + combinedCertaintyEstimateS(r) + " is person " + personFNS(bL) + " ";
					explanation +=  personIDS(bL) + " with partnership " + partnershipIDS(r) + " as the joining object, supported by the evidence in records ";
				} else {
					explanation += "Person "  + personFNS(bL) + " " + personIDS(bL) + " is also identified as a possible " + queryTypeS(qt);
					explanation += " with a certainty estimation of " + combinedCertaintyEstimateS(r) + " with partnership " + partnershipIDS(r);
					explanation += " as the joining object, supported by the evidence in records ";
				}

				explanation += addEvidenceToString(r);

			}

			//			for(ResultObject r : resultObject) {
			//				Link bL = r.getBranchLink();
			////								explanation += bL.getLinkedPerson().getFirstName() + " @H " + resultObject.getCombinedHeuristic() + " by " + bL.getLinkedIntermediaryObject().getRef() + "\n";
			//			}

		}

		explanation += "\n";

		return explanation;
	}



	private static String siblingBridgeIDS(ResultObject r) {
		return "(ID: " + r.getSupportingSiblingBridges()[0].getId() + ")";
	}

	private static String marriageBridgeIDS(ResultObject r) {
		return "(ID: " + r.getSupportingMarriageBridges()[0].getId() + ")";
	}

	private static String partnershipIDS(ResultObject r) {
		return "ID " + Integer.toString(r.getRootLink().getLinkedIntermediaryObject().getId());
	}

	private static String nL() {
		return "\n";
	}

	private static String interL1PersonFNS(ResultObject r, int i) {
		return personFNS(r.getIntermidiaryLinks1()[i]);
	}

	private static String interL2PersonFNS(ResultObject r, int i) {
		return personFNS(r.getIntermidiaryLinks2()[i]);
	}

	private static String interL1PersonIDS(ResultObject r, int i) {
		return personIDS(r.getIntermidiaryLinks1()[i]);
	}

	private static String interL2PersonIDS(ResultObject r, int i) {
		return personIDS(r.getIntermidiaryLinks2()[i]);
	}

	private static String personIDS(Link bL) {
		return "(ID: " + bL.getLinkedPerson().getId() + ")";
	}

	private static String combinedCertaintyEstimateS(ResultObject r) {
		return Float.toString(r.getCombinedCertatintyEstimate());
	}

	private static String queryTypeS(QueryType qt) {
		switch (qt) {
		case CB_PARTNERS:
			return "childbearing partners";
		case CHILDREN:
			return "children";
		case FATHERS:
			return "fathers";
		case FATHERS_SIDE_SIBLINGS:
			return "father's side siblings";
		case FULL_SIBLINGS:
			return "full siblings";
		case MARRIAGE_BRIDGE:
			return "marriage bridge";
		case MOTHERS:
			return "mothers";
		case MOTHERS_SIDE_SIBLINGS:
			return "mother's side sibling";
		case SIBLING_BRIDGE:
			return "sibling bridges";
		default:
			break;
		}

		return " object ";
	}

	private static String sNL() {
		return ". \n";
	}

	private static String personFNS(Link link) {
		return link.getLinkedPerson().getFirstName();
	}

	private static String queryTypeS(ResultObject resultObject) {
		QueryType qt = resultObject.getQueryType();
		return queryTypeS(qt);

	}

	private static String addMarriageBridgesToString(ResultObject r) {
		boolean marFirst = true;
		String explanation = "";
		for(MarriageBridge lS : r.getSupportingMarriageBridges()) {

			if(r.getSupportingMarriageBridges().length != 1) {
				if(marFirst) {
					marFirst = false;
					explanation += "This marriage bridge is supported by " + r.getSupportingMarriageBridges().length + " distinct marriage bridges. The most likely of these is ";
					explanation += " the marriage bridge ID " + lS.getId() + " supported by records ";
				} else {
					explanation += "The marriage bridge ID " + lS.getId() + " also supports the pairing of these two people as spouses, ";
					explanation += "in this case supported by records ";
				}						
				explanation += addEvidenceToString(r);

			} else {

				explanation += "This marriage bridge is supported by ";
				explanation += "the marriage bridge ID " + lS.getId() + " supported by records ";
				explanation += addEvidenceToString(r);
			}

		}
		return explanation;
	}

	private static String addSiblingBridgesToString(ResultObject r) {
		boolean sibFirst = true;
		String explanation = "";
		for(SiblingBridge lS : r.getSupportingSiblingBridges()) {

			if(r.getSupportingSiblingBridges().length != 1) {
				if(sibFirst) {
					sibFirst = false;
					explanation += "This sibling bridge is supported by " + r.getSupportingSiblingBridges().length + " distinct sibling bridges. The most likely of these is ";
					explanation += " the sibling bridge ID " + lS.getId() + " supported by records ";
				} else {
					explanation += "The sibling bridge ID " + lS.getId() + " also supports the pairing of these two people as siblings, ";
					explanation += "in this case supported by records ";
				}						
				explanation += addEvidenceToString(r);

			} else {

				explanation += "This sibling bridge is supported by ";
				explanation += "the sibling bridge ID " + lS.getId() + " supported by records ";
				explanation += addEvidenceToString(r);
			}

		}
		return explanation;
	}

	//	private static String addEvidenceToString(ResultObject r) {
	//		String explanation = "";
	//		Evidence[] evidence = getRecords(r);
	//		int c = 0;
	//		for(Evidence e : evidence) {
	//			explanation += e.getId();
	//			if(c == evidence.length - 1) {
	//				explanation += ". ";
	//			} else if(c == evidence.length - 2) {
	//				explanation += " and ";
	//			} else {
	//				explanation += ", ";
	//			}
	//			c++;
	//		}
	//		return explanation;
	//	}

	private static String addEvidenceToString(ResultObject r) {
		String explanation = "";
		Evidence[] allEvidence = getRecords(r);
		ArrayList<Evidence> temp = new ArrayList<Evidence>();
		for(Evidence e : allEvidence) {
			if(!temp.contains(e))
				temp.add(e);
		}

		Evidence[] evidence = temp.toArray(new Evidence[temp.size()]);

		int c = 0;
		for(Evidence e : evidence) {
			explanation += e.getId();
			if(c == evidence.length - 1) {
				explanation += ". ";
			} else if(c == evidence.length - 2) {
				explanation += " and ";
			} else {
				explanation += ", ";
			}
			c++;
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
