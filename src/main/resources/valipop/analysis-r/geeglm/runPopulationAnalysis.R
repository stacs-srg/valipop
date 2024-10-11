source("geeglm/runAnalyisFunction.R")

pathToRunDir <- commandArgs(TRUE)[1]
maxBirthAge <- as.integer(commandArgs(TRUE)[2])
subTitle <- commandArgs(TRUE)[3]

runAnalysis(pathToRunDir, maxBirthAge, subTitle)
