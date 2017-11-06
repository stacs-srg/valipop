deathSatGLM <- function(in.data) {
  return(runGLM(freq ~ Date * Age * Sex * Died * Source, in.data))
}

deathSelGLM <- function(in.data) {
  return(runGLM(freq ~ Date + Age + Sex + Died + Date:Died + Age:Died + Sex:Died + Date:Age:Died 
                + Date:Sex:Died + Age:Sex:Died, in.data))
}

obSatGLM <- function(in.data) {
  return(runGLM(freq ~ Date * Age * NPCIAP * CIY * Source, in.data))
}

obSelGLM <- function(in.data) {
  return(runGLM(freq ~ Age + NPCIAP + CIY + Date + Age:NPCIAP + Age:CIY + NPCIAP:CIY + Age:NPCIAP:CIY, in.data))
}

mbSatGLM <- function(in.data) {
  return(runGLM(freq ~ Date * NCIY * Age * Source, in.data))
}

mbSelGLM <- function(in.data) {
  return(runGLM(freq ~ Date + NCIY + Age + Date:NCIY + Date:Age, in.data))
}

partSatGLM <- function(in.data) {
  return(runGLM(freq ~ Date * NPA * Age * Source, in.data))
}

partSelGLM <- function(in.data) {
  return(runGLM(freq ~ Date + NPA + Age + NPA:Age, in.data))
}

sepSatGLM <- function(in.data) {
  return(runGLM(freq ~ Date * NCIP * Separated * Source, in.data))
}

sepSatGLM.2 <- function(in.data) {
  return(runGLM(freq ~ Date * NCIP * Source, in.data))
}

sepSelGLM <- function(in.data) {
  return(runGLM(freq ~ Date + NCIP + Separated + NCIP:Separated, in.data))
}

runGLM <- function(formula, in.data) {
  
  glm.model = glm(formula, data = in.data, family = poisson)
  
  
  plot(residuals(glm.model), type = "l")
  
  
  acf(residuals(glm.model), lag.max = 100)
  acf(residuals(glm.model), lag.max = 1000)
  
  require(mgcv)
  fit<- gam(residuals(glm.model) ~ s(in.data$idvar))
  plot(fit)
  
  print(summary(fit))
  
  return(glm.model)
  
}
