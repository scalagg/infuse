package gg.scala.infuse.commons.configuration

/**
 * @author GrowlyX
 * @since 6/26/2022
 */
data class InfuseGeneralConfig(
    val identifier: String = "gen3",
    val groups: List<String> = listOf(
        "gen3-test"
    )
)
