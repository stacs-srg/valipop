makeConstantsSet <- function(setupBR, setupDR, tS) {
  constants <- data.frame(
    "setup br" = setupBR,
    "setup dr" = setupDR,
    "tS" = tS
  )
}


convertResultSummaryToJob <- function(result, priority, n, deterministic, seed, constantsSet, resultsDir, recordFormat = NA, reason = NA, reqMemory = NA) {

  job <- data.frame(
            "priority" = priority,
            "code version" = result$Code.Version,
            "reason" = 
              if(is.na(reason))
                "reason" = result$Reason
              else
                "reason" = reason,
              
            "n" = n,
            "seed size" = result$Seed.Pop.Size,
            "rf" = result$Recovery.Factor,
            "prf" = result$Proportional.Recovery.Factor,
            "iw" = result$Input.Width,
            "input dir" = result$Inputs.Directory,
            "results dir" = resultsDir,
            "required memory" = 
              if(is.na(reqMemory))
                "required memory" = ceiling(result$Peak.Memory.Usage..MB * 1.05 / 1000)
              else
                "required memory" = reqMemory,
            
            "output record format" = 
              if(is.na(recordFormat))
                "output record format" = result$Output.Record.Format
              else
                "output record format" = recordFormat,
              
            "deterministic" = deterministic,
            "seed" = 
              if(deterministic)
                "seed" = toString(seed)
              else
                "seed" = "-",
            
            "setup br" = constantsSet$setup.br,
            "setup dr" = constantsSet$setup.dr,
            "bf" = result$Birth.Factor,
            "df" = result$Death.Factor,
            "tS" = constantsSet$tS,
            "t0" = result$Start.Date,
            "tE" = result$End.Date,
            "timestep" = result$Timestep,
            "binomial sampling" = result$binomial.Sampling,
            "min birth spacing" = result$Min.Birth.Spacing,
            "min ges period" = result$Min.Birth.Spacing,
            "ct tree stepback" = result$CT.Tree.Stepback, 
            check.names=F
            )
  
  return(job)
  
}

repeatJobDeterministically <- function(result, priority, constantsSet, resultsDir, recordFormat = NA, reason = NA, reqMemory = NA) {
  job <- convertResultSummaryToJob(result, priority, 1, TRUE, result$Seed, constantsSet, resultsDir, recordFormat = recordFormat, reason = reason, reqMemory = reqMemory)
  return(job)
}

repeatJobsDeterministically <- function(results, constantsSet, resultsDir, recordFormat = NA, priority = 4, reason = NA, reqMemory = NA) {
  jobs <- data.frame()
  
  for (index in 1:nrow(results)) { 
    result = results[index, ]
    jobs <- rbind(jobs, repeatJobDeterministically(result, priority, constantsSet, resultsDir, recordFormat, reason, reqMemory))
  }
    
  return(jobs)
}

runMorePromisingJobs <- function(results, constantsSet, uptoN, resultsDir, cutoff, recordFormat = NA, priority = 4, reason = NA, reqMemory = NA) {
  
  jobs <- data.frame()
  
  for(seed in unique(results$Seed.Pop.Size)) {
      pC <- promisingCandidates(seed, dfToSummaryDF(results))
      if(nrow(pC) > cutoff)
        pC <- pC[1:cutoff,]
      
      for(i in 1:nrow(pC)) {
        row <- pC[i,]
        result <- results[which(
                        results$Proportional.Recovery.Factor == row$prf & 
                        results$Recovery.Factor == row$rf &
                        results$Seed.Pop.Size == seed)
                , ][1,]
        
        if(uptoN - row$count > 0)
          jobs <- rbind(jobs, 
                        convertResultSummaryToJob(result, priority, uptoN - row$count, 
                                                  FALSE, "-", constantsSet, resultsDir, recordFormat, reason, reqMemory))
      }
  }
  return(jobs)
}

outputJobs <- function(jobs, file) {
  write.csv(jobs, file = file, row.names=FALSE, quote=FALSE)
}

countJobsInBand <- function(jobQ, memCeiling, threshold) {
  inBand <- jobQ[which(memCeiling*threshold < jobQ$`required memory` & jobQ$`required memory` < memCeiling),]
  return(sum(inBand$n))
}

