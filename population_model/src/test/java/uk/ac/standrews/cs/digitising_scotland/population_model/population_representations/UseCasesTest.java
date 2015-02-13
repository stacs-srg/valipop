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
	public void testNuclearFamilyUseCaseStructure() {
		LinkedPopulation pop = UseCases.generateNuclearFamilyUseCase();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	@Test
	public void tesNonCrossOverMultiGenerationUseCaseStructure() {
		LinkedPopulation pop = UseCases.generateNonCrossOverMultiGenerationUseCase();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	@Test
	public void testCrossOverMultiGenerationUseCaseStructure() {
		LinkedPopulation pop = UseCases.generateCrossOverMultiGenerationUseCase();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	@Test
	public void testSingleBestFitUseCaseStructure() {
		LinkedPopulation pop = UseCases.generateSingleBestFitUseCase();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	@Test
	public void testMaleLineUseCaseStructure() {
		LinkedPopulation pop = UseCases.generateMaleLineUseCase();
		testLinksInBothDirectionsForUseCase(pop);
	}

	@Test
	public void testCousinsUseCaseStructure() {
		LinkedPopulation pop = UseCases.generateCousinsUseCase();
		testLinksInBothDirectionsForUseCase(pop);
	}
	
	@Test
	public void testEnforcedSiblingsUseCaseStructure() {
		LinkedPopulation pop = UseCases.generateEnforcedSiblingsUseCase();
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
		DirectLink[] motherLinks = child.getParentsPartnership().getLinkedPartnership().getFemalePotentialPartnerLinks();
		LinkedPerson[] mothers = new LinkedPerson[motherLinks.length];
		int c = 0;
		for(DirectLink l : motherLinks) {
			mothers[c++] = (LinkedPerson) l.getLinkedPerson();
		}
		for(LinkedPerson m : mothers) {
			List<DirectLink> p = m.getPartnerships();
			List<LinkedPerson> children = new ArrayList<LinkedPerson>();
			for(DirectLink l : p) {
				children.add((LinkedPerson) l.getLinkedPartnership().getChildLink().getLinkedPerson());
			}
			
			Assert.assertTrue(children.contains(child));
		}
	}

	public void fatherAndChildLinked(LinkedPerson child) {
		if(child.getParentsPartnership() == null) {
			return;
		}
		DirectLink[] fatherLinks = child.getParentsPartnership().getLinkedPartnership().getMalePotentialPartnerLinks();
		LinkedPerson[] fathers = new LinkedPerson[fatherLinks.length];
		int c = 0;
		for(DirectLink l : fatherLinks) {
			fathers[c++] = (LinkedPerson) l.getLinkedPerson();
		}
		for(LinkedPerson m : fathers) {
			List<DirectLink> p = m.getPartnerships();
			List<LinkedPerson> children = new ArrayList<LinkedPerson>();
			for(DirectLink l : p) {
				children.add((LinkedPerson) l.getLinkedPartnership().getChildLink().getLinkedPerson());
			}
			Assert.assertTrue(children.contains(child));
		}
	}
	
	public void partnersLinked(LinkedPerson person) {
		if(person.getPartnerships().size() == 0)
			return;
		
		List<DirectLink> partnershipLinks = person.getPartnerships();
		List<DirectLink> partnerLinks = new ArrayList<DirectLink>();
		for(DirectLink l : partnershipLinks) {
			if(person.getSex() == 'M') {
				partnerLinks.addAll(Arrays.asList(l.getLinkedPartnership().getFemalePotentialPartnerLinks()));
			} else if(person.getSex() == 'F') {
				partnerLinks.addAll(Arrays.asList(l.getLinkedPartnership().getMalePotentialPartnerLinks()));
			}
		}
		List<LinkedPerson> partners = new ArrayList<LinkedPerson>();
		for(DirectLink l : partnerLinks) {
			partners.add((LinkedPerson) l.getLinkedPerson());
		}
		
		List<DirectLink> returnPartnershipLinks = new ArrayList<DirectLink>();
		List<DirectLink> returnPartnerLinks = new ArrayList<DirectLink>();
		List<LinkedPerson> returnPartners = new ArrayList<LinkedPerson>();
		
		for(LinkedPerson partner : partners) {
			returnPartnershipLinks.addAll(partner.getPartnerships());
		}
		
		for(DirectLink l : returnPartnershipLinks) {
			if(person.getSex() == 'M') {
				returnPartnerLinks.addAll(Arrays.asList(l.getLinkedPartnership().getMalePotentialPartnerLinks()));
			} else if(person.getSex() == 'F') {
				returnPartnerLinks.addAll(Arrays.asList(l.getLinkedPartnership().getFemalePotentialPartnerLinks()));
			}
		}
		
		for(DirectLink l : returnPartnerLinks) {
			returnPartners.add((LinkedPerson) l.getLinkedPerson());
		}
		Assert.assertTrue(returnPartners.contains(person));
		
	}
}
