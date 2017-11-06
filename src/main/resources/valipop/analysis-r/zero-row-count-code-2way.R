path = "/Users/tsd4/OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/stats-ceil/20170921-233443:913/tables/death-CT-zav-1.csv"

path = "/Users/tsd4/OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/stats-ct-adjust-working/20170921-160630:348/tables/death-CT-zav-0.csv"
data = read.csv(path, sep = ',', header = T)

data <- data[which(data$Date >= 1855), ]
data <- data[which(data$Date < 2014), ]

data.sim = data[which(data$Source == "SIM"),]
data.stat = data[which(data$Source == "STAT"),]
summary(data.sim)
summary(data.stat)

model = loglm(freq ~ Date + Age + Sex + Died + Date:Died + Age:Died + Sex:Died + Date:Age:Died + Date:Sex:Died + Age:Sex:Died, data = data)
model

apply(data.stat, 1, f1, output = "tempCounts-b-2.csv")

apply(data.sim, 1, f2, output = "tempCounts-b-op2.csv")

head(data)

f1 <- function(x, output) {
  
  d = data.sim[which(data.sim$Sex == x[2] & data.sim$Date == x[5] & data.sim$Died == x[4] & data.sim$Age == gsub("\\s", "", x[3])  ), ]
  if(nrow(d) == 0) {
    cat(paste("DEx,", x[2], ",", x[3], ",", x[4], ",", gsub("\\s", "", x[6]), ",0,", x[5], "\n"), file=output, append=TRUE)
  } 
  else {
    if(x[6] != d$freq) {
      cat(paste("NEq,", x[2], ",", x[3], ",", x[4], ",", gsub("\\s", "", x[6]), ",", d$freq, ",", x[5], "\n"), file=output, append=TRUE)
      
    } else {
      cat(paste("Eq,", x[2], ",", x[3], ",", x[4], ",", gsub("\\s", "", x[6]), ",", d$freq, ",", x[5],  "\n"), file=output, append=TRUE)
    }
  }
}


f2 <- function(x, output) {
  
  d = data.stat[which(data.stat$Sex == x[2] & data.stat$Date == x[5] & data.stat$Died == x[4] & data.stat$Age == gsub("\\s", "", x[3])  ), ]
  if(nrow(d) == 0) {
    cat(paste("DEx,", x[2], ",", x[3], ",", x[4], ",", gsub("\\s", "", x[6]), ",0,", x[5], "\n"), file=output, append=TRUE)
  } 
  else {
    if(x[6] != d$freq) {
      cat(paste("NEq,", x[2], ",", x[3], ",", x[4], ",", gsub("\\s", "", x[6]), ",", d$freq, ",", x[5], "\n"), file=output, append=TRUE)
      
    } else {
      cat(paste("Eq,", x[2], ",", x[3], ",", x[4], ",", gsub("\\s", "", x[6]), ",", d$freq, ",", x[5],  "\n"), file=output, append=TRUE)
    }
  }
}

