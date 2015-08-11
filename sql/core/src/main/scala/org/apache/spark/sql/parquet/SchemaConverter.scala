package org.apache.spark.sql.parquet

import org.apache.parquet.schema.{Type, MessageType}
import org.apache.spark.sql.types.{StructField, StructType}

trait SchemaConverter {
  def convert(parquetSchema: MessageType): StructType
  def convert(catalystSchema: StructType): MessageType
  def convertField(field: StructField): Type
}
