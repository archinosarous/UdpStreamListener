package io.skul

case class GelfMessage(
  version: String,
  host: String,
  shortMessage: String,
  fullMessage: Option[String],
  timestamp: Option[Double],
  level: Int,
  optionalFields: List[OptionalField[_]]
)

sealed trait OptionalField[T] {
  def key: String
  def value: T
}

case class StringOptionalField (key: String,  value: String) extends OptionalField[String]

case class DoubleOptionalField (key: String,  value: Double) extends OptionalField[Double]

