package gg.scala.infuse.spigot.commands

import gg.scala.commons.acf.CommandHelp
import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Default
import gg.scala.commons.acf.annotation.Description
import gg.scala.commons.acf.annotation.HelpCommand
import gg.scala.commons.acf.annotation.Optional
import gg.scala.commons.acf.annotation.Subcommand
import gg.scala.commons.acf.annotation.Syntax
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.infuse.commons.audience
import gg.scala.infuse.commons.legacy
import gg.scala.infuse.commons.rank.RankService
import gg.scala.infuse.spigot.display.RankDisplayResult
import net.evilblock.cubed.util.CC
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 6/26/2022
 */
@AutoRegister
@CommandAlias("rank")
@CommandPermission("infuse.command.rank")
object RankCommand : ScalaCommand()
{
    @Default
    @HelpCommand
    fun onHelp(help: CommandHelp)
    {
        help.showHelp()
    }

    @Subcommand("list|showall")
    @Description("View all existing ranks.")
    @CommandPermission("infuse.command.rank.subcommand.list")
    fun onList(sender: CommandSender, @Optional page: Int?)
    {
        val rankList = RankService.findAllRanks()

        if (rankList.isEmpty())
        {
            throw ConditionFailedException("There are no existing ranks.")
        }

        RankDisplayResult.display(
            sender, rankList, page ?: 1, "rank list %s"
        )
    }

    @Subcommand("create")
    @Description("Create a new rank.")
    @CommandPermission("infuse.command.rank.subcommand.create")
    fun onCreate(player: Player, name: String): CompletableFuture<Void>
    {
        if (name.contains(" "))
        {
            throw ConditionFailedException("Rank names must not contain whitespaces.")
        }

        val existing = RankService.findRank(name)

        if (existing != null)
        {
            throw ConditionFailedException("A rank by that name already exists.")
        }

        return RankService
            .createNewRank(name)
            .thenAcceptAsync {
                player.uniqueId.audience().sendMessage(
                    "${CC.SEC}You've created the ".legacy()
                        .append(
                            it.getColoredName()
                        )
                        .append(
                            "${CC.SEC} rank!".legacy()
                        )
                )
            }
    }
}
