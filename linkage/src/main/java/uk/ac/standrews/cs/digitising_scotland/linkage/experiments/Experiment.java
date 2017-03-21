package uk.ac.standrews.cs.digitising_scotland.linkage.experiments;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

public class Experiment {

    private final String[] arg_names;
    private final String[] arg_values;
    private final Class experiment_class;

    public Experiment(String[] arg_names, String[] arg_values, Class experiment_class) {

        this.arg_names = arg_names;
        this.arg_values = arg_values;
        this.experiment_class = experiment_class;
    }

    public void printDescription() {

        System.out.println("Run at: " + new Date());
        System.out.println("On: " + getHostName());
        System.out.println("Algorithm: " + experiment_class.getSimpleName());
        System.out.println();

        int longest_arg_length = getMaxLength(arg_names);

        for (int i = 0; i < arg_values.length; i++) {
            System.out.println(arg_names[i] + ": " + pad(arg_names[i], longest_arg_length) + arg_values[i]);
        }

        System.out.println();
    }

    public void usage() {

        System.err.println("Usage: run with " + String.join(" ", arg_names));
    }

    private String getHostName() {

        try {
            return InetAddress.getLocalHost().getHostName();

        } catch (UnknownHostException e) {
            return "unknown host";
        }
    }

    private String pad(String s, int length) {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length - s.length(); i++) {
            builder.append(" ");
        }
        return builder.toString();
    }

    private int getMaxLength(String[] arg_names) {

        int longest = 0;
        for (String arg_name : arg_names) {
            if (arg_name.length() > longest) {
                longest = arg_name.length();
            }
        }
        return longest;
    }
}
