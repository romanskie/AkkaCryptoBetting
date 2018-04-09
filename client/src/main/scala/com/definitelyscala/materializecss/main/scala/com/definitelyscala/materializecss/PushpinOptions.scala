package com.definitelyscala.materializecss

import scala.scalajs.js

@js.native
trait PushpinOptions extends js.Object {

  /**
    * The distance in pixels from the top of the page where the element becomes fixed.
    * Default: 0
    */
  var top: Double = js.native

  /**
    * The distance in pixels from the top of the page where the elements stops being fixed.
    * Default: Infinity
    */
  var bottom: Double = js.native

  /**
    * The offset from the top the element will be fixed at.
    * Default: 0
    */
  var offset: Double = js.native
}
