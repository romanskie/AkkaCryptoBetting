package com.definitelyscala.materializecss

import scala.scalajs.js

@js.native
trait DropDownOptions extends js.Object {

  /**
    * The duration of the transition enter in milliseconds.
    * Default: 300
    */
  var inDuration: Double = js.native

  /**
    * The duration of the transition out in milliseconds.
    * Default: 225
    */
  var outDuration: Double = js.native

  /**
    * If true, constrainWidth to the size of the dropdown activator.
    * Default: true
    */
  var constrainWidth: Boolean = js.native

  /**
    * If true, the dropdown will open on hover.
    * Default: false
    */
  var hover: Boolean = js.native

  /**
    * This defines the spacing from the aligned edge.
    * Default: 0
    */
  var gutter: Double = js.native

  /**
    * If true, the dropdown will show below the activator.
    * Default: false
    */
  var belowOrigin: Boolean = js.native

  /**
    * Defines the edge the menu is aligned to.
    * Default: 'left'
    */
  var alignment: String = js.native

  /**
    * If true, stops the event propagating from the dropdown origin click handler.
    *
    * Default: false
    */
  var stopPropagation: Boolean = js.native
}
