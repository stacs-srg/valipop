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

  data_id_sorted <- data_id[order(data_id$idvar, data_id$NPA, data_id$Age), ]

  return(data_id_sorted)
}

bin2dec <- function(binaryvector) {
  sum(2^(which(rev(binaryvector) == TRUE) - 1))
}
