out <- commandArgs(TRUE)[1]

write(paste("batch", "start.time", "v.M", "stat.run.time", sep = ","), out)

source("src/main/resources/valipop/analysis-r/paper/code/FileFunctions.R")

df.errors <- filesToDF("/cs/tmp/tsd4/results/batch66-fs/batch66-fs-results-summary.csv",
                       "/cs/tmp/tsd4/results/batch67-fs/batch67-fs-results-summary.csv",
                       "/cs/tmp/tsd4/results/batch68-fs/batch68-fs-results-summary.csv", 
                       "/cs/tmp/tsd4/results/batch69-fs/batch69-fs-results-summary.csv", 
                       "/cs/tmp/tsd4/results/batch70-fs/batch70-fs-results-summary.csv", 
                       "/cs/tmp/tsd4/results/batch72-fs/batch72-fs-results-summary.csv", 
                       "/cs/tmp/tsd4/results/batch73-fs/batch72-fs-results-summary.csv", 
                       "/cs/tmp/tsd4/results/batch75-fs/batch75-fs-results-summary.csv", 
                    onlyGetStatErrors = TRUE)

for(i in 1:nrow(df.errors)) {
  
  time.start <- proc.time()[3]
  
  r <- df.errors[i,]$Reason
  st <- df.errors[i,]$Start.Time
  
  # run analysis
  source("src/main/resources/valipop/analysis-r/geeglm/runAnalyisFunction.R")
  pathToRunDir <- paste("/cs/tmp/tsd4/results", r, st, sep = "/")
  runAnalysis(pathToRunDir, 50, paste(pathToRunDir, "RERUN"))
  
  # run count
  analysisFile <- paste("/cs/tmp/tsd4/results", r, st, "analysis.html", sep = "/")
  command <- paste("sh src/main/resources/valipop/analysis-r/geeglm/geeglm-minima-search.sh", analysisFile, "50", sep = " ")
  result <- system(command, intern = TRUE)
  
  # output start time and new v.m to file
  
  time.end <- proc.time()[3]
  time <- time.end - time.start
  write(paste(r, st, result, time, sep = ","), out, append = TRUE)
  
}