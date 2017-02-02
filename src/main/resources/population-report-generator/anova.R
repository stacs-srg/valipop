runAnovaForAllFilesIn <- function(dir_path, pol_order) {
  
  files = list.files(path = dir_path, pattern = ".*.dat");
  
  pdf(file = paste(dir_path, "plots.pdf"))
  par(mfrow = c(3,2))
  
  for(f in files) {
    p = paste(dir_path, f, sep="")
    runAnova(p, pol_order, f)
  }
  
  dev.off()
  
}

runAnova <- function(path, pol_order, title) {
  
  print(path)
  mydata = read.table(path, sep=" ", head=T)
  
  #orders data
  mydata = mydata[with(mydata, order(mydata$label)), ]
  
  #plots data
  plot(main = title, mydata$label[mydata$group=="generated"], mydata$value[mydata$group=="generated"], xlim=range(mydata$label),
       ylim=range(mydata$value), type="b", xlab = "Number of children in partnership", ylab = "Proportion of partnerships ending")
  lines(mydata$label[mydata$group=="desired"], mydata$value[mydata$group=="desired"], type="b", col=3)
  #lines(mydata$label, mydata$value, type="b", col=2)
  
  #model without grouping variable
  try(fit1 <- lm(label ~ poly(value, pol_order), data = mydata))
  
  #model with grouping variable
  try(fit2 <- lm(label ~ poly(value, pol_order) * group, data = mydata))
  
  #compare models 
  try(anova(fit1, fit2))
  
}