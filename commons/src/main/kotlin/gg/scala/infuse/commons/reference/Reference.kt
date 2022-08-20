package gg.scala.infuse.commons.reference

import net.evilblock.cubed.serializers.impl.AbstractTypeSerializable

/**
 * @author GrowlyX
 * @since 6/26/2022
 */
interface Reference<T, U> : AbstractTypeSerializable
{
    val original: T

    fun parsed(): U?
}
