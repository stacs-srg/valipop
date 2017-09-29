
source("geeglm/process-data-functions.R")
source("geeglm/population-plot-functions.R")
source("geeglm/llm-functions.R")
source("geeglm/geeglm-functions.R")
source("geeglm/glm-functions.R")
source("geeglm/id-funtions.R")

par(mfrow = c(2,2))

age <- 0

sc_bf.0_rf.0.5_df.0_mf <- cleanData(readInData("~/OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bf-scot-2/20170928-104109:084/tables/death-CT.csv"))
sourceSummary(sc_bf.0_rf.0.5_df.0_mf)
plotCohorts(sc_bf.0_rf.0.5_df.0_mf, age, "Scot - BF 0 RF 0.5 DF 0 - MF imp")

sc_bf.0_rf.0.5_df.0_mf.OB <- cleanOBData(readInData("~/OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bf-scot-2/20170928-104109:084/tables/ob-CT.csv"), 50)

sc_bf.0.075_rf.0.5_df.0_mf.OB <- cleanOBData(readInData("~/OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bf-scot-4/20170928-144559:115/tables/ob-CT.csv"), 50)

obSelLLM(sc_bf.0_rf.0.5_df.0_mf.OB)
obSelLLM(sc_bf.0.075_rf.0.5_df.0_mf.OB)

sc_bf.0_rf.0.5_df.0_mf.OB.ordered <- addCohortIDs.ob(sc_bf.0_rf.0.5_df.0_mf.OB)
sc_bf.0.075_rf.0.5_df.0_mf.OB.ordered <- addCohortIDs.ob(sc_bf.0.075_rf.0.5_df.0_mf.OB)

obirthSatGEEGLM(sc_bf.0_rf.0.5_df.0_mf.OB.ordered)
obirthSatGEEGLM(sc_bf.0.075_rf.0.5_df.0_mf.OB.ordered)


sourceSummary(d)
data.ob <- cleanOBData(d, 50)

data.ob.sorted <- addCohortIDs.ob(data.ob)
sourceSummary(data.ob.sorted)

obirthSatGEEGLM(data.ob.sorted)
obirthSatGLM(data.ob.sorted)
