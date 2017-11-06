
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

deathPath <- paste(subpath, "/death-CT.csv", sep = "")
obPath <- paste(subpath, "/ob-CT.csv", sep = "")
mbPath <- paste(subpath, "/mb-CT.csv", sep = "")
partPath <- paste(subpath, "/part-CT.csv", sep = "")
sepPath <- paste(subpath, "/sep-CT.csv", sep = "")

data.death <- cleanDeathData(readInData(deathPath))
data.ob <- cleanOBData(readInData(obPath), maxBirthingAge)
data.mb <- cleanMBData(readInData(mbPath), maxBirthingAge)
data.part <- cleanPartData(readInData(partPath))
data.sep <- cleanSepData(readInData(sepPath))

death.analysis <- deathSelLLM(data.death)
ob.analysis <- obSelLLM(data.ob)
mb.analysis <- mbSelLLM(data.mb)
part.analysis <- partSelLLM(data.part)
sep.analysis <- sepSelLLM(data.sep)

death.v <- calcV(death.analysis$df, death.analysis$lrt)
ob.v <- calcV(ob.analysis$df, ob.analysis$lrt)
mb.v <- calcV(mb.analysis$df, mb.analysis$lrt)
part.v <- calcV(part.analysis$df, part.analysis$lrt)
sep.v <- calcV(sep.analysis$df, sep.analysis$lrt)

v <- death.v + ob.v + mb.v + part.v + sep.v

print(v)

       

  