deathSatGEEGLM <- function(in.data) {
  return(runGEEGLM(freq ~ Date * Age * Sex * Died * Source, in.data))
}

deathSelGEEGLM <- function(in.data) {
  return(runGEEGLM(freq ~ Date + Age + Sex + Died + Date:Died + Age:Died + Sex:Died + Date:Age:Died + Date:Sex:Died + Age:Sex:Died, in.data))
}

obSatGEEGLM <- function(in.data) {
  return(runGEEGLM(freq ~ Date * Age * NPCIAP * CIY * Source, in.data))
}

mbSatGEEGLM <- function(in.data) {
  # We don't use Age as it reduces the frequency values for the higher NCIY values to virtually 0, and this confounding the poisson based assumptions of the model
  return(runGEEGLM(freq ~ Date * NCIY * Source, in.data))
}

partSatGEEGLM <- function(in.data) {
  return(runGEEGLM(freq ~ Date * NPA * Age * Source, in.data))
}

sepSatGEEGLM <- function(in.data) {
  return(runGEEGLM(freq ~ NCIP * Separated, in.data))
}


runGEEGLM <- function(formula, in.data) {
  
  par(mfrow = c(1,3))
  library(geepack)  
  mod <- geeglm(formula, id=idvar, data = in.data, corstr="ar1")
  print(summary(mod))
  
  plot(residuals(mod), type = "l")
  acf(residuals(mod), lag.max = 100)
  acf(residuals(mod), lag.max = 500)
  acf(residuals(mod), lag.max = 1000)
  
  require(mgcv)
  fit<- gam(residuals(mod) ~ s(in.data$YOB))
  plot(fit)
  summary(fit)
  
  fit<- gam(residuals(mod) ~ s(in.data$idvar))
  plot(fit)
  summary(fit)
  
  par(mfrow = c(1,1))
  
  return(mod)
}
