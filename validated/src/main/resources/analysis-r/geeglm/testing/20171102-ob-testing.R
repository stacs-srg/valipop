source("geeglm/process-data-functions.R")
source("geeglm/population-plot-functions.R")
source("geeglm/id-funtions.R")
source("geeglm/geeglm-functions.R")
source("geeglm/llm-functions.R")
source("geeglm/glm-functions.R")

path <- "../results/minima-scot-i/20171102-082627:358/tables/"
title <- "19.5M ja pop - bf= -3"

ob <- cleanOBData(readInData(paste(path, "ob-CT.csv", sep = "")), 50)
sourceSummary(ob)

obSelLLM(ob)
plotOB.2(ob, title, scales = "fixed")


path.bf0 <- "../results/minima-scot-i/20171101-111927:575/tables/"
title.bf0 <- "19.5M scot pop - bf = 0"

ob.bf0 <- cleanOBData(readInData(paste(path.bf0, "ob-CT.csv", sep = "")), 50, round = FALSE)
sourceSummary(ob.bf0)
plotOB.2(ob.bf0[which(ob.bf0$Date > 1990),], title.bf0)

plotOB.2(ob.bf0, title.bf0, scales = "fixed", yob = 1855)

plotOB.2(ob, title, ciy = "NO", scales = "fixed", yob = 1855)