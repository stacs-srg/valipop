source("paper/code/FileFunctions.R")

df.all <- filesToDF("/cs/tmp/tsd4/results/batch52-fs/batch52-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch53-fs/batch53-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch53-fs/batch52now53-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch54-fs/batch54-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch55-fs/batch55-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch56-fs/batch56-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch57-fs/batch57-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch58-fs/batch58-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch59-fs/batch59-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch60-fs/batch60-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch61-fs/batch61-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch62-fs/batch62-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch63-fs/batch63-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch64-fs/batch64-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch65-fs/batch65-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch66-fs/batch66-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch67-fs/batch67-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch68-fs/batch68-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch69-fs/batch69-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch70-fs/batch70-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch71-fs/batch71-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch72-fs/batch72-fs-results-summary.csv", 
                    "/cs/tmp/tsd4/results/batch73-fs/batch73-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch74-fs/batch74-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch75-fs/batch75-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch76-fs/batch76-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch77-fs/batch77-fs-results-summary.csv",
                    onlyGetStatErrors = FALSE)

df.all <- filesToDF("/cs/tmp/tsd4/results/ja-batch1/ja-batch1-results-summary.csv",
                    "/cs/tmp/tsd4/results/ja-batch2/ja-batch2-results-summary.csv",
                    "/cs/tmp/tsd4/results/ja-batch3/ja-batch3-results-summary.csv",
                    "/cs/tmp/tsd4/results/ja-batch4/ja-batch4-results-summary.csv",
                    "/cs/tmp/tsd4/results/ja-batch5/ja-batch5-results-summary.csv",
                    "/cs/tmp/tsd4/results/ja-batch6/ja-batch6-results-summary.csv",
                    "/cs/tmp/tsd4/results/ja-batch7/ja-batch7-results-summary.csv",
                    "/cs/tmp/tsd4/results/ja-batch8/ja-batch8-results-summary.csv",
                    "/cs/tmp/tsd4/results/ja-batch9/ja-batch9-results-summary.csv",
                    "/cs/tmp/tsd4/results/ja-batch10/ja-batch10-results-summary.csv",
                    "/cs/tmp/tsd4/results/ja-batch11/ja-batch11-results-summary.csv",
                    "/cs/tmp/tsd4/results/ja-batch12/ja-batch12-results-summary.csv",
                    "/cs/tmp/tsd4/results/ja-batch13/ja-batch13-results-summary.csv",
                    "/cs/tmp/tsd4/results/ja-batch14/ja-batch14-results-summary.csv",
                    "/cs/tmp/tsd4/results/ja-batch15/ja-batch15-results-summary.csv",
                    onlyGetStatErrors = FALSE)

df.all <- filesToDF("/cs/tmp/tsd4/results/PAPER-MANI/PAPER-MANI-results-summary.csv",
                    onlyGetStatErrors = FALSE)

df.all <- filesToDF("/cs/tmp/tsd4/results/mani-paper-19/mani-paper-19-results-summary.csv",
                    onlyGetStatErrors = FALSE)

summary(df.all)


summary <- dfToSummaryDF(df.all)
final <- summaryDfToFinalDF(summary)

selected <- selectFromFullDF(df.all, final)

t <- selected

w <- 8
h <- 5

summary(t)

std <- function(x) sd(x)/sqrt(length(x))

calcCI <- function(data) {
  if(nrow(data) <= 1) {
    print("CI FROM ONE OBSERVATION?!?")
    return(data)
  }
  r <- t.test(data)
  return((r$conf.int[2] - r$conf.int[1]) / 2)
}

df <- data.frame(seedSize=integer(), 
                 averageTotalSize=integer(),
                 simRT=double(),
                 simRTci=double(),
                 ctRT=double(),
                 ctRTci=double(),
                 statRT=double(),
                 statRTci=double(),
                 recRT=double(),
                 recRTci=double(),
                 totalRT=double(),
                 totalRTci=double(),
                 passRate=double(),
                 rf=double(),
                 prf=double(),
                 memoryUsage=double(),
                 memoryUsageCI=double(),
                 firstSuccessTime=double(),
                 firstSuccessTimeCI=double(),
                 stringsAsFactors=FALSE)

rts <- data.frame(averageTotalSize=double(),
                  runTime=double(),
                  cumalativeRunTime=double(),
                  cumalativeCI=double(),
                  type=character(),
                  stringsAsFactors=FALSE)

