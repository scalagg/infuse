package gg.scala.infuse.commons.rank

import gg.scala.aware.annotation.Subscribe
import gg.scala.aware.message.AwareMessage
import io.liftgate.lease.LeaseDependency

/**
 * @author GrowlyX
 * @since 6/26/2022
 */
object RankCommunicator
{
    @Subscribe("rank-update")
    fun onRankUpdate(
        message: AwareMessage
    )
    {
        RankService.reload().join()
        LeaseDependency.invalidate(InfuseRank::class)
    }
}
