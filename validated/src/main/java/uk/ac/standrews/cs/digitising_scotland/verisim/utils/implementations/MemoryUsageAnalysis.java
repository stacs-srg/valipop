package uk.ac.standrews.cs.digitising_scotland.verisim.utils.implementations;

import java.lang.management.ManagementFactory;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MemoryUsageAnalysis {

    private static boolean checkMemory = false;

    private static long maxUsage = 0L;

    public static void main(String[] args) {

        checkMemory= true;
        OBDModel.runPopulationModel(args);

        System.out.println("---------------------------------\n");
        System.out.println("Max Memory Usage : " + (maxUsage / 1e6) + " MB");
        System.out.println("We recommend to increase by 10% to give adequate headroom\n");

    }

    public static void log() {

        if(checkMemory) {
            long currentUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
            if(currentUsage > maxUsage) {
                maxUsage = currentUsage;
            }
        }

    }
}
