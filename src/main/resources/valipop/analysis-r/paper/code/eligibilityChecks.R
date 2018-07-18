nba <- "/Volumes/TOSHIBA EXT/pop-runs/batch59-fs/batch59-fs-results-summary.csv"
t <- read.table(nba, sep = ",", header = TRUE)

summary(t)

sub <- cbind.data.frame(ss = t$Seed.Pop.Size, tp = t$Total.Pop, srt = t$Sim.Run.time, ec = t$Eligibility.Checks, fec = t$Failed.Eligibility.Checks, pfec = t$Failed.Eligibility.Checks / t$Eligibility.Checks)

plot(sub)


library(ggplot2)

ggplot(sub) +
  geom_point(aes(x = pfec, y = srt)) +
  facet_grid( ~ ss)
