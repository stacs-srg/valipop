outputToFile <- function(jobs, file) {
  write.csv(jobs, file = file, row.names=FALSE, quote=FALSE)
}

cleanWorkspace <- function() {
  rm(list = setdiff(ls(envir = .GlobalEnv), "cleanWorkspace"), envir = .GlobalEnv)
}
