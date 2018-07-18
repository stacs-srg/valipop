source("paper/code/FileFunctions.R")

seed.500k <- fileToSummaryDF("/Volumes/TOSHIBA_EXT/results/batch57-fs/batch57-fs-results-summary.csv")
seed.250k <- fileToSummaryDF("/Volumes/TOSHIBA EXT/pop-runs/batch56-fs/batch56-fs-results-summary.csv")
seed.125k <- fileToSummaryDF("/Volumes/TOSHIBA EXT/pop-runs/batch52-fs/batch52-fs-results-summary.csv")
seed.62500 <- fileToSummaryDF("/Volumes/TOSHIBA EXT/pop-runs/batch55-fs/batch55-fs-results-summary.csv")
seed.31250 <- fileToSummaryDF("/Volumes/TOSHIBA EXT/pop-runs/batch53-fs/batch53-fs-results-summary.csv")
seed.15625 <- fileToSummaryDF("/Volumes/TOSHIBA EXT/pop-runs/batch54-fs/batch54-fs-results-summary.csv", filter = 15625)
seed.7812 <- fileToSummaryDF("/Volumes/TOSHIBA EXT/pop-runs/batch54-fs/batch54-fs-results-summary.csv", filter = 7812)

library(plotly)

op <- 0.2

plot_ly() %>%
  add_trace(data = seed.500k, x = seed.500k$prf, y = seed.500k$rf, z = seed.500k$mean, type="mesh3d", opacity=op, name = "500k")  %>%
  add_trace(data = seed.250k, x = seed.250k$prf, y = seed.250k$rf, z = seed.250k$mean, type="mesh3d", opacity=op, name = "250k")  %>%
  add_trace(data = seed.125k, x = seed.125k$prf, y = seed.125k$rf, z = seed.125k$mean, type="mesh3d", opacity=op, name = "125k")  %>%
  add_trace(data = seed.62500, x = seed.62500$prf, y = seed.62500$rf, z = seed.62500$mean, type="mesh3d", opacity=op, name = "62500")  %>%
  add_trace(data = seed.31250, x = seed.31250$prf, y = seed.31250$rf, z = seed.31250$mean, type="mesh3d", opacity=op, name = "31250")  %>%
  add_trace(data = seed.15625, x = seed.15625$prf, y = seed.15625$rf, z = seed.15625$mean, type="mesh3d", opacity=op, name = "15625")  %>%
  add_trace(data = seed.7812, x = seed.7812$prf, y = seed.7812$rf, z = seed.7812$mean, type="mesh3d", opacity=op, name = "7812")  %>%
  layout(
    showlegend = TRUE,
    title = "Mean V",
    scene = list(
      xaxis = list(title = "prf"),
      yaxis = list(title = "rf"),
      zaxis = list(title = "v")
    ))

plot_ly() %>%
  add_trace(data = seed.500k, x = seed.500k$prf, y = seed.500k$rf, z = seed.500k$min, type="mesh3d", opacity=op, name = "500k")  %>%
  add_trace(data = seed.250k, x = seed.250k$prf, y = seed.250k$rf, z = seed.250k$min, type="mesh3d", opacity=op, name = "250k")  %>%
  add_trace(data = seed.125k, x = seed.125k$prf, y = seed.125k$rf, z = seed.125k$min, type="mesh3d", opacity=op, name = "125k")  %>%
  add_trace(data = seed.62500, x = seed.62500$prf, y = seed.62500$rf, z = seed.62500$min, type="mesh3d", opacity=op, name = "62500")  %>%
  add_trace(data = seed.31250, x = seed.31250$prf, y = seed.31250$rf, z = seed.31250$min, type="mesh3d", opacity=op, name = "31250")  %>%
  add_trace(data = seed.15625, x = seed.15625$prf, y = seed.15625$rf, z = seed.15625$min, type="mesh3d", opacity=op, name = "15625")  %>%
  add_trace(data = seed.7812, x = seed.7812$prf, y = seed.7812$rf, z = seed.7812$min, type="mesh3d", opacity=op, name = "7812")  %>%
  layout(
    showlegend = TRUE,
    title = "Min V",
    scene = list(
      xaxis = list(title = "prf"),
      yaxis = list(title = "rf"),
      zaxis = list(title = "v", range = c(0,0))
    ))


#df <- df[which(df$min == 0),]

df <- seed.500k

library(ggplot2)
ggplot() + geom_label(data = df, aes(prf, rf, label = paste(pass.rate, count, sep = "\n"), fill = pass.rate, colour = count), size = 2.5) +
  scale_colour_gradient(low = "red", high = "white") +
  scale_fill_gradient(low = "grey", high = "green")

library(plotly)
plot_ly() %>%
  add_trace(data = df, x = df$prf, y = df$rf, z = df$mean, type="mesh3d", opacity=0.5)  %>%
  add_trace(data = df, x = df$prf, y = df$rf, z = df$min, type="scatter3d", opacity=0.5)  %>%
  layout(
    title = "Cluster experiments - seed = 250000",
    scene = list(
      xaxis = list(title = "prf"),
      yaxis = list(title = "rf"),
      zaxis = list(title = "v")
    ))


plot_ly() %>%
  add_trace(data = df, x = df$prf, y = df$rf, z = df$pass.rate, type="mesh3d", opacity=0.5)  %>%
  layout(
    title = "Cluster experiments - seed = 250000",
    scene = list(
      xaxis = list(title = "prf"),
      yaxis = list(title = "rf"),
      zaxis = list(title = "v")
    ))



