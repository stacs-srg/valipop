package utils.specialTypes.dataKeys;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class BirthDataKey extends DataKey {


    public BirthDataKey(Integer age, Integer order, int forNPeople) {
        super(age, order, forNPeople);
    }

    public Integer getAge() {
        return getYLabel();
    }

    public Integer getOrder() {
        return getXLabel();
    }
}
