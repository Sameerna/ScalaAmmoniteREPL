package com.flutura.datawrangler

import ammonite.compiler.{CompilerBuilder, DefaultCodeWrapper}
import ammonite.compiler.iface.{CodeWrapper, Preprocessor, CompilerBuilder => ICompilerBuilder}
import ammonite.interp.{Interpreter, Watchable}
import ammonite.main.{Defaults, Scripts}
import ammonite.interp.Interpreter.{SheBang, SheBangEndPattern, predefImports}
import ammonite.interp.api.AmmoniteExit
import ammonite.interp.script.AmmoniteBuildServer.classPathWhitelist
import ammonite.main
import ammonite.repl.Signaller.handlers.result
import ammonite.repl.{Repl, ReplApiImpl, SessionApiImpl}
import ammonite.repl.api.{FrontEnd, History, ReplLoad}
import ammonite.runtime.{Frame, ImportHook, Storage}
import ammonite.util.Res.Success
import ammonite.util.Util.{CodeSource, newLine, normalizeNewlines}
import ammonite.util.{Bind, Colors, CompilationError, Evaluated, Imports, Name, PredefInfo, Printer, Ref, Res, ScriptOutput, Tag, Util}

import java.nio.file.NoSuchFileException
import scala.tools.nsc.Properties.scalacDir.path
//import com.flutura.datawrangler.SessionRepl.sparkSessionCode
import org.apache.spark.sql.SparkSession
import scala.util.{Left,Right}
import java.io.{ByteArrayOutputStream, PrintStream}
import scala.collection.mutable
//import scala.meta.internal.Scaladoc.Tag
import scala.tools.nsc.io.File

