package fr.vallfeur.ryuka.commands

import fr.vallfeur.ryuka.FileManager
import fr.vallfeur.ryuka.Main
import fr.vallfeur.ryuka.Util
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.UserSnowflake
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.time.Instant
import java.util.*
import kotlin.collections.HashMap

class Blr(args: List<String>, event: MessageReceivedEvent) {

    companion object {
        // memberId, list: reason, authorId, timestamp
        val list = HashMap<String, List<String>>()

    }

    init {
        if (args.isNotEmpty()) {
            when (args[0]) {
                "add" -> {
                    if (args.size < 3) {
                        event.channel.sendMessageEmbeds(Util.errorEmbed("${Main.config.getPrefix()}blr add <utilisateur> <raison>"))
                            .queue()

                    } else {
                        val memberId = Util.parseId(args[1])

                        if (!Util.checkIdFormat(memberId)) {
                            event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().idFormat)).queue()

                        } else {
                            val sb = StringBuilder()
                            args.forEach { if (it != args[1] && it != args[0]) sb.append("$it ") }

                            list[memberId] = listOf("$sb", event.author.id, "${Instant.now().epochSecond}")

                            FileManager.blrUpdate()

                            event.guild.getMemberById(memberId)!!.roles.forEach {
                                if (it.id != Main.config.getRoles().permi) event.guild.removeRoleFromMember(
                                    UserSnowflake.fromId(memberId),
                                    it
                                ).queue()

                            }

                            event.channel.sendMessageEmbeds(EmbedBuilder()
                                .setTitle(Util.parseMessage(
                                    Main.config.getMessages().blr.resultTitle,
                                    event,
                                    memberId,
                                    "notMention"
                                )
                                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                                )
                                .setDescription("**Raison:** $sb")
                                .setFooter(
                                    Util.parseMessage(Main.config.getMessages().runnedBy, event, memberId),
                                    event.jda.selfUser.avatarUrl
                                )
                                .setTimestamp(Calendar.getInstance().toInstant())
                                .setColor(Main.color)
                                .build()
                            ).queue()

                        }

                    }

                }

                "list" -> {
                    if (args.size == 1) {
                        if (list.isEmpty()) {
                            event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().blr.showNoOne))
                                .queue()

                        } else {
                            val embed = EmbedBuilder()
                                .setColor(Main.color)
                                .setTitle(Main.config.getMessages().blr.showResultSecondTitle)
                                .setFooter(
                                    Util.parseMessage(Main.config.getMessages().runnedBy, event),
                                    event.jda.selfUser.avatarUrl
                                )
                                .setTimestamp(Calendar.getInstance().toInstant())

                            var i = 1
                            list.forEach { (memberId, data) ->
                                embed.addField(
                                    "Blr #$i",
                                    event.guild.getMemberById(memberId)?.asMention ?: "introuvable",
                                    false
                                )
                                embed.addField("Raison", data[0], true)
                                embed.addField(
                                    "Auteur",
                                    event.guild.getMemberById(data[1])?.asMention ?: "introuvable",
                                    true
                                )
                                embed.addField("Date", "<t:${data[2]}:F>", true)

                                i++
                            }

                            event.channel.sendMessageEmbeds(embed.build()).queue()

                        }

                    } else if (args.size == 2) {
                        val memberId = Util.parseId(args[1])

                        if (!Util.checkIdFormat(memberId)) {
                            event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().idFormat)).queue()

                        } else {
                            if (!list.contains(memberId)) {
                                event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().blr.notFoundInList))
                                    .queue()

                            } else {
                                val data = list[memberId]!!

                                event.channel.sendMessageEmbeds(
                                    EmbedBuilder()
                                        .setTitle(
                                            Util.parseMessage(
                                                Main.config.getMessages().blr.showResultTitle,
                                                event,
                                                memberId,
                                                "notMention"
                                            )
                                        )
                                        .setColor(Main.color)
                                        .addField("Raison", data[0], true)
                                        .addField(
                                            "Auteur",
                                            event.guild.getMemberById(data[1])?.asMention ?: "introuvable",
                                            true
                                        )
                                        .addField("Date", "<t:${data[2]}:F>", true)
                                        .setFooter(
                                            Util.parseMessage(
                                                Main.config.getMessages().runnedBy,
                                                event,
                                                memberId,
                                                "notMention"
                                            ), event.jda.selfUser.avatarUrl
                                        )
                                        .setTimestamp(Calendar.getInstance().toInstant())
                                        .build()
                                ).queue()

                            }

                        }

                    } else {
                        event.channel.sendMessageEmbeds(Util.errorEmbed("${Main.config.getPrefix()}blr list <utilisateur>"))
                            .queue()

                    }

                }

                "remove" -> {
                    if (args.size != 2) {
                        event.channel.sendMessageEmbeds(Util.errorEmbed("${Main.config.getPrefix()}blr remove <utilisateur>")).queue()

                    }
                    else {
                        val memberId = Util.parseId(args[1])

                        if (!Util.checkIdFormat(memberId)) {
                            event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().idFormat)).queue()

                        }
                        else {
                            if (!list.contains(memberId)) {
                                event.channel.sendMessageEmbeds(Util.errorEmbed(Util.parseMessage(Main.config.getMessages().blr.notFoundInList, event, memberId))).queue()

                            }
                            else {
                                list.remove(memberId)
                                FileManager.blrUpdate()

                                event.channel.sendMessageEmbeds(EmbedBuilder()
                                    .setTitle(Util.parseMessage(Main.config.getMessages().blr.removeResultTitle, event, memberId, "notMention"))
                                    .setColor(Main.color)
                                    .setFooter(Util.parseMessage(Main.config.getMessages().runnedBy, event, memberId, "notMention"), event.jda.selfUser.avatarUrl)
                                    .setTimestamp(Calendar.getInstance().toInstant())
                                    .build()
                                ).queue()

                            }

                        }

                    }

                }

                else -> event.channel.sendMessageEmbeds(Util.errorEmbed("${Main.config.getPrefix()}blr <add | list | remove>")).queue()

            }

        }
        else
            event.channel.sendMessageEmbeds(Util.errorEmbed("${Main.config.getPrefix()}blr <add | list | remove>")).queue()

    }

}
