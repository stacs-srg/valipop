calcP <- function(x) {
  # taken from MASS library - as used to calculate P values in summary(loglm)
  if(x$df > 0L) {
    return(1 - pchisq(x$lrt, x$df) )
  } else { 
    return(1)
  }
}

# Read in the data
file <- "/Users/tsd4/OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/ExpTesting/20170802-084303:465/tables/ob-CT.csv"
file <- commandArgs(TRUE)[1]

data = read.csv(file, sep = ',', header = T)

# Standardise the data
data$freq <- round(data$freq)
data <- data[which(data$freq != 0), ]
data <- data[which(data$Date >= 1855) , ]
data <- data[which(data$Date <= 2015) , ]
data <- data[which(data$Age != "0to14"), ]
data <- data[which(data$Age != "50+"), ]

# Analysis
library("MASS")
#model = loglm(freq ~ Date + Age + Sex + Died + Date:Died + Age:Died + Sex:Died + Date:Age:Died + Date:Sex:Died + Age:Sex:Died, data = data)

p <- calcP(model)

if(p > 0.75) {
  return(p)
}

model = loglm(freq ~ Age * NPCIAP * CIY * Date, data = data)

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


