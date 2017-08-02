calcP <- function(x) {
  # taken from MASS library - as used to calculate P values in summary(loglm)
  if(x$df > 0L) {
    return(1 - pchisq(x$lrt, x$df) )
  } else { 
    return(1)
  }
}

# Read in the data
file <- "/Users/tsd4/OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/ExpTesting/20170802-084303:465/tables/mb-CT.csv"
file <- commandArgs(TRUE)[1]

data = read.csv(file, sep = ',', header = T)

# Standardise the data
data$freq <- round(data$freq)
data <- data[which(data$freq != 0), ]
data <- data[which(data$Date >= 1855) , ]
data <- data[which(data$Date <= 2015) , ]

# Analysis
library("MASS")
#model = loglm(freq ~ Date * NCIY * Age, data = data)

p <- calcP(model)

model

if(p > 0.75) {
  return(p)
}

model = loglm(freq ~ Date * NCIY * Age, data = data)

p2 <- calcP(model)

if(p2 > 0.75) {
  return(p2)
}

model = step(model, direction = "both")

p3 <- calcP(model)

if(p3 > 0.75) {
  return(p3)
}

model = loglm(freq ~ Source * Date * Sex * Age * Died, data = data)
model = step(model, direction = "both")

p4 <- calcP(model)

if(p4 > 0.75) {
  return(max(p1, p2, p3))
} else {
  return(-1)
}

