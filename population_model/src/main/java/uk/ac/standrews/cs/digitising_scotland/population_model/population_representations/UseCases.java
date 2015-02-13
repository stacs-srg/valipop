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

		population.addPartnership(new LinkedChildbearingPartnership(0, "alef"));
		population.addPartnership(new LinkedChildbearingPartnership(1, "bet"));
		population.addPartnership(new LinkedChildbearingPartnership(2, "gimel"));

		population.getPartnershipByRef("alef").setChildLink(population.findPersonByFirstName("alpha"), new Evidence[]{records[2]});
		population.getPartnershipByRef("bet").setChildLink(population.findPersonByFirstName("beta"), new Evidence[]{records[1]});
		population.getPartnershipByRef("gimel").setChildLink(population.findPersonByFirstName("gamma"), new Evidence[]{records[0]});

		population.getPartnershipByRef("alef").addPossibleMaleLink(population.findPersonByFirstName("delta"), new Evidence[]{records[2], records[6]}, 0.8f);
		population.getPartnershipByRef("alef").addPossibleMaleLink(population.findPersonByFirstName("zeta"), new Evidence[]{records[2], records[7]}, 0.6f);

		population.getPartnershipByRef("alef").addPossibleFemaleLink(population.findPersonByFirstName("iota"), new Evidence[]{records[2], records[5]}, 0.3f);
		population.getPartnershipByRef("alef").addPossibleFemaleLink(population.findPersonByFirstName("theta"), new Evidence[]{records[2], records[4]}, 0.4f);
		population.getPartnershipByRef("alef").addPossibleFemaleLink(population.findPersonByFirstName("epsilon"), new Evidence[]{records[2], records[3]}, 0.9f);

		population.getPartnershipByRef("bet").addPossibleMaleLink(population.findPersonByFirstName("delta"), new Evidence[]{records[1], records[6]}, 0.7f);

		population.getPartnershipByRef("bet").addPossibleFemaleLink(population.findPersonByFirstName("epsilon"), new Evidence[]{records[1], records[3]}, 0.7f);
		population.getPartnershipByRef("bet").addPossibleFemaleLink(population.findPersonByFirstName("eta"), new Evidence[]{records[1], records[8]}, 0.5f);

		population.getPartnershipByRef("gimel").addPossibleMaleLink(population.findPersonByFirstName("delta"), new Evidence[]{records[0], records[6]}, 0.9f);
		population.getPartnershipByRef("gimel").addPossibleMaleLink(population.findPersonByFirstName("zeta"), new Evidence[]{records[0], records[7]}, 0.5f);

		population.getPartnershipByRef("gimel").addPossibleFemaleLink(population.findPersonByFirstName("epsilon"), new Evidence[]{records[0], records[3]}, 0.6f);

		return population;
	}

	public static LinkedPopulation generateNonCrossOverMultiGenerationUseCase() {

		LinkedPopulation population = new LinkedPopulation("Non Cross Over Multi Generaltion Use Case");
		population.addPerson(new LinkedPerson(0, "a", "Name", 'M'));
		population.addPerson(new LinkedPerson(1, "b", "Name", 'M'));
		population.addPerson(new LinkedPerson(2, "c", "Name", 'F'));
		population.addPerson(new LinkedPerson(3, "d", "Name", 'F'));
		population.addPerson(new LinkedPerson(4, "e", "Name", 'M'));
		population.addPerson(new LinkedPerson(5, "f", "Name", 'M'));
		population.addPerson(new LinkedPerson(6, "g", "Name", 'F'));
		population.addPerson(new LinkedPerson(7, "h", "Name", 'F'));
		population.addPerson(new LinkedPerson(8, "i", "Name", 'M'));
		population.addPerson(new LinkedPerson(9, "j", "Name", 'F'));


		Evidence[] records = new Evidence[10];
		for(int i = 0; i < 10; i++)
			records[i] = new Evidence(i);

		population.addPartnership(new LinkedChildbearingPartnership(0, "alpha"));
		population.addPartnership(new LinkedChildbearingPartnership(1, "beta"));
		population.addPartnership(new LinkedChildbearingPartnership(2, "gamma"));

		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("a"), new Evidence[]{records[0]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("g"), new Evidence[]{records[6]});
		population.getPartnershipByRef("gamma").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[2]});

		population.getPartnershipByRef("alpha").addPossibleMaleLink(population.findPersonByFirstName("b"), new Evidence[]{records[0], records[1]}, 0.8f);

		population.getPartnershipByRef("alpha").addPossibleFemaleLink(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[2]}, 0.6f);
		population.getPartnershipByRef("alpha").addPossibleFemaleLink(population.findPersonByFirstName("g"), new Evidence[]{records[0], records[6]}, 0.6f);

		population.getPartnershipByRef("beta").addPossibleMaleLink(population.findPersonByFirstName("e"), new Evidence[]{records[2], records[5]}, 0.7f);
		population.getPartnershipByRef("beta").addPossibleMaleLink(population.findPersonByFirstName("f"), new Evidence[]{records[2], records[4]}, 0.8f);

		population.getPartnershipByRef("beta").addPossibleFemaleLink(population.findPersonByFirstName("d"), new Evidence[]{records[3], records[2]}, 0.6f);

		population.getPartnershipByRef("gamma").addPossibleMaleLink(population.findPersonByFirstName("i"), new Evidence[]{records[6], records[7]}, 0.8f);

		population.getPartnershipByRef("gamma").addPossibleFemaleLink(population.findPersonByFirstName("j"), new Evidence[]{records[6], records[9]}, 0.6f);
		population.getPartnershipByRef("gamma").addPossibleFemaleLink(population.findPersonByFirstName("h"), new Evidence[]{records[6], records[8]}, 0.4f);

		return population;		

	}

	public static LinkedPopulation generateCrossOverMultiGenerationUseCase() {

		LinkedPopulation population = new LinkedPopulation("Cross Over Multi Generaltion Use Case");
		population.addPerson(new LinkedPerson(0, "a", "Name", 'M'));
		population.addPerson(new LinkedPerson(1, "b", "Name", 'M'));
		population.addPerson(new LinkedPerson(2, "c", "Name", 'F'));
		population.addPerson(new LinkedPerson(3, "d", "Name", 'F'));
		population.addPerson(new LinkedPerson(4, "e", "Name", 'M'));
		population.addPerson(new LinkedPerson(5, "f", "Name", 'M'));
		population.addPerson(new LinkedPerson(6, "g", "Name", 'F'));
		population.addPerson(new LinkedPerson(7, "h", "Name", 'F'));
		population.addPerson(new LinkedPerson(8, "i", "Name", 'M'));
		population.addPerson(new LinkedPerson(9, "j", "Name", 'F'));


		Evidence[] records = new Evidence[10];
		for(int i = 0; i < 10; i++)
			records[i] = new Evidence(i);

		population.addPartnership(new LinkedChildbearingPartnership(0, "alpha"));
		population.addPartnership(new LinkedChildbearingPartnership(1, "beta"));
		population.addPartnership(new LinkedChildbearingPartnership(2, "gamma"));

		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("a"), new Evidence[]{records[0]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("g"), new Evidence[]{records[6]});
		population.getPartnershipByRef("gamma").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[2]});

		population.getPartnershipByRef("alpha").addPossibleMaleLink(population.findPersonByFirstName("b"), new Evidence[]{records[0], records[1]}, 0.8f);

		population.getPartnershipByRef("alpha").addPossibleFemaleLink(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[2]}, 0.6f);
		population.getPartnershipByRef("alpha").addPossibleFemaleLink(population.findPersonByFirstName("g"), new Evidence[]{records[0], records[6]}, 0.6f);

		population.getPartnershipByRef("beta").addPossibleMaleLink(population.findPersonByFirstName("e"), new Evidence[]{records[2], records[5]}, 0.7f);
		population.getPartnershipByRef("beta").addPossibleMaleLink(population.findPersonByFirstName("f"), new Evidence[]{records[2], records[4]}, 0.8f);

		population.getPartnershipByRef("beta").addPossibleFemaleLink(population.findPersonByFirstName("d"), new Evidence[]{records[3], records[2]}, 0.6f);

		population.getPartnershipByRef("gamma").addPossibleMaleLink(population.findPersonByFirstName("i"), new Evidence[]{records[6], records[7]}, 0.8f);
		population.getPartnershipByRef("gamma").addPossibleMaleLink(population.findPersonByFirstName("e"), new Evidence[]{records[6], records[5]}, 0.9f);

		population.getPartnershipByRef("gamma").addPossibleFemaleLink(population.findPersonByFirstName("j"), new Evidence[]{records[6], records[9]}, 0.6f);
		population.getPartnershipByRef("gamma").addPossibleFemaleLink(population.findPersonByFirstName("h"), new Evidence[]{records[6], records[8]}, 0.4f);
		population.getPartnershipByRef("gamma").addPossibleFemaleLink(population.findPersonByFirstName("d"), new Evidence[]{records[6], records[3]}, 0.8f);

		return population;		

	}

	public static LinkedPopulation generateSingleBestFitUseCase() {

		LinkedPopulation population = new LinkedPopulation("Single Best Fit Use Case");
		population.addPerson(new LinkedPerson(0, "a", "Name", 'M'));
		population.addPerson(new LinkedPerson(1, "b", "Name", 'M'));
		population.addPerson(new LinkedPerson(2, "c", "Name", 'F'));
		population.addPerson(new LinkedPerson(3, "d", "Name", 'M'));
		population.addPerson(new LinkedPerson(4, "e", "Name", 'F'));
		population.addPerson(new LinkedPerson(5, "f", "Name", 'F'));
		population.addPerson(new LinkedPerson(6, "g", "Name", 'M'));
		population.addPerson(new LinkedPerson(7, "h", "Name", 'M'));
		population.addPerson(new LinkedPerson(8, "i", "Name", 'F'));
		population.addPerson(new LinkedPerson(9, "j", "Name", 'M'));
		population.addPerson(new LinkedPerson(10, "k", "Name", 'F'));
		population.addPerson(new LinkedPerson(11, "l", "Name", 'M'));
		population.addPerson(new LinkedPerson(12, "m", "Name", 'M'));
		population.addPerson(new LinkedPerson(13, "n", "Name", 'F'));
		population.addPerson(new LinkedPerson(14, "o", "Name", 'F'));


		Evidence[] records = new Evidence[15];
		for(int i = 0; i < 15; i++)
			records[i] = new Evidence(i);

		population.addPartnership(new LinkedChildbearingPartnership(0, "alpha"));
		population.addPartnership(new LinkedChildbearingPartnership(1, "beta"));
		population.addPartnership(new LinkedChildbearingPartnership(2, "gamma"));
		population.addPartnership(new LinkedChildbearingPartnership(3, "delta"));
		population.addPartnership(new LinkedChildbearingPartnership(4, "epsilon"));
		population.addPartnership(new LinkedChildbearingPartnership(5, "zeta"));
		population.addPartnership(new LinkedChildbearingPartnership(6, "eta"));

		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("a"), new Evidence[]{records[0]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("b"), new Evidence[]{records[1]});
		population.getPartnershipByRef("gamma").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[2]});
		population.getPartnershipByRef("delta").setChildLink(population.findPersonByFirstName("g"), new Evidence[]{records[3]});
		population.getPartnershipByRef("epsilon").setChildLink(population.findPersonByFirstName("f"), new Evidence[]{records[4]});
		population.getPartnershipByRef("zeta").setChildLink(population.findPersonByFirstName("d"), new Evidence[]{records[5]});
		population.getPartnershipByRef("eta").setChildLink(population.findPersonByFirstName("e"), new Evidence[]{records[6]});

		population.getPartnershipByRef("alpha").addPossibleMaleLink(population.findPersonByFirstName("b"), new Evidence[]{records[0], records[1]}, 0.9f);
		population.getPartnershipByRef("alpha").addPossibleFemaleLink(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[2]}, 0.9f);
		
		population.getPartnershipByRef("beta").addPossibleMaleLink(population.findPersonByFirstName("g"), new Evidence[]{records[3], records[1]}, 0.9f);
		population.getPartnershipByRef("beta").addPossibleFemaleLink(population.findPersonByFirstName("f"), new Evidence[]{records[1], records[4]}, 0.9f);
		
		population.getPartnershipByRef("gamma").addPossibleMaleLink(population.findPersonByFirstName("d"), new Evidence[]{records[2], records[5]}, 0.9f);
		population.getPartnershipByRef("gamma").addPossibleFemaleLink(population.findPersonByFirstName("e"), new Evidence[]{records[6], records[2]}, 0.9f);
		
		population.getPartnershipByRef("delta").addPossibleMaleLink(population.findPersonByFirstName("m"), new Evidence[]{records[3], records[7]}, 0.9f);
		population.getPartnershipByRef("delta").addPossibleFemaleLink(population.findPersonByFirstName("n"), new Evidence[]{records[3], records[8]}, 0.9f);
		
		population.getPartnershipByRef("epsilon").addPossibleMaleLink(population.findPersonByFirstName("l"), new Evidence[]{records[4], records[9]}, 0.9f);
		population.getPartnershipByRef("epsilon").addPossibleFemaleLink(population.findPersonByFirstName("o"), new Evidence[]{records[4], records[10]}, 0.9f);
		
		population.getPartnershipByRef("zeta").addPossibleMaleLink(population.findPersonByFirstName("h"), new Evidence[]{records[5], records[11]}, 0.9f);
		population.getPartnershipByRef("zeta").addPossibleFemaleLink(population.findPersonByFirstName("i"), new Evidence[]{records[5], records[12]}, 0.9f);
		
		population.getPartnershipByRef("eta").addPossibleMaleLink(population.findPersonByFirstName("j"), new Evidence[]{records[6], records[13]}, 0.9f);
		population.getPartnershipByRef("eta").addPossibleFemaleLink(population.findPersonByFirstName("k"), new Evidence[]{records[6], records[14]}, 0.9f);
		
		return population;
		
	}
	
	public static LinkedPopulation generateMaleLineUseCase() {

		LinkedPopulation population = new LinkedPopulation("Male Line Use Case");
		population.addPerson(new LinkedPerson(0, "a", "Name", 'M'));
		population.addPerson(new LinkedPerson(1, "b", "Name", 'F'));
		population.addPerson(new LinkedPerson(2, "c", "Name", 'M'));
		population.addPerson(new LinkedPerson(3, "d", "Name", 'F'));
		population.addPerson(new LinkedPerson(4, "e", "Name", 'M'));
		population.addPerson(new LinkedPerson(5, "f", "Name", 'F'));
		population.addPerson(new LinkedPerson(6, "g", "Name", 'M'));
		population.addPerson(new LinkedPerson(7, "h", "Name", 'F'));
		population.addPerson(new LinkedPerson(8, "i", "Name", 'M'));
		population.addPerson(new LinkedPerson(9, "j", "Name", 'F'));
		population.addPerson(new LinkedPerson(10, "k", "Name", 'M'));
		population.addPerson(new LinkedPerson(11, "l", "Name", 'F'));
		population.addPerson(new LinkedPerson(12, "m", "Name", 'M'));
		population.addPerson(new LinkedPerson(13, "n", "Name", 'F'));
		population.addPerson(new LinkedPerson(14, "o", "Name", 'M'));
		population.addPerson(new LinkedPerson(15, "p", "Name", 'F'));
		population.addPerson(new LinkedPerson(16, "q", "Name", 'M'));
		population.addPerson(new LinkedPerson(17, "r", "Name", 'F'));
		population.addPerson(new LinkedPerson(18, "s", "Name", 'M'));
		population.addPerson(new LinkedPerson(19, "t", "Name", 'F'));
		population.addPerson(new LinkedPerson(20, "u", "Name", 'M'));
		population.addPerson(new LinkedPerson(21, "v", "Name", 'M'));
		population.addPerson(new LinkedPerson(22, "w", "Name", 'M'));


		Evidence[] records = new Evidence[22];
		for(int i = 0; i < 22; i++)
			records[i] = new Evidence(i);

		population.addPartnership(new LinkedChildbearingPartnership(0, "alpha"));
		population.addPartnership(new LinkedChildbearingPartnership(1, "beta"));
		population.addPartnership(new LinkedChildbearingPartnership(2, "gamma"));
		population.addPartnership(new LinkedChildbearingPartnership(3, "delta"));
		population.addPartnership(new LinkedChildbearingPartnership(4, "epsilon"));
		population.addPartnership(new LinkedChildbearingPartnership(5, "zeta"));
		population.addPartnership(new LinkedChildbearingPartnership(6, "eta"));
		population.addPartnership(new LinkedChildbearingPartnership(7, "theta"));
		population.addPartnership(new LinkedChildbearingPartnership(8, "iota"));
		population.addPartnership(new LinkedChildbearingPartnership(9, "kappa"));

		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("a"), new Evidence[]{records[0]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[2]});
		population.getPartnershipByRef("gamma").setChildLink(population.findPersonByFirstName("e"), new Evidence[]{records[4]});
		population.getPartnershipByRef("delta").setChildLink(population.findPersonByFirstName("g"), new Evidence[]{records[17]});
		population.getPartnershipByRef("epsilon").setChildLink(population.findPersonByFirstName("i"), new Evidence[]{records[19]});
		population.getPartnershipByRef("zeta").setChildLink(population.findPersonByFirstName("v"), new Evidence[]{records[5]});
		population.getPartnershipByRef("eta").setChildLink(population.findPersonByFirstName("m"), new Evidence[]{records[7]});
		population.getPartnershipByRef("theta").setChildLink(population.findPersonByFirstName("w"), new Evidence[]{records[8]});
		population.getPartnershipByRef("iota").setChildLink(population.findPersonByFirstName("o"), new Evidence[]{records[10]});
		population.getPartnershipByRef("kappa").setChildLink(population.findPersonByFirstName("s"), new Evidence[]{records[14]});

		population.getPartnershipByRef("alpha").addPossibleMaleLink(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[2]}, 0.9f);
		population.getPartnershipByRef("alpha").addPossibleFemaleLink(population.findPersonByFirstName("b"), new Evidence[]{records[0], records[1]}, 0.9f);

		population.getPartnershipByRef("beta").addPossibleMaleLink(population.findPersonByFirstName("e"), new Evidence[]{records[2], records[4]}, 0.7f);
		population.getPartnershipByRef("beta").addPossibleMaleLink(population.findPersonByFirstName("v"), new Evidence[]{records[2], records[5]}, 0.6f);
		population.getPartnershipByRef("beta").addPossibleFemaleLink(population.findPersonByFirstName("d"), new Evidence[]{records[2], records[3]}, 0.9f);
		
		population.getPartnershipByRef("gamma").addPossibleMaleLink(population.findPersonByFirstName("g"), new Evidence[]{records[4], records[17]}, 0.9f);
		population.getPartnershipByRef("gamma").addPossibleFemaleLink(population.findPersonByFirstName("f"), new Evidence[]{records[4], records[16]}, 0.9f);
		
		population.getPartnershipByRef("delta").addPossibleMaleLink(population.findPersonByFirstName("i"), new Evidence[]{records[17], records[19]}, 0.9f);
		population.getPartnershipByRef("delta").addPossibleFemaleLink(population.findPersonByFirstName("h"), new Evidence[]{records[17], records[18]}, 0.9f);
		
		population.getPartnershipByRef("epsilon").addPossibleMaleLink(population.findPersonByFirstName("k"), new Evidence[]{records[19], records[21]}, 0.9f);
		population.getPartnershipByRef("epsilon").addPossibleFemaleLink(population.findPersonByFirstName("j"), new Evidence[]{records[19], records[20]}, 0.9f);

		population.getPartnershipByRef("zeta").addPossibleMaleLink(population.findPersonByFirstName("m"), new Evidence[]{records[5], records[7]}, 0.7f);
		population.getPartnershipByRef("zeta").addPossibleMaleLink(population.findPersonByFirstName("w"), new Evidence[]{records[5], records[8]}, 0.6f);
		population.getPartnershipByRef("zeta").addPossibleFemaleLink(population.findPersonByFirstName("l"), new Evidence[]{records[5], records[6]}, 0.9f);
		
		population.getPartnershipByRef("eta").addPossibleMaleLink(population.findPersonByFirstName("o"), new Evidence[]{records[7], records[10]}, 0.9f);
		population.getPartnershipByRef("eta").addPossibleFemaleLink(population.findPersonByFirstName("n"), new Evidence[]{records[7], records[9]}, 0.9f);
		
		population.getPartnershipByRef("theta").addPossibleMaleLink(population.findPersonByFirstName("s"), new Evidence[]{records[8], records[14]}, 0.9f);
		population.getPartnershipByRef("theta").addPossibleFemaleLink(population.findPersonByFirstName("r"), new Evidence[]{records[8], records[13]}, 0.9f);
		
		population.getPartnershipByRef("iota").addPossibleMaleLink(population.findPersonByFirstName("q"), new Evidence[]{records[10], records[12]}, 0.9f);
		population.getPartnershipByRef("iota").addPossibleFemaleLink(population.findPersonByFirstName("p"), new Evidence[]{records[10], records[11]}, 0.9f);
		
		population.getPartnershipByRef("kappa").addPossibleMaleLink(population.findPersonByFirstName("u"), new Evidence[]{records[14], records[15]}, 0.9f);
		population.getPartnershipByRef("kappa").addPossibleFemaleLink(population.findPersonByFirstName("t"), new Evidence[]{records[14], records[16]}, 0.9f);
		
		
		return population;
		
	}
	
	public static LinkedPopulation generateCousinsUseCase() {

		LinkedPopulation population = new LinkedPopulation("Cousins Use Case");
		population.addPerson(new LinkedPerson(0, "a", "Name", 'M'));
		population.addPerson(new LinkedPerson(1, "b", "Name", 'M'));
		population.addPerson(new LinkedPerson(2, "c", "Name", 'F'));
		population.addPerson(new LinkedPerson(3, "d", "Name", 'M'));
		population.addPerson(new LinkedPerson(4, "e", "Name", 'F'));
		population.addPerson(new LinkedPerson(5, "f", "Name", 'F'));
		population.addPerson(new LinkedPerson(6, "g", "Name", 'F'));
		population.addPerson(new LinkedPerson(7, "h", "Name", 'M'));
		population.addPerson(new LinkedPerson(8, "i", "Name", 'F'));
		population.addPerson(new LinkedPerson(9, "j", "Name", 'M'));
		population.addPerson(new LinkedPerson(10, "k", "Name", 'M'));
		population.addPerson(new LinkedPerson(11, "l", "Name", 'M'));
		population.addPerson(new LinkedPerson(12, "m", "Name", 'M'));
		
		Evidence[] records = new Evidence[13];
		for(int i = 0; i < 13; i++)
			records[i] = new Evidence(i);

		population.addPartnership(new LinkedChildbearingPartnership(0, "alpha"));
		population.addPartnership(new LinkedChildbearingPartnership(1, "beta"));
		population.addPartnership(new LinkedChildbearingPartnership(2, "gamma"));
		population.addPartnership(new LinkedChildbearingPartnership(3, "delta"));
		population.addPartnership(new LinkedChildbearingPartnership(4, "epsilon"));
		population.addPartnership(new LinkedChildbearingPartnership(5, "zeta"));
		
		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("a"), new Evidence[]{records[0]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("k"), new Evidence[]{records[11]});
		population.getPartnershipByRef("gamma").setChildLink(population.findPersonByFirstName("m"), new Evidence[]{records[9]});
		population.getPartnershipByRef("delta").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[2]});
		population.getPartnershipByRef("epsilon").setChildLink(population.findPersonByFirstName("g"), new Evidence[]{records[6]});
		population.getPartnershipByRef("zeta").setChildLink(population.findPersonByFirstName("i"), new Evidence[]{records[7]});
		
		population.getPartnershipByRef("alpha").addPossibleMaleLink(population.findPersonByFirstName("b"), new Evidence[]{records[0], records[1]}, 0.9f);
		population.getPartnershipByRef("alpha").addPossibleFemaleLink(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[2]}, 0.9f);

		population.getPartnershipByRef("beta").addPossibleMaleLink(population.findPersonByFirstName("j"), new Evidence[]{records[11], records[12]}, 0.7f);
		population.getPartnershipByRef("beta").addPossibleFemaleLink(population.findPersonByFirstName("g"), new Evidence[]{records[6], records[11]}, 0.9f);
		
		population.getPartnershipByRef("gamma").addPossibleMaleLink(population.findPersonByFirstName("l"), new Evidence[]{records[9], records[10]}, 0.9f);
		population.getPartnershipByRef("gamma").addPossibleFemaleLink(population.findPersonByFirstName("i"), new Evidence[]{records[7], records[9]}, 0.9f);
		
		population.getPartnershipByRef("delta").addPossibleMaleLink(population.findPersonByFirstName("d"), new Evidence[]{records[2], records[3]}, 0.9f);
		population.getPartnershipByRef("delta").addPossibleFemaleLink(population.findPersonByFirstName("e"), new Evidence[]{records[2], records[4]}, 0.9f);
		population.getPartnershipByRef("delta").addPossibleFemaleLink(population.findPersonByFirstName("f"), new Evidence[]{records[2], records[5]}, 0.9f);
		
		population.getPartnershipByRef("epsilon").addPossibleMaleLink(population.findPersonByFirstName("d"), new Evidence[]{records[3], records[6]}, 0.9f);
		population.getPartnershipByRef("epsilon").addPossibleFemaleLink(population.findPersonByFirstName("f"), new Evidence[]{records[5], records[6]}, 0.9f);

		population.getPartnershipByRef("zeta").addPossibleMaleLink(population.findPersonByFirstName("h"), new Evidence[]{records[7], records[8]}, 0.6f);
		population.getPartnershipByRef("zeta").addPossibleFemaleLink(population.findPersonByFirstName("e"), new Evidence[]{records[4], records[7]}, 0.9f);
		
		return population;
		
	}
	
	
	public static LinkedPopulation generateEnforcedSiblingsUseCase() {
		
		LinkedPopulation population = new LinkedPopulation("Enforced Siblings Use Case");
		population.addPerson(new LinkedPerson(0, "a", "Name", 'M'));
		population.addPerson(new LinkedPerson(1, "b", "Name", 'M'));
		population.addPerson(new LinkedPerson(2, "c", "Name", 'M'));
		population.addPerson(new LinkedPerson(3, "d", "Name", 'F'));
		population.addPerson(new LinkedPerson(4, "e", "Name", 'M'));
		population.addPerson(new LinkedPerson(5, "f", "Name", 'F'));
		population.addPerson(new LinkedPerson(6, "g", "Name", 'F'));
		
		Evidence[] records = new Evidence[8];
		for(int i = 0; i < 8; i++)
			records[i] = new Evidence(i);

		population.addPartnership(new LinkedChildbearingPartnership(0, "alpha"));
		population.addPartnership(new LinkedChildbearingPartnership(1, "beta"));
		
		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[0]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("g"), new Evidence[]{records[1]});
		
		population.getPartnershipByRef("alpha").addPossibleMaleLink(population.findPersonByFirstName("a"), new Evidence[]{records[0], records[3]}, 0.9f);
		population.getPartnershipByRef("alpha").addPossibleMaleLink(population.findPersonByFirstName("b"), new Evidence[]{records[0], records[2]}, 0.9f);
		population.getPartnershipByRef("alpha").addPossibleFemaleLink(population.findPersonByFirstName("d"), new Evidence[]{records[0], records[4]}, 0.9f);

		population.getPartnershipByRef("beta").addPossibleMaleLink(population.findPersonByFirstName("a"), new Evidence[]{records[1], records[3]}, 0.7f);
		population.getPartnershipByRef("beta").addPossibleMaleLink(population.findPersonByFirstName("e"), new Evidence[]{records[1], records[5]}, 0.7f);
		population.getPartnershipByRef("beta").addPossibleFemaleLink(population.findPersonByFirstName("d"), new Evidence[]{records[1], records[4]}, 0.9f);
		population.getPartnershipByRef("beta").addPossibleFemaleLink(population.findPersonByFirstName("f"), new Evidence[]{records[1], records[6]}, 0.9f);
		
		population.addSiblingsObject(new LinkedSiblings(0, "alef"));
		population.getSiblingsObjectByRef("alef").addPossibleSibling1Link(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[7]}, 1f);
		population.getSiblingsObjectByRef("alef").addPossibleSibling2Link(population.findPersonByFirstName("g"), new Evidence[]{records[1], records[7]}, 1f);
		
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
