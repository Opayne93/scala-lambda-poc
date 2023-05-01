package com.lifeway.it.poc

import io.circe.generic.auto._
import io.github.mkotsur.aws.handler.Lambda._
import io.github.mkotsur.aws.handler.Lambda
import java.util.Date
import io.circe.parser.decode
import org.crac.{Context, Core, Resource}
import com.lifeway.it.poc.util.SnapstartUtil
import com.lifeway.consumersolutions.eventsourcing.infrastructure.common.util.{Logger, LambdaLogger, Util}
import com.lifeway.consumersolutions.eventsourcing.infrastructure

case class TestRequest(input: String)

case class TestResult(output: String, date: String)

class TestHandler extends Lambda[TestRequest, TestResult] with Resource {
  Core.getGlobalContext.register(this);

  val logger: Logger                                       = LambdaLogger(Logger.LogLevel(Util.env("LOG_LEVEL"), Logger.LogLevel.Info))

  override def beforeCheckpoint(context: Context[_ <: Resource]): Unit = {
    logger.info("Before lambda snapshot")
    TestHandler.beforeCheckpoint(this)
  }

  override def afterRestore(context: Context[_ <: Resource]): Unit = 
    logger.info("After lambda snapshot")
  


  override def handle(req: TestRequest) = Right(TestResult(req.input.reverse, new Date().toInstant().toString()))

}

object TestHandler extends SnapstartUtil {
  
  def beforeCheckpoint(
      handler: TestHandler
  ): Either[TestRequest, TestResult] = {
    val rawRequest: String = """{ "input": "hello jvm lambda" }"""

    //Exercise the handler wrapper itself.
    exerciseHandler(handler, rawRequest)

    //call our code again (so we can write  a test on this return type)
    val request: TestRequest = decode[TestRequest](rawRequest).toOption.get
    Right(TestResult("test response", new Date().toInstant().toString()))
  }

  def primeSeive(s: LazyList[Int], head: Int) = { 
    val r = s filter {
            x => {
                if (x % head != 0) {
                  true
                } else {
                  false
                }
            }
    }
    r
  }
  def numberStream(g: Int): LazyList[Int] = LazyList.from(g)
      
  def sieve_of_Eratosthenes(stream: LazyList[Int]): 
  LazyList[Int] = stream.head #:: sieve_of_Eratosthenes(
      primeSeive(stream.tail, stream.head))
      
  val no_of_primes = sieve_of_Eratosthenes(numberStream(2))
  
  def calculatePrimesOfN(n: Int): Either[Nothing, TestResult] = {
    (no_of_primes take n ) foreach {
      println(_)
    }
    Right(TestResult("complete", new Date().toInstant().toString()))
  }


}
