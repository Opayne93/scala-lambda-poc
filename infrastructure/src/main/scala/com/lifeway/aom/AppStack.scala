package com.lifeway.aom

import com.lifeway.consumersolutions.eventsourcing.infrastructure.SnsEventBridge
import com.lifeway.consumersolutions.eventsourcing.infrastructure.persistence.{Dynamo, DynamoEventSourcingCoreTables}
import software.amazon.awscdk.{Duration, RemovalPolicy, Stack, StackProps, Tags}
import software.constructs.Construct
import com.lifeway.consumersolutions.eventsourcing.infrastructure.context.Environment
import com.lifeway.consumersolutions.eventsourcing.infrastructure.context.KafkaConfig.KafkaAuthenticationType.{
  BasicAuth,
  Scram512,
}
import com.lifeway.consumersolutions.eventsourcing.infrastructure.context.VPC.PrivateLambdaVPC
import com.lifeway.consumersolutions.eventsourcing.infrastructure.function.{ScalaJsAsset, ScalaJsFunction}
import software.amazon.awscdk.services.apigatewayv2.alpha.{AddRoutesOptions, HttpMethod}
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.HttpLambdaIntegration
import software.amazon.awscdk.services.apigatewayv2.alpha.{ApiMapping, DomainName, HttpApi, HttpStage}
import software.amazon.awscdk.services.certificatemanager.Certificate
import software.amazon.awscdk.services.dynamodb.{
  Attribute,
  AttributeType,
  BillingMode,
  GlobalSecondaryIndexProps,
  ProjectionType,
  StreamViewType,
  Table,
  TableProps,
}
import software.amazon.awscdk.services.ec2.{IVpc, SecurityGroup, Vpc, VpcAttributes}
import software.amazon.awscdk.services.iam.{ManagedPolicy, PermissionsBoundary}
import software.amazon.awscdk.services.lambda.StartingPosition
import software.amazon.awscdk.services.lambda.eventsources.{ManagedKafkaEventSource, SelfManagedKafkaEventSource}
import software.amazon.awscdk.services.route53.targets.{ApiGatewayDomain, ApiGatewayv2DomainProperties}
import software.amazon.awscdk.services.route53.{
  ARecord,
  HostedZone,
  HostedZoneProviderProps,
  RecordSet,
  RecordTarget,
  RecordType,
}
import software.amazon.awscdk.services.secretsmanager.Secret
import software.amazon.awscdk.services.ssm
import software.amazon.awscdk.services.ssm.SecureStringParameterAttributes
import software.amazon.awscdk.services.events
import software.amazon.awscdk.services.events.{EventBus, EventBusProps}

import software.amazon.awscdk.services.events.targets.SqsQueue
import software.amazon.awscdk.services.sqs.{DeduplicationScope, FifoThroughputLimit, Queue}
import com.amazonaws.services.lambda.runtime.api.client.LambdaRequestHandler
import software.amazon.awscdk.services.lambda
import software.amazon.awscdk.AssetOptions
import software.amazon.awscdk.BundlingOptions

class AppStack(
    scope: Construct,
    id: String,
    context: AppContext,
    props: StackProps = null,
) extends Stack(scope, id, props) {


  val scalaPocHandler = new lambda.Function(
      this,
      "scala-lambda-poc",
      lambda.FunctionProps
        .builder()
        .description("a poc lambda using scala")
        .runtime(lambda.Runtime.JAVA_11)
        .code(lambda.Code.fromAsset("./build/poc.zip"))
        .handler("com.lifeway.it.poc.TestHandler::handle")
        .timeout(software.amazon.awscdk.Duration.seconds(30))
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
