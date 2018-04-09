package com.definitelyscala.materializecss

import scala.scalajs.js
import scala.scalajs.js.|

@js.native
trait JQuery extends js.Object {

  /**
    * open Fixed Action Button
    */
  def openFAB(): Unit = js.native

  /**
    * close Fixed Action Button
    */
  def closeFAB(): Unit = js.native

  /**
    * Select allows user input through specified options.
    * Initialization
    */
  def material_select(): Unit = js.native

  /**
    * Select allows user input through specified options.
    * Updating/Destroying Select
    *
    * @param method "destroy" destroy the material select
    */
  def material_select(method: String): Unit = js.native

  /**
    * Use a character counter in fields where a character restriction is in place.
    */
  def characterCounter(): JQuery = js.native

  /**
    * Collapsibles are accordion elements that expand when clicked on.
    * They allow you to hide content that is not immediately relevant to the user.
    *
    * @param options the collapsible options
    */
  def collapsible(options: CollapsibleOptions = js.native): JQuery = js.native

  /**
    * Tooltips are small, interactive, textual hints for mainly graphical elements.
    * When using icons for actions you can use a tooltip to give people clarification on its function.
    *
    * @param options the tooltip options or the string "remove" to remove the tooltip function
    */
  def tooltip(options: TooltipOptions | String = js.native): JQuery = js.native

  /**
    * Add a dropdown list to any button.
    * Make sure that the data-activates attribute matches the id in the <ul> tag.
    *
    * @param options the drop down options
    */
  def dropdown(options: DropDownOptions = js.native): Unit = js.native

  /**
    * Material box is a material design implementation of the Lightbox plugin.
    */
  def materialbox(): JQuery = js.native

  /**
    * slider is a simple and elegant image carousel.
    * You can also have captions that will be transitioned on their own depending on their alignment.
    * You can also have indicators that show up on the bottom of the slider.
    *
    * @param options the slider options
    */
  def slider(options: SliderOptions): JQuery = js.native

  /**
    * slider is a simple and elegant image carousel.
    * You can also have captions that will be transitioned on their own depending on their alignment.
    * You can also have indicators that show up on the bottom of the slider.
    *
    * @param method the string "start" to start the animation or "pauze" to pauze the animation
    */
  def slider(method: String): JQuery = js.native

  /**
    * Our slider is a simple and elegant image carousel.
    * You can also have captions that will be transitioned on their own depending on their alignment.
    * You can also have indicators that show up on the bottom of the slider.
    *
    * @param options the slider options or the string "start" to start the animation or "pauze" to pauze the animation
    */
  def carousel(options: CarouselOptions): JQuery = js.native

  /**
    * Our slider is a simple and elegant image carousel.
    * You can also have captions that will be transitioned on their own depending on their alignment.
    * You can also have indicators that show up on the bottom of the slider.
    *
    * @param method the methods to pause, start, move to next and move to previous slide.
    */
  def carousel(method: String, count: Double): JQuery = js.native

  /**
    * Modal for dialog boxes, confirmation messages, or other content that can be called up.
    *
    * For Initialization.
    */
  def modal(): JQuery = js.native

  /**
    * Modal for dialog boxes, confirmation messages, or other content that can be called up.
    *
    * For opening and closing modals programatically.
    *
    * @param string action action to do (`open` or `close)
    */
  def modal(action: String): Unit = js.native

  /**
    * Modal for dialog boxes, confirmation messages, or other content that can be called up.
    *
    * To customize the behaviour of a modal
    *
    * @param options the lean modal options
    */
  def modal(options: ModalOptions): Unit = js.native

  /**
    * Parallax is an effect where the background content or image in this case, is moved at a different speed than the foreground content while scrolling.
    */
  def parallax(): JQuery = js.native

  /**
    * Pushpin is a fixed positioning plugin.
    *
    * @param options the push pin options
    */
  def pushpin(options: PushpinOptions = js.native): JQuery = js.native

  /**
    * Scrollspy is a jQuery plugin that tracks certain elements and which element the user's screen is currently centered on.
    *
    * @param options the scroll spy options
    */
  def scrollSpy(options: ScrollSpyOptions = js.native): JQuery = js.native

  /**
    * A slide out menu. You can add a dropdown to your sidebar by using our collapsible component.
    *
    * @params methodOrOptions the slide navigation options or a string with "show" to reveal or "hide" to hide the menu
    */
  def sideNav(methodOrOptions: SideNavOptions | String = js.native): Unit =
    js.native

  /**
    * Programmatically trigger the tab change event
    *
    * @param method, the method to call (always "select_tab") and a param containing the id of the tab to open
    */
  def tabs(method: String, tab: String): JQuery = js.native

  /**
    * Tab Initialization with options
    *
    * @param TabOptions options jQuery plugin options
    */
  def tabs(options: TabOptions): JQuery = js.native

  /**
    * Chip Initialization
    *
    * @param ChipOptions options Material chip options
    */
  def material_chip(options: ChipOptions): JQuery = js.native

  /**
    * To access chip data
    *
    * @param string method name of the method to invoke
    */
  def material_chip(method: String): js.Array[ChipDataObject] | ChipDataObject =
    js.native
}
