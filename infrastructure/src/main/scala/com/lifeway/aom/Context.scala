package com.lifeway.aom

import com.lifeway.consumersolutions.eventsourcing.infrastructure.context.VPC.{PrivateLambdaVPC, PublicVPC}
import com.lifeway.consumersolutions.eventsourcing.infrastructure.context.{
  AlarmTopic,
  Environment,
  EnvironmentAware,
  KafkaConfig,
  NabiProdAlarmTopic,
}

trait AppContext extends EnvironmentAware with AlarmTopic with PublicVPC with PrivateLambdaVPC with KafkaConfig {

  val appDomain = "aom"
  val awsEnv: software.amazon.awscdk.Environment
  val kafkaInternalDomainTopic: String

  val permissionsAPIURL: String
  val roleViewAPIURL: String
}

case class SandboxContext(developerName: String)
    extends AppContext
    with NabiProdAlarmTopic
    with PublicVPC.Nonprod
    with PrivateLambdaVPC.NonProd
    with KafkaConfig.MskNonProd {

  override val environment: Environment = Environment.Sandbox(developerName)
  override val awsEnv: software.amazon.awscdk.Environment           = software.amazon.awscdk.Environment.builder().account("345777899508").region("us-east-1").build()

  override val kafkaAuthenticationSecretArn =
    "arn:aws:secretsmanager:us-east-1:345777899508:secret:customer-kafka-api-key"

  val kafkaInternalDomainTopic   = "dev.CustomerInternal"
  override val permissionsAPIURL = s"https://customer-api.int.lifeway.com/permission"
  override val roleViewAPIURL    = s"https://customer-api.int.lifeway.com/roles"
}

case object IntContext
    extends AppContext
    with NabiProdAlarmTopic
    with PublicVPC.Nonprod
    with PrivateLambdaVPC.NonProd
    with KafkaConfig.MskNonProd {

  override val environment: Environment = Environment.Int
  override val awsEnv: software.amazon.awscdk.Environment           = software.amazon.awscdk.Environment.builder().account("903189529808").region("us-east-1").build()

  override val kafkaAuthenticationSecretArn =
    "arn:aws:secretsmanager:us-east-1:903189529808:secret:customer-kafka-api-key-int"

  val kafkaInternalDomainTopic   = "int.CustomerInternal"
  override val permissionsAPIURL = s"https://customer-api.int.lifeway.com/permission"
  override val roleViewAPIURL    = s"https://customer-api.int.lifeway.com/roles"
}

