package com.github.ereichert.elblogparser

import java.net.InetSocketAddress
import java.time.ZonedDateTime

import com.trafficland.elblogparser._
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.matchers.{MatchResult, Matcher}
import ELBRecordFieldMatchers._

class ELBRecordParserUnitSpec extends WordSpec {

  "ELBRecordParser.parse" should {

    "return the correct list of parsing errors when there are multiple field parsing errors." in {
      val badRecord = "bad_timestamp elbname 172.16.1.6:54814 172.16.20.5:9000 0.000039 " +
        "0.145507 0.00003 200 bad_backend_status_code 10995116277760 bad_bytes " +
        "\"GET http://some.domain.com:80/path0/path1?param0=p0&param1=p1 HTTP/1.1\""

      ELBRecordParser().parse(badRecord) {
        case RecordParsingSuccess(_) => fail("Parsing succeeded with multiple errors present.")

        case RecordParsingFailure(_, errors) =>
          errors.map(_.asInstanceOf[FieldParsingFailure[_]].field) should contain
            only(
              Timestamp,
              BackendStatusCode,
              SentBytes
            )
      }
    }

    "return a parsing error referencing the sent bytes time when the sent bytes is malformed." in {
      val badRecord = "2016-10-19T16:00:20.863859Z elbname 172.16.1.6:54814 172.16.20.5:9000 0.000039 " +
        "0.145507 0.00003 200 404 10995116277760 bad_bytes " +
        "\"GET http://some.domain.com:80/path0/path1?param0=p0&param1=p1 HTTP/1.1\""

      ELBRecordParser().parse(badRecord) { parsingResult =>
        parsingResult should failWithASingleField(SentBytes)
      }
    }

    "return a parsing error referencing the received bytes time when the received bytes is malformed." in {
      val badRecord = "2016-10-19T16:00:20.863859Z elbname 172.16.1.6:54814 172.16.20.5:9000 0.000039 " +
        "0.145507 0.00003 200 404 bad_bytes 5497558138880 " +
        "\"GET http://some.domain.com:80/path0/path1?param0=p0&param1=p1 HTTP/1.1\""

      ELBRecordParser().parse(badRecord) { parsingResult =>
        parsingResult should failWithASingleField(ReceivedBytes)
      }
    }

    "return a parsing error referencing the backend status code time when the backend status code is malformed." in {
      val badRecord = "2016-10-19T16:00:20.863859Z elbname 172.16.1.6:54814 172.16.20.5:9000 0.000039 " +
        "0.145507 0.00003 200 bad_backend_status_code 10995116277760 5497558138880 " +
        "\"GET http://some.domain.com:80/path0/path1?param0=p0&param1=p1 HTTP/1.1\""

      ELBRecordParser().parse(badRecord) { parsingResult =>
        parsingResult should failWithASingleField(BackendStatusCode)
      }
    }

    "return a parsing error referencing the ELB status code time when the ELB status code is malformed." in {
      val badRecord = "2016-10-19T16:00:20.863859Z elbname 172.16.1.6:54814 172.16.20.5:9000 0.000039 " +
        "0.145507 0.00003 bad_status_code 404 10995116277760 5497558138880 " +
        "\"GET http://some.domain.com:80/path0/path1?param0=p0&param1=p1 HTTP/1.1\""

      ELBRecordParser().parse(badRecord) { parsingResult =>
        parsingResult should failWithASingleField(ELBStatusCode)
      }
    }

    "return a parsing error referencing the response processing time when the response processing time is malformed." in {
      val badRecord = "2016-10-19T16:00:20.863859Z elbname 172.16.1.6:54814 172.16.20.5:9000 0.000039 " +
        "0.145507 bad_response_processing_time 200 404 10995116277760 5497558138880 " +
        "\"GET http://some.domain.com:80/path0/path1?param0=p0&param1=p1 HTTP/1.1\""

      ELBRecordParser().parse(badRecord) { parsingResult =>
        parsingResult should failWithASingleField(ResponseProcessingTime)
      }
    }

    "return a parsing error referencing the backend processing time when the backend processing time is malformed." in {
      val badRecord = "2016-10-19T16:00:20.863859Z elbname 172.16.1.6:54814 172.16.20.5:9000 0.000039 " +
        "bad_backend_processing_time 0.00003 200 404 10995116277760 5497558138880 " +
        "\"GET http://some.domain.com:80/path0/path1?param0=p0&param1=p1 HTTP/1.1\""

      ELBRecordParser().parse(badRecord) { parsingResult =>
        parsingResult should failWithASingleField(BackendProcessingTime)
      }
    }

    "return a parsing error referencing the request processing time when the request processing time is malformed." in {
      val badRecord = "2016-10-19T16:00:20.863859Z elbname 172.16.1.6:54814 172.16.20.5:9000 bad_request_processing_time " +
        "0.145507 0.00003 200 404 10995116277760 5497558138880 " +
        "\"GET http://some.domain.com:80/path0/path1?param0=p0&param1=p1 HTTP/1.1\""

      ELBRecordParser().parse(badRecord) { parsingResult =>
        parsingResult should failWithASingleField(RequestProcessingTime)
      }
    }

    "return a parsing error referencing the backend address when the backend address is malformed." in {
      val badRecord = "2016-10-19T16:00:20.863859Z elbname 172.16.1.6:54814 bad_backend_address 0.000039 " +
        "0.145507 0.00003 200 404 10995116277760 5497558138880 " +
        "\"GET http://some.domain.com:80/path0/path1?param0=p0&param1=p1 HTTP/1.1\""

      ELBRecordParser().parse(badRecord) { parsingResult =>
        parsingResult should failWithASingleField(BackendAddress)
      }
    }

    "return a parsing error referencing the client address when the client address is malformed." in {
      val badRecord = "2016-10-19T16:00:20.863859Z elbname bad_client_address 172.16.20.5:9000 0.000039 " +
        "0.145507 0.00003 200 404 10995116277760 5497558138880 " +
        "\"GET http://some.domain.com:80/path0/path1?param0=p0&param1=p1 HTTP/1.1\""

      ELBRecordParser().parse(badRecord) { parsingResult =>
        parsingResult should failWithASingleField(ClientAddress)
      }
    }

    "return a parsing error referencing the timestamp when the timestamp is malformed." in {
      val badRecord = "bad_timestamp elbname 172.16.1.6:54814 172.16.20.5:9000 0.000039 " +
        "0.145507 0.00003 200 404 10995116277760 5497558138880 " +
        "\"GET http://some.domain.com:80/path0/path1?param0=p0&param1=p1 HTTP/1.1\""

      ELBRecordParser().parse(badRecord) { parsingResult =>
        parsingResult should failWithASingleField(Timestamp)
      }
    }

    "parse a version 1 record that has no errors.." in {
      val rawV1ELBRecord = "2016-10-19T16:00:20.863859Z elbname 172.16.1.6:54814 172.16.20.5:9000 0.000039 " +
        "0.145507 0.00003 200 404 10995116277760 5497558138880 " +
        "\"GET http://some.domain.com:80/path0/path1?param0=p0&param1=p1 HTTP/1.1\""

      ELBRecordParser().parse(rawV1ELBRecord) {
        case RecordParsingSuccess(elbRecord) =>
          elbRecord.timestamp should equal(ZonedDateTime.parse("2016-10-19T16:00:20.863859Z"))
          elbRecord.elbName should equal("elbname")
          elbRecord.clientAddress should equal(InetSocketAddress.createUnresolved("172.16.1.6", 54814))
          elbRecord.backendAddress should equal(InetSocketAddress.createUnresolved("172.16.20.5", 9000))
          elbRecord.requestProcessingTime should equal(0.000039)
          elbRecord.backendProcessingTime should equal(0.145507)
          elbRecord.responseProcessingTime should equal(0.00003)
          elbRecord.elbStatusCode should equal(200)
          elbRecord.backendStatusCode should equal(404)
          elbRecord.receivedBytes should equal(10995116277760L)
          elbRecord.sentBytes should equal(5497558138880L)
          elbRecord.requestMethod should equal(GET)
          elbRecord.requestURL.toString should equal("http://some.domain.com:80/path0/path1?param0=p0&param1=p1")
          elbRecord.requestHTTPVersion should equal(`HTTPVersion1.1`)
          elbRecord.userAgent should not be defined
          elbRecord.sslCipher should not be defined
          elbRecord.sslProtocol should not be defined

        case RecordParsingFailure(_, errors) => fail(errors.toString)
      }
    }

    "parse a version 2 record that has no errors.." in {
      val rawV2ELBRecord = "2016-10-19T16:00:20.863859Z elbname 172.16.1.6:54814 172.16.20.5:9000 0.000039 " +
        "0.145507 0.00003 200 404 10995116277760 5497558138880 " +
        "\"GET http://some.domain.com:80/path0/path1?param0=p0&param1=p1 HTTP/1.1\"" +
        "\"Mozilla/5.0 (cloud; like Mac OS X; en-us)\" some_ssl_cipher some_ssl_protocol"

      ELBRecordParser().parse(rawV2ELBRecord) {
        case RecordParsingSuccess(elbRecord) =>
          elbRecord.timestamp should equal(ZonedDateTime.parse("2016-10-19T16:00:20.863859Z"))
          elbRecord.elbName should equal("elbname")
          elbRecord.clientAddress should equal(InetSocketAddress.createUnresolved("172.16.1.6", 54814))
          elbRecord.backendAddress should equal(InetSocketAddress.createUnresolved("172.16.20.5", 9000))
          elbRecord.requestProcessingTime should equal(0.000039)
          elbRecord.backendProcessingTime should equal(0.145507)
          elbRecord.responseProcessingTime should equal(0.00003)
          elbRecord.elbStatusCode should equal(200)
          elbRecord.backendStatusCode should equal(404)
          elbRecord.receivedBytes should equal(10995116277760L)
          elbRecord.sentBytes should equal(5497558138880L)
          elbRecord.requestMethod should equal(GET)
          elbRecord.requestURL.toString should equal("http://some.domain.com:80/path0/path1?param0=p0&param1=p1")
          elbRecord.requestHTTPVersion should equal(`HTTPVersion1.1`)
          elbRecord.userAgent should be(Some("Mozilla/5.0 (cloud; like Mac OS X; en-us)"))
          elbRecord.sslCipher should be(Some("some_ssl_cipher"))
          elbRecord.sslProtocol should be(Some("some_ssl_protocol"))

        case RecordParsingFailure(_, errors) => fail(errors.toString)
      }
    }
  }
}

trait ELBRecordFieldMatchers {

  class ShouldFailWithFieldMatcher(expectedField: ELBRecordField) extends Matcher[RecordParsingResult] {

    def apply(left: RecordParsingResult): MatchResult = {
        left match {
          case RecordParsingSuccess(_) =>
            MatchResult(
              matches = false,
              "parsing succeeded with a bad backend processing time",
              ""
            )

          case RecordParsingFailure(_, errors) =>
            if(errors.length > 1) {
              MatchResult(
                matches = false,
                s"There were ${errors.length} errors instead of 1.",
                "There was a single error."
              )
            }else {
              val FieldParsingFailure(field, _) = errors.head
              MatchResult(
                matches = field == expectedField,
                s"Expected field $expectedField did not match actual field $field.",
                s"Expected field $expectedField matched actual field $field."
              )
            }
        }
    }
  }

  def failWithASingleField(expectedField: ELBRecordField) = new ShouldFailWithFieldMatcher(expectedField)
}

object ELBRecordFieldMatchers extends ELBRecordFieldMatchers