import _root_.controllers.AssetsComponents
import akka.util.Timeout
import com.softwaremill.macwire._
import modules.{ControllerModule, DaoModule, DatabaseModule}
import play.api.ApplicationLoader.Context
import play.api._
import play.api.i18n.I18nComponents
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import router.Routes

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Application loader that wires up the application dependencies using Macwire
  */
class ScalaCoinBettingApplicationLoader extends ApplicationLoader {
  def load(context: Context): Application =
    new ScalaCoinBettingComponents(context).application
}

class ScalaCoinBettingComponents(context: Context)
    extends BuiltInComponentsFromContext(context)
    with DatabaseModule
    with DaoModule
    with ControllerModule
    with AssetsComponents
    with I18nComponents
    with play.filters.HttpFiltersComponents {

  // this will run the database migrations on startup
  applicationEvolutions

  implicit lazy val ec: ExecutionContext = executionContext
  implicit lazy val timeout: Timeout = 5 second

  // set up logger
  LoggerConfigurator(context.environment.classLoader).foreach {
    _.configure(context.environment, context.initialConfiguration, Map.empty)
  }

  lazy val router: Router = {
    // add the prefix string in local scope for the Routes constructor
    val prefix: String = "/"
    wire[Routes]
  }

  override val httpFilters: Seq[EssentialFilter] = Seq(allowedHostsFilter)
}
