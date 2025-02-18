run_dir_path <- commandArgs(TRUE)[1]
max_birthing_age <- as.integer(commandArgs(TRUE)[2])

death <- clean_death_data(
  read_in_data(paste(run_dir_path, "/tables/death-CT.csv", sep = ""))
)

mbirth <- clean_mb_data(
  read_in_data(paste(run_dir_path, "/tables/mb-CT.csv", sep = "")),
  max_birthing_age,
  round = TRUE
)

obirth <- clean_ob_data(
  read_in_data(paste(run_dir_path, "/tables/ob-CT.csv", sep = "")),
  max_birthing_age
)

part <- clean_part_data(
  read_in_data(paste(run_dir_path, "/tables/part-CT.csv", sep = "")),
  round = TRUE
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
