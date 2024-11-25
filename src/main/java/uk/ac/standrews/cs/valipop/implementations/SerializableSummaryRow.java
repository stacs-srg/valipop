package uk.ac.standrews.cs.valipop.implementations;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordFormat;

public class SerializableSummaryRow implements Serializable {
    public LocalDateTime startTime;
    public String reason;
    public String codeVersion;
    public Period inputWidth;
    public Period timestep;
    public LocalDate startDate;
    public LocalDate endDate;
    public int simLength;
    public int seed;
    public double birthFactor;
    public double deathFactor;
    public double recoveryFactor;
    public double proportionalRecoveryFactor;
    public double oversizedGeographyFactor;
    public Period minBirthSpacing;
    public RecordFormat outputRecordFormat;
    public int seedPop;
    public String resultsDirectory;
    public String inputsDirectory;
    public int startPop;
    public int totalPop;
    public int endPop;
    public int peakPop;
    public int ctTreeStepback;
    public double ctTreePrecision;
    public int eligibilityChecks;
    public int failedEligibilityChecks;
    public boolean completed;
    public double simRunTime;
    public double ctRunTime;
    public double recordsRunTime;
    public double statsRunTime;
    public boolean binomialSampling;
    public long maxMemoryUsage;
    public Double v;
    public String hostname;
    public SerializableConfig config;

    public SerializableSummaryRow(
        LocalDateTime startTime,
        String reason,
        String codeVersion,
        Period inputWidth,
        Period timestep,
        LocalDate startDate,
        LocalDate endDate,
        int simLength,
        int seed,
        double birthFactor,
        double deathFactor,
        double recoveryFactor,
        double proportionalRecoveryFactor,
        double oversizedGeographyFactor,
        Period minBirthSpacing,
        RecordFormat outputRecordFormat,
        int seedPop,
        String resultsDirectory,
        String inputsDirectory,
        int startPop,
        int totalPop,
        int endPop,
        int peakPop,
        int ctTreeStepback,
        double ctTreePrecision,
        int eligibilityChecks,
        int failedEligibilityChecks,
        boolean completed,
        double simRunTime,
        double ctRunTime,
        double recordsRunTime,
        double statsRunTime,
        boolean binomialSampling,
        long maxMemoryUsage,
        Double v,
        String hostname,
        SerializableConfig config
    ) {
        this.startTime                     = startTime;
        this.reason                        = reason;
        this.codeVersion                   = codeVersion;
        this.inputWidth                    = inputWidth;
        this.timestep                      = timestep;
        this.startDate                     = startDate;
        this.endDate                       = endDate;
        this.simLength                     = simLength;
        this.seed                          = seed;
        this.birthFactor                   = birthFactor;
        this.deathFactor                   = deathFactor;
        this.recoveryFactor                = recoveryFactor;
        this.proportionalRecoveryFactor    = proportionalRecoveryFactor;
        this.oversizedGeographyFactor      = oversizedGeographyFactor;
        this.minBirthSpacing               = minBirthSpacing;
        this.outputRecordFormat            = outputRecordFormat;
        this.seedPop                       = seedPop;
        this.resultsDirectory              = resultsDirectory;
        this.inputsDirectory               = inputsDirectory;
        this.startPop                      = startPop;
        this.totalPop                      = totalPop;
        this.endPop                        = endPop;
        this.peakPop                       = peakPop;
        this.ctTreeStepback                = ctTreeStepback;
        this.ctTreePrecision               = ctTreePrecision;
        this.eligibilityChecks             = eligibilityChecks;
        this.failedEligibilityChecks       = failedEligibilityChecks;
        this.completed                     = completed;
        this.simRunTime                    = simRunTime;
        this.ctRunTime                     = ctRunTime;
        this.recordsRunTime                = recordsRunTime;
        this.statsRunTime                  = statsRunTime;
        this.binomialSampling              = binomialSampling;
        this.maxMemoryUsage                = maxMemoryUsage;
        this.v                             = v;
        this.hostname                      = hostname;
        this.config                        = config;
    }
}
