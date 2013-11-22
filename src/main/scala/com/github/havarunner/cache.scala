package com.github.havarunner

import scala.collection.mutable
import com.github.havarunner.Reflections._
import com.github.havarunner.ConcurrencyControl._
import com.github.havarunner.SuiteCache._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

private[havarunner] object SuiteCache {
  private val cache = new mutable.HashMap[Class[_ <: HavaRunnerSuite[_]], HavaRunnerSuite[_]] with mutable.SynchronizedMap[Class[_ <: HavaRunnerSuite[_]], HavaRunnerSuite[_]]

  def suiteInstance(suiteClass: Class[_ <: HavaRunnerSuite[_]]): HavaRunnerSuite[_] =
    suiteClass.synchronized {
      cache.getOrElseUpdate(suiteClass, {
        val havaRunnerSuiteInstance: HavaRunnerSuite[_] = instantiateSuite(suiteClass)
        registerShutdownHook(havaRunnerSuiteInstance)
        havaRunnerSuiteInstance
      })
    }

  private def instantiateSuite(suiteClass: Class[_ <: HavaRunnerSuite[_]]): HavaRunnerSuite[_] = {
    val noArgConstructor = suiteClass.getDeclaredConstructor()
    noArgConstructor.setAccessible(true)
    val havaRunnerSuiteInstance: HavaRunnerSuite[_] = noArgConstructor.newInstance()
    havaRunnerSuiteInstance
  }

  private def registerShutdownHook(havaRunnerSuiteInstance: HavaRunnerSuite[_]) {
    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable() {
      def run() {
        havaRunnerSuiteInstance.afterSuite()
      }
    }))
  }
}

private[havarunner] object TestInstanceCache {
  val cache = new mutable.HashMap[ScenarioAndClass, TestInstance] with mutable.SynchronizedMap[ScenarioAndClass, TestInstance]
  val instanceGroupLocks = new mutable.HashMap[ScenarioAndClass, AnyRef] with mutable.SynchronizedMap[ScenarioAndClass, AnyRef]

  def testInstance(implicit testAndParameters: TestAndParameters): TestInstance =
    cachedTestInstance(
      testAndParameters,
      testAndParameters.partOf map suiteInstance
    )

  private def cachedTestInstance(implicit testAndParameters: TestAndParameters, suiteOption: Option[HavaRunnerSuite[_]]): TestInstance =
    instanceGroupLocks.getOrElseUpdate(testAndParameters.groupCriterion, new Object).synchronized { // Sync with instance group. Different groups may run parallel.
      cache.
        get(testAndParameters.groupCriterion).
        getOrElse({
          val instance = TestInstance(instantiate) // Do not use Map#getOrElseUpdate, because it would block unnecessarily
          cache.update(testAndParameters.groupCriterion, instance)
          instance
        })
    }
}
