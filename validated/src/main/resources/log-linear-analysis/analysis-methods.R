
mbAnalysis <- function(file) {
  
  MB.data = read.csv("/Users/tsd4/OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/CT/20170729-194213:256/tables/mb-CT.csv")
  
  print(summary(MB.data[which(MB.data$Source == "SIM"),]))
  print(summary(MB.data[which(MB.data$Source == "STAT"),]))
  
  MB.data.stat <- MB.data[which(MB.data$Source == "STAT") ,]
  MB.data.sim <- MB.data[which(MB.data$Source == "SIM") ,]
  
  par(mfrow=c(1,2))
  yMax <- max(MB.data.sim$freq, MB.data.stat$freq) * 1.01
  plot(MB.data.stat$NCIY, MB.data.stat$freq, col = 2, ylim = c(0, yMax))
  plot(MB.data.sim$NCIY, MB.data.sim$freq, col = 3, ylim = c(0, yMax))
  par(mfrow=c(1,1))
  
  library("MASS")
  MB.model.sat = loglm(Source ~ freq * Date * NCIY * Age, data = MB.data)
  MB.model.step.result = print(step(MB.model.sat, direction = "both"))
  MB.model.sel = eval(parse(text=MB.model.step.result["call"]))
  print("If this below model does not contain any source interactions
      then we can assert that the sim and input populations are of 
      the same specified statistical properties\n")
  print(MB.model.sel)
  
  MB.glm.model = glm(freq ~ Source * Date * NCIY * Age, data = MB.data, family = poisson)
  warnings()
  print(anova(MB.glm.model, test = "Chisq"))
  
}

deathAnalysis <- function() {
  
  Death.data = read.csv("/Users/tsd4/OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/Investigations/20170731-123210:866/tables/death-CT.csv", sep = ',', header = T)
  
  Death.data$freq <- round(Death.data$freq)
  Death.data <- Death.data[which(Death.data$freq != 0), ]
  Death.data <- Death.data[which(Death.data$Date >= 1855) , ]
  Death.data <- Death.data[which(Death.data$Date <= 2015) , ]
  
  summary(Death.data[which(Death.data$Source == "SIM"),])
  summary(Death.data[which(Death.data$Source == "STAT"),])
  
  Death.glm.model = glm(freq ~ Source * Date * Age * Sex * Died, data = Death.data, family = poisson)
  anova(Death.glm.model, test = "Chisq")
  
  library("MASS")
  Death.model.sat = loglm(freq ~ Source * Date * Age * Sex * Died, data = Death.data)
  Death.model.step.result = step(Death.model.sat, direction = "both")
  Death.model.sel = eval(parse(text=Death.model.step.result["call"]))
  cat("If this below model does not contain any source interactions
      then we can assert that the sim and input populations are of 
      the same specified statistical properties\n")
  Death.model.sel
  
  
  
}

mbAnalysis("/Users/tsd4/OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/CT/20170729-194213:256/tables/mb-CT.csv")