for(ss in unique(t$Seed.Pop.Size)) {

  t.ss <- t[which(t$Seed.Pop.Size == ss), ]
  
  ats <- mean(t.ss$Total.Pop)
  
  srt <- mean(t.ss$Sim.Run.time)
  srtci <- calcCI(t.ss$Sim.Run.time)
  rt1 <- srt
  rts[nrow(rts)+1,] <- c(ats, rt1, rt1, srtci, "Simulation")
  
  ctrt <- mean(t.ss$CT.Run.time)
  ctRTci <- calcCI(t.ss$CT.Run.time)
  rt2 <- ctrt
  rts[nrow(rts)+1,] <- c(ats, rt2, rt1+rt2, srtci+ctRTci, "Contingency Tables")
  
  strt <- mean(t.ss$Stats.Run.Time)
  strtci <- calcCI(t.ss$Stats.Run.Time)
  rt3 <- strt
  rts[nrow(rts)+1,] <- c(ats, rt3, rt1+rt2+rt3, srtci+ctRTci+strtci, "Validation")
  
  rrt <- mean(t.ss$Records.Run.time)
  rrtci <- calcCI(t.ss$Records.Run.time)
  rt4 <- rrt
  rts[nrow(rts)+1,] <- c(ats, rt4, rt1+rt2+rt3+rt4, srtci+ctRTci+strtci+rrtci, "Record Generation")
  
  trt <- srt + ctrt + strt + rrt
  trtci <- srtci + ctRTci + strtci + rrtci

  pass.rate <- round(length(which(t.ss$v.M == 0)) / length(which(t.ss$v.M >= 0)), digits = 3)
  first.success <- trt / pass.rate
  first.success.ci <- trtci / pass.rate
  
  
  mem <- mean(t.ss$Peak.Memory.Usage..MB.)
  memCI <- calcCI(t.ss$Peak.Memory.Usage..MB.)
  
  if(length(unique(t.ss$Recovery.Factor)) != 1) {
    stop("mulitple rfs for a given seed - fix this or change the code")
  }
  
  if(length(unique(t.ss$Proportional.Recovery.Factor)) != 1) {
    stop("mulitple prfs for a given seed - fix this or change the code")
  }
  
  df[nrow(df)+1,] <- c(ss, ats, srt, srtci, ctrt, ctRTci, strt, strtci, rrt, rrtci, trt, trtci, pass.rate, unique(t.ss$Recovery.Factor), unique(t.ss$Proportional.Recovery.Factor), mem, memCI, first.success, first.success.ci)
}

rts$averageTotalSize <- as.numeric(rts$averageTotalSize)
rts$cumalativeRunTime <- as.numeric(rts$cumalativeRunTime)
rts$cumalativeCI <- as.numeric(rts$cumalativeCI)
rts$runTime <- as.numeric(rts$runTime)
rts$type <- factor(rts$type, levels = c("Record Generation", "Validation", "Contingency Tables", "Simulation"))


library(ggthemes)
library(ggplot2)

require(scales)

loglogaxis <- ggplot(df) 

#+ scale_x_continuous(trans='log', breaks=c(300000, 500000, 1250000, 2500000, 5000000, 10000000,20000000), labels = comma) +
            #  scale_y_continuous(trans='log', breaks=c(60,120,180,240,300,600,900,1200,1800,2400,3000,3600,4200))

theme <- theme_gdocs()
scale <- scale_color_gdocs()

p1 <- ggplot(df) + theme + scale +
  geom_errorbar(aes(x=averageTotalSize, ymin=simRT-simRTci, ymax=simRT+simRTci), width = 0.05) +
  geom_line(aes(x=averageTotalSize, y=simRT), colour = 'blue') +
  geom_point(aes(x=averageTotalSize, y=simRT), colour = 'blue') +
  xlab("Average of Total Population Size") +
  ylab("Time Taken for Simulation Phase (s)") +
  ggtitle("Simulation Run Time against Total Population Size")
  

p2 <- ggplot(df) + theme + scale +
  geom_errorbar(aes(x=averageTotalSize, ymin=ctRT-ctRTci, ymax=ctRT+ctRTci), width = 0.05) +
  geom_line(aes(x=averageTotalSize, y=ctRT), colour = 'red') +
  geom_point(aes(x=averageTotalSize, y=ctRT), colour = 'red') +
  xlab("Average of Total Population Size") +
  ylab("Time Taken for Contingengcy Table Phase (s)") +
  ggtitle("Contingency Table Generation Time against \n Total Population Size")

p3 <- ggplot(df)+ theme + scale +
  geom_errorbar(aes(x=averageTotalSize, ymin=statRT-statRTci, ymax=statRT+statRTci), width = 0.05) +
  geom_line(aes(x=averageTotalSize, y=statRT), colour = 'cyan') +
  geom_point(aes(x=averageTotalSize, y=statRT), colour = 'cyan') +
  xlab("Average of Total Population Size") +
  ylab("Time Taken for Statistical Validation Phase (s)") +
  ggtitle("Statistical Validation Time against Total Population Size") +
  scale_y_continuous(trans='log', breaks=c(300,360,420,480,540,600,660,720,780,840,900))

