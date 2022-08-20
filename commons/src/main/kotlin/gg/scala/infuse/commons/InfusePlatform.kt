package gg.scala.infuse.commons

import gg.scala.infuse.commons.configuration.InfuseGeneralConfig
import gg.scala.infuse.commons.player.InfusePlayer
import gg.scala.infuse.commons.player.PlayerService
import net.kyori.adventure.platform.AudienceProvider
import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 6/26/2022
 */
interface InfusePlatform<T : InfusePlayer>
{
    val audience: Lazy<AudienceProvider>
    val logging: Logger
    val platformType: PlatformType

    val generalConfig: Lazy<InfuseGeneralConfig>

    fun playerService(): PlayerService<T>
}
