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
      expr = run_geeglm(freq ~ Date * PartnerAge * Age * Source, in_data),
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

############################################################################################

read_in_data <- function(path) {
  data <- read.csv(path, sep = ",", header = TRUE)

  return(data)
}

clean_data <- function(dirty_data, round = TRUE, start_year, end_year) {
  data <- dirty_data
  if (round) {
    data <- dirty_data[which(dirty_data$freq > 0.5), ]
    data$freq <- round(data$freq)
  }
  data <- data[which(data$Date < end_year), ]
  data <- data[which(data$Date > start_year), ]

  return(data)
}

clean_death_data <- function(dirty_data, round = TRUE, start_year, end_year) {
  return(clean_data(dirty_data, round, start_year = start_year, end_year = end_year))
}

# TODO Why is max mother's age a parameter but not minimum?
# TODO Why ignore such outliers in the real data?
clean_birth_data <- function(dirty_data, max_birthing_age, round = TRUE, start_year, end_year) {
  data <- clean_data(dirty_data, round, start_year = start_year, end_year = end_year)
  data <- data[which(data$Age >= 15), ]
  data <- data[which(data$Age <= max_birthing_age), ]
  return(data)
}

clean_multiple_birth_data <- function(dirty_data, max_birthing_age, round = TRUE, start_year, end_year)  {
  clean_data <- clean_birth_data(dirty_data, max_birthing_age, round, start_year = start_year, end_year = end_year)
  clean_data <- clean_data[which(clean_data$NCIY != "0"), ]

  return(clean_data)
}

clean_partnership_data <- function(dirty_data, round = TRUE, start_year, end_year) {
  data <- clean_data(dirty_data, round = round, start_year = start_year, end_year = end_year)
  data <- data[which(data$PartnerAge != "na"), ]
  data$PartnerAge <- droplevels(factor(data$PartnerAge))
  return(data)
}

############################################################################################

