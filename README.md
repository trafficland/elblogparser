# ELB Log Parser

Using the parser is pretty straight forward.

Add our Bintray repo to your project's resolvers. This is usually done in your build.

Here's an example using the build.sbt style build.

```scala

lazy val project =  Project(name, file("."))
  .settings(
    .
    .
    .
    resolvers ++= Seq(
      Resolver.url("bintray-trafficland-oss", url("https://dl.bintray.com/trafficland/oss/"))(
        Patterns(isMavenCompatible = false, Resolver.localBasePattern)
      )
    )
    .
    .
    .
  )
```

Use the parser.

```scala
import com.trafficland.elblogparser._

def main(args: Array[String]): Unit = {
    val parser = ELBRecordParser()
    
    val unparsedRecord: String = ??? // A string from an ELB access log.   
    parser.parse(unparsedRecord) { result: RecordParsingResult =>
      ??? // Do something with the result.  
    }
}
```

### Design Notes

For efficiency, the parser is built for reuse. Every time parser.parse(_) is called the internal buffers are cleared. This cuts down on allocations.

The reason parser.parse(_) takes a callback, as opposed to taking a Seq[String] and returning a Seq[RecordParsingResult], is because if the ELB producing billions of records it may not be 
 reasonable to store the records in memory. By taking a callback the developer using this library is given a functional way of handling the results.
