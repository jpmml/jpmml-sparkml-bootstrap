/*
 * Copyright (c) 2016 Villu Ruusmann
 *
 * This file is part of JPMML-SparkML
 *
 * JPMML-SparkML is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JPMML-SparkML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with JPMML-SparkML.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jpmml.sparkml.bootstrap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.Predictor;
import org.apache.spark.ml.classification.DecisionTreeClassifier;
import org.apache.spark.ml.feature.RFormula;
import org.apache.spark.ml.regression.DecisionTreeRegressor;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.StructType;
import org.dmg.pmml.PMML;
import org.jpmml.model.MetroJAXBUtil;
import org.jpmml.sparkml.ConverterUtil;

public class Main {

	@Parameter (
		names = {"--csv-input"},
		description = "Data CSV input file",
		required = true
	)
	private File csvInput = null;

	@Parameter (
		names = "--function",
		description = "Model function. Either CLASSIFICATION or REGRESSION"
	)
	private FunctionType function = FunctionType.CLASSIFICATION;

	@Parameter (
		names = {"--formula"},
		description = "Model formula in R formula notation. See http://stat.ethz.ch/R-manual/R-patched/library/stats/html/formula.html",
		required = true
	)
	private String formula = null;

	@Parameter (
		names = {"--pmml-output"},
		description = "Model PMML output file",
		required = true
	)
	private File pmmlOutput = null;


	static
	public void main(String... args) throws Exception {
		Main main = new Main();

		JCommander commander = new JCommander(main);
		commander.setProgramName(Main.class.getName());

		try {
			commander.parse(args);
		} catch(ParameterException pe){
			commander.usage();

			throw pe;
		}

		main.run();
	}

	private void run() throws Exception {
		SparkConf sparkConf = new SparkConf();

		try(JavaSparkContext sparkContext = new JavaSparkContext(sparkConf)){
			SQLContext sqlContext = new SQLContext(sparkContext);

			DataFrameReader reader = sqlContext.read()
				.format("com.databricks.spark.csv")
				.option("header", "true")
				.option("inferSchema", "true");

			DataFrame dataFrame = reader.load(this.csvInput.getAbsolutePath());

			StructType schema = dataFrame.schema();
			System.out.println(schema.treeString());

			Pipeline pipeline = createPipeline(this.function, this.formula);

			PipelineModel pipelineModel = pipeline.fit(dataFrame);

			PMML pmml = ConverterUtil.toPMML(schema, pipelineModel);

			try(OutputStream os = new FileOutputStream(this.pmmlOutput.getAbsolutePath())){
				MetroJAXBUtil.marshalPMML(pmml, os);
			}
		}
	}

	static
	private Pipeline createPipeline(FunctionType function, String formulaString){
		RFormula formula = new RFormula()
			.setFormula(formulaString);

		Predictor<?, ?, ?> predictor;

		switch(function){
			case CLASSIFICATION:
				predictor = new DecisionTreeClassifier()
					.setMinInstancesPerNode(10);
				break;
			case REGRESSION:
				predictor = new DecisionTreeRegressor()
					.setMinInstancesPerNode(10);
				break;
			default:
				throw new IllegalArgumentException();
		}

		predictor
			.setLabelCol(formula.getLabelCol())
			.setFeaturesCol(formula.getFeaturesCol());

		Pipeline pipeline = new Pipeline()
			.setStages(new PipelineStage[]{formula, predictor});

		return pipeline;
	}

	static
	public enum FunctionType {
		CLASSIFICATION,
		REGRESSION
	}
}