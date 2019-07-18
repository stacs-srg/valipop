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