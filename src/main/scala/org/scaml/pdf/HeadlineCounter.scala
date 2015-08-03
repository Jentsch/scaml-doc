package org.scaml.pdf

import org.scaml.{Node, Text, Element}
import org.scaml.attributes._

class HeadlineCounter extends ((Element) => Element) {
  override def apply(doc: Element): Element = {
    val (result, _) = updateHeadlines(doc)
    result
  }

  private def updateHeadlines(doc: Element, counters: List[Int] = 0 :: Nil): (Element, List[Int]) = (doc, counters) match {
    case (Element(children, h2), section :: _) if h2.getOrElse(Tag, "") == "h2" =>
      (Element(Text((section + 1).toString + ". ") +: children, h2),
        (section + 1) :: 0 :: Nil)
    case (Element(children, h3), section :: subsection :: _) if h3.getOrElse(Tag, "") == "h3" =>
      (Element(Text(section.toString + "." + (subsection + 1) + ". ") +: children, h3),
        section :: (subsection + 1) :: 0 :: Nil)
    case (Element(children, h4), section :: subsection :: subsubsection :: _) if h4.getOrElse(Tag, "") == "h3" =>
      (Element(Text(section.toString + "." + subsection + "." + (subsubsection + 1) + ". ") +: children, h4),
        section :: subsection :: (subsubsection + 1) :: 0 :: Nil)
    case (Element(children, mod), counters) =>
      val (updatedChildren, updatedCounter) = children.foldLeft((List.empty[Node], counters)) {
        case ((collected, counters), element: Element) =>
          val (result, updatedCounters) = updateHeadlines(element, counters)
          (collected :+ result, updatedCounters)
        case ((collected, counters), node) =>
          (collected :+ node, counters)
      }
      (Element(updatedChildren, mod), updatedCounter)
    case (node, counters) =>
      (node, counters)
  }
}
