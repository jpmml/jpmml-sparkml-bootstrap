JPMML-SparkML-Bootstrap
=======================

The simplest way to get started with a [JPMML-SparkML] (https://github.com/jpmml/jpmml-evaluator) powered software project.

# IMPORTANT #

This is a legacy codebase.

Starting from September 2016, this project has been superseded by the [JPMML-SparkML-Package] (https://github.com/jpmml/jpmml-sparkml-package) project.

# Prerequisites #

* Java 1.7 or newer.
* [Apache Maven] (https://maven.apache.org/) 3.2 or newer.
* [Apache Spark] (http://spark.apache.org/) 1.6.0 or newer.

# Installation #

Check out the JPMML-SparkML-Bootstrap project and enter its directory:
```
git clone https://github.com/jpmml/jpmml-sparkml-bootstrap.git
cd jpmml-sparkml-bootstrap
```

Build the project:
```
mvn clean install
```

The build produces an uber-JAR file `target/bootstrap-1.0-SNAPSHOT.jar`.

# Development #

Initialize [Eclipse IDE] (https://eclipse.org/ide/) support files `.project` and `.classpath`:
```
mvn eclipse:eclipse
```

Launch the Eclipse IDE, and open the project import wizard via `File` > `Import...` > `General / Existing Projects into Workspace`. In the project wizard window, activate the radio button `Select root directory` and specify the location of the JPMML-SparkML-Bootstrap directory. Click `Finish` to close the project wizard window.

The Eclipse IDE will show the imported JPMML-SparkML-Bootstrap project in the package explorer view as `jpmml-sparkml-bootstrap`.

# Usage #

The uber-JAR file contains an executable class `org.jpmml.sparkml.bootstrap.Main`, which fits a simple two-stage Spark ML pipeline model where the first stage is a [`RFormula`] (https://spark.apache.org/docs/latest/api/java/org/apache/spark/ml/feature/RFormula.html) feature selector and the second stage is either a [`DecisionTreeRegressor`] (https://spark.apache.org/docs/latest/api/java/org/apache/spark/ml/regression/DecisionTreeRegressor.html) or [`DecisionTreeClassifier`] (https://spark.apache.org/docs/latest/api/java/org/apache/spark/ml/classification/DecisionTreeClassifier.html) estimator.

This application is suitable for the quick exploration of datasets.

Launching this application using the [`spark-submit`] (http://spark.apache.org/docs/latest/submitting-applications.html) script:
```
spark-submit \
  --class org.jpmml.sparkml.bootstrap.Main \
  target/bootstrap-1.0-SNAPSHOT.jar \
  --csv-input <path to data CSV input file> \
  --formula <model formula in R formula notation> \
  --function <model function> \
  --pmml-output <path to model PMML output file>
```

### Wine quality dataset

The [wine quality dataset] (https://archive.ics.uci.edu/ml/datasets/Wine+Quality) is suitable both for regression and classification analyses.

Predicting the quality score (integer in range 1 to 10) of wines:
```
spark-submit --master local --class org.jpmml.sparkml.bootstrap.Main target/bootstrap-1.0-SNAPSHOT.jar --csv-input src/test/resources/wine.csv --formula "quality ~ ." --function REGRESSION --pmml-output wine-quality.pmml
```

Predicting the color ("white" or "red") of wines:
```
spark-submit --master local --class org.jpmml.sparkml.bootstrap.Main target/bootstrap-1.0-SNAPSHOT.jar --csv-input src/test/resources/wine.csv --formula "color ~ . -quality" --function CLASSIFICATION --pmml-output wine-color.pmml
```

### Adult (aka Census) dataset

The [adult dataset] (https://archive.ics.uci.edu/ml/datasets/Adult) is suitable for classification analyses.

Predicting the income level ("<=50K" or ">50K") of US residents:
```
spark-submit --master local --class org.jpmml.sparkml.bootstrap.Main target/bootstrap-1.0-SNAPSHOT.jar --csv-input src/test/resources/census.csv --formula "income ~ ." --function CLASSIFICATION --pmml-output census.pmml
```

# License #

JPMML-SparkML-Bootstrap is licensed under the [GNU Affero General Public License (AGPL) version 3.0] (http://www.gnu.org/licenses/agpl-3.0.html). Other licenses are available on request.

# Additional information #

Please contact [info@openscoring.io] (mailto:info@openscoring.io)
