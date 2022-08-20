package gg.scala.infuse.commons.rank.metadata.defaults

import gg.scala.infuse.commons.rank.metadata.RankMetadata
import gg.scala.infuse.commons.rank.metadata.RankMetadataParseException
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

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

    abstract fun displayAsText(): Component
    abstract fun parse(input: String): T
}

abstract class BooleanMetadata(
    private val defaultSetting: Boolean
) : DefaultMetadata<Boolean>(defaultSetting)
{
    override fun displayAsText() = Component
        .text(
            if (this.value) "Yes" else "No"
        )
        .color(
            if (this.value) NamedTextColor.GREEN else NamedTextColor.RED
        )

    override fun parse(input: String) =
        input.lowercase()
            .toBooleanStrictOrNull()
            ?: throw RankMetadataParseException(
                "Invalid boolean supplied (values: true, false)"
            )


    override fun reset()
    {
        this.value = this.defaultSetting
    }
}

abstract class IntegerMetadata(
    private val defaultSetting: Int
) : DefaultMetadata<Int>(defaultSetting)
{
    override fun displayAsText() = Component
        .text(this.value)

    override fun parse(input: String) =
        input.lowercase()
            .toIntOrNull()
            ?: throw RankMetadataParseException(
                "Invalid integer supplied (ex: 1, 2, 3)"
            )

    override fun reset()
    {
        this.value = this.defaultSetting
    }
}

abstract class ComponentMetadata(
    private val defaultComponent: Component
) : DefaultMetadata<Component>(
    defaultComponent
)
{
    override fun displayAsText() = this.value

    override fun parse(input: String) =
        LegacyComponentSerializer
            .legacyAmpersand().deserialize(input)

    override fun reset()
    {
        this.value = defaultComponent
    }
}
