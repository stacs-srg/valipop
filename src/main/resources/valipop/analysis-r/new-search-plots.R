setwd("OneDrive/cs/PhD/code/population-model/src/main/resources/valipop/analysis-r/")

checkPlots <- function(full_df) {

    library(ggplot2)
    
    print(ggplot(full_df) +
      geom_point(aes(Total.Pop, Peak.Memory.Usage..MB.), 
                 col = round(3*full_df$Proportional.Recovery.Factor)+1,
                 shape = round(3*full_df$Recovery.Factor)+1))
    
    print(ggplot(full_df) +
      geom_point(aes(Seed.Pop.Size, Total.Pop), 
                 col = round(3*full_df$Proportional.Recovery.Factor)+1,
                 shape = round(3*full_df$Recovery.Factor)+1))
    
    print(ggplot(full_df) +
            geom_point(aes(Seed.Pop.Size, Peak.Memory.Usage..MB.), 
                       col = round(3*full_df$Proportional.Recovery.Factor)+1,
                       shape = round(3*full_df$Recovery.Factor)+1))

}

fs3D <- function(seed, summary_df, rf.min = NA, rf.max = NA, prf.min = NA, prf.max = NA, type = "mesh3d") {
  
  sub <- getSubDF(seed, summary_df, rf.min, rf.max, prf.min, prf.max)
  
  library(plotly)
  plot_ly() %>%
    add_trace(data = sub, x = sub$prf, y = sub$rf, z = sub$mean, type=type, opacity=0.5)  %>%
    add_trace(data = sub, x = sub$prf, y = sub$rf, z = sub$min, type=type, opacity=0.5)  %>%
    layout(
      title = paste("Cluster experiments - seed =", seed),
      scene = list(
        xaxis = list(title = "prf"),
        yaxis = list(title = "rf"),
        zaxis = list(title = "v")
      ))
}

fs3Dbf <- function(seed, summary_df, rf.min = NA, rf.max = NA, prf.min = NA, prf.max = NA, type = "mesh3d") {
  
  sub <- getSubDF(seed, summary_df, rf.min, rf.max, prf.min, prf.max)
  
  bf._1.0 <- sub[which(sub$bf == -1.0), ]
  bf._0.75 <- sub[which(sub$bf == -0.75), ]
  bf._0.5 <- sub[which(sub$bf == -0.5), ]
  bf._0.25 <- sub[which(sub$bf == -0.25), ]
  bf.0.0 <- sub[which(sub$bf == 0.0), ]
  bf.0.1 <- sub[which(sub$bf == 0.1), ]
  bf.0.2 <- sub[which(sub$bf == 0.2), ]
  bf.0.25 <- sub[which(sub$bf == 0.25), ]
  bf.0.3 <- sub[which(sub$bf == 0.3), ]
  bf.0.4 <- sub[which(sub$bf == 0.4), ]
  bf.0.5 <- sub[which(sub$bf == 0.5), ]
  bf.0.6 <- sub[which(sub$bf == 0.6), ]
  bf.0.7 <- sub[which(sub$bf == 0.7), ]
  bf.0.75 <- sub[which(sub$bf == 0.75), ]
  bf.0.8 <- sub[which(sub$bf == 0.8), ]
  bf.0.9 <- sub[which(sub$bf == 0.9), ]
  bf.1.0 <- sub[which(sub$bf == 1.0), ]
  
  library(plotly)
  plot_ly() %>%
    add_trace(data = bf._1.0, x = bf._1.0$prf, y = bf._1.0$rf, z = bf._1.0$min, type=type, opacity=0.5, name = "-1.0")  %>%
    add_trace(data = bf._0.75, x = bf._0.75$prf, y = bf._0.75$rf, z = bf._0.75$min, type=type, opacity=0.5, name = "-0.75")  %>%
    add_trace(data = bf._0.5, x = bf._0.5$prf, y = bf._0.5$rf, z = bf._0.5$min, type=type, opacity=0.5, name = "-0.5")  %>%
    add_trace(data = bf._0.25, x = bf._0.25$prf, y = bf._0.25$rf, z = bf._0.25$min, type=type, opacity=0.25, name = "-0.25")  %>%
    add_trace(data = bf.0.0, x = bf.0.0$prf, y = bf.0.0$rf, z = bf.0.0$min, type=type, opacity=0.5, name = "0.0")  %>%
    add_trace(data = bf.0.1, x = bf.0.1$prf, y = bf.0.1$rf, z = bf.0.1$min, type=type, opacity=0.5, name = "0.1")  %>%
    add_trace(data = bf.0.2, x = bf.0.2$prf, y = bf.0.2$rf, z = bf.0.2$min, type=type, opacity=0.5, name = "0.2")  %>%
    add_trace(data = bf.0.25, x = bf.0.25$prf, y = bf.0.25$rf, z = bf.0.25$min, type=type, opacity=0.25, name = "0.25")  %>%
    add_trace(data = bf.0.3, x = bf.0.3$prf, y = bf.0.3$rf, z = bf.0.3$min, type=type, opacity=0.5, name = "0.3")  %>%
    add_trace(data = bf.0.4, x = bf.0.4$prf, y = bf.0.4$rf, z = bf.0.4$min, type=type, opacity=0.5, name = "0.4")  %>%
    add_trace(data = bf.0.5, x = bf.0.5$prf, y = bf.0.5$rf, z = bf.0.5$min, type=type, opacity=0.5, name = "0.5")  %>%
    add_trace(data = bf.0.6, x = bf.0.6$prf, y = bf.0.6$rf, z = bf.0.6$min, type=type, opacity=0.5, name = "0.6")  %>%
    add_trace(data = bf.0.7, x = bf.0.7$prf, y = bf.0.7$rf, z = bf.0.7$min, type=type, opacity=0.5, name = "0.7")  %>%
    add_trace(data = bf.0.75, x = bf.0.75$prf, y = bf.0.75$rf, z = bf.0.75$min, type=type, opacity=0.5, name = "0.75")  %>%
    add_trace(data = bf.0.8, x = bf.0.8$prf, y = bf.0.8$rf, z = bf.0.8$min, type=type, opacity=0.5, name = "0.8")  %>%
    add_trace(data = bf.0.9, x = bf.0.9$prf, y = bf.0.9$rf, z = bf.0.9$min, type=type, opacity=0.5, name = "0.9")  %>%
    add_trace(data = bf.1.0, x = bf.1.0$prf, y = bf.1.0$rf, z = bf.1.0$min, type=type, opacity=0.5, name = "1.0")  %>%
    layout(
      title = paste("Cluster experiments - seed =", seed),
      scene = list(
        xaxis = list(title = "prf"),
        yaxis = list(title = "rf"),
        zaxis = list(title = "v")
      ),
      showlegend = TRUE
    )
}


