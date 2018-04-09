package modules

import play.api.db.evolutions.EvolutionsComponents
import play.api.db.slick.evolutions.SlickEvolutionsComponents
import play.api.db.slick.{DbName, SlickComponents}
import slick.jdbc.JdbcProfile

trait DatabaseModule
    extends SlickComponents
    with EvolutionsComponents
    with SlickEvolutionsComponents {

  lazy val dbConfig = slickApi.dbConfig[JdbcProfile](DbName("default"))

}
