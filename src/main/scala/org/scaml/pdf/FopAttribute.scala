package org.scaml.pdf

import org.scaml.Attribute

case class FopAttribute(override val name: String) extends Attribute[String](name)

