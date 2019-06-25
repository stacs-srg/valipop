getSubDF <- function(seed, summaryDF, rf.min = NA, rf.max = NA, prf.min = NA, prf.max = NA) {
  sub <- summaryDF[which(summaryDF$seed == seed),]
  
  if(!is.na(rf.min)) {
    sub <- sub[which(sub$rf >= rf.min),]
  }
  
  if(!is.na(rf.max)) {
    sub <- sub[which(sub$rf <= rf.max),]
  }
  
  if(!is.na(prf.min)) {
    sub <- sub[which(sub$prf >= prf.min),]
  }
  
  if(!is.na(prf.max)) {
    sub <- sub[which(sub$prf <= prf.max),]
  }
  
  return(sub)
}

labelPlot2D <- function(seed, summaryDF, rf.min = NA, rf.max = NA, prf.min = NA, prf.max = NA, detail = NA) {
  
  sub <- getSubDF(seed, summaryDF, rf.min, rf.max, prf.min, prf.max)
  
  library('ggplot2')
  if(is.na(detail)) {
    return(ggplot() + 
             geom_label(data = sub, aes(prf, rf, label = paste(pass.rate, count, sep = "\n"), fill = pass.rate, colour = count), size = 2.5) +
             scale_colour_gradient(low = "red", high = "white") +
             scale_fill_gradient(low = "grey", high = "green"))
  } else {
    return(ggplot() + 
             geom_label(data = sub, aes(prf, rf, label = paste(pass.rate, count, prf, rf, sep = "\n"), fill = pass.rate, colour = count), size = 2.5) +
             scale_colour_gradient(low = "red", high = "white") +
             scale_fill_gradient(low = "grey", high = "green"))
  }
  
}

plot3D <- function(seed, summaryDF, rf.min = NA, rf.max = NA, prf.min = NA, prf.max = NA) {
  
  sub <- getSubDF(seed, summaryDF, rf.min, rf.max, prf.min, prf.max)
  
  library(plotly)
  plot_ly() %>%
    add_trace(data = sub, x = sub$prf, y = sub$rf, z = sub$pass.rate, type="mesh3d", opacity=0.5)  %>%
    layout(
      title = "3D Exploration Plot",
      scene = list(
        xaxis = list(title = "prf"),
        yaxis = list(title = "rf"),
        zaxis = list(title = "pass rate")
      ))
}
  

labelPlot2D(20000, summary)
plot3D(20000, summary)
labelPlot2D(15625, summary, rf.min = 0.3390, rf.max = 0.3396, prf.max = 0.005, detail = TRUE)

plot3D(15625, summary, rf.min = 0.3390, rf.max = 0.3396, prf.max = 0.005)
labelPlot2D(125000, summary)

df.by.date <- df.all[which(df.all$Reason == "batch72-fs"),]
df.by.date <- df.by.date[order(df.by.date$Start.Time.Date),]

ggplot(df.by.date) +
  geom_point(aes(Start.Time.Date, Peak.Memory.Usage..MB.))
