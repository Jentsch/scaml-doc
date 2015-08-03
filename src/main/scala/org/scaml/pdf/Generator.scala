package org.scaml.pdf

import java.io._
import java.nio.file.Paths
import javax.xml.transform._
import javax.xml.transform.sax._
import javax.xml.transform.stream._

import org.apache.fop.apps._
import org.apache.fop.apps.io.ResourceResolverFactory
import org.apache.xmlgraphics.util.MimeConstants
import org.scaml._
import org.scaml.attributes._

import scala.xml.{Elem, MetaData, UnprefixedAttribute}

object Generator {

  sealed abstract class Output(val path: String, val mime: String)

  case class PostScript(override val path: String) extends Output(path, "application/pdf")

  val target = new File("output.pdf")
  val rawOutput = "output.xml"

  private val fopFactory = {
    val fopConfig = this.getClass.getResource("fop-config.xml")
    val workingDir = {
      val currentRelativePath = Paths.get("")
      currentRelativePath.toAbsolutePath.toUri
    }

    new FopConfParser(
      fopConfig.openStream(), workingDir, ResourceResolverFactory.createDefaultResourceResolver).getFopFactoryBuilder.build
  }


  def apply(document: Element) {

    // Step 2: Set up output stream.
    // Note: Using BufferedOutputStream for performance reasons (helpful with FileOutputStreams).

    val out: OutputStream = new BufferedOutputStream(new FileOutputStream(target))
    try {
      val fop: Fop = fopFactory.newFop(MimeConstants.MIME_PDF, out)
      val factory: TransformerFactory = TransformerFactory.newInstance
      val transformer: Transformer = factory.newTransformer
      val fo: Elem = body(simplify(document))
      try {
        scala.xml.XML.save(rawOutput, fo)
      } catch {
        case e: Exception =>
          println("Error while creating debuging result")
          e.printStackTrace()
      }
      val src: Source = new StreamSource(new StringReader(fo.toString()))
      val res: Result = new SAXResult(fop.getDefaultHandler)
      transformer.transform(src, res)
    } finally {
      if (out != null) out.close()
    }
  }

  private def simplify(doc: Element): Element = {
    val simpleChildren: Seq[Node] = doc.children.flatMap {
      case Element(ch, modifier) if modifier.isEmpty =>
        ch.map {
          case elem: Element => simplify(elem)
          case node => node
        }
      case elem: Element =>
        simplify(elem) :: Nil
      case node =>
        node :: Nil
    }
    val joinedText = simpleChildren.foldRight(List.empty[Node]) {
      case (Text(leftText), Text(rightText) :: rest) =>
        Text(leftText + rightText) :: rest
      case (node, rest) =>
        node :: rest
    }
    if (doc.children == joinedText) {
      doc
    } else {
      simplify(doc.copy(children = joinedText))
    }
  }

  def body(node: Element) =
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:fox="http://xmlgraphics.apache.org/fop/extensions">
      {node.children.map(content)}
    </fo:root>

  private def content(node: Node): scala.xml.Node = node match {
    case Text(text) =>
      scala.xml.Text(text)
    case Element(_, modifiers) if modifiers.isDefinedAt(XML) =>
      modifiers.get(XML).get
    case Element(ch, attributes) =>
      val tag: String =
        attributes.get(FopTag) orElse
          attributes.get(Display).map(_.name) getOrElse
          "inline"

      scala.xml.Elem("fo", tag, metaData(attributes), scala.xml.TopScope, true, ch.map(content): _*)
  }

  private def metaData(at: Modifier) = {
    def attribute(name: String, value: String, prev: MetaData) =
      new UnprefixedAttribute(name, value, prev)

    val preresult = at.foldLeft(scala.xml.Null: MetaData) {
      case (n, FontFamily(family)) =>
        attribute("font-family", family, n)
      case (n, TextAlign(align)) =>
        attribute("text-align", align, n)
      case (n, FontWeight(weight)) =>
        attribute("font-weight", weight.toString, n)
      case (n, WhiteSpaceCollapse(on)) =>
        attribute("white-space-collapse", if (on) "true" else "false", n)
      case (n, BreakBefore(break)) =>
        attribute("break-before", break.name, n)
      case (n, BreakAfter(break)) =>
        attribute("break-after", break.name, n)
      case (n, TextTransform(transform)) =>
        attribute("text-transform", transform.name, n)
      case (n, TextColor(color)) =>
        attribute("color", "#" + color.toHex, n)
      case (n, mod) if ptAttributes.isDefinedAt(mod.attribute) =>
        attribute(ptAttributes(mod.attribute), mod.value.toString, n)
      case (n, mod) =>
        mod.attribute match {
          case FopAttribute(name) =>
            attribute(name, mod.value.toString, n)
          // Ignore unknown attributes
          case _ =>
            n
        }
    }

    decorations(at) match {
      case None => preresult
      case Some(dec) => attribute("text-decoration", dec, preresult)
    }
  }

  private def decorations(at: Modifier) = {
    val decoration = Map(
      TextUnderline -> "underline",
      TextOverline -> "overline",
      TextLineThrought -> "line-through")

    val may = decoration.flatMap { case (key, value) =>
      at.get(key) map { enable =>
        (if (enable) "" else "no -") + decoration(key)
      }
    }

    if (may.isEmpty)
      None
    else
      Some(may.mkString(" "))
  }

  private val ptAttributes = Map[Attribute[_], String](
    FontSize -> "font-size",
    LineHeight -> "line-height",
    SpaceAfter -> "space-after",
    SpaceBefore -> "space-before")
}
