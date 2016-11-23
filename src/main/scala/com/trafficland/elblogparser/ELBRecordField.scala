package com.trafficland.elblogparser

import java.net.{InetSocketAddress, URL}
import java.time.ZonedDateTime

sealed trait ELBRecordField {
  val startDelimiter: Option[Char] = None
  val endDelimiter: Char = ' '

  protected def parseAddress(raw: String): FieldParsingResult = try {
      val split = raw.split(':')
      FieldParsingSuccess(InetSocketAddress.createUnresolved(split(0), split(1).toInt))
    } catch {
      case e: Exception => FieldParsingFailure(this, e.toString)
    }

  protected def parseProcessingTime(raw: String): FieldParsingResult = try {
      FieldParsingSuccess(raw.toDouble)
    } catch {
      case e: Exception => FieldParsingFailure(this, e.toString)
    }

  protected def parseStatusCode(raw: String): FieldParsingResult = try {
      FieldParsingSuccess(raw.toInt)
    } catch {
      case e: Exception => FieldParsingFailure(this, e.toString)
    }

  protected def parseByteQuantity(raw: String): FieldParsingResult = try {
    FieldParsingSuccess(raw.toLong)
  } catch {
    case e: Exception => FieldParsingFailure(this, e.toString)
  }

  def parse(raw: String): FieldParsingResult
}

case object Timestamp extends ELBRecordField {

  def parse(raw: String): FieldParsingResult = {
    try {
      FieldParsingSuccess(ZonedDateTime.parse(raw))
    } catch {
      case e: Exception => FieldParsingFailure(this, e.toString)
    }
  }
}

case object ELBName extends ELBRecordField {

  def parse(raw: String): FieldParsingResult = FieldParsingSuccess(raw)
}

case object ClientAddress extends ELBRecordField {

  def parse(raw: String): FieldParsingResult = parseAddress(raw)
}

case object BackendAddress extends ELBRecordField {

  def parse(raw: String): FieldParsingResult = parseAddress(raw)
}

case object RequestProcessingTime extends ELBRecordField {

  def parse(raw: String): FieldParsingResult = parseProcessingTime(raw)
}

case object BackendProcessingTime extends ELBRecordField {

  def parse(raw: String): FieldParsingResult = parseProcessingTime(raw)
}

case object ResponseProcessingTime extends ELBRecordField {

  def parse(raw: String): FieldParsingResult = parseProcessingTime(raw)
}

case object ELBStatusCode extends ELBRecordField {

  def parse(raw: String): FieldParsingResult = parseStatusCode(raw)
}

case object BackendStatusCode extends ELBRecordField {

  def parse(raw: String): FieldParsingResult = parseStatusCode(raw)
}

case object ReceivedBytes extends ELBRecordField {

  override def parse(raw: String): FieldParsingResult = parseByteQuantity(raw)
}

case object SentBytes extends ELBRecordField {

  override def parse(raw: String): FieldParsingResult = parseByteQuantity(raw)
}

case object RequestMethod extends ELBRecordField {

  override val startDelimiter: Option[Char] = Some('\"')

  override def parse(raw: String): FieldParsingResult = {
    raw.toUpperCase match {
      case "GET" => FieldParsingSuccess(GET)
      case "HEAD" => FieldParsingSuccess(HEAD)
      case "POST" => FieldParsingSuccess(POST)
      case "PUT" => FieldParsingSuccess(PUT)
      case "DELETE" => FieldParsingSuccess(DELETE)
      case "CONNECT" => FieldParsingSuccess(CONNECT)
      case "OPTIONS" => FieldParsingSuccess(OPTIONS)
      case "TRACE" => FieldParsingSuccess(TRACE)
      case "PATCH" => FieldParsingSuccess(PATCH)
      case _ => FieldParsingFailure(this, "Request method is not valid.")
    }
  }
}

case object RequestURL extends ELBRecordField {

  override def parse(raw: String): FieldParsingResult = try {
    FieldParsingSuccess(new URL(raw))
  } catch {
    case e: Exception => FieldParsingFailure(this, e.toString)
  }
}

case object RequestHTTPVersion extends ELBRecordField {

  override val endDelimiter: Char = '\"'
  final val `version1.0String` = "HTTP/1.0"
  final val `version1.1String` = "HTTP/1.1"
  final val `version2String` = "HTTP/2"

  override def parse(raw: String): FieldParsingResult = {
    raw match {
      case `version1.0String` => FieldParsingSuccess(`HTTPVersion1.0`)
      case `version1.1String` => FieldParsingSuccess(`HTTPVersion1.1`)
      case `version2String` => FieldParsingSuccess(`HTTPVersion2`)
      case _ => FieldParsingFailure(this, "Request HTTP version is not valid.")
    }
  }
}

case object UserAgent extends ELBRecordField {

  override val startDelimiter: Option[Char] = Some('\"')
  override val endDelimiter: Char = '\"'

  override def parse(raw: String): FieldParsingResult = if(raw.isEmpty) {
    FieldParsingSuccess(None)
  } else {
    FieldParsingSuccess(Some(raw))
  }
}

case object SSLCipher extends ELBRecordField {

  override val startDelimiter: Option[Char] = Some(' ')

  override def parse(raw: String): FieldParsingResult = if(raw.isEmpty) {
    FieldParsingSuccess(None)
  } else {
    FieldParsingSuccess(Some(raw))
  }
}

case object SSLProtocol extends ELBRecordField {

  override def parse(raw: String): FieldParsingResult = if(raw.isEmpty) {
    FieldParsingSuccess(None)
  } else {
    FieldParsingSuccess(Some(raw))
  }
}

object FieldParsingResult {
  implicit def value[V](result: FieldParsingResult): V = result.get
}

sealed trait FieldParsingResult {
  def get[U]: U
}

final case class FieldParsingSuccess[T](value: T) extends FieldParsingResult {
  def get[U]: U = value.asInstanceOf[U]
}

final case class FieldParsingFailure[T](field: ELBRecordField, error: String) extends FieldParsingResult {
  def get[U]: Nothing = throw new NoSuchElementException("This field was not successfully parsed.")
}