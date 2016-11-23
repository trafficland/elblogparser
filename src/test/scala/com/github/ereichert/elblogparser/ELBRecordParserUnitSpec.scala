package com.github.ereichert.elblogparser

import java.net.InetSocketAddress
import java.time.ZonedDateTime

import com.trafficland.elblogparser._
import org.scalatest.Matchers._
import org.scalatest.WordSpec

class ELBRecordParserUnitSpec extends WordSpec {

  "ELBRecordParser.parse" should {

    "parse a version 1 record that has no errors." in {
      val rawV1ELBRecord = "2016-10-19T16:00:20.863859Z elbname 172.16.1.6:54814 172.16.20.5:9000 0.000039 " +
        "0.145507 0.00003 200 404 10995116277760 5497558138880 \"GET http://some.domain.com:80/path0/path1?param0=p0&param1=p1 HTTP/1.1\""

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

    "parse a version 2 record that has no errors." in {
      val rawV2ELBRecord = "2016-10-19T16:00:20.863859Z elbname 172.16.1.6:54814 172.16.20.5:9000 0.000039 " +
        "0.145507 0.00003 200 404 10995116277760 5497558138880 \"GET http://some.domain.com:80/path0/path1?param0=p0&param1=p1 HTTP/1.1\" " +
        "\"Mozilla/5.0 (cloud; like Mac OS X; en-us) AppleWebKit/537.36.0 (KHTML, like Gecko) Version/4.0.4 Mobile/7B334b Safari/537.36.0\" some_ssl_cipher some_ssl_protocol"

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
          elbRecord.userAgent should be(Some("Mozilla/5.0 (cloud; like Mac OS X; en-us) AppleWebKit/537.36.0 (KHTML, like Gecko) Version/4.0.4 Mobile/7B334b Safari/537.36.0"))
          elbRecord.sslCipher should be(Some("some_ssl_cipher"))
          elbRecord.sslProtocol should be(Some("some_ssl_protocol"))

        case RecordParsingFailure(_, errors) => fail(errors.toString)
      }
    }
  }
}