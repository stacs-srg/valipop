
par(mfrow=c(2,2))

df.0_rf0.5 <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-dfs/20170927-105211:760/tables/death-CT.csv"))
sourceSummary(df.0_rf0.5)

plotCohorts(df.0_rf0.5, 0, "DF-0 RF-0.5")

df.0.1_rf0.5 <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-dfs/20170927-110259:682/tables/death-CT.csv"))
sourceSummary(df.0.1_rf0.5)

plotCohorts(df.0.1_rf0.5, 0, "DF-0.1 RD - 0.5")

bf.minus1 <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-dfs/20170927-112800:244/tables/death-CT.csv"))
sourceSummary(bf.minus1)
plotCohorts(bf.minus1, 0, "BF -1")

bf.0.1 <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bfs/20170927-114740:759/tables/death-CT.csv"))
sourceSummary(bf.0.1)
plotCohorts(bf.0.1, 0, "BF 0.1 RF 1")

bf.0.25 <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bfs/20170927-115713:912/tables/death-CT.csv"))
sourceSummary(bf.0.25)
plotCohorts(bf.0.25, 0, "BF 0.25 RF 1")
bf.0.25.ordered <- addPanelIDs.SY(bf.0.25)

deathSatGEEGLM(bf.0.25.ordered)

bf.0.33_rf.0.0 <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bfs/20170927-121043:901/tables/death-CT.csv"))
sourceSummary(bf.0.33_rf.0.0)
plotCohorts(bf.0.33_rf.0.0, 0, "BF 0.33 RF 0")

bf.0.33_rf.0.5 <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bfs/20170927-122108:040/tables/death-CT.csv"))
sourceSummary(bf.0.33_rf.0.5)
plotCohorts(bf.0.33_rf.0.5, 0, "BF 0.33 RF 0.5 DF 0")

bf.0.33_rf.0.5.ordered <- addPanelIDs.SSY(bf.0.33_rf.0.5)

deathSatGEEGLM(bf.0.33_rf.0.5.ordered)

bf.0.33_rf.1.0 <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bfs/20170927-123147:734/tables/death-CT.csv"))
sourceSummary(bf.0.33_rf.1.0)
plotCohorts(bf.0.33_rf.1.0, 0, "BF 0.33 RF 1.0 DF 0")

bf.0.33_rf.0.5_df0.25 <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bfs/20170927-131049:000/tables/death-CT.csv"))
sourceSummary(bf.0.33_rf.0.5_df0.25)
plotCohorts(bf.0.33_rf.0.5_df0.25, 0, "BF 0.33 RF 0.5 DF 0.25")

bf.0.33_rf.0.5_df0.5 <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bfs/20170927-132208:502/tables/death-CT.csv"))
sourceSummary(bf.0.33_rf.0.5_df0.5)
plotCohorts(bf.0.33_rf.0.5_df0.5, 0, "BF 0.33 RF 0.5 DF 0.5")

par(mfrow=c(2,2))

bf.0.33_rf.0.5 <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bfs/20170927-122108:040/tables/death-CT.csv"))
sourceSummary(bf.0.33_rf.0.5)
plotCohorts(bf.0.33_rf.0.5, 80, "BF 0.33 RF 0.5 DF 0 - A80")

bf.0.33_rf.0.5_df0.25 <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bfs/20170927-131049:000/tables/death-CT.csv"))
sourceSummary(bf.0.33_rf.0.5_df0.25)
plotCohorts(bf.0.33_rf.0.5_df0.25, 80, "BF 0.33 RF 0.5 DF 0.25 - A80")

bf.0.33_rf.0.5_df0.5 <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bfs/20170927-132208:502/tables/death-CT.csv"))
sourceSummary(bf.0.33_rf.0.5_df0.5)
plotCohorts(bf.0.33_rf.0.5_df0.5, 80, "BF 0.33 RF 0.5 DF 0.5 - A80")