add_cohort_ids_ob <- function(in_data) {

  e <- min(in_data$YOB) # nolint: object_usage_linter.
  l <- max(in_data$YOB) # nolint: object_usage_linter.

  id_data <- in_data

  id_data <- within(id_data, {
    idvar <- # nolint: object_usage_linter.
      ifelse(CIY == "true",
             ifelse(Source == "SIM",
                    ifelse(NPCIAP == "0",
                           (YOB - e) + bin2dec(c(TRUE, TRUE, FALSE, FALSE, FALSE)) *
                             (l - e + 1), #SIM-0
                           ifelse(NPCIAP == "1",
                                  (YOB - e) + bin2dec(c(TRUE, TRUE, FALSE, FALSE, TRUE)) *
                                    (l - e + 1), #SIM-1
                                  ifelse(NPCIAP == "2",
                                         (YOB - e) + bin2dec(c(TRUE, TRUE, FALSE, TRUE, FALSE)) *
                                           (l - e + 1), #SIM-2
                                         ifelse(NPCIAP == "3",
                                                (YOB - e) + bin2dec(c(TRUE, TRUE, FALSE, TRUE, TRUE)) *
                                                  (l - e + 1), #SIM-3
                                                ifelse(NPCIAP == "4",
                                                       (YOB - e) + bin2dec(c(TRUE, TRUE, TRUE, FALSE, FALSE)) *
                                                         (l - e + 1), #SIM-4
                                                       ifelse(NPCIAP == "5",
                                                              (YOB - e) + bin2dec(c(TRUE, TRUE, TRUE, FALSE, TRUE)) *
                                                                (l - e + 1), #SIM-5+
                                                              (YOB - e) + bin2dec(c(TRUE, TRUE, TRUE, TRUE, FALSE)) *
                                                                (l - e + 1) #SIM-0+
                                                       )
                                                )
                                         )
                                  )
                           )
                    ),
                    ifelse(NPCIAP == "0",
                           (YOB - e) + bin2dec(c(FALSE, TRUE, FALSE, FALSE, FALSE)) *
                             (l - e + 1), #STAT-0
                           ifelse(NPCIAP == "1",
                                  (YOB - e) + bin2dec(c(FALSE, TRUE, FALSE, FALSE, TRUE)) *
                                    (l - e + 1), #STAT-1
                                  ifelse(NPCIAP == "2",
                                         (YOB - e) + bin2dec(c(FALSE, TRUE, FALSE, TRUE, FALSE)) *
                                           (l - e + 1), #STAT-2
                                         ifelse(NPCIAP == "3",
                                                (YOB - e) + bin2dec(c(FALSE, TRUE, FALSE, TRUE, TRUE)) *
                                                  (l - e + 1), #STAT-3
                                                ifelse(NPCIAP == "4",
                                                       (YOB - e) + bin2dec(c(FALSE, TRUE, TRUE, FALSE, FALSE)) *
                                                         (l - e + 1), #STAT-4
                                                       ifelse(NPCIAP == "5",
                                                              (YOB - e) + bin2dec(c(FALSE, TRUE, TRUE, FALSE, TRUE)) *
                                                                (l - e + 1), #STAT-5+
                                                              (YOB - e) + bin2dec(c(FALSE, TRUE, TRUE, TRUE, FALSE)) *
                                                                (l - e + 1) #STAT-0+
                                                       )
                                                )
                                         )
                                  )
                           )
                    )
             ),
             ifelse(Source == "SIM",
                    ifelse(NPCIAP == "0",
                           (YOB - e) + bin2dec(c(TRUE, FALSE, FALSE, FALSE, FALSE)) *
                             (l - e + 1), #SIM-0
                           ifelse(NPCIAP == "1",
                                  (YOB - e) + bin2dec(c(TRUE, FALSE, FALSE, FALSE, TRUE)) *
                                    (l - e + 1), #SIM-1
                                  ifelse(NPCIAP == "2",
                                         (YOB - e) + bin2dec(c(TRUE, FALSE, FALSE, TRUE, FALSE)) *
                                           (l - e + 1), #SIM-2
                                         ifelse(NPCIAP == "3",
                                                (YOB - e) + bin2dec(c(TRUE, FALSE, FALSE, TRUE, TRUE)) *
                                                  (l - e + 1), #SIM-3
                                                ifelse(NPCIAP == "4",
                                                       (YOB - e) + bin2dec(c(TRUE, FALSE, TRUE, FALSE, FALSE)) *
                                                         (l - e + 1), #SIM-4
                                                       ifelse(NPCIAP == "5",
                                                              (YOB - e) + bin2dec(c(TRUE, FALSE, TRUE, FALSE, TRUE)) *
                                                                (l - e + 1), #SIM-5+
                                                              (YOB - e) + bin2dec(c(TRUE, FALSE, TRUE, TRUE, FALSE)) *
                                                                (l - e + 1) #SIM-0+
                                                       )
                                                )
                                         )
                                  )
                           )
                    ),
                    ifelse(NPCIAP == "0",
                           (YOB - e) + bin2dec(c(FALSE, FALSE, FALSE, FALSE, FALSE)) *
                             (l - e + 1), #STAT-0
                           ifelse(NPCIAP == "1",
                                  (YOB - e) + bin2dec(c(FALSE, FALSE, FALSE, FALSE, TRUE)) *
                                    (l - e + 1), #STAT-1
                                  ifelse(NPCIAP == "2",
                                         (YOB - e) + bin2dec(c(FALSE, FALSE, FALSE, TRUE, FALSE)) *
                                           (l - e + 1), #STAT-2
                                         ifelse(NPCIAP == "3",
                                                (YOB - e) + bin2dec(c(FALSE, FALSE, FALSE, TRUE, TRUE)) *
                                                  (l - e + 1), #STAT-3
                                                ifelse(NPCIAP == "4",
                                                       (YOB - e) + bin2dec(c(FALSE, FALSE, TRUE, FALSE, FALSE)) *
                                                         (l - e + 1), #STAT-4
                                                       ifelse(NPCIAP == "5",
                                                              (YOB - e) + bin2dec(c(FALSE, FALSE, TRUE, FALSE, TRUE)) *
                                                                (l - e + 1), #STAT-5+
                                                              (YOB - e) + bin2dec(c(FALSE, FALSE, TRUE, TRUE, FALSE)) *
                                                                (l - e + 1) #STAT-0+
                                                       )
                                                )
                                         )
                                  )
                           )
                    )
             )
      )
  })

  data_id_sorted <- id_data[order(id_data$idvar, id_data$Age, id_data$CIY), ]

  return(data_id_sorted)
}

