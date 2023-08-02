package fr.vallfeur.ryuka.commands

import fr.vallfeur.ryuka.FileManager
import fr.vallfeur.ryuka.Main
import fr.vallfeur.ryuka.Util
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.UserSnowflake
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.LayoutComponent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction
import okhttp3.internal.toImmutableList
import java.io.Serializable
import java.lang.NumberFormatException
import java.net.http.WebSocket.Listener
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Warn : ListenerAdapter() {

    companion object {
        // memberId, list  (list count == the warns count): with lists a contains : reasons, warn author, timestamp
        val warns = HashMap<String, MutableList<List<String>>>()
        // msgId, memberId
        private val msgSave = HashMap<String, String>()

        private lateinit var args: List<String>
        private lateinit var event: MessageReceivedEvent

        fun init(args: List<String>, event: MessageReceivedEvent) {
            this.args = args
            this.event = event

            when (args[0]) {
                "add" -> {
                    if (args.size < 3) {
                        event.channel.sendMessageEmbeds(Util.errorEmbed("${Main.config.getPrefix()}warn add <utilisateur> <raison>")).queue()

                    }
                    else {
                        val memberId = Util.parseId(args[1])

                        if (!Util.checkIdFormat(memberId)) {
                            event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().idFormat)).queue()

                        }
                        else {
                            val sb = StringBuilder()

                            args.forEach {
                                if (it != args[0] && it != args[1]) sb.append("$it ")

                            }

                            if (warns[memberId] == null) warns[memberId] = mutableListOf()
                            warns[memberId]!!.add(listOf("$sb", event.author.id, "${Instant.now().epochSecond}"))

                            FileManager.warnUpdate()


                            event.channel.sendMessageEmbeds(EmbedBuilder()
                                .setTitle(Util.parseMessage(Main.config.getMessages().warnAdd.resultTitle, event, memberId, "notMention"))
                                .setColor(Main.color)
                                .addField(Main.config.getMessages().warnAdd.resultFirstField, "$sb", true)
                                .addField(Main.config.getMessages().warnAdd.resultSecondField, "${warns[memberId]!!.size}", true)
                                .setFooter(Util.parseMessage(Main.config.getMessages().runnedBy, event, memberId), event.jda.selfUser.avatarUrl)
                                .build()
                            ).queue()

                        }
                    }

                }
                "list" -> {
                    if (args.size != 2) {
                        event.channel.sendMessageEmbeds(Util.errorEmbed("${Main.config.getPrefix()}warn list <utilisateur>")).queue()

                    }
                    else {
                        val memberId = Util.parseId(args[1])

                        if (!Util.checkIdFormat(memberId)) {
                            event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().idFormat)).queue()

                        }
                        else {
                            if (!warns.contains(memberId)) {
                                event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().warnList.noWarnRegistered)).queue()

                            }
                            else {
                                if (!event.guild.isMember(UserSnowflake.fromId(memberId))) {
                                    event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().memberNotFound)).queue()

                                }
                                else {
                                    val display = displayEmbed(memberId, 1)

                                    val msg = event.channel.sendMessageEmbeds(display[0] as MessageEmbed)
                                    if (display[1] != 1) msg.addActionRow(
                                        Button.secondary("prev", Emoji.fromUnicode("\u25C0")).asDisabled(),
                                        Button.secondary("page", "1/${display[1]}").asDisabled(),
                                        Button.secondary("next", Emoji.fromUnicode("\u25B6")).withDisabled(display[1] == 1)
                                    )

                                    msg.queue { msgSave[it.id] = memberId }

                                }

                            }

                        }

                    }

                }
                "remove" -> {
                    if (args.size != 3) {
                        event.channel.sendMessageEmbeds(Util.errorEmbed("${Main.config.getPrefix()}warn remove <utilisateur> <warn id>")).queue()

                    }
                    else {
                        val memberId = Util.parseId(args[1])

                        if (!Util.checkIdFormat(memberId)) {
                            event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().idFormat)).queue()

                        }
                        else {
                            if (!warns.contains(memberId)) {
                                event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().warnList.noWarnRegistered)).queue()

                            }
                            else {
                                if (!event.guild.isMember(UserSnowflake.fromId(memberId))) {
                                    event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().memberNotFound)).queue()

                                }
                                else {
                                    if (!Util.checkIdFormat(args[2])) {
                                        event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().warnRemove.idFormat)).queue()

                                    }
                                    else {
                                        val warnId = args[2].toInt()

                                        if (warns[memberId]!!.size < warnId) {
                                            event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().warnRemove.wrongId)).queue()

                                        }
                                        else {
                                            warns[memberId]!!.removeAt(warnId - 1)
                                            if (warns[memberId]!!.isEmpty()) warns.remove(memberId)

                                            FileManager.warnUpdate()

                                            event.channel.sendMessageEmbeds(EmbedBuilder()
                                                .setTitle(Util.parseMessage(Main.config.getMessages().warnRemove.resultTitle, event, memberId, "notMention"))
                                                .setColor(Main.color)
                                                .setFooter(Util.parseMessage(Main.config.getMessages().runnedBy, event, memberId, "notMention"), event.jda.selfUser.avatarUrl)
                                                .setTimestamp(Calendar.getInstance().toInstant())
                                                .build()
                                            ).queue()

                                        }

                                    }

                                }

                            }

                        }

                    }

                }
                else -> event.channel.sendMessageEmbeds(Util.errorEmbed("${Main.config.getPrefix()}warn <add | list | remove>")).queue()

            }

        }



        private fun displayEmbed(memberId: String, page: Int): List<Any> {
            val embed = EmbedBuilder()
                .setTitle(Util.parseMessage(Main.config.getMessages().warnList.resultTitle, event, memberId, "notMention"))
                .setColor(Main.color)
                .setFooter(Util.parseMessage(Main.config.getMessages().runnedBy, event, memberId), event.jda.selfUser.avatarUrl)
                .setTimestamp(Calendar.getInstance().toInstant())

            // page, fields
            val fields = HashMap<Int, List<MessageEmbed.Field>>()
            var fieldCount = 1

            var i = 1

            val tempList = ArrayList<MessageEmbed.Field>()
            var pageCount = 1
            warns[memberId]!!.forEach {
                if (fieldCount == 4) {
                    fields[pageCount] = tempList.toList()
                    tempList.clear()

                    fieldCount = 1
                    pageCount++
                }

                tempList.addAll(listOf(
                    MessageEmbed.Field("Warn $i", "<t:${it[2]}:F>", false),
                    MessageEmbed.Field("Raison", it[0], true),
                    MessageEmbed.Field("Auteur", event.guild.getMemberById(it[1])?.asMention ?: "introuvable", true)
                ))

                fieldCount++
                i++
            }

            // adding the rest of the elements if fields is not a multiple of 3
            if (tempList.isNotEmpty()) fields[pageCount] = tempList.toList()

            fields[page]!!.forEach { embed.addField(it) }

            return listOf(embed.build(), pageCount)
        }

    }


    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        /*
            next page
         */
        if (event.componentId == "next") {
            if (msgSave[event.message.id] == null) {
                event.editComponents().setActionRow(Button.secondary("unknown", Main.config.getMessages().buttons.userNotFound).asDisabled()).queue()

            }
            else if (warns[msgSave[event.message.id]] == null) {
                event.editComponents().setActionRow(Button.secondary("unknown", Main.config.getMessages().buttons.userWarnsNotFound).asDisabled()).queue()

            }
            else if (!event.message.embeds[0].footer!!.text!!.contains(event.user.effectiveName)) {
                event.reply(Main.config.getMessages().buttons.notMessageAuthor).setEphemeral(true).queue { it.setEphemeral(true) }

            }
            else {
                val page = event.message.getButtonById("page")!!.label.split("/")[0].toInt() + 1
                val display = displayEmbed(msgSave[event.message.id]!!, page)

                event.editMessageEmbeds(display[0] as MessageEmbed).setActionRow(
                    Button.secondary("prev", Emoji.fromUnicode("\u25C0")),
                    Button.secondary("page", "$page/${display[1]}").asDisabled(),
                    Button.secondary("next", Emoji.fromUnicode("\u25B6")).withDisabled((display[1] as Int == page))
                ).queue()

            }

        }

        /*
            prev page
         */
        if (event.componentId == "prev") {
            if (msgSave[event.message.id] == null) {
                event.editComponents().setActionRow(Button.secondary("unknown", Main.config.getMessages().buttons.userNotFound).asDisabled()).queue()

            }
            else if (warns[msgSave[event.message.id]] == null) {
                event.editComponents().setActionRow(Button.secondary("unknown", Main.config.getMessages().buttons.userWarnsNotFound).asDisabled()).queue()

            }
            else if (!event.message.embeds[0].footer!!.text!!.contains(event.user.effectiveName)) {
                event.reply(Main.config.getMessages().buttons.notMessageAuthor).setEphemeral(true).queue()

            }
            else {
                val page = event.message.getButtonById("page")!!.label.split("/")[0].toInt() - 1
                val display = displayEmbed(msgSave[event.message.id]!!, page)

                event.editMessageEmbeds(display[0] as MessageEmbed).setActionRow(
                    Button.secondary("prev", Emoji.fromUnicode("\u25C0")).withDisabled((page == 1)),
                    Button.secondary("page", "$page/${display[1]}").asDisabled(),
                    Button.secondary("next", Emoji.fromUnicode("\u25B6")).withDisabled((display[1] as Int == page))
                ).queue()

            }

        }

    }

}
