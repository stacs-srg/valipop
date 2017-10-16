runAnalysis <- function(pathToRunDir, maxBirthAge, subTitle) {
  rmarkdown::render(
    "src/main/resources/analysis-r/geeglm/analysis.Rhtml", 
    output_file = pathToRunDir,
    params = list(
      pathToRunDir = pathToRunDir,
      maxBirthingAge = maxBirthingAge,
      subTitle = subTitle)
    )
}


pathToRunDir <- commandArgs(TRUE)[1]
maxBirthAge <- commandArgs(TRUE)[2]
subTitle <- commandArgs(TRUE)[3]

runAnalysis(pathToRunDir, maxBirthAge, subTitle)
