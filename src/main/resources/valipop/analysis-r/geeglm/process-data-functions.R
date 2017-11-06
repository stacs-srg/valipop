
readInData <- function(path) {
  data = read.csv(path, sep = ',', header = T)
  return(data)
}

cleanData <- function(dirtyData, round = TRUE) {
  clean.data <- dirtyData
  if(round) {
    clean.data <- dirtyData[which(dirtyData$freq > 0.5),]
    clean.data$freq <- round(clean.data$freq)
  }
  clean.data <- clean.data[which(clean.data$Date < 2014),]
  clean.data <- clean.data[which(clean.data$Date > 1854),]
  return(clean.data)
}

cleanDeathData <- function(dirtyData, round = TRUE) {
  return(cleanData(dirtyData, round))
}

cleanOBData <- function(dirtyData, largestBirthingAge, round = TRUE) {
  clean.data <- cleanData(dirtyData, round)
  clean.data <- clean.data[which(clean.data$Age >= 15), ]
  clean.data <- clean.data[which(clean.data$Age <= largestBirthingAge), ]
  #clean.data <- clean.data[which(clean.data$CIY == "YES"), ]
  return(clean.data)
}

sourceSummary <- function(data) {
  print(summary(data[which(data$Source == "SIM" ),]))
  print(summary(data[which(data$Source == "STAT"),]))
}

cleanMBData <-function(dirtyData, largestBirthingAge, round = TRUE)  {
  #dirtyData$freq <- ceiling(dirtyData$freq)
  clean.data <- cleanOBData(dirtyData, largestBirthingAge, round)
  clean.data <- clean.data[which(clean.data$NCIY != "0"), ]

  return(clean.data)
}

cleanPartData <- function(dirtyData, round = TRUE) {
  clean.data <- cleanData(dirtyData, round = round)
  clean.data <- clean.data[which(clean.data$NPA != "na") , ]
  clean.data$NPA <- droplevels(clean.data$NPA)
  return(clean.data)
}

cleanSepData <- function(dirtyData, round = TRUE) {
  clean.data <- cleanData(dirtyData, round)
  clean.data <- clean.data[which(clean.data$Separated != "NA") , ]
  clean.data$Separated <- droplevels(clean.data$Separated)
  clean.data$NCIP <- droplevels(clean.data$NCIP)
  return(clean.data)
}

