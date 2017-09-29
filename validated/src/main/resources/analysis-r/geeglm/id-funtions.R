

addCohortIDs.ob <- function(in.data) {
  
  e <- min(in.data$YOB)
  l <- max(in.data$YOB)
  
  data.id <- in.data
  
  data.id = within(data.id, {
    idvar = ifelse(Source == "SIM", 
                   (YOB - e) + bin2dec(c(TRUE)) * (l - e + 1),
                   (YOB - e) + bin2dec(c(FALSE)) * (l - e + 1))
  })

  data.id.sorted <- data.id[order(data.id$idvar, data.id$Age, data.id$NPCIAP, data.id$CIY),]
  return(data.id.sorted)  
  
}

addCohortIDs.death <- function(in.data) {
  
  e <- min(in.data$YOB)
  l <- max(in.data$YOB)
  
  data.id <- in.data
  
  data.id = within(data.id, {
    idvar = ifelse(Source == "SIM", 
                   ifelse(Sex == "MALE", 
                          (YOB - e) + bin2dec(c(TRUE, TRUE)) * (l - e + 1),
                          (YOB - e) + bin2dec(c(TRUE, FALSE)) * (l - e + 1)
                   ),
                   ifelse(Sex == "MALE", 
                          (YOB - e) + bin2dec(c(FALSE, TRUE)) * (l - e + 1),
                          (YOB - e) + bin2dec(c(FALSE, FALSE)) * (l - e + 1)
                   )
    ) 
  })
  
  data.id.sorted <- data.id[order(data.id$idvar, data.id$Age, data.id$Died),]
  return(data.id.sorted)  
  
}

addCohortIDs.mb <- function(in.data) {
  
  e <- min(in.data$YOB)
  l <- max(in.data$YOB)
  
  data.id <- in.data
  
  data.id = within(data.id, {
    idvar = ifelse(Source == "SIM", 
                   ifelse(Sex == "MALE", 
                          (YOB - e) + bin2dec(c(TRUE, TRUE)) * (l - e + 1),
                          (YOB - e) + bin2dec(c(TRUE, FALSE)) * (l - e + 1)
                   ),
                   ifelse(Sex == "MALE", 
                          (YOB - e) + bin2dec(c(FALSE, TRUE)) * (l - e + 1),
                          (YOB - e) + bin2dec(c(FALSE, FALSE)) * (l - e + 1)
                   )
    ) 
  })
  
  data.id.sorted <- data.id[order(data.id$idvar, data.id$Age, data.id$Died),]
  return(data.id.sorted)  
  
}


bin2dec <- function(binaryvector) { 
  sum(2^(which(rev(binaryvector)==TRUE)-1)) 
}

addPanelIDs.SY <- function(in.data) {
  data.id = within(in.data, {
    idvar = ifelse(Source == "SIM", 
                   ifelse(Sex == "MALE", as.numeric(paste("1", "1", YOB, sep = "")), as.numeric(paste("1", "2", YOB, sep = ""))),
                   ifelse(Sex == "MALE", as.numeric(paste("1", "1", YOB, sep = "")), as.numeric(paste("1", "2", YOB, sep = ""))))
  })
  
  data.id = within(data.id, {
    idvarScaled = ifelse(idvar > 110000 & idvar < 120000, 
                         idvar - 111749,
                         idvar)
  })
  
  data.id = within(data.id, {
    idvarScaled = ifelse(idvar > 120000 & idvar < 130000, 
                         idvar - 121485,
                         idvarScaled)
  })
  
  data.id = within(data.id, {
    idvarScaled = ifelse(idvar > 210000 & idvar < 220000, 
                         idvar - 211221,
                         idvarScaled)
  })
  
  data.id = within(data.id, {
    idvarScaled = ifelse(idvar > 220000 & idvar < 230000, 
                         idvar - 220957,
                         idvarScaled)
  })
  
  
  data.id.sorted <- data.id[order(data.id$idvarScaled, data.id$Age, data.id$Died),]
  return(data.id.sorted)
}