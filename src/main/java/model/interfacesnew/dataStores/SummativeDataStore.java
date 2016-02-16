package model.interfacesnew.dataStores;

import model.enums.VariableType;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface SummativeDataStore extends ImputableDataStore, CheckableDataStore {

    NumberTable getData(VariableType variable);

    void setData(VariableType variable, NumberTable table);

}
