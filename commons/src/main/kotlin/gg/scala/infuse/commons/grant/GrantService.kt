package gg.scala.infuse.commons.grant

import com.mongodb.client.model.Filters.eq
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.infuse.commons.player.InfusePlayer
import gg.scala.infuse.commons.rank.RankService
import gg.scala.infuse.commons.reference.impl.RankReference
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.impl.MongoDataStoreStorageLayer
import gg.scala.store.storage.type.DataStoreStorageType
import org.bson.conversions.Bson
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 8/20/2022
 */
@Service
object GrantService
{
    @Configure
    fun configure()
    {
        DataStoreObjectControllerCache.create<InfuseGrant>()
    }

    private fun findGrants(filter: Bson): CompletableFuture<List<InfuseGrant>>
    {
        val controller = DataStoreObjectControllerCache.findNotNull<InfuseGrant>()

        return controller.useLayerWithReturn<MongoDataStoreStorageLayer<InfuseGrant>, CompletableFuture<List<InfuseGrant>>>(DataStoreStorageType.MONGO) {
            return@useLayerWithReturn this.loadAllWithFilter(filter)
                .thenApply { it.values.toList() }
        }
    }

    fun findGrantsBy(uuid: UUID) =
        findGrants(
            eq("appliedBy", uuid.toString())
        )

    fun findGrantsFor(uuid: UUID?) =
        findGrants(
            eq("appliedTo", uuid.toString())
        )

    fun createAutomatedGrant(
        player: InfusePlayer
    ): CompletableFuture<Void>
    {
        val grant = InfuseGrant(
            identifier = UUID.randomUUID(),
            appliedTo = player.identifier,
            appliedBy = null,
            appliedFor = "Automated",
            rank = RankService.findDefaultRankReference(),
            automated = true
        )

        return grant.saveAndPushUpdates()
    }
}
