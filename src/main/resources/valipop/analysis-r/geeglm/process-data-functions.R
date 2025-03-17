
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
