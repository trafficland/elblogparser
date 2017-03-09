package com.trafficland

package object elblogparser {

  case class ProcessingTime(value: Double)

  case class StatusCode(value: Int)

  sealed trait HTTPRequestMethod
  case object GET extends HTTPRequestMethod
  case object HEAD extends HTTPRequestMethod
  case object POST extends HTTPRequestMethod
  case object PUT extends HTTPRequestMethod
  case object DELETE extends HTTPRequestMethod
  case object CONNECT extends HTTPRequestMethod
  case object OPTIONS extends HTTPRequestMethod
  case object TRACE extends HTTPRequestMethod
  case object PATCH extends HTTPRequestMethod

  sealed trait HTTPVersion
  case object V1_0 extends HTTPVersion
  case object V1_1 extends HTTPVersion
  case object V2 extends HTTPVersion

  object Implicits {

    implicit def doubleToProcessingTime(value: Double): ProcessingTime = ProcessingTime(value)

    implicit def processingTimeToDouble(processingTime: ProcessingTime): Double = processingTime.value

    implicit def intToStatusCode(value: Int): StatusCode = StatusCode(value)

    implicit def statusCodeToInt(statusCode: StatusCode): Int = statusCode.value
  }
}
