package gg.scala.infuse.commons.player

import gg.scala.infuse.commons.PlatformType
import gg.scala.infuse.commons.config
import gg.scala.infuse.commons.grant.GrantService
import gg.scala.infuse.commons.grant.InfuseGrant
import gg.scala.infuse.commons.player.metadata.PlayerMetadata
import gg.scala.infuse.commons.player.metadata.PlayerMetadataContainer
import gg.scala.infuse.commons.utility.GrantUtil
import gg.scala.store.controller.annotations.Timestamp
import gg.scala.store.storage.storable.IDataStoreObject
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 8/20/2022
 */
open class InfusePlayer(
    override val identifier: UUID,
    val username: String,
    private val ipAddress: String? = null
) : IDataStoreObject
{
    @JvmField
    @Timestamp
    var timestamp = -1

    private val auditedIpAddresses = mutableSetOf<String>()
    private val assignedPermissions = mutableMapOf<PlatformType, MutableList<String>>()

    @Transient
    private var activeGrant: InfuseGrant? = null

    @Transient
    private var activeSubGrant: InfuseGrant? = null

    val metadata = PlayerMetadataContainer()

    open fun notifyPlayerOfRankUpdate(
        primaryGrant: InfuseGrant?, subGrant: InfuseGrant?
    )
    {

    }

    open fun addPermission(node: String)
    {

    }

    open fun configurePermissionResources()
    {

    }

    open fun recalculatePermissions()
    {

    }

    open fun reconfigurePlayerFrontend()
    {

    }

    open fun scheduleIfAvailable(instant: Boolean, task: () -> Unit)
    {

    }

    fun prominentGrant(): InfuseGrant =
        this.activeGrant!!

    @JvmOverloads
    fun recalculateGrants(
        autoNotify: Boolean = false,
        forceRecalculatePermissions: Boolean = false,
        shouldCalculateNow: Boolean = false,
        connecting: Boolean = false
    ): CompletableFuture<Void>
    {
        return GrantService.findGrantsFor(this.identifier)
            .thenAcceptAsync { grants ->
                if (grants == null || grants.isEmpty())
                {
                    if (this.activeGrant == null)
                    {
                        GrantService.createAutomatedGrant(this).join()
                    }

                    return@thenAcceptAsync
                }

                var shouldNotifyPlayer = autoNotify
                val previousRank = fetchPreviousRank(grants)

                grants.forEach { grant ->
                    if (!grant.removed && grant.hasExpired())
                    {
                        GrantUtil.expireGrant(grant).join()
                    }
                }

                val oldGrant = this.activeGrant

                this.activeGrant = GrantUtil
                    .getProminentGrant(grants)

                // This should never happen, so we will
                // revert the grant back to the old grant.
                if (this.activeGrant == null && oldGrant != null)
                {
                    this.activeGrant = oldGrant
                    return@thenAcceptAsync
                }

                var shouldRecalculatePermissions = forceRecalculatePermissions
                val activeGrantUid = this.activeGrant?.rank?.parsed()?.identifier

                if (
                    previousRank != null &&
                    activeGrant != null &&
                    previousRank != activeGrantUid
                )
                {
                    shouldRecalculatePermissions = true
                    shouldNotifyPlayer = true
                }

                val subGrant = GrantUtil
                    .getProminentSubGrant(
                        activeGrant!!, grants
                    )

                this.notifyPlayerOfRankUpdate(
                    if (shouldNotifyPlayer && !connecting)
                        this.prominentGrant() else null,
                    if (
                        subGrant != null &&
                        this.activeSubGrant != subGrant
                    ) subGrant else null
                )

                if (activeGrant == null)
                {
                    GrantService.createAutomatedGrant(this).join()
                }

                if (shouldRecalculatePermissions)
                {
                    handlePermissionApplication(grants, shouldCalculateNow)
                }
            }
    }

    fun handlePermissionApplication(
        grants: List<InfuseGrant>, instant: Boolean
    )
    {
        val handlePlayerSetup = {
            val permissionOnlyGrants = GrantUtil
                .getPermissibleGrants(grants)

            this.configurePermissionResources()

            for (grant in permissionOnlyGrants)
            {
                grant.rank.parsed()
                    ?.applicablePermissions()
                    ?.forEach { permission ->
                        this.addPermission(permission)
                    }
            }

            this.assignedPermissions[PlatformType.Spigot]
                ?.forEach {
                    this.addPermission(it)
                }

            this.recalculatePermissions()
            this.reconfigurePlayerFrontend()
        }

        this.scheduleIfAvailable(instant, handlePlayerSetup)
    }

    fun configureFirstLogin()
    {
        this.metadata.update(
            "first-login",
            PlayerMetadata(System.currentTimeMillis())
        )

        this.metadata.update(
            "first-login-server",
            PlayerMetadata(config.identifier)
        )

        this.metadata.update(
            "first-login-groups",
            PlayerMetadata(config.groups)
        )

        this.finalizeMetaData()

    }

    private fun finalizeMetaData()
    {
        this.metadata.update(
            "last-connection",
            PlayerMetadata(System.currentTimeMillis())
        )

        this.metadata.update(
            "last-connected-server-id",
            PlayerMetadata(config.identifier)
        )

        this.metadata.update(
            "last-connected-server-group",
            PlayerMetadata(config.groups)
        )

        if (this.ipAddress != null)
        {
            this.metadata.update(
                "last-ip-address",
                PlayerMetadata(ipAddress)
            )

            this.auditedIpAddresses.add(ipAddress)
        }
    }

    private fun fetchPreviousRank(grants: List<InfuseGrant>): UUID?
    {
        var uuid: UUID? = null

        if (activeGrant == null)
        {
            val currentGrant = GrantUtil
                .getProminentGrant(grants)

            if (currentGrant != null)
            {
                uuid = currentGrant.rank.parsed()?.identifier
            }
        } else
        {
            uuid = activeGrant?.rank?.parsed()?.identifier
        }

        return uuid
    }
}
