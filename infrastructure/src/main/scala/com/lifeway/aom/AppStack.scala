package com.lifeway.aom

import software.amazon.awscdk.{Duration, RemovalPolicy, Stack, StackProps, Tags}
import software.constructs.Construct
import com.lifeway.consumersolutions.eventsourcing.infrastructure.context.Environment
import software.amazon.awscdk.services.lambda.StartingPosition
import software.amazon.awscdk.services.lambda
import software.amazon.awscdk.AssetOptions
import software.amazon.awscdk.BundlingOptions
import software.amazon.awscdk.services.iam.ManagedPolicy
import software.amazon.awscdk.services.iam.PermissionsBoundary

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

  PermissionsBoundary.of(this).apply(permissionsBoundary)
  
  val scalaPocHandler = new lambda.Function(
      this,
      "customer-scala-lambda-poc",
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

  val cfnFunction = scalaPocHandler.getNode().getDefaultChild().asInstanceOf[lambda.CfnFunction]
  cfnFunction.addPropertyOverride("SnapStart", java.util.Map.of("ApplyOn", "PublishedVersions"))

  val version = scalaPocHandler.getCurrentVersion()

// private Version createUnicornLambdaFunction() {
//         var lambda =  Function.Builder.create(this, "UnicornStoreSpringFunction")
//                 .runtime(Runtime.JAVA_11)
//                 .functionName("unicorn-store-spring")
//                 .memorySize(2048)
//                 .timeout(Duration.seconds(29))
//                 .code(Code.fromAsset("../../software/unicorn-store-spring/target/store-spring-1.0.0-aws.jar"))
//                 .handler("org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest")
//                 .vpc(infrastructureStack.getVpc())
//                 .securityGroups(List.of(infrastructureStack.getApplicationSecurityGroup()))
//                 .environment(Map.of(
//                     "SPRING_DATASOURCE_PASSWORD", infrastructureStack.getDatabaseSecretString(),
//                     "SPRING_DATASOURCE_URL", infrastructureStack.getDatabaseJDBCConnectionString(),
//                     "SPRING_DATASOURCE_HIKARI_maximumPoolSize", "1",
//                     "AWS_SERVERLESS_JAVA_CONTAINER_INIT_GRACE_TIME", "500"
//                     ,"JAVA_TOOL_OPTIONS", "-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
//                 ))
//                 .build();
//
//         // Enable Snapstart
//         CfnFunction cfnFunction = (CfnFunction) lambda.getNode().getDefaultChild();
//         cfnFunction.addPropertyOverride("SnapStart", Map.of("ApplyOn", "PublishedVersions"));
//         // Return version
//         return lambda.getCurrentVersion();
//     }

  Tags.of(this).add("Department", "digital-experience")
  Tags.of(this).add("Owner", "aom")
  Tags.of(this).add("Project", "poc")
  Tags.of(this).add("Application", "scala-lambda-poc")
  Tags.of(this).add("Role", "poc")
  Tags.of(this).add("Environment", context.environment.environmentName)
}
