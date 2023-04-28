package com.lifeway.aom.util

import com.lifeway.consumersolutions.eventsourcing.infrastructure.context.EnvironmentAware
import software.amazon.awscdk.Duration
import software.amazon.awscdk.services.sqs.{DeduplicationScope, FifoThroughputLimit, Queue}
import software.constructs.Construct

object FifoQueueBuilder {

  def apply(scope: Construct, id: String, context: EnvironmentAware, projectName: String, queueName: String) = {
    val queueNameWithFifo = if (queueName.endsWith(".fifo")) queueName else s"$queueName.fifo"

    Queue.Builder
      .create(scope, id)
      .queueName(s"$projectName-${context.environment.environmentFullName}-$queueNameWithFifo")
      .fifo(true)
      .contentBasedDeduplication(false)
      .deduplicationScope(DeduplicationScope.MESSAGE_GROUP)
      .fifoThroughputLimit(FifoThroughputLimit.PER_MESSAGE_GROUP_ID)
      .visibilityTimeout(Duration.seconds(30))
      .receiveMessageWaitTime(Duration.seconds(20))
      .retentionPeriod(Duration.days(7))
      .build()
  }

}
