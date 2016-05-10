package model;

import model.interfaces.populationModel.IPartnership;
import model.interfaces.populationModel.IPerson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Partnership implements IPartnership {

    private static Logger log = LogManager.getLogger(Partnership.class);
    private static int nextId = 0;

    private static int getNewId() {
        return nextId++;
    }

    private int id;
    private IPerson male;
    private IPerson female;

    private List<IPerson> children = new ArrayList<IPerson>();

    public Partnership(IPerson male, IPerson female) {

        this.id = getNewId();

        if(male.getSex() != 'm' || female.getSex() != 'f') {
            log.fatal("A member(s) of partnership does not bear correct sex for given role.");
            System.exit(202);
        }

        this.male = male;
        this.female = female;

    }

    public void addChildren(List<IPerson> children) {
        this.children.addAll(children);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public IPerson getFemalePartner() {
        return female;
    }

    @Override
    public IPerson getMalePartner() {
        return male;
    }

    @Override
    public IPerson getPartnerOf(IPerson id) {
        if (id.getSex() == 'm') {
            return female;
        } else {
            return male;
        }
    }

    @Override
    public List<IPerson> getChildren() {
        return children;
    }

    @Override
    public int compareTo(IPartnership o) {
        return this.id == o.getId() ? 0 : -1;
    }
}
