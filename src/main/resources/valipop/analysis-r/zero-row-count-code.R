#This script is to look into the tables we are producing and if they match up to our assumptions

full.path = "OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/full-table-runs/20170919-114441:059/tables/full-CT.csv"
full.data = read.csv(path, sep = ',', header = T)
Death.data = read.csv(path, sep = ',', header = T)

Death.data$freq <- round(Death.data$freq)
Death.data <- Death.data[which(Death.data$freq != 0), ]
Death.data <- Death.data[which(Death.data$Date >= 1855), ]
Death.data <- Death.data[which(Death.data$Date < 2014), ]

full.data$freq <- round(full.data$freq)
full.data <- full.data[which(full.data$freq != 0), ]
full.data <- full.data[which(full.data$Date >= 1855), ]
full.data <- full.data[which(full.data$Date < 2014), ]

library("MASS")
model = loglm(freq ~ Date + Age + Sex + Died + Date:Died + Age:Died + Sex:Died + Date:Age:Died + Date:Sex:Died + Age:Sex:Died, data = full.data)
model

Death.data.sim = Death.data[which(Death.data$Source == "SIM"),]
Death.data.stat = Death.data[which(Death.data$Source == "STAT"),]

apply(Death.data.stat, 1, f1, output = "tempCounts2.csv")

apply(Death.data.sim, 1, f2, output = "tempCounts2.csv")


f1 <- function(x, output) {
  
  d = Death.data.sim[which(Death.data.sim$Sex == x[2] & Death.data.sim$Date == x[5] & Death.data.sim$Died == x[4] & Death.data.sim$Age == gsub("\\s", "", x[3])  ), ]
  if(nrow(d) == 0) {
    cat(paste("DEx,", gsub("\\s", "", x[6]), ",0,", x[5], "\n"), file=output, append=TRUE)
  } 
  else {
    #print("Row exists ( sim | stat ) freq")
    if(x[6] != d$freq) {
      cat(paste("NEq,", gsub("\\s", "", x[6]), ",", d$freq, ",", x[5], "\n"), file=output, append=TRUE)
      
    } else {
      cat(paste("Eq,", gsub("\\s", "", x[6]), ",", d$freq, ",", x[5],  "\n"), file=output, append=TRUE)
    }
  }
}

