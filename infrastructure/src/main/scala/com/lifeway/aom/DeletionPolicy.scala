package com.lifeway.aom

import software.amazon.awscdk.{CfnResource, IAspect, RemovalPolicy}
import software.constructs.IConstruct

class DeletionPolicy extends IAspect {

  override def visit(node: IConstruct): Unit =
    node match {
      case c: CfnResource => c.applyRemovalPolicy(RemovalPolicy.DESTROY)
      case _              =>
    }

}
