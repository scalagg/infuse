package gg.scala.infuse.commons.player

import gg.scala.flavor.service.Configure
import gg.scala.store.controller.DataStoreObjectController
import gg.scala.store.controller.DataStoreObjectControllerCache
import java.util.*
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 8/20/2022
 */
abstract class PlayerService<T : InfusePlayer>(
    private val type: KClass<T>
)
{
    lateinit var controller: DataStoreObjectController<T>

    fun find(uniqueId: UUID): T? =
        this.controller.localCache()[uniqueId]

    @Configure
    fun configure()
    {
        this.controller = DataStoreObjectControllerCache.create(type)
    }
}
