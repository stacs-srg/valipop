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

df <- data.frame(seedSize=integer(), 
                 averageTotalSize=integer(),
                 simRT=double(),
                 simRTse=double(),
                 ctRT=double(),
                 ctRTse=double(),
                 statRT=double(),
                 statRTse=double(),
                 recRT=double(),
                 recRTse=double(),
                 totalRT=double(),
                 totalRTse=double(),
                 passRate=double(),
                 rf=double(),
                 prf=double(),
                 stringsAsFactors=FALSE)

rts <- data.frame(averageTotalSize=double(),
                  runTime=double(),
                  cumalativeRunTime=double(),
                  type=character(),
                  stringsAsFactors=FALSE)

for(ss in unique(t$Seed.Pop.Size)) {

  t.ss <- t[which(t$Seed.Pop.Size == ss), ]
  
  ats <- mean(t.ss$Total.Pop)
  
  srt <- mean(t.ss$Sim.Run.time)
  srtse <- std(t.ss$Sim.Run.time)
  rt1 <- srt
  rts[nrow(rts)+1,] <- c(ats, rt1, rt1, "Simulation")
  
  ctrt <- mean(t.ss$CT.Run.time)
  ctrtse <- std(t.ss$CT.Run.time)
  rt2 <- ctrt
  rts[nrow(rts)+1,] <- c(ats, rt2, rt1+rt2, "Contingency Tables")
  
  strt <- mean(t.ss$Stats.Run.Time)
  strtse <- std(t.ss$Stats.Run.Time)
  rt3 <- strt
  rts[nrow(rts)+1,] <- c(ats, rt3, rt1+rt2+rt3, "Validation")
  
  rrt <- mean(t.ss$Records.Run.time)
  rrtse <- std(t.ss$Records.Run.time)
  rt4 <- rrt
  rts[nrow(rts)+1,] <- c(ats, rt4, rt1+rt2+rt3+rt4, "Record Generation")
  
  trt <- srt + ctrt + strt + rrt
  trtse <- srtse + ctrtse + strtse + rrtse

  pass.rate <- round(length(which(t.ss$v.M == 0)) / length(which(t.ss$v.M >= 0)), digits = 3)
  
  if(length(unique(t.ss$Recovery.Factor)) != 1) {
    stop("mulitple rfs for a given seed - fix this or change the code")
  }
  
  if(length(unique(t.ss$Proportional.Recovery.Factor)) != 1) {
    stop("mulitple prfs for a given seed - fix this or change the code")
  }
  
  df[nrow(df)+1,] <- c(ss, ats, srt, srtse, ctrt, ctrtse, strt, strtse, rrt, rrtse, trt, trtse, pass.rate, unique(t.ss$Recovery.Factor), unique(t.ss$Proportional.Recovery.Factor))
}

rts$averageTotalSize <- as.numeric(rts$averageTotalSize)
rts$cumalativeRunTime <- as.numeric(rts$cumalativeRunTime)
rts$runTime <- as.numeric(rts$runTime)
rts$type <- factor(rts$type, levels = c("Record Generation", "Validation", "Contingency Tables", "Simulation"))


library(ggthemes)
library(ggplot2)

require(scales)

loglogaxis <- ggplot(df) + scale_x_continuous(trans='log', breaks=c(300000, 500000, 1250000, 2500000, 5000000, 10000000,20000000), labels = comma) +
              scale_y_continuous(trans='log', breaks=c(60,120,180,240,300,600,900,1200,1800,2400,3000,3600,4200))

theme <- theme_gdocs()
scale <- scale_color_gdocs()

p1 <- loglogaxis + theme + scale +
  geom_errorbar(aes(x=averageTotalSize, ymin=simRT-simRTse, ymax=simRT+simRTse), width = 0.05) +
  geom_line(aes(x=averageTotalSize, y=simRT), colour = 'blue') +
  geom_point(aes(x=averageTotalSize, y=simRT), colour = 'blue') +
  xlab("Average of Population Total Size") +
  ylab("Time Taken for Simulation Phase (s)") +
  ggtitle("Simulation Run Time against Total Population Size")
  

p2 <- loglogaxis + theme + scale +
  geom_errorbar(aes(x=averageTotalSize, ymin=ctRT-ctRTse, ymax=ctRT+ctRTse), width = 0.05) +
  geom_line(aes(x=averageTotalSize, y=ctRT), colour = 'red') +
  geom_point(aes(x=averageTotalSize, y=ctRT), colour = 'red') +
  xlab("Average of Population Total Size") +
  ylab("Time Taken for Contingengcy Table Phase (s)") +
  ggtitle("Contingency Table Generation Time against \n Total Population Size")

