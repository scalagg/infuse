package gg.scala.infuse.commons.rank.metadata

import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 6/26/2022
 */
class RankMetadataContainer
{
    val metadata =
        mutableMapOf<KClass<out RankMetadata<*>>, RankMetadata<*>>()

    inline fun <reified T : RankMetadata<*>> metadata() =
        this.metadata[T::class]!! as T

    inline fun <reified T : RankMetadata<*>> value() =
        this.metadata<T>().value

    fun registerDefaults(
        vararg kClasses: KClass<out RankMetadata<*>>
    )
    {
        for (kClass in kClasses)
        {
            this.metadata[kClass] = kClass.java
                .getDeclaredConstructor().newInstance()
        }
    }
}
