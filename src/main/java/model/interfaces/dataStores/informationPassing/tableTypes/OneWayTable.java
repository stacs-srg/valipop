package model.interfaces.dataStores.informationPassing.tableTypes;

import javafx.scene.control.Cell;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface OneWayTable<CellType> {

    CellType getValue(int y);

}