runtimeOfJobsInBand <- function(jobQ, memCeiling, threshold, avgRT) {
  inBand <- jobQ[which(memCeiling*threshold < jobQ$`required memory` & jobQ$`required memory` < memCeiling),]
  runtime <- 0
  if(nrow(inBand) != 0) {
    for(i in 1:nrow(inBand)) {
      job <- inBand[i,]
      runtime <- runtime + (job$n * avgRT[which(avgRT$seed == job$`seed size`), "avg.run.time"])
    }
  }
  
  return(runtime)
}

getJC <- function(calc, ins.mem) {
  return(calc[which(ins.mem == calc$ins.mem), ]$job.count)
}

getTRT <- function(calc, ins.mem) {
  return(calc[which(ins.mem == calc$ins.mem), ]$total.run.time)
}

addNode <- function(letter, nodes) {
  return(c(nodes,letter))
}

addProfile <- function(profile, instances) {
  return(c(instances,profile))
}

getNumInstances <- function(instances, mem) {
  return(sum(instances == mem))
}

updateParrallelRunTime <- function(calc, instances) {
  for(i in 1:nrow(calc)) {
    row <- calc[i, ]
    if(getNumInstances(instances, row$ins.mem) != 0) {
      row$parrallel.run.time <- getTRT(calc, row$ins.mem) / getNumInstances(instances, row$ins.mem)
      calc[i, ] <- row
    }
  }
  return(calc)
}

insWithHighestPRT <- function(calc, instances) {
  
  temp <- calc[which(calc$job.count > getNumInstances(calc, instances)), ]
  
  if(nrow(temp) == 0) return(NULL)
  
  return(temp[which(temp$parrallel.run.time == max(temp$parrallel.run.time)),][1,]$ins.mem)
}

calcDeployProfile <- function(jobQ, allRuns, clusterNodes) {
  
  avgRT <- data.frame("seed"= c(), "avg.run.time" = c())
  
  seeds <- unique(allRuns$Seed.Pop.Size)[order(unique(allRuns$Seed.Pop.Size))]
  for(seed in seeds) {
    sub <- allRuns[which(allRuns$Seed.Pop.Size == seed),]
    avgRT <- rbind(avgRT, data.frame("seed" = seed, "avg.run.time" = round(mean(sub$CT.Run.time + sub$Sim.Run.time + sub$Stats.Run.Time + sub$Records.Run.time))))
  }
  
  calc <- data.frame(
    "ins.mem"= c(12,8,6,4), 
    "job.count" = 
      c(countJobsInBand(jobQ, 12, 0.65),
        countJobsInBand(jobQ, 8, 0.65),
        countJobsInBand(jobQ, 6, 0.65),
        countJobsInBand(jobQ, 4, 0.65)),
    "total.run.time" = 
      c(runtimeOfJobsInBand(jobQ, 12, 0.65, avgRT),
        runtimeOfJobsInBand(jobQ, 8, 0.65, avgRT),
        runtimeOfJobsInBand(jobQ, 6, 0.65, avgRT),
        runtimeOfJobsInBand(jobQ, 4, 0.65, avgRT)), 
    "parrallel.run.time" = c(0,0,0,0))
  
  #print(calc)
  
  #for(mem in calc$ins.mem) {
  #  row <- calc[which(mem == calc$ins.mem), ]
  #  row$total.run.time <- row$job.count * avgRT[which(avgRT$)]
  #}
  
  A <- c(12)
  B <- c(8,4)
  C <- c(6,6)
  D <- c(4,4,4)

  nodes <- c()
  instances <- c()
  
  #setup
  if(getJC(calc, 12) != 0) {
    nodes <- addNode("A", nodes)
    instances <- addProfile(A, instances)
  }
  
  if(getJC(calc, 8) != 0) {
    nodes <- addNode("B", nodes)
    instances <- addProfile(B, instances)
  }
  
  if(getJC(calc, 6) != 0) {
    nodes <- addNode("C", nodes)
    instances <- addProfile(C, instances)
  }
  
  if(getJC(calc, 4) != 0) {
    nodes <- addNode("D", nodes)
    instances <- addProfile(D, instances)
  }
  
  while(length(nodes) < clusterNodes) {
    
    calc <- updateParrallelRunTime(calc, instances)
    
    #print(calc)
    
    insToInc <- insWithHighestPRT(calc, instances)
    
    if(is.null(insToInc)) break
    
    if(insToInc == 12) {
      nodes <- addNode("A", nodes)
      instances <- addProfile(A, instances)
    } else if(insToInc == 8) {
      nodes <- addNode("B", nodes)
      instances <- addProfile(B, instances)
    } else if(insToInc == 6) {
      nodes <- addNode("C", nodes)
      instances <- addProfile(C, instances)
    } else if(insToInc == 4) {
      nodes <- addNode("D", nodes)
      instances <- addProfile(D, instances)
    }
    
  }
  print(calc)
  print(table(nodes))
  print(max(calc$parrallel.run.time/(60*60)))
  
  return(calc)
  
}

