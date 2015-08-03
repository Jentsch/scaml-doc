package org.scaml.pdf

import org.scaml._
import org.scaml.pdf.Block._

import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions
import scala.util.DynamicVariable

/**
 * Enables the block syntax.
 *
 * {{{
 *   object Document extends DinA4 {
 *     title"Title"
 *
 *     p"""
 *       Text text text
 * """
 * }
 * }}}
 */
trait Block {
  /**
   * Modifiers added to the resulting element
   */
  protected def modifier = Modifier.empty

  /**
   * The result of this block will be given this method
   */
  protected def surround(element: Element): Element = element

  protected def extensions: List[Function[Element, Element]] = Nil

  /** So far collected children */
  private[Block] val buffer = ListBuffer.empty[Node]

  /** Used by children to register them self. */
  protected implicit val self: Block = this

  /** Adds a node to this Builder */
  private[scaml] def register(n: Node): Unit =
    buffer.append(n)

  /**
   * Used to allow Modifiers as String Interpolator. Don't do this at home kids.
   */
  implicit def modifiersForBlockSyntax[S](sc: StringContext)(implicit blockSyntax: BlockSyntax[S]): S = {
    stringContext = sc
    blockSyntax.style
  }

  implicit class RichModifier(modifier: Modifier) {
    def apply(parts: Inlineable*): Unit = {
      val context = stringContext.
        getOrElse(sys.error("Invalid call of RichModifier, see org.scaml.org.pdf.Block for vaild examples"))
      val result = context.ml(parts: _*)
      buffer += Element(result :: Nil, modifier)
    }
  }

  override def toString =
    BlockToNode(this).toString()
}

object Block {
  private val _stringContext = new DynamicVariable[Option[StringContext]](None)

  private def stringContext: Option[StringContext] = _stringContext.value

  private def stringContext_=(sc: StringContext): Unit =
    _stringContext.value = Some(sc)

  implicit def BlockToNode(block: Block): Element = {
    val initialDocument = Element(block.buffer.to[Seq], block.modifier)
    val extendedDocument = block.extensions.foldRight(initialDocument) { case (extension, doc) => extension(doc) }
    block.surround(extendedDocument)
  }
}