case class Session(SessionName : String) extends Thread{
  override def run()
    {
    println("Thread " + Thread.currentThread().getName() + " is running.")
     println( Thread.currentThread().getState)
//      Thread.currentThread().join(1000)
    }

    // abst function or wrapper function
  val outBytes = new ByteArrayOutputStream
  val errBytes = new ByteArrayOutputStream
  val resBytes = new ByteArrayOutputStream
  def outString = new String(outBytes.toByteArray)
  def resString = new String(resBytes.toByteArray)

  val warningBuffer = mutable.Buffer.empty[String]
  val errorBuffer = mutable.Buffer.empty[String]
  val infoBuffer = mutable.Buffer.empty[String]
  val printer0 = Printer(
    new PrintStream(outBytes),
    new PrintStream(errBytes),
    new PrintStream(resBytes),
    x => warningBuffer.append(x + Util.newLine),
    x => errorBuffer.append(x + Util.newLine),
    x => infoBuffer.append(x + Util.newLine)
  )
  val compilerBuilder: ICompilerBuilder = CompilerBuilder
  var allOutput = ""

  def predef: (String, Option[os.Path]) = ("", None)
  def codeWrapper: CodeWrapper = DefaultCodeWrapper
  val tempDir = os.Path(
    java.nio.file.Files.createTempDirectory("ammonite-tester")
  )
  val storage = new Storage.Folder(tempDir)
  val initialClassLoader = Thread.currentThread().getContextClassLoader
  val frames = Ref(List(Frame.createInitial(initialClassLoader)))
  val sess0 = new SessionApiImpl(frames)
  val baseImports = ammonite.main.Defaults.replImports ++ Interpreter.predefImports
  val basePredefs = Seq(
    PredefInfo(Name("testPredef"), predef._1, false, predef._2)
  )
  val customPredefs = Seq()
  val parser = ammonite.compiler.Parsers
  var currentLine = 0
  val interp = try {
    new Interpreter(
      compilerBuilder,
      parser,
      printer0,
      storage = storage,
      wd = os.pwd,
      colors = Ref(Colors.BlackWhite),
      getFrame = () => frames().head,
      createFrame = () => {
        val f = sess0.childFrame(frames().head);
        frames() = f :: frames();
        f
      },
      initialClassLoader = initialClassLoader,
      replCodeWrapper = codeWrapper,
      scriptCodeWrapper = codeWrapper,
      alreadyLoadedDependencies = Defaults.alreadyLoadedDependencies(),
      importHooks = ImportHook.defaults,
      classPathWhitelist = ammonite.repl.Repl.getClassPathWhitelist(thin = true)
    )

  } catch {
    case e: Throwable =>
      println(infoBuffer.mkString)
      println(outString)
      println(resString)
      println(warningBuffer.mkString)
      println(errorBuffer.mkString)
      throw e
  }
  val extraBridges = Seq(
    (
      "ammonite.TestReplBridge",
      "test",
      new TestReplApi {
        def message = "ba"
      }
    ),
    (
      "ammonite.repl.ReplBridge",
      "repl",
      new ReplApiImpl {
        replApi =>
        def replArgs0 = Vector.empty[Bind[_]]

        def printer = printer0

        def sess = sess0

        val prompt = Ref("@")
        val frontEnd = Ref[FrontEnd](null)

        def lastException: Throwable = null

        def fullHistory = storage.fullHistory()

        def history = new History(Vector())

        val colors = Ref(Colors.BlackWhite)

        def newCompiler() = interp.compilerManager.init(force = true)

        def fullImports = interp.predefImports ++ imports

        def imports = frames().head.imports

        def usedEarlierDefinitions = frames().head.usedEarlierDefinitions

        def width = 80

        def height = 80

        object load extends ReplLoad with (String => Unit) {

          def apply(line: String) = {
            interp.processExec(line, currentLine, () => currentLine += 1) match {
              case Res.Failure(s) => throw new CompilationError(s)
              case Res.Exception(t, s) => throw t
              case _ =>
            }
          }

          def exec(file: os.Path): Unit = {
            interp.watch(file)
            apply(normalizeNewlines(os.read(file)))
          }
        }

        def _compilerManager = interp.compilerManager
      }))
  def skipSheBangLine(code: String) = {
    val newLineLength = newLine.length

    /**
     * the skipMultipleLines function is necessary to support the parsing of
     * multiple shebang lines. The NixOs nix-shell normally uses 2+ shebang lines.
     */
    def skipMultipleLines(ind: Int = 0): Int = {
      val index = code.indexOf('\n', ind)
      if (code.substring(index + 1).startsWith(SheBang))
        skipMultipleLines(ind + index + 1)
      else index - (newLineLength - 1)
    }

    if (code.startsWith(SheBang)) {
      val matcher = SheBangEndPattern matcher code
      val shebangEnd = if (matcher.find) matcher.end else skipMultipleLines()
      val numberOfStrippedLines = newLine.r.findAllMatchIn(code.substring(0, shebangEnd)).length
      (newLine * numberOfStrippedLines) + code.substring(shebangEnd)
    } else
      code
  }
  val wd = os.pwd
  //val wd : os.Path
  def watch(p: os.Path): Unit = interp.watch(p)
  val stmts = Seq(
    "println(2+2)",
    "println(6+3)"
  )
  val wrapperName = Name("cmd" + currentLine)
  val processed: Preprocessor.Output = processed
  val codeSource = CodeSource(
    wrapperName,
    Seq(),
    Seq(Name("ammonite"), Name("$sess")),
    Some(wd / "(console)")
  )



  // processLine
  def processLine(code: String): Res[Evaluated] = {
    interp.processLine(code, stmts, currentLine, false, () => currentLine += 1)
    match {
      case res => res
    }
  }
  //  def evaluateLine(processed: Preprocessor.Output,
  //                   fileName: String,
  //                   indexedWrapperName: Name,
  //                   silent: Boolean = false,
  //                   incrementLine: () => Unit): Res[(Evaluated, Tag)] ={
  //    interp.evaluateLine(processed,fileName,indexedWrapperName,false,() =>currentLine +=1)
  //      val tag1 =  ammonite.util.Tag
  //     match {
  ////        case Success(tag: Tag) =>tag1
  //        case res => res
  //      }
  //  }
  // processExec
  def codeExec(code: String): ammonite.util.Res[ammonite.util.Imports] = {
    interp.processExec(code, currentLine, () => currentLine += 1)
    match {
      case Res.Failure(s) => throw new CompilationError(s)
      case Res.Exception(t, s) => throw t
      case res => res
    }
  }
  // processModule
  def processModule(file: os.Path): Res[ScriptOutput.Metadata] = {
    // run in different threads
    watch(file)
    val (pkg, wrapper) = ammonite.util.Util.pathToPackageWrapper(
      Seq(Name("dummy")),
      file relativeTo wd
    )
    interp.processModule(
      normalizeNewlines(os.read(file)),
      CodeSource(
        wrapper,
        pkg,
        Seq(Name("ammonite"), Name("$file")),
        Some(wd)
      ),
      autoImport = true,
      extraCode = "",
      hardcoded = false
    ) match {
      case Res.Failure(s) => throw new CompilationError(s)
      case Res.Exception(t, s) => throw t
      case res => res
    }
  }
  //  def runScript(path: os.Path,
  //                scriptArgs: Seq[String])
  //  : (Res[Any], Seq[( Long)]) = {
  //
  //  }
  //Process Single Block
  def processSingleBlock(codeSource: CodeSource, name: Name): Res[(Evaluated, Tag)] = {
    interp.processSingleBlock(processed, codeSource, name) match {
      case res => res
    }
  }
  val script = Scripts
  //outside th block: same as interp
// function curry/ Higher order functions.

  def processFileArgs(file: os.Path, args: List[String])
  :Res[Any] = {
    println(script.hashCode() + " = script HashCode ")
    println(this.interp.hashCode() + " = HashCode")
    val output = script.runScript(os.pwd, file, this.interp, args)
//    script.finalize()
    output
  }
}