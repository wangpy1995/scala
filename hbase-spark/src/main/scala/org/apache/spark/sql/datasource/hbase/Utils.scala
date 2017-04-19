
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.sql.datasource.hbase

import java.nio.ByteBuffer
import java.util.{HashMap => JavaHashMap}

import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import com.twitter.chill.ResourcePool
import org.apache.hadoop.hbase.classification.InterfaceAudience
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.datasources.AvroSerdes
import org.apache.spark.serializer.{KryoSerializer, SerializerInstance}
import org.apache.spark.sql.types._
import org.apache.spark.unsafe.types.UTF8String
import org.apache.spark.util.MutablePair
import org.apache.spark.{SparkConf, SparkEnv}

import scala.reflect.ClassTag

@InterfaceAudience.Private
object Utils {


  /**
    * Parses the hbase field to it's corresponding
    * scala type which can then be put into a Spark GenericRow
    * which is then automatically converted by Spark.
    */
  def hbaseFieldToScalaType(
                             f: Field,
                             src: Array[Byte],
                             offset: Int,
                             length: Int): Any = {
    if (f.exeSchema.isDefined) {
      // If we have avro schema defined, use it to get record, and then convert them to catalyst data type
      val m = AvroSerdes.deserialize(src, f.exeSchema.get)
      val n = f.avroToCatalyst.map(_ (m))
      n.get
    } else {
      // Fall back to atomic type
      f.dt match {
        case BooleanType => toBoolean(src, offset)
        case ByteType => src(offset)
        case DoubleType => Bytes.toDouble(src, offset)
        case FloatType => Bytes.toFloat(src, offset)
        case IntegerType => Bytes.toInt(src, offset)
        case LongType | TimestampType => Bytes.toLong(src, offset)
        case ShortType => Bytes.toShort(src, offset)
        case StringType => toUTF8String(src, offset, length)
        case BinaryType =>
          val newArray = new Array[Byte](length)
          System.arraycopy(src, offset, newArray, 0, length)
          newArray
        // TODO: add more data type support
        case _ => SparkSqlSerializer.deserialize[Any](src)
      }
    }
  }

  // convert input to data type
  def toBytes(input: Any, field: Field): Array[Byte] = {
    if (field.schema.isDefined) {
      // Here we assume the top level type is structType
      val record = field.catalystToAvro(input)
      AvroSerdes.serialize(record, field.schema.get)
    } else {
      input match {
        case data: Boolean => Bytes.toBytes(data)
        case data: Byte => Array(data)
        case data: Array[Byte] => data
        case data: Double => Bytes.toBytes(data)
        case data: Float => Bytes.toBytes(data)
        case data: Int => Bytes.toBytes(data)
        case data: Long => Bytes.toBytes(data)
        case data: Short => Bytes.toBytes(data)
        case data: UTF8String => data.getBytes
        case data: String => Bytes.toBytes(data)
        // TODO: add more data type support
        case _ => throw new Exception(s"unsupported data type ${field.dt}")
      }
    }
  }

  def toBoolean(input: Array[Byte], offset: Int): Boolean = {
    input(offset) != 0
  }

  def toUTF8String(input: Array[Byte], offset: Int, length: Int): UTF8String = {
    UTF8String.fromBytes(input.slice(offset, offset + length))
  }
}

private[sql] class SparkSqlSerializer(conf: SparkConf) extends KryoSerializer(conf) {
  override def newKryo(): Kryo = {
    val kryo = super.newKryo()
    kryo.setRegistrationRequired(false)
    kryo.register(classOf[MutablePair[_, _]])
    kryo.register(classOf[org.apache.spark.sql.catalyst.expressions.GenericRow])
    kryo.register(classOf[org.apache.spark.sql.catalyst.expressions.GenericInternalRow])
    kryo.register(classOf[org.apache.spark.sql.catalyst.expressions.UnsafeRow])
    kryo.register(classOf[java.math.BigDecimal], new JavaBigDecimalSerializer)
    kryo.register(classOf[BigDecimal], new ScalaBigDecimalSerializer)

    kryo.register(classOf[Decimal])
    kryo.register(classOf[JavaHashMap[_, _]])

    kryo.setReferences(false)
    kryo
  }
}


private[sql] class KryoResourcePool(size: Int)
  extends ResourcePool[SerializerInstance](size) {

  val ser: SparkSqlSerializer = {
    val sparkConf = Option(SparkEnv.get).map(_.conf).getOrElse(new SparkConf())
    new SparkSqlSerializer(sparkConf)
  }

  def newInstance(): SerializerInstance = ser.newInstance()
}

private[sql] object SparkSqlSerializer {
  @transient lazy val resourcePool = new KryoResourcePool(30)

  private[this] def acquireRelease[O](fn: SerializerInstance => O): O = {
    val kryo = resourcePool.borrow
    try {
      fn(kryo)
    } finally {
      resourcePool.release(kryo)
    }
  }

  def serialize[T: ClassTag](o: T): Array[Byte] =
    acquireRelease { k =>
      k.serialize(o).array()
    }

  def deserialize[T: ClassTag](bytes: Array[Byte]): T =
    acquireRelease { k =>
      k.deserialize[T](ByteBuffer.wrap(bytes))
    }
}


private[sql] class JavaBigDecimalSerializer extends Serializer[java.math.BigDecimal] {
  def write(kryo: Kryo, output: Output, bd: java.math.BigDecimal) {
    // TODO: There are probably more efficient representations than strings...
    output.writeString(bd.toString)
  }

  def read(kryo: Kryo, input: Input, tpe: Class[java.math.BigDecimal]): java.math.BigDecimal = {
    new java.math.BigDecimal(input.readString())
  }
}

private[sql] class ScalaBigDecimalSerializer extends Serializer[BigDecimal] {
  def write(kryo: Kryo, output: Output, bd: BigDecimal) {
    // TODO: There are probably more efficient representations than strings...
    output.writeString(bd.toString)
  }

  def read(kryo: Kryo, input: Input, tpe: Class[BigDecimal]): BigDecimal = {
    new java.math.BigDecimal(input.readString())
  }
}