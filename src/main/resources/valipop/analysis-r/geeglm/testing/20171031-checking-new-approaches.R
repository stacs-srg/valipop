source("geeglm/process-data-functions.R")
source("geeglm/population-plot-functions.R")
source("geeglm/id-funtions.R")
source("geeglm/geeglm-functions.R")
source("geeglm/llm-functions.R")
source("geeglm/glm-functions.R")

path <- "../results/minima-scot-h/20171031-131349:638/tables/"
title <- "6M scot pop - bf=1.375"

#Check part plots
part <- cleanPartData(readInData(paste(path, "part-CT.csv", sep = "")), round = FALSE)
plotPart(part, title, disc = TRUE, scales = "free_y")

part.ids <- addCohortIDs.part(part)
partSatGEEGLM(part.ids)

partSelLLM(part)

#Cohort plot
death <- cleanDeathData(readInData(paste(path, "death-CT.csv", sep = "")), round = FALSE)

sourceSummary(death)
par(mfrow=c(3,2))

plotCohorts(death, 90, title)

#Check OB
ob <- cleanOBData(readInData(paste(path, "ob-CT.csv", sep = "")), 50)
sourceSummary(ob)

obSelLLM(ob)
plotOB.2(ob, "t")
summary(ob)

ob.ids <- addCohortIDs.ob(ob)
ob.mod <- obSatGLM(ob.ids)
summary(ob.mod)

ob.mod.gee <- obSatGEEGLM(ob.ids)

#Checking sep
sep <- cleanSepData(readInData(paste(path, "sep-CT.csv", sep = "")))
sourceSummary(sep[sep$Separated == "YES",])
sep <- sep[sep$Separated == "YES",]

sep.ids <- addCohortIDs.sep(sep)
sep.mod <- sepSatGLM(sep.ids)

sep.mod
sep.mod <- sepSatGEEGLM(sep.ids)
summary(sep.mod)

sep.nr <- cleanSepData(readInData(paste(path, "sep-CT.csv", sep = "")), round = FALSE)
sep.nr <- sep.nr[sep.nr$Separated == "NO",]
sep.nr.ids <- addCohortIDs.sep(sep.nr)
sep.nr.mod <- sepSatGLM.2(sep.nr.ids)
sep.nr.mod.gee <- sepSatGEEGLM.2(sep.nr.ids)
summary(sep.nr.mod.gee)

summary(sep.nr.mod)

sourceSummary(sep.nr)

plotSep(sep.nr, "Sep")
plotSep.single(sep.nr, "Sep")


sep.nr <- cleanSepData(readInData(paste(path, "sep-CT.csv", sep = "")), round = FALSE)
sep.nr <- sep.nr[sep.nr$Separated == "YES",]
sep.nr.ids <- addCohortIDs.sep2(sep.nr)
sep.nr.mod.gee <- sepSatGEEGLM.2(sep.nr.ids)
summary(sep.nr.mod.gee)

mod.glm <- sepSatGLM(sep.nr.ids)
summary(mod.glm)
