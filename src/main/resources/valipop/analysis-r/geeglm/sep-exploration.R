source("geeglm/process-data-functions.R")  
source("geeglm/id-funtions.R")
source("geeglm/geeglm-functions.R")
source("geeglm/glm-functions.R")
source("geeglm/llm-functions.R")

par(mfrow = c(3,2))
pathToTablesDir <- "../results/minima-ja-f/20171013-015613:749/"
sep <- cleanSepData(readInData(paste(pathToTablesDir, "tables/sep-CT.csv", sep = "")))

sourceSummary(sep)

sep.ids <- addCohortIDs.sep(sep)
sourceSummary(sep.ids)

mod <- sepSatGEEGLM(sep.ids)
anova(mod, test = "Chisq")


sepSatLLM(sep.ids)
