package com.lifeway.aom

import software.amazon.awscdk.{App, DefaultStackSynthesizer, StackProps}
import software.amazon.awscdk.RemovalPolicy
import software.amazon.awscdk.services.dynamodb.Table
import software.amazon.awscdk.Aspects
import software.amazon.awscdk.IStackSynthesizer

import scala.util.Try

object CDKApp {

  def main(args: Array[String]): Unit = {
    val app = new App()

    val devName: Option[String] = Try(app.getNode.tryGetContext("devName").toString).toOption

    devName match {
      case Some(developer) =>
        val sandboxContext = SandboxContext(developer)
        buildEnvStack(app, developer, sandboxContext)
      case None =>
        val intContext = IntContext
        buildEnvStack(app, "int", intContext)
    }

    app.synth()
  }

  def synthesizer: IStackSynthesizer = DefaultStackSynthesizer.Builder.create().qualifier("customer").build()

  def buildEnvStack(
      app: App,
      env: String,
      context: AppContext,
  ): Unit = {

    val appStack = new AppStack(
      app,
      s"scala-poc-$env-appstack",
      context,
      StackProps
        .builder()
        .stackName(s"scala-poc-$env-appstack")
        .synthesizer(synthesizer)
        .env(context.awsEnv)
        .build(),
    )

  }

}
