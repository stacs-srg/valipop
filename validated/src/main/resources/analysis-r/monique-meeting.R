
path0 = "/Users/tsd4/OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/stats-ct-adjust-working/20170921-160630:348/tables/death-CT-zav-0.csv"
path1 = "/Users/tsd4/OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/stats-ct-adjust-working/20170921-160630:348/tables/death-CT-zav-1.csv"

path = "/Users/tsd4/OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/pre-test/20170922-073944:253/tables/death-CT.csv"
data = read.csv(path, sep = ',', header = T)
summary(data)
data <- data[which(data$Date < 2014),]
data <- data[which(data$Date > 1854),]

data.zeroed = read.csv(path0, sep = ',', header = T)
data.ones = read.csv(path1, sep = ',', header = T)
data.missing = data.zeroed[which(data.zeroed$Source == "STAT" | (data.zeroed$Source == "SIM" & data.zeroed$freq != 0)) ,]

head(data.ones[which(data.ones$freq == 0) ,])

head(data.ones)

library("MASS")
model.zeroed = loglm(freq ~ Date + Age + Sex + Died + Date:Died + Age:Died + Sex:Died + Date:Age:Died + Date:Sex:Died + Age:Sex:Died, data = data.zeroed)
model.ones = loglm(freq ~ Date + Age + Sex + Died + Date:Died + Age:Died + Sex:Died + Date:Age:Died + Date:Sex:Died + Age:Sex:Died, data = data.ones)
model.missing = loglm(freq ~ Date + Age + Sex + Died + Date:Died + Age:Died + Sex:Died + Date:Age:Died + Date:Sex:Died + Age:Sex:Died, data = data.missing)

model.zeroed
summary(model.ones)
model.missing
data.sorted <- data[order(data$Date, data$Age),]
head(data.sorted)

glm.model = glm(freq ~ Date + Age + Sex + Died + Date:Died + Age:Died + Sex:Died + Date:Age:Died 
                + Date:Sex:Died + Age:Sex:Died, data = data.sorted, family = poisson)
plot(residuals(glm.model), type = "l")

plot(acf(residuals(glm.model), lag.max = 1000))
require(mgcv)
fit<- gam(residuals(glm.model) ~ s(data.sorted$Date))
plot(fit)

summary(fit)

glm.model.sat = glm(freq ~ Source * Date * Age * Sex * Died, data = data.ones, family = poisson)
summary(glm.model.sat)
anova(glm.model, test = "Chisq")

