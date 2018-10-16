package {{organization}}.{{name}}

import com.amazonaws.services.s3.event.S3EventNotification
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.fasterxml.jackson.databind.ObjectMapper
import com.typesafe.scalalogging.Logger
import io.onema.json.JavaExtensions._
import io.onema.json.Mapper

import scala.collection.JavaConverters._


class CopyLogic {

  //--- Fields ---
  protected val log = Logger("logic")
  private val s3Client: AmazonS3 = AmazonS3ClientBuilder.defaultClient()

  //--- Methods ---
  def process(message: String, destinationBucket: String): Unit = {
    log.info(message)
    val mapper: ObjectMapper = Mapper.allowUnknownPropertiesMapper
    val s3Records = message.jsonDecode[S3EventNotification](mapper).getRecords.asScala
    s3Records.foreach(x => {
      val sourceBucket = x.getS3.getBucket.getName
      val sourceKey = x.getS3.getObject.getKey
      s3Client.copyObject(sourceBucket, sourceKey, destinationBucket, sourceKey)
    })
  }
}
