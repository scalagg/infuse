package gg.scala.infuse.commons.player.metadata

/**
 * @author GrowlyX
 * @since 8/20/2022
 */
class PlayerMetadataContainer
{
    private val metadata = mutableMapOf<String, PlayerMetadata>()

    fun update(key: String, value: PlayerMetadata): PlayerMetadataContainer
    {
        this.metadata[key] = value
        return this
    }

    fun get(key: String): PlayerMetadata?
    {
        return this.metadata[key]
    }
}
