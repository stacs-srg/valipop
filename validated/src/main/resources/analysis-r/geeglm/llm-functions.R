deathSelLLM <- function(in.data) {
  library(MASS)
  model = loglm(freq ~ Date + Age + Sex + Died + Date:Died + Age:Died + Sex:Died + Date:Age:Died + Date:Sex:Died + Age:Sex:Died, data = in.data)
  return(model)
}

obSelLLM <- function(in.data) {
  library(MASS)
  model = loglm(freq ~ Age + NPCIAP + CIY + Date + Age:NPCIAP + Age:CIY + NPCIAP:CIY + Age:NPCIAP:CIY, data = in.data)
  return(model)
}

mbSelLLM <- function(in.data) {
  library(MASS)
  model = loglm(freq ~ Date + NCIY + Age + Date:NCIY + Date:Age, data = in.data)
  return(model)
}

partSelLLM <- function(in.data) {
  library(MASS)
  model = loglm(freq ~ Date + NPA + Age + NPA:Age, data = in.data)
  return(model)
}

sepSelLLM <- function(in.data) {
  library(MASS)
  model = loglm(freq ~ Date + NCIP + Separated + NCIP:Separated, data = in.data)
  return(model)
}