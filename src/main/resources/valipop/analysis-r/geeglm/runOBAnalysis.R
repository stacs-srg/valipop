runOBAnalysis <- function(pathToRunDir, maxBirthAge, subTitle) {
  library(knitr)
  pathToRunDir <- paste(pathToRunDir, "/", sep = "")
  maxBirthingAge <- maxBirthAge
  subTitle <- subTitle
  
  knitr::opts_chunk$set(fig.height=12, fig.width=16)
  knitr::knit2html(
    "src/main/resources/valipop/analysis-r/geeglm/ob-analysis.Rhtml", 
    output = paste(pathToRunDir, "/ob-analysis.html", sep = "")
  )
}

pathToRunDir <- commandArgs(TRUE)[1]
maxBirthAge <- commandArgs(TRUE)[2]
subTitle <- commandArgs(TRUE)[3]

runOBAnalysis(pathToRunDir, maxBirthAge, subTitle)
