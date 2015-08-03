package org.scaml.pdf

import org.scaml._
import org.scaml.pdf.DefaultStyle._

case class Reference(title: String, authors: Seq[String] = Nil, date: String = "", serie: String = "", pages: (Int, Int) = (0, 0))

class References(referenceSytle: ReferenceSytle = DefaultReferences) extends ((Element) => Element) {
  override def apply(doc: Element): Element = {
    def list(doc: Element): Seq[Reference] = doc match {
      case Element(Nil, Ref(ref)) => ref :: Nil
      case Element(children, _) =>
        children.collect{case e: Element => list(e)}.flatten
    }
    val refenceList = list(doc)

    def update(doc: Element): Node = doc match {
      case Element(Nil, Ref(ref)) =>
        val pos = refenceList.indexOf(ref)
        referenceSytle.inText(ref, pos + 1)

      case Element(Nil, RefList(())) =>
        referenceSytle.sorroundBy(refenceList.zipWithIndex.map{case (ref, pos) => referenceSytle.listEntry(ref, pos + 1)})

      case Element(children, modifiers) =>
        Element(children.map{
          case e: Element =>
            update(e)
          case text =>
            text
        }, modifiers)
    }

    update(doc).asInstanceOf[Element]
  }
}

trait ReferenceSytle {
  def inText(reference: Reference, position: Int): String

  def listEntry(reference: Reference, position: Int): Node
  
  def sorroundBy(list: Seq[Node]): Element = Element(list)
}

object DefaultReferences extends ReferenceSytle {
  override def inText(reference: Reference, position: Int): String = "[" + position.toString + "]"

  override def listEntry(reference: Reference, position: Int): Element = {
    val serie = if (reference.serie.isEmpty)
      ml""
    else
      ml"""in $italic ${Text(reference.serie)}"""

    val pages = reference.pages match {
      case (0, 0) => ""
      case (start, end) if start == end => "page " + start.toString
      case (start, end) => s"pages $start-$end"
     }

    ml"""
      ${FopTag > "list-item"} {
        ${FopTag > "list-item-label" & "end-indent" -> "label-end()" } {$p {[${Text(position.toString)}]}}
        ${FopTag > "list-item-body" &  "start-indent" -> "body-start()" } {$p {
          ${Text(reference.authors.mkString(", "))}. ${Text(reference.title)} $serie ${Text(pages)} ${Text(reference.date)}.
        }}
      }
    """
  }

  override def sorroundBy(list: Seq[Node]): Element = ml""" ${FopTag > "list-block"} ${super.sorroundBy(list)}"""
}