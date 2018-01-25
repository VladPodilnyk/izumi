package org.bitbucket.pshirshov.izumi.distage.provisioning.strategies

import java.lang.reflect.Method

import org.bitbucket.pshirshov.izumi.distage.commons.{ReflectionUtil, TraitTools}
import org.bitbucket.pshirshov.izumi.distage.model.plan.Association
import org.bitbucket.pshirshov.izumi.distage.model.plan.ExecutableOp.WiringOp
import org.bitbucket.pshirshov.izumi.distage.provisioning.cglib.{CgLibTraitMethodInterceptor, CglibTools}
import org.bitbucket.pshirshov.izumi.distage.provisioning.{OpResult, ProvisioningContext}


import scala.reflect.runtime.currentMirror

class TraitStrategyDefaultImpl extends TraitStrategy {
  def makeTrait(context: ProvisioningContext, t: WiringOp.InstantiateTrait): Seq[OpResult] = {
    val traitDeps = context.narrow(t.wiring.associations.map(_.wireWith).toSet)

    val wiredMethodIndex = TraitStrategyDefaultImpl.makeIndex(t.wiring.associations)

    val instanceType = t.wiring.instanceType
    val runtimeClass = currentMirror.runtimeClass(instanceType.tpe)
    val dispatcher = new CgLibTraitMethodInterceptor(wiredMethodIndex, traitDeps)

    CglibTools.mkdynamic(dispatcher, instanceType, runtimeClass, t) {
      instance =>
        TraitTools.initTrait(instanceType, runtimeClass, instance)
        Seq(OpResult.NewInstance(t.target, instance))
    }
  }


}

object TraitStrategyDefaultImpl {
  final val instance = new TraitStrategyDefaultImpl()

  def makeIndex(t: Seq[Association.Method]): Map[Method, Association.Method] = {
    t.map {
      m =>
        ReflectionUtil.toJavaMethod(m.context.definingClass, m.symbol) -> m
    }.toMap
  }
}
