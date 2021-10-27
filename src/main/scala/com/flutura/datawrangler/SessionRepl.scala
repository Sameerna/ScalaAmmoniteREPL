package com.flutura.datawrangler
import ammonite.main.Scripts
import ammonite.main
import ammonite.util.{CompilationError, Res}

import java.io.File
import scala.collection.mutable
import com.flutura.datawrangler.SessionRepl
import org.apache.spark.sql.SparkSession
import os.{Path, pwd}

import scala.tools.nsc
object SessionRepl {
  //  Map of String session name and the session object
  var sessions: mutable.Map[String, Session] = mutable.Map()
  // empty map
  println(sessions)
  //Session Object
  // while creating a session instance pass the session name
  def openSession(sessionName: String): Unit = {
        //    putting an entry in the map

    var session = Session(sessionName)
    sessions.put(sessionName,session)
    println(s"$sessionName created successfully")

  }
  def executeCode(sessionName: String, code: String): Unit = {

    //       use the session in the map ()
    if (sessions.isEmpty) println("Session empty")
    else if
    (sessions.contains(sessionName)) {
      println(s"Executing code in $sessionName")
      // code
      val sess = sessions(sessionName)
      sess.codeExec(code)
    }
      else println("Session Name mismatch")
    }
  def processLine(sessionName: String,code: String):Unit ={

    if (sessions.isEmpty) println("Session empty")
    else if
    (sessions.contains(sessionName)) {
      println(s"Executing Line in $sessionName")
      // code
      val sess = sessions(sessionName)
      sess.processLine(code)
    }
    else println("Session Name mismatch")
  }
  def execModule(sessionName: String, file: Path):Unit ={

    if (sessions.isEmpty) println("Session empty")
    else if
    (sessions.contains(sessionName)) {
      println(s"Executing module $sessionName")
      // code
      val sess = sessions(sessionName)

      sess.processModule(file)
      match {
        case Res.Failure(s) => throw new CompilationError(s)
        case Res.Exception(t, s) => throw t
        case res => res
      }
    }
    else println("Session Name mismatch")
  }
  def execFileArgs(sessionName: String,file:Path,args: List[String])={
    if (sessions.isEmpty) println("Session empty")
    else if
    (sessions.contains(sessionName)) {
      println(s"Executing File in $sessionName")
      // code
      val sess = sessions(sessionName)
      sess.run()
      // define
//      sess.start()
//      sess.join()
      sess.processFileArgs(file,args)
    }
      else println("Session Name mismatch")
    }
//
//  def sparkSessionCode(sessionName: String, sparkAppName: String): Unit= {
//    val sess = sessions(sessionName)
//    println("executing spark code")
//  }
  def closeSession(SessionName: String) = {
    sessions.remove(SessionName)
    //      println(sessions)
    println(s"$SessionName removed ")
  }
}

//sess.codeExec("test_script.spk")



