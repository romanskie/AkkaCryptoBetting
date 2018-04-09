package com.definitelyscala.materializecss

import scala.scalajs.js

@js.native
trait ModalOptions extends js.Object {

  /**
    * Modal can be dismissed by clicking outside of the modal.
    * Default: true
    */
  var dismissible: Boolean = js.native

  /**
    * Opacity of modal background.
    * Default. .5
    */
  var opacity: Double = js.native

  /**
    * Transition in duration.
    * Default: 300
    */
  var inDuration: Double = js.native

  /**
    * Transition out duration.
    * Default: 200
    */
  var outDuration: Double = js.native

  /**
    * Starting top style attribute
    * Default: `4%`
    */
  var startingTop: String = js.native

  /**
    * Ending top style attribute
    * Default : `10%`
    */
  var endingTop: String = js.native

  /**
    * Callback for Modal open.
    * Default: function() { alert('Ready'); }
    */
  var ready: js.Function = js.native

  /**
    * Callback for Modal close.
    * Default: function() { alert('Closed'); }
    */
  var complete: js.Function = js.native
}
