package gg.scala.infuse.commons.rank

import gg.scala.flavor.service.Close
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.infuse.commons.InfuseCommons
import gg.scala.infuse.commons.logger
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import gg.scala.infuse.commons.rank.metadata.RankMetadata
import gg.scala.infuse.commons.rank.metadata.annotate.MetadataDefault
import gg.scala.infuse.commons.rank.metadata.defaults.DefaultRankMetadata
import gg.scala.infuse.commons.rank.metadata.defaults.DisplayNameMetadata
import gg.scala.infuse.commons.rank.metadata.defaults.NameMetadata
import gg.scala.infuse.commons.rank.metadata.defaults.VisibilityMetadata
import gg.scala.infuse.commons.rank.metadata.defaults.WeightMetadata
import gg.scala.infuse.commons.reference.impl.RankReference
import gg.scala.infuse.commons.text
import io.liftgate.lease.LeaseStrategy
import io.liftgate.lease.lease
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

/**
 * @author GrowlyX
 * @since 6/26/2022
 */
@Service
object RankService
{
    private val ranks = ConcurrentHashMap<UUID, InfuseRank>()

    @Suppress("UNCHECKED_CAST")
    private val defaults = InfuseCommons
        .flavor.reflections
        .getTypesAnnotatedWith<MetadataDefault>()
        .map { it as Class<RankMetadata<*>> }

    fun available() = ranks.size
    fun findAllRanks() = this.ranks.values.toList()

    @Configure
    fun configure()
    {
        DataStoreObjectControllerCache.create<InfuseRank>()

        this.reload().join()

        if (findDefaultRankNullable() == null)
        {
            this.createNewRank("Default").join()
                .updateMetadata<DefaultRankMetadata, Boolean>(true)
                .saveAndPushUpdatesGlobally()
                .join()

            logger.info("Automatically created default rank by name \"Default\"")
        }
    }

    @Close
    fun close() = this.ranks.clear()

    fun findDefaultRank(): InfuseRank =
        this.findDefaultRankNullable()!!

    fun findDefaultRankReference(): RankReference =
        RankReference(
            this.findDefaultRank().identifier
        )

    fun findDefaultRankNullable(): InfuseRank? =
        this.ranks.values
            .find {
                it.metadata<DefaultRankMetadata, Boolean>()
            }

    fun createNewRank(name: String): CompletableFuture<InfuseRank>
    {
        val rank = InfuseRank(UUID.randomUUID())
            .updateMetadata<NameMetadata, String>(name)

        this.configureDefaultMeta(rank)

        return rank.saveAndPushUpdatesGlobally()
            .thenApply { rank }
    }

    fun configureDefaultMeta(rank: InfuseRank)
    {
        rank.container.registerDefaults(
            *defaults.map { it.kotlin }.toTypedArray()
        )
    }

    // TODO: invalidate lease cache on new rank creation/update
    val sortedRankFormat by lease(
        strategy = LeaseStrategy.Compute,
        dependencies = listOf(InfuseRank::class)
    ) {
        val component = Component.empty()
        val ranks = ranks.values

        val comma = "".text()
            .color(NamedTextColor.WHITE)
            .append(", ".text())

        ranks
            .filter {
                it.metadata<VisibilityMetadata, Boolean>()
            }
            .sortedBy {
                -it.metadata<WeightMetadata, Int>()
            }
            .forEachIndexed { index, rank ->
                val container = rank.metadata<DisplayNameMetadata, Component>()
                component.append(container)

                if (index != ranks.size - 1)
                {
                    component.append(comma)
                }
            }

        component
    }

    fun findRank(uuid: UUID): InfuseRank?
    {
        return ranks.values
            .firstOrNull {
                it.identifier == uuid
            }
    }

    fun findRank(name: String): InfuseRank?
    {
        return ranks.values
            .firstOrNull {
                it.metadata<NameMetadata, String>()
                    .equals(name, true)
            }
    }

    fun reload(): CompletableFuture<Void>
    {
        return DataStoreObjectControllerCache
            .findNotNull<InfuseRank>()
            .loadAll(DataStoreStorageType.MONGO)
            .thenAccept { entries ->
                this.ranks.clear()
                this.ranks.putAll(entries)
            }
    }
}
