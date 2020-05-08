import scala.collection.mutable.ArrayBuffer
import scala.xml._
import scala.xml.transform._
import scala.xml.dtd._
import scala.xml.parsing.ConstructingParser
import java.io._
import java.nio.file._
import java.util._
import java.util.regex._
import java.security._
import javax.imageio._
import java.nio.file._

object InlineImg extends App {

  /**
    * Transforms all descendants matching a predicate.
    * n a node
    * pred the predicate to match
    * trans the transformation to apply to matching descendants
    */
  def transformIf(n: Node, pred: (Node)=>Boolean, trans: (Node)=>Node): Node =
    if (pred(n)) trans(n) else
      n match {
        case e: Elem =>
          if (e.descendant.exists(pred))
            e.copy(e.prefix, e.label, e.attributes, e.scope, true,
              e.child.map(transformIf(_, pred, trans)))
          else e
        case _ => n
      }

  def transform(n: Node, pf: PartialFunction[Node, Node]) =
    transformIf(n, pf.isDefinedAt(_), pf.apply(_));

  def inline(e: Elem) : Elem = {
    val src=e.attributes("src").text
    if (src.startsWith("data:") || src.startsWith("http:") || src.startsWith("https:")) e
    else {
      val dir = Paths.get(args(0)).toAbsolutePath.getParent
      val bytes = Files.readAllBytes(dir.resolve(src))
      val enc = Base64.getEncoder().encodeToString(bytes)
      val extension = src.substring(src.lastIndexOf(".") + 1).toLowerCase

      e % Attribute(null, "src", "data:image/" + extension + ";base64," + enc, Null)
    }
  }

  if (args.length < 1) {
    System.err.println("Usage: scala inlineimg.scala file")
    System.exit(1)
  }

  val parser = ConstructingParser.fromFile(new File(args(0)), preserveWS = true)
  val doc = parser.document

  val transformed = transform(doc.docElem, { case e @ <img/> => inline(e.asInstanceOf[Elem])})

  /*
  val file = Paths.get(args(0))
  val backup = file.toAbsolutePath.getParent.resolve(file.getFileName.toString + ".bak")
  Files.deleteIfExists(backup)
  Files.move(file, backup)
  XML.save(args(0), transformed,
    enc = "UTF-8")
   */
  println(transformed)

}
