package utils

import models.betting.Bet
import models.user.request.{BetUserResult, ParticipantData}

object BettingActorHelper {

  def calculateClosedBetResult(
      betID: Long,
      bet: Bet,
      endGain: Double,
      plusGainParticipantData: Vector[ParticipantData],
      minusGainParticipantData: Vector[ParticipantData])
    : Vector[BetUserResult] = {

    if (endGain > 0.0) {
      val winnerPool = plusGainParticipantData.map(item => item.invest).sum
      val looserPool = bet.poolInScalaCoins - winnerPool
      val winnerVector = plusGainParticipantData map (item => {
        BetUserResult(
          id = None,
          betID = betID,
          userID = item.id,
          invest = item.invest,
          prediction = item.prediction,
          joinTime = item.joinTime,
          gain = item.invest + (item.invest / winnerPool) * looserPool
        )
      })
      val looserVector = minusGainParticipantData map (item => {
        BetUserResult(id = None,
                      betID = betID,
                      userID = item.id,
                      invest = item.invest,
                      prediction = item.prediction,
                      joinTime = item.joinTime,
                      gain = 0D)
      })
      winnerVector ++: looserVector
    } else {
      val winnerPool = minusGainParticipantData.map(item => item.invest).sum
      val looserPool = bet.poolInScalaCoins - winnerPool
      val winnerVector = minusGainParticipantData map (item => {
        BetUserResult(
          id = None,
          betID = betID,
          userID = item.id,
          invest = item.invest,
          prediction = item.prediction,
          joinTime = item.joinTime,
          gain = item.invest + (item.invest / winnerPool) * looserPool
        )
      })
      val looserVector = plusGainParticipantData map (item => {
        BetUserResult(id = None,
                      betID = betID,
                      userID = item.id,
                      invest = item.invest,
                      prediction = item.prediction,
                      joinTime = item.joinTime,
                      gain = 0D)
      })
      winnerVector ++: looserVector
    }
  }
  def calculateOpenBetResult(betID: Long,
                             bet: Bet,
                             endGain: Double,
                             plusGainParticipantData: Vector[ParticipantData],
                             minusGainParticipantData: Vector[ParticipantData])
    : Vector[BetUserResult] = {

    if (endGain > 0.0) {
      val looserVector = minusGainParticipantData map (item => {
        BetUserResult(id = None,
                      betID = betID,
                      userID = item.id,
                      invest = item.invest,
                      prediction = item.prediction,
                      joinTime = item.joinTime,
                      gain = 0D)
      })
      val winnerVector = plusGainParticipantData map (item => {
        BetUserResult(
          id = None,
          betID = betID,
          userID = item.id,
          invest = item.invest,
          prediction = item.prediction,
          joinTime = item.joinTime,
          gain = item.invest * ((bet.stopTime.toDouble - item.joinTime.toDouble) / bet.duration.toDouble) * math
            .max(0D, 1D - math.abs(item.prediction - endGain)) + item.invest * math
            .max(0, math.ceil(1 - math.abs(item.prediction - endGain)))
        )
      })
      winnerVector ++: looserVector
    } else {
      val looserVector = plusGainParticipantData map (item => {
        BetUserResult(id = None,
                      betID = betID,
                      userID = item.id,
                      invest = item.invest,
                      prediction = item.prediction,
                      joinTime = item.joinTime,
                      gain = 0D)
      })
      val winnerVector = minusGainParticipantData map (item => {
        BetUserResult(
          id = None,
          betID = betID,
          userID = item.id,
          invest = item.invest,
          prediction = item.prediction,
          joinTime = item.joinTime,
          gain = item.invest * ((bet.stopTime.toDouble - item.joinTime.toDouble) / bet.duration.toDouble) * math
            .max(0D, 1D - math.abs(item.prediction - endGain)) + item.invest * math
            .max(0, math.ceil(1 - math.abs(item.prediction - endGain)))
        )
      })
      winnerVector ++: looserVector
    }
  }

}
