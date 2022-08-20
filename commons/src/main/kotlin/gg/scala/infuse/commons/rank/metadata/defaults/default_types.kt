package gg.scala.infuse.commons.rank.metadata.defaults

import gg.scala.infuse.commons.rank.metadata.RankMetadata
import gg.scala.infuse.commons.rank.metadata.RankMetadataParseException
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

/**
 * @author GrowlyX
 * @since 6/26/2022
 */
abstract class DefaultMetadata<T>(
    private val default: T
) : RankMetadata<T>
{
    override var value = this.default

    override fun update(input: String)
    {
        this.value = parse(input)
    }

    override fun reset()
    {
        this.value = this.default
    }

    abstract fun parse(input: String): T
}

abstract class BooleanMetadata(
    defaultSetting: Boolean
) : DefaultMetadata<Boolean>(defaultSetting)
{
    override fun parse(input: String) =
        input.lowercase()
            .toBooleanStrictOrNull()
            ?: throw RankMetadataParseException(
                "Invalid boolean supplied (values: true, false)"
            )
}

abstract class IntegerMetadata(
    defaultSetting: Int
) : DefaultMetadata<Int>(defaultSetting)
{
    override fun parse(input: String) =
        input.lowercase()
            .toIntOrNull()
            ?: throw RankMetadataParseException(
                "Invalid integer supplied (ex: 1, 2, 3)"
            )
}

abstract class MiniMessageMetadata(
    private val defaultComponent: Component
) : DefaultMetadata<Component>(
    defaultComponent
)
{
    override fun parse(input: String) =
        MiniMessage.miniMessage()
            .deserialize(input)

    override fun reset()
    {
        this.value = defaultComponent
    }
}
