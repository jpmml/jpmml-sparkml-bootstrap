loadWineQuality = function(color){
	data = read.table(paste("http://archive.ics.uci.edu/ml/machine-learning-databases/wine-quality/winequality-", color, ".csv", sep = ""), sep = ";", header = TRUE)
	names(data) = c("fixed_acidity", "volatile_acidity", "citric_acid", "residual_sugar", "chlorides", "free_sulfur_dioxide", "total_sulfur_dioxide", "density", "pH", "sulphates", "alcohol", "quality")

	return (data)
}

red = loadWineQuality("red")
red$color = "red"

white = loadWineQuality("white")
white$color = "white"

wine = rbind(red, white)

write.table(wine, "../resources/wine.csv", sep = ",", quote = FALSE, row.names = FALSE, col.names = TRUE)
