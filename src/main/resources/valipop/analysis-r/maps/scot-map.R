library(rgdal)
data.shape<-readOGR(dsn="/Users/tsd4/Downloads/BoundaryData/scotland_pca_1991.shp")
plot(data.shape)
data.shape[which(data.shape$label == "ZE"),]
data.shape$label


library(rgdal)
data.shape<-readOGR(dsn="/Users/tsd4/Downloads/DS_10283_2409/Scotland_boundary/Scotland boundary.shp")
plot(data.shape)
