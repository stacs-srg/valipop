addMissingColumns <- function(df) {
  
  if(! "Eligibility.Checks" %in% colnames(df)) {
    df[, "Eligibility.Checks"] <- NA
  }
  
  if(! "Failed.Eligibility.Checks" %in% colnames(df)) {
    df[, "Failed.Eligibility.Checks"] <- NA
  }
  
  return(df)
}

filesToDF <- function(path, ..., onlyGetStatErrors = FALSE) {
  df <- read.table(path, sep = ",", header = TRUE, encoding = "utf8")
  df <- addMissingColumns(df)
  
  files <- list(...)
  
  for(f in files) {
    temp <- read.table(f, sep = ",", header = TRUE, encoding = "utf8")
    temp <- addMissingColumns(temp)
    
    df <- rbind(df, temp)
  }
  
  if(onlyGetStatErrors) {
    df.t <- df[which(df$Stats.Run.Time < 10) ,]
    df <- rbind(df.t, df[which(df$v.M == 99999 & df$Stats.Run.Time >= 10) ,])
  } else {
    print(paste("Runs removed due to stats error (stats run time < 10s):", length(which(df$Stats.Run.Time < 10))))
    df <- df[which(df$Stats.Run.Time >= 10),]
    
    print(paste("Runs removed due to stats error (v.M == 99999):", length(which(df$v.M == 99999))))
    df <- df[which(df$v.M != 99999),]
  }
  
  df[, "Start.Time.Date"] <- NA
  df$Start.Time.Date <- df$Start.Time
  
  df$Start.Time.Date <- paste(substr(df$Start.Time.Date, 1,4), substr(df$Start.Time.Date, 6,19), sep = "/")
  df$Start.Time.Date <- paste(substr(df$Start.Time.Date, 1,7), substr(df$Start.Time.Date, 9,19), sep = "/")
  df$Start.Time.Date <- paste(substr(df$Start.Time.Date, 1,10), substr(df$Start.Time.Date, 12,19), sep = " ")
  
  Sys.setenv(TZ="Europe/London")
  df$Start.Time.Date <- strftime(df$Start.Time.Date, format = "%Y/%m/%d %H:%M:%S")
  
  return(df)
}

dfToSummaryDF <- function(inDF, seed = NA) {
  t <- inDF
  
  if(!is.na(seed)) {
    t <- t[which(t$Seed.Pop.Size == seed),]
  }
  
  df <- data.frame(seed=integer(),
                   prf=integer(),
                   rf=integer(),
                   bf=integer(),
                   df=integer(),
                   mean=double(),
                   median=double(),
                   min=double(),
                   pass.rate=double(),
                   max=double(),
                   count=integer(),
                   stringsAsFactors=FALSE)
  
  for(s in unique(t$Seed.Pop.Size)) {
    t.s <- t[which(t$Seed.Pop.Size == s), ]
    for(p in unique(t.s$Proportional.Recovery.Factor)) {
      t.p <- t.s[which(t.s$Proportional.Recovery.Factor == p), ]
      for(r in unique(t.p$Recovery.Factor)) {
        t.p.r <- t.p[which(t.p$Recovery.Factor == r), ]
        for(bf in unique(t.p.r$Birth.Factor)) {
          t.p.r.bf <- t.p.r[which(t.p.r$Birth.Factor == bf), ]
          for(dF in unique(t.p.r.bf$Death.Factor)) {
            t.p.r.bf.df <- t.p.r.bf[which(t.p.r.bf$Death.Factor == dF), ]
      
              mean <- mean(t.p.r.bf.df$v.M)
              median <- median(t.p.r.bf.df$v.M)
              min <- min(t.p.r.bf.df$v.M)
              pass.rate <- round(length(which(t.p.r.bf.df$v.M == 0)) / length(which(t.p.r.bf.df$v.M >= 0)), digits = 3)
              max <- max(t.p.r.bf.df$v.M)
              count <- length(which(t.p.r.bf.df$v.M >= 0))
              df[nrow(df)+1,] <- c(s, p, r, bf, dF, mean, median, min, pass.rate, max, count)
          }
        }
      }
    }
  }
  
  return(df)
}

summaryDfToFinalDF <- function(summaryDF, minN = NA) {
  
  t <- summaryDF
  
  finalDF <- data.frame(seed=integer(),
                   prf=integer(),
                   rf=integer(),
                   bf=integer(),
                   df=integer(),
                   mean=double(),
                   median=double(),
                   min=double(),
                   pass.rate=double(),
                   max=double(),
                   count=integer(),
                   stringsAsFactors=FALSE)
  
  for(s in unique(t$seed)) {
    t.s <- t[which(t$seed == s), ]
    
    if(!is.na(minN)) {
      t.s <- t.s[which(t.s$count >= minN), ]
    }
    
    if(max(t.s$pass.rate) == 0) {
      r <- t.s[which.min(t.s$min),]
    } else {
      r <- t.s[which.max(t.s$pass.rate),]
    }
    finalDF[nrow(finalDF)+1,] <- c(r$seed, r$prf, r$rf, r$bf, r$df, r$mean, r$median, r$min, r$pass.rate, r$max, r$count)
  }
  
  return(finalDF)
  
}

fileToSummaryDF <- function(path, filter = NA) {
  fileDF <- filesToDF(path)
  return(dfToSummaryDF(fileDF, filter))
}

selectFromFullDF <- function(fullDF, selected) {
  
  chosenDF <- data.frame(matrix(ncol = 37, nrow = 0))
  colnames(chosenDF) <- colnames(fullDF)
  
  for(r in 1:nrow(selected)) {
    row <- selected[r, ]
    chosenDF <- rbind(chosenDF, fullDF[which(fullDF$Seed.Pop.Size == row$seed & fullDF$Recovery.Factor == row$rf & fullDF$Proportional.Recovery.Factor == row$prf ), ])
  }
  
  return(chosenDF)
}
