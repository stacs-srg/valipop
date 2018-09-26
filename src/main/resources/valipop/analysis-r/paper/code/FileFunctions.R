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
  df <- read.table(path, sep = ",", header = TRUE)
  df <- addMissingColumns(df)
  
  files <- list(...)
  
  for(f in files) {
    temp <- read.table(f, sep = ",", header = TRUE)
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
  df$Start.Time.Date <- sapply(strsplit(as.character(df$Start.Time),":"), '[', 1)
  
  df$Start.Time.Date <- paste(substr(df$Start.Time.Date, 1,4), substr(df$Start.Time.Date, 5,15), sep = "/")
  df$Start.Time.Date <- paste(substr(df$Start.Time.Date, 1,7), substr(df$Start.Time.Date, 8,16), sep = "/")
  df$Start.Time.Date <- paste(substr(df$Start.Time.Date, 1,10), substr(df$Start.Time.Date, 12,17), sep = " ")
  df$Start.Time.Date <- paste(substr(df$Start.Time.Date, 1,13), substr(df$Start.Time.Date, 14,17), sep = ":")
  df$Start.Time.Date <- paste(substr(df$Start.Time.Date, 1,16), substr(df$Start.Time.Date, 17,18), sep = ":")
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
        mean <- mean(t.p.r$v.M)
        median <- median(t.p.r$v.M)
        min <- min(t.p.r$v.M)
        pass.rate <- round(length(which(t.p.r$v.M == 0)) / length(which(t.p.r$v.M >= 0)), digits = 3)
        max <- max(t.p.r$v.M)
        count <- length(which(t.p.r$v.M >= 0))
        df[nrow(df)+1,] <- c(s, p, r, mean, median, min, pass.rate, max, count)
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
    
    r <- t.s[which.max(t.s$pass.rate),]
    finalDF[nrow(finalDF)+1,] <- c(r$seed, r$prf, r$rf, r$mean, r$median, r$min, r$pass.rate, r$max, r$count)
  }
  
  return(finalDF)
  
}

fileToSummaryDF <- function(path, filter = NA) {
  fileDF <- filesToDF(path)
  return(dfToSummaryDF(fileDF, filter))
}

selectFromFullDF <- function(fullDF, selected) {
  
  chosenDF <- data.frame(matrix(ncol = 32, nrow = 0))
  colnames(chosenDF) <- colnames(fullDF)
  
  for(r in 1:nrow(selected)) {
    row <- selected[r, ]
    chosenDF <- rbind(chosenDF, fullDF[which(fullDF$Seed.Pop.Size == row$seed & fullDF$Recovery.Factor == row$rf & fullDF$Proportional.Recovery.Factor == row$prf ), ])
  }
  
  return(chosenDF)
}
