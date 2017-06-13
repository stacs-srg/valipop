# sourceInteractionComparison
  library("MASS")
  dT <- read.table(dataFile, header = T)
  model.sat = loglm(freq ~ yob * age * sex * died * source, data = dT)
  model.step.result = step(model.sat, direction = "backward")
  model.sel = eval(parse(text=model.step.result["call"]))
  cat("If this below model does not contain any source interactions
        then we can assert that the sim and input populations are of 
        the same specified statistical properties\n")
  model.sel


# predictBasedComparion
  library("MASS")
  d <- read.table(dataFile, header = T)
  d.in = subset(d, source == "in")
  d.sim = subset(d, source == "sim")
  
  model.sat = loglm(freq ~ yob * age * sex * died, data = d.in)
  model.step.result = step(model.sat, direction = "backward")
  model.sel = eval(parse(text=model.step.result["call"]))
  
  model.sel.glm = glm(model.sel, data = d.in, family = poisson)
  anova(model.sel.glm, test = "Chisq")
  
  yhat = predict(model.sel.glm, d.sim, type = "response")
  y = d.sim$freq
  numVars = 12
  
  Rsq = 1 - (sum((y - yhat)^2)/sum((y - mean(y))^2))
  F = ((sum((yhat - mean(y))^2))/1)/((sum((y - yhat)^2))/(numVars-2))
  p = 1 - pchisq(q = F, df = numVars - 2)
  