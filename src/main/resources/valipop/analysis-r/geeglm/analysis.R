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

run_dir_path <- commandArgs(TRUE)[1]
max_birthing_age <- as.integer(commandArgs(TRUE)[2])
t0 <- commandArgs(TRUE)[3]
tE <- commandArgs(TRUE)[4]

# Convert date strings to years for the clean functions
t0_year <- as.integer(substr(t0, 1, 4))
tE_year <- as.integer(substr(tE, 1, 4))

death <- clean_death_data(
  read_in_data(paste(run_dir_path, "/tables/death-contingency-table.csv", sep = "")),
  round = TRUE,
  start = t0_year,
  end = tE_year
)

mbirth <- clean_mb_data(
  read_in_data(paste(run_dir_path, "/tables/multiple-birth-contingency-table.csv", sep = "")),
  max_birthing_age,
  round = TRUE,
  start = t0_year,
  end = tE_year
)

obirth <- clean_ob_data(
  read_in_data(paste(run_dir_path, "/tables/birth-contingency-table.csv", sep = "")),
  max_birthing_age,
  round = TRUE,
  start = t0_year,
  end = tE_year
)

part <- clean_part_data(
  read_in_data(paste(run_dir_path, "/tables/partnership-contingency-table.csv", sep = "")),
  round = TRUE,
  start = t0_year,
  end = tE_year
)

death_ids <- add_cohort_ids_death(death)
obirth_ids <- add_cohort_ids_ob(obirth)
mbirth_ids <- add_cohort_ids_mb(mbirth)
part_ids <- add_cohort_ids_part(part)

death_geeglm <- try(death_sat_geeglm(death_ids))
ob_geeglm <- try(ob_sat_geeglm(obirth_ids))
mb_geeglm <- try(mb_sat_geeglm(mbirth_ids))
part_geeglm <- try(part_sat_geeglm(part_ids))

print(summary(death_geeglm))
print(summary(ob_geeglm))
print(summary(mb_geeglm))
print(summary(part_geeglm))
