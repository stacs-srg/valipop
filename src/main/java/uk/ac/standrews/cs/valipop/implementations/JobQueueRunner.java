package uk.ac.standrews.cs.valipop.implementations;

import com.google.common.collect.Sets;
import uk.ac.standrews.cs.nds.util.FileUtil;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.utils.*;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordFormat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class JobQueueRunner {

    private static int THREAD_LIMIT = 1;
    public static int threadCount = 1;

    private static double appropriateUsageThreshold = 0.5;
    private static double memoryIncreaseOnMemoryException = 1.2;

    private static final ArrayList<String> order = new ArrayList<>(Arrays.asList(new String[]{"priority","code version","reason","n","seed size","rf","prf","iw","input dir","results dir","required memory","output record format","deterministic","seed","setup br","setup dr","bf","df","tS","t0","tE","timestep","binomial sampling","min birth spacing","min ges period","ct tree stepback"}));

    public static void main(String[] args) throws InterruptedException, IOException {

        // from cmd line args:
        // get status path
        Path statusPath = Paths.get(args[0]);

        // get job q path
        Path jobQPath = Paths.get(args[1]);

        // get assigned memory
        int assignedMemory = Integer.valueOf(args[2]);

        THREAD_LIMIT = Integer.valueOf(args[3]);

        double threshold = Double.parseDouble(args[4]);

        while(getStatus(statusPath)) {

            if(nodeIdle(threshold) && !checkPause(statusPath)) {

                try {
                    DataRow chosenJob = getJob(jobQPath, assignedMemory);

                    if (chosenJob != null) {
                        System.out.println("JOB TAKEN @ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " - " + chosenJob.toString(order));

                        try {

                            // runs GC to ensure no object left in memory from previous sims that may skew memory usage logging
                            System.gc();

                            Config config = convertJobToConfig(chosenJob);

                            OBDModel model = new OBDModel(config);
                            try {
                                model.runSimulation();
                                model.analyseAndOutputPopulation(false, 5);

                                if(THREAD_LIMIT == 1) {
                                    new AnalysisThread(model, config, threadCount).run(); // this runs it in the main thread
                                } else {
                                    while (threadCount >= THREAD_LIMIT)
                                        Thread.sleep(10000);

                                    new AnalysisThread(model, config, threadCount).start(); // this runs it in a new thread
                                }

                            } catch (PreEmptiveOutOfMemoryWarning e) {
                                model.recordOutOfMemorySummary();
                                model.getSummaryRow().outputSummaryRowToFile();

                                // put job back in queue with higher memory requirement
                                returnJobToQueue(jobQPath, chosenJob, (int) Math.ceil(assignedMemory * memoryIncreaseOnMemoryException), chosenJob.getInt("priority"), true);
                            }

                        } catch (InvalidInputFileException e) {
                            System.out.println("JOB RETURNED @ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " - " + chosenJob.toString(order));
                            returnJobToQueue(jobQPath, chosenJob, chosenJob.getInt("required memory"), 99, true);
                        }

                    } else {
                        System.out.println("NO SUITABLE JOB FOUND @ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        Thread.sleep(10000);
                    }
                } catch (InvalidInputFileException e) {
                    System.out.println("Either the job file doesn't have any jobs or it's malformed - I'm going to keep looping and wait for you to fix that... @ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    Thread.sleep(60000);
                }
            }

        }

        System.out.println("Closing due to status");

    }

    // checks recent load average - if over threshold then does not run next sim until load average has dropped
    private static boolean nodeIdle(double threshold) throws IOException, InterruptedException {
        String result = execCmd("uptime");
        String[] split = result.split(" ");
        double load = Double.parseDouble(split[split.length - 3].split(",")[0]);

        System.out.println("Node busy (" + load + ") @ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        if(load > threshold)
            Thread.sleep(60000);
        
        return load < threshold;
    }

    public static String execCmd(String cmd) throws java.io.IOException {
        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private static boolean getStatus(Path statusPath) throws IOException {
        // read in file
        ArrayList<String> lines = new ArrayList<>(InputFileReader.getAllLines(statusPath));

        if(!lines.isEmpty()) {
            switch (lines.get(0)) {
                case "run": return true;
                case "terminate" : return false;
            }
        }

        return true;
    }

    private static boolean checkPause(Path statusPath) throws IOException, InterruptedException {
        // read in file
        ArrayList<String> lines = new ArrayList<>(InputFileReader.getAllLines(statusPath));

        System.out.println("Status 'pause' @ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        boolean pause = false;
        
        if(!lines.isEmpty())
            pause = lines.get(0).equals("pause");

        if(pause)
            Thread.sleep(10000);

        return pause;
    }

    public static DataRow getJob(Path jobFile, int availiableMemory) throws IOException, InterruptedException, InvalidInputFileException {

        System.out.println("Locking job file @ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        FileChannel fileChannel = getFileChannel(jobFile);
        DataRow chosenJob = null;

        try {
            // lock job file
            fileChannel.lock(0, Long.MAX_VALUE, false);
            DataRowSet jobs = readInJobFile(fileChannel);

            int maxRequiredMemory = 0;
            int maxPriorityLevel = 99;

            int maxUncheckedMemory = 0;
            int maxUncheckedPriorityLevel = 99;

            // throw away jobs requiring too much memory
            Map<String, DataRowSet> jobsByMemory = jobs.splitOn("required memory");

            for(String memoryRequired : jobsByMemory.keySet()) {

                int reqMemory = Integer.valueOf(memoryRequired);

                if(availiableMemory >= reqMemory) {
//                    for(DataRow dr : jobsByMemory.get(memoryRequired))
//                        jobs.remove(dr);
//                } else {

                    for(DataRow dr : jobsByMemory.get(memoryRequired)) {
                        // get priority
                        try {
                            int priority = dr.getInt("priority");

                            // if priority greater than maxPriority
                            if(priority < maxPriorityLevel) {
                                // if priority equal to 1 OR required memory meets usage threshold then
                                if(priority == 1 || reqMemory > availiableMemory * appropriateUsageThreshold) {
                                    // update maxPriority and maxRequiredMemory
                                    maxPriorityLevel = priority;
                                    maxRequiredMemory = reqMemory;
                                }
                            }

                            // if priority equal to maxPriority and memory greater than maxRequiredMemory
                            if(priority == maxPriorityLevel && reqMemory > maxRequiredMemory)
                                maxRequiredMemory = reqMemory;

                            if(priority <= maxUncheckedPriorityLevel && reqMemory > maxUncheckedMemory) {
                                maxUncheckedMemory = reqMemory;
                                maxUncheckedPriorityLevel = priority;
                            }

                        } catch (InvalidInputFileException e) {
                            // Priority is malformed
                            returnJobToQueue(jobFile, dr, dr.getInt("required memory"), 99, false);
                        }
                    }
                }
            }

            // if a job has been found
            if(maxPriorityLevel != 99 && maxRequiredMemory != 0) {
                chosenJob = chooseJob(jobsByMemory, maxRequiredMemory, maxPriorityLevel, jobs, fileChannel);
            } else if(maxUncheckedPriorityLevel != 99 & maxUncheckedMemory != 0) {
                chosenJob = chooseJob(jobsByMemory, maxUncheckedMemory, maxUncheckedPriorityLevel, jobs, fileChannel);
            }

        } finally {
            // release job file
            fileChannel.close(); // also releases the lock
            System.out.println("Releasing job file @ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        return chosenJob;
        
    }

    private static DataRow chooseJob(Map<String, DataRowSet> jobsByMemory, int maxRequiredMemory, int maxPriorityLevel, DataRowSet jobs, FileChannel fileChannel) throws IOException, InvalidInputFileException {

        DataRowSet chosenJobSet = jobsByMemory.get(String.valueOf(maxRequiredMemory)).splitOn("priority").get(String.valueOf(maxPriorityLevel));

        // take job at head of remaning jobQueue
        DataRow chosenJob  = chosenJobSet.iterator().next();

        // decrement n value in chosen job
        int n = Integer.valueOf(chosenJob.getValue("n"));
        n--;

        // if n now equals 0
        if (n == 0) {
            // remove job from data structure
            jobs.remove(chosenJob);
        } else {
            chosenJob.setValue("n", String.valueOf(n));
        }

        if(chosenJob.getInt("priority") > 3)
            chosenJob.setValue("priority", "3");

        // write datastructure back to job file
        writeToJobFile(fileChannel, jobs);

        return chosenJob;
    }

    private static Config convertJobToConfig(DataRow chosenJob) throws InvalidInputFileException {

        Config config = new Config(
                chosenJob.getLocalDate("tS"),
                chosenJob.getLocalDate("t0"),
                chosenJob.getLocalDate("tE"),
                chosenJob.getInt("seed size"),
                chosenJob.getPath("input dir"),
                chosenJob.getPath("results dir"),
                chosenJob.getValue("reason"));

        config.setSetupBirthRate(chosenJob.getDouble("setup br"));
        config.setSetupDeathRate(chosenJob.getDouble("setup dr"));
        config.setRecoveryFactor(chosenJob.getDouble("rf"));
        config.setProportionalRecoveryFactor(chosenJob.getDouble("prf"));
        config.setInputWidth(chosenJob.getPeriod("iw"));
        config.setMinBirthSpacing(chosenJob.getPeriod("min birth spacing"));
        config.setMinGestationPeriod(chosenJob.getPeriod("min ges period"));
        config.setBirthFactor(chosenJob.getDouble("bf"));
        config.setDeathFactor(chosenJob.getDouble("df"));

        try {
            config.setOutputRecordFormat(Enum.valueOf(RecordFormat.class, chosenJob.getValue("output record format")));
        } catch (IllegalArgumentException e) {
            throw new InvalidInputFileException("Invalid value for output record format");
        }

        boolean deterministic = chosenJob.getBoolean("deterministic");
        config.setDeterministic(deterministic);
        if(deterministic) {
            config.setSeed(chosenJob.getInt("seed"));
        }

        config.setTimestep(chosenJob.getPeriod("timestep"));
        config.setBinomialSampling(chosenJob.getBoolean("binomial sampling"));
        config.setCTtreeStepBack(chosenJob.getInt("ct tree stepback"));

        // return config
        return config;
    }

    private static FileChannel getFileChannel(Path jobFile) throws IOException {
        HashSet<StandardOpenOption> options = new HashSet<>(Sets.newHashSet(StandardOpenOption.READ, StandardOpenOption.WRITE));
        return FileChannel.open(jobFile, options);
    }

    public static void returnJobToQueue(Path jobFile, DataRow chosenJob, int requiredMemory, int priority, boolean releaseLockOnExit) throws IOException, InvalidInputFileException, InterruptedException {

        // get file and lock
        FileChannel fileChannel = getFileChannel(jobFile);

        try {
            System.out.println("Locking job file (R) @ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            fileChannel.lock(0, Long.MAX_VALUE, false);

            // read in file to data structure
            DataRowSet jobs = readInJobFile(fileChannel);

            boolean jobFound = false;

            for(DataRow job : jobs) {
                // find job that matches on all fields apart from count and memory requirment
                if(sameJob(job, chosenJob)) {
                    // inc. count by 1
                    job.setValue("n", String.valueOf(job.getInt("n") + 1));

                    // update memory requirement (if an increase)
                    if(job.getInt("required memory") < requiredMemory) {
                        job.setValue("required memory", String.valueOf(requiredMemory));
                    }

                    job.setValue("priority", String.valueOf(priority));
                    jobFound = true;
                    break;
                }
            }

            if(!jobFound) {
                // assume remaining job count has been undertaken by other process and thus count has reached zero - thus reinsert job with count 1
                chosenJob.setValue("n", String.valueOf(1));
                chosenJob.setValue("required memory", String.valueOf(requiredMemory));
                chosenJob.setValue("priority", String.valueOf(priority));
                jobs.add(chosenJob);
            }

            // write ds back to file
            writeToJobFile(fileChannel, jobs);

        } finally {
            // release job file
            if(releaseLockOnExit) {
                fileChannel.close(); // also releases the lock
                System.out.println("Releasing job file (R) @ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        }
    }

    private static boolean sameJob(DataRow a, DataRow b) {
        // assert if all fields apart from count and memory requirment
        for(String label : a.getLabels())
            if(!label.equals("required memory") && !label.equals("n") )
                if(!a.getValue(label).equals(b.getValue(label)))
                    return false;

        return true;
    }

    public static void writeToJobFile(FileChannel jobFile, DataRowSet jobs) throws IOException {
        jobFile.truncate(0);

        String toFileString = jobs.toString(order);

        ByteBuffer buf = ByteBuffer.allocate(toFileString.getBytes().length + 1000);
        buf.clear();
        buf.put(toFileString.getBytes());

        buf.flip();

        while(buf.hasRemaining()) {
            jobFile.write(buf);
        }
    }

    public static DataRowSet readInJobFile(FileChannel jobFile) throws IOException, InvalidInputFileException, InterruptedException {

        ByteBuffer buffer = ByteBuffer.allocate((int) jobFile.size());
        int noOfBytesRead = jobFile.read(buffer);

        String labels = null;
        Set<String> data = new HashSet<>();

        while (noOfBytesRead != -1) {

            buffer.flip();

            String line = "";

            while (buffer.hasRemaining()) {

                char c = (char) buffer.get();
                if(c == '\n') {
                    if(labels == null)
                        labels = line;
                    else
                        data.add(line);

                    line = "";
                } else {
                    line += c;
                }

            }

            if(line != "") data.add(line);

            buffer.clear();
            Thread.sleep(1000);
            noOfBytesRead = jobFile.read(buffer);
        }

        try {
            return new DataRowSet(labels, data);
        } catch (NoSuchElementException e) {
            throw new InvalidInputFileException("There's no jobs in the file");
        }
    }

}
