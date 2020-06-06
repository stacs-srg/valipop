setwd("OneDrive/cs/PhD/code/population-model/src/main/resources/valipop/analysis-r/")
pathToRunDir.nomig <- "/cs/tmp/tsd4/results/fs-15-revert/2019-07-11T15-51-46-491/"
pathToRunDir.mig <- "/cs/tmp/tsd4/results/ss-6/1k/2019-08-16T16-22-57-637/"

source("geeglm/process-data-functions.R")  
source("geeglm/id-funtions.R")
source("geeglm/geeglm-functions.R")

death <- cleanDeathData(readInData(paste(pathToRunDir.mig, "tables/death-CT.csv", sep = "")), round = TRUE, end = 1973)

source("geeglm/utils.R")
source("geeglm/population-plot-functions.R")
title <- paste(" - Age: 0 - ")
title <- wrap_sentence(title, 50)
plotCohorts(death, 0, title)

deathGEEGLM <- function(pathToRunDir, round = T) {
  death <- cleanDeathData(readInData(paste(pathToRunDir.mig, "tables/death-CT.csv", sep = "")), round = round, end = 1973)
  sourceSummary(death)
  death.ids <- addCohortIDs.death(death)
  death.GEEGLM <- deathSatGEEGLM(death.ids)
  print(summary(death.GEEGLM))
}

deathGEEGLM(pathToRunDir.mig, round = T)
deathGEEGLM(pathToRunDir.nomig)

mbirth <- cleanMBData(readInData(paste(pathToRunDir.mig, "tables/mb-CT.csv", sep = "")), 54, round = T)
obirth <- cleanOBData(readInData(paste(pathToRunDir, "tables/ob-CT.csv", sep = "")), maxBirthingAge)
part <- cleanPartData(readInData(paste(pathToRunDir.mig, "tables/part-CT.csv", sep = "")), round = T)
sep <- cleanSepData(readInData(paste(pathToRunDir, "tables/sep-CT.csv", sep = "")), round = T)

sourceSummary(obirth)
sourceSummary(mbirth)
sourceSummary(part)
sourceSummary(sep)

source("id-funtions.R")
obirth.ids <- addCohortIDs.ob(obirth)
mbirth.ids <- addCohortIDs.mb2(mbirth)
part.ids <- addCohortIDs.part3(part)

source("geeglm-functions.R")
death.GEEGLM <- deathSatGEEGLM(death.ids)
print(summary(death.GEEGLM))

ob.GEEGLM <- obSatGEEGLM(obirth.ids)
print(summary(ob.GEEGLM))

mb.GEEGLM <- mbSatGEEGLM(mbirth.ids)
print(summary(mb.GEEGLM))

part.GEEGLM <- partSatGEEGLM(part.ids)
print(summary(part.GEEGLM))


plotMB(mbirth)
plotPart()
