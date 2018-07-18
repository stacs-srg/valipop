source("src/main/resources/valipop/analysis-r/paper/code/FileFunctions.R")

df.errors <- filesToDF("/cs/tmp/tsd4/results/batch52-fs/batch52-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch53-fs/batch53-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch53-fs/batch52now53-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch54-fs/batch54-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch55-fs/batch55-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch56-fs/batch56-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch57-fs/batch57-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch58-fs/batch58-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch59-fs/batch59-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch60-fs/batch60-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch61-fs/batch61-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch62-fs/batch62-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch63-fs/batch63-fs-results-summary.csv", 
                    onlyGetStatErrors = TRUE)

for(i in 1:nrow(df.errors)) {
  
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
  write(paste(st, result, sep = ","), out, append = TRUE)
  
}