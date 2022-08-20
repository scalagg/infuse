package gg.scala.infuse.commons.expiring

import gg.scala.infuse.commons.forever
import net.evilblock.cubed.util.time.TimeUtil
import org.apache.commons.lang3.time.DurationFormatUtils
import java.time.Duration
import java.time.Instant
import java.util.*

/**
 * Useful abstract utility to denote
 * the expiration status of an object.
 *
 * A permanent model, or non-expiring
 * model will have a duration of [forever], which
 * is based off of [ChronoUnit]'s Duration interpretation of forever.
 *
 * @author GrowlyX
 * @since 6/25/2022
 */
abstract class Expiring(
    val applied: Instant,
    val duration: Duration = forever
)
{
    private val expiration = Instant.now()
        .plusMillis(duration.toMillis())

    private val expirationDate = Date
        .from(this.expiration)

    private val permanent =
        this.duration == forever

    fun expiresAt() = this.expiration

    fun untilExpire() = this.expiration
        .minusMillis(
            System.currentTimeMillis()
        )

    fun hasExpired() = Instant.now()
        .isAfter(this.expiration)

    val expirationFormat = if (permanent)
        "Never" else TimeUtil
        .formatIntoCalendarString(expirationDate)

    val durationFormat = if (permanent)
        "permanent" else
    {
        DurationFormatUtils
            .formatDurationWords(
                duration.toMillis(), true, true
            )
    }

    val durationFancyFormat: String
        get() = if (permanent)
            "not expire" else
        {
            "expire in ${
                DurationFormatUtils.formatDurationWords(
                    expirationDate.time - System.currentTimeMillis(),
                    true, true
                )
            }"
        }

    val durationFromNowFormat: String
        get() = if (permanent)
            "not expire" else
        {
            DurationFormatUtils.formatDurationWords(
                expirationDate.time - System.currentTimeMillis(),
                true, true
            )
        }
}
