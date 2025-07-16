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


read_in_data <- function(path) {
  data <- read.csv(path, sep = ",", header = TRUE)

  return(data)
}

clean_data <- function(dirty_data, round = TRUE, start = 1940, end = 2019) {
  data <- dirty_data
  if (round) {
    data <- dirty_data[which(dirty_data$freq > 0.5), ]
    data$freq <- round(data$freq)
  }
  data <- data[which(data$Date < end), ]
  data <- data[which(data$Date > start), ]

  return(data)
}

clean_death_data <- function(
  dirty_data,
  round = TRUE,
  start = 1940,
  end = 2019
) {
  return(clean_data(dirty_data, round, start = start, end = end))
}

clean_ob_data <- function(dirty_data, max_birthing_age, round = TRUE) {
  data <- clean_data(dirty_data, round)
  data <- data[which(data$Age >= 15), ]
  data <- data[which(data$Age <= max_birthing_age), ]
  return(data)
}

clean_mb_data <- function(dirty_data, max_birthing_age, round = TRUE)  {
  clean_data <- clean_ob_data(dirty_data, max_birthing_age, round)
  clean_data <- clean_data[which(clean_data$NCIY != "0"), ]

  return(clean_data)
}

clean_part_data <- function(
  dirty_data,
  round = TRUE,
  start = 1940,
  end = 2019
) {
  data <- clean_data(dirty_data, round = round, start = start, end = end)
  data <- data[which(data$NPA != "na"), ]
  data$NPA <- droplevels(factor(data$NPA))
  return(data)
}
