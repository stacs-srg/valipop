
all <- read.csv("~/Desktop/all.csv")

options(digits=16)

library(data.table)

distances <- all[which(is.na(all$lon)), c("lat", "lon")]
distances <- distances[!(distances$lat %like% "Something"), ]
distances$dist <- as.numeric(as.character(distances$lat))


ggplot(distances) +
  geom_histogram(aes(x = distances$dist), binwidth = 3)

full.locations <- all[which(!is.na(all$lon)), c("lat", "lon")]
full.locations$lat <- as.numeric(as.character(full.locations$lat))
scotlandCenter <- c(-4.6, 56.9)



library(ggmap)
prep <- get_googlemap(
  center = scotlandCenter,
  zoom = 7, 
  maptype = 'roadmap', #also r hybrid/terrain/roadmap/satellite
  scale = 2,
  key = "AIzaSyCFTOXXsFZ4s7J2pskLr91gqx8OyV059Eg")

ggmap(prep, size=c(100, 100), extent="device", darken=0.5, legend="bottom") + 
  geom_point(data = full.locations, aes(x = lon, y = lat, alpha = 0.2))



origin <- c(-2.43492198501177, 55.52654705)
scotlandCenter <- origin
full.locations <- read.csv("~/Desktop/circle.csv")


# ----------------
mixed <- read.csv("~/Desktop/out.csv")

distances <- mixed[which(mixed$test == "dist"), ]

ggplot(distances) +
  geom_histogram(aes(x = distances$target), binwidth = 1)

delta <- mixed[which(mixed$test == "delta"), ]

ggplot(delta) +
  geom_point(aes(x = target, y = result), alpha = 0.05) +
  geom_abline(intercept = 0, slope = 1)


