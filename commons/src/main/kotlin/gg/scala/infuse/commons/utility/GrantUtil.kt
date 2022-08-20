package gg.scala.infuse.commons.utility

import gg.scala.infuse.commons.config
import gg.scala.infuse.commons.grant.InfuseGrant
import gg.scala.infuse.commons.rank.metadata.defaults.VisibilityMetadata
import gg.scala.infuse.commons.rank.metadata.defaults.WeightMetadata
import java.time.Instant
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 8/20/2022
 */
object GrantUtil
{
    @JvmStatic
    fun expireGrant(grant: InfuseGrant): CompletableFuture<Void>
    {
        grant.removedFor = "Expired"
        grant.removedAt = Instant.now()
        grant.removedOn = config.identifier

        return grant.save()
    }

    @JvmStatic
    fun getProminentGrant(grants: List<InfuseGrant>): InfuseGrant? {
        return grants
            .sortedByDescending { it.applied.toEpochMilli() }
            .sortedByDescending { it.rank.parsed()?.metadata<WeightMetadata, Int>() }
            .firstOrNull {
                it.active && it.applicable() &&
                it.rank.parsed()?.metadata<VisibilityMetadata, Boolean>() == true
            }
    }

    @JvmStatic
    fun getProminentSubGrant(prominent: InfuseGrant, grants: List<InfuseGrant>): InfuseGrant? {
        return grants
            .sortedByDescending { it.applied.toEpochMilli() }
            .sortedByDescending { it.rank.parsed()?.metadata<WeightMetadata, Int>() }
            .firstOrNull {
                it != prominent && it.active && it.applicable() &&
                        it.rank.parsed()?.metadata<VisibilityMetadata, Boolean>() == true
            }
    }


    @JvmStatic
    fun getPermissibleGrants(grants: List<InfuseGrant>): List<InfuseGrant> {
        return grants
            .sortedByDescending { it.rank.parsed()?.metadata<WeightMetadata, Int>() }
            .filter { !it.active && it.applicable() }
    }
}