searchNearPromisingJobs <- function(results, constantsSet, uptoN, resultsDir, cutoff, minStep, explorationStep, priority = 4, recordFormat = NA, reason = NA, reqMemory = NA) {
  
  jobs <- data.frame()
  
  for(seed in unique(results$Seed.Pop.Size)) {
    pC <- promisingCandidates(seed, dfToSummaryDF(results))
    #pCCutoff <- pC[which(pC$count < uptoN),]
    
    #if(nrow(pCCutoff) > cutoff) {
    #  pCCutoff <- pCCutoff[1:cutoff,]
    #}
    
    expandedOrigins <- 0
    
    for(i in 1:nrow(pC)) {
      row <- pC[i,]
      
      jobProforma <- results[which(
        results$Proportional.Recovery.Factor == row$prf & 
          results$Recovery.Factor == row$rf &
          results$Seed.Pop.Size == seed)
        , ][1,]
      
      o <- data.frame("x" = row$rf, "y" = row$prf)
      
      tb <- data.frame("x" = pC$rf, 
                       "y" = pC$prf,
                       "dy" = rep(0,nrow(pC)),
                       "dx" = rep(0,nrow(pC)),
                       "h" = rep(0,nrow(pC)),
                       "q" = rep(0,nrow(pC)))
      
      tb$dy <- tb$y-o$y
      tb$dx <- tb$x-o$x
      
      tb$h <- sqrt((tb$dx)^2+(tb$dy)^2) #add it origin location
      
      rs <- which(tb$dy >= 0 & tb$dx > 0 & tb$x != o$x & tanh((tb$y-o$y)/(tb$x-o$x)) >= 0)
      if(length(rs) > 0) tb[rs,]$q <- 2
      
      rs <- which(tb$dy >= 0 & tb$dx > 0 & tb$x != o$x & tanh((tb$y-o$y)/(tb$x-o$x)) >= pi/4)
      if(length(rs) > 0) tb[rs, ]$q <- 1
      
      rs <- which(tb$dy < 0 & tb$dx >= 0 & tb$y != o$y & tanh((tb$x-o$x)/(o$y-tb$y)) >= 0)
      if(length(rs) > 0) tb[rs,]$q <- 4
      
      rs <- which(tb$dy < 0 & tb$dx >= 0 & tb$y != o$y & tanh((tb$x-o$x)/(o$y-tb$y)) >= pi/4)
      if(length(rs) > 0) tb[rs,]$q <- 3
      
      rs <- which(tb$dy <= 0 & tb$dx < 0 & tb$x != o$x & tanh((o$y-tb$y)/(o$x-tb$x)) >= 0)
      if(length(rs) > 0) tb[rs,]$q <- 6
      
      rs <- which(tb$dy <= 0 & tb$dx < 0 & tb$x != o$x & tanh((o$y-tb$y)/(o$x-tb$x)) >= pi/4)
      if(length(rs) > 0) tb[rs,]$q <- 5
      
      rs <- which(tb$dy > 0 & tb$dx <= 0 & tb$y != o$y & tanh((o$x-tb$x)/(tb$y-o$y)) >= 0)
      if(length(rs) > 0) tb[rs,]$q <- 8
      
      rs <- which(tb$dy > 0 & tb$dx <= 0 & tb$y != o$y & tanh((o$x-tb$x)/(tb$y-o$y)) >= pi/4)
      if(length(rs) > 0) tb[rs,]$q <- 7
      
      tb
      
      n <- 2
      
      dxL <- c(1,1,1,1,-1,-1,-1,-1)
      dyL <- c(1,1,-1,-1,-1,-1,1,1)
      
      count <- 0
      
      if(uptoN - row$count > 0) #bring the origin uptoN runs
        jobs <- rbind(jobs, 
                      convertResultSummaryToJob(jobProforma, priority, uptoN - row$count, 
                                                FALSE, "-", constantsSet, resultsDir, recordFormat, reason, reqMemory))
      for(q in 1:8) {
        sub <- tb[which(tb$q == q),]
        if(nrow(sub) > 0) {
          sub <- sub[order(sub$h),][1,]
          
          if(sub$h/n >= minStep) {
            for(s in seq(1,n-1,1)) {
              cy <- signif((s*sub$h)/(n*sqrt(1+(sub$dx/sub$dy)^2)), digits = 2)
              cx <- signif((s*sub$h)/(n*sqrt(1+(sub$dy/sub$dx)^2)), digits = 2)
              
              ny <- o$y + dyL[q] * cy
              nx <- o$x + dxL[q] * cx
              
              print(paste(nx,ny))
              # add job
              
              newJob <- jobProforma
              newJob$rf <- nx
              newJob$prf <- ny
              
              jobs <- rbind(jobs, 
                            convertResultSummaryToJob(newJob, priority, uptoN, 
                                                      FALSE, "-", constantsSet, resultsDir, recordFormat, reason, reqMemory))
            }
          } else {
            count <- count + 1
          }
          
        } else {
          # do something with empty quadrant if not up against edge
          hToRFBound <- (abs(round(o$x)-o$x))/(sin((pi*(2*q-1)/16)))
          hToPRFBound <- (abs(round(o$y)-o$y))/(cos((pi*(2*q-1)/16)))
          
          minBound <- min(hToPRFBound, hToRFBound)
          
          if(minBound > minStep) {
            # add as job at checked exploration step
            
            chosenStep <- explorationStep
            
            if(explorationStep > minBound)
              chosenStep <- minBound
            
            cy <- signif(chosenStep*cos(pi*(4*q-1)/16), digits = 2)
            cx <- signif(chosenStep*sin(pi*(4*q-1)/16), digits = 2)
            
            ny <- o$y + cy
            nx <- o$x + cx
            
            newJob <- jobProforma
            newJob$rf <- nx
            newJob$prf <- ny
            
            jobs <- rbind(jobs, 
                          convertResultSummaryToJob(newJob, priority, uptoN, 
                                                    FALSE, "-", constantsSet, resultsDir, recordFormat, reason, reqMemory))
            
          } else {
            count <- count + 1
          }
        }
      }
      
      if(count != 8) {
        # if 8 all options are within minStep - so look for another option
        expandedOrigins <- expandedOrigins + 1
        
        if(expandedOrigins >= cutoff)
          break
        
      }
      
    }
  }
  return(jobs)
}


