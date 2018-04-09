package com.definitelyscala.materializecss

import scala.scalajs.js

@js.native
trait CarouselOptions extends js.Object {

  /**
    * Transition duration in milliseconds
    * Default: 200
    */
  var duration: Double = js.native

  /**
    * Perspective zoom. If 0, all items are the same size.
    * Default: -100
    */
  var dist: Double = js.native

  /**
    * Set the duration of the transition animation in ms.
    * Default: 500
    */
  var shift: Double = js.native

  /**
    * Set the duration between transitions in ms.
    * Default: 6000
    */
  var padding: Double = js.native

  /**
    * Set the width of the carousel.
    * Default: false
    */
  var fullWidth: Boolean = js.native

  /**
    * Set to true to show indicators.
    *
    * Default: false
    */
  var indicators: Boolean = js.native

  /**
    * Don't wrap around and cycle through items.
    *
    * Default: false
    */
  var noWrap: Boolean = js.native
}
