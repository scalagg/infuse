package gg.scala.infuse.commons.grant

import gg.scala.infuse.commons.audit.AuditedModel
import gg.scala.infuse.commons.reference.impl.RankReference
import java.util.*

/**
 * @author GrowlyX
 * @since 6/26/2022
 */
class Grant(
    identifier: UUID,
    appliedTo: UUID,
    appliedBy: UUID,
    appliedFor: String,
    val rank: RankReference,
    scope: Set<String> = setOf("global"),
    automated: Boolean = false
) : AuditedModel(
    identifier, appliedTo,
    appliedBy, appliedFor,
    appliedScope = scope,
    appliedAutomated = automated
)
