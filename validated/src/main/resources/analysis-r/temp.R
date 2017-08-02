file <- "/Users/tsd4/OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/ExpTesting/20170802-084303:465/tables/full-CT.csv"
data = read.csv(file, sep = ',', header = T)

# Standardise the data
data$freq <- round(data$freq)
data <- data[which(data$freq != 0), ]
data <- data[which(data$Date >= 1855) , ]
data <- data[which(data$Date <= 2014) , ]
data <- data[which(data$Separated != "NA") , ]
data <- data[which(data$Separated == "YES") , ]
data <- data[which(data$CIY == "YES") , ]


par(mfrow=c(1,1))

data.sim <- data[which(data$Source == "SIM"),]
data.stat <- data[which(data$Source == "STAT"),]

maxY <- max(data.sim$freq, data.stat$freq) * 1.05

plot(data.stat$Date, data.stat$freq, col = 2, ylim = c(0, maxY))
points(data.sim$Date, data.sim$freq, col = 3)


plot(data.stat$Separated, data.stat$freq, col = 2)
points(data.sim$Separated, data.sim$freq, col = 3)

summary(data)

summary(data[which(data$Source == "SIM"),])
summary(data[which(data$Source == "STAT"),])

data.sim[which(data.sim$freq > 3900) , ]



# Analysis
library("MASS")
#model = loglm(freq ~ Date + Age + Sex + Died + Date:Died + Age:Died + Sex:Died + Date:Age:Died + Date:Sex:Died + Age:Sex:Died, data = data)

p <- calcP(model)

if(p > 0.75) {
  return(p)
}

model = loglm(freq ~ Source + Date * NCIP * Separated, data = data)

p2 <- calcP(model)


file <- "/Users/tsd4/OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/ExpTesting/20170802-084303:465/tables/ob-CT.csv"
file <- commandArgs(TRUE)[1]

data = read.csv(file, sep = ',', header = T)

data$freq <- round(data$freq)
data <- data[which(data$freq != 0), ]
data <- data[which(data$Date >= 1855) , ]
data <- data[which(data$Date <= 2015) , ]
data <- data[which(data$Age != "0to14"), ]
data <- data[which(data$Age != "50+"), ]
unique(data$Age)
