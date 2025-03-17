library(geepack)

death_sat_geeglm <- function(in_data) {
  # "ar2" not supported in geeglm ?
  return(
    run_geeglm(freq ~ Date * Age * Sex * Died * Source, in_data, constr = "ar1")
  )
}

ob_sat_geeglm <- function(in_data) {
  return(run_geeglm(freq ~ Date * Age * CIY * Source, in_data))
}

mb_sat_geeglm <- function(in_data) {
  return(run_geeglm(freq ~ Date * Age * NCIY, in_data, constr = "ar1"))
}

part_sat_geeglm <- function(in_data) {
  return(run_geeglm(freq ~ Date * NPA * Age * Source, in_data))
}

run_geeglm <- function(formula, in_data, constr = "ar1") {
  mod <- geeglm(
    formula,
    id = idvar, # nolint: object_usage_linter.
    data = in_data,
    corstr = constr
  )

  return(mod)
}
