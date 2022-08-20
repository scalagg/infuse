package gg.scala.infuse.commons

import gg.scala.flavor.Flavor
import gg.scala.flavor.FlavorOptions
import gg.scala.flavor.reflections.PackageIndexer
import gg.scala.infuse.commons.rank.metadata.RankMetadata
import gg.scala.infuse.commons.reference.Reference
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializer
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 6/26/2022
 */
object InfuseCommons
{
    var platform by Delegates.notNull<InfusePlatform>()

    val flavor by lazy {
        Flavor.create<InfuseCommons>(
            FlavorOptions(platform.logging)
        )
    }

    fun configure()
    {
        Serializers.create {
            registerTypeHierarchyAdapter(
                Reference::class.java,
                AbstractTypeSerializer<Reference<*, *>>()
            )

            registerTypeHierarchyAdapter(
                RankMetadata::class.java,
                AbstractTypeSerializer<RankMetadata<*>>()
            )
        }

        flavor.bind<InfusePlatform>() to platform
        flavor.startup()
    }

    fun close()
    {
        flavor.close()
    }
}
