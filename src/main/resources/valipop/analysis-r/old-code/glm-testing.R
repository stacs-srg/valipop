
path = "OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm/20170925-115556:076/tables/death-CT.csv"

data = read.csv(path, sep = ',', header = T)
summary(data[which(data$Source == "STAT"),])
summary(data[which(data$Source == "SIM"),])


debug.data <- data[which(data$Age > 105),]
order(debug.data, debug.data$YOB)
summary(debug.data)


library(geepack)

head(data)
data.sorted <- data[order(data$Source, data$YOB, data$Age),]
head(data.sorted, 25)

  
# This creates the panel ids. These are based on Source, Sex and YOB
path = "OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm/20170925-115556:076/tables/death-CT.csv"
path = "OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-big/20170925-205109:366/tables/death-CT.csv"
path = "OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-big/20170925-194825:771/tables/death-CT.csv"
path = "OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm-big/20170925-173500:990/tables/death-CT.csv"
path = "OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/over-ageing/20170926-113306:529/tables/death-CT.csv"

data = read.csv(path, sep = ',', header = T)
summary(data[which(data$Source == "STAT"),])
summary(data[which(data$Source == "SIM"),])

data <- data[which(data$freq > 0.5),]
data$freq <- round(data$freq)
data <- data[which(data$Date < 2014),]
data <- data[which(data$Date > 1854),]


data.id = within(data, {
                 idvar = ifelse(Source == "SIM", 
                                ifelse(Sex == "MALE", as.numeric(paste("1", "1", YOB, sep = "")), as.numeric(paste("1", "2", YOB, sep = ""))),
                                ifelse(Sex == "MALE", as.numeric(paste("2", "1", YOB, sep = "")), as.numeric(paste("2", "2", YOB, sep = ""))))
                 })

data.id.sorted <- data.id[order(data.id$idvar, data.id$Age, data.id$Died),]
head(data.id.sorted, 25)
library(geepack)
mod2 <- geeglm(freq ~ Date * Age * Sex * Died * Source, id=idvar, data = data.id.sorted, corstr="ar1")
summary(mod2)

plot(residuals(mod2), type = "l")

acf(residuals(mod2), lag.max = 1000)
require(mgcv)
fit<- gam(residuals(mod2) ~ s(data.id.sorted$YOB))
plot(fit, xlim = c(1855,2013))
fit<- gam(residuals(mod2) ~ s(data.id.sorted$idvar))
plot(fit
summary(fit)



# This creates the panel ids. These are based on Sex and YOB
path = "OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/geeglm/20170925-115556:076/tables/death-CT.csv"

data = read.csv(path, sep = ',', header = T)

data <- data[which(data$freq > 0.5),]
data$freq <- round(data$freq)
data <- data[which(data$Date < 2014),]
data <- data[which(data$Date > 1854),]

data.id = within(data, {
  idvar = ifelse(Source == "SIM", 
                 ifelse(Sex == "MALE", as.numeric(paste("1", "1", YOB, sep = "")), as.numeric(paste("1", "2", YOB, sep = ""))),
                 ifelse(Sex == "MALE", as.numeric(paste("1", "1", YOB, sep = "")), as.numeric(paste("1", "2", YOB, sep = ""))))
})

data.id.sorted <- data.id[order(data.id$idvar, data$Age, data$Died, data$Source),]
head(data.id.sorted, 25)
library(geepack)
mod2 <- geeglm(freq ~ Date * Age * Sex * Died * Source, id=idvar, family = poisson(), data = data.id.sorted, corstr="ar1")
summary(mod2)



mod1 <- geeglm(freq ~ Date + Age + Sex + Died + Date:Died + Age:Died + Sex:Died + Date:Age:Died + Date:Sex:Died + Age:Sex:Died, id=idvar, data = data.id.sorted, corstr="ar1")
mod1
summary(mod1)
anova(mod1)

mod2 <- geeglm(freq ~ Date * Age * Sex * Died * Source, id=idvar, data = data.id.sorted, corstr="ar1")
summary(mod2)


glm.model = glm(freq ~ Date + Age + Sex + Died + Date:Died + Age:Died + Sex:Died + Date:Age:Died 
                + Date:Sex:Died + Age:Sex:Died, data = data.id.sorted, family = poisson)
plot(residuals(glm.model), type = "l")

plot(acf(residuals(glm.model), lag.max = 1000))
require(mgcv)
fit<- gam(residuals(glm.model) ~ s(data.id.sorted$Date))
plot(fit)

summary(fit)

