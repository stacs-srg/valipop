package model.interfaces.dataStores.informationPassing.tableTypes;

/**
 * The OneWayTable interface provides a one dimensional table construct which can be parametrised for use depending on
 * what types of the values the value cells in the table will hold. The look up column of the table is always integer
 * based.
 * <p>
 * An example of a OneWayTable with CellType Double.
 * <p>
 * | Value
 * ------------
 * 0 | 0.203
 * 1 | 0.102
 * 2 | 0.001
 * ...
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface OneWayTable<CellType> {

    CellType getValue(int rowValue);

}
