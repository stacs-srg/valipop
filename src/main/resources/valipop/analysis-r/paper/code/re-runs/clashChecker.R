
srf <- commandArgs(TRUE)[1]
out <- commandArgs(TRUE)[2]

write(paste("start.time", "action", "v.M", sep = ","), out)

source("src/main/resources/valipop/analysis-r/paper/code/FileFunctions.R")

# Read in srf to df
srf.df <- filesToDF(srf)

# for each
for(i in 1:nrow(srf.df)) {
  
  r <- srf.df[i,]$Reason
  st <- srf.df[i,]$Start.Time
  vm <- srf.df[i,]$v.M
  
  command <- paste("sh src/main/resources/valipop/analysis-r/paper/code/checker.sh", r, st, sep = " ")
  ret.a <- system(command, intern = TRUE)
  
  if(ret.a == "FAILED") {
  
    # run analysis
    source("src/main/resources/valipop/analysis-r/runAnalyisFunction.R")
    pathToRunDir <- paste("/cs/tmp/tsd4/results", r, st, sep = "/")
    runAnalysis(pathToRunDir, 50, paste(pathToRunDir, "RERUN"))
    
    # run count
    analysisFile <- paste("/cs/tmp/tsd4/results", r, st, "analysis.html", sep = "/")
    command <- paste("sh src/main/resources/valipop/analysis-r/geeglm/geeglm-minima-search.sh", analysisFile, "50", sep = " ")
    result <- system(command, intern = TRUE)
    
    # output start time and new v.m to file
    write(paste(st, "RERUN", result, sep = ","), out, append = TRUE)
    
  } else {
    write(paste(st, "RETAIN", vm, sep = ","), out, append = TRUE)
  }
  
}
