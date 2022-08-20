package gg.scala.infuse.spigot.display

import gg.scala.infuse.commons.rank.InfuseRank
import gg.scala.infuse.commons.rank.metadata.defaults.WeightMetadata
import gg.scala.infuse.commons.serialize
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.PaginatedResult

/**
 * @author GrowlyX
 * @since 8/20/2022
 */
object RankDisplayResult : PaginatedResult<InfuseRank>()
{
    override fun format(result: InfuseRank, resultIndex: Int): String
    {
        return "${CC.PRI}#$resultIndex ${CC.WHITE}- ${result.getColoredName().serialize()} ${CC.WHITE}- ${CC.GRAY}[weight: ${result.metadata<WeightMetadata, Int>()}]"
    }

    override fun getHeader(page: Int, maxPages: Int): String
    {
        return "${CC.PRI}=== ${CC.SEC}Ranks ${CC.GRAY}($page/$maxPages) ${CC.PRI}==="
    }
}
