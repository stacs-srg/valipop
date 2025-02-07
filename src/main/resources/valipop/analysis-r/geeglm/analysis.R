
# Install geepack if not already present
#if (!require(geepack)) {
#  install.packages("geepack")
#}

pathToRunDir <- commandArgs(TRUE)[1]
maxBirthingAge <- as.integer(commandArgs(TRUE)[2])

death <- cleanDeathData(readInData(paste(pathToRunDir, "/tables/death-CT.csv", sep = "")))
mbirth <- cleanMBData(readInData(paste(pathToRunDir, "/tables/mb-CT.csv", sep = "")), maxBirthingAge, round = T)
obirth <- cleanOBData(readInData(paste(pathToRunDir, "/tables/ob-CT.csv", sep = "")), maxBirthingAge)
part <- cleanPartData(readInData(paste(pathToRunDir, "/tables/part-CT.csv", sep = "")), round = T)

death.ids <- addCohortIDs.death(death)
obirth.ids <- addCohortIDs.ob(obirth)
mbirth.ids <- addCohortIDs.mb2(mbirth)
part.ids <- addCohortIDs.part3(part)

death.GEEGLM <- try(deathSatGEEGLM(death.ids))
ob.GEEGLM <- try(obSatGEEGLM(obirth.ids))
mb.GEEGLM <- try(mbSatGEEGLM(mbirth.ids))
part.GEEGLM <- try(partSatGEEGLM(part.ids))

print(summary(death.GEEGLM))
print(summary(ob.GEEGLM))
print(summary(mb.GEEGLM))
print(summary(part.GEEGLM))