package gg.scala.infuse.commons.rank.metadata.defaults

import gg.scala.infuse.commons.rank.RankService
import gg.scala.infuse.commons.rank.metadata.annotate.MetadataDefault
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

/**
 * @author GrowlyX
 * @since 6/26/2022
 */
@MetadataDefault
class NameMetadata : DefaultMetadata<String>("Default")
{
    override fun parse(input: String) = input

    override fun name() = "Name"
    override fun description() =
        "Describes the rank mentioned in a single word."

    override fun getAbstractType() = NameMetadata::class.java
}

@MetadataDefault
class DisplayNameMetadata : MiniMessageMetadata(
    Component.empty()
        .color(NamedTextColor.GRAY)
        .append(
            Component.text("Default")
        )
)
{
    override fun name() = "Display Name"
    override fun description() =
        "Displayed on the response of a list command, scoreboard, or something else."

    override fun getAbstractType() = DisplayNameMetadata::class.java
}

@MetadataDefault
class PrefixMetadata : MiniMessageMetadata(
    Component.empty().color(NamedTextColor.GRAY)
)
{
    override fun name() = "Prefix"
    override fun description() =
        "Displayed at the beginning of the player's username in a chat message."

    override fun getAbstractType() = PrefixMetadata::class.java
}

@MetadataDefault
class SuffixMetadata : MiniMessageMetadata(
    Component.empty()
)
{
    override fun name() = "Suffix"
    override fun description() =
        "Displayed at the end of the player's username in a chat message."

    override fun getAbstractType() = SuffixMetadata::class.java
}

@MetadataDefault
class ColorMetadata : MiniMessageMetadata(
    Component.empty().color(NamedTextColor.GRAY)
)
{
    override fun name() = "Color"
    override fun description() =
        "Displayed at the beginning of a name-tag, or something of that sort."

    override fun getAbstractType() = ColorMetadata::class.java
}

@MetadataDefault
class VisibilityMetadata : BooleanMetadata(true)
{
    override fun name() = "Visibility"
    override fun description() =
        "Is this a public rank or no? You decide"

    override fun getAbstractType() = VisibilityMetadata::class.java
}

@MetadataDefault
class DefaultRankMetadata : BooleanMetadata(false)
{
    override fun name() = "Default Rank"
    override fun description() =
        "Is this a default rank?"

    override fun getAbstractType() = DefaultRankMetadata::class.java
}

@MetadataDefault
class WeightMetadata : IntegerMetadata(RankService.available())
{
    override fun name() = "Weight"
    override fun description() =
        "Where does this rank lie in the list of available ranks?"

    override fun getAbstractType() = WeightMetadata::class.java
}


