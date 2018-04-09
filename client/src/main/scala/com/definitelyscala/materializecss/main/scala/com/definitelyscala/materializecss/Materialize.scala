package com.definitelyscala.materializecss

import scala.scalajs.js
import scala.scalajs.js.|

@js.native
trait Materialize extends js.Object {

  /**
    * Displays a toast message on screen
    *
    * @param message The message to display on screen
    * @param displayLength The duration in milliseconds to display the message on screen
    * @param className The className to use to format the message to display
    * @param completeCallback Callback function to call when the messages completes/hides.
    */
  def toast(message: String | js.Dynamic,
            displayLength: Double,
            className: String = js.native,
            completeCallback: js.Function = js.native): Unit = js.native

  /**
    * Fires an event when the page is scrolled to a certain area
    *
    * @param options optional parameter with scroll fire options
    */
  def scrollFire(options: ScrollFireOptions = js.native): Unit = js.native

  /**
    * A staggered reveal effect for any UL Tag with list items
    *
    * @param selector the selector for the list to show in staggered fasion
    */
  def showStaggeredList(selector: String): Unit = js.native

  /**
    * Fade in images. It also animates grayscale and brightness to give it a unique effect.
    *
    * @param selector the selector for the image to fade in
    */
  def fadeInImage(selector: String): Unit = js.native

  /**
    * Update all text field to reinitialize all the Materialize labels on the page if dynamically adding inputs
    */
  def updateTextFields(): Unit = js.native
}
