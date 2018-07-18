# USE LATEST DATA SET
birthRecords <- read.csv("../results/testing-mar-changes/20180228-104105:461/records/birth_records.csv", quote = "", h = T)
deathRecords <- read.csv("../results/testing-mar-changes/20180228-104105:461/records/death_records.csv", quote = "", h = T)
marriageRecords <- read.csv("../results/testing-mar-changes/20180228-104105:461/records/marriage_records.csv", quote = "", h = T)

summary(birthRecords)

julian(as.Date(birthRecords$birth.date, "%d/%m/%Y"))

birthRecords$diff <- julian(as.Date(paste(birthRecords$day.of.parents..marriage, birthRecords$month.of.parents..marriage, birthRecords$year.of.parents..marriage,sep="/"), "%d/%m/%Y")) - julian(as.Date(birthRecords$birth.date, "%d/%m/%Y"))

plot(as.factor(birthRecords$year.of.reg))


plot(as.factor(birthRecords[which(birthRecords$year.of.parents..marriage != "NA" & birthRecords$marriageBaby == "true"), ]$diff))

summary(birthRecords$diff)


birthRecords[which(birthRecords$marriageBaby == "true" & birthRecords$diff > 0), ]

# -------------------

birthRecords <- read.csv("../results/testing-mar-changes/20180305-135939:441/records/birth_records.csv", quote = "", h = T)
birthRecords$diff <- julian(as.Date(paste(birthRecords$day.of.parents..marriage, birthRecords$month.of.parents..marriage, birthRecords$year.of.parents..marriage,sep="/"), "%d/%m/%Y")) - julian(as.Date(birthRecords$birth.date, "%d/%m/%Y"))
plot(as.factor(birthRecords[which(birthRecords$year.of.parents..marriage != "NA" & birthRecords$marriageBaby == "true"), ]$diff))
