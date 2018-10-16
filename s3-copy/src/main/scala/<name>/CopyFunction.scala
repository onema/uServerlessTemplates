package {{organization}}.{{name}}

import com.amazonaws.serverless.proxy.model.{AwsProxyRequest, AwsProxyResponse}
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.{S3Event, SNSEvent}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import io.onema.userverless.configuration.lambda.EnvLambdaConfiguration
import io.onema.userverless.function.LambdaHandler
import io.onema.json.Extensions._
import org.apache.http.HttpStatus

import scala.collection.JavaConverters._


class CopyFunction extends LambdaHandler[SNSEvent, String] with EnvLambdaConfiguration {

  //--- Fields ---
  val logic = new CopyLogic()
  private val destinationBucket: String = getValue("destination/bucket").get

  //--- Methods ---
  def execute(snsEvent: SNSEvent, context: Context): String = {
    snsEvent.getRecords.asScala.foreach(x => {
      logic.process(x.getSNS.getMessage, destinationBucket)
    })
    "OK"
  }
}
