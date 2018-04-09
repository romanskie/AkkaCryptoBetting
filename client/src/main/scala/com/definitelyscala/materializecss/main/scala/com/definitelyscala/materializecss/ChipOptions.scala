package com.definitelyscala.materializecss

import scala.scalajs.js

@js.native
trait ChipOptions extends js.Object {

  /**
    * Set the chip data
    */
  var data: js.Array[ChipDataObject] = js.native

  /**
    * Set first placeholder when there are no tags
    */
  var placeholder: String = js.native

  /**
    * Set second placeholder when adding additional tags.
    */
  var secondaryPlaceholder: String = js.native

  /**
    * Set autocomplete data.
    */
  var autocompleteData: js.Any = js.native

  /**
    * Set autocomplete limit.
    */
  var autocompleteLimit: Double = js.native
}
