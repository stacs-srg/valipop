pathToRunDir <- "../results/batch24-job31-sc5/20180315-232711:251/"

source("geeglm/process-data-functions.R")  
death <- cleanDeathData(readInData(paste(pathToRunDir, "tables/death-CT.csv", sep = "")))
sourceSummary(death)

source("geeglm/id-funtions.R")
death.ids1 <- addCohortIDs.death(death)


par(mfrow = c(4,2))

library(geepack)  
mod <- geeglm(freq ~ Date * Age * Sex * Died * Source, id=idvar, data = death.ids1, corstr="ar2")
print(summary(mod))

plot(residuals(mod), type = "l")
acf(residuals(mod), lag.max = 220)
acf(residuals(mod), lag.max = 500)
acf(residuals(mod), lag.max = 1000)


library(MRSea)
runACF(death.ids1$idvar, mod, store=F)
runACF(death.ids1$idvar, mod, store=T)

library(ggplot2)
runDiagnostics(mod)
getPvalues(mod)
runPartialPlots(mod, death.ids1)
plotCumRes(mod, varlist = c())

require(mgcv)
fit<- gam(residuals(mod) ~ s(in.data$YOB))
plot(fit)
summary(fit)

fit<- gam(residuals(mod) ~ s(in.data$idvar))
plot(fit)
summary(fit)

mod



