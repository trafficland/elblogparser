package com.trafficland

package object elblogparser {

  type ProcessingTime = Double
  type StatusCode = Int
  type HTTPVersion = Double

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

  final val `HTTPVersion1.1` = 1.1
  final val `HTTPVersion1.0` = 1.0
  final val `HTTPVersion2` = 2
}
