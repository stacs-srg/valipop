ggplot() + 
  geom_point(aes(x = runs.c$Total.Pop, y = runs.c$Peak.Memory.Usage..MB., color = runs.c$Seed.Pop.Size)) + 
  geom_abline(aes(slope =0, intercept=12000), colour = "red") + geom_abline(aes(slope =0, intercept=12000*0.65)) +
  geom_abline(aes(slope =0, intercept=8000), colour = "blue") + geom_abline(aes(slope =0, intercept=8000*0.65)) + 
  geom_abline(aes(slope =0, intercept=6000), colour = "green") + geom_abline(aes(slope =0, intercept=6000*0.65)) +
geom_abline(aes(slope =0, intercept=4000), colour = "yellow") + geom_abline(aes(slope =0, intercept=4000*0.65))