p4 <- ggplot(df) + theme + scale +
  geom_errorbar(aes(x=averageTotalSize, ymin=recRT-recRTci, ymax=recRT+recRTci), width = 0.05) +
  geom_line(aes(x=averageTotalSize, y=recRT), colour = 'green') +
  geom_point(aes(x=averageTotalSize, y=recRT), colour = 'green') +
  xlab("Average of Total Population Size") +
  ylab("Time Taken for Record Output Phase (s)") +
  ggtitle("Record Output Time against Total Population Size")

dir <- paste("paper/", gsub(" ", "-", Sys.time()), sep = "")
dir.create(dir)

library(gridExtra)
ggsave(plot = arrangeGrob(p1, p2, p3, p4, nrow=2), 
       filename = "breakdown_plots.png",
       path=dir,
       width=w*2, height=h*2, dpi=300)

p5 <- ggplot(data = rts) + theme + scale +
  geom_area(aes(x=averageTotalSize, y=runTime, fill = type), alpha = 0.7, position = position_stack(reverse=F)) +
  geom_errorbar(aes(x=averageTotalSize, ymin=cumalativeRunTime-cumalativeCI, ymax=cumalativeRunTime+cumalativeCI), width = 100000) +
  geom_point(aes(x=averageTotalSize, y=cumalativeRunTime, group = type, fill = type), pch = 23, size = 1) +
  xlab("Average of Total Population Size") +
  ylab("Cumalative Time Taken (s)") +
  ggtitle("Cumalative Run Time against Total Population Size") +
  theme(legend.position="bottom", legend.direction = "horizontal") +
  scale_fill_manual("Cumalative runtime to end of:", breaks = c("Simulation", "Contingency Tables", "Validation", "Record Generation"), values=c("green","cyan","red","blue")) + 
  scale_x_continuous(breaks=c(0, 2500000, 5000000, 7500000, 10000000,12500000, 15000000, 17500000, 20000000), labels = comma) +
  theme(plot.margin=unit(c(0.2,1.0,0.2,0.2),"cm"))
  
ggsave(plot = p5, 
       filename = "collective_plot_A.png",
       path = dir,
       width = w, height=h, dpi=300)

p6 <- ggplot(df) + theme + scale +
  geom_errorbar(aes(x=averageTotalSize, ymin=memoryUsage-memoryUsageCI, ymax=memoryUsage+memoryUsageCI), width = 0.05) +
  geom_line(aes(x=averageTotalSize, y=memoryUsage), colour = 'blue') +
  geom_point(aes(x=averageTotalSize, y=memoryUsage), colour = 'blue') +
  xlab("Average of Total Population Size") +
  ylab("Memory Usage (MB)") +
  ggtitle("Memory Usage against Total Population Size") +
  scale_x_continuous(trans='log', breaks=c(300000, 500000, 1250000, 2500000, 5000000, 10000000,20000000)) +
  scale_y_continuous(trans='log', breaks=c(5000,10000, 20000, 40000, 80000))

ggsave(plot = p6, 
       filename = "memory_plot.png",
       path = dir,
       width=w, height=h, dpi=300)

p7 <- ggplot(df) + theme + scale +
  geom_point(stat = "identity", aes(x=df$averageTotalSize, y=df$passRate), colour = 'blue', fill = 'blue', pch = 4, size = 5) +
  geom_label(aes(x=df$averageTotalSize, y=df$passRate, label = paste("RF: ", df$rf, "\nPRF: ", df$prf)), nudge_x = -0.3, nudge_y = -0.1, hjust = 0) +
  xlab("Average of Total Population Size") +
  ylab("Proportion of Valid Populations") +
  ggtitle("Population Validity against Total Population Size (labeled with \nsimulation 'Recovery Factor' and 'Proportional Recovery Factor')") +
  ylim(0,1) +
  scale_x_continuous(trans='log', breaks=c(300000, 500000, 1250000, 2500000, 5000000, 10000000,20000000))

ggsave(plot = p7, 
       filename = "pass_rate_plot.png", 
       path = dir,
       width=w, height=h, dpi=300)

p8 <- ggplot(df) + theme + scale +
  geom_errorbar(aes(x=averageTotalSize, ymin=firstSuccessTime-firstSuccessTimeCI, ymax=firstSuccessTime+firstSuccessTimeCI), width = 250000) +
  geom_point(aes(x=df$averageTotalSize, y=df$firstSuccessTime), colour = 'blue', fill = 'blue', pch = 4, size = 3) +
  xlab("Average of Total Population Size") +
  ylab("Average Time to Valid Populations") +
  ggtitle("Average Time to Valid Population against Total Population Size") +
  scale_x_continuous(breaks=c(0, 2500000, 5000000, 7500000, 10000000,12500000, 15000000, 17500000, 20000000), labels = comma) +
  theme(plot.margin=unit(c(0.2,1.0,0.2,0.2),"cm"))

ggsave(plot = p8, 
       filename = "average_time_plot.png", 
       path = dir,
       width=w, height=h, dpi=300)
