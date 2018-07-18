pathToRunDir = "../results/testing-stats/20180613-133510:959/"
pathToRunDirB = "../results/testing-stats/20180613-110533:462/"
pathToRunDirC = "../results/batch39-job36-sc500k/20180604-121800:537/"

source("geeglm/process-data-functions.R")  
death <- cleanDeathData(readInData(paste(pathToRunDir, "tables/death-CT.csv", sep = "")), round = F, start = 1850)
death <- readInData(paste(pathToRunDir, "tables/death-CT.csv", sep = ""))

source("geeglm/utils.R")
source("geeglm/population-plot-functions.R")
title <- paste("", " - Age: 0 - ", pathToRunDir)
title <- wrap_sentence(title, 50)
plotCohorts(death, 0, title, xlim = c(1854, 2010))
title <- paste("", " - Age: 50 - ", pathToRunDir)
title <- wrap_sentence(title, 50)
plotCohorts(death, 50, title, xlim = c(1854, 2010))


death[which(death$Source == "SIM" & death$freq > 19000) , ]
death[which(death$Date == 1855 & death$Age == 0 & death$Sex == "MALE") , ]

sort(unique(death[which(death$freq == death$Date) , ]$Age))

sourceSummary(death)



sep <- cleanSepData(readInData(paste(pathToRunDir, "tables/sep-CT.csv", sep = "")), round = F)
sepB <- cleanSepData(readInData(paste(pathToRunDirB, "tables/sep-CT.csv", sep = "")), round = F)
sepC <- cleanSepData(readInData(paste(pathToRunDirC, "tables/sep-CT.csv", sep = "")), round = F)

sourceSummary(sepC)
sourceSummary(sepB)
plotSep.single(sep, title = "TEST A")
plotSep.single(sepB, title = "TEST B")
plotSep(sepC, title = "TEST C")

source("geeglm/id-funtions.R")
source("geeglm/geeglm-functions.R")
sep.ids <- addCohortIDs.sep2(sepC)
sep.GEEGLM <- sepSatGEEGLM(sep.ids)
print(summary(sep.GEEGLM))