par(mfrow=c(2,2))

bf.0.33_rf.0.5_mf <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bfs/20170927-133807:467/tables/death-CT.csv"))
sourceSummary(bf.0.33_rf.0.5_mf)
plotCohorts(bf.0.33_rf.0.5_mf, 0, "BF 0.33 RF 0.5 DF 0 - MF imp")

bf.0.0_rf.0.5_mf <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bfs/20170927-135943:041/tables/death-CT.csv"))
sourceSummary(bf.0.0_rf.0.5_mf)
plotCohorts(bf.0.0_rf.0.5_mf, 0, "BF 0 RF 0.5 DF 0 - MF imp")
plotCohorts(bf.0.0_rf.0.5_mf, 50, "BF 0 RF 0.5 DF 0 - MF imp - A50")
plotCohorts(bf.0.0_rf.0.5_mf, 80, "BF 0 RF 0.5 DF 0 - MF imp - A80")
plotCohorts(bf.0.0_rf.0.5_mf, 100, "BF 0 RF 0.5 DF 0 - MF imp - A100")

bf.0.0_rf.0_mf <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bfs/20170927-141351:523/tables/death-CT.csv"))
sourceSummary(bf.0.0_rf.0_mf)
plotCohorts(bf.0.0_rf.0_mf, 0, "BF 0 RF 0 DF 0 - MF imp")
plotCohorts(bf.0.0_rf.0_mf, 50, "BF 0 RF 0 DF 0 - MF imp - A50")
plotCohorts(bf.0.0_rf.0_mf, 80, "BF 0 RF 0 DF 0 - MF imp - A80")
plotCohorts(bf.0.0_rf.0_mf, 100, "BF 0 RF 0 DF 0 - MF imp - A100")

bf.0.0_rf.1_mf <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bfs/20170927-142415:966/tables/death-CT.csv"))
sourceSummary(bf.0.0_rf.1_mf)
plotCohorts(bf.0.0_rf.1_mf, 0, "BF 0 RF 1 DF 0 - MF imp")
plotCohorts(bf.0.0_rf.1_mf, 50, "BF 0 RF 1 DF 0 - MF imp - A50")
plotCohorts(bf.0.0_rf.1_mf, 80, "BF 0 RF 1 DF 0 - MF imp - A80")
plotCohorts(bf.0.0_rf.1_mf, 100, "BF 0 RF 1 DF 0 - MF imp - A100")

plotCohorts(bf.0.0_rf.0_mf, 0, "BF 0 RF 0 DF 0 - MF imp")
plotCohorts(bf.0.0_rf.0.5_mf, 0, "BF 0 RF 0.5 DF 0 - MF imp")
plotCohorts(bf.0.0_rf.1_mf, 0, "BF 0 RF 1 DF 0 - MF imp")

bf.0.0_rf.0.5_mf.ordered <- addPanelIDs.SSY(bf.0.0_rf.0.5_mf)
deathSatGEEGLM(bf.0.0_rf.0.5_mf.ordered)
deathSelGEEGLM(bf.0.0_rf.0.5_mf.ordered)

par(mfrow=c(2,2))

bf.m0.36_rf.0.5_mf <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bfs/20170927-144352:731/tables/death-CT.csv"))
sourceSummary(bf.m0.36_rf.0.5_mf)
plotCohorts(bf.m0.36_rf.0.5_mf, 0, "BF -0.36 RF 0.5 DF 0 - MF imp")
plotCohorts(bf.m0.36_rf.0.5_mf, 50, "BF -0.36 RF 0.5 DF 0 - MF imp - A50")
plotCohorts(bf.m0.36_rf.0.5_mf, 80, "BF -0.36 RF 0.5 DF 0 - MF imp - A80")
plotCohorts(bf.m0.36_rf.0.5_mf, 100, "BF -0.36 RF 0.5 DF 0 - MF imp - A100")

