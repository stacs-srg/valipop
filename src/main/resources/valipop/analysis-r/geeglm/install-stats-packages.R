options(repos = c(CRAN = "https://cran.rstudio.org"))
setwd(".")

install.packages("RCurl")

MRSeaPath <- tempfile()
download.file(
    "https://github.com/lindesaysh/MRSea/releases/download/v1.6/MRSea_1.6.tar.gz",
    MRSeaPath,
    "libcurl"
)
install.packages(MRSeaPath, repos=NULL)

install.packages("MASS")
install.packages("gridExtra")
install.packages("geepack")
install.packages("ggplot2")
install.packages("mgcv")
install.packages("knitr")
install.packages("markdown")
