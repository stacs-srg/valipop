source("geeglm/process-data-functions.R")
source("geeglm/population-plot-functions.R")
source("geeglm/id-funtions.R")
source("geeglm/geeglm-functions.R")
source("geeglm/llm-functions.R")
source("geeglm/glm-functions.R")

par(mfrow=c(2,2))

ja.pathToTablesDir <- "../results/exp6-ms-jaAA-1m/20180108-124159:727/tables/"
sc.pathToTablesDir <- "../results/minima-scot-f/20171017-090258:140/tables/"

ja.title <- "ja - bf : 0"
sc.title <- "scot - bf : 2.4375"

ja.part <- cleanPartData(readInData(paste(ja.pathToTablesDir, "part-CT.csv", sep = "")))
sc.part <- cleanPartData(readInData(paste(sc.pathToTablesDir, "part-CT.csv", sep = "")))

sourceSummary(ja.part)
sourceSummary(sc.part)

ja.part.ids <- addCohortIDs.part(ja.part)
sc.part.ids <- addCohortIDs.part(sc.part)

partSatGEEGLM(ja.part.ids)
partSatGEEGLM(sc.part.ids)

#-------

ja.ob <- cleanOBData(readInData(paste(ja.pathToTablesDir, "ob-CT.csv", sep = "")), 55)
sourceSummary(ja.ob)
sourceSummary(ja.ob[which(ja.ob$Age == 15 & ja.ob$CIY == "YES"),])
sourceSummary(ja.ob[which(ja.ob$Age == 15),])

ja.part.unclean <- readInData(paste(ja.pathToTablesDir, "part-CT.csv", sep = ""))
sourceSummary(ja.part.unclean[which(ja.part.unclean$Age == 15),])

#-----------
er.pathToTablesDir <- "../results/minima-scot-f/20171017-231930:963/tables/"
er.title <- "sc - er - bf : 2.0"
er.part <- cleanPartData(readInData(paste(er.pathToTablesDir, "part-CT.csv", sep = "")))
sourceSummary(er.part.nc)

er.part.ids <- addCohortIDs.part(er.part)
sourceSummary(er.part.ids)

er.part.ids2 <- addCohortIDs.part2(er.part)
sourceSummary(er.part.ids2)

str(er.part.ids)
par(mfrow = c(3,2))
m <- partSatGEEGLM(er.part.ids2)
plot(residuals(m), type = "l")
pacf(residuals(m), lag.max = 100)

head(er.part.ids2, 50)

er.ob <- cleanOBData(readInData(paste(er.pathToTablesDir, "ob-CT.csv", sep = "")), 50)
sourceSummary(er.ob[which(er.ob$Age > 39),])
partSelLLM(er.part.ids)
partSelLLM(er.part.ids)

plotPart(er.part, "963 - SCOT - ALL", NULL)

er.ob <- cleanOBData(readInData(paste(er.pathToTablesDir, "ob-CT.csv", sep = "")), 50)
plotOB(er.ob)



bi.pathToTablesDir <- "../results/part-experiments/20171030-132810:067/tables/"
bi.part <- cleanPartData(readInData(paste(bi.pathToTablesDir, "part-CT.csv", sep = "")))
bi.ob <- cleanOBData(readInData(paste(bi.pathToTablesDir, "ob-CT.csv", sep = "")), 50)
summary(bi.ob)

age.min <- 35
age.max <- 39

bi.ob.sub <- bi.ob[which(bi.ob$Age >= age.min & bi.ob$Age <= age.max & bi.ob$NPCIAP == 0),]
bi.ob.sub
sum(bi.ob.sub[bi.ob.sub == "STAT",]$freq)
sum(bi.ob.sub[bi.ob.sub == "SIM",]$freq)

plotPart(bi.part, "067 - ALL", NULL)

bi.ob <- cleanOBData(readInData(paste(bi.pathToTablesDir, "ob-CT.csv", sep = "")), 50)
bi.death <- cleanDeathData(readInData(paste(bi.pathToTablesDir, "death-CT.csv", sep = "")))
plotCohorts(bi.death, 0, "Bi")


fix.pathToTablesDir <- "../results/part-experiments/20171031-101824:562/tables/"
fix.part <- cleanPartData(readInData(paste(fix.pathToTablesDir, "part-CT.csv", sep = "")))
fix.part.nr <- cleanPartData(readInData(paste(fix.pathToTablesDir, "part-CT.csv", sep = "")), round = FALSE)


partSelLLM(fix.part)
partSatGEEGLM(fix.part.nr)

fix.part.nr.ids <- addCohortIDs.part(fix.part.nr)
fix.part.glm.sat <- partSatGLM(fix.part.nr.ids)
summary(fix.part.glm.sat)


plotPart(fix.part.nr, "FIX? - 562NR - ALL", disc = FALSE, scales = "fixed")

fix.pathToTablesDir <- "../results/minima-scot-h/20171031-110844:519/tables/"
fix.part <- cleanPartData(readInData(paste(fix.pathToTablesDir, "part-CT.csv", sep = "")))
fix.part.nr <- cleanPartData(readInData(paste(fix.pathToTablesDir, "part-CT.csv", sep = "")), round = FALSE)

plotPart(fix.part, "519 - ALL")
plotPart(fix.part.nr, "519nr - ALL")
partSelLLM(fix.part.nr)
par(mfrow = c(3,2))

partSatGEEGLM(fix.part.nr)


