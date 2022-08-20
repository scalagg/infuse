package gg.scala.infuse.spigot

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.container.ContainerDisable
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.config.annotations.ContainerConfig
import gg.scala.infuse.commons.InfuseCommons
import gg.scala.infuse.commons.InfusePlatform
import gg.scala.infuse.commons.PlatformType
import gg.scala.infuse.commons.configuration.InfuseGeneralConfig
import gg.scala.infuse.commons.player.PlayerService
import gg.scala.infuse.spigot.player.SpigotPlayerService
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 6/26/2022
 */
@ContainerConfig(
    value = "settings",
    model = InfuseGeneralConfig::class
)
@Plugin(
    name = "infuse",
    description = "Next-generation cross-platform network base.",
    depends = [
        PluginDependency("scala-commons"),
        PluginDependency("store-spigot"),
        PluginDependency("cloudsync", soft = true)
    ]
)
class InfuseSpigot : ExtendedScalaPlugin(), InfusePlatform<InfuseSpigotPlayer>
{
    companion object
    {
        @JvmStatic
        var instance by Delegates.notNull<InfuseSpigot>()
    }

    override val audience = lazy {
        BukkitAudiences.create(this)
    }

    override val generalConfig = lazy {
        this.config<InfuseGeneralConfig>()
    }

    override fun playerService() = SpigotPlayerService

    override val logging = this.logger!!
    override val platformType = PlatformType.Spigot

    @ContainerEnable
    fun containerEnable()
    {
        instance = this

        InfuseCommons.platform = this
        InfuseCommons.configure()
    }

    @ContainerDisable
    fun containerDisable()
    {
        InfuseCommons.close()
    }
}
