package gg.scala.infuse.commons.audit

import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.infuse.commons.config
import gg.scala.infuse.commons.expiring.Expiring
import java.time.Instant
import java.util.UUID
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 6/26/2022
 */
abstract class AuditedModel(
    override val identifier: UUID,
    val appliedTo: UUID,
    val appliedBy: UUID?,
    val appliedFor: String,
    val appliedScope: Set<String> =
        setOf("global"),
    appliedAt: Instant = Instant.now(),
    val automated: Boolean = false
) : Expiring(appliedAt), IDataStoreObject
{
    val appliedOn = config.identifier

    var removedBy: UUID? = null
    var removedOn: String? = null
    var removedFor: String? = null
    var removedAt: Instant? = null

    val removed: Boolean
        get() = this.removedAt != null

    val active: Boolean
        get() = !removed && !hasExpired()

    fun customScope(): Boolean
    {
        if (
            this.appliedScope.size == 1 &&
            this.appliedScope.first() == "global"
        )
        {
            return false
        }

        return this.appliedScope.isNotEmpty()
    }

    fun applicable(): Boolean
    {
        if (this.appliedScope.contains("global"))
            return true

        return this.appliedScope
            .any {
                it in config.groups
            }
    }

    open fun save(): CompletableFuture<Void>
    {
        return CompletableFuture.completedFuture(null)
    }

    open fun saveAndPushUpdates(): CompletableFuture<Void>
    {
        return CompletableFuture.completedFuture(null)
    }
}
