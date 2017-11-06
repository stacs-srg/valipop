
plotCohorts <- function(Death.data, age, title) {
  death <- "NO"

  #label <- "iw:100y - with MB - FACTOR 0.007462401"
  #label <- "iw:NO-SC - MB - F1 - BS + 1 - fix to undercounting of SIM"
  label <- title
  
  Death.data <- Death.data[order(Death.data$Date), ]
  
  Death.data.sim.male <- Death.data[which(Death.data$Age == age & Death.data$Source == "SIM" & Death.data$Sex == "MALE" & Death.data$Died == death ) , ]
  Death.data.sim.female <- Death.data[which(Death.data$Age == age & Death.data$Source == "SIM" & Death.data$Sex == "FEMALE" & Death.data$Died == death ) , ]
  
  Death.data.stat.male <- Death.data[which(Death.data$Age == age & Death.data$Source == "STAT" & Death.data$Sex == "MALE" & Death.data$Died == death ) , ]
  Death.data.stat.female <- Death.data[which(Death.data$Age == age & Death.data$Source == "STAT" & Death.data$Sex == "FEMALE" & Death.data$Died == death ) , ]
  
  maxY <- max(Death.data[which(Death.data$Age == age & Death.data$Died == death) , ]$freq) * 1.05
  minY <- min(Death.data[which(Death.data$Age == age & Death.data$Died == death) , ]$freq) * 0.95
  
  plot(Death.data.stat.male$Date, Death.data.stat.male$freq, col=2, main = label, ylim = c(minY, maxY),  panel.first = grid(), type = "l")
  points(Death.data.stat.female$Date, Death.data.stat.female$freq, col=3, type = "l")
  points(Death.data.sim.male$Date, Death.data.sim.male$freq, col=6, type = "l")
  points(Death.data.sim.female$Date, Death.data.sim.female$freq, col=1, type = "l")
  
  #abline(lm(Death.data.stat.male$freq ~ Death.data.stat.male$Date, data = Death.data.stat.male), col=2)
  #abline(lm(Death.data.stat.female$freq ~ Death.data.stat.female$Date, data = Death.data.stat.female), col=3)
  
  #abline(lm(Death.data.sim.male$freq ~ Death.data.sim.male$Date, data = Death.data.sim.male), col=6)
  #abline(lm(Death.data.sim.female$freq ~ Death.data.sim.female$Date, data = Death.data.sim.female), col=1)
}

plotOB <- function(Birth.data, title) {
  
  title <- "Test"
  
  sourceSummary(Birth.data)

  Birth.data.sim <- Birth.data[which(Birth.data$Source == "SIM" & Birth.data$CIY == "YES"),]
  Birth.data.stat <- Birth.data[which(Birth.data$Source == "STAT" & Birth.data$CIY == "YES"),]
  
  maxY <- max(Birth.data$freq) * 1.05
  minY <- min(Birth.data$freq) * 0.95
  
  plot(Birth.data.stat$Age, Birth.data.stat$freq, col=3, main = title, ylim = c(minY, maxY),  panel.first = grid())
  points(Birth.data.sim$Age, Birth.data.sim$freq, col=1)
  
  plot(Birth.data$Date, Birth.data$freq)
  
  
}

plotOB.2 <- function(Birth.data, title, age = NULL, date = NULL, yob = NULL, scales = "free_y", ciy = NULL) {
  
  library(ggplot2)
  
  sub <- Birth.data
  
  if(!is.null(age)) {
    sub <- sub[which(sub$Age == age),]
  }
  
  if(!is.null(date)) {
    sub <- sub[which(sub$Date == date),]
  }
  
  if(!is.null(yob)) {
    sub <- sub[which(sub$YOB == yob),]
  }
  
  if(!is.null(ciy)) {
    sub <- sub[which(sub$CIY == ciy),]
  }
  
  aggdata <- aggregate(sub$freq, by=list(sub$Source, sub$NPCIAP, sub$Age, sub$Date, sub$CIY), FUN=sum, na.rm=TRUE)
  colnames(aggdata)[1:6] <- c("Source", "NPCIAP", "Age", "Date", "CIY", "freq")
  
  if(is.null(ciy)) {
    ggplot(aggdata,aes(x=NPCIAP, y=freq, fill=Source)) + 
      geom_bar(position="dodge", stat = "identity") +
      facet_wrap(~as.factor(aggdata$Age) + as.factor(aggdata$CIY), ncol=10, scales = scales) + 
      theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
      labs(title = title)
  } else {
    ggplot(aggdata,aes(x=NPCIAP, y=freq, fill=Source)) + 
      geom_bar(position="dodge", stat = "identity") +
      facet_wrap(~as.factor(aggdata$Age), ncol=5, scales = scales) + 
      theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
      labs(title = title)
  }
}

