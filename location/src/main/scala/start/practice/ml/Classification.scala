package start.practice.ml

import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by wpy on 2017/6/5.
  */
object Classification {
  val sparkConf = new SparkConf().setAppName("svm").setMaster("local[*]")
  val sc = SparkContext.getOrCreate(sparkConf)

  def svmTest() {
    val svm_data = getClass.getResource("/mllib/sample_svm_data.txt").getFile
    val data = sc.textFile(svm_data)
    val parsedData = data.map { line =>
      val parts = line.split(" ").map(_.toDouble)
      LabeledPoint(parts(0), Vectors.dense(parts.tail))
    }
    val m = data.map(line => Vectors.dense(line.split(" ").map(_.toDouble)))
    val matrix = new RowMatrix(m)
    println(matrix.rows.collect().toSeq.mkString("\n"))
    val numIterations = 2
    val model = SVMWithSGD.train(parsedData, numIterations)
    val labelsAndPredictions = parsedData.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }
    println(labelsAndPredictions.collect().toSeq)
    val trainError = labelsAndPredictions.filter(r => r._1 != r._2).count.toDouble / labelsAndPredictions.count
    println(s"trainError=$trainError")
  }
}
