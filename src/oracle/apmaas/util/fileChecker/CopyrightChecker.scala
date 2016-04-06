package oracle.apmaas.util.fileChecker

import java.io.{FileNotFoundException, FileFilter, IOException, File}
import java.nio.file.{Paths, Files}

import scala.actors.migration.pattern
import scala.io.Source

/**
  * Created by yiyitan on 4/5/2016.
  */
class CopyrightChecker() {
      private val COPYRIGHT_BLOCK_LINE_NUM = 2
      private val COPYRIGHT_FIRST_LINE_PATTERN =
        "\\s*Copyright\\s*\\(c\\)\\s*([0-9]{4}|([0-9]{4})\\s*\\-\\s*([0-9]{4}))\\s*Oracle\\s*Corporation,\\s*Redwood\\s*Shores,\\s*CA,\\s*USA"
      private val COPYRIGHT_SECOND_LINE_PATTERN = "\\s*(a|A)ll\\s*(r|R)ights\\s*(r|R)eserved\\s*"

      private val COPYRIGHT_START_LINE_KEYWORD_NUM = 5
      private val COPYRIGHT_START_LINE_KEYWORD_NUM_Threshold = 2
      private val COPYRIGHT_START_LINE_KEYWORD_1 = "copyright"
      private val COPYRIGHT_START_LINE_KEYWORD_2 = "oracle"
      private val COPYRIGHT_START_LINE_KEYWORD_3 = "coporation"
      private val COPYRIGHT_START_LINE_KEYWORD_4 = "redwood"
      private val COPYRIGHT_START_LINE_KEYWORD_5 = "shores"

      private val APM_WLDF_INTERNAL_FILE_NAME = "apm-wldf-INTERNAL-RELEASE.properties"
      private val APM_WLDF_FUTURE_FILE_NAME = "apm-wldf-FUTURE.properties"

      private val COPYRIGHT_OK = 0
      private val COPYRIGHT_Not_Present = 0x0001
      private val COPYRIGHT_Wrong_Format = 0x0002

      private val FILE_EXT_NAME_Java = ".java"
      val FILE_EXT_NAME_PROPERTIES = "properties"

      private val CURRENT_DIR  = "."
      private val RESULT_FILE_NAME_Prefix  = "crCheckResult_"
      private var incorrectFilePaths: Map[Integer, List[String]] = null
      private var copyrightPatterns: Array[String] = null
      private var startLineKeywords: Array[String] = null

      incorrectFilePaths = Map()
      copyrightPatterns = new Array[String](COPYRIGHT_BLOCK_LINE_NUM)
      copyrightPatterns(0) = COPYRIGHT_FIRST_LINE_PATTERN
      copyrightPatterns(1) = COPYRIGHT_SECOND_LINE_PATTERN

      startLineKeywords = new Array[String](COPYRIGHT_START_LINE_KEYWORD_NUM)
      startLineKeywords(0) = COPYRIGHT_START_LINE_KEYWORD_1
      startLineKeywords(1) = COPYRIGHT_START_LINE_KEYWORD_2
      startLineKeywords(2) = COPYRIGHT_START_LINE_KEYWORD_3
      startLineKeywords(3) = COPYRIGHT_START_LINE_KEYWORD_4
      startLineKeywords(4) = COPYRIGHT_START_LINE_KEYWORD_5

      def getIncorrectFilePaths() : Map[Integer, List[String]] = incorrectFilePaths

      def checkPath(paramPath: String, recursive: Boolean) = {
          var path = paramPath
          if(paramPath == null) path = CURRENT_DIR
          var file = new File(path)
          var canonicalPath = ""

          try {
            canonicalPath = file.getCanonicalPath()
            if (file.exists()) {
              if (file.isFile)
                checkFile(file)
              else if (file.isDirectory)
                checkDir(file, recursive)

              writeResultToFile(canonicalPath)
            } else {
              // writeException
            }
          } catch  {
            case e: IOException => e.printStackTrace()
          }
      }
      // scala recursive function need a return type
      def checkDir(dir : File, recursive: Boolean) : Boolean = {
          try {
              var path = dir.getCanonicalPath

              var files : Array[File] = dir.listFiles()
              if (files != null) {
                  // approach 1: use for loop
                 /*
                  for (curFile <- files if curFile.isFile ) {
                      checkFile(curFile)
                  }
                  */
                  // approach 2: use function, pass function to a loop
                  var isCheck = (file : File) => {
                      if (file.isFile()) checkFile(file)
                  }
                  files.foreach((curFile : File) => isCheck(curFile))

                  if (recursive) {
                      isCheck = (file: File) => {
                        if (file.isDirectory) checkDir(file, true)
                      }
                  }
              }
          } catch {
            case e: IOException => e.printStackTrace()
          }
          return false
      }

      def checkFile(file : File) = {
          var caseNum = COPYRIGHT_Not_Present
          var filePath = ""
          try {
              filePath = file.getCanonicalPath
              val src = Source.fromFile(file)
              var i = 0
              for (line <- src.getLines()) {
                  caseNum = checkStartLine(line)
                  if (caseNum != COPYRIGHT_Not_Present) {
                      if (caseNum == COPYRIGHT_OK) {
                        if ((i + 1) >= src.getLines().size) caseNum = COPYRIGHT_Wrong_Format
                        else {
                          var varLine = src.getLines().take(i + 1).toString()
                          caseNum = checkFollowingLine(varLine, 2)
                          dumpToResult(caseNum,filePath)
                          // jump out of function
                        }
                      }
                  }
              }
          } catch {
             case fe: FileNotFoundException => fe.printStackTrace()
             case  e: IOException => e.printStackTrace()
          }
          dumpToResult(caseNum,filePath)
      }

      def dumpToResult = (caseNum : Integer, filePath: String) => {
          if (incorrectFilePaths.contains(caseNum)) {
              var previousList = incorrectFilePaths.get(caseNum).get :+ filePath
              incorrectFilePaths = incorrectFilePaths + (caseNum -> previousList)
          } else {
            incorrectFilePaths = incorrectFilePaths + (caseNum -> List(filePath))
          }
      }
      def writeResultToFile (path : String)= {}
      def checkStartLine (line : String) : Int = {
          var caseNum = COPYRIGHT_Not_Present
          val pattern = COPYRIGHT_FIRST_LINE_PATTERN
          //val Pattern(fromYear, toYear) = line // ???

      }
      def checkFollowingLine (line: String, index: Int) : Int = {return 1}




}
