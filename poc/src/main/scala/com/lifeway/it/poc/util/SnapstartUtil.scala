package com.lifeway.it.poc.util

import com.amazonaws.services.lambda.runtime
import com.amazonaws.services.lambda.runtime.{ClientContext, CognitoIdentity, RequestStreamHandler}

import java.io.{ByteArrayOutputStream, OutputStream}

trait SnapstartUtil {

  // $COVERAGE-OFF$  Nothing to test here at a Unit level.
  def exerciseHandler[H <: RequestStreamHandler](handler: H, input: String): Unit = {
    val inputStringRequest: java.io.InputStream =
      new java.io.ByteArrayInputStream(input.getBytes(java.nio.charset.StandardCharsets.UTF_8.name))

    val context: com.amazonaws.services.lambda.runtime.Context = new com.amazonaws.services.lambda.runtime.Context {
      override def getAwsRequestId: String         = "not available"
      override def getLogGroupName: String         = "not available"
      override def getLogStreamName: String        = "not available"
      override def getFunctionName: String         = "not available"
      override def getFunctionVersion: String      = "not available"
      override def getInvokedFunctionArn: String   = "not available"
      override def getIdentity: CognitoIdentity    = null
      override def getClientContext: ClientContext = null
      override def getRemainingTimeInMillis: Int   = 10_000
      override def getMemoryLimitInMB: Int         = 1024
      override def getLogger: runtime.LambdaLogger =
        new runtime.LambdaLogger {
          override def log(message: String): Unit      = System.out.println(message)
          override def log(message: Array[Byte]): Unit = System.out.println("ignored")
        }
    }

    val outputStream: OutputStream = new ByteArrayOutputStream()
    handler.handleRequest(inputStringRequest, outputStream, context)
    outputStream.toString
  }
  // $COVERAGE-ON$
}
