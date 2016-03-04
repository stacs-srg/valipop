package model.interfaces.dataStores.informationPassing.tableTypes;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface TwoWayTable<CellType> extends Table {

    CellType getValue(int y, int x);

}
