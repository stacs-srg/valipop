source("paper/code/FileFunctions.R")

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
      p <- p + geom_label(aes(x= pCMin$rf, y= pCMin$prf, label = pCMin$min, colour = "min", size = 16, alpha = 0.99))
    
    if(nrow(vPCPR) != 0)
      p <- p + geom_label(aes(x= vPCPR$rf, y= vPCPR$prf, label = paste0(vPCPR$pass.rate*100,"%"), size = 16, colour = "pass rate", alpha = 0.99))
    
    
    print(p)
  }
}