promisingCandidates <- function(seed, summaryDF, minN = NA) {
  
  t <- summaryDF
  
  t.s <- t[which(t$seed == seed), ]
    
  if(!is.na(minN)) {
    t.s <- t.s[which(t.s$count >= minN), ]
  }
  
  sorted <- t.s[order(-t.s$pass.rate, -t.s$count, t.s$min, t.s$mean),] 
  
  return(sorted)
}

promisingCandidatesMean <- function(seed, summaryDF, minN = NA) {
  
  t <- summaryDF
  
  t.s <- t[which(t$seed == seed), ]
  
  if(!is.na(minN)) {
    t.s <- t.s[which(t.s$count >= minN), ]
  }
  
  sorted <- t.s[order(-t.s$pass.rate, t.s$mean, t.s$min),] 
  
  return(sorted)
}

getBestNRuns <- function(tDF, n, popSize) {
  tDF[which(tDF$Seed.Pop.Size == popSize),][order(tDF[which(tDF$Seed.Pop.Size == popSize),]$v.M),][1:n,]
}

avgRunTimesBySeed <- function(tDF) {
  seeds <- unique(tDF$Seed.Pop.Size)[order(unique(tDF$Seed.Pop.Size))]
  for(seed in seeds) {
    sub <- tDF[which(tDF$Seed.Pop.Size == seed),]
    print(paste(seed, "-", round(mean(sub$Total.Pop)), "-", round(mean(sub$CT.Run.time + sub$Sim.Run.time + sub$Stats.Run.Time + sub$Records.Run.time))))
  }
}

promisingCandidatesBySeed <- function(runs.fs, cutoff) {
  seeds <- unique(runs.fs$Seed.Pop.Size)[order(unique(runs.fs$Seed.Pop.Size))]
  
  for(seed in seeds) {
    print(paste("For seed: ", seed))
    print(promisingCandidates(seed, dfToSummaryDF(runs.fs))[1:cutoff,])
  }
}

