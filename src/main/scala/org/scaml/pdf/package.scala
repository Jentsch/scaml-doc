package org.scaml

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
 *     """
 *   }
 * }}}
 */
package object pdf {


}
