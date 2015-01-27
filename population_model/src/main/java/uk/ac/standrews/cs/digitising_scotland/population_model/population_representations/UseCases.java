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

import uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.adapted_db.ExportPopulationToDB;


public class UseCases {

	public static LinkedPopulation generateNuclearFamilyUseCase() {
		
		LinkedPopulation population = new LinkedPopulation("Nuclear Family Use Case");
		population.addPerson(new LinkedPerson(0, "alpha", "Family", 'M'));
		population.addPerson(new LinkedPerson(1, "beta", "Family", 'M'));
		population.addPerson(new LinkedPerson(2, "gamma", "Family", 'M'));
		population.addPerson(new LinkedPerson(3, "delta", "Family", 'M'));
		population.addPerson(new LinkedPerson(4, "epsilon", "Family", 'F'));
		population.addPerson(new LinkedPerson(5, "zeta", "Not", 'M'));
		population.addPerson(new LinkedPerson(6, "eta", "Not", 'F'));
		population.addPerson(new LinkedPerson(7, "theta", "Not", 'F'));
		population.addPerson(new LinkedPerson(8, "iota", "Not", 'F'));
		
		Evidence[] records = new Evidence[9];
		for(int i = 0; i < 9; i++)
			records[i] = new Evidence(i);
		
		population.addPartnership(new LinkedPartnership(0, "alef"));
		population.addPartnership(new LinkedPartnership(1, "bet"));
		population.addPartnership(new LinkedPartnership(2, "gimel"));
		
		population.getPartnershipByRef("alef").setChildLink(population.findPersonByFirstName("alpha"), new Evidence[]{records[2]});
		population.getPartnershipByRef("bet").setChildLink(population.findPersonByFirstName("beta"), new Evidence[]{records[1]});
		population.getPartnershipByRef("gimel").setChildLink(population.findPersonByFirstName("gamma"), new Evidence[]{records[0]});
		
		population.getPartnershipByRef("alef").addPossibleFatherLink(population.findPersonByFirstName("delta"), new Evidence[]{records[2], records[6]}, 0.8f);
		population.getPartnershipByRef("alef").addPossibleFatherLink(population.findPersonByFirstName("zeta"), new Evidence[]{records[2], records[7]}, 0.6f);
		
		population.getPartnershipByRef("alef").addPossibleMotherLink(population.findPersonByFirstName("iota"), new Evidence[]{records[2], records[5]}, 0.3f);
		population.getPartnershipByRef("alef").addPossibleMotherLink(population.findPersonByFirstName("theta"), new Evidence[]{records[2], records[4]}, 0.4f);
		population.getPartnershipByRef("alef").addPossibleMotherLink(population.findPersonByFirstName("epsilon"), new Evidence[]{records[2], records[3]}, 0.9f);
		
		population.getPartnershipByRef("bet").addPossibleFatherLink(population.findPersonByFirstName("delta"), new Evidence[]{records[1], records[6]}, 0.7f);
		
		population.getPartnershipByRef("bet").addPossibleMotherLink(population.findPersonByFirstName("epsilon"), new Evidence[]{records[1], records[3]}, 0.7f);
		population.getPartnershipByRef("bet").addPossibleMotherLink(population.findPersonByFirstName("eta"), new Evidence[]{records[1], records[8]}, 0.5f);
		
		population.getPartnershipByRef("gimel").addPossibleFatherLink(population.findPersonByFirstName("delta"), new Evidence[]{records[0], records[6]}, 0.9f);
		population.getPartnershipByRef("gimel").addPossibleFatherLink(population.findPersonByFirstName("zeta"), new Evidence[]{records[0], records[7]}, 0.5f);
		
		population.getPartnershipByRef("gimel").addPossibleMotherLink(population.findPersonByFirstName("epsilon"), new Evidence[]{records[0], records[3]}, 0.6f);
		
		return population;
	}
	
	public static void main(String[] args) {
		LinkedPopulation pop = generateNuclearFamilyUseCase();
		try {
			new ExportPopulationToDB(pop);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
