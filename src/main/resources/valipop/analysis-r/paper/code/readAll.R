source("src/main/resources/valipop/analysis-r/paper/code/FileFunctions.R")

df.all <- filesToDF("/cs/tmp/tsd4/results/batch52-fs/batch52-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch53-fs/batch53-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch53-fs/batch52now53-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch54-fs/batch54-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch55-fs/batch55-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch56-fs/batch56-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch57-fs/batch57-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch59-fs/batch59-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch60-fs/batch60-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch61-fs/batch61-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch62-fs/batch62-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch63-fs/batch63-fs-results-summary.csv")

summary(df.all)



df.all[, "v.M.Check"] <- NA



df.pre <- df.all[which(df.all$Start.Time.Date < "2018/07/10 18:24:00"),]
df.post <- df.all[which(df.all$Start.Time.Date > "2018/07/10 18:24:00"),]

for(i in 1:nrow(df.post)) {
  
  command <- paste("cat /Volumes/TOSHIBA_EXT/results/", df.post[i,]$Reason, "/", df.post[i,]$Start.Time, "/analysis.html | grep ", df.post[i,]$Start.Time, sep = "")
  a <- system(command, intern = TRUE)
  
  if(length(a) == 0) {
    print(paste("MIXED RUNS -", df.post[i,]$Reason, df.post[i,]$Start.Time))
  }
  
  #df.all[i,]$v.M.Check <- as.numeric(system(command, intern = TRUE))
  
}

#command <- paste("sh ~/OneDrive/cs/PhD/code/population-model/src/main/resources/valipop/analysis-r/geeglm/geeglm-minima-search.sh /Volumes/TOSHIBA_EXT/pop-runs/", df.all[i,]$Reason, "/", df.all[i,]$Start.Time, "/analysis.html 50", sep = "")

temp.df <- df.all[which(!is.na(df.all$v.M.Check)),]

summary(temp.df)
temp.df[(which(temp.df$v.M != temp.df$v.M.Check)),][c("Start.Time","Reason","v.M","v.M.Check")]

df.all$v.M <- df.all$v.M.Check

summary <- dfToSummaryDF(df.all)
plot(summary)

sub <- summary[which(summary$seed == 15625),]

ggplot() + 
  geom_label(data = sub, aes(prf, rf, label = paste(pass.rate, count, sep = "\n"), fill = pass.rate, colour = count), size = 2.5) +
  scale_colour_gradient(low = "red", high = "white") +
  scale_fill_gradient(low = "grey", high = "green")


a <- system("sh ~/OneDrive/cs/PhD/code/population-model/src/main/resources/valipop/analysis-r/geeglm/geeglm-minima-search.sh /Volumes/TOSHIBA_EXT/pop-runs/batch52-fs/20180704-011733:766/analysis.html 50", intern = TRUE)
