package com.definitelyscala.materializecss

import scala.scalajs.js

@js.native
trait SideNavOptions extends js.Object {

  /**
    * Default: 240
    */
  var menuWidth: Double = js.native

  /**
    * The horizontal origin
    * Default: ' left'
    */
  var edge: String = js.native

  /**
    * Closes side-nav on <a> clicks, useful for Angular/Meteor
    * Default: false
    */
  var closeOnClick: Boolean = js.native

  /**
    * Choose whether you can drag to open on touch screens
    * Default: true
    */
  var draggable: Boolean = js.native
}
