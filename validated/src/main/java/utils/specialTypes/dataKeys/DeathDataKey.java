package utils.specialTypes.dataKeys;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DeathDataKey extends DataKey {

    public DeathDataKey(Integer age, int forNPeople) {
        super(age, forNPeople);
    }

    public Integer getAge() {
        return getYLabel();
    }
}
