package com.github.pshirshov.izumi.distage.planning

import com.github.pshirshov.izumi.distage.model.definition.Binding.{ImplBinding, SetElementBinding}
import com.github.pshirshov.izumi.distage.model.definition.{Binding, Module, ModuleBase}
import com.github.pshirshov.izumi.distage.model.planning.PlanningHook
import com.github.pshirshov.izumi.distage.model.reflection.universe.RuntimeDIUniverse

trait EarlyAutoSetHook extends PlanningHook {
  def elementOf(b: Binding): Option[RuntimeDIUniverse.DIKey]

  override def hookDefinition(defn: ModuleBase): ModuleBase = {
    val autoSetsElements = defn.bindings.flatMap {
      case i: ImplBinding =>
        elementOf(i)
          .flatMap {
            key =>
              Some(SetElementBinding(key, i.implementation))
          }
          .toSeq

      case _ =>
        None
    }

    Module.make(defn.bindings ++ autoSetsElements)
  }
}

