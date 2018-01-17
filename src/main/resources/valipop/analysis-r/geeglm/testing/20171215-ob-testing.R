source("process-data-functions.R")  
source("population-plot-functions.R")

pathToRunDir <- "~/OneDrive/cs/PhD/code/population-model/src/main/resources/valipop/results/minima-scot-l/20171106-150452:191/"
obirth <- cleanOBData(readInData(paste(pathToRunDir, "tables/ob-CT.csv", sep = "")), 50)
part <- cleanPartData(readInData(paste(pathToRunDir, "tables/part-CT.csv", sep = "")), round = FALSE)
sourceSummary(obirth)

plotOB.2(obirth, "N,N,N,N,free_y,N")

plotOB.2(obirth, "N,N,N,N,fixed,YES", ciy = "YES", scales = "fixed")
plotOB.2(obirth, "N,N,N,N,fixed,NO", ciy = "NO", scales = "fixed")

plotOB.2(obirth, "N,1855,N,N,fixed,YES", yob = 1855, ciy = "YES", scales = "fixed")
plotOB.2(obirth, "N,1855,N,N,fixed,NO", yob = 1855, ciy = "NO", scales = "fixed")



---
  
plotOB.2(obirth, "N,N,N,N,free_y,N")

plotOB.2(obirth, "N,N,N,N,fixed,YES", ciy = "YES", scales = "fixed")
plotOB.2(obirth, "N,N,N,N,fixed,NO", ciy = "NO", scales = "fixed")

plotOB.2(obirth, "N,1855,N,N,fixed,YES", yob = 1855, ciy = "YES", scales = "fixed")
plotOB.2(obirth, "N,1855,N,N,fixed,NO", yob = 1855, ciy = "NO", scales = "fixed")

plotPart(part, "T")


--------
  
