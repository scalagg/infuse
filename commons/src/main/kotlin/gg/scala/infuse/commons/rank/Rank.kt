package gg.scala.infuse.commons.rank

import gg.scala.aware.thread.AwareThreadContext
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import gg.scala.infuse.commons.PlatformType
import gg.scala.infuse.commons.config
import gg.scala.infuse.commons.message
import gg.scala.infuse.commons.rank.metadata.RankMetadata
import gg.scala.infuse.commons.rank.metadata.RankMetadataContainer
import gg.scala.infuse.commons.reference.impl.RankReference
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 6/26/2022
 */
data class Rank(
    override val identifier: UUID
) : IDataStoreObject
{
    val container = RankMetadataContainer()

    val children = mutableListOf<RankReference>()
    val permissions = mutableMapOf<PlatformType, MutableMap<String, MutableList<String>>>()

    init
    {
        for (value in PlatformType.values())
        {
            this.permissions[value] =
                mutableMapOf(
                    "global" to mutableListOf()
                )
        }
    }

    inline fun <reified T : RankMetadata<U>, U> metadata(): U
    {
        return this.container.metadata<T>().value
    }

    inline fun <reified T : RankMetadata<U>, U> updateMetadata(value: U): Rank
    {
        this.container.metadata<T>().value = value
        return this
    }

    fun applicablePermissions(): List<String>
    {
        val compoundedPermissions =
            mutableListOf<String>()

        this.permissions[PlatformType.Spigot]!!
            .filter {
                config.groups.contains(it.key)
            }
            .forEach {
                compoundedPermissions += it.value
            }

        this.children
            .mapNotNull { it.parsed() }
            .flatMap { it.applicablePermissions() }
            .filter { !compoundedPermissions.contains(it) }
            .forEach {
                compoundedPermissions += it
            }

        return compoundedPermissions
    }

    private fun invalidate()
    {
        children.toList()
            .filter {
                it.parsed() == null
            }
            .forEach {
                this.children.remove(it)
            }
    }

    private fun save(): CompletableFuture<Void>
    {
        return DataStoreObjectControllerCache.findNotNull<Rank>()
            .save(this, DataStoreStorageType.MONGO)
    }

    fun saveAndPushUpdatesGlobally(): CompletableFuture<Void>
    {
        return this.save()
            .thenRun {
                message("rank-update")
                    .publish(
                        AwareThreadContext.SYNC
                    )
            }
    }
}
