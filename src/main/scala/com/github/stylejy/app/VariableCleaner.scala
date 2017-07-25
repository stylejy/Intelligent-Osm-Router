package com.github.stylejy.app

import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer

/**
  * Created by stylejy on 19/07/2017.
  */
trait VariableCleaner {

  def resetVariable[T](inputVar: T) = inputVar match {
      case _: ArrayBuffer[_] => inputVar.asInstanceOf[ArrayBuffer[Any]].clear()
      case _: Map[_,_] => inputVar.asInstanceOf[Map[Any, Any]].clear()
      case _ => {
        println("Function resetVariable doesn't support the type")
        throw new Exception("Function resetVariable doesn't support the type")
      }
  }
}
