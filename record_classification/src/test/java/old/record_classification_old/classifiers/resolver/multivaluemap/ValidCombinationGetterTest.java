/*
 * Copyright 2015 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package old.record_classification_old.classifiers.resolver.multivaluemap;

import com.google.common.collect.Multiset;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import old.record_classification_old.classifiers.resolver.Interfaces.LossFunction;
import old.record_classification_old.classifiers.resolver.LengthWeightedLossFunction;
import old.record_classification_old.classifiers.resolver.generic.LossFunctionApplier;
import old.record_classification_old.classifiers.resolver.generic.ValidCombinationGetter;
import old.record_classification_old.datastructures.classification.Classification;
import old.record_classification_old.datastructures.classification.ClassificationSetValidityAssessor;
import old.record_classification_old.datastructures.code.CodeNotValidException;
import old.record_classification_old.datastructures.tokens.TokenSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * Created by fraserdunlop on 07/10/2014 at 15:34.
 */
@Ignore
public class ValidCombinationGetterTest {

    private ValidCombinationGetter vCG = new ValidCombinationGetter(new ClassificationSetValidityAssessor());
    private MultiValueMapTestHelper mvmHelper;
    private LossFunction<Multiset<Classification>, Double> lengthWeighted = new LengthWeightedLossFunction();
    private LossFunctionApplier lossFunctionApplier = new LossFunctionApplier(new LengthWeightedLossFunction());

    @Before
    public void setup() throws IOException, CodeNotValidException {

        mvmHelper = new MultiValueMapTestHelper();
        mvmHelper.addMockEntryToMatrix("brown", "2100", 0.5);
        mvmHelper.addMockEntryToMatrix("white", "2100", 0.85);
        mvmHelper.addMockEntryToMatrix("brown", "2200", 0.81);
        mvmHelper.addMockEntryToMatrix("white", "2200", 0.87);
        mvmHelper.addMockEntryToMatrix("brown", "4215", 0.87);
        mvmHelper.addMockEntryToMatrix("white", "4215", 0.8);
        mvmHelper.addMockEntryToMatrix("brown", "6700", 0.85);
        mvmHelper.addMockEntryToMatrix("white", "6700", 0.83);
    }

    @Test
    public void getValidCodeTriplesTest() throws Exception, CodeNotValidException {

        TokenSet originalSet = new TokenSet("brown white");
        List<Multiset<Classification>> validTriples = vCG.getValidSets(mvmHelper.getMap(), originalSet);
        Assert.assertEquals(20, validTriples.size());
        mvmHelper.addMockEntryToMatrix("blue", "3000", 0.83);
        TokenSet originalSet1 = new TokenSet("brown white blue");
        validTriples = vCG.getValidSets(mvmHelper.getMap(), originalSet1);
        Assert.assertEquals(41, validTriples.size());
        for (Multiset<Classification> set : validTriples) {
            Assert.assertEquals(1.5, lengthWeighted.calculate(set), 1.5);
        }
        Set<Classification> best = lossFunctionApplier.getBest(validTriples);
        Double averageConfidence = 0.;
        for (Classification triple : best) {
            averageConfidence += triple.getConfidence();
        }
        Assert.assertEquals((2 * 0.87 + 0.83), averageConfidence, 0.001);
    }

    @Test
    public void testLossFunctionApplierReturnsEmptySetWithEmptyGetBestArg() {

        List<Multiset<Classification>> classifications = new ArrayList<>();
        Assert.assertTrue(lossFunctionApplier.getBest(classifications).isEmpty());
    }
}