p3 <- loglogaxis + theme + scale +
  geom_errorbar(aes(x=averageTotalSize, ymin=statRT-statRTse, ymax=statRT+statRTse), width = 0.05) +
  geom_line(aes(x=averageTotalSize, y=statRT), colour = 'cyan') +
  geom_point(aes(x=averageTotalSize, y=statRT), colour = 'cyan') +
  xlab("Average of Population Total Size") +
  ylab("Time Taken for Statistical Validation Phase (s)") +
  ggtitle("Statistical Validation Time against Total Population Size") +
  scale_y_continuous(trans='log', breaks=c(300,360,420,480,540,600,660,720,780,840,900))

p4 <- loglogaxis + theme + scale +
  geom_errorbar(aes(x=averageTotalSize, ymin=recRT-recRTse, ymax=recRT+recRTse), width = 0.05) +
  geom_line(aes(x=averageTotalSize, y=recRT), colour = 'green') +
  geom_point(aes(x=averageTotalSize, y=recRT), colour = 'green') +
  xlab("Average of Population Total Size") +
  ylab("Time Taken for Record Output Phase (s)") +
  ggtitle("Record Output Time against Total Population Size")

dir <- paste("paper/", gsub(" ", "-", Sys.time()), sep = "")
dir.create(dir)

library(gridExtra)
ggsave(plot = arrangeGrob(p1, p2, p3, p4, nrow=2), 
       filename = "breakdown_plots.png",
       path=dir,
       width=w*2, height=h*2, dpi=300)

p5 <- ggplot() + theme + scale +
  geom_area(data = rts, aes(x=averageTotalSize, y=runTime, fill = type), alpha = 0.7, position = position_stack(reverse=F)) +
  geom_point(data = rts, aes(x=averageTotalSize, y=cumalativeRunTime, group = type, fill = type), pch = 23, size = 2) +
  xlab("Average of Population Total Size") +
  ylab("Cumalative Time Taken (s)") +
  ggtitle("Cumalative Run Time against Total Population Size") +
  theme(legend.position="bottom", legend.direction = "horizontal") +
  scale_fill_manual("Cumalative runtime to end of:", breaks = c("Simulation", "Contingency Tables", "Validation", "Record Generation"), values=c("green","cyan","red","blue")) +
  scale_x_continuous(trans='log', breaks=c(312500, 625000, 1250000, 2500000, 5000000, 10000000,20000000), labels = comma)
  

p5b <- ggplot() + theme + scale +
    geom_line(data = rts, aes(x=averageTotalSize, y=cumalativeRunTime, group = type, colour = type)) + 
    geom_point(data = rts, aes(x=averageTotalSize, y=cumalativeRunTime, group = type, colour = type)) +
    xlab("Average of Population Total Size") +
    ylab("Cumalative Time Taken (s)") +
    ggtitle("Cumalative Run Time against Total Population Size") +
    theme(legend.position="bottom", legend.direction = "horizontal") +
    scale_colour_manual("Cumalative runtime to end of:", breaks = c("Simulation","Contingency Tables", "Validation", "Record Generation"), values=c("green","cyan","red","blue")) +
    scale_x_continuous(labels = comma) + theme(plot.margin = margin(10, 30, 10, 30))

  #scale_x_continuous(trans='log', breaks=c(300000, 500000, 1250000, 2500000, 5000000, 10000000,20000000), labels = comma)
    

ggsave(plot = p5, 
       filename = "collective_plot_A.png",
       path = dir,
       width = w, height=h, dpi=300)

ggsave(plot = p5b, 
       filename = "collective_plot_B.png",
       path = dir,
       width = w, height=h, dpi=300)

p6 <- ggplot(t) + theme + scale +
  geom_boxplot(aes(x=t$Total.Pop, y=t$Peak.Memory.Usage..MB., group = t$Seed.Pop.Size), colour = 'blue') +
  xlab("Average of Population Total Size") +
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
  xlab("Average of Population Total Size") +
  ylab("Proportion of Valid Populations") +
  ggtitle("Population Validity against Total Population Size (labeled with \nsimulation 'Recovery Factor' and 'Proportional Recovery Factor')") +
  ylim(0,1) +
  scale_x_continuous(trans='log', breaks=c(300000, 500000, 1250000, 2500000, 5000000, 10000000,20000000))

ggsave(plot = p7, 
       filename = "pass_rate_plot.png", 
       path = dir,
       width=w, height=h, dpi=300)
