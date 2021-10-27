package com.flutura.datawrangler
import com.flutura.datawrangler.SessionRepl.{closeSession, execFileArgs, execModule, executeCode, openSession, processLine, sessions}
import com.sun.javafx.scene.control.skin.Utils.getResource

import java.io.{File, PrintWriter}
import scala.io.Source
import scala.io.Source.fromFile
object test extends App {

def session1 (SessionName:String):Unit={
  val session1 = new Session(SessionName)
  session1.start()
//    val thread = new Thread {
//      override def run: Unit ={
//
//        while (Thread.interrupted()){
//          println("Thread " + Thread.currentThread().getName() +
//            " is running.")
//        }
//      }
//    }
////  println(Thread.getAllStackTraces)
//    thread.start()
//    thread.join()
  openSession(SessionName)
  execFileArgs(SessionName,os.pwd/"test_script.sc",List("sparkApp1"))
  closeSession(SessionName)

}
  session1("Session1")
//  sparkSessionCode("Session1","spark1")
//executeCode("Session1","import $file.test_script")
//  print("app1")
//  executeCode("Session1", "test_script.spk")
//  execModule("Session1",os.path(os.pwd/"test_script.sc") app1)
// /executeCode("Session1","test_script.spk")
//  processLine("Session1","println(5)")
//  processModule("Session1",os.pwd/"src"/"main"/"scala"/"resources"/"test_script.sc")
// executeModule("Session1",os.pwd/"src"/"main"/"scala"/"resources"/"test_script.sc")
def session2 (SessionName: String)= {
  val session2 = new Session(SessionName)
  session2.start()
  session2.join(5000)
//  session2.join(1000)
//val thread = new Thread {
//  override def run: Unit ={
//    println("Thread " + Thread.currentThread().getName() +
//      " is running.")
//    while (Thread.interrupted()){
//
//    }
//  }
//}
////  println(Thread.getAllStackTraces)
//  thread.start()
  openSession(SessionName)
  execFileArgs(SessionName,os.pwd/"testSpark.sc",List("sparkApp2"))
  closeSession(SessionName)
}
  session2("Session2")

//  openSession("Session2")
//  execute("Session2","val y = 6")
//  execute("Session2", "println(y+5)")
//  closeSession("Session2")
//  closeSession("Session1")


//  openSession("Session3")
//  execFileArgs("Session3",os.pwd/"test_script.sc",List("sparkApp3"))
//  closeSession("Session3")
}
