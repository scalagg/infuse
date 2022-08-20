package gg.scala.infuse.commons

import gg.scala.aware.message.AwareMessage
import gg.scala.infuse.commons.communication.CommunicationsService
import gg.scala.infuse.commons.configuration.InfuseGeneralConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 6/25/2022
 */
val forever = ChronoUnit.FOREVER.duration!!

val logger: Logger
    get() = platform.logging

val platform: InfusePlatform
    get() = InfuseCommons.platform

val config: InfuseGeneralConfig
    get() = platform.generalConfig.value

fun String.text() =
    Component.text(this)

fun String.legacy() = LegacyComponentSerializer
    .legacyAmpersand().deserialize(this)

fun Component.serialize() = LegacyComponentSerializer
    .legacyAmpersand().serialize(this)

fun UUID.audience() = platform
    .audience.value.player(this)

fun message(
    packet: String,
    vararg meta: Pair<String, Any>
): AwareMessage
{
    return AwareMessage.of(
        packet,
        CommunicationsService.aware,
        *meta
    )
}
