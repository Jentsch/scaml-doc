package org.scaml.pdf

import org.scaml._

trait SimpleDinA4 extends Block {
  override protected def surround(element: Element): Element = ml"""
    ${"layout-master-set" & Modifier.empty} {
      ${"simple-page-master" & pageDimensions} {
        ${"region-body" & "margin-top" -> "21.25pt" & "margin-bottom" -> "21.25pt"} {}
        ${"region-before" & "region-name" -> "page-header" & "extent" -> "11in"} {}
        ${"region-after" & "region-name" -> "page-footer" & "extent" -> "11in" & "display-align" -> "after"} {}
      }
    }
    ${"page-sequence" & "master-reference" -> "simpleA4"} {
      ${"static-content" & "flow-name" -> "page-header"}
        $header
      ${"static-content" & "flow-name" -> "page-footer"}
        $footer
      ${"flow" & "flow-name" -> "xsl-region-body"} {
        ${super.surround(element)}
      }
    }
  """

  private def pageDimensions: Modifier =
    "master-name" -> "simpleA4" & "page-height" -> "297mm" & "page-width" -> "210mm" &
      "margin-top" -> "2cm" & "margin-bottom" -> "2cm" & "margin-left" -> "2cm" & "margin-right" -> "2cm"

  def footer: Element = Element(Nil)
  def header: Element = Element(Nil)

  /**
   * Modifiers added to the resulting element
   */
  override protected def modifier: Modifier = super.modifier & FopTag > "block"
}
