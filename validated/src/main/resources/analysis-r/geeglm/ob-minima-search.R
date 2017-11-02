
calcV <- function(df, lrt) {
  v <- 0
  ifelse(df <= 0,
         v <- 1,
         v <- lrt / df
  )
  return(v)
}

source("src/main/resources/analysis-r/geeglm/process-data-functions.R")
source("src/main/resources/analysis-r/geeglm/llm-functions.R")

tablesDir <- commandArgs(TRUE)[1]
maxBirthingAge <- commandArgs(TRUE)[2]

subpath <- tablesDir

obPath <- paste(subpath, "/ob-CT.csv", sep = "")
data.ob <- cleanOBData(readInData(obPath), maxBirthingAge)
ob.analysis <- obSelLLM(data.ob)
ob.v <- calcV(ob.analysis$df, ob.analysis$lrt)

print(v)