promisingCandidatesPlot <- function(runs.fs, cutoff, rf.min = 0.0, rf.max = 1.0, prf.min = 0.0, prf.max = 1.0, seeds = NA) {
  if(is.na(seeds)) {
    seeds <- unique(runs.fs$Seed.Pop.Size)[order(unique(runs.fs$Seed.Pop.Size))]
  }
  
  cutoff.origonal <- cutoff
  
  library(ggplot2)
  
  for(seed in seeds) {
    
    sub <- getSubDF(seed, dfToSummaryDF(runs.fs), rf.min, rf.max, prf.min, prf.max)
    
    zeros <- nrow(promisingCandidates(seed, sub)[which(promisingCandidates(seed, sub)$min == 0),])
    cutoff <- cutoff.origonal + zeros
    
    aC <- sub
    pCMin <- promisingCandidates(seed, sub)[1:cutoff,]
    vPCPR <- pCMin[which(pCMin$min == 0), ]
    pCMin <- pCMin[which(pCMin$min != 0), ]
    pCMean <- promisingCandidatesMean(seed, sub)[1:cutoff,]
    
    color_group <- c("black","red","blue", "blue")
    
    p <- ggplot() +
      geom_point(aes(x= aC$rf, y= aC$prf, size = aC$count, colour = "all")) +
      geom_point(aes(x= pCMean$rf, y= pCMean$prf, size = pCMean$mean, colour = "mean")) +
      xlab("rf") + 
      ylab("prf") +
      ggtitle(paste("Seed size:", seed)) +
      scale_colour_manual(values=color_group) + scale_y_continuous(breaks = seq(0,1 ,0.1)) + scale_x_continuous(breaks = seq(0,1,0.1))
    
    if(nrow(pCMin) != 0)
      p <- p + geom_label(aes(x= pCMin$rf, y= pCMin$prf, label = pCMin$min, colour = "min", size = 8, alpha = 0.99))
    
    if(nrow(vPCPR) != 0)
      p <- p + geom_label(aes(x= vPCPR$rf, y= vPCPR$prf, label = paste0(vPCPR$pass.rate*100,"%"), size = 4, colour = "pass rate", alpha = 0.99))
    
    
    print(p)
  }
}

source("paper/code/FileFunctions.R")
source("paper/code/rfs-discovery/explorationPlots.R")
runs.fs <- filesToDF("/cs/tmp/tsd4/results/fx-1/fx-1-results-summary.csv", "/cs/tmp/tsd4/results/fx-2/fx-2-results-summary.csv", "/cs/tmp/tsd4/results/mani-2/mani-2-results-summary.csv", onlyGetStatErrors = FALSE)
runs.r <- filesToDF("/cs/tmp/tsd4/results/fx-r/fx-r-results-summary.csv", "/cs/tmp/tsd4/results/mani-r/mani-r-results-summary.csv", onlyGetStatErrors = FALSE)


runs.c <- filesToDF("/cs/tmp/tsd4/results/fx-1/fx-1-results-summary.csv", "/cs/tmp/tsd4/results/fx-2/fx-2-results-summary.csv", "/cs/tmp/tsd4/results/mani-2/mani-2-results-summary.csv", "/cs/tmp/tsd4/results/fx-r/fx-r-results-summary.csv", "/cs/tmp/tsd4/results/mani-r/mani-r-results-summary.csv", onlyGetStatErrors = FALSE)
runs.det <- filesToDF("/cs/tmp/tsd4/results/fx-det-check/fx-det-check-results-summary.csv", onlyGetStatErrors = FALSE)

checkPlots(runs.c)

avgRunTimesBySeed(runs.c)

getBestNRuns(runs.c, 1, 250000)

promisingCandidatesBySeed(runs.det, 5)
promisingCandidatesPlot(runs.fs, 10)

promisingCandidatesBySeed(runs.c, 5)

promisingCandidatesPlot(runs.fs, 10, rf.min = 0.95, rf.max = 1.0, prf.min = 0.95, prf.max = 1.0, seeds = 62500)

promisingCandidates(15625, dfToSummaryDF(runs.fs))[1:10,]

fs3Dbf(15625, dfToSummaryDF(runs.fs)[which(dfToSummaryDF(runs.fs)$min < 10),], type="scatter3d")
fs3Dbf(31250, dfToSummaryDF(runs.fs))

fs3Dbf(31250, promisingCandidates(31250, dfToSummaryDF(runs.fs))[1:10,], type="scatter3d")
fs3Dbf(15625, promisingCandidatesMean(31250, dfToSummaryDF(runs.fs))[1:10,], type="scatter3d")

temp <- runs.fs[which(runs.fs$Seed.Pop.Size == 250000),]
plot(temp$Peak.Memory.Usage..MB.)

labelPlot2D(15625, dfToSummaryDF(runs.fs))
plot3D(15625, dfToSummaryDF(runs.fs))

summaryDfToFinalDF(dfToSummaryDF(runs.fs))

labelPlot2D(15625, summary, rf.min = 0.3390, rf.max = 0.3396, prf.max = 0.005, detail = TRUE)

plot3D(15625, summary, rf.min = 0.3390, rf.max = 0.3396, prf.max = 0.005)