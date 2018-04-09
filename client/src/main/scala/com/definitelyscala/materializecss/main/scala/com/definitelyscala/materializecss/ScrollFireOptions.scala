package com.definitelyscala.materializecss

import scala.scalajs.js

@js.native
trait ScrollFireOptions extends js.Object {

  /**
    * The selector for the element that is being tracked.
    */
  var selector: String = js.native

  /**
    * Offset to use when activating the scroll fire event
    * If this is 0, the callback will be fired when the selector element is at the very bottom of the user's window.
    */
  var offset: Double = js.native

  /**
    * The string function call that you want to make when the user scrolls to the threshold.
    * It will only be called once.
    * Example: 'console.log("hello, world!")';
    */
  var callback: js.Function = js.native
}
