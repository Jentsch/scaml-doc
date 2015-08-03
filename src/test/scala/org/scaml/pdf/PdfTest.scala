package org.scaml.pdf

import org.specs2.Specification
import org.scaml.attributes._
import org.scaml.ML

import DefaultStyle._

class PdfTest extends Specification  {
  val author = Reference(title = "Some important paper", authors = List("Sam Somebody"), pages = 8 -> 12)
  val paper2 = Reference(
    title = "Experiences with Domain-specific Language Embedding in Scala",
    authors = List("Sloane, Tony"),
    date = "2008",
    serie = "Domain-Specific Program Development"
  )

  object Test extends SimpleDinA4 {
    title"Test document"

    section"Main"

    p"""
        A very long text that should have at least one line break. This is necessary to test the line break ability of fop.
        Such textes are first introduced by ${cite(author)}
    """

    subsection"Detailed topic"

    p"""
      >${xml(<fo:instream-foreign-object>
      <math xmlns="http://www.w3.org/1998/Math/MathML" mode="inline">
        <mrow>
          <mi>I</mi>
          <mrow>
            <mo>(</mo>
            <mi>x</mi>
            <mo>)</mo>
          </mrow>
          <mo>=</mo>
          <mi>F</mi>
          <mo stretchy="false">(</mo>
          <mi>x</mi>
          <mo stretchy="false">)</mo>
          <mo>+</mo>
          <mi>C</mi>
        </mrow>
      </math>
    </fo:instream-foreign-object>)}<
    """

    subsection"About Language Embedding in Scala"

    p"""
       Object-oriented frameworks are often difficult to use.
       Framework- specific extensions to integrated development environments (IDEs) aim to mitigate the difficulty by
       offering tools that leverage the knowledge about framework's application programming interfaces (APIs).
       These tools commonly offer support for code visualization, automatic and interactive code generation, and code
       validation. Current practices, however, require such extensions to be custom-built manually for each supported
       framework.
       In this paper, we propose an approach to building framework-specific IDE extensions based on framework-specific
       modeling languages (FSMLs).
       We show how the definitions of different FSMLs can be interpreted in these extensions to provide advanced tool
       support for different framework APIs that the FSMLs are designed for. See ${cite(paper2)}"""

    section"Literatur"

    p"$refList"

    override def header = ml"${DefaultStyle.p & TextAlign > "right"} {Header}"
    override def footer = ml"${DefaultStyle.p & TextAlign > "center"} {$pageNumber}"
    override def extensions = new HeadlineCounter :: new References :: super.extensions
  }


  def is = s2"""
  ${Generator(Test) should not(throwA[Exception])}
"""
}
