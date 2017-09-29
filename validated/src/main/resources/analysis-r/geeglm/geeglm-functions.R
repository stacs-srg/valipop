deathSatGEEGLM <- function(in.data) {
  return(runGEEGLM(freq ~ Date * Age * Sex * Died * Source, in.data))
}

deathSelGEEGLM <- function(in.data) {
  return(runGEEGLM(freq ~ Date + Age + Sex + Died + Date:Died + Age:Died + Sex:Died + Date:Age:Died + Date:Sex:Died + Age:Sex:Died, in.data))
}

obSatGEEGLM <- function(in.data) {
  return(runGEEGLM(freq ~ Date * Age * NPCIAP * CIY * Source, in.data))
}

mbSatGLM <- function(in.data) {
  return(runGEEGLM(freq ~ Date * NCIY * Age * Source, in.data))
}

partSatGLM <- function(in.data) {
  return(runGEEGLM(freq ~ Date * NPA * Age * Source, in.data))
}

sepSatGLM <- function(in.data) {
  return(runGEEGLM(freq ~ Date * NCIP * Separated * Source, in.data))
}


runGEEGLM <- function(formula, in.data) {
  
  library(geepack)  
  mod <- geeglm(formula, id=idvar, data = in.data, corstr="ar1")
  print(summary(mod))
  
  plot(residuals(mod), type = "l")
  acf(residuals(mod), lag.max = 100)
  
  require(mgcv)
  fit<- gam(residuals(mod) ~ s(in.data$YOB))
  plot(fit)
  summary(fit)
  
  fit<- gam(residuals(mod) ~ s(in.data$idvar))
  plot(fit)
  summary(fit)
  
  return(mod)
}