add_cohort_ids_death <- function(in_data) {
  e <- min(in_data$YOB) # nolint: object_usage_linter.

  male_l <- max( # nolint: object_usage_linter.
    in_data[which(in_data$Sex == "M"), ]$YOB
  )
  female_l <- max( # nolint: object_usage_linter.
    in_data[which(in_data$Sex == "F"), ]$YOB
  )

  data_id <- within(in_data, {
    idvar <- ifelse(Source == "SIM",  # nolint: object_usage_linter.
                    ifelse(Sex == "M",
                           (YOB - e) + bin2dec(c(TRUE, TRUE)) * (male_l - e + 1),
                           (YOB - e) + bin2dec(c(TRUE, FALSE)) * (female_l - e + 1)
                    ),
                    ifelse(Sex == "M",
                           (YOB - e) + bin2dec(c(FALSE, TRUE)) * (male_l - e + 1),
                           (YOB - e) + bin2dec(c(FALSE, FALSE)) * (female_l - e + 1)
                    )
    )
  })

  data_id_sorted <- data_id[order(data_id$idvar, data_id$Age, data_id$Died), ]

  return(data_id_sorted)
}

add_cohort_ids_mb <- function(in_data) {
  e <- min(in_data$YOB) # nolint: object_usage_linter.
  l <- max(in_data$YOB) # nolint: object_usage_linter.
  data_id <- within(in_data, {
    idvar <- ifelse(Source == "SIM",  # nolint: object_usage_linter.
                    (YOB - e) + bin2dec(c(TRUE)) * (l - e + 1),
                    (YOB - e) + bin2dec(c(FALSE)) * (l - e + 1)
    )
  })

  data_id_sorted <- data_id[order(data_id$idvar, data_id$NCIY, data_id$Age), ]

  return(data_id_sorted)
}

add_cohort_ids_part <- function(in_data) {
  e <- min(in_data$YOB) # nolint: object_usage_linter.
  l <- max(in_data$YOB) # nolint: object_usage_linter.

  data_id <- within(in_data, {
    idvar <- ifelse(Source == "SIM", # nolint: object_usage_linter.
                    (YOB - e) + bin2dec(c(TRUE)) * (l - e + 1),
                    (YOB - e) + bin2dec(c(FALSE)) * (l - e + 1)
    )
  })

  data_id_sorted <- data_id[order(data_id$idvar, data_id$PartnerAge, data_id$Age), ]

  return(data_id_sorted)
}

bin2dec <- function(binaryvector) {
  sum(2^(which(rev(binaryvector) == TRUE) - 1))
}

############################################################################################

run_dir_path <- commandArgs(TRUE)[1]
max_mother_birth_age <- as.integer(commandArgs(TRUE)[2])
start_year <- as.integer(commandArgs(TRUE)[3])
end_year <- as.integer(commandArgs(TRUE)[4])

death_data <- clean_death_data(
  read_in_data(paste(run_dir_path, "/death-contingency-table.csv", sep = "")),
  start_year = start_year,
  end_year = end_year
)

multiple_birth_data <- clean_multiple_birth_data(
  read_in_data(paste(run_dir_path, "/multiple-birth-contingency-table.csv", sep = "")),
  max_mother_birth_age,
  round = TRUE,
  start_year = start_year,
  end_year = end_year
)

birth_data <- clean_birth_data(
  read_in_data(paste(run_dir_path, "/birth-contingency-table.csv", sep = "")),
  max_mother_birth_age,
  start_year = start_year,
  end_year = end_year
)

partnership_data <- clean_partnership_data(
  read_in_data(paste(run_dir_path, "/partnership-contingency-table.csv", sep = "")),
  round = TRUE,
  start_year = start_year,
  end_year = end_year
)

death_ids <- add_cohort_ids_death(death_data)
birth_ids <- add_cohort_ids_ob(birth_data)
multiple_birth_ids <- add_cohort_ids_mb(multiple_birth_data)
partnership_ids <- add_cohort_ids_part(partnership_data)

death_analysis <- try(death_sat_geeglm(death_ids))
birth_analysis <- try(ob_sat_geeglm(birth_ids))
multiple_birth_analysis <- try(mb_sat_geeglm(multiple_birth_ids))
partnership_analysis <- try(part_sat_geeglm(partnership_ids))

cat("\n--------------- Death Statistics Analysis ---------------")
print(summary(death_analysis))

cat("\n--------------- Birth Statistics Analysis ---------------")
print(summary(birth_analysis))

cat("\n--------------- Multiple Birth Statistics Analysis ---------------")
print(summary(multiple_birth_analysis))

cat("\n--------------- Partnership Statistics Analysis ---------------")
print(summary(partnership_analysis))
