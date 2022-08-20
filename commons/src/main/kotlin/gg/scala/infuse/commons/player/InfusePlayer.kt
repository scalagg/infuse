package gg.scala.infuse.commons.player

import gg.scala.store.controller.annotations.Timestamp
import gg.scala.store.storage.storable.IDataStoreObject
import java.util.*

/**
 * @author GrowlyX
 * @since 8/20/2022
 */
open class InfusePlayer(
    override val identifier: UUID,
    val username: String
) : IDataStoreObject
{
    @JvmField
    @Timestamp
    var timestamp = -1
}
