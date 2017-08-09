package com.test.udp

import play.api.libs.json._

trait JsonFormatter[T] {
  def formatter: Format[T]
}

case class GelfFormat(
  version: String,
  host: String,
  short_message: String,
  full_message: Option[String],
  timestamp: Option[BigDecimal],
  level: Int//,
  //stringMetaDatas: Seq[StringMetaData] = Nil,
  //numberMetadatas: Seq[NumberMetaData] = Nil
)

case class StringMetaData(
  key: String,
  value: String
)

case class NumberMetaData(
  key: String,
  value: Int
)

object StringMetaData extends JsonFormatter[StringMetaData] {
  implicit val formatter = Json.format[StringMetaData]
}

object NumberMetaData extends JsonFormatter[NumberMetaData] {
  implicit val formatter = Json.format[NumberMetaData]
}

object GelfFormat extends JsonFormatter[GelfFormat] {
  implicit val formatter = Json.format[GelfFormat]
}



