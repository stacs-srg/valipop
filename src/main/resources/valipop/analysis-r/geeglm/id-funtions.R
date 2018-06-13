
addCohortIDs.ob <- function(in.data) {
  
  e <- min(in.data$YOB)
  l <- max(in.data$YOB)
  
  id.data <- in.data
  
  id.data = within(id.data, {
    
    # print(NPCIAP)
    
    idvar <- 
      
      ifelse(CIY == "YES",
             
             ifelse(Source == "SIM", 
                    ifelse(NPCIAP == "0", 
                           (YOB - e) + bin2dec(c(TRUE,TRUE,FALSE,FALSE,FALSE)) * (l - e + 1), #SIM-0
                           ifelse(NPCIAP == "1",
                                  (YOB - e) + bin2dec(c(TRUE,TRUE,FALSE,FALSE,TRUE)) * (l - e + 1), #SIM-1
                                  ifelse(NPCIAP == "2",
                                         (YOB - e) + bin2dec(c(TRUE,TRUE,FALSE,TRUE,FALSE)) * (l - e + 1), #SIM-2
                                         ifelse(NPCIAP == "3",
                                                (YOB - e) + bin2dec(c(TRUE,TRUE,FALSE,TRUE,TRUE)) * (l - e + 1), #SIM-3
                                                ifelse(NPCIAP == "4",
                                                       (YOB - e) + bin2dec(c(TRUE,TRUE,TRUE,FALSE,FALSE)) * (l - e + 1), #SIM-4
                                                       ifelse(NPCIAP == "5",
                                                              (YOB - e) + bin2dec(c(TRUE,TRUE,TRUE,FALSE,TRUE)) * (l - e + 1), #SIM-5+
                                                              (YOB - e) + bin2dec(c(TRUE,TRUE,TRUE,TRUE,FALSE)) * (l - e + 1) #SIM-0+
                                                       )
                                                )
                                         )
                                  )
                           )
                    ),
                    ifelse(NPCIAP == "0", 
                           (YOB - e) + bin2dec(c(FALSE,TRUE,FALSE,FALSE,FALSE)) * (l - e + 1), #STAT-0
                           ifelse(NPCIAP == "1",
                                  (YOB - e) + bin2dec(c(FALSE,TRUE,FALSE,FALSE,TRUE)) * (l - e + 1), #STAT-1
                                  ifelse(NPCIAP == "2",
                                         (YOB - e) + bin2dec(c(FALSE,TRUE,FALSE,TRUE,FALSE)) * (l - e + 1), #STAT-2
                                         ifelse(NPCIAP == "3",
                                                (YOB - e) + bin2dec(c(FALSE,TRUE,FALSE,TRUE,TRUE)) * (l - e + 1), #STAT-3
                                                ifelse(NPCIAP == "4",
                                                       (YOB - e) + bin2dec(c(FALSE,TRUE,TRUE,FALSE,FALSE)) * (l - e + 1), #STAT-4
                                                       ifelse(NPCIAP == "5",
                                                              (YOB - e) + bin2dec(c(FALSE,TRUE,TRUE,FALSE,TRUE)) * (l - e + 1), #STAT-5+
                                                              (YOB - e) + bin2dec(c(FALSE,TRUE,TRUE,TRUE,FALSE)) * (l - e + 1) #STAT-0+
                                                       )
                                                )
                                         )
                                  )
                           )
                    )
             ),
             ifelse(Source == "SIM", 
                    ifelse(NPCIAP == "0", 
                           (YOB - e) + bin2dec(c(TRUE,FALSE,FALSE,FALSE,FALSE)) * (l - e + 1), #SIM-0
                           ifelse(NPCIAP == "1",
                                  (YOB - e) + bin2dec(c(TRUE,FALSE,FALSE,FALSE,TRUE)) * (l - e + 1), #SIM-1
                                  ifelse(NPCIAP == "2",
                                         (YOB - e) + bin2dec(c(TRUE,FALSE,FALSE,TRUE,FALSE)) * (l - e + 1), #SIM-2
                                         ifelse(NPCIAP == "3",
                                                (YOB - e) + bin2dec(c(TRUE,FALSE,FALSE,TRUE,TRUE)) * (l - e + 1), #SIM-3
                                                ifelse(NPCIAP == "4",
                                                       (YOB - e) + bin2dec(c(TRUE,FALSE,TRUE,FALSE,FALSE)) * (l - e + 1), #SIM-4
                                                       ifelse(NPCIAP == "5",
                                                              (YOB - e) + bin2dec(c(TRUE,FALSE,TRUE,FALSE,TRUE)) * (l - e + 1), #SIM-5+
                                                              (YOB - e) + bin2dec(c(TRUE,FALSE,TRUE,TRUE,FALSE)) * (l - e + 1) #SIM-0+
                                                       )
                                                )
                                         )
                                  )
                           )
                    ),
                    ifelse(NPCIAP == "0", 
                           (YOB - e) + bin2dec(c(FALSE,FALSE,FALSE,FALSE,FALSE)) * (l - e + 1), #STAT-0
                           ifelse(NPCIAP == "1",
                                  (YOB - e) + bin2dec(c(FALSE,FALSE,FALSE,FALSE,TRUE)) * (l - e + 1), #STAT-1
                                  ifelse(NPCIAP == "2",
                                         (YOB - e) + bin2dec(c(FALSE,FALSE,FALSE,TRUE,FALSE)) * (l - e + 1), #STAT-2
                                         ifelse(NPCIAP == "3",
                                                (YOB - e) + bin2dec(c(FALSE,FALSE,FALSE,TRUE,TRUE)) * (l - e + 1), #STAT-3
                                                ifelse(NPCIAP == "4",
                                                       (YOB - e) + bin2dec(c(FALSE,FALSE,TRUE,FALSE,FALSE)) * (l - e + 1), #STAT-4
                                                       ifelse(NPCIAP == "5",
                                                              (YOB - e) + bin2dec(c(FALSE,FALSE,TRUE,FALSE,TRUE)) * (l - e + 1), #STAT-5+
                                                              (YOB - e) + bin2dec(c(FALSE,FALSE,TRUE,TRUE,FALSE)) * (l - e + 1) #STAT-0+
                                                       )
                                                )
                                         )
                                  )
                           )
                    )
             )
      )
    
    
  })
  
  
  data.id.sorted <- id.data[order(id.data$idvar, id.data$Age, id.data$CIY),]
  return(data.id.sorted)  
  
}

