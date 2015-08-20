/*
 * Copyright 2015 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.util;

import java.util.*;

/**
 * @author masih
 */
public final class Combinations {

    private Combinations() { throw new UnsupportedOperationException(); }

    public static <T> List<List<T>> permute(Collection<T> input) {

        return permute(new ArrayList<>(input), 0);
    }

    private static <T> List<List<T>> permute(List<T> input, int start) {

        List<List<T>> permutations = new ArrayList<>();

        if (start == input.size()) {
            List<T> combination = new ArrayList<>();
            for (int i = 0; i < input.size(); i++) {
                combination.add(input.get(i));
            }
            permutations.add(combination);
        }
        else {
            for (int i = start; i < input.size(); i++) {
                swap(input, start, i);
                permutations.addAll(permute(input, start + 1));
                swap(input, start, i);
            }
        }

        return permutations;
    }

    private static <T> void swap(final List<T> a, final int start, final int i) {

        T temp = a.get(start);

        a.set(start, a.get(i));
        a.set(i, temp);
    }

    public static <T> List<List<T>> powerset(Collection<T> input) {

        List<List<T>> powerset = new ArrayList<>();
        powerset.add(new ArrayList<>());

        for (T item : input) {
            final List<List<T>> new_combination = new ArrayList<>();

            for (List<T> subset : powerset) {
                new_combination.add(subset);
                final List<T> new_sub_combination = new ArrayList<T>(subset);
                new_sub_combination.add(item);
                new_combination.add(new_sub_combination);
            }

            powerset = new_combination;
        }

        return powerset;
    }

    public static <T> List<List<T>> all(Collection<T> input) {

        // TODO horrible code; implemnet properly
        final List<List<T>> powerset = powerset(input);
        final Set<List<T>> all = new HashSet<>(powerset);

        for (List<T> powerset_element : powerset) {

            if (powerset_element.size() > 1) {
                List<T> reverse = new ArrayList<>(powerset_element);
                Collections.reverse(reverse);
                all.add(reverse);
            }
        }

        return new ArrayList<>(all);
    }
}
