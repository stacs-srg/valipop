source("paper/code/FileFunctions.R")

df.check <- filesToDF("/cs/tmp/tsd4/results/batch69-fs/batch69-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch70-fs/batch70-fs-results-summary.csv",
                    onlyGetStatErrors = FALSE)

df.check[, "v.M.Check"] <- NA

setwd("~/OneDrive/cs/PhD/code/population-model/")

for(i in 1:nrow(df.check)) {
  
  failurePath <- paste("~/temp/", df.check[i,]$Reason, "/", df.check[i,]$Start.Time, sep = "")
  system(paste("mkdir -p", failurePath, sep = " "))
  system(paste("touch ", failurePath, "/f.txt", sep = ""))
  command <- paste("sh ~/OneDrive/cs/PhD/code/population-model/src/main/resources/valipop/analysis-r/geeglm/geeglm-minima-search.sh /cs/tmp/tsd4/results/", df.check[i,]$Reason, "/", df.check[i,]$Start.Time, "/analysis.html ", failurePath, "/f.txt", sep = "")
  #system(paste("rm -r", failurePath, sep = " "))
  
  df.check[i,]$v.M.Check <- as.numeric(system(command, intern = TRUE))
  
}

#command <- paste("sh ~/OneDrive/cs/PhD/code/population-model/src/main/resources/valipop/analysis-r/geeglm/geeglm-minima-search.sh /Volumes/TOSHIBA_EXT/pop-runs/", df.all[i,]$Reason, "/", df.all[i,]$Start.Time, "/analysis.html 50", sep = "")

temp.df <- df.check[which(!is.na(df.check$v.M.Check)),]

summary(temp.df)
temp.df[(which(temp.df$v.M != temp.df$v.M.Check)),][c("Start.Time","Reason","v.M","v.M.Check")]
