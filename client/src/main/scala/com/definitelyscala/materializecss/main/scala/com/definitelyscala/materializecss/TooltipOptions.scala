package com.definitelyscala.materializecss

import scala.scalajs.js

@js.native
trait TooltipOptions extends js.Object {

  /**
    * The delay before the tooltip shows (in milliseconds)
    */
  var delay: Double = js.native

  /**
    * Tooltip text. Can use custom HTML if you set the html option
    */
  var tooltip: String = js.native

  /**
    * Set the direction of the tooltip. 'top', 'right', 'bottom', 'left'.
    *
    * (Default: 'bottom')
    */
  var position: String = js.native

  /**
    * Allow custom html inside the tooltip.
    *
    * (Default: false)
    */
  var html: Boolean = js.native
}
