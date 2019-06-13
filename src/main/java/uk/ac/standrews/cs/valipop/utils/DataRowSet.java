package uk.ac.standrews.cs.valipop.utils;

import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRange;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRangeToDoubleSet;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.LabelledValueSet;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.StringToDoubleSet;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DataRowSet implements Iterable<DataRow> {

    List<String> labels;
    Set<DataRow> dataset = new HashSet<>();

    public DataRowSet(DataRow initialRow) {
        setLabels(initialRow.getLabels());
        dataset.add(initialRow);
    }

    public DataRowSet(String labels, List<String> lines, String filterOn, String filterValue) throws InvalidInputFileException {

        for(String line : lines) {
            DataRow dr = new DataRow(labels, line);
            if(dr.getValue(filterOn).equals(filterValue)) {
                if(!dr.getValue("Age").equals(".")) {
                    dr.setValue("Age", String.valueOf(Double.valueOf(Math.floor(Double.valueOf(dr.getValue("Age")))).intValue()));
                    dataset.add(dr);
                }
            }
        }

        setLabels(dataset.iterator().next().getLabels());
    }

    private void setLabels(Set<String> labels) {
        this.labels = new ArrayList<>(labels);
    }

    public DataRowSet(String labels, Set<String> lines) throws InvalidInputFileException {

        for(String line : lines)
            dataset.add(new DataRow(labels, line));

        setLabels(dataset.iterator().next().getLabels());
    }


    public boolean hasLabel(String label) {
        return labels.contains(label);
    }

    public Map<String, DataRowSet> splitOn(String splitOn) {

        Map<String, DataRowSet> tables = new HashMap<>();

        for (DataRow row : dataset) {
            String splitValue = row.getValue(splitOn);
            if(tables.keySet().contains(splitValue)) {
                tables.get(splitValue).add(row);
            } else {
                tables.put(splitValue, new DataRowSet(row));
            }
        }

        return tables;
    }

    public TreeMap<IntegerRange, LabelledValueSet<String, Double>> to2DTableOfProportions(String xLabelOfInt, String yLabelOfString, RandomGenerator random) {

        Map<String, Map<String, Integer>> counts = new HashMap<>();
        Map<String, Integer> totalCountsUnderX = new HashMap<>();

        for(DataRow row : dataset) {

            String iR = new IntegerRange(row.getValue(xLabelOfInt)).toString();

            if(totalCountsUnderX.containsKey(iR)) {
                Map<String, Integer> map = counts.get(iR);

                if(map.containsKey(row.getValue(yLabelOfString))) {
                    map.put(row.getValue(yLabelOfString), map.get(row.getValue(yLabelOfString)) + 1);
                } else {
                    map.put(row.getValue(yLabelOfString), 1);
                }

            } else {
                Map<String, Integer> map = new HashMap<>();
                map.put(row.getValue(yLabelOfString), 1);
                counts.put(iR, map);
            }

//            if(!totalCountsUnderX.containsKey(iR)) totalCountsUnderX.put(iR, 0);

            totalCountsUnderX.computeIfAbsent(iR, k -> 0 );
            totalCountsUnderX.put(iR, totalCountsUnderX.get(iR) + 1);

        }

        TreeMap<IntegerRange, LabelledValueSet<String, Double>> proportions = new TreeMap<>();
        Set<String> labels = new TreeSet<>();

        for(String iR : counts.keySet()) {
            int totalCount = totalCountsUnderX.get(iR);
            Map<String, Integer> countMap = counts.get(iR);

            LabelledValueSet<String, Double> proportionMap = new StringToDoubleSet(random);

            for(String label : countMap.keySet()) {
                proportionMap.add(label, countMap.get(label) / (double) totalCount);
                labels.add(label);
            }

            proportions.put(new IntegerRange(iR), proportionMap);
        }

        for(IntegerRange iR : proportions.keySet()) {

            LabelledValueSet<String, Double> proportionMap = proportions.get(iR);

            for(String label : labels) {
                if(!proportionMap.getLabels().contains(label)) {
                    proportionMap.add(label, 0.0);
                }
            }

        }
        return proportions;

    }

    public void add(DataRow row) {

        dataset.add(row);
    }



    public void remove(DataRow row) {
        dataset.remove(row);
    }

    @Override
    public Iterator<DataRow> iterator() {
        return dataset.iterator();
    }

    public String toString(List<String> order) {
        StringBuilder sb = new StringBuilder();

        for(int s = 0; s < order.size() - 1; s++) {
            sb.append(order.get(s) + ",");
        }
        sb.append(order.get(order.size() - 1) + "\n");

        for(DataRow dr : dataset) {
            for(int s = 0; s < order.size() - 1; s++) {
                sb.append(dr.getValue(order.get(s)) + ",");
            }
            sb.append(dr.getValue(order.get(order.size() - 1)) + "\n");
        }

        return sb.toString();
    }
}
