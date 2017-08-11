package com.github.stylejy.app.Helpers.System

import java.io._

/**
  * Created by stylejy on 19/07/2017.
  */
object FileIOHelper {
  def in(fileName: String): DataInputStream = {
    new DataInputStream(new FileInputStream(new File(fileName)))
  }

  def out(fileName: String): DataOutputStream = {
    new DataOutputStream(new FileOutputStream(new File(fileName)))
  }

}
