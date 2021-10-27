package com.flutura.datawrangler

import ammonite.interp.api.APIHolder

trait TestReplApi {
  def message: String
}

object TestReplBridge extends APIHolder[TestReplApi]

case class Nope(n: Int)