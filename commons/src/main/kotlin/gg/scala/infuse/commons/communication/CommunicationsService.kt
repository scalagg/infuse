package gg.scala.infuse.commons.communication

import gg.scala.aware.AwareBuilder
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.reflections.PackageIndexer
import gg.scala.flavor.service.Close
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.infuse.commons.InfusePlatform
import gg.scala.infuse.commons.logger
import gg.scala.infuse.commons.rank.RankCommunicator

/**
 * @author GrowlyX
 * @since 6/26/2022
 */
@Service
object CommunicationsService
{
    @Inject
    lateinit var platform: InfusePlatform<*>

    val aware by lazy {
        AwareBuilder.of<AwareMessage>("infuse")
            .logger(platform.logging)
            .codec(AwareMessageCodec)
            .build()
    }

    @Configure
    fun configure()
    {
        aware.listen(RankCommunicator)

        aware.connect()
            .toCompletableFuture()
            .join()

        logger.info("Subscribed to incoming pub/sub messages.")
    }

    @Close
    fun close()
    {
        aware.shutdown()
    }
}
