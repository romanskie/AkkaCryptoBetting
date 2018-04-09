package models.betting

sealed trait BettingCategory
case object BettingCategory {
  case object Open extends BettingCategory
  case object Closed extends BettingCategory
}