bf.m0.36_rf.0.5_mf.ordered <- addPanelIDs.SSY(bf.m0.36_rf.0.5_mf)
deathSatGEEGLM(bf.m0.36_rf.0.5_mf.ordered)
deathSelGEEGLM(bf.m0.36_rf.0.5_mf.ordered)

par(mfrow=c(2,2))

bf.m0.3_rf.0.5_mf <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bfs/20170927-145715:904/tables/death-CT.csv"))
sourceSummary(bf.m0.3_rf.0.5_mf)
plotCohorts(bf.m0.3_rf.0.5_mf, 0, "BF -0.3 RF 0.5 DF 0 - MF imp")
plotCohorts(bf.m0.3_rf.0.5_mf, 50, "BF -0.3 RF 0.5 DF 0 - MF imp - A50")
plotCohorts(bf.m0.3_rf.0.5_mf, 80, "BF -0.3 RF 0.5 DF 0 - MF imp - A80")
plotCohorts(bf.m0.3_rf.0.5_mf, 100, "BF -0.3 RF 0.5 DF 0 - MF imp - A100")

deathSelLLM(bf.m0.3_rf.0.5_mf)

bf.m0.3_rf.0.5_mf.ordered <- addPanelIDs.SSY(bf.m0.3_rf.0.5_mf)
deathSatGEEGLM(bf.m0.3_rf.0.5_mf.ordered)
deathSelGEEGLM(bf.m0.3_rf.0.5_mf.ordered)

bf.m0.3_rf.0.3_df.0.25_mf <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bfs/20170927-164243:335/tables/death-CT.csv"))
sourceSummary(bf.m0.3_rf.0.3_df.0.25_mf)
plotCohorts(bf.m0.3_rf.0.3_df.0.25_mf, 0, "BF -0.3 RF 0.3 DF 0.25 - MF imp")

bf.m0.3_rf.0.3_df.0.5_mf <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bfs/20170927-165320:705/tables/death-CT.csv"))
sourceSummary(bf.m0.3_rf.0.3_df.0.5_mf)
plotCohorts(bf.m0.3_rf.0.3_df.0.5_mf, 0, "BF -0.3 RF 0.3 DF 0.5 - MF imp")

age <- 0

bf.m0.35_rf.0.1_df.0_mf <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bf-scot/20170928-004319:565/tables/death-CT.csv"))
sourceSummary(bf.m0.35_rf.0.1_df.0_mf)
plotCohorts(bf.m0.35_rf.0.1_df.0_mf, age, "BF -0.35 RF 0.1 DF 0 - MF imp")

bf.m0.3_rf.0.3_df.0_mf <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bf-scot/20170928-054108:134/tables/death-CT.csv"))
sourceSummary(bf.m0.3_rf.0.3_df.0_mf)
plotCohorts(bf.m0.3_rf.0.3_df.0_mf, age, "BF -0.3 RF 0.3 DF 0 - MF imp")

bf.m0.35_rf.1_df.0_mf <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bf-scot/20170928-084652:731/tables/death-CT.csv"))
sourceSummary(bf.m0.35_rf.1_df.0_mf)
plotCohorts(bf.m0.35_rf.1_df.0_mf, age, "BF -0.35 RF 1 DF 0 - MF imp")

sc_bf.0_rf.0.5_df.0_mf <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bf-scot-2/20170928-104109:084/tables/death-CT.csv"))
sourceSummary(sc_bf.0_rf.0.5_df.0_mf)
plotCohorts(sc_bf.0_rf.0.5_df.0_mf, age, "Scot - BF 0 RF 0.5 DF 0 - MF imp")

sc_bf.0.25_rf.0.5_df.0_mf <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bf-scot-3/20170928-120908:142/tables/death-CT.csv"))
sourceSummary(sc_bf.0.25_rf.0.5_df.0_mf)
plotCohorts(sc_bf.0.25_rf.0.5_df.0_mf, age, "Scot - BF 0.25 RF 0.5 DF 0 - MF imp")

