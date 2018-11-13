
library(ggplot2)

pdf("dummy-results.pdf")

link.results <- data.frame(characteristic = character(),
                           datasetID = integer(),
                           size = integer(), 
                           error = character(), 
                           precision = double(), 
                           recall = double(), 
                           f.measure = double(),
                           stringsAsFactors = FALSE)

link.results.metric <- data.frame(characteristic = character(),
                                   datasetID = integer(),
                                   size = integer(), 
                                   error = character(), 
                                   metric = double(), 
                                   type = character(),
                                   stringsAsFactors = FALSE)

size.labels <- c(
  '125000'='Total Size: 125,000',
  '250000'='Total Size: 250,000',
  '500000'='Total Size: 500,000',
  '1000000'='Total Size: 1,000,000',
  '2000000'='Total Size: 2,000,000',
  '4000000'='Total Size: 4,000,000',
  '8000000'='Total Size: 8,000,000',
  '16000000'='Total Size: 16,000,000'
)

population.labels <- c(
  'A'='Popualation A',
  'B'='Popualation B',
  'C'='Popualation C',
  '125000'='Total Size:\n125,000',
  '250000'='Total Size:\n250,000',
  '500000'='Total Size:\n500,000',
  '1000000'='Total Size:\n1,000,000',
  '2000000'='Total Size:\n2,000,000',
  '4000000'='Total Size:\n4,000,000',
  '8000000'='Total Size:\n8,000,000',
  '16000000'='Total Size:\n16,000,000'
)

for(c in LETTERS[1:3]) {
  for(s in 1:8) {
    for(e in LETTERS[1:3]) {
        for(id in 1:((11-s)^2)) {
        precision <- runif(1)
        recall <- runif(1)
        f1 <- (2*precision*recall)/(precision+recall)
        link.results[nrow(link.results)+1,] <- c(c, id, 62500*2^s, e, precision, recall, f1)
        link.results.metric[nrow(link.results.metric)+1,] <- c(c, id, 62500*2^s, e, precision, "precision")
        link.results.metric[nrow(link.results.metric)+1,] <- c(c, id, 62500*2^s, e, recall, "recall")
        link.results.metric[nrow(link.results.metric)+1,] <- c(c, id, 62500*2^s, e, f1, "f measure")
      }
    }
  }
}

link.results$characteristic <- as.factor(link.results$characteristic)
link.results$error <- as.factor(link.results$error)
link.results$precision <- as.numeric(link.results$precision)
link.results$recall <- as.numeric(link.results$recall)
link.results$f.measure <- as.numeric(link.results$f.measure)
link.results$size <- as.numeric(link.results$size)

link.results.metric$characteristic <- as.factor(link.results.metric$characteristic)
link.results.metric$error <- as.factor(link.results.metric$error)
link.results.metric$metric <- as.numeric(link.results.metric$metric)
link.results.metric$type <- as.factor(link.results.metric$type)
link.results.metric$size <- as.numeric(link.results.metric$size)


options(scipen=10000)

plotConsistency <- function(data, errorProfile, labels) {
  p <- ggplot(data[which(data$error==errorProfile),]) +
    geom_point(aes(precision,recall)) +
    facet_grid(size~characteristic, labeller = as_labeller(labels)) +
    ggtitle(paste("Consistency - Error Profile", errorProfile)) +
    labs(x = "Precision", y = "Recall")
  return(p)
}

plotConsistency(link.results, "A", population.labels)
plotConsistency(link.results, "B", population.labels)
plotConsistency(link.results, "C", population.labels)

plotRobustness <- function(data, errorProfile, labels) {
  p <- ggplot(data[which(data$error==errorProfile),]) +
    geom_boxplot(aes(characteristic,metric, colour = type), width = 0.4,position=position_dodge(0.5)) +
    facet_wrap(~size, ncol=2, labeller = as_labeller(labels)) +
    ggtitle(paste("Robustness - Error Profile", errorProfile)) +
    labs(colour = "Metric", x = "Characteristic", y = "Metric")
  return(p)
}

plotRobustness(link.results.metric, "A", size.labels)
plotRobustness(link.results.metric, "B", size.labels)
plotRobustness(link.results.metric, "C", size.labels)

plotScalability <- function(data, errorProfile, labels) {
  p <- ggplot(data[which(data$error==errorProfile),]) +
    geom_boxplot(aes(as.factor(size),metric, colour = type), width = 0.4,position=position_dodge(0.5)) +
    facet_wrap(~characteristic, ncol=1, labeller = as_labeller(labels)) +
    ggtitle(paste("Scalability - Error Profile", errorProfile)) +
    labs(colour = "Metric", x = "Total Size", y = "Metric")
  return(p)
}

plotScalability(link.results.metric, "A", population.labels)
plotScalability(link.results.metric, "B", population.labels)
plotScalability(link.results.metric, "C", population.labels)

plotResilience <- function(data, population, labels) {
  p <- ggplot(data[which(data$characteristic==population),]) +
    geom_boxplot(aes(error,metric, colour = type), width = 0.4,position=position_dodge(0.5)) +
    facet_wrap(~size, ncol=2, labeller = as_labeller(labels)) +
    ggtitle(paste("Resilience - Population", population)) +
    labs(colour = "Metric", x = "Error Profile", y = "Metric")
  return(p)
}

plotResilience(link.results.metric, "A", size.labels)
plotResilience(link.results.metric, "B", size.labels)
plotResilience(link.results.metric, "C", size.labels)

dev.off()













