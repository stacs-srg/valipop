
srf <- commandArgs(TRUE)[1]
out <- commandArgs(TRUE)[2]
start <- commandArgs(TRUE)[3]
end <- commandArgs(TRUE)[4]

write(paste("start.time", "v.M", sep = ","), out)

source("src/main/resources/valipop/analysis-r/paper/code/FileFunctions.R")

# Read in srf to df
srf.df <- filesToDF(srf, onlyGetStatErrors = FALSE)

if(!is.na(start)) {
  srf.df <- srf.df[c(start:end), ]
}

# for each
for(i in 1:nrow(srf.df)) {
  
  r <- srf.df[i,]$Reason
  st <- srf.df[i,]$Start.Time
  
  # run analysis
  source("src/main/resources/valipop/analysis-r/geeglm/runAnalyisFunction.R")
  pathToRunDir <- paste("/cs/tmp/tsd4/results", r, st, sep = "/")
  runAnalysis(pathToRunDir, 50, paste(pathToRunDir, "RERUN"))
  
  failurePath <- paste("~/temp/", srf.df[i,]$Reason, "/", srf.df[i,]$Start.Time, sep = "")
  system(paste("mkdir -p", failurePath, sep = " "))
  system(paste("touch ", failurePath, "/f.txt", sep = ""))
  # run count
  analysisFile <- paste("/cs/tmp/tsd4/results", r, st, "analysis.html", sep = "/")
  command <- paste("sh src/main/resources/valipop/analysis-r/geeglm/geeglm-minima-search.sh ", analysisFile, " ", failurePath, "/f.txt", sep = "")
  result <- system(command, intern = TRUE)
  
  # output start time and new v.m to file
  write(paste(st, result, sep = ","), out, append = TRUE)
  
}
