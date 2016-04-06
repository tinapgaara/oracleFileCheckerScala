package oracle.apmaas.util.fileChecker

/**
  * Created by yiyitan on 4/5/2016.
  */
object TestForEach extends App {

  var isCheck = (a : Int) => {
    if (a == 3) println("check !")
    else println("not check")
  }

    var list = List(1, 2, 3, 4)
    list.foreach((i: Int) => isCheck(i))

    var map = Map(1-> List("a", "b", "c"), 2-> List("e", "d", "f"))
    var oldlist = map.get(1).get

    val p2 = "q" :: oldlist
    val p3 = oldlist :+ "q"

  print (p2)
  print(p3)
    map = map + (1 -> p2)
  map = map + (1 -> p3)

    print (map)

}
