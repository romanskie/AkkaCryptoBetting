package com.definitelyscala.materializecss

import scala.scalajs.js

@js.native
trait CollapsibleOptions extends js.Object {

  /**
    * A setting that changes the collapsible behavior to expandable instead of the default accordion style
    */
  var accordion: Boolean = js.native

  /**
    * Callback for Collapsible section close.
    * Default: function() { alert('Closed'); }
    */
  var onClose: js.Function = js.native

  /**
    * Callback for Collapsible section open.
    * Default: function() { alert('Opened'); }
    */
  var onOpen: js.Function = js.native
}
