package gg.scala.infuse.commons.reference.impl

import gg.scala.infuse.commons.rank.InfuseRank
import gg.scala.infuse.commons.rank.RankService
import gg.scala.infuse.commons.reference.Reference
import java.util.*

/**
 * @author GrowlyX
 * @since 6/26/2022
 */
class RankReference(
    override val original: UUID
) : Reference<UUID, InfuseRank>
{
    override fun getAbstractType() = Reference::class.java

    override fun parsed() =
        RankService.findRank(original)
}
