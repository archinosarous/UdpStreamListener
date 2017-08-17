package io.skul



import play.api.libs.json._

trait JsonFormatter[T] {
  def formatter: Format[T]
}

case class BaseData(
  version: String,
  host: String,
  short_message: String,
  full_message: Option[String],
  timestamp: Option[BigDecimal],
  level: Int
)

case class GelfFormat(
  baseData: BaseData,
  stringMetaDatas: Seq[StringMetaData] = Nil,
  numberMetadatas: Seq[NumberMetaData] = Nil
)

case class StringMetaData(
  key: String,
  value: String
)

case class NumberMetaData(
  key: String,
  value: Double
)

//object StringMetaData extends JsonFormatter[StringMetaData] {
//  implicit val formatter = Json.format[StringMetaData]
//}

object NumberMetaData extends JsonFormatter[NumberMetaData] {
  implicit val formatter = Json.format[NumberMetaData]
}

object BaseData extends JsonFormatter[BaseData] {
  implicit val formatter = Json.format[BaseData]
}

//object GelfFormat extends JsonFormatter[GelfFormat] {
//  implicit val formatter = Json.format[GelfFormat]
//}



