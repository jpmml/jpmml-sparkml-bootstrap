loadAdult = function(name, skip = 0){
	data = read.table(paste("https://archive.ics.uci.edu/ml/machine-learning-databases/adult/", name, sep = ""), skip = skip, sep = ",", header = FALSE, na.strings = "?", row.names = NULL, col.names = c("age", "workclass", "fnlwgt", "education", "education_num", "marital_status", "occupation", "relationship", "race", "sex", "capital_gain", "capital_loss", "hours_per_week", "native_country", "income"), strip.white = TRUE)

	return (data)
}

adult.train = loadAdult("adult.data")
adult.test = loadAdult("adult.test", skip = 1)

# Strip the "." suffix
levels(adult.test$income) = c("<=50K", ">50K")

adult.all = rbind(adult.train, adult.test)
adult.all = adult.all[complete.cases(adult.all), ]

adult.all$fnlwgt = NULL

write.table(adult.all, "../resources/census.csv", sep = ",", quote = FALSE, row.names = FALSE, col.names = TRUE)
