source("paper/code/FileFunctions.R")
source("paper/code/rfs-discovery/explorationPlots.R")

checkPlots <- function(full_df) {

    library(ggplot2)
    
    print(ggplot(full_df) +
      geom_point(aes(Total.Pop, Peak.Memory.Usage..MB.), 
                 col = round(3*full_df$Proportional.Recovery.Factor)+1,
                 shape = round(3*full_df$Recovery.Factor)+1))
    
    print(ggplot(full_df) +
      geom_point(aes(Seed.Pop.Size, Total.Pop), 
                 col = round(3*full_df$Proportional.Recovery.Factor)+1,
                 shape = round(3*full_df$Recovery.Factor)+1))
    
    print(ggplot(full_df) +
            geom_point(aes(Seed.Pop.Size, Peak.Memory.Usage..MB.), 
                       col = round(3*full_df$Proportional.Recovery.Factor)+1,
                       shape = round(3*full_df$Recovery.Factor)+1))

}

fs3D <- function(seed, summary_df, rf.min = NA, rf.max = NA, prf.min = NA, prf.max = NA, type = "mesh3d") {
  
  sub <- getSubDF(seed, summary_df, rf.min, rf.max, prf.min, prf.max)
  
  library(plotly)
  plot_ly() %>%
    add_trace(data = sub, x = sub$prf, y = sub$rf, z = sub$mean, type=type, opacity=0.5)  %>%
    add_trace(data = sub, x = sub$prf, y = sub$rf, z = sub$min, type=type, opacity=0.5)  %>%
    layout(
      title = paste("Cluster experiments - seed =", seed),
      scene = list(
        xaxis = list(title = "prf"),
        yaxis = list(title = "rf"),
        zaxis = list(title = "v")
      ))
}

fs3Dbf <- function(seed, summary_df, rf.min = NA, rf.max = NA, prf.min = NA, prf.max = NA, type = "mesh3d") {
  
  sub <- getSubDF(seed, summary_df, rf.min, rf.max, prf.min, prf.max)
  
  bf._1.0 <- sub[which(sub$bf == -1.0), ]
  bf._0.75 <- sub[which(sub$bf == -0.75), ]
  bf._0.5 <- sub[which(sub$bf == -0.5), ]
  bf._0.25 <- sub[which(sub$bf == -0.25), ]
  bf.0.0 <- sub[which(sub$bf == 0.0), ]
  bf.0.1 <- sub[which(sub$bf == 0.1), ]
  bf.0.2 <- sub[which(sub$bf == 0.2), ]
  bf.0.25 <- sub[which(sub$bf == 0.25), ]
  bf.0.3 <- sub[which(sub$bf == 0.3), ]
  bf.0.4 <- sub[which(sub$bf == 0.4), ]
  bf.0.5 <- sub[which(sub$bf == 0.5), ]
  bf.0.6 <- sub[which(sub$bf == 0.6), ]
  bf.0.7 <- sub[which(sub$bf == 0.7), ]
  bf.0.75 <- sub[which(sub$bf == 0.75), ]
  bf.0.8 <- sub[which(sub$bf == 0.8), ]
  bf.0.9 <- sub[which(sub$bf == 0.9), ]
  bf.1.0 <- sub[which(sub$bf == 1.0), ]
  
  library(plotly)
  plot_ly() %>%
    add_trace(data = bf._1.0, x = bf._1.0$prf, y = bf._1.0$rf, z = bf._1.0$min, type=type, opacity=0.5, name = "-1.0")  %>%
    add_trace(data = bf._0.75, x = bf._0.75$prf, y = bf._0.75$rf, z = bf._0.75$min, type=type, opacity=0.5, name = "-0.75")  %>%
    add_trace(data = bf._0.5, x = bf._0.5$prf, y = bf._0.5$rf, z = bf._0.5$min, type=type, opacity=0.5, name = "-0.5")  %>%
    add_trace(data = bf._0.25, x = bf._0.25$prf, y = bf._0.25$rf, z = bf._0.25$min, type=type, opacity=0.25, name = "-0.25")  %>%
    add_trace(data = bf.0.0, x = bf.0.0$prf, y = bf.0.0$rf, z = bf.0.0$min, type=type, opacity=0.5, name = "0.0")  %>%
    add_trace(data = bf.0.1, x = bf.0.1$prf, y = bf.0.1$rf, z = bf.0.1$min, type=type, opacity=0.5, name = "0.1")  %>%
    add_trace(data = bf.0.2, x = bf.0.2$prf, y = bf.0.2$rf, z = bf.0.2$min, type=type, opacity=0.5, name = "0.2")  %>%
    add_trace(data = bf.0.25, x = bf.0.25$prf, y = bf.0.25$rf, z = bf.0.25$min, type=type, opacity=0.25, name = "0.25")  %>%
    add_trace(data = bf.0.3, x = bf.0.3$prf, y = bf.0.3$rf, z = bf.0.3$min, type=type, opacity=0.5, name = "0.3")  %>%
    add_trace(data = bf.0.4, x = bf.0.4$prf, y = bf.0.4$rf, z = bf.0.4$min, type=type, opacity=0.5, name = "0.4")  %>%
    add_trace(data = bf.0.5, x = bf.0.5$prf, y = bf.0.5$rf, z = bf.0.5$min, type=type, opacity=0.5, name = "0.5")  %>%
    add_trace(data = bf.0.6, x = bf.0.6$prf, y = bf.0.6$rf, z = bf.0.6$min, type=type, opacity=0.5, name = "0.6")  %>%
    add_trace(data = bf.0.7, x = bf.0.7$prf, y = bf.0.7$rf, z = bf.0.7$min, type=type, opacity=0.5, name = "0.7")  %>%
    add_trace(data = bf.0.75, x = bf.0.75$prf, y = bf.0.75$rf, z = bf.0.75$min, type=type, opacity=0.5, name = "0.75")  %>%
    add_trace(data = bf.0.8, x = bf.0.8$prf, y = bf.0.8$rf, z = bf.0.8$min, type=type, opacity=0.5, name = "0.8")  %>%
    add_trace(data = bf.0.9, x = bf.0.9$prf, y = bf.0.9$rf, z = bf.0.9$min, type=type, opacity=0.5, name = "0.9")  %>%
    add_trace(data = bf.1.0, x = bf.1.0$prf, y = bf.1.0$rf, z = bf.1.0$min, type=type, opacity=0.5, name = "1.0")  %>%
    layout(
      title = paste("Cluster experiments - seed =", seed),
      scene = list(
        xaxis = list(title = "prf"),
        yaxis = list(title = "rf"),
        zaxis = list(title = "v")
      ),
      showlegend = TRUE
    )
}
