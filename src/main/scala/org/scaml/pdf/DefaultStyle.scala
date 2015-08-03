package org.scaml.pdf

import org.scaml.{Modifier, WebAttribute, Element}
import org.scaml.attributes._

class DefaultStyle {
  def text = TextColor > black &
    FontFamily > "Vollkorn" &
    FontSize > 10.pt

  def headline = text &
    FontFamily > "Caladea" &
    SpaceAfter > 8.pt &
    SpaceBefore > 8.pt &
    FopTag > "block" &
    bold &
    "keep-with-next" -> "always"

  def title = headline &
    FontSize > 16.pt &
    Tag > "h1"

  def section = headline &
    FontSize > 14.pt &
    SpaceBefore > 10.pt &
    Tag > "h2"

  def subsection = headline &
    bold &
    Tag > "h3"

  def p = text &
    TextAlign > "justify" &
    FopTag > "block"

  def pageNumber =
    Element(Nil, FopTag > "page-number")

  def italic: Modifier =
    "font-style" -> "italic"
  def i = italic

  implicit val style: this.type = this
}

object DefaultStyle extends DefaultStyle
