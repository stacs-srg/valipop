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
package old.record_classification_old.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Analyses the numbers of strings and words within a file.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @see <a href="http://digitisingscotland.cs.st-andrews.ac.uk/record_classification/scripts/analyse_unique_records.html">Launch script documentation</a>
 */
public class AnalyseRecordCounts {

    private static Random random = new Random(3495792347L);

    private static Charset FILE_CHARSET = Charset.forName("UTF-16");

    public static void main(String[] args) throws IOException {

        if (argsValid(args)) {

            final String input_path_string = args[0];
            final String output_path_string = args[1];

            final int number_of_samples = Integer.parseInt(args[2]);
            final int number_of_repetitions = Integer.parseInt(args[3]);

            final int prefix_length = args.length > 4 ? Integer.parseInt(args[4]) : 0;
            final int suffix_length = args.length > 5 ? Integer.parseInt(args[5]) : 0;

            getFileEncoding(args);

            analyseStringsInFile(input_path_string, output_path_string, number_of_samples, number_of_repetitions, prefix_length, suffix_length);
        }
    }

    private static void getFileEncoding(String[] args) {

        if (args.length > 6) {
            FILE_CHARSET = Charset.forName(args[6]);
        }
    }

    private static boolean argsValid(String[] args) {

        if (args.length < 4) {
            System.err.println("Usage: java UniqueAnalysis <input file path> <output file path> <number of samples> <number of repetitions>");
            return false;
        }

        if (!Files.exists(Paths.get(args[0]))) {
            System.err.println("Error: can't open input file " + args[0] + " in directory " + System.getProperty("user.dir"));
            return false;
        }

        if (Files.exists(Paths.get(args[1]))) {
            System.err.println("Error: output file " + args[1] + " already exists in directory " + System.getProperty("user.dir"));
            return false;
        }

        return true;
    }

    private static void analyseStringsInFile(String input_path_string, String output_path_string, int number_of_samples, int number_of_repetitions, int prefix_length, int suffix_length) throws IOException {

        try (final PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(output_path_string), FILE_CHARSET))) {

            long start = System.currentTimeMillis();


            String[] strings = loadStringsFromFile(input_path_string, prefix_length, suffix_length);



                analyse(strings, writer, input_path_string);


            System.out.println("Completed processing in " + (System.currentTimeMillis() - start) / 1000 + "s");

        }
    }

    public static void analyse(String[] strings, PrintWriter writer, String input_path_string) {



        Map<String, Integer> string_counts = new HashMap<>();
        Map<String, Integer> word_counts = new HashMap<>();

        int j = 0;
        for (int i = 0; i < strings.length; i++) {

            String line = strings[i];
            record(line, string_counts);

            for (String word : line.split("[ ,()\\-\\[\\]]+")) {
                record(word, word_counts);
            }

        }

        printAnalysis(string_counts, word_counts, writer, input_path_string);

    }

    private static void record(String s, Map<String, Integer> counts) {

        if (counts.containsKey(s)) {
            counts.put(s, counts.get(s) + 1);
        }
        else counts.put(s, 1);
    }

    private static void printAnalysis(Map<String, Integer> string_counts, Map<String, Integer> word_counts, PrintWriter writer, String input_path_string) {

        writer.println(new Date());
        writer.println("Input file: " + input_path_string);
        writer.println();

        writer.println("String counts: ");
        outputMap(string_counts, writer);
        writer.println();
        writer.println();
        writer.println();

        writer.println("Word counts: ");
        outputMap(word_counts, writer);
    }

    private static void outputMap(Map<String, Integer> map, PrintWriter writer) {

        for (String s : map.keySet()) {
            writer.println(s + "\t" + map.get(s));
        }
    }

    private static String[] loadStringsFromFile(String path_string, int prefix_length, int suffix_length) throws IOException {

        try (final BufferedReader reader = Files.newBufferedReader(Paths.get(path_string), FILE_CHARSET)) {

            List<String> list = new ArrayList<>();

            String line = getLine(reader, prefix_length, suffix_length);
            int line_count = 0;
            int ignored_lines = 0;

            while (line != null) {
                if (notIgnored(line)) {
                    list.add(line);
                }
                else {
                    ignored_lines++;
                }

                line_count++;
                printProgress(line_count);
                line = getLine(reader, prefix_length, suffix_length);
            }

            System.out.println("Read " + line_count + " lines.");
            System.out.println("Ignored " + ignored_lines + " lines.");
            return list.toArray(new String[0]);
        }
    }

    private static String getLine(BufferedReader reader, int prefix_length, int suffix_length) throws IOException {

        final String line = reader.readLine();
        if (line == null) return null;
        final int length = line.length();
        return length > prefix_length + suffix_length ? line.substring(prefix_length, length - suffix_length) : "";
    }

    private static void printProgress(int line_count) {

        if (line_count >= 1000 && hasOneSigFig(line_count)) {
            System.out.println("Read " + line_count + " lines.");
        }
    }

    private static boolean hasOneSigFig(int n) {

        int i = n;
        int j = 1;
        while (i >= 10) {
            i = i / 10;
            j *= 10;
        }

        return i * j == n;
    }

    private static boolean notIgnored(String line) {

        return line.length() > 0;
    }

    private static void permute(String[] strings, Random random) {

        final int length = strings.length;

        for (int i = 0; i < length; i++) {
            swap(strings, i, random.nextInt(length));
        }
    }

    private static void swap(String[] strings, int i, int j) {

        String temp = strings[i];
        strings[i] = strings[j];
        strings[j] = temp;
    }
}
