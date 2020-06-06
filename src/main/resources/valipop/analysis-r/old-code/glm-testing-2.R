OB.data = read.csv("OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-bf-scot-4/20170928-144559:115/tables/ob-CT.csv", sep = ',', header = T)
summary(OB.data)
Death.data = read.csv("/Users/tsd4/Desktop/wednesday/results/bf-search-c/20170809-230536:594///tables/ob-CT.csv", sep = ',', header = T)

summary(OB.data[which(OB.data$Source == "SIM"),])
summary(OB.data[which(OB.data$Source == "STAT"),])

OB.data <- OB.data[which(OB.data$Date >= 1855) , ]
OB.data <- OB.data[which(OB.data$Date < 2015) , ]
OB.data$freq <- round(OB.data$freq)
OB.data <- OB.data[which(OB.data$freq != 0), ]

OB.data.stat <- OB.data[which(OB.data$Source == "STAT") ,]
OB.data.sim <- OB.data[which(OB.data$Source == "SIM") ,]

par(mfrow=c(1,1))
yMax <- max(OB.data.sim$freq, OB.data.stat$freq) + 20
plot(OB.data.stat$CIY, OB.data.stat$freq, col = 2, ylim = c(0, yMax))
points(OB.data.sim$CIY, OB.data.sim$freq, col = 3, ylim = c(0, yMax))

OB.data.stat.cohort <- OB.data[which(OB.data$Source == "STAT" & OB.data$NPCIAP == npciap) ,]
OB.data.sim.cohort <- OB.data[which(OB.data$Source == "SIM" & OB.data$NPCIAP == npciap) ,]

cohort <- "20to24"
npciap <- 3
ciy <- "YES"
OB.data.stat.cohort <- OB.data[which(OB.data$Source == "STAT" & OB.data$Age == cohort & OB.data$NPCIAP == npciap) ,]
OB.data.sim.cohort <- OB.data[which(OB.data$Source == "SIM" & OB.data$Age == cohort & OB.data$NPCIAP == npciap) ,]

par(mfrow=c(1,1))
maxY = max(OB.data.sim.cohort$freq, OB.data.stat.cohort$freq)

plot(OB.data.stat.cohort$Date, OB.data.stat.cohort$freq, col = 2, ylim = c(0, maxY))
points(OB.data.sim.cohort$Date, OB.data.sim.cohort$freq, col = 3)

plot(OB.data.stat$NPCIAP, OB.data.stat$CIY, col = 2)
plot(OB.data.sim$NPCIAP, OB.data.sim$CIY, col = 3)

OB.data

library("MASS")
OB.model.sat = loglm(freq ~ Source * Age * NPCIAP * CIY * Date, data = OB.data)
OB.model.step.result = step(OB.model.sat, direction = "backward")
OB.model.sel = eval(parse(text=OB.model.step.result["call"]))
cat("If this below model does not contain any source interactions
    then we can assert that the sim and input populations are of 
    the same specified statistical properties\n")
OB.model.sel

OB.glm.model = glm(freq ~ Source * Age * NPCIAP * CIY * Date, data = OB.data, family = poisson)
warnings()
anova(OB.glm.model, test = "Chisq")
