source("geeglm/process-data-functions.R")  

pathToRunDir <- "~/OneDrive/cs/PhD/code/population-model/src/main/resources/valipop/results/minima-scot-l/20171106-133847:812/"
maxBirthingAge <- 50

mbirth <- cleanMBData(readInData(paste(pathToRunDir, "tables/mb-CT.csv", sep = "")), maxBirthingAge, round = FALSE)
sourceSummary(mbirth)

par(mfrow = c(1,1))
source("geeglm/population-plot-functions.R")
plotMB(mbirth)

mbirth.ids1 <- addCohortIDs.mb(mbirth)
mbirth.ids2 <- addCohortIDs.mb2(mbirth)

par(mfrow = c(2,3))

head(mbirth.ids1, 20)
head(mbirth.ids2, 20)

mbSatGEEGLM(mbirth.ids)
mbSatGEEGLM(mbirth.ids2)


#------------

pathToRunDir <- "~/OneDrive/cs/PhD/code/population-model/src/main/resources/valipop/results/minima-scot-A/20171123-165703:533/"
part <- cleanPartData(readInData(paste(pathToRunDir, "tables/part-CT.csv", sep = "")), round = FALSE)

sourceSummary(part)

plotPart(part, "LATEST", date = NULL, disc = TRUE, scales = "free_y")

part.ids <- addCohortIDs.part(part)
part.ids2 <- addCohortIDs.part2(part)
part.ids3 <- addCohortIDs.part3(part)


head(part.ids, 25)
head(part.ids2, 25)
head(part.ids3, 25)

partSatGEEGLM(part.ids)
partSatGEEGLM(part.ids2)
partSatGEEGLM(part.ids3)

#------------

pathToRunDir <- "~/OneDrive/cs/PhD/code/population-model/src/main/resources/valipop/results/minima-scot-B/20171124-024350:195/"
part <- cleanPartData(readInData(paste(pathToRunDir, "tables/part-CT.csv", sep = "")), round = FALSE)

plotPart(part, "LATEST", date = NULL, disc = TRUE, scales = "free_y")
plotPart(part, "LATEST", date = NULL, disc = TRUE, scales = "fixed")

sep <- cleanSepData(readInData(paste(pathToRunDir, "tables/sep-CT.csv", sep = "")), round = FALSE)

sep.ids1 <- addCohortIDs.sep(sep)
sep.ids2 <- addCohortIDs.sep2(sep)

plotSep.single(sep, "Sep")

sepSatGEEGLM(sep.ids1)
sepSatGEEGLM(sep.ids2)
sepSatGLM(sep.ids1)
sepSatGLM(sep.ids2)

sepSatLLM(sep.ids1)

sepSatGEEGLM.2(sep.ids1)
sepSatGEEGLM.2(sep.ids2)
