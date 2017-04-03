createReport <- function(resultsDirPath) {
  
  path <<- resultsDirPath
  rmarkdown::render("population-report-generator.Rmd", output_dir = resultsDirPath)
  
}