package org.scaml.pdf

import org.scaml._
import org.specs2._

class TestBlockSyntax extends Specification {

  object TestCase extends Block {
    p"WWW"
  }


  def is = s2"""
  ${TestCase.toString should contain("Text(WWW)")}
"""
}