addCohortIDs.ob2 <- function(in.data) {
  
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
  maleL <- max(in.data[which(in.data$Sex == "MALE"),]$YOB)
  femaleL <- max(in.data[which(in.data$Sex == "FEMALE"),]$YOB)
  
  data.id <- in.data
  
  data.id = within(data.id, {
    idvar = ifelse(Source == "SIM", 
                   ifelse(Sex == "MALE", 
                          (YOB - e) + bin2dec(c(TRUE, TRUE)) * (maleL - e + 1),
                          (YOB - e) + bin2dec(c(TRUE, FALSE)) * (femaleL - e + 1)
                   ),
                   ifelse(Sex == "MALE", 
                          (YOB - e) + bin2dec(c(FALSE, TRUE)) * (maleL - e + 1),
                          (YOB - e) + bin2dec(c(FALSE, FALSE)) * (femaleL - e + 1)
                   )
    ) 
  })
  
  data.id.sorted <- data.id[order(data.id$idvar, data.id$Age, data.id$Died),]
  return(data.id.sorted)  
  
}

addCohortIDs.death2 <- function(in.data) {
  
  e <- min(in.data$YOB)
  maleL <- max(in.data[which(in.data$Sex == "MALE"),]$YOB)
  femaleL <- max(in.data[which(in.data$Sex == "FEMALE"),]$YOB)
  
  data.id <- in.data
  
  data.id = within(data.id, {
    idvar = ifelse(Source == "SIM", 
                   ifelse(Sex == "MALE",
                          ifelse(Died == "YES",
                            (YOB - e) + bin2dec(c(TRUE, TRUE, TRUE)) * (maleL - e + 1),
                            (YOB - e) + bin2dec(c(TRUE, TRUE, FALSE)) * (maleL - e + 1)
                          ),
                          ifelse(Died == "YES",
                             (YOB - e) + bin2dec(c(TRUE, FALSE, TRUE)) * (femaleL - e + 1),
                             (YOB - e) + bin2dec(c(TRUE, FALSE, FALSE)) * (femaleL - e + 1)
                          )
                   ),
                   ifelse(Sex == "MALE", 
                          ifelse(Died == "YES",
                            (YOB - e) + bin2dec(c(FALSE, TRUE, TRUE)) * (maleL - e + 1),
                            (YOB - e) + bin2dec(c(FALSE, TRUE, FALSE)) * (maleL - e + 1)
                          ),
                          ifelse(Died == "YES",
                             (YOB - e) + bin2dec(c(FALSE, FALSE, TRUE)) * (femaleL - e + 1),
                             (YOB - e) + bin2dec(c(FALSE, FALSE, FALSE)) * (femaleL - e + 1)
                          )
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
                   (YOB - e) + bin2dec(c(TRUE)) * (l - e + 1),
                   (YOB - e) + bin2dec(c(FALSE)) * (l - e + 1))
  })
  
  data.id.sorted <- data.id[order(data.id$idvar, data.id$Age, data.id$NCIY),]
  return(data.id.sorted)  
  
}

addCohortIDs.mb2 <- function(in.data) {
  
  e <- min(in.data$YOB)
  l <- max(in.data$YOB)
  
  data.id <- in.data
  
  data.id = within(data.id, {
    idvar = ifelse(Source == "SIM", 
                   (YOB - e) + bin2dec(c(TRUE)) * (l - e + 1),
                   (YOB - e) + bin2dec(c(FALSE)) * (l - e + 1))
  })
  
  data.id.sorted <- data.id[order(data.id$idvar, data.id$NCIY, data.id$Age),]
  return(data.id.sorted)  
  
}

addCohortIDs.part3 <- function(in.data) {
  
  e <- min(in.data$YOB)
  l <- max(in.data$YOB)
  
  data.id <- in.data
  
  data.id = within(data.id, {
    idvar = ifelse(Source == "SIM", 
                   (YOB - e) + bin2dec(c(TRUE)) * (l - e + 1),
                   (YOB - e) + bin2dec(c(FALSE)) * (l - e + 1))
  })
  
  data.id.sorted <- data.id[order(data.id$idvar, data.id$NPA, data.id$Age),]
  return(data.id.sorted)
  
}

addCohortIDs.part2 <- function(in.data) {
  
  e <- min(in.data$YOB)
  l <- max(in.data$YOB)
  
  data.id <- in.data
  
  data.id = within(data.id, {
    idvar = ifelse(Source == "SIM", 
                   (YOB - e) + bin2dec(c(TRUE)) * (l - e + 1),
                   (YOB - e) + bin2dec(c(FALSE)) * (l - e + 1))
  })
  
  data.id.sorted <- data.id[order(data.id$idvar, data.id$Age, data.id$NPA),]
  return(data.id.sorted)  
  
}

addCohortIDs.part <- function(in.data) {
  
  e <- min(in.data$Date)
  l <- max(in.data$Date)
  
  data.id <- in.data
  
  data.id = within(data.id, {
    idvar = ifelse(Source == "SIM", 
                   ifelse(NPA == "15-19", 
                          (Date - e) + bin2dec(c(TRUE,FALSE,FALSE,FALSE)) * (l - e + 1), #SIM-15-19
                          ifelse(NPA == "20-24",
                                 (Date - e) + bin2dec(c(TRUE,FALSE,FALSE,TRUE)) * (l - e + 1), #SIM-20-24
                                 ifelse(NPA == "25-29",
                                        (Date - e) + bin2dec(c(TRUE,FALSE,TRUE,FALSE)) * (l - e + 1), #SIM-25-29
                                        ifelse(NPA == "30-34",
                                               (Date - e) + bin2dec(c(TRUE,FALSE,TRUE,TRUE)) * (l - e + 1), #SIM-30-34
                                               ifelse(NPA == "35-39",
                                                      (Date - e) + bin2dec(c(TRUE,TRUE,FALSE,FALSE)) * (l - e + 1), #SIM-35-39
                                                      ifelse(NPA == "40-44",
                                                             (Date - e) + bin2dec(c(TRUE,TRUE,FALSE,TRUE)) * (l - e + 1), #SIM-40-44
                                                             ifelse(NPA == "45-49",
                                                                    (Date - e) + bin2dec(c(TRUE,TRUE,TRUE,FALSE)) * (l - e + 1), #SIM-40-49
                                                                    (Date - e) + bin2dec(c(TRUE,TRUE,TRUE,TRUE)) * (l - e + 1) #SIM-50+
                                                             )
                                                      )
                                               )
                                        )
                                 )
                          )
                   ),
                   ifelse(NPA == "15-19", 
                          (Date - e) + bin2dec(c(FALSE,FALSE,FALSE,FALSE)) * (l - e + 1), #STAT-15-19
                          ifelse(NPA == "20-24",
                                 (Date - e) + bin2dec(c(FALSE,FALSE,FALSE,TRUE)) * (l - e + 1), #STAT-20-24
                                 ifelse(NPA == "25-29",
                                        (Date - e) + bin2dec(c(FALSE,FALSE,TRUE,FALSE)) * (l - e + 1), #STAT-25-29
                                        ifelse(NPA == "30-34",
                                               (Date - e) + bin2dec(c(FALSE,FALSE,TRUE,TRUE)) * (l - e + 1), #STAT-30-34
                                               ifelse(NPA == "35-39",
                                                      (Date - e) + bin2dec(c(FALSE,TRUE,FALSE,FALSE)) * (l - e + 1), #STAT-35-39
                                                      ifelse(NPA == "40-44",
                                                             (Date - e) + bin2dec(c(FALSE,TRUE,FALSE,TRUE)) * (l - e + 1), #STAT-40-44
                                                             ifelse(NPA == "45-49",
                                                                    (Date - e) + bin2dec(c(FALSE,TRUE,TRUE,FALSE)) * (l - e + 1), #STAT-40-49
                                                                    (Date - e) + bin2dec(c(FALSE,TRUE,TRUE,TRUE)) * (l - e + 1) #STAT-50+
                                                             )
                                                      )
                                               )
                                        )
                                 )
                          )
                   )
    )
  })
  
  data.id.sorted <- data.id[order(data.id$idvar, data.id$Age, data.id$YOB),]
  return(data.id.sorted)  
  
  
}

addCohortIDs.sep <- function(in.data) {
  
  e <- min(in.data$YOB)
  l <- max(in.data$YOB)
  
  data.id <- in.data
  
  data.id = within(data.id, {
    idvar = ifelse(Source == "SIM", 
                   (YOB - e) + bindec(c(TRUE)) * (l - e + 1),
                   (YOB - e) + bin2dec(c(FALSE)) * (l - e + 1))
  })
  
  data.id.sorted <- data.id[order(data.id$idvar, data.id$NCIP, data.id$Separated),]
  return(data.id.sorted)  
  
}

addCohortIDs.sep2 <- function(in.data) {
  
  e <- min(in.data$YOB)
  l <- max(in.data$YOB)
  
  data.id <- in.data
  
  data.id = within(data.id, {
    idvar = ifelse(Source == "SIM",
                   ifelse(Separated == "YES",
                          (YOB - e) + bin2dec(c(TRUE,TRUE)) * (l - e + 1),
                          (YOB - e) + bin2dec(c(TRUE,FALSE)) * (l - e + 1)
                   ),
                   ifelse(Separated == "YES",
                          (YOB - e) + bin2dec(c(FALSE,TRUE)) * (l - e + 1),
                          (YOB - e) + bin2dec(c(FALSE,FALSE)) * (l - e + 1)
                   )
    )
  })
  
  data.id.sorted <- data.id[order(data.id$idvar, data.id$NCIP),]
  return(data.id.sorted)  
  
}

addCohortIDs.sep3 <- function(in.data) {
  
  e <- min(in.data$YOB)
  l <- max(in.data$YOB)
  
  data.id <- in.data
  
  data.id = within(data.id, {
    idvar = ifelse(Source == "SIM", 
         ifelse(NCIP == "0", 
                (YOB - e) + bin2dec(c(TRUE,FALSE,FALSE,FALSE)) * (l - e + 1), #SIM-0
                ifelse(NCIP == "1",
                       (YOB - e) + bin2dec(c(TRUE,FALSE,FALSE,TRUE)) * (l - e + 1), #SIM-1
                       ifelse(NCIP == "2",
                              (YOB - e) + bin2dec(c(TRUE,FALSE,TRUE,FALSE)) * (l - e + 1), #SIM-2
                              ifelse(NCIP == "3",
                                     (YOB - e) + bin2dec(c(TRUE,FALSE,TRUE,TRUE)) * (l - e + 1), #SIM-3
                                     ifelse(NCIP == "4",
                                            (YOB - e) + bin2dec(c(TRUE,TRUE,FALSE,FALSE)) * (l - e + 1), #SIM-4
                                            ifelse(NCIP == "5+",
                                                   (YOB - e) + bin2dec(c(TRUE,TRUE,FALSE,TRUE)) * (l - e + 1), #SIM-5+
                                                   (YOB - e) + bin2dec(c(TRUE,TRUE,TRUE,FALSE)) * (l - e + 1) #SIM-0+
                                            )
                                     )
                              )
                       )
                )
         ),
         ifelse(NCIP == "0", 
                (YOB - e) + bin2dec(c(FALSE,FALSE,FALSE,FALSE)) * (l - e + 1), #STAT-0
                ifelse(NCIP == "1",
                       (YOB - e) + bin2dec(c(FALSE,FALSE,FALSE,TRUE)) * (l - e + 1), #STAT-1
                       ifelse(NCIP == "2",
                              (YOB - e) + bin2dec(c(FALSE,FALSE,TRUE,FALSE)) * (l - e + 1), #STAT-2
                              ifelse(NCIP == "3",
                                     (YOB - e) + bin2dec(c(FALSE,FALSE,TRUE,TRUE)) * (l - e + 1), #STAT-3
                                     ifelse(NCIP == "4",
                                            (YOB - e) + bin2dec(c(FALSE,TRUE,FALSE,FALSE)) * (l - e + 1), #STAT-4
                                            ifelse(NCIP == "5",
                                                   (YOB - e) + bin2dec(c(FALSE,TRUE,FALSE,TRUE)) * (l - e + 1), #STAT-5+
                                                   (YOB - e) + bin2dec(c(FALSE,TRUE,TRUE,FALSE)) * (l - e + 1) #STAT-0+
                                            )
                                     )
                              )
                       )
                )
         )
  )
    
  })
  
  data.id.sorted <- data.id[order(data.id$idvar, data.id$NCIP),]
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