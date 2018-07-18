calcP <- function(x) {
  
  # taken from MASS library - as used to calculate P values in summary(loglm)
  if(x$df > 0L) {
    return(1 - pchisq(x$lrt, x$df) )
  } else { 
    return(1)
  }
}

deathAnalysis <- function(file) {
  # Read in the data
  #file <- "/Users/tsd4/OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/scot-sun-b/20170806-133506:129/tables/death-CT.csv"
  data = read.csv(file, sep = ',', header = T)
  
  # Standardise the data
  data$freq <- round(data$freq)
  data <- data[which(data$freq != 0), ]
  data <- data[which(data$Date >= 1855) , ]
  data <- data[which(data$Date < 2015) , ]
  
  # Analysis
  library("MASS")
  model = loglm(freq ~ Source + Date + Age + Sex + Died + Date:Died + Age:Died + Sex:Died + Date:Age:Died + Date:Sex:Died + Age:Sex:Died, data = data)
  
  p <- calcP(model)
  
  
  if(p > 0.75) {
    return(c(p, model$lrt, model$df))
    
  }
  
  model = loglm(freq ~ Date * Sex * Age * Died, data = data)
  
  p2 <- calcP(model)
  

  
  if(p2 > 0.75) {
    return(c(p2, model$lrt, model$df))
  }
  
  p3 <- 0
  tryCatch(
    {
      model = step(model, direction = "both")
      p3 <- calcP(model)
      
      if(p3 > 0.75) {
        return(c(p3, model$lrt, model$df))
      }
      
    } , error = function(err) {
      return(-2)
    }
  )
  
  model = loglm(freq ~ Source * Date * Sex * Age * Died, data = data)
  
  p4 <- calcP(model)
  
  if(p4 > 0.75) {
    return(c(max(p, p2, p3), model$lrt, model$df))
  }
  
  model = step(model, direction = "both")
  
  p5 <- calcP(model)
  
  if(p5 > 0.75) {
    return(c(max(p, p2, p3), model$lrt, model$df))
  } else {
    return(-1)
  }
}

obAnalysis <- function(file, largestBirthLabel) {
  #file <- "OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/scot-sun-c/20170806-145607:935/tables/ob-CT.csv"
  data = read.csv(file, sep = ',', header = T)
  
  # Standardise the data
  data$freq <- round(data$freq)
  data <- data[which(data$freq != 0), ]
  data <- data[which(data$Date >= 1855) , ]
  data <- data[which(data$Date < 2015) , ]
  data <- data[which(data$Age != "0to14"), ]
  data <- data[which(data$Age != largestBirthLabel), ]
  #data <- data[which(data$CIY == "YES"), ]
  
  # Analysis
  library("MASS")
  
  model = loglm(freq ~ Age * NPCIAP * CIY * Date, data = data)
  
  p2 <- calcP(model)
  
  model
  
  if(p2 > 0.75) {
    return(c(p2, model$lrt, model$df))
  }
  
  p3 <- 0
  tryCatch(
    {
      model = step(model, direction = "both")
      p3 <- calcP(model)
      
      if(p3 > 0.75) {
        return(c(p3, model$lrt, model$df))
      }
      
    } , error = function(err) {
      return(-2)
    }
  )
  
  model = loglm(freq ~ Source * Age * NPCIAP * CIY * Date, data = data)
  
  p4 <- calcP(model)
  
  if(p4 > 0.75) {
    return(c(max(p2, p3), model$lrt, model$df))
  }
  
  model = step(model, direction = "both")
  
  p5 <- calcP(model)
  
  if(p5 > 0.75) {
    return(c(max(p2, p3), model$lrt, model$df))
  } else {
    return(-1)
  }
}

mbAnalysis <- function(file, largestBirthLabel) {
  data = read.csv(file, sep = ',', header = T)
  
  summary(data[which(data$Source == "SIM"),])
  summary(data[which(data$Source == "STAT"),])
  
  # Standardise the data
  data$freq <- round(data$freq)
  data <- data[which(data$freq != 0), ]
  data <- data[which(data$Date >= 1855) , ]
  data <- data[which(data$Date < 2015) , ]
  data <- data[which(data$Age != "0to14"), ]
  data <- data[which(data$Age != largestBirthLabel), ]
  
  # Analysis
  library("MASS")
  
  model = loglm(freq ~ Date * NCIY * Age, data = data)
  
  p2 <- calcP(model)
  
  if(p2 > 0.75) {
    return(c(p2, model$lrt, model$df))
  }
  
  p3 <- 0
  tryCatch(
    {
      model = step(model, direction = "both")
      p3 <- calcP(model)
      
      if(p3 > 0.75) {
        return(c(p3, model$lrt, model$df))
      }
      
    } , error = function(err) {
      return(-2)
    }
  )
  
  model = loglm(freq ~ Source * Date * NCIY * Age, data = data)
  
  p4 <- calcP(model)
  
  if(p4 > 0.75) {
    return(c(max(p2, p3), model$lrt, model$df))
  }
  
  model = step(model, direction = "both")
  
  p5 <- calcP(model)
  
  if(p5 > 0.75) {
    return(c(max(p2, p3), model$lrt, model$df))
  } else {
    return(-1)
  }
}

