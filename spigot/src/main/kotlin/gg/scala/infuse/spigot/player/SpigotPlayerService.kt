package gg.scala.infuse.spigot.player

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.infuse.commons.player.PlayerService
import gg.scala.infuse.spigot.InfuseSpigotPlayer
import gg.scala.store.storage.type.DataStoreStorageType
import me.lucko.helper.Events
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * @author GrowlyX
 * @since 8/20/2022
 */
@Service
object SpigotPlayerService : PlayerService<InfuseSpigotPlayer>(InfuseSpigotPlayer::class)
{
    @Configure
    fun configureSuper()
    {
        super.configure()

        Events.subscribe(AsyncPlayerPreLoginEvent::class.java)
            .handler {
                controller
                    .loadOptimalCopy(
                        it.uniqueId
                    ) {
                        InfuseSpigotPlayer(it.uniqueId, it.name)
                    }
                    .join()
            }

        Events.subscribe(PlayerQuitEvent::class.java)
            .handler {
                controller.localCache().remove(it.player.uniqueId)
                    ?.let { cached ->
                        this.controller.save(cached, DataStoreStorageType.MONGO)
                    }
            }
    }
}
