
plotCohorts <- function(Death.data, age, title) {
  death <- "NO"

  #label <- "iw:100y - with MB - FACTOR 0.007462401"
  #label <- "iw:NO-SC - MB - F1 - BS + 1 - fix to undercounting of SIM"
  label <- title
  
  Death.data.sim.male <- Death.data[which(Death.data$Age == age & Death.data$Source == "SIM" & Death.data$Sex == "MALE" & Death.data$Died == death ) , ]
  Death.data.sim.female <- Death.data[which(Death.data$Age == age & Death.data$Source == "SIM" & Death.data$Sex == "FEMALE" & Death.data$Died == death ) , ]
  
  Death.data.stat.male <- Death.data[which(Death.data$Age == age & Death.data$Source == "STAT" & Death.data$Sex == "MALE" & Death.data$Died == death ) , ]
  Death.data.stat.female <- Death.data[which(Death.data$Age == age & Death.data$Source == "STAT" & Death.data$Sex == "FEMALE" & Death.data$Died == death ) , ]
  
  maxY <- max(Death.data[which(Death.data$Age == age & Death.data$Died == death) , ]$freq) * 1.05
  minY <- min(Death.data[which(Death.data$Age == age & Death.data$Died == death) , ]$freq) * 0.95
  
  plot(Death.data.stat.male$Date, Death.data.stat.male$freq, col=2, main = label, ylim = c(minY, maxY),  panel.first = grid())
  points(Death.data.stat.female$Date, Death.data.stat.female$freq, col=3)
  points(Death.data.sim.male$Date, Death.data.sim.male$freq, col=6)
  points(Death.data.sim.female$Date, Death.data.sim.female$freq, col=1)
  
  abline(lm(Death.data.stat.male$freq ~ Death.data.stat.male$Date, data = Death.data.stat.male), col=2)
  abline(lm(Death.data.stat.female$freq ~ Death.data.stat.female$Date, data = Death.data.stat.female), col=3)
  
  abline(lm(Death.data.sim.male$freq ~ Death.data.sim.male$Date, data = Death.data.sim.male), col=6)
  abline(lm(Death.data.sim.female$freq ~ Death.data.sim.female$Date, data = Death.data.sim.female), col=1)
}

