package gg.scala.infuse.commons.grant

import gg.scala.aware.thread.AwareThreadContext
import gg.scala.infuse.commons.audit.AuditedModel
import gg.scala.infuse.commons.message
import gg.scala.infuse.commons.reference.impl.RankReference
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 6/26/2022
 */
class InfuseGrant(
    identifier: UUID,
    appliedTo: UUID,
    appliedBy: UUID?,
    appliedFor: String,
    val rank: RankReference,
    scope: Set<String> = setOf("global"),
    automated: Boolean = false
) : AuditedModel(
    identifier, appliedTo,
    appliedBy, appliedFor,
    appliedScope = scope,
    automated = automated
)
{
    override fun save() =
        DataStoreObjectControllerCache
            .findNotNull<InfuseGrant>()
            .save(this, DataStoreStorageType.MONGO)

    override fun saveAndPushUpdates(): CompletableFuture<Void> =
        save().thenRun {
            message(
                "recalculate-grants",
                "player" to this.appliedTo.toString()
            ).publish(
                AwareThreadContext.SYNC
            )
        }
}
