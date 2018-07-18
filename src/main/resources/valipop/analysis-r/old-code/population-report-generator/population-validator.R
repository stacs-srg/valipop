fileName = paste(path, "dat/FEMALE_DEATH/femaleDeaths-1855_1_1-cohort.dat", sep="")
print(fileName)
df = data.frame(read.table(fileName, sep=" ", head=T))

