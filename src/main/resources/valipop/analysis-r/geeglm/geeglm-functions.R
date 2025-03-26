library(geepack)

death_sat_geeglm <- function(in_data) {
  # "ar2" not supported in geeglm ?
  return(
    tryCatch(
      expr = run_geeglm(
        freq ~ Date * Age * Sex * Died * Source,
        in_data,
        constr = "ar1"
      ),
      error = function(e) {
        warning("Population size too small for death analysis")
      }
    )
  )
}

ob_sat_geeglm <- function(in_data) {
  return(
    tryCatch(
      expr = run_geeglm(freq ~ Date * Age * CIY * Source, in_data),
      error = function(e) {
        warning("Population size too small for ordered birth analysis")
      }
    )
  )
}

mb_sat_geeglm <- function(in_data) {
  return(
    tryCatch(
      expr = run_geeglm(freq ~ Date * Age * NCIY, in_data, constr = "ar1"),
      error = function(e) {
        warning("Population size too small for multiple birth analysis")
      }
    )
  )
}

part_sat_geeglm <- function(in_data) {
  return(
    tryCatch(
      expr = run_geeglm(freq ~ Date * NPA * Age * Source, in_data),
      error = function(e) {
        warning("Population size too small for partnering analysis")
      }
    )
  )
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
