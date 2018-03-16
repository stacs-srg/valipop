deathSatGEEGLM <- function(in.data) {
  return(runGEEGLM(freq ~ Date * Age * Sex * Died * Source, in.data, constr = "ar2"))
}

obSatGEEGLM <- function(in.data) {
  return(runGEEGLM(freq ~ Date * Age * NPCIAP * CIY * Source, in.data))
}

mbSatGEEGLM <- function(in.data) {
  return(runGEEGLM(freq ~ Date * Age * NCIY * Source, in.data))
}

partSatGEEGLM <- function(in.data) {
  return(runGEEGLM(freq ~ Date * NPA * Age * Source, in.data))
}

sepSatGEEGLM <- function(in.data, constr = "ar1") {
  return(runGEEGLM(freq ~ Date * Source * NCIP * Separated, in.data, constr = constr))
}

sepSatGEEGLM.2 <- function(in.data) {
  return(runGEEGLM(freq ~ Date * Source * NCIP, in.data))
}

sepSatGEEGLM.3 <- function(in.data) {
  return(runGEEGLM(freq ~ Date + Source + NCIP + Separated, in.data))
}

sepSatGEEGLM.4 <- function(in.data, constr = "ar1") {
  return(runGEEGLM(freq ~ Date * Source * Separated, in.data, constr = constr))
}

runGEEGLM <- function(formula, in.data, constr = "ar1") {
  
  library(geepack)  
  mod <- geeglm(formula, id=idvar, data = in.data, corstr=constr)
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
  
  return(mod)
}
