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
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.adapted_interfaces.IPerson;

public class UseCasesTest {
	
	@Test
	public void testNuclearFamilyUseCase1Structure() {
		LinkedPopulation pop = UseCases.generateNuclearFamilyUseCase1();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	@Test
	public void testNuclearFamilyUseCase7Structure() {
		LinkedPopulation pop = UseCases.generateNuclearFamilyUseCase7();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	@Test
	public void testNuclearFamilyUseCase8Structure() {
		LinkedPopulation pop = UseCases.generateNuclearFamilyUseCase8();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	@Test
	public void testNuclearFamilyUseCase13Structure() {
		LinkedPopulation pop = UseCases.generateNuclearFamilyUseCase13();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	@Test
	public void testNuclearFamilyUseCase14Structure() {
		LinkedPopulation pop = UseCases.generateNuclearFamilyUseCase14();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	@Test
	public void testNonCrossOverMultiGenerationUseCase2Structure() {
		LinkedPopulation pop = UseCases.generateNonCrossOverMultiGenerationUseCase2();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	@Test
	public void testNonCrossOverMultiGenerationUseCase15Structure() {
		LinkedPopulation pop = UseCases.generateNonCrossOverMultiGenerationUseCase15();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	@Test
	public void testNonCrossOverMultiGenerationUseCase16Structure() {
		LinkedPopulation pop = UseCases.generateCrossOverMultiGenerationUseCase16();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	@Test
	public void testCrossOverMultiGenerationUseCase3Structure() {
		LinkedPopulation pop = UseCases.generateCrossOverMultiGenerationUseCase3();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	@Test
	public void tesNonCrossOverMultiGenerationUseCase9Structure() {
		LinkedPopulation pop = UseCases.generateNonCrossOverMultiGenerationUseCase9();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	@Test
	public void testCrossOverMultiGenerationUseCase11Structure() {
		LinkedPopulation pop = UseCases.generateCrossOverMultiGenerationUseCase11();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	@Test
	public void testCrossOverMultiGenerationUseCase17Structure() {
		LinkedPopulation pop = UseCases.generateCrossOverMultiGenerationUseCase17();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	@Test
	public void testSingleBestFitUseCase4Structure() {
		LinkedPopulation pop = UseCases.generateSingleBestFitUseCase4();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	@Test
	public void testMaleLineUseCase5Structure() {
		LinkedPopulation pop = UseCases.generateMaleLineUseCase5();
		testLinksInBothDirectionsForUseCase(pop);
	}

	@Test
	public void testCousinsUseCase6Structure() {
		LinkedPopulation pop = UseCases.generateCousinsUseCase6();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	@Test
	public void testCousinsUseCase10Structure() {
		LinkedPopulation pop = UseCases.generateCousinsUseCase10();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	@Test
	public void testCousinsUseCase18Structure() {
		LinkedPopulation pop = UseCases.generateCousinsUseCase18();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	@Test
	public void testEnforcedSiblingsUseCase12Structure() {
		LinkedPopulation pop = UseCases.generateEnforcedSiblingsUseCase12();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	
	
	public void testLinksInBothDirectionsForUseCase(LinkedPopulation pop) {
		Iterator<IPerson> i = pop.getPeople().iterator();
		while(i.hasNext()) {
			LinkedPerson p = (LinkedPerson) i.next();
			
			fatherAndChildLinked(p);
			motherAndChildLinked(p);
			partnersLinked(p);
		}
	}

	public void motherAndChildLinked(LinkedPerson child) {
		if(child.getParentsPartnership() == null) {
			return;
		}
		Link[] motherLinks = child.getParentsPartnership().getLinkedIntermediaryObject().getPerson1PotentialLinks();
		LinkedPerson[] mothers = new LinkedPerson[motherLinks.length];
		int c = 0;
		for(Link l : motherLinks) {
			mothers[c++] = (LinkedPerson) l.getLinkedPerson();
		}
		for(LinkedPerson m : mothers) {
			List<Link> p = m.getPartnerships();
			List<LinkedPerson> children = new ArrayList<LinkedPerson>();
			for(Link l : p) {
				children.add((LinkedPerson)((LinkedChildbearingPartnership) l.getLinkedIntermediaryObject()).getChildLink().getLinkedPerson());
			}
			
//			print(children);
//			print(child);
			
			
			Assert.assertTrue(children.contains(child));
		}
	}
	
	public void fatherAndChildLinked(LinkedPerson child) {
		if(child.getParentsPartnership() == null) {
			return;
		}
		Link[] fatherLinks = child.getParentsPartnership().getLinkedIntermediaryObject().getPerson2PotentialLinks();
		LinkedPerson[] fathers = new LinkedPerson[fatherLinks.length];
		int c = 0;
		for(Link l : fatherLinks) {
			fathers[c++] = (LinkedPerson) l.getLinkedPerson();
		}
		for(LinkedPerson m : fathers) {
			List<Link> p = m.getPartnerships();
			List<LinkedPerson> children = new ArrayList<LinkedPerson>();
			for(Link l : p) {
				children.add((LinkedPerson)((LinkedChildbearingPartnership) l.getLinkedIntermediaryObject()).getChildLink().getLinkedPerson());
			}
			Assert.assertTrue(children.contains(child));
		}
	}
	
	public void partnersLinked(LinkedPerson person) {
//		Utils.print(person);
//		System.out.println(person.getPartnerships().size());
		if(person.getPartnerships().size() == 0)
			return;
		
		List<Link> partnershipLinks = person.getPartnerships();
		List<Link> partnerLinks = new ArrayList<Link>();
		for(Link l : partnershipLinks) {
			if(person.getSex() == 'M') {
				partnerLinks.addAll(Arrays.asList(l.getLinkedIntermediaryObject().getPerson2PotentialLinks()));
			} else if(person.getSex() == 'F') {
				partnerLinks.addAll(Arrays.asList(l.getLinkedIntermediaryObject().getPerson1PotentialLinks()));
			}
		}
		List<LinkedPerson> partners = new ArrayList<LinkedPerson>();
		for(Link l : partnerLinks) {
			partners.add((LinkedPerson) l.getLinkedPerson());
		}
		
//		Utils.print(partners);
		
		List<Link> returnPartnershipLinks = new ArrayList<Link>();
		List<Link> returnPartnerLinks = new ArrayList<Link>();
		List<LinkedPerson> returnPartners = new ArrayList<LinkedPerson>();
		
		for(LinkedPerson partner : partners) {
			returnPartnershipLinks.addAll(partner.getPartnerships());
		}
		
		for(Link l : returnPartnershipLinks) {
			if(person.getSex() == 'M') {
				returnPartnerLinks.addAll(Arrays.asList(l.getLinkedIntermediaryObject().getPerson1PotentialLinks()));
			} else if(person.getSex() == 'F') {
				returnPartnerLinks.addAll(Arrays.asList(l.getLinkedIntermediaryObject().getPerson2PotentialLinks()));
			}
		}
		
		for(Link l : returnPartnerLinks) {
			returnPartners.add((LinkedPerson) l.getLinkedPerson());
		}
		
//		Utils.print(returnPartners);
//		Utils.print(person);
		
		Assert.assertTrue(returnPartners.contains(person));
		
	}
}
