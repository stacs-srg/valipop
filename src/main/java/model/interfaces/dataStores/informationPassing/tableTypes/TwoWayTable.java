package model.interfaces.dataStores.informationPassing.tableTypes;

/**
 * The TwoWayTable interface provides a two dimensional table construct which can be parametrised for use depending on
 * what types of the values the value cells in the table will hold. The look up columns of the table is always integer
 * based.
 *
 * An example of a TwoWayTable with CellType Double.
 *
 *     |   0   |   1   |   2   |
 * ------------------------------
 *   0 | 0.203 | 0.002 | 0.000 |
 *   1 | 0.102 | 0.012 | 0.003 |  ...
 *   2 | 0.001 | 0.011 | 0.011 |
 *              ...
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface TwoWayTable<CellType> extends Table {

    CellType getValue(int rowValue, int columnValue);

}
