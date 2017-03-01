
ANOVAResult <- setClass(
  
  "ANOVAResult",
  
  slots = c(
    plot = 'recordedplot',
    anovaAnalysis = "anova",
    p = "numeric"
  )
)


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

runAnova <- function(path, pol_order, title, xlabel, ylabel) {
  
  print(path)
  mydata = read.table(path, sep=" ", head=T)
  
  #orders data
  mydata = mydata[with(mydata, order(mydata$label)), ]
  
  png()
  dev.control('enable')
  
  plot(main = title, mydata$label[mydata$group=="generated"], mydata$value[mydata$group=="generated"], xlim=range(mydata$label), 
             ylim=range(mydata$value), type="b", xlab = xlabel, ylab = ylabel)
  
  lines(mydata$label[mydata$group=="desired"], mydata$value[mydata$group=="desired"], type="b", col=3)
  legend(x='topright', legend=c("Generated", "Desired"),
         col=c(1, 3), lty=1:1, cex=1)
  
  gPlot = recordPlot()
  dev.off()
  
  #model without grouping variable
  try(fit1 <- lm(label ~ poly(value, pol_order), data = mydata))
  
  #model with grouping variable
  try(fit2 <- lm(label ~ poly(value, pol_order) * group, data = mydata))
  
  #compare models 
  try(anova <- anova(fit1, fit2))
  
  ret <- ANOVAResult(plot = gPlot, anovaAnalysis = anova, p = anova$'Pr(>F)'[2])
  
  
  return(ret)
}
