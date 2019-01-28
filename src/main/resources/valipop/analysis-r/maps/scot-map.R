library(rgdal)
data.shape<-readOGR(dsn="BoundaryData/scotland_pca_1991.shp")
plot(data.shape)
plot(data.shape[which(data.shape$label == "G8"),])
data.shape$label


library(rgdal)
data.shape<-readOGR(dsn="DS_10283_2409/Scotland_boundary/Scotland boundary.shp")
plot(data.shape)
