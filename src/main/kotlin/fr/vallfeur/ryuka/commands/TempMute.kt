package fr.vallfeur.ryuka.commands

import fr.vallfeur.ryuka.FileManager
import fr.vallfeur.ryuka.Main
import fr.vallfeur.ryuka.Util
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.UserSnowflake
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.io.Serializable
import java.time.OffsetDateTime.*
import java.util.*

class TempMute(args: List<String>, event: MessageReceivedEvent) {

    /*
        -tempmute <member> <duration> <duration unit> <reason>
     */

    init {
        if (args.size < 4) {
            event.channel.sendMessageEmbeds(Util.errorEmbed("${Main.config.getPrefix()}tempmute <utilisateur> <durée> <unité> <raison>")).queue()

        }
        else {
            val memberId = Util.parseId(args[0])

            if (!Util.checkIdFormat(memberId)) {
                event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().idFormat)).queue()

            }
            else {
                if (!event.guild.isMember(UserSnowflake.fromId(memberId))) {
                    event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().memberNotFound)).queue()

                }
                else {

                    if (mutes.contains(memberId)) {
                        event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().tempmute.alreadyMute)).queue()

                    }
                    else {

                        try {
                            val count = Integer.parseInt(args[1])

                            if (!listOf("s", "m", "j", "h").contains(args[2])) {
                                event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().invalidUnit)).queue()

                            }
                            else
                            {
                                val sb = StringBuilder();

                                args.forEach {
                                    if (!(args[0].equals(it, true) || args[1].equals(it, true) || args[2].equals(it, true))) {
                                        sb.append("$it ")

                                    }

                                }

                                add(memberId, count, args[2], sb.toString(), args[1])
                                event.guild.getRoleById(Main.config.getRoles().mute)?.let { event.guild.addRoleToMember(UserSnowflake.fromId(memberId), it).queue() }

                                event.channel.sendMessageEmbeds(
                                    EmbedBuilder()
                                        .setTitle(Main.config.getMessages().tempmute.resultTitle)
                                        .setColor(Main.color)
                                        .setDescription(Util.parseMessage(Main.config.getMessages().tempmute.resultDescription, event, memberId))
                                        .addField("Durée", "${args[1]}${args[2]}", true)
                                        .addField("Raison", "$sb", true)
                                        .setTimestamp(now())
                                        .setFooter(Util.parseMessage(Main.config.getMessages().runnedBy, event, memberId), event.jda.selfUser.avatarUrl)
                                        .build()
                                ).queue()

                            }

                        } catch (_: NumberFormatException) {
                            event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().tempmute.invalidDuration)).queue()

                        }

                    }

                }

            }

        }


    }

    // memberId, list containg : expiration time as Calendar, duration as String, reason as String, when as Long
    companion object {
        val mutes = HashMap<String, List<Serializable>>()

    }

    private fun add(memberId: String, count: Int, unit: String, reason: String, duration: String) {
        val currentTime = Calendar.getInstance()

        val expirationTime = when (unit) {
            "s" -> currentTime.apply { add(Calendar.SECOND, count) }
            "m" -> currentTime.apply { add(Calendar.MINUTE, count) }
            "h" -> currentTime.apply { add(Calendar.HOUR, count) }
            "j" -> currentTime.apply { add(Calendar.DAY_OF_MONTH, count) }
            else -> currentTime

        }

        mutes[memberId] = listOf(expirationTime, duration + unit, reason, Calendar.getInstance(Locale.FRANCE).time.toInstant().epochSecond)
        FileManager.tempMuteUpdate()
    }

}