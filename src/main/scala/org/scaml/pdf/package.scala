package org.scaml

import scala.language.implicitConversions

/**
 * Dialect of ScaML to create PDFs.
 *
 * Main difference is the Block-Syntax, which allows the usage of modifiers as string prefix.
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
package object pdf {
  implicit val style = BlockSyntax(DefaultStyle)

  implicit def tupleToFopAttribute(tuple2: (String, String)): Modifier =
    FopAttribute(tuple2._1) > tuple2._2

  val FopTag = new Attribute[String]("FopTag")

  implicit def stringToTag(symbol: String): Modifier = FopTag > symbol

  def cite(ref: Reference) = Element(Nil, Ref > ref)

  def refList = Element(Nil, RefList > ((): Unit))

  private[pdf] val RefList = new Attribute[Unit]("ReferenceList")
  private[pdf] val Ref = new Attribute[Reference]("Reference")

  def xml(xml: scala.xml.Node) =
    ml"${XML > xml}{}"

  private[pdf] val XML = new Attribute[scala.xml.Node]("XML")
}
