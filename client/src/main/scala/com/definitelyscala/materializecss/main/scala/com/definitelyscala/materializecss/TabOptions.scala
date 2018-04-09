package com.definitelyscala.materializecss

import scala.scalajs.js

@js.native
trait TabOptions extends js.Object {

  /**
    * Execute a callback function when the tab is changed.
    *
    * The callback provides a parameter which refers to the current tab being shown.
    */
  var onShow: js.Function = js.native

  /**
    * Set to true to enable swipeable tabs. This also uses the responsiveThreshold option.
    *
    * Default: false
    */
  var swipeable: Boolean = js.native

  /**
    * The maximum width of the screen, in pixels, where the swipeable functionality initializes.
    *
    * Default: Infinity
    */
  var responsiveThreshold: Double = js.native
}
