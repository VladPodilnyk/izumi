package com.github.pshirshov.izumi.distage

import com.github.pshirshov.izumi.distage.model.LoggerHook

class LoggerHookDefaultImpl extends LoggerHook {

}

object LoggerHookDefaultImpl {
  final val instance = new LoggerHookDefaultImpl()
}