package com.lifeway.aom

import software.amazon.awscdk.{Duration, RemovalPolicy, Stack, StackProps, Tags}
import software.constructs.Construct
import com.lifeway.consumersolutions.eventsourcing.infrastructure.context.Environment
import software.amazon.awscdk.services.lambda.StartingPosition
import software.amazon.awscdk.services.lambda
import software.amazon.awscdk.AssetOptions
import software.amazon.awscdk.BundlingOptions
import software.amazon.awscdk.services.iam.ManagedPolicy

class AppStack(
    scope: Construct,
    id: String,
    context: AppContext,
    props: StackProps = null,
) extends Stack(scope, id, props) {


  val permissionsBoundary = ManagedPolicy.fromManagedPolicyName(
    this,
    "permissions-boundary",
    "lifeway/systems/digitalexperience/aom/customer-permissions-boundary",
  )

  val scalaPocHandler = new lambda.Function(
      this,
      "scala-lambda-poc",
      lambda.FunctionProps
        .builder()
        .description("a poc lambda using scala")
        .runtime(lambda.Runtime.JAVA_11)
        .code(lambda.Code.fromAsset("./poc/.build/poc.zip"))
        .handler("com.lifeway.it.poc.TestHandler::handle")
        .timeout(Duration.seconds(30))
        .memorySize(1024)
        .build()
  )

  Tags.of(this).add("Department", "digital-experience")
  Tags.of(this).add("Owner", "aom")
  Tags.of(this).add("Project", "poc")
  Tags.of(this).add("Application", "scala-lambda-poc")
  Tags.of(this).add("Role", "poc")
  Tags.of(this).add("Environment", context.environment.environmentName)
}