plotMB <- function(mb.data) {

  
  aggdata <- aggregate(mb.data$freq, by=list(mb.data$Source, mb.data$Date, mb.data$NCIY), FUN=sum, na.rm=TRUE)
  
  sel <- aggdata
  colnames(sel)[1:4] <- c("Source", "Date", "NCIY", "freq")

  maxY <- max(sel$Date)
  minY <- min(sel$Date)
  
  maxC <- max(sel$NCIY)
  minC <- min(sel$NCIY)
  
  
  for(y in minY:maxY) {
    for(c in minC:maxC) {
      for(s in c("STAT", "SIM")) {
      
        if(dim(sel[sel$Date == y & sel$NCIY == c & sel$Source == s, ])[1] == 0) {
          r <- data.frame(s,y,c,0)
          names(r) <- c("Source", "Date", "NCIY", "freq")
          sel <- rbind(sel,r)
        }
        
      }
    }
  }

  sel <- sel[order(sel$Date),]
  sel$freq <- round(sel$freq)
  
  cols <- rainbow(8)
  
  colour <- 1
  selA <- sel[which(sel$Source == "SIM" & sel$NCIY == 1), ]
  plot(selA$Date, selA$freq, col = cols[colour], type = "l", ylim = c(0, max(sel$freq) *1.05))
  colour <- colour+1
  
  selA <- sel[which(sel$Source == "STAT" & sel$NCIY == 1), ]
  points(selA$Date, selA$freq, col = cols[colour], type = "l")
  colour <- colour+1
  
  selA <- sel[which(sel$Source == "SIM" & sel$NCIY == 2), ]
  points(selA$Date, selA$freq, col = cols[colour], type = "l")
  colour <- colour+1
  
  selA <- sel[which(sel$Source == "STAT" & sel$NCIY == 2), ]
  points(selA$Date, selA$freq, col = cols[colour], type = "l")
  colour <- colour+1
  
  selA <- sel[which(sel$Source == "SIM" & sel$NCIY == 3), ]
  points(selA$Date, selA$freq, col = cols[colour], type = "l")
  colour <- colour+1
  
  selA <- sel[which(sel$Source == "STAT" & sel$NCIY == 3), ]
  points(selA$Date, selA$freq, col = cols[colour], type = "l")
  colour <- colour+1
  
  selA <- sel[which(sel$Source == "SIM" & sel$NCIY == 4), ]
  points(selA$Date, selA$freq, col = cols[colour], type = "l")
  colour <- colour+1
  
  selA <- sel[which(sel$Source == "STAT" & sel$NCIY == 4), ]
  points(selA$Date, selA$freq, col = cols[colour], type = "l")
  colour <- colour+1
  
  legend("topright", inset = 0.01, title = "Number of children in Year", c("Sim - 1", "Stat - 1", "Sim - 2", "Stat - 2", "Sim - 3", "Stat - 3", "Sim - 4", "Stat - 4"), 
         col = c(cols), lty = c(1,1,1,1,1,1,1,1), cex=0.8, y.intersp=c(0.8), box.lty=0)
  
  colour <- 3
  selA <- sel[which(sel$Source == "SIM" & sel$NCIY == 2), ]
  plot(selA$Date, selA$freq, col = cols[colour], type = "l", ylim = c(0, max(sel[sel$NCIY == 2, ]$freq) *1.05))
  colour <- colour+1
  
  selA <- sel[which(sel$Source == "STAT" & sel$NCIY == 2), ]
  points(selA$Date, selA$freq, col = cols[colour], type = "l")
  colour <- colour+1
  
  selA <- sel[which(sel$Source == "SIM" & sel$NCIY == 3), ]
  points(selA$Date, selA$freq, col = cols[colour], type = "l")
  colour <- colour+1
  
  selA <- sel[which(sel$Source == "STAT" & sel$NCIY == 3), ]
  points(selA$Date, selA$freq, col = cols[colour], type = "l")
  colour <- colour+1
  
  selA <- sel[which(sel$Source == "SIM" & sel$NCIY == 4), ]
  points(selA$Date, selA$freq, col = cols[colour], type = "l")
  colour <- colour+1
  
  selA <- sel[which(sel$Source == "STAT" & sel$NCIY == 4), ]
  points(selA$Date, selA$freq, col = cols[colour], type = "l")
  colour <- colour+1
  
  colour <- 5
  selA <- sel[which(sel$Source == "SIM" & sel$NCIY == 3), ]
  plot(selA$Date, selA$freq, col = cols[colour], ylim = c(0, max(sel[sel$NCIY == 3, ]$freq) *1.05), type = "l")
  colour <- colour+1
  
  selA <- sel[which(sel$Source == "STAT" & sel$NCIY == 3), ]
  points(selA$Date, selA$freq, col = cols[colour], type = "l")
  colour <- colour+1
  
  selA <- sel[which(sel$Source == "SIM" & sel$NCIY == 4), ]
  points(selA$Date, selA$freq, col = cols[colour], type = "l")
  colour <- colour+1
  
  selA <- sel[which(sel$Source == "STAT" & sel$NCIY == 4), ]
  points(selA$Date, selA$freq, col = cols[colour], type = "l")
  colour <- colour+1


}

