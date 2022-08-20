package gg.scala.infuse.commons.rank.metadata

import net.evilblock.cubed.serializers.impl.AbstractTypeSerializable

/**
 * @author GrowlyX
 * @since 6/26/2022
 */
interface RankMetadata<T> : AbstractTypeSerializable
{
    var value: T

    fun update(input: String)
    fun reset()

    fun name(): String
    fun description(): String
}
