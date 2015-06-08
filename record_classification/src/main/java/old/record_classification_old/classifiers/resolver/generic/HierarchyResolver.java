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
package old.record_classification_old.classifiers.resolver.generic;

import old.record_classification_old.datastructures.classification.Classification;
import old.record_classification_old.datastructures.code.Code;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Resolves hierarchies in the keys of a MultiValueMap. Moves ancestor key contents
 * into decendent key lists.
 */
public class HierarchyResolver {

    /**
     * Moves ancestor key contents to decendent key lists.
     * @param map MultiValueMap
     * @return new MultiValueMap with hierarchies in keys resolved
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public  MultiValueMap<Code, Classification> moveAncestorsToDescendantKeys(final MultiValueMap<Code, Classification> map) throws IOException, ClassNotFoundException {
        MultiValueMap<Code, Classification> clone = map.deepClone();
        for (Code key : map)
            moveAncestorsIntoKey(map, clone, key);
        return clone;
    }

    /**
     * Iterates over keys in map checking if they are ancestors of K decendentKey. If they are
     * then their values are migrated to the List associated with decendentKey (in clone) and
     * the ancestor keys removed (from clone).
     * @param map original MultiValueMap
     * @param clone clone of map which is edited
     * @param descendantKey the decendent key
     */
    private void moveAncestorsIntoKey(MultiValueMap<Code, Classification> map, MultiValueMap<Code, Classification> clone, Code descendantKey) {
        for(Code ancestor : getAncestors(descendantKey, clone.keySet())) {
                clone.get(descendantKey).addAll(map.get(ancestor));
                clone.remove(ancestor);
        }
    }

    /**
     * Returns the set of ancestors of K k contained in Set<K> keys.
     * @param k the key
     * @param keys the keys
     * @return the ancestors of k.
     */
    private Set<Code> getAncestors(final Code k, final Set<Code> keys) {
        Set<Code> ancestors = new HashSet<>();
        for (Code key : keys) {
            if (k.isAncestor(key)) { ancestors.add(key); }
        }
        return ancestors;
    }

}
