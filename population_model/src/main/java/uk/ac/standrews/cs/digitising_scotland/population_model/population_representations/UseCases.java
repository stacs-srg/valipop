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

public class UseCases {

	public static LinkedPopulation generateNuclearFamilyUseCase1() {

		LinkedPopulation population = new LinkedPopulation("Nuclear Family Use Case");
		population.addPerson(new LinkedPerson(0, "a", "Family", 'M'));
		population.addPerson(new LinkedPerson(1, "b", "Family", 'M'));
		population.addPerson(new LinkedPerson(2, "c", "Family", 'M'));
		population.addPerson(new LinkedPerson(3, "d", "Family", 'M'));
		population.addPerson(new LinkedPerson(4, "e", "Family", 'F'));
		population.addPerson(new LinkedPerson(5, "f", "Not", 'M'));
		population.addPerson(new LinkedPerson(6, "g", "Not", 'F'));
		population.addPerson(new LinkedPerson(7, "h", "Not", 'F'));
		population.addPerson(new LinkedPerson(8, "i", "Not", 'F'));

		Evidence[] records = new Evidence[9];
		for(int i = 0; i < 9; i++)
			records[i] = new Evidence(i);

		population.addPartnership(new ChildbearingPartnership(0, "alpha"));
		population.addPartnership(new ChildbearingPartnership(1, "beta"));
		population.addPartnership(new ChildbearingPartnership(2, "gamma"));

		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("a"), new Evidence[]{records[2]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("b"), new Evidence[]{records[1]});
		population.getPartnershipByRef("gamma").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[0]});

		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[2], records[6]}, 0.8f);
		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("f"), new Evidence[]{records[2], records[7]}, 0.6f);

		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("i"), new Evidence[]{records[2], records[5]}, 0.3f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("h"), new Evidence[]{records[2], records[4]}, 0.4f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[2], records[3]}, 0.9f);

		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[1], records[6]}, 0.7f);

		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[1], records[3]}, 0.7f);
		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("g"), new Evidence[]{records[1], records[8]}, 0.5f);

		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[0], records[6]}, 0.9f);
		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("f"), new Evidence[]{records[0], records[7]}, 0.5f);

		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[0], records[3]}, 0.6f);

		return population;
	}

	public static LinkedPopulation generateNonCrossOverMultiGenerationUseCase2() {

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

		population.addPartnership(new ChildbearingPartnership(0, "alpha"));
		population.addPartnership(new ChildbearingPartnership(1, "beta"));
		population.addPartnership(new ChildbearingPartnership(2, "gamma"));

		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("a"), new Evidence[]{records[0]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("g"), new Evidence[]{records[6]});
		population.getPartnershipByRef("gamma").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[2]});

		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("b"), new Evidence[]{records[0], records[1]}, 0.8f);

		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[2]}, 0.6f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("g"), new Evidence[]{records[0], records[6]}, 0.6f);

		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("e"), new Evidence[]{records[2], records[5]}, 0.7f);
		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("f"), new Evidence[]{records[2], records[4]}, 0.8f);

		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("d"), new Evidence[]{records[3], records[2]}, 0.6f);

		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("i"), new Evidence[]{records[6], records[7]}, 0.8f);

		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("j"), new Evidence[]{records[6], records[9]}, 0.6f);
		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("h"), new Evidence[]{records[6], records[8]}, 0.4f);

		return population;		

	}

	public static LinkedPopulation generateCrossOverMultiGenerationUseCase3() {

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

		population.addPartnership(new ChildbearingPartnership(0, "alpha"));
		population.addPartnership(new ChildbearingPartnership(1, "beta"));
		population.addPartnership(new ChildbearingPartnership(2, "gamma"));

		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("a"), new Evidence[]{records[0]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("g"), new Evidence[]{records[6]});
		population.getPartnershipByRef("gamma").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[2]});

		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("b"), new Evidence[]{records[0], records[1]}, 0.8f);

		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[2]}, 0.6f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("g"), new Evidence[]{records[0], records[6]}, 0.6f);

		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("e"), new Evidence[]{records[2], records[5]}, 0.7f);
		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("f"), new Evidence[]{records[2], records[4]}, 0.8f);

		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("d"), new Evidence[]{records[3], records[2]}, 0.6f);

		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("i"), new Evidence[]{records[6], records[7]}, 0.8f);
		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("e"), new Evidence[]{records[6], records[5]}, 0.9f);

		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("j"), new Evidence[]{records[6], records[9]}, 0.6f);
		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("h"), new Evidence[]{records[6], records[8]}, 0.4f);
		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("d"), new Evidence[]{records[6], records[3]}, 0.8f);

		return population;		

	}

	public static LinkedPopulation generateSingleBestFitUseCase4() {

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

		population.addPartnership(new ChildbearingPartnership(0, "alpha"));
		population.addPartnership(new ChildbearingPartnership(1, "beta"));
		population.addPartnership(new ChildbearingPartnership(2, "gamma"));
		population.addPartnership(new ChildbearingPartnership(3, "delta"));
		population.addPartnership(new ChildbearingPartnership(4, "epsilon"));
		population.addPartnership(new ChildbearingPartnership(5, "zeta"));
		population.addPartnership(new ChildbearingPartnership(6, "eta"));

		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("a"), new Evidence[]{records[0]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("b"), new Evidence[]{records[1]});
		population.getPartnershipByRef("gamma").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[2]});
		population.getPartnershipByRef("delta").setChildLink(population.findPersonByFirstName("g"), new Evidence[]{records[3]});
		population.getPartnershipByRef("epsilon").setChildLink(population.findPersonByFirstName("f"), new Evidence[]{records[4]});
		population.getPartnershipByRef("zeta").setChildLink(population.findPersonByFirstName("d"), new Evidence[]{records[5]});
		population.getPartnershipByRef("eta").setChildLink(population.findPersonByFirstName("e"), new Evidence[]{records[6]});

		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("b"), new Evidence[]{records[0], records[1]}, 0.9f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[2]}, 0.9f);

		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("g"), new Evidence[]{records[3], records[1]}, 0.9f);
		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("f"), new Evidence[]{records[1], records[4]}, 0.9f);

		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[2], records[5]}, 0.9f);
		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[6], records[2]}, 0.9f);

		population.getPartnershipByRef("delta").addPossiblePerson1Link(population.findPersonByFirstName("m"), new Evidence[]{records[3], records[7]}, 0.9f);
		population.getPartnershipByRef("delta").addPossiblePerson2Link(population.findPersonByFirstName("n"), new Evidence[]{records[3], records[8]}, 0.9f);

		population.getPartnershipByRef("epsilon").addPossiblePerson1Link(population.findPersonByFirstName("l"), new Evidence[]{records[4], records[9]}, 0.9f);
		population.getPartnershipByRef("epsilon").addPossiblePerson2Link(population.findPersonByFirstName("o"), new Evidence[]{records[4], records[10]}, 0.9f);

		population.getPartnershipByRef("zeta").addPossiblePerson1Link(population.findPersonByFirstName("h"), new Evidence[]{records[5], records[11]}, 0.9f);
		population.getPartnershipByRef("zeta").addPossiblePerson2Link(population.findPersonByFirstName("i"), new Evidence[]{records[5], records[12]}, 0.9f);

		population.getPartnershipByRef("eta").addPossiblePerson1Link(population.findPersonByFirstName("j"), new Evidence[]{records[6], records[13]}, 0.9f);
		population.getPartnershipByRef("eta").addPossiblePerson2Link(population.findPersonByFirstName("k"), new Evidence[]{records[6], records[14]}, 0.9f);

		return population;

	}

	public static LinkedPopulation generateMaleLineUseCase5() {

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

		population.addPartnership(new ChildbearingPartnership(0, "alpha"));
		population.addPartnership(new ChildbearingPartnership(1, "beta"));
		population.addPartnership(new ChildbearingPartnership(2, "gamma"));
		population.addPartnership(new ChildbearingPartnership(3, "delta"));
		population.addPartnership(new ChildbearingPartnership(4, "epsilon"));
		population.addPartnership(new ChildbearingPartnership(5, "zeta"));
		population.addPartnership(new ChildbearingPartnership(6, "eta"));
		population.addPartnership(new ChildbearingPartnership(7, "theta"));
		population.addPartnership(new ChildbearingPartnership(8, "iota"));
		population.addPartnership(new ChildbearingPartnership(9, "kappa"));

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

		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[2]}, 0.9f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("b"), new Evidence[]{records[0], records[1]}, 0.9f);

		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("e"), new Evidence[]{records[2], records[4]}, 0.7f);
		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("v"), new Evidence[]{records[2], records[5]}, 0.6f);
		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("d"), new Evidence[]{records[2], records[3]}, 0.9f);

		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("g"), new Evidence[]{records[4], records[17]}, 0.9f);
		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("f"), new Evidence[]{records[4], records[16]}, 0.9f);

		population.getPartnershipByRef("delta").addPossiblePerson1Link(population.findPersonByFirstName("i"), new Evidence[]{records[17], records[19]}, 0.9f);
		population.getPartnershipByRef("delta").addPossiblePerson2Link(population.findPersonByFirstName("h"), new Evidence[]{records[17], records[18]}, 0.9f);

		population.getPartnershipByRef("epsilon").addPossiblePerson1Link(population.findPersonByFirstName("k"), new Evidence[]{records[19], records[21]}, 0.9f);
		population.getPartnershipByRef("epsilon").addPossiblePerson2Link(population.findPersonByFirstName("j"), new Evidence[]{records[19], records[20]}, 0.9f);

		population.getPartnershipByRef("zeta").addPossiblePerson1Link(population.findPersonByFirstName("m"), new Evidence[]{records[5], records[7]}, 0.7f);
		population.getPartnershipByRef("zeta").addPossiblePerson1Link(population.findPersonByFirstName("w"), new Evidence[]{records[5], records[8]}, 0.6f);
		population.getPartnershipByRef("zeta").addPossiblePerson2Link(population.findPersonByFirstName("l"), new Evidence[]{records[5], records[6]}, 0.9f);

		population.getPartnershipByRef("eta").addPossiblePerson1Link(population.findPersonByFirstName("o"), new Evidence[]{records[7], records[10]}, 0.9f);
		population.getPartnershipByRef("eta").addPossiblePerson2Link(population.findPersonByFirstName("n"), new Evidence[]{records[7], records[9]}, 0.9f);

		population.getPartnershipByRef("theta").addPossiblePerson1Link(population.findPersonByFirstName("s"), new Evidence[]{records[8], records[14]}, 0.9f);
		population.getPartnershipByRef("theta").addPossiblePerson2Link(population.findPersonByFirstName("r"), new Evidence[]{records[8], records[13]}, 0.9f);

		population.getPartnershipByRef("iota").addPossiblePerson1Link(population.findPersonByFirstName("q"), new Evidence[]{records[10], records[12]}, 0.9f);
		population.getPartnershipByRef("iota").addPossiblePerson2Link(population.findPersonByFirstName("p"), new Evidence[]{records[10], records[11]}, 0.9f);

		population.getPartnershipByRef("kappa").addPossiblePerson1Link(population.findPersonByFirstName("u"), new Evidence[]{records[14], records[15]}, 0.9f);
		population.getPartnershipByRef("kappa").addPossiblePerson2Link(population.findPersonByFirstName("t"), new Evidence[]{records[14], records[16]}, 0.9f);


		return population;

	}

	public static LinkedPopulation generateCousinsUseCase6() {

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

		population.addPartnership(new ChildbearingPartnership(0, "alpha"));
		population.addPartnership(new ChildbearingPartnership(1, "beta"));
		population.addPartnership(new ChildbearingPartnership(2, "gamma"));
		population.addPartnership(new ChildbearingPartnership(3, "delta"));
		population.addPartnership(new ChildbearingPartnership(4, "epsilon"));
		population.addPartnership(new ChildbearingPartnership(5, "zeta"));

		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("a"), new Evidence[]{records[0]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("k"), new Evidence[]{records[11]});
		population.getPartnershipByRef("gamma").setChildLink(population.findPersonByFirstName("m"), new Evidence[]{records[9]});
		population.getPartnershipByRef("delta").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[2]});
		population.getPartnershipByRef("epsilon").setChildLink(population.findPersonByFirstName("g"), new Evidence[]{records[6]});
		population.getPartnershipByRef("zeta").setChildLink(population.findPersonByFirstName("i"), new Evidence[]{records[7]});

		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("b"), new Evidence[]{records[0], records[1]}, 0.9f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[2]}, 0.9f);

		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("j"), new Evidence[]{records[11], records[12]}, 0.7f);
		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("g"), new Evidence[]{records[6], records[11]}, 0.9f);

		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("l"), new Evidence[]{records[9], records[10]}, 0.9f);
		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("i"), new Evidence[]{records[7], records[9]}, 0.9f);

		population.getPartnershipByRef("delta").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[2], records[3]}, 0.9f);
		population.getPartnershipByRef("delta").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[2], records[4]}, 0.9f);
		population.getPartnershipByRef("delta").addPossiblePerson2Link(population.findPersonByFirstName("f"), new Evidence[]{records[2], records[5]}, 0.9f);

		population.getPartnershipByRef("epsilon").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[3], records[6]}, 0.9f);
		population.getPartnershipByRef("epsilon").addPossiblePerson2Link(population.findPersonByFirstName("f"), new Evidence[]{records[5], records[6]}, 0.9f);

		population.getPartnershipByRef("zeta").addPossiblePerson1Link(population.findPersonByFirstName("h"), new Evidence[]{records[7], records[8]}, 0.6f);
		population.getPartnershipByRef("zeta").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[4], records[7]}, 0.9f);

		return population;

	}

	public static LinkedPopulation generateNuclearFamilyUseCase7() {

		LinkedPopulation population = new LinkedPopulation("Nuclear Family Use Case");
		population.addPerson(new LinkedPerson(0, "a", "Family", 'M'));
		population.addPerson(new LinkedPerson(1, "b", "Family", 'M'));
		population.addPerson(new LinkedPerson(2, "c", "Family", 'M'));
		population.addPerson(new LinkedPerson(3, "d", "Family", 'M'));
		population.addPerson(new LinkedPerson(4, "e", "Family", 'F'));
		population.addPerson(new LinkedPerson(5, "f", "Not", 'M'));
		population.addPerson(new LinkedPerson(6, "g", "Not", 'F'));
		population.addPerson(new LinkedPerson(7, "h", "Not", 'F'));
		population.addPerson(new LinkedPerson(8, "i", "Not", 'F'));

		Evidence[] records = new Evidence[11];
		for(int i = 0; i < 11; i++)
			records[i] = new Evidence(i);

		population.addPartnership(new ChildbearingPartnership(0, "alpha"));
		population.addPartnership(new ChildbearingPartnership(1, "beta"));
		population.addPartnership(new ChildbearingPartnership(2, "gamma"));

		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("a"), new Evidence[]{records[2]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("b"), new Evidence[]{records[1]});
		population.getPartnershipByRef("gamma").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[0]});

		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[2], records[6]}, 0.8f);
		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("f"), new Evidence[]{records[2], records[7]}, 0.6f);

		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("i"), new Evidence[]{records[2], records[5]}, 0.3f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("h"), new Evidence[]{records[2], records[4]}, 0.4f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[2], records[3]}, 0.9f);

		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[1], records[6]}, 0.7f);

		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[1], records[3]}, 0.7f);
		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("g"), new Evidence[]{records[1], records[8]}, 0.5f);

		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[0], records[6]}, 0.9f);
		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("f"), new Evidence[]{records[0], records[7]}, 0.5f);

		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[0], records[3]}, 0.6f);

		population.addSiblingsObject(new SiblingBridge(0, "alef"));
		population.addSiblingsObject(new SiblingBridge(1, "bet"));
		population.addSiblingsObject(new SiblingBridge(2, "gimel"));

		population.getSiblingsObjectByRef("alef").addPossiblePerson1Link(population.findPersonByFirstName("a"), new Evidence[]{records[2], records[8]}, 0.8f);
		population.getSiblingsObjectByRef("alef").addPossiblePerson2Link(population.findPersonByFirstName("b"), new Evidence[]{records[1], records[8]}, 0.8f);

		population.getSiblingsObjectByRef("bet").addPossiblePerson1Link(population.findPersonByFirstName("a"), new Evidence[]{records[2], records[9]}, 0.8f);
		population.getSiblingsObjectByRef("bet").addPossiblePerson2Link(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[9]}, 0.8f);

		population.getSiblingsObjectByRef("gimel").addPossiblePerson1Link(population.findPersonByFirstName("b"), new Evidence[]{records[1], records[10]}, 0.8f);
		population.getSiblingsObjectByRef("gimel").addPossiblePerson2Link(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[10]}, 0.8f);

		return population;
	}

	public static LinkedPopulation generateNuclearFamilyUseCase8() {

		LinkedPopulation population = new LinkedPopulation("Nuclear Family Use Case");
		population.addPerson(new LinkedPerson(0, "a", "Family", 'M'));
		population.addPerson(new LinkedPerson(1, "b", "Family", 'M'));
		population.addPerson(new LinkedPerson(2, "c", "Family", 'M'));
		population.addPerson(new LinkedPerson(3, "d", "Family", 'M'));
		population.addPerson(new LinkedPerson(4, "e", "Family", 'F'));
		population.addPerson(new LinkedPerson(5, "f", "Not", 'M'));
		population.addPerson(new LinkedPerson(6, "g", "Not", 'F'));
		population.addPerson(new LinkedPerson(7, "h", "Not", 'F'));
		population.addPerson(new LinkedPerson(8, "i", "Not", 'F'));

		Evidence[] records = new Evidence[9];
		for(int i = 0; i < 9; i++)
			records[i] = new Evidence(i);

		population.addPartnership(new ChildbearingPartnership(0, "alpha"));
		population.addPartnership(new ChildbearingPartnership(1, "beta"));
		population.addPartnership(new ChildbearingPartnership(2, "gamma"));

		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("a"), new Evidence[]{records[2]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("b"), new Evidence[]{records[1]});
		population.getPartnershipByRef("gamma").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[0]});

		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[2], records[6]}, 0.8f);
		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("f"), new Evidence[]{records[2], records[7]}, 0.6f);

		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("i"), new Evidence[]{records[2], records[5]}, 0.3f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("h"), new Evidence[]{records[2], records[4]}, 0.4f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[2], records[3]}, 0.9f);

		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[1], records[6]}, 0.7f);

		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[1], records[3]}, 0.7f);
		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("g"), new Evidence[]{records[1], records[8]}, 0.5f);

		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[0], records[6]}, 0.9f);
		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("f"), new Evidence[]{records[0], records[7]}, 0.5f);

		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[0], records[3]}, 0.6f);

		population.addSiblingsObject(new SiblingBridge(0, "alef"));

		population.getSiblingsObjectByRef("alef").addPossiblePerson1Link(population.findPersonByFirstName("a"), new Evidence[]{records[2], records[8]}, 0.8f);
		population.getSiblingsObjectByRef("alef").addPossiblePerson2Link(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[8]}, 0.8f);

		return population;
	}

	public static LinkedPopulation generateNonCrossOverMultiGenerationUseCase9() {

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


		Evidence[] records = new Evidence[11];
		for(int i = 0; i < 11; i++)
			records[i] = new Evidence(i);

		population.addPartnership(new ChildbearingPartnership(0, "alpha"));
		population.addPartnership(new ChildbearingPartnership(1, "beta"));
		population.addPartnership(new ChildbearingPartnership(2, "gamma"));

		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("a"), new Evidence[]{records[0]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("g"), new Evidence[]{records[6]});
		population.getPartnershipByRef("gamma").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[2]});

		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("b"), new Evidence[]{records[0], records[1]}, 0.8f);

		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[2]}, 0.6f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("g"), new Evidence[]{records[0], records[6]}, 0.6f);

		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("e"), new Evidence[]{records[2], records[5]}, 0.7f);
		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("f"), new Evidence[]{records[2], records[4]}, 0.8f);

		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("d"), new Evidence[]{records[3], records[2]}, 0.6f);

		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("i"), new Evidence[]{records[6], records[7]}, 0.8f);

		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("j"), new Evidence[]{records[6], records[9]}, 0.6f);
		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("h"), new Evidence[]{records[6], records[8]}, 0.4f);

		population.addSiblingsObject(new SiblingBridge(0, "alef"));

		population.getSiblingsObjectByRef("alef").addPossiblePerson1Link(population.findPersonByFirstName("g"), new Evidence[]{records[6], records[10]}, 0.8f);
		population.getSiblingsObjectByRef("alef").addPossiblePerson2Link(population.findPersonByFirstName("c"), new Evidence[]{records[2], records[10]}, 0.8f);


		return population;		

	}

	public static LinkedPopulation generateCousinsUseCase10() {

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

		Evidence[] records = new Evidence[14];
		for(int i = 0; i < 14; i++)
			records[i] = new Evidence(i);

		population.addPartnership(new ChildbearingPartnership(0, "alpha"));
		population.addPartnership(new ChildbearingPartnership(1, "beta"));
		population.addPartnership(new ChildbearingPartnership(2, "gamma"));
		population.addPartnership(new ChildbearingPartnership(3, "delta"));
		population.addPartnership(new ChildbearingPartnership(4, "epsilon"));
		population.addPartnership(new ChildbearingPartnership(5, "zeta"));

		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("a"), new Evidence[]{records[0]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("k"), new Evidence[]{records[11]});
		population.getPartnershipByRef("gamma").setChildLink(population.findPersonByFirstName("m"), new Evidence[]{records[9]});
		population.getPartnershipByRef("delta").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[2]});
		population.getPartnershipByRef("epsilon").setChildLink(population.findPersonByFirstName("g"), new Evidence[]{records[6]});
		population.getPartnershipByRef("zeta").setChildLink(population.findPersonByFirstName("i"), new Evidence[]{records[7]});

		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("b"), new Evidence[]{records[0], records[1]}, 0.9f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[2]}, 0.9f);

		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("j"), new Evidence[]{records[11], records[12]}, 0.7f);
		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("g"), new Evidence[]{records[6], records[11]}, 0.9f);

		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("l"), new Evidence[]{records[9], records[10]}, 0.9f);
		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("i"), new Evidence[]{records[7], records[9]}, 0.9f);

		population.getPartnershipByRef("delta").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[2], records[3]}, 0.9f);
		population.getPartnershipByRef("delta").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[2], records[4]}, 0.9f);
		population.getPartnershipByRef("delta").addPossiblePerson2Link(population.findPersonByFirstName("f"), new Evidence[]{records[2], records[5]}, 0.9f);

		population.getPartnershipByRef("epsilon").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[3], records[6]}, 0.9f);
		population.getPartnershipByRef("epsilon").addPossiblePerson2Link(population.findPersonByFirstName("f"), new Evidence[]{records[5], records[6]}, 0.9f);

		population.getPartnershipByRef("zeta").addPossiblePerson1Link(population.findPersonByFirstName("h"), new Evidence[]{records[7], records[8]}, 0.6f);
		population.getPartnershipByRef("zeta").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[4], records[7]}, 0.9f);

		population.addSiblingsObject(new SiblingBridge(0, "alef"));

		population.getSiblingsObjectByRef("alef").addPossiblePerson1Link(population.findPersonByFirstName("c"), new Evidence[]{records[2], records[13]}, 0.8f);
		population.getSiblingsObjectByRef("alef").addPossiblePerson2Link(population.findPersonByFirstName("g"), new Evidence[]{records[6], records[13]}, 0.8f);

		return population;

	}

	public static LinkedPopulation generateCrossOverMultiGenerationUseCase11() {

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


		Evidence[] records = new Evidence[11];
		for(int i = 0; i < 11; i++)
			records[i] = new Evidence(i);

		population.addPartnership(new ChildbearingPartnership(0, "alpha"));
		population.addPartnership(new ChildbearingPartnership(1, "beta"));
		population.addPartnership(new ChildbearingPartnership(2, "gamma"));

		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("a"), new Evidence[]{records[0]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("g"), new Evidence[]{records[6]});
		population.getPartnershipByRef("gamma").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[2]});

		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("b"), new Evidence[]{records[0], records[1]}, 0.8f);

		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[2]}, 0.6f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("g"), new Evidence[]{records[0], records[6]}, 0.6f);

		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("e"), new Evidence[]{records[2], records[5]}, 0.7f);
		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("f"), new Evidence[]{records[2], records[4]}, 0.8f);

		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("d"), new Evidence[]{records[3], records[2]}, 0.6f);

		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("i"), new Evidence[]{records[6], records[7]}, 0.8f);
		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("e"), new Evidence[]{records[6], records[5]}, 0.9f);

		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("j"), new Evidence[]{records[6], records[9]}, 0.6f);
		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("h"), new Evidence[]{records[6], records[8]}, 0.4f);
		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("d"), new Evidence[]{records[6], records[3]}, 0.8f);

		population.addSiblingsObject(new SiblingBridge(0, "alef"));

		population.getSiblingsObjectByRef("alef").addPossiblePerson1Link(population.findPersonByFirstName("g"), new Evidence[]{records[6], records[10]}, 0.8f);
		population.getSiblingsObjectByRef("alef").addPossiblePerson2Link(population.findPersonByFirstName("c"), new Evidence[]{records[2], records[10]}, 0.8f);

		return population;		

	}

	public static LinkedPopulation generateEnforcedSiblingsUseCase12() {

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

		population.addPartnership(new ChildbearingPartnership(0, "alpha"));
		population.addPartnership(new ChildbearingPartnership(1, "beta"));

		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[0]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("g"), new Evidence[]{records[1]});

		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("a"), new Evidence[]{records[0], records[3]}, 0.9f);
		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("b"), new Evidence[]{records[0], records[2]}, 0.9f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("d"), new Evidence[]{records[0], records[4]}, 0.9f);

		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("a"), new Evidence[]{records[1], records[3]}, 0.7f);
		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("e"), new Evidence[]{records[1], records[5]}, 0.7f);
		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("d"), new Evidence[]{records[1], records[4]}, 0.9f);
		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("f"), new Evidence[]{records[1], records[6]}, 0.9f);

		population.addSiblingsObject(new SiblingBridge(0, "alef"));
		population.getSiblingsObjectByRef("alef").addPossiblePerson1Link(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[7]}, 1f);
		population.getSiblingsObjectByRef("alef").addPossiblePerson2Link(population.findPersonByFirstName("g"), new Evidence[]{records[1], records[7]}, 1f);

		return population;

	}

	public static LinkedPopulation generateNuclearFamilyUseCase13() {

		LinkedPopulation population = new LinkedPopulation("Nuclear Family Use Case");
		population.addPerson(new LinkedPerson(0, "a", "Family", 'M'));
		population.addPerson(new LinkedPerson(1, "b", "Family", 'M'));
		population.addPerson(new LinkedPerson(2, "c", "Family", 'M'));
		population.addPerson(new LinkedPerson(3, "d", "Family", 'M'));
		population.addPerson(new LinkedPerson(4, "e", "Family", 'F'));
		population.addPerson(new LinkedPerson(5, "f", "Not", 'M'));
		population.addPerson(new LinkedPerson(6, "g", "Not", 'F'));
		population.addPerson(new LinkedPerson(7, "h", "Not", 'F'));
		population.addPerson(new LinkedPerson(8, "i", "Not", 'F'));
		population.addPerson(new LinkedPerson(9, "j", "Not", 'F'));

		Evidence[] records = new Evidence[13];
		for(int i = 0; i < 13; i++)
			records[i] = new Evidence(i);

		population.addPartnership(new ChildbearingPartnership(0, "alpha"));
		population.addPartnership(new ChildbearingPartnership(1, "beta"));
		population.addPartnership(new ChildbearingPartnership(2, "gamma"));

		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("a"), new Evidence[]{records[2]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("b"), new Evidence[]{records[1]});
		population.getPartnershipByRef("gamma").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[0]});

		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[2], records[6]}, 0.8f);
		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("f"), new Evidence[]{records[2], records[7]}, 0.6f);

		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("i"), new Evidence[]{records[2], records[5]}, 0.3f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("h"), new Evidence[]{records[2], records[4]}, 0.4f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[2], records[3]}, 0.9f);

		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[1], records[6]}, 0.7f);

		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[1], records[3]}, 0.7f);
		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("g"), new Evidence[]{records[1], records[8]}, 0.5f);

		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[0], records[6]}, 0.9f);
		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("f"), new Evidence[]{records[0], records[7]}, 0.5f);

		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[0], records[3]}, 0.6f);

		population.addSiblingsObject(new SiblingBridge(0, "alef"));
		population.addSiblingsObject(new SiblingBridge(1, "bet"));
		population.addSiblingsObject(new SiblingBridge(2, "gimel"));

		population.getSiblingsObjectByRef("alef").addPossiblePerson1Link(population.findPersonByFirstName("a"), new Evidence[]{records[2], records[8]}, 0.8f);
		population.getSiblingsObjectByRef("alef").addPossiblePerson2Link(population.findPersonByFirstName("b"), new Evidence[]{records[1], records[8]}, 0.8f);

		population.getSiblingsObjectByRef("bet").addPossiblePerson1Link(population.findPersonByFirstName("a"), new Evidence[]{records[2], records[9]}, 0.8f);
		population.getSiblingsObjectByRef("bet").addPossiblePerson2Link(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[9]}, 0.8f);

		population.getSiblingsObjectByRef("gimel").addPossiblePerson1Link(population.findPersonByFirstName("b"), new Evidence[]{records[1], records[10]}, 0.8f);
		population.getSiblingsObjectByRef("gimel").addPossiblePerson2Link(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[10]}, 0.8f);

		population.addMarraigePartnership(new MarriageBridge(0, "ani"));

		population.getMarraigePartnershipByRef("ani").addPossibleHusbandLink(population.findPersonByFirstName("d"), new Evidence[]{records[6],  records[11]}, 0.7f);
		population.getMarraigePartnershipByRef("ani").addPossibleWifeLink(population.findPersonByFirstName("e"), new Evidence[]{records[3],  records[11]}, 0.7f);
		population.getMarraigePartnershipByRef("ani").addPossibleWifeLink(population.findPersonByFirstName("j"), new Evidence[]{records[12],  records[11]}, 0.8f);

		return population;
	}

	public static LinkedPopulation generateNuclearFamilyUseCase14() {

		LinkedPopulation population = new LinkedPopulation("Nuclear Family Use Case");
		population.addPerson(new LinkedPerson(0, "a", "Family", 'M'));
		population.addPerson(new LinkedPerson(1, "b", "Family", 'M'));
		population.addPerson(new LinkedPerson(2, "c", "Family", 'M'));
		population.addPerson(new LinkedPerson(3, "d", "Family", 'M'));
		population.addPerson(new LinkedPerson(4, "e", "Family", 'F'));
		population.addPerson(new LinkedPerson(5, "f", "Not", 'M'));
		population.addPerson(new LinkedPerson(6, "g", "Not", 'F'));
		population.addPerson(new LinkedPerson(7, "h", "Not", 'F'));
		population.addPerson(new LinkedPerson(8, "i", "Not", 'F'));

		Evidence[] records = new Evidence[11];
		for(int i = 0; i < 11; i++)
			records[i] = new Evidence(i);

		population.addPartnership(new ChildbearingPartnership(0, "alpha"));
		population.addPartnership(new ChildbearingPartnership(1, "beta"));
		population.addPartnership(new ChildbearingPartnership(2, "gamma"));

		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("a"), new Evidence[]{records[2]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("b"), new Evidence[]{records[1]});
		population.getPartnershipByRef("gamma").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[0]});

		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[2], records[6]}, 0.8f);
		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("f"), new Evidence[]{records[2], records[7]}, 0.6f);

		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("i"), new Evidence[]{records[2], records[5]}, 0.3f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("h"), new Evidence[]{records[2], records[4]}, 0.4f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[2], records[3]}, 0.9f);

		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[1], records[6]}, 0.7f);

		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[1], records[3]}, 0.7f);
		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("g"), new Evidence[]{records[1], records[8]}, 0.5f);

		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[0], records[6]}, 0.9f);
		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("f"), new Evidence[]{records[0], records[7]}, 0.5f);

		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[0], records[3]}, 0.6f);

		population.addSiblingsObject(new SiblingBridge(0, "alef"));

		population.getSiblingsObjectByRef("alef").addPossiblePerson1Link(population.findPersonByFirstName("a"), new Evidence[]{records[2], records[8]}, 0.8f);
		population.getSiblingsObjectByRef("alef").addPossiblePerson2Link(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[8]}, 0.8f);

		population.addMarraigePartnership(new MarriageBridge(0, "ani"));
		population.addMarraigePartnership(new MarriageBridge(1, "bani"));

		population.getMarraigePartnershipByRef("ani").addPossibleHusbandLink(population.findPersonByFirstName("f"), new Evidence[]{records[7],  records[9]}, 0.7f);
		population.getMarraigePartnershipByRef("ani").addPossibleWifeLink(population.findPersonByFirstName("e"), new Evidence[]{records[3],  records[9]}, 0.7f);

		population.getMarraigePartnershipByRef("bani").addPossibleHusbandLink(population.findPersonByFirstName("d"), new Evidence[]{records[6],  records[10]}, 0.7f);
		population.getMarraigePartnershipByRef("bani").addPossibleWifeLink(population.findPersonByFirstName("g"), new Evidence[]{records[8],  records[10]}, 0.7f);

		return population;
	}

	public static LinkedPopulation generateNonCrossOverMultiGenerationUseCase15() {

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


		Evidence[] records = new Evidence[11];
		for(int i = 0; i < 11; i++)
			records[i] = new Evidence(i);

		population.addPartnership(new ChildbearingPartnership(0, "alpha"));
		population.addPartnership(new ChildbearingPartnership(1, "beta"));
		population.addPartnership(new ChildbearingPartnership(2, "gamma"));

		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("a"), new Evidence[]{records[0]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("g"), new Evidence[]{records[6]});
		population.getPartnershipByRef("gamma").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[2]});

		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("b"), new Evidence[]{records[0], records[1]}, 0.8f);

		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[2]}, 0.6f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("g"), new Evidence[]{records[0], records[6]}, 0.6f);

		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("e"), new Evidence[]{records[2], records[5]}, 0.7f);
		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("f"), new Evidence[]{records[2], records[4]}, 0.8f);

		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("d"), new Evidence[]{records[3], records[2]}, 0.6f);

		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("i"), new Evidence[]{records[6], records[7]}, 0.8f);

		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("j"), new Evidence[]{records[6], records[9]}, 0.6f);
		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("h"), new Evidence[]{records[6], records[8]}, 0.4f);

		population.addMarraigePartnership(new MarriageBridge(0, "ani"));

		population.getMarraigePartnershipByRef("ani").addPossibleHusbandLink(population.findPersonByFirstName("b"), new Evidence[]{records[1],  records[10]}, 0.7f);
		population.getMarraigePartnershipByRef("ani").addPossibleWifeLink(population.findPersonByFirstName("g"), new Evidence[]{records[6],  records[10]}, 0.7f);

		return population;		

	}

	public static LinkedPopulation generateCrossOverMultiGenerationUseCase16() {

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


		Evidence[] records = new Evidence[12];
		for(int i = 0; i < 12; i++)
			records[i] = new Evidence(i);

		population.addPartnership(new ChildbearingPartnership(0, "alpha"));
		population.addPartnership(new ChildbearingPartnership(1, "beta"));
		population.addPartnership(new ChildbearingPartnership(2, "gamma"));

		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("a"), new Evidence[]{records[0]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("g"), new Evidence[]{records[6]});
		population.getPartnershipByRef("gamma").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[2]});

		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("b"), new Evidence[]{records[0], records[1]}, 0.8f);

		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[2]}, 0.6f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("g"), new Evidence[]{records[0], records[6]}, 0.6f);

		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("e"), new Evidence[]{records[2], records[5]}, 0.7f);
		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("f"), new Evidence[]{records[2], records[4]}, 0.8f);

		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("d"), new Evidence[]{records[3], records[2]}, 0.6f);

		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("i"), new Evidence[]{records[6], records[7]}, 0.8f);
		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("e"), new Evidence[]{records[6], records[5]}, 0.9f);

		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("j"), new Evidence[]{records[6], records[9]}, 0.6f);
		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("h"), new Evidence[]{records[6], records[8]}, 0.4f);
		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("d"), new Evidence[]{records[6], records[3]}, 0.8f);

		population.addMarraigePartnership(new MarriageBridge(0, "ani"));
		population.addMarraigePartnership(new MarriageBridge(1, "bani"));

		population.getMarraigePartnershipByRef("ani").addPossibleHusbandLink(population.findPersonByFirstName("i"), new Evidence[]{records[7], records[10]}, 0.7f);
		population.getMarraigePartnershipByRef("ani").addPossibleWifeLink(population.findPersonByFirstName("h"), new Evidence[]{records[8], records[10]}, 0.7f);

		population.getMarraigePartnershipByRef("bani").addPossibleHusbandLink(population.findPersonByFirstName("f"), new Evidence[]{records[4], records[11]}, 0.7f);
		population.getMarraigePartnershipByRef("bani").addPossibleWifeLink(population.findPersonByFirstName("d"), new Evidence[]{records[3], records[11]}, 0.7f);

		return population;		

	}

	public static LinkedPopulation generateCrossOverMultiGenerationUseCase17() {

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


		Evidence[] records = new Evidence[13];
		for(int i = 0; i < 13; i++)
			records[i] = new Evidence(i);

		population.addPartnership(new ChildbearingPartnership(0, "alpha"));
		population.addPartnership(new ChildbearingPartnership(1, "beta"));
		population.addPartnership(new ChildbearingPartnership(2, "gamma"));

		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("a"), new Evidence[]{records[0]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("g"), new Evidence[]{records[6]});
		population.getPartnershipByRef("gamma").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[2]});

		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("b"), new Evidence[]{records[0], records[1]}, 0.8f);

		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[2]}, 0.6f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("g"), new Evidence[]{records[0], records[6]}, 0.6f);

		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("e"), new Evidence[]{records[2], records[5]}, 0.7f);
		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("f"), new Evidence[]{records[2], records[4]}, 0.8f);

		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("d"), new Evidence[]{records[3], records[2]}, 0.6f);

		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("i"), new Evidence[]{records[6], records[7]}, 0.8f);
		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("e"), new Evidence[]{records[6], records[5]}, 0.9f);

		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("j"), new Evidence[]{records[6], records[9]}, 0.6f);
		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("h"), new Evidence[]{records[6], records[8]}, 0.4f);
		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("d"), new Evidence[]{records[6], records[3]}, 0.8f);

		population.addSiblingsObject(new SiblingBridge(0, "alef"));

		population.getSiblingsObjectByRef("alef").addPossiblePerson1Link(population.findPersonByFirstName("g"), new Evidence[]{records[6], records[10]}, 0.8f);
		population.getSiblingsObjectByRef("alef").addPossiblePerson2Link(population.findPersonByFirstName("c"), new Evidence[]{records[2], records[10]}, 0.8f);

		population.addMarraigePartnership(new MarriageBridge(0, "ani"));
		population.addMarraigePartnership(new MarriageBridge(1, "bani"));

		population.getMarraigePartnershipByRef("ani").addPossibleHusbandLink(population.findPersonByFirstName("e"), new Evidence[]{records[5], records[11]}, 0.7f);
		population.getMarraigePartnershipByRef("ani").addPossibleWifeLink(population.findPersonByFirstName("d"), new Evidence[]{records[3], records[11]}, 0.7f);

		population.getMarraigePartnershipByRef("bani").addPossibleHusbandLink(population.findPersonByFirstName("b"), new Evidence[]{records[1], records[12]}, 0.7f);
		population.getMarraigePartnershipByRef("bani").addPossibleWifeLink(population.findPersonByFirstName("g"), new Evidence[]{records[6], records[12]}, 0.7f);

		return population;		

	}

	public static LinkedPopulation generateCousinsUseCase18() {

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

		Evidence[] records = new Evidence[19];
		for(int i = 0; i < 19; i++)
			records[i] = new Evidence(i);

		population.addPartnership(new ChildbearingPartnership(0, "alpha"));
		population.addPartnership(new ChildbearingPartnership(1, "beta"));
		population.addPartnership(new ChildbearingPartnership(2, "gamma"));
		population.addPartnership(new ChildbearingPartnership(3, "delta"));
		population.addPartnership(new ChildbearingPartnership(4, "epsilon"));
		population.addPartnership(new ChildbearingPartnership(5, "zeta"));

		population.getPartnershipByRef("alpha").setChildLink(population.findPersonByFirstName("a"), new Evidence[]{records[0]});
		population.getPartnershipByRef("beta").setChildLink(population.findPersonByFirstName("k"), new Evidence[]{records[11]});
		population.getPartnershipByRef("gamma").setChildLink(population.findPersonByFirstName("m"), new Evidence[]{records[9]});
		population.getPartnershipByRef("delta").setChildLink(population.findPersonByFirstName("c"), new Evidence[]{records[2]});
		population.getPartnershipByRef("epsilon").setChildLink(population.findPersonByFirstName("g"), new Evidence[]{records[6]});
		population.getPartnershipByRef("zeta").setChildLink(population.findPersonByFirstName("i"), new Evidence[]{records[7]});

		population.getPartnershipByRef("alpha").addPossiblePerson1Link(population.findPersonByFirstName("b"), new Evidence[]{records[0], records[1]}, 0.9f);
		population.getPartnershipByRef("alpha").addPossiblePerson2Link(population.findPersonByFirstName("c"), new Evidence[]{records[0], records[2]}, 0.9f);

		population.getPartnershipByRef("beta").addPossiblePerson1Link(population.findPersonByFirstName("j"), new Evidence[]{records[11], records[12]}, 0.7f);
		population.getPartnershipByRef("beta").addPossiblePerson2Link(population.findPersonByFirstName("g"), new Evidence[]{records[6], records[11]}, 0.9f);

		population.getPartnershipByRef("gamma").addPossiblePerson1Link(population.findPersonByFirstName("l"), new Evidence[]{records[9], records[10]}, 0.9f);
		population.getPartnershipByRef("gamma").addPossiblePerson2Link(population.findPersonByFirstName("i"), new Evidence[]{records[7], records[9]}, 0.9f);

		population.getPartnershipByRef("delta").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[2], records[3]}, 0.9f);
		population.getPartnershipByRef("delta").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[2], records[4]}, 0.9f);
		population.getPartnershipByRef("delta").addPossiblePerson2Link(population.findPersonByFirstName("f"), new Evidence[]{records[2], records[5]}, 0.9f);

		population.getPartnershipByRef("epsilon").addPossiblePerson1Link(population.findPersonByFirstName("d"), new Evidence[]{records[3], records[6]}, 0.9f);
		population.getPartnershipByRef("epsilon").addPossiblePerson2Link(population.findPersonByFirstName("f"), new Evidence[]{records[5], records[6]}, 0.9f);

		population.getPartnershipByRef("zeta").addPossiblePerson1Link(population.findPersonByFirstName("h"), new Evidence[]{records[7], records[8]}, 0.6f);
		population.getPartnershipByRef("zeta").addPossiblePerson2Link(population.findPersonByFirstName("e"), new Evidence[]{records[4], records[7]}, 0.9f);

		population.addSiblingsObject(new SiblingBridge(0, "alef"));

		population.getSiblingsObjectByRef("alef").addPossiblePerson1Link(population.findPersonByFirstName("c"), new Evidence[]{records[2], records[13]}, 0.8f);
		population.getSiblingsObjectByRef("alef").addPossiblePerson2Link(population.findPersonByFirstName("g"), new Evidence[]{records[6], records[13]}, 0.8f);

		population.addMarraigePartnership(new MarriageBridge(0, "ani"));
		population.addMarraigePartnership(new MarriageBridge(1, "bani"));
		population.addMarraigePartnership(new MarriageBridge(2, "gani"));
		population.addMarraigePartnership(new MarriageBridge(3, "doni"));
		population.addMarraigePartnership(new MarriageBridge(4, "eni"));

		population.getMarraigePartnershipByRef("ani").addPossibleHusbandLink(population.findPersonByFirstName("d"), new Evidence[]{records[3],  records[14]}, 0.7f);
		population.getMarraigePartnershipByRef("ani").addPossibleWifeLink(population.findPersonByFirstName("f"), new Evidence[]{records[5],  records[14]}, 0.7f);

		population.getMarraigePartnershipByRef("bani").addPossibleHusbandLink(population.findPersonByFirstName("h"), new Evidence[]{records[8],  records[15]}, 0.7f);
		population.getMarraigePartnershipByRef("bani").addPossibleWifeLink(population.findPersonByFirstName("e"), new Evidence[]{records[4],  records[15]}, 0.7f);

		population.getMarraigePartnershipByRef("gani").addPossibleHusbandLink(population.findPersonByFirstName("b"), new Evidence[]{records[1],  records[16]}, 0.7f);
		population.getMarraigePartnershipByRef("gani").addPossibleWifeLink(population.findPersonByFirstName("c"), new Evidence[]{records[2],  records[16]}, 0.7f);

		population.getMarraigePartnershipByRef("doni").addPossibleHusbandLink(population.findPersonByFirstName("j"), new Evidence[]{records[12],  records[17]}, 0.7f);
		population.getMarraigePartnershipByRef("doni").addPossibleWifeLink(population.findPersonByFirstName("g"), new Evidence[]{records[6],  records[17]}, 0.7f);

		population.getMarraigePartnershipByRef("eni").addPossibleHusbandLink(population.findPersonByFirstName("l"), new Evidence[]{records[10],  records[18]}, 0.7f);
		population.getMarraigePartnershipByRef("eni").addPossibleWifeLink(population.findPersonByFirstName("i"), new Evidence[]{records[7],  records[18]}, 0.7f);

		return population;

	}

}
