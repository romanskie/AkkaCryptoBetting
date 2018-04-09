package com.definitelyscala.materializecss

import scala.scalajs.js

@js.native
trait SliderOptions extends js.Object {

  /**
    * Set to false to hide slide indicators.
    * Default: true
    */
  var indicators: Boolean = js.native

  /**
    * Set height of slider.
    * Default: 400
    */
  var height: Double = js.native

  /**
    * Set the duration of the transition animation in ms.
    * Default: 500
    */
  var transition: Double = js.native

  /**
    * Set the duration between transitions in ms.
    * Default: 6000
    */
  var interval: Double = js.native
}
