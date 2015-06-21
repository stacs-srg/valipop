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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2;

import org.apache.commons.lang3.*;

import java.util.*;

/**
 * Performs a classification process for a given number of repetitions. 
 * 
 * @author Masih Hajiarab Derkani
 */
public class ClassificationProcessRepetitions {

    public List<ClassificationProcess> repeat(ClassificationProcess process, int repetitions) throws Exception {

        final List<ClassificationProcess> performed_processes = new ArrayList<>();
        final byte[] serialized_process = SerializationUtils.serialize(process);
        for (int i = 0; i < repetitions; i++) {
            final ClassificationProcess process_clone = (ClassificationProcess) SerializationUtils.deserialize(serialized_process);
            process_clone.call();
            performed_processes.add(process_clone);
        }

        return performed_processes;
    }
}