scotTestCS <- makeConstantsSet(0.0233,0.0322,"1687-01-01")
clustersResultDir <- "/cs/tmp/tsd4/results/"
maniResultDir <- "/home/tsd4/results/"

passingRuns <- runs.c[which(runs.c$Seed.Pop.Size < 100000 & runs.c$v.M == 0), ]

passingRuns <- getBestNRuns(runs.c, 1, 250000)

jobQ <- repeatJobsDeterministically(passingRuns, scotTestCS, maniResultDir, recordFormat = "TD", reason = "mani-r")

promisingJobExtraRuns <- runMorePromisingJobs(runs.c[which(runs.c$Seed.Pop.Size < 100000),], scotTestCS, 25, clustersResultDir, 20, reason = "fx-2")
calc <- calcDeployProfile(promisingJobExtraRuns, runs.c, 22)

#jobQ <- rbind(jobQ, )

outputJobs(jobQ, "~/Desktop/gen-mani-job-q.csv")


pcs <- promisingCandidates(15625, dfToSummaryDF(runs.fs))[1:10,]


    

o <- data.frame("x" = 0.5, "y" = 0.5)


for(q in 1:8)
  print(signif(chosenStep*cos(pi*(2*q-1)/16), digits = 2))


temp <- data.frame("q" = seq(1,8,1), 
                   "x" = rep(0,8),
                   "y" = rep(0,8))

temp$y <- signif(chosenStep*cos(pi*(4*temp$q-1)/16), digits = 4)
temp$x <- signif(chosenStep*sin(pi*(4*temp$q-1)/16), digits = 4)

library(ggplot2)
ggplot(temp) +
  geom_text(aes(x,y, label = q))


results <- runs.c[which(runs.c$Seed.Pop.Size == 62500), ]
search <- searchNearPromisingJobs(results, scotTestCS, 10, clustersResultDir, 100, 0.01, 0.2)
calc <- calcDeployProfile(search, runs.c, 22)
outputJobs(search, "~/Desktop/gen-job-q.csv")


table(search$seed.size)

ggplot(search) +
  geom_point(aes(rf, prf)) 
#+
  facet_wrap(~search$seed.size)
