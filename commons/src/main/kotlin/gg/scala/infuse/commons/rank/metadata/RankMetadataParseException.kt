package gg.scala.infuse.commons.rank.metadata

/**
 * @author GrowlyX
 * @since 6/26/2022
 */
class RankMetadataParseException(
    override val message: String = "Invalid input supplied"
) : IllegalArgumentException(message)
