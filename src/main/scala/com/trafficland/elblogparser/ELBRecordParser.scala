package com.trafficland.elblogparser

import scala.annotation.tailrec

sealed trait RecordParsingResult
final case class RecordParsingSuccess(record: ELBRecord) extends RecordParsingResult
final case class RecordParsingFailure(rawRecord: String, errors: List[FieldParsingResult]) extends RecordParsingResult

case class ELBRecordParser(fieldBufferCapacity: Int = 512) {

  protected val fieldVal = new StringBuilder(fieldBufferCapacity)
  protected var remainingChars: List[Char] = List.empty

  def parse[T](rawRecord: String)(handler: RecordParsingResult => T): T = {
    remainingChars = rawRecord.toList
    val fieldParsingResults = Map[ELBRecordField, FieldParsingResult](
      Timestamp -> parseField(Timestamp),
      ELBName -> parseField(ELBName),
      ClientAddress -> parseField(ClientAddress),
      BackendAddress -> parseField(BackendAddress),
      RequestProcessingTime -> parseField(RequestProcessingTime),
      BackendProcessingTime -> parseField(BackendProcessingTime),
      ResponseProcessingTime -> parseField(ResponseProcessingTime),
      ELBStatusCode -> parseField(ELBStatusCode),
      BackendStatusCode -> parseField(BackendStatusCode),
      ReceivedBytes -> parseField(ReceivedBytes),
      SentBytes -> parseField(SentBytes),
      RequestMethod -> parseField(RequestMethod),
      RequestURL -> parseField(RequestURL),
      RequestHTTPVersion -> parseField(RequestHTTPVersion),
      UserAgent -> parseField(UserAgent),
      SSLCipher -> parseField(SSLCipher),
      SSLProtocol -> parseField(SSLProtocol)
    )

    val result = if (parsingSuccessful(fieldParsingResults)) {
      RecordParsingSuccess(
        ELBRecord(
          timestamp = fieldParsingResults(Timestamp),
          elbName = fieldParsingResults(ELBName),
          clientAddress = fieldParsingResults(ClientAddress),
          backendAddress = fieldParsingResults(BackendAddress),
          requestProcessingTime = fieldParsingResults(RequestProcessingTime),
          backendProcessingTime = fieldParsingResults(BackendProcessingTime),
          responseProcessingTime = fieldParsingResults(ResponseProcessingTime),
          elbStatusCode = fieldParsingResults(ELBStatusCode),
          backendStatusCode = fieldParsingResults(BackendStatusCode),
          receivedBytes = fieldParsingResults(ReceivedBytes),
          sentBytes = fieldParsingResults(SentBytes),
          requestMethod = fieldParsingResults(RequestMethod),
          requestURL = fieldParsingResults(RequestURL),
          requestHTTPVersion = fieldParsingResults(RequestHTTPVersion),
          userAgent = fieldParsingResults(UserAgent),
          sslCipher = fieldParsingResults(SSLCipher),
          sslProtocol = fieldParsingResults(SSLProtocol)
        )
      )
    } else {
      RecordParsingFailure(rawRecord,
        fieldParsingResults.values.filter {
          case FieldParsingFailure(_, _) => true
          case _ => false
        }.toList
      )
    }

    handler(result)
  }

  protected def parsingSuccessful(results: Map[ELBRecordField, FieldParsingResult]): Boolean = results.forall {
    case (_, FieldParsingSuccess(_)) => true
    case _ => false
  }

  protected def parseField(field: ELBRecordField): FieldParsingResult = {
    fieldVal.clear()
    field.startDelimiter.foreach(sd => remainingChars = splitAtStartDelimiter(remainingChars, sd))
    remainingChars = splitAtEndDelimiter(remainingChars, field.endDelimiter, fieldVal)
    field.parse(fieldVal.toString)
  }

  @tailrec
  private def splitAtStartDelimiter(raw: List[Char], delimiter: Char): List[Char] = raw match {
    case nextChar :: charsTail if nextChar != delimiter =>
      splitAtStartDelimiter(charsTail, delimiter)

    case _ :: charsTail => charsTail

    case _ => raw
  }

  @tailrec
  private def splitAtEndDelimiter(raw: List[Char], delimiter: Char, fieldVal: StringBuilder): List[Char] = raw match {
    case nextChar :: charsTail if nextChar != delimiter =>
      fieldVal.append(nextChar)
      splitAtEndDelimiter(charsTail, delimiter, fieldVal)

    case _ :: charsTail => charsTail

    case _ => raw
  }
}
