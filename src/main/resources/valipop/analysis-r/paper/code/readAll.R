source("paper/code/FileFunctions.R")

df.all <- filesToDF("/cs/tmp/tsd4/results/batch52-fs/batch52-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch53-fs/batch53-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch53-fs/batch52now53-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch54-fs/batch54-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch55-fs/batch55-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch56-fs/batch56-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch57-fs/batch57-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch58-fs/batch58-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch59-fs/batch59-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch60-fs/batch60-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch61-fs/batch61-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch62-fs/batch62-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch63-fs/batch63-fs-results-summary.csv",
                    "/cs/tmp/tsd4/results/batch64-fs/batch64-fs-results-summary.csv",
                    onlyGetStatErrors = FALSE)

summary(df.all)


summary <- dfToSummaryDF(df.all)
final <- summaryDfToFinalDF(summary)

selected <- selectFromFullDF(df.all, final)






library('ggplot2')
ggplot() + 
  geom_label(data = summary, aes(prf, rf, label = paste(pass.rate, count, sep = "\n"), fill = pass.rate, colour = count), size = 2.5) +
  scale_colour_gradient(low = "red", high = "white") +
  scale_fill_gradient(low = "grey", high = "green") +
  facet_wrap(~ seed, nrow = 7)

