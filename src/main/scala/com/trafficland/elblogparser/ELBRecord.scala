package com.trafficland.elblogparser

import java.net.{ InetSocketAddress, URI }
import java.time.ZonedDateTime

case class ELBRecord(
  timestamp: ZonedDateTime,
  elbName: String,
  clientAddress: InetSocketAddress,
  backendAddress: InetSocketAddress,
  requestProcessingTime: ProcessingTime,
  backendProcessingTime: ProcessingTime,
  responseProcessingTime: ProcessingTime,
  elbStatusCode: StatusCode,
  backendStatusCode: StatusCode,
  receivedBytes: Long,
  sentBytes: Long,
  requestMethod: HTTPRequestMethod,
  requestURI: URI,
  requestHTTPVersion: HTTPVersion,
  userAgent: Option[String],
  sslCipher: Option[String],
  sslProtocol: Option[String]
)