plotPart <- function(part.data, title, date = NULL, disc = TRUE, scales = "free_y") {
  
  library(ggplot2)
  #sub <- part.data[which(part.data$Age >= age.min & part.data$Age < age.max & part.data$YOB == yob),]
  
  if(!is.null(date)) {
    sub <- part.data[which(part.data$Date == date),]
  } else {
    sub <- part.data
  }
  
  if(disc) {
  
  sub = within(sub, {
    Age.disc =  ifelse(15 <= Age & Age <= 19, 
                       "15to19",
                       ifelse(20 <= Age & Age <= 24, 
                              "20to24",
                              ifelse(25 <= Age & Age <= 29, 
                                     "25to29",
                                     ifelse(30 <= Age & Age <= 34, 
                                            "30to34",
                                            ifelse(35 <= Age & Age <= 39, 
                                                   "35to39",
                                                   ifelse(40 <= Age & Age <= 44, 
                                                          "40to44",
                                                          ifelse(45 <= Age & Age <= 49, 
                                                                 "45to49",
                                                                 ifelse(50 <= Age & Age <= 54, 
                                                                        "50to54",
                                                                        "55+"
                                                                 ))))))))
  })
  
  sub$Age.disc <- as.factor(sub$Age.disc)
  
  aggdata <- aggregate(sub$freq, by=list(sub$Source, sub$NPA, sub$Age.disc), FUN=sum, na.rm=TRUE)
  colnames(aggdata)[1:4] <- c("Source", "NPA", "Age.disc", "freq")
  
  ggplot(aggdata,aes(x=NPA, y=freq, fill=Source)) + 
    geom_bar(position="dodge", stat = "identity") +
    facet_wrap(~as.factor(aggdata$Age.disc), ncol=2, scales = scales) + 
    theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
    labs(title = title)
  
  } else {
    
    aggdata <- aggregate(sub$freq, by=list(sub$Source, sub$NPA, sub$Age), FUN=sum, na.rm=TRUE)
    colnames(aggdata)[1:4] <- c("Source", "NPA", "Age", "freq")
    
    ggplot(aggdata,aes(x=NPA, y=freq, fill=Source)) + 
      geom_bar(position="dodge", stat = "identity") +
      facet_wrap(~as.factor(aggdata$Age), ncol=5, scales = scales) + 
      theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
      labs(title = title)
  }
  
}

plotSep <- function(Part.data, title, date = NULL) {
 
  library(ggplot2)
  
  sub <- Part.data
  
  if(!is.null(date)) {
    sub <- sub[which(sub$Date == date),]
  }
  
  aggdata <- aggregate(sub$freq, by=list(sub$Source, sub$NCIP, sub$Date), FUN=sum, na.rm=TRUE)
  colnames(aggdata)[1:4] <- c("Source", "NCIP", "Date", "freq")
  
  ggplot(aggdata,aes(x=NCIP, y=freq, fill=Source)) + 
    geom_bar(position="dodge", stat = "identity") +
    facet_wrap(~as.factor(aggdata$Date), ncol=8, scales = "fixed") + 
    theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
    labs(title = title) 
  
}

plotSep.single <- function(Part.data, title, date = NULL) {
  
  library(ggplot2)
  
  sub <- Part.data
  
  if(!is.null(date)) {
    sub <- sub[which(sub$Date == date),]
  }
  
  aggdata <- aggregate(sub$freq, by=list(sub$Source, sub$NCIP, sub$Separated), FUN=sum, na.rm=TRUE)
  colnames(aggdata)[1:4] <- c("Source", "NCIP", "Separated", "freq")
  
  ggplot(aggdata,aes(x=NCIP, y=freq, fill=Source)) + 
    geom_bar(position="dodge", stat = "identity") +
    facet_wrap(~as.factor(aggdata$Separated), ncol=2, scales = "free_y") + 
    theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
    labs(title = title) 
  
}
