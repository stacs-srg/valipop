

pra <- read.table("~/Desktop/pra.csv", header = TRUE, sep = ",")


library(plotly)

plot_ly() %>%
  add_trace(data = pra, x = pra$Precision, y = pra$Recall, z = pra$Accuracy, type="scatter3d", opacity=0.5)  %>%
  layout(
    title = "PRA",
    scene = list(
      xaxis = list(title = "Precision"),
      yaxis = list(title = "Recall"),
      zaxis = list(title = "Accuracy")
    ))
