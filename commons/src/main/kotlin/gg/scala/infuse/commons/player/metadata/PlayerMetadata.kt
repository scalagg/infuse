package gg.scala.infuse.commons.player.metadata

/**
 * @author GrowlyX
 * @since 8/20/2022
 */
data class PlayerMetadata(
    val value: Any
)
{
    inline fun <reified T> asType(): T
    {
        return this.value as T
    }
}
