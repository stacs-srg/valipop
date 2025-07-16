#
# valipop - <https://github.com/stacs-srg/valipop>
# Copyright © 2025 Systems Research Group, University of St Andrews (graham.kirby@st-andrews.ac.uk)
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program. If not, see <http://www.gnu.org/licenses/>.
#

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