sc_bf.0.5_rf.0.5_df.0_mf <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bf-scot-3/20170928-122024:671/tables/death-CT.csv"))
sourceSummary(sc_bf.0.5_rf.0.5_df.0_mf)
plotCohorts(sc_bf.0.5_rf.0.5_df.0_mf, age, "Scot - BF 0.5 RF 0.5 DF 0 - MF imp")

par(mfrow=c(2,2))

sc_bf.0.05_rf.0.5_df.0_mf <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bf-scot-4/20170928-135213:035/tables/death-CT.csv"))
sourceSummary(sc_bf.0.05_rf.0.5_df.0_mf)
plotCohorts(sc_bf.0.05_rf.0.5_df.0_mf, age, "Scot - BF 0.05 RF 0.5 DF 0 - MF imp")

deathSelLLM(sc_bf.0.05_rf.0.5_df.0_mf)

sc_bf.0.05_rf.0.5_df.0_mf.ordered <- addPanelIDs.SSY(sc_bf.0.05_rf.0.5_df.0_mf)
deathSatGEEGLM(sc_bf.0.05_rf.0.5_df.0_mf.ordered)

sc_bf.0.1_rf.0.5_df.0_mf <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bf-scot-4/20170928-140304:055/tables/death-CT.csv"))
sourceSummary(sc_bf.0.1_rf.0.5_df.0_mf)
plotCohorts(sc_bf.0.1_rf.0.5_df.0_mf, age, "Scot - BF 0.1 RF 0.5 DF 0 - MF imp")

deathSelLLM(sc_bf.0.1_rf.0.5_df.0_mf)

sc_bf.0.1_rf.0.5_df.0_mf.ordered <- addPanelIDs.SY(sc_bf.0.1_rf.0.5_df.0_mf)
deathSatGEEGLM(sc_bf.0.1_rf.0.5_df.0_mf.ordered)
deathSatGLM(sc_bf.0.1_rf.0.5_df.0_mf.ordered)

sc_bf.0.75_rf.0.5_df.0_mf <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bf-scot-4/20170928-144559:115/tables/death-CT.csv"))
sourceSummary(sc_bf.0.75_rf.0.5_df.0_mf)
plotCohorts(sc_bf.0.75_rf.0.5_df.0_mf, age, "Scot - BF 0.075 RF 0.5 DF 0 - MF imp")

deathSelLLM(sc_bf.0.75_rf.0.5_df.0_mf)

sc_bf.0.75_rf.0.5_df.0_mf.ordered <- addPanelIDs.SSY(sc_bf.0.75_rf.0.5_df.0_mf)
deathSatGEEGLM(sc_bf.0.75_rf.0.5_df.0_mf.ordered)

L_sc_bf.0.75_rf.0.5_df.0_mf <- cleanData(readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bf-scot-4/20170928-150551:663/tables/death-CT.csv"))
sourceSummary(L_sc_bf.0.75_rf.0.5_df.0_mf)
plotCohorts(L_sc_bf.0.75_rf.0.5_df.0_mf, age, "Large - Scot - BF 0.075 RF 0.5 DF 0 - MF imp")

deathSelLLM(L_sc_bf.0.75_rf.0.5_df.0_mf)

L_sc_bf.0.75_rf.0.5_df.0_mf.ordered <- addPanelIDs.SSY(L_sc_bf.0.75_rf.0.5_df.0_mf)
deathSatGEEGLM(L_sc_bf.0.75_rf.0.5_df.0_mf.ordered)

#OB
d <- readInData("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bf-scot-4/20170928-144559:115/tables/ob-CT.csv")

sourceSummary(d)
data.ob <- cleanOBData(d, 50)

data.ob.sorted <- addCohortIDs.ob(data.ob)
sourceSummary(data.ob.sorted)

obirthSatGEEGLM(data.ob.sorted)
obirthSatGLM(data.ob.sorted)
