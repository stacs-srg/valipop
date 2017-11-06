
runGEEGLMAnalysis <- function(subTitle, maxBirthingAge) {
  
  pathToTablesDir <- "../results/minima-ja-f/20171013-015613:749/"
  
  source("geeglm/process-data-functions.R")  
  
  pdf(paste(pathToTablesDir, "analysis.pdf", sep = ""),width=8,height=11)

  death <- cleanDeathData(readInData(paste(pathToTablesDir, "tables/death-CT.csv", sep = "")))
  mbirth <- cleanMBData(readInData(paste(pathToTablesDir, "tables/mb-CT.csv", sep = "")), maxBirthingAge)
  obirth <- cleanOBData(readInData(paste(pathToTablesDir, "tables/ob-CT.csv", sep = "")), maxBirthingAge)
  part <- cleanPartData(readInData(paste(pathToTablesDir, "tables/part-CT.csv", sep = "")))
  sep <- cleanSepData(readInData(paste(pathToTablesDir, "tables/sep-CT.csv", sep = "")))

  source("geeglm/population-plot-functions.R")
  
  plot(1,1, col ="white")
  
  sourceSummary(death)
  sourceSummary(obirth)
  sourceSummary(mbirth)
  sourceSummary(part)
  sourceSummary(sep)
  
  
  
  
  
  
  dev.off()
  
}

par(mfrow=c(2,1))
title <- paste(subTitle, " - Age: 0 - ", pathToTablesDir)
plotCohorts(death, 0, title)
title <- paste(subTitle, " - Age: 50 - ", pathToTablesDir)
plotCohorts(death, 50, title)
par(mfrow=c(1,1))

---
source("geeglm/id-funtions.R")
death.ids <- addCohortIDs.death(death)
obirth.ids <- addCohortIDs.ob(obirth)
mbirth.ids <- addCohortIDs.mb(mbirth)
part.ids <- addCohortIDs.part(part)
sep.ids <- addCohortIDs.sep(sep)

source("geeglm/geeglm-functions.R")
deathSatGEEGLM(death.ids)
obSatGEEGLM(obirth.ids)
mbSatGEEGLM(mbirth.ids)
partSatGEEGLM(part.ids)
sepSatGEEGLM(sep.ids)

plotMB(mbirth)
#-----------------------------
source("geeglm/process-data-functions.R")

par(mfrow=c(4,2))

pathToTablesDir <- "../results/minima-ja-f/20171013-015613:749/tables/"
title <- "ja - bf : 0"

death <- cleanDeathData(readInData(paste(pathToTablesDir, "death-CT.csv", sep = "")))
obirth <- cleanOBData(readInData(paste(pathToTablesDir, "ob-CT.csv", sep = "")), 50)
mbirth <- cleanMBData(readInData(paste(pathToTablesDir, "mb-CT.csv", sep = "")), 50)
part <- cleanPartData(readInData(paste(pathToTablesDir, "part-CT.csv", sep = "")))
sep <- cleanSepData(readInData(paste(pathToTablesDir, "sep-CT.csv", sep = "")))
                      
sourceSummary(death)
sourceSummary(obirth)
sourceSummary(mbirth)
sourceSummary(part)
sourceSummary(sep)

source("geeglm/population-plot-functions.R")
plotCohorts(death, 0, title)
plotMB(mbirth)

source("geeglm/id-funtions.R")
death.ids <- addCohortIDs.death(death)
obirth.ids <- addCohortIDs.ob(obirth)
mbirth.ids <- addCohortIDs.mb(mbirth)
part.ids <- addCohortIDs.part(part)
sep.ids <- addCohortIDs.sep(sep)

source("geeglm/geeglm-functions.R")
deathSatGEEGLM(death.ids)
obSatGEEGLM(obirth.ids)
mbSatGEEGLM(mbirth.ids)
partSatGEEGLM(part.ids)
sepSatGEEGLM(sep.ids)

source("geeglm/glm-functions.R")

par(mfrow = c(3,2))
r.mb <- readInData(paste(pathToTablesDir, "mb-CT.csv", sep = ""))
r.mb <- r.mb[r.mb$NCIY != 0,]
r.mb <- r.mb[r.mb$Date > 1855,]
r.mb$freq <- ceiling(r.mb$freq)
sourceSummary(r.mb)
head(r.mb)
plotMB(r.mb)

r.mb.ids <- addCohortIDs.mb(r.mb)

runGEEGLM(freq ~ Date * NCIY * Source, r.mb.ids)

library(MASS)
model = loglm(freq ~ Source * Date * NCIY * Age, data = r.mb.ids)
model

mbSatGEEGLM(r.mb.ids)

plot(part)
plot(part$Age)

r.part <- readInData(paste(pathToTablesDir, "mb-CT.csv", sep = ""))