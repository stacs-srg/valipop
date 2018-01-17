pathToRunDir <- "../results/exp3-ms-ja-5/20171214-115053:996/"

pathToRunDir <- "../results/exp2-ms-sc-5/20171212-165546:396/"


source("geeglm/process-data-functions.R")  
death <- cleanDeathData(readInData(paste(pathToRunDir, "tables/death-CT.csv", sep = "")))
sourceSummary(death)

source("geeglm/id-funtions.R")
death.ids1 <- addCohortIDs.death(death)
death.ids2 <- addCohortIDs.death2(death)

par(mfrow = c(2,3))

source("geeglm/geeglm-functions.R")
m1 <- deathSatGEEGLM(death.ids1)
m1

m2 <- deathSatGEEGLM(death.ids2)
m2
str(death)

plotCohorts(death, 90, title = "")
