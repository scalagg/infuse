package gg.scala.infuse.spigot

import gg.scala.infuse.commons.audience
import gg.scala.infuse.commons.forever
import gg.scala.infuse.commons.grant.InfuseGrant
import gg.scala.infuse.commons.legacy
import gg.scala.infuse.commons.player.InfusePlayer
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.time.DateUtil
import org.bukkit.Bukkit
import org.bukkit.permissions.PermissionAttachment
import java.util.*

/**
 * @author GrowlyX
 * @since 8/20/2022
 */
class InfuseSpigotPlayer(
    identifier: UUID,
    username: String,
    ipAddress: String?
) : InfusePlayer(identifier, username, ipAddress)
{
    @Transient
    val loginTasks = mutableListOf<() -> Unit>()

    @Transient
    private var attachment: PermissionAttachment? = null

    fun player() = Bukkit
        .getPlayer(this.identifier)!!

    override fun recalculatePermissions()
    {
        this.player().recalculatePermissions()
    }

    override fun configurePermissionResources()
    {
        if (this.attachment != null)
        {
            this.attachment!!.permissions.clear()
            this.recalculatePermissions()
            return
        }

        this.attachment = player()
            .addAttachment(
                InfuseSpigot.instance
            )
    }

    override fun addPermission(node: String)
    {
        this.attachment!!.setPermission(
            node.removePrefix("*"),
            node.startsWith("*")
        )
    }

    override fun notifyPlayerOfRankUpdate(
        primaryGrant: InfuseGrant?, subGrant: InfuseGrant?
    )
    {
        val messenger = { grant: InfuseGrant, prefix: String, suffix: String ->
            this.identifier.audience().sendMessage(
                "${CC.GREEN}$prefix ".legacy()
                    .append(
                        grant.rank.parsed()!!.getColoredName()
                    )
                    .append(
                        "${CC.GREEN}$suffix.".legacy()
                    )
            )

            if (grant.duration != forever)
            {
                this.identifier.audience().sendMessage(
                    "${CC.GREEN}This rank will expire in: ${CC.WHITE}${
                        DateUtil.formatDateDiff(grant.applied.toEpochMilli())
                    }".legacy()
                )
            }
        }

        if (primaryGrant != null)
        {
            messenger(primaryGrant, "You've been given the", " rank")
        }

        if (subGrant != null)
        {
            messenger(subGrant, "You've been given a sub-rank of", "")
        }
    }

    override fun scheduleIfAvailable(
        instant: Boolean, task: () -> Unit
    )
    {
        if (instant)
        {
            Tasks.sync(task)
        } else
        {
            this.loginTasks.add(task)
        }
    }
}
