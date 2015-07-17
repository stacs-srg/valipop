package uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class Mixin {

    // This is used to give the same effect as adding the @JsonProperty attribute
    // to the constructor of DenseMatrix, which can't be done as it's a 3rd party class.
    public Mixin(@JsonProperty("values") double[][] values) {}
}