partAnalysis <- function(file) {
  
  #file <- "OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/scoty/20170805-182618:884/tables/part-CT.csv"
  data = read.csv(file, sep = ',', header = T)
  
  # Standardise the data
  data$freq <- round(data$freq)
  data <- data[which(data$freq != 0), ]
  data <- data[which(data$Date >= 1855) , ]
  data <- data[which(data$Date < 2015) , ]
  data <- data[which(data$NPA != "na") , ]
  
  # Analysis
  library("MASS")
  
  model = loglm(freq ~ Date * NPA * Age, data = data)
  
  p2 <- calcP(model)
  
  if(p2 > 0.75) {
    return(c(p2, model$lrt, model$df))
  }
  
  p3 <- 0
  tryCatch(
    {
      model = step(model, direction = "both")
      p3 <- calcP(model)
      
      if(p3 > 0.75) {
        return(c(p3, model$lrt, model$df))
      }
      
    } , error = function(err) {
      return(-2)
    }
  )
  
  model = loglm(freq ~ Source * Date * NPA * Age, data = data)
  
  p4 <- calcP(model)
  
  if(p4 > 0.75) {
    return(c(max(p2, p3), model$lrt, model$df))
  }
  
  model = step(model, direction = "both")
  
  p5 <- calcP(model)
  
  if(p5 > 0.75) {
    return(c(max(p2, p3), model$lrt, model$df))
  } else {
    return(-1)
  }
  
}

sepAnalysis <- function(file) {
  
  data = read.csv(file, sep = ',', header = TRUE)
  
  # Standardise the data
  data$freq <- round(data$freq)
  data <- data[which(data$freq != 0), ]
  data <- data[which(data$Date >= 1855) , ]
  data <- data[which(data$Date < 2015) , ]
  data <- data[which(data$Separated != "NA") , ]
  
  # Analysis
  library("MASS")
  model = loglm(freq ~ Date + NCIP + Separated, data = data)  
  
  p <- calcP(model)
  
  if(p > 0.75) {
    return(c(p, model$lrt, model$df))
  }
  
  model = loglm(freq ~ Date * NCIP * Separated, data = data)
  
  p2 <- calcP(model)
  
  if(p2 > 0.75) {
    return(c(p2, model$lrt, model$df))
  }
  
  p3 <- 0
  tryCatch(
    {
      model = step(model, direction = "both")
      p3 <- calcP(model)
      
      if(p3 > 0.75) {
        return(c(p3, model$lrt, model$df))
      }
      
    } , error = function(err) {
      return(-2)
    }
  )
  
  model = loglm(freq ~ Source * Date * NCIP * Separated, data = data)
  
  p4 <- calcP(model)
  
  if(p4 > 0.75) {
    return(c(max(p, p2, p3), model$lrt, model$df))
  }
  
  model = step(model, direction = "both")
  
  p5 <- calcP(model)
  
  if(p5 > 0.75) {
    return(c(max(p, p2, p3), model$lrt, model$df))
  } else {
    return(-1)
  }
  
}

analysis <- function(outputFile, resultsDir, runFile, largestBirthLabel) {
  
  t <- proc.time()
  
  subpath <- paste(resultsDir, "/", runFile, "/tables", sep = "")
  
  death <- deathAnalysis(paste(subpath, "/death-CT.csv", sep = ""))
  ob <- obAnalysis(paste(subpath, "/ob-CT.csv", sep = ""), largestBirthLabel)
  mb <- mbAnalysis(paste(subpath, "/mb-CT.csv", sep = ""), largestBirthLabel)
  part <- partAnalysis(paste(subpath, "/part-CT.csv", sep = ""))
  sep <- sepAnalysis(paste(subpath, "/sep-CT.csv", sep = ""))
  
  tt <- proc.time() - t
  
  passed = FALSE
  if(sum(death[1], ob[1], mb[1], part[1], sep[1]) == 5) {
    passed = TRUE
  }
  
  line = ""
  line = paste(runFile, death[1], sep = ",")
  line = paste(line, death[2], sep = ",")
  line = paste(line, death[3], sep = ",")
  line = paste(line, ob[1], sep = ",")
  line = paste(line, ob[2], sep = ",")
  line = paste(line, ob[3], sep = ",")
  line = paste(line, mb[1], sep = ",")
  line = paste(line, mb[2], sep = ",")
  line = paste(line, mb[3], sep = ",")
  line = paste(line, part[1], sep = ",")
  line = paste(line, part[2], sep = ",")
  line = paste(line, part[3], sep = ",")
  line = paste(line, sep[1], sep = ",")
  line = paste(line, sep[2], sep = ",")
  line = paste(line, sep[3], sep = ",")
  line = paste(line, passed, sep = ",")
  line = paste(line, tt[3], sep = ",")
  
  if(!file.exists(outputFile)) {
    headings = "RunFile,Death-P,Death-Dev,Death-DF,OB-P,OB-Dev,OB-DF,MB-P,MB-Dev,MB-DF,Part-P,Part-Dev,Part-DF,Sep-P,Sep-Dev,Sep-DF,Passed,Time-Taken"
    
    write(headings, file=outputFile, append=TRUE)
  }
  
  write(line, file=outputFile, append=TRUE)
  
}

outputFile <- commandArgs(TRUE)[1]
resultsDir <- commandArgs(TRUE)[2]
runFile <- commandArgs(TRUE)[3]
largestBirthLabel <- commandArgs(TRUE)[4]

analysis(outputFile, resultsDir, runFile, largestBirthLabel)
