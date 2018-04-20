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
  #print(summary(mod))
  
  par(mfrow = c(1,2))
  plot(residuals(mod), type = "l")
  
  library(MRSea)
  library(ggplot2)
  runACF(in.data$idvar, mod, store=F)
  runDiagnosticsADAPTED(mod)
  
  par(mfrow = c(1,2))
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

# Code from MRSea
runDiagnosticsADAPTED<-function(model){
  print("Assessing predictive power")
  
  response<-model$y
  dat<- model$data
  
  #Assessing predictive power
  
  #r-squared:
  r2<-1-(sum((response-fitted(model))**2)/sum((response-mean(response))**2))
  #concordence correlation
  num<- 2*sum((response-mean(response))*(fitted(model)-mean(fitted(model))))
  den<- sum((response-mean(response))**2)+sum((fitted(model)-mean(fitted(model)))**2)
  rc<-num/den
  
  scaledRes<- residuals(model, type="response")/
    sqrt(family(model)$variance(fitted(model))*as.numeric(summary(model)$dispersion[1]))
  
  df<-data.frame(fits=fitted(model), res=scaledRes, smx=lowess(fitted(model), scaledRes)$x, smy=lowess(fitted(model), scaledRes)$y, response=response )
  
  
  a<- ggplot(df) + geom_point(aes(response, fits), alpha=0.15, size=3) + geom_abline(intercept=0, slope=1) + labs(x='Observed Values', y='Fitted Values', title=paste("Concordence correlation: ", round(rc,4), "\nMarginal R-squared value: ", round(r2,4), sep=""))
  a<-a + theme_bw() + theme(panel.grid.major=element_blank(), axis.text.x=element_text(size=15), axis.text.y=element_text(size=15), axis.title.x=element_text(size=15), axis.title.y=element_text(size=15), plot.title=element_text(size=15))
  
  b<- ggplot(df) + geom_point(aes(fits, res), alpha=0.15, size=3)+ geom_line(aes(smx, smy), col='red') + geom_abline(intercept=0, slope=0) + labs(x='Fitted Values', y='Scaled Pearsons Residuals')
  b<- b + theme_bw() + theme(panel.grid.major=element_blank(), axis.text.x=element_text(size=15), axis.text.y=element_text(size=15), axis.title.x=element_text(size=15), axis.title.y=element_text(size=15))
  
  library(gridExtra)
  grid.arrange(a, b, ncol=2, nrow = 1)  
  
}

