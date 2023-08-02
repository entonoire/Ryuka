package fr.vallfeur.ryuka.commands

import fr.vallfeur.ryuka.Main
import fr.vallfeur.ryuka.Util
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.UserSnowflake
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.util.*

class TempMuteList(args: List<String>, event: MessageReceivedEvent) {

    /*
        -tempmutelist [member]
     */

    init {
        if (args.isNotEmpty()) {
            val memberId = args[0].replace("<@", "").replace(">", "").trim()

            if (!Util.checkIdFormat(memberId)) {
                event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().idFormat)).queue()

            }
            else {
                if (!event.guild.isMember(UserSnowflake.fromId(memberId))) {
                    event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().memberNotFound)).queue()

                }
                else {
                    if (!TempMute.mutes.contains(memberId)) {
                        event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().tempmutelist.memberNotMute)).queue()

                    }
                    else {
                        val member = event.guild.getMemberById(memberId)

                        var duration = TempMute.mutes[memberId]!![1] as String
                        val reason = TempMute.mutes[memberId]!![2] as String
                        val time = TempMute.mutes[memberId]!![3] as Long

                        duration = if (duration[0] == '1')
                            duration
                                .replace("s"," Seconde")
                                .replace("m"," Minute")
                                .replace("h"," Heure")
                                .replace("j"," Jour")
                        else
                            duration
                                .replace("s"," Secondes")
                                .replace("m"," Minutes")
                                .replace("h"," Heures")
                                .replace("j"," Jours")


                        val embed = EmbedBuilder()
                            .setTitle(Main.config.getMessages().tempmutelist.memberResultTitle.replace("\$member", member!!.effectiveName))
                            .setColor(Main.color)
                            .addField(Main.config.getMessages().tempmutelist.resultFirstFieldName, member.asMention, true)
                            .addField(Main.config.getMessages().tempmutelist.resultSecondFieldName, duration, true)
                            .addField(Main.config.getMessages().tempmutelist.resultThirdFieldName, "<t:$time:F>", true)
                            .addField(Main.config.getMessages().tempmutelist.resultFourthFieldName, reason, true)
                            .setTimestamp(Calendar.getInstance().toInstant())
                            .setFooter(Util.parseMessage(Main.config.getMessages().runnedBy, event, memberId), event.jda.selfUser.avatarUrl)

                        event.channel.sendMessageEmbeds(embed.build()).queue()

                    }

                }

            }

        }
        else {
            if (TempMute.mutes.isEmpty()) {
                val embed = EmbedBuilder()
                    .setTitle(Main.config.getMessages().tempmutelist.noMemberTempmute)
                    .setColor(Main.color)
                    .setTimestamp(Calendar.getInstance().toInstant())
                    .setFooter(Util.parseMessage(Main.config.getMessages().runnedBy, event), event.jda.selfUser.avatarUrl)
                    .build()


                event.channel.sendMessageEmbeds(embed).queue()

            }
            else {
                val embed = EmbedBuilder()
                    .setTitle(Main.config.getMessages().tempmutelist.resultTitle)
                    .setColor(Main.color)
                    .setTimestamp(Calendar.getInstance().toInstant())
                    .setFooter(Util.parseMessage(Main.config.getMessages().runnedBy, event), event.jda.selfUser.avatarUrl)

                TempMute.mutes.forEach { (memberId) ->
                    var duration = TempMute.mutes[memberId]!![1] as String
                    val reason = TempMute.mutes[memberId]!![2] as String
                    val time = TempMute.mutes[memberId]!![3] as Long

                    duration = if (duration[0] == '1')
                        duration
                            .replace("s"," Seconde")
                            .replace("m"," Minute")
                            .replace("h"," Heure")
                            .replace("j"," Jour")
                    else
                        duration
                            .replace("s"," Secondes")
                            .replace("m"," Minutes")
                            .replace("h"," Heures")
                            .replace("j"," Jours")

                    if (event.guild.isMember(UserSnowflake.fromId(memberId))) embed.addField(Main.config.getMessages().tempmutelist.resultFirstFieldName, event.guild.getMemberById(memberId)!!.effectiveName, false)
                    else                                                      embed.addField(Main.config.getMessages().tempmutelist.resultFirstFieldName, "introuvable", false)
                    embed.addField(Main.config.getMessages().tempmutelist.resultSecondFieldName, duration, true)
                    embed.addField(Main.config.getMessages().tempmutelist.resultThirdFieldName, "<t:$time:F>", true)
                    embed.addField(Main.config.getMessages().tempmutelist.resultFourthFieldName, reason, true)

                }


                /*
                    create a tiny embed if to long
                 */
                if (!embed.isValidLength) {
                    embed
                        .setTitle(Main.config.getMessages().tempmutelist.resultTitle)
                        .setDescription(Main.config.getMessages().tempmutelist.resultToLong)
                        .setColor(Main.color)
                        .setTimestamp(Calendar.getInstance().toInstant())
                        .setFooter(Util.parseMessage(Main.config.getMessages().runnedBy, event), event.jda.selfUser.avatarUrl)

                    var i = 0;
                    TempMute.mutes.forEach { (memberId, list) ->
                        if (i >= 9) return@forEach

                        var duration = TempMute.mutes[memberId]!![1] as String
                        val reason = TempMute.mutes[memberId]!![2] as String
                        val time = TempMute.mutes[memberId]!![3] as Long

                        duration = if (duration[0] == '1')
                            duration
                                .replace("s"," Seconde")
                                .replace("m"," Minute")
                                .replace("h"," Heure")
                                .replace("j"," Jour")
                        else
                            duration
                                .replace("s"," Secondes")
                                .replace("m"," Minutes")
                                .replace("h"," Heures")
                                .replace("j"," Jours")

                        if (event.guild.isMember(UserSnowflake.fromId(memberId))) embed.addField(Main.config.getMessages().tempmutelist.resultFirstFieldName, event.guild.getMemberById(memberId)!!.effectiveName, false)
                        else                                                      embed.addField(Main.config.getMessages().tempmutelist.resultFirstFieldName, "introuvable", false)
                        embed.addField(Main.config.getMessages().tempmutelist.resultSecondFieldName, duration, true)
                        embed.addField(Main.config.getMessages().tempmutelist.resultThirdFieldName, "<t:$time:F>", true)
                        embed.addField(Main.config.getMessages().tempmutelist.resultFourthFieldName, reason, true)


                        i++
                    }

                }

                event.channel.sendMessageEmbeds(embed.build()).queue()

            }

        }

    